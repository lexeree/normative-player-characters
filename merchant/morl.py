from agents import Agent
from collections import defaultdict
import log
import random


class MORLAgent(Agent):
    def __init__(self, env, filter=None, occ=False, ngrl=True, alpha=0.5, epsilon=0.05, gamma=0.9, ntrain=10, export=''):
        Agent.__init__(self, env)
        self.name = 'Greatest Mass Agent'
        self.filter = filter
        self.supervise = occ
        self.learn = ngrl
        self.logger = log.Log(self.name, self.env.map, export)
        self.qValues0 = {}
        self.qValues0 = defaultdict(lambda: 0.0, self.qValues0)
        self.qValues1 = {}
        self.qValues1 = defaultdict(lambda:0.0, self.qValues1)
        self.qValues2 = {}
        self.qValues2 = defaultdict(lambda: 0.0, self.qValues2)
        self.alpha = alpha
        self.epsilon = epsilon
        self.gamma = gamma
        self.ntrain = ntrain


    def getQValue1(self, state, action):
        return self.qValues1[(state, action)]


    def getQValue2(self, state, action):
        return self.qValues2[(state, action)]

    def computeValue1(self, state, possible):
        #print(self.getPossibleActions(state))
        qvals = [self.getQValue1(state, a) for a in possible]
        return max(qvals)

    def computeValue2(self, state, possible):
        qvals = [self.getQValue2(state, a) for a in possible]
        return max(qvals)


    def policy(self, state, train):
        possible = self.getLegalActions(state, train)
        v = self.computeValue1(state, possible) + self.computeValue2(state, possible)
        acts = []
        for act in possible:
            if (self.getQValue1(state, act) + self.getQValue2(state, act)) == v:
                acts.append(act)
        return random.choice(acts)

    def act(self, state, train=False):
        action = self.policy(state, train)
        if train:
            if random.random() <= self.epsilon:
                return random.choice(self.getLegalActions(state, True))
            else:
                return action
        else:
            return action

    def update1(self, state0, action, state1, reward):
        curQ = self.getQValue1(state0, action)
        self.qValues1[(state0, action)] = (1 - self.alpha) * curQ + self.alpha * (
                reward + self.gamma * self.computeValue1(state1, self.getPossibleActions(state1)))

    def update2(self, state0, action, state1, reward):
        curQ = self.getQValue2(state0, action)
        self.qValues2[(state0, action)] = (1 - self.alpha) * curQ + self.alpha * (
                reward + self.gamma * self.computeValue2(state1, self.getPossibleActions(state1)))

    def test(self, rec=False):
        self.env.reset()
        state = self.env.initialState()
        if rec:
            self.logger.record_state(state, 'North', [])
        state, r = self.env.stateTransition(state, 'North')
        steps = 1
        while not state.final:
            act = self.act(state)
            if rec:
                lst = []
                for p in self.getPossibleActions(state):
                    a = p + ' = ' + str(self.getQValue1(state, p)) + ' / '+ str(self.getQValue2(state, p))
                    lst.append(a)
                self.logger.record_state(state, act, lst)
            state, r = self.env.stateTransition(state, act)
            steps += 1
        if rec:
            self.logger.export_trace()
        return steps, len(state.inventory), state.get_value(), state.damage

    def run(self, n, rec=False):
        for i in range(n):
            time, mass, value, damage = self.test(rec=False)
            if rec:
                self.logger.add_summary(i, time, mass, value, damage)
        if rec:
            self.logger.export_summary()

    def train(self):
        for n in range(self.ntrain):
            self.env.reset()
            state = self.env.initialState()
            state, r = self.env.stateTransition(state, 'North')
            reward = 0
            while not state.final:
                act = self.act(state, train=True)
                r2 = self.filter.evaluate(self.get_labels(state), act)
                nstate, r1 = self.env.stateTransition(state, act)
                self.update1(state, act, nstate, r1)
                self.update2(state, act, nstate, r2)
                #print(self.getQValue2(state, act))
                reward += r
                state = nstate
            print('Episode ' + str(n + 1) + ' complete!')
            print('Total rewards: ' + str(reward))
            print('Damage taken: ' + str(state.damage))
            print('Inventory value: ' + str(state.get_value()))


class WeightedAgent(MORLAgent):
    def __init__(self, env, filter=None, occ=False, focc=False, ngrl=True, weight=0.0, alpha=0.5, epsilon=0.05, gamma=0.9, ntrain=10, export=''):
        MORLAgent.__init__(self, env, filter, occ, focc, ngrl, alpha, epsilon, gamma, ntrain, export)
        self.name = 'Linear Scalarization Agent'
        self.logger = log.Log(self.name, self.env.map, export)
        self.weight = weight


    def policy(self, state, train):
        possible = self.getLegalActions(state, train)
        v = self.computeValue1(state,possible) + self.weight*self.computeValue2(state,possible)
        acts = []
        for act in possible:
            if (self.getQValue1(state, act) + self.weight*self.getQValue2(state, act)) == v:
                acts.append(act)
        return random.choice(acts)



class TLQLAgent(MORLAgent):
    def __init__(self, env,  filter=None, occ=False, ngrl=True, alpha=0.5, epsilon=0.05, gamma=0.9, ntrain=10, export=''):
        MORLAgent.__init__(self, env, filter, occ, ngrl, alpha, epsilon, gamma, ntrain, export)
        self.name = 'TLQL Agent'
        self.logger = log.Log(self.name, self.env.map, export)

    def policy(self, state, train):
        possible = self.getLegalActions(state, train)
        v2 = self.computeValue2(state, possible)
        actions = []
        for act in possible:
            if self.getQValue2(state, act) == v2:
                actions.append(act)
        acts = []
        qs = [self.getQValue1(state, a) for a in actions]
        v1 = max(qs)
        for act in actions:
            if self.getQValue1(state, act) == v1:
                acts.append(act)
        return random.choice(acts)


class NGRLVCAgent(MORLAgent):
    def __init__(self, env, filter=None, occ=False, ngrl=True, alpha=0.5, epsilon=0.05, gamma=0.9, ntrain=10, threshold=-3.0, export=''):
        MORLAgent.__init__(self, env, filter, occ, ngrl, alpha, epsilon, gamma, ntrain, export)
        self.name = 'NGRL with VC'
        self.logger = log.Log(self.name, self.env.map, export)
        self.rValues3 = {} #sub-ideal function
        self.rValues3 = defaultdict(lambda: 0.0, self.rValues3)
        self.threshold = threshold

    def getRValue3(self, state, action):
        return self.rValues3[(state, action)]

    def computeValue3(self, state, possible):
        rvals = [self.getRValue3(state, a) for a in possible]
        return max(rvals)

    def update3(self, state, action, reward):
        self.rValues3[(state, action)] = reward

    def train(self):
        for n in range(self.ntrain):
            self.env.reset()
            state = self.env.initialState()
            state, r = self.env.stateTransition(state, 'North')
            reward = 0
            while not state.final:
                act = self.act(state, train=True)
                r2, r3 = self.filter.dualEvaluate(self.get_labels(state), act, self.getPossibleActions(state))
                nstate, r1 = self.env.stateTransition(state, act)
                self.update1(state, act, nstate, r1)
                self.update2(state, act, nstate, r2)
                self.update3(state, act, r3)
                reward += r
                state = nstate
            print('Episode ' + str(n + 1) + ' complete!')
            print('Total rewards: ' + str(reward))
            print('Damage taken: ' + str(state.damage))
            print('Inventory value: ' + str(state.get_value()))

    def policy(self, state, train):
        possible = self.getLegalActions(state, train)
        v2 = self.computeValue2(state, possible)
        actions2 = []
        for act in possible:
            if self.getQValue2(state, act) == v2:
                actions2.append(act)
        v3 = self.computeValue3(state, actions2)
        actions3 = []
        for act in actions2:
            if self.getRValue3(state, act) == v3:
                actions3.append(act)
        v1 = self.computeValue1(state, actions3)
        actions1 = []
        for act in actions3:
            if self.getQValue1(state, act) == v1:
                actions1.append(act)
        return random.choice(actions1)


class NGRLVCFilterAgent(MORLAgent):
    def __init__(self, env, filter=None, occ=False, ngrl=True, alpha=0.5, epsilon=0.05, gamma=0.9, ntrain=10, threshold=-3.0, export=''):
        MORLAgent.__init__(self, env, filter, occ, ngrl, alpha, epsilon, gamma, ntrain, export)
        self.name = 'NGRL with VC and filter'
        self.logger = log.Log(self.name, self.env.map, export)
        self.threshold = threshold

    def policy(self, state, train):
        possible = self.getLegalActions(state, train)
        v2 = self.computeValue2(state, possible)
        actions2 = []
        for act in possible:
            if self.getQValue2(state, act) == v2:
                actions2.append(act)
        actions3 = self.filter.filter(self.get_labels(state), actions2)
        v1 = self.computeValue1(state, actions3)
        actions1 = []
        for act in actions3:
            if self.getQValue1(state, act) == v1:
                actions1.append(act)
        return random.choice(actions1)
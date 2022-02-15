#EMERY: Thresholded lexicographic multi objective RL agents


from game import *
from learningAgents import ReinforcementAgent
from featureExtractors import *

import random,util,math

class TLQLearningAgent(ReinforcementAgent):
    def __init__(self, **args):
        ReinforcementAgent.__init__(self, **args)
        self.QValues1 = util.Counter()
        self.QValues2 = util.Counter()
        self.legalActions = []

    def getQValue1(self, state, action):
        return self.QValues1[(state, action)]

    def getQValue2(self, state, action):
        return min(0, self.QValues2[(state, action)])


    def getAction(self, state, filter=None, train=False, supervise=False):
        # Pick Action
        self.legalActions = self.getLegalActions(state, filter, train, supervise)
        action = None
        if not self.legalActions:
          return action
        randomize = util.flipCoin(self.epsilon)
        if randomize:
          action = random.choice(self.legalActions)
        else:
          action = self.getPolicy(state)
        return action

    def update(self, state, action, nextState, reward):
        curQ = self.QValues1[(state, action)]
        self.QValues1[(state, action)] = (1 - self.alpha) * curQ + self.alpha * (
                    reward + self.discount * self.getValue1(nextState))

    def update2(self, state, action, nextState, reward):
        curQ = self.QValues2[(state, action)]
        self.QValues2[(state, action)] = (1 - self.alpha) * curQ + self.alpha * (
                    reward + self.discount * self.getValue2(nextState))

    def getPolicy(self, state):
        actions1 = []
        if not self.legalActions:
            return None
        val2 = self.getValue2(state)
        for action in self.legalActions:
            if val2 == self.getQValue2(state, action):
                actions1.append(action)
        qvals1 = [self.getQValue1(state, act) for act in actions1]
        val1 = max(qvals1)
        actions2 = []
        for a in actions1:
            if val1 == self.getQValue1(state, a):
                actions2.append(a)
        return random.choice(actions2)

    def getValue1(self, state, filter=None, train=False):
        qvals = [self.getQValue1(state, action) for action in self.legalActions]
        if not qvals:
            return 0.0
        return max(qvals)

    def getValue2(self, state, filter=None, train=False):
        qvals = [self.getQValue2(state, action) for action in self.legalActions]
        if not qvals:
            return 0.0
        return max(qvals)

class PacmanTLQAgent(TLQLearningAgent):
    def __init__(self, epsilon=0.05,gamma=0.8,alpha=0.2, numTraining=0, **args):
        args['epsilon'] = epsilon
        args['gamma'] = gamma
        args['alpha'] = alpha
        args['numTraining'] = numTraining
        self.index = 0  # This is always Pacman
        TLQLearningAgent.__init__(self, **args)


    def getAction(self, state, filter=None, train=False, supervise=False):
        action = TLQLearningAgent.getAction(self,state, filter, train, supervise)
        self.doAction(state,action)
        return action


class ApproximateTLQAgent(PacmanTLQAgent):
    def __init__(self, extractor='IdentityExtractor', **args):
        self.featExtractor = util.lookup(extractor, globals())()
        PacmanTLQAgent.__init__(self, **args)
        self.weights1 = util.Counter()
        self.weights2 = util.Counter()

    def getWeights(self):
        return self.weights1, self.weights2

    def getQValue1(self, state, action):
        qval1 = 0.0
        features = self.featExtractor.getFeatures(state, action)
        for feature in features:
          qval1 += features[feature] * self.weights1[feature]
        return qval1

    def getQValue2(self, state, action):
        qval2 = 0.0
        features = self.featExtractor.getFeatures(state, action)
        for feature in features:
          qval2 += features[feature] * self.weights2[feature]
        return qval2

    def update(self, state, action, nextState, reward):
        features = self.featExtractor.getFeatures(state, action)
        difference = reward + self.discount * self.getValue1(nextState) - self.getQValue1(state, action)
        for feature in features:
          self.weights1[feature] += self.alpha * difference * features[feature]

    def update2(self, state, action, nextState, reward):
        features = self.featExtractor.getFeatures(state, action)
        difference = reward + self.discount * self.getValue2(nextState) - self.getQValue2(state, action)
        for feature in features:
          self.weights2[feature] += self.alpha * difference * features[feature]

    def final(self, state):
        # call the super-class final method
        PacmanTLQAgent.final(self, state)

        # did we finish training?
        if self.episodesSoFar == self.numTraining:
            # you might want to print your weights here for debugging
            #print('self.weights',self.weights)
            pass

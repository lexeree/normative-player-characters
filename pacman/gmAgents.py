#EMERY: Greatest Mass multi objective RL agents


from game import *
from learningAgents import ReinforcementAgent
from featureExtractors import *

import random,util,math

class GMLearningAgent(ReinforcementAgent):
    """
      Q-Learning Agent

      Functions you should fill in:
        - computeValueFromQValues
        - computeActionFromQValues
        - getQValue
        - getAction
        - update

      Instance variables you have access to
        - self.epsilon (exploration prob)
        - self.alpha (learning rate)
        - self.discount (discount rate)

      Functions you should use
        - self.getLegalActions(state)
          which returns legal actions for a state
    """
    def __init__(self, **args):
        "You can initialize Q-values here..."
        ReinforcementAgent.__init__(self, **args)
        self.QValues1 = util.Counter()
        self.QValues2 = util.Counter()
        self.legalActions = []

    def getQValue1(self, state, action):
        """
          Returns Q(state,action)
          Should return 0.0 if we have never seen a state
          or the Q node value otherwise
        """
        return self.QValues1[(state, action)]

    def getQValue2(self, state, action):
        """
          Returns Q(state,action)
          Should return 0.0 if we have never seen a state
          or the Q node value otherwise
        """
        return self.QValues2[(state, action)]

    def computeValueFromQValues1(self, state):
        """
          Returns max_action Q(state,action)
          where the max is over legal actions.  Note that if
          there are no legal actions, which is the case at the
          terminal state, you should return a value of 0.0.
        """
        qvals = [self.getQValue1(state, action) for action in self.legalActions]
        if not qvals:
            return 0.0
        return max(qvals)


    def computeValueFromQValues2(self, state):
        """
          Returns max_action Q(state,action)
          where the max is over legal actions.  Note that if
          there are no legal actions, which is the case at the
          terminal state, you should return a value of 0.0.
        """
        qvals = [self.getQValue2(state, action) for action in self.legalActions]
        if not qvals:
            return 0.0
        return max(qvals)


    def computeValueFromQValues(self, state):
        """
          Returns max_action Q(state,action)
          where the max is over legal actions.  Note that if
          there are no legal actions, which is the case at the
          terminal state, you should return a value of 0.0.
        """
        qvals = [self.getQValue1(state, action) + self.getQValue2(state, action) for action in self.legalActions]
        if not qvals:
            return 0.0
        return max(qvals)

    def computeActionFromQValues(self, state, filter=None, train=False):
        """
          Compute the best action to take in a state.  Note that if there
          are no legal actions, which is the case at the terminal state,
          you should return None.
        """
        actions = []
        if not self.legalActions:
          return None
        val = self.computeValueFromQValues(state)
        for action in self.legalActions:
            if val == self.getQValue1(state, action) + self.getQValue2(state, action):
                actions.append(action)
        return random.choice(actions)

    def getAction(self, state, filter=None, train=False, supervise=False):
        """
          Compute the action to take in the current state.  With
          probability self.epsilon, we should take a random action and
          take the best policy action otherwise.  Note that if there are
          no legal actions, which is the case at the terminal state, you
          should choose None as the action.

          HINT: You might want to use util.flipCoin(prob)
          HINT: To pick randomly from a list, use random.choice(list)
        """
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
        """
          The parent class calls this to observe a
          state = action => nextState and reward transition.
          You should do your Q-Value update here

          NOTE: You should never call this function,
          it will be called on your behalf
        """
        curQ = self.QValues1[(state, action)]
        self.QValues1[(state, action)] = (1 - self.alpha) * curQ + self.alpha * (
                    reward + self.discount * self.getValue1(nextState))

    def update2(self, state, action, nextState, reward):
        curQ = self.QValues2[(state, action)]
        self.QValues2[(state, action)] = (1 - self.alpha) * curQ + self.alpha * (
                    reward + self.discount * self.getValue2(nextState))

    def getPolicy(self, state):
        return self.computeActionFromQValues(state)

    def getValue1(self, state, filter=None, train=False):
        return self.computeValueFromQValues1(state)

    def getValue2(self, state, filter=None, train=False):
        return self.computeValueFromQValues2(state)

    def getPoliciesW(self, state, w, filter=None, train=False, supervise=False):
        actions = []
        legalActions = self.getLegalActions(state, filter, train, supervise)
        val = self.computeValueFromQValues1(state) + w * self.computeValueFromQValues2(state)
        for action in legalActions:
            if val == self.getQValue1(state, action) + w * self.getQValue2(state, action):
                actions.append(action)
        return actions

    def getPolicies2(self, state, filter=None, train=False, supervise=False):
        actions = []
        legalActions = self.getLegalActions(state, filter, train, supervise)
        val = self.computeValueFromQValues2(state)
        for action in legalActions:
            if val == self.getQValue2(state, action):
                actions.append(action)
        return actions

    def getPolicies1(self, state, filter=None, train=False, supervise=False):
        actions = []
        legalActions = self.getLegalActions(state, filter, train, supervise)
        val = self.computeValueFromQValues2(state)
        for action in legalActions:
            if val == self.getQValue2(state, action):
                actions.append(action)
        return actions


class PacmanGMAgent(GMLearningAgent):
    "Exactly the same as QLearningAgent, but with different default parameters"

    def __init__(self, epsilon=0.05,gamma=0.8,alpha=0.2, numTraining=0, **args):
        """
        These default parameters can be changed from the pacman.py command line.
        For example, to change the exploration rate, try:
            python pacman.py -p PacmanQLearningAgent -a epsilon=0.1

        alpha    - learning rate
        epsilon  - exploration rate
        gamma    - discount factor
        numTraining - number of training episodes, i.e. no learning after these many episodes
        """
        args['epsilon'] = epsilon
        args['gamma'] = gamma
        args['alpha'] = alpha
        args['numTraining'] = numTraining
        self.index = 0  # This is always Pacman
        GMLearningAgent.__init__(self, **args)

    def getAction(self, state, filter=None, train=False, supervise=False):
        """
        Simply calls the getAction method of QLearningAgent and then
        informs parent of action for Pacman.  Do not change or remove this
        method.
        """
        action = GMLearningAgent.getAction(self,state, filter, train, supervise)
        self.doAction(state,action)
        return action

    def final(self, state):
        GMLearningAgent.final(self, state)
        if self.episodesSoFar == self.numTraining:
            t = False
            w = 0
            states = [sa[0] for sa in self.QValues1.keys()]
            for s in states:
                legalActions = self.getLegalActions(s, None, False, False)
                q1 = self.computeValueFromQValues1(s)
                #since Q2 is 0 or strictly negative...
                for action in legalActions:
                    q2 = self.getQValue2(s, action)
                    if q2 == 0:
                        ratio = 0
                    else:
                        ratio = abs(q1/q2)
                    w = max(w, ratio)
            print("w = " + str(w))






class ApproximateGMAgent(PacmanGMAgent):
    """
       ApproximateQLearningAgent

       You should only have to overwrite getQValue
       and update.  All other QLearningAgent functions
       should work as is.
    """
    def __init__(self, extractor='IdentityExtractor', **args):
        self.featExtractor = util.lookup(extractor, globals())()
        PacmanGMAgent.__init__(self, **args)
        self.weights1 = util.Counter()
        self.weights2 = util.Counter()

    def getWeights(self):
        return self.weights1, self.weights2

    def getQValue1(self, state, action):
        """
          Should return Q(state,action) = w * featureVector
          where * is the dotProduct operator
        """
        qval1 = 0.0
        features = self.featExtractor.getFeatures(state, action)
        for feature in features:
          qval1 += features[feature] * self.weights1[feature]
        return qval1


    def getQValue2(self, state, action):
        """
          Should return Q(state,action) = w * featureVector
          where * is the dotProduct operator
        """
        qval2 = 0.0
        features = self.featExtractor.getFeatures(state, action)
        for feature in features:
          qval2 += features[feature] * self.weights2[feature]
        return qval2

    def update(self, state, action, nextState, reward):
        """
           Should update your weights based on transition
        """
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
        "Called at the end of each game."
        # call the super-class final method
        PacmanGMAgent.final(self, state)

        # did we finish training?
        if self.episodesSoFar == self.numTraining:
            # you might want to print your weights here for debugging
            #print('self.weights',self.weights)
            pass

from game import *
from learningAgents import ReinforcementAgent
from weightedAgents import WeightedLearningAgent, PacmanWeightedAgent, ApproximateWeightedAgent
from featureExtractors import *

import random,util,math

class QLearningAgent(WeightedLearningAgent):
    def __init__(self, **args):
        WeightedLearningAgent.__init__(self, **args)


class PacmanQAgent(PacmanWeightedAgent):
    def __init__(self, **args):
        PacmanWeightedAgent.__init__(self, **args)


class ApproximateQAgent(ApproximateWeightedAgent):
    def __init__(self, **args):
        ApproximateWeightedAgent.__init__(self, **args)

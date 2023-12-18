import random
import environment
import morl
from filter import NormativeFilter
import os


if __name__ == '__main__':
    random.seed(0)
    script = os.path.realpath(os.path.join(os.getcwd(), os.path.dirname(__file__)))
    map_address = script + '/layouts/basic.txt'
    env = environment.Environment(map_address, risk=1)
    labels = ['North_danger', 'South_danger', 'East_danger', 'West_danger', 'at_danger', 'attacked']
    actions = ['Fight', 'North', 'South', 'East', 'West', 'Extract', 'Pickup', 'Unload']
    f = NormativeFilter('pacifist', actions, labels)
    agent = morl.NGRLVCFilterAgent(env, filter=f, ngrl=True, alpha=0.5, epsilon=0.25, ntrain=3000)
    f.init()
    agent.train()
    a, i, b, c = agent.test(rec=True)
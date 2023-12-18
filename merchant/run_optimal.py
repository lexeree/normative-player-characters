import random
import environment
import qlearning as q
import os


if __name__ == '__main__':
    random.seed(0)
    script = os.path.realpath(os.path.join(os.getcwd(), os.path.dirname(__file__)))
    map_address = script + '/layouts/basic.txt'
    env = environment.Environment(map_address, risk=1)
    agent = q.RewardQAgent(env, occ=False,  alpha=0.5, epsilon=0.25, ntrain=3000)
    agent.train()
    a, i, b, c = agent.test(rec=True)
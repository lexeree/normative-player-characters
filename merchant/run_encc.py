import random
import environment
import morl
from filter import NormativeSupervisor
import os


if __name__ == '__main__':
    random.seed(0)
    script = os.path.realpath(os.path.join(os.getcwd(), os.path.dirname(__file__)))
    map_address = script + '/layouts/dangerous.txt'
    env = environment.Environment(map_address, risk=1)
    f = NormativeSupervisor('pacifist', 'DDPL2')
    f.connect()
    agent = morl.NGRLVCAgent(env, filter=f, ngrl=True, alpha=0.5, epsilon=0.25, ntrain=3000)
    agent.train()
    a, i, b, c = agent.test(rec=True)
    f.terminate_server()
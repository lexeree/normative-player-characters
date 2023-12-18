import argparse
import random
import environment
from filter import NormativeFilter
from agents import RandomAgent
from qlearning import RewardQAgent
from morl import TLQLAgent, NGRLITAgent

if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='Configure game.')
    parser.add_argument('-p', '--player', dest='player', help='random/qLearning')
    parser.add_argument('-l', '--layout', dest='layout', help='the file from which to load the map layout', default='basic')
    parser.add_argument('-f', '--fix', action='store_true', dest='fix',
                      help='Fixes the random seed to always play the same game', default=False)
    parser.add_argument('-n', '--numGames', dest='numGames', type=int, help='the number of GAMES to play', default=1)
    parser.add_argument('-x', '--numTraining', dest='numTraining', type=int,
                      help='How many episodes are training (suppresses output)', default=0)
    parser.add_argument('--norm', dest='norm', help='Specify norm set: broken, busy, hungry, vegan, vegetarian', default=None)
    parser.add_argument('--reason', dest='reason', help='Specify reasoner type: DDPL', default=None)
    parser.add_argument('--rec', dest='rec', help='Would you like to save a record of tests run? Input file name.',
                      default=None)
    parser.add_argument('--threshold', dest='threshold', help='Threshold....', default=None)
    parser.add_argument('--supervise', action='store_true', dest='supervise', help='Use normative supervisor?',
                      default=False)
    parser.add_argument('--learn', action='store_true', dest='learn', help='Learn with norms - only choose with MORL agent',
                      default=False)
    parser.add_argument('--sublearn', action='store_true', dest='learn2',
                      help='Learn with sub ideal reward function; only select for SubIdealAgent', default=False)
    parser.add_argument('--port', type=int, dest='port', help='Port number.', default=6666)
    parser.add_argument('--alpha', dest='alpha', type=float, default=0.5)
    parser.add_argument('--epsilon', dest='epsilon', type=float, default=0.25)

    player = parser.parse_args().player
    layout = parser.parse_args().layout
    fix = parser.parse_args().fix
    games = parser.parse_args().numGames
    train = parser.parse_args().numTraining
    norm = parser.parse_args().norm
    reason = parser.parse_args().reason
    rec = parser.parse_args().rec
    supervise = parser.parse_args().supervise
    learn = parser.parse_args().learn
    learn2 = parser.parse_args().learn2
    thresh = parser.parse_args().threshold
    port = parser.parse_args().port
    alpha = parser.parse_args().alpha
    epsilon = parser.parse_args().epsilon

    if fix:
        random.seed(0)
    if supervise or learn:
        filt = NormativeFilter(norm, reason, port=port)
        filt.connect()
    else:
        filt = None
    f = open('layouts/'+layout+'.txt')
    print(f)
    env = environment.Environment('layouts/'+layout+'.txt', risk=1)
    if player == 'random':
        agent = RandomAgent(env, filter=filt, occ=supervise)
    elif player == 'qLearning':
        agent = RewardQAgent(env, filter=filt, occ=supervise, alpha=alpha, epsilon=epsilon, ntrain=train, export=rec)
        agent.train()
    elif player == 'TLQL':
        agent = TLQLAgent(env, filter=filt, occ=supervise, ngrl=learn, alpha=alpha, epsilon=epsilon, ntrain=train, export=rec)
        agent.train()
    elif player == 'SubIdeal':
        agent = NGRLITAgent(env, filter=filt, occ=supervise, ngrl=learn2, alpha=alpha, epsilon=epsilon, ntrain=train, threshold=thresh, export=rec)
        agent.train()
    a, i, b, c = agent.test(rec=True)
    if filt is not None:
        filt.terminate_server()

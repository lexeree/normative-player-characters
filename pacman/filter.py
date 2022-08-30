#EMERY: for contacting the normative supervisor
import subprocess
import json, socket
from time import sleep


HOST, PORT = "localhost", 6666

class NormativeFilter():

    def __init__(self, norms, reason, port=PORT):
        self.violations = []
        self.norm_base = norms
        self.reasoner = reason
        self.server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.port = port
        self.id = 0
        self.compliant = True

    def connect(self):
        self.server.connect((HOST,self.port))
        sleep(1)

    def setID(self, i):
        self.id = i

    def terminate_server(self):
        to = {}
        to['request'] = "TERMINATION"
        to_send = json.dumps(to)
        self.server.sendall(to_send)
        self.server.send('\n')
        self.server.close()

    def isCompliant(self):
        return self.compliant


    def send_request(self, request):
        to_send = json.dumps(request)
        self.server.sendall(to_send)
        self.server.send('\n')
        receipt = self.server.recv(1024)
        answ = json.loads(receipt)
        return answ


    def process_message(self, message):
        self.compliant = message['compliant']
        if message['response'] == 'RECOMMENDATION':
            legal = message['actions']
            return legal
        elif message['response'] == 'EVALUATION':
            if self.compliant:
                return 0
            else:
                return -1
        elif message['response'] == 'DUAL-EVALUATION':
            subideal = message['sub-ideal']
            if self.compliant:
                return 0,0
            else:
                if subideal:
                    return -1,0
                else:
                    return -1,-1


    def build_query(self, state, actions, rt):
        to = self.translate_state(state)
        to['name'] = 'pacman'
        to['id'] = self.id
        to['request'] = rt
        if rt == 'FILTER':
            to['possible'] = actions
        elif rt == 'EVALUATION':
            to['action'] = actions[0]
        elif rt == 'DUAL-EVALUATION':
            to['action'] = actions[0]
        return to


    def translate_state(self, state):
        colour = ['b', 'o', 'g']
        layout = state.layout
        to = {}
        to['norms'] = self.norm_base
        to['reasoner'] = self.reasoner

        dim = {}
        dim['x'] = layout.width
        dim['y'] = layout.height

        game = {}
        game['dimension'] = dim
        game['score'] = int(state.score)

        i = 0
        lay = []
        for agent in state.agentStates:
            a = {}
            pos = {}
            pos['x'] = agent.getPosition()[0]
            pos['y'] = agent.getPosition()[1]
            a['position'] = pos
            if agent.isPacman:
                a['type'] = 'p'
            else:
                lab = colour[i]
                if agent.isScared():
                    lab = 'sc_'+lab
                a['type'] = lab
                i += 1
            lay.append(a)
        game['layout'] = lay
        game['blue_eaten'] = state._ghostsEaten1
        game['orange_eaten'] = state._ghostsEaten2
        to['game'] = game
        return to


    def printEvaluation(self):
        num = 0
        total = 0
        for v in self.violations:
            num = num + v[0]
            total = total + v[1]
        print("Number of Violations:"+str(num))
        print("Total weight of Violations:" + str(total))



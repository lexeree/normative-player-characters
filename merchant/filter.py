import json, socket
from time import sleep


HOST, PORT = "localhost", 6666

class NormativeSupervisor:

    def __init__(self, norms, reason, port=PORT):
        self.process = None
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

    def filter(self, labels, actions=None):
        #print(str(state.x)+","+str(state.y))
        to = self.build_query(labels, 'FILTER', actions=actions)
        mes = self.send_request(to)
        actions = self.process_message(mes)
        #print(actions)
        return actions

    def evaluate(self, labels, action):
        to = self.build_query(labels, 'EVALUATION', action=action)
        #print(to)
        mes = self.send_request(to)
        reward = self.process_message(mes)
        return reward

    def dualEvaluate(self, labels, action, actions):
        to = self.build_query(labels, 'DUAL-EVALUATION', actions=actions, action=action)
        mes = self.send_request(to)
        reward = self.process_message(mes)
        return reward

    def metric(self, labels, action):
        to = self.build_query(labels, 'METRIC', action=action)
        #print(to)
        mes = self.send_request(to)
        reward = self.process_message(mes)
        return reward

    def terminate_server(self):
        to = {}
        to['request'] = "TERMINATION"
        to_send = json.dumps(to)
        self.server.sendall(bytes(to_send,encoding="utf-8"))
        br = '\n'
        self.server.send(bytes(br,encoding="utf-8"))
        self.server.close()

    def isCompliant(self):
        return self.compliant


    def send_request(self, request):
        to_send = json.dumps(request)
        self.server.sendall(bytes(to_send,encoding="utf-8"))
        br = '\n'
        self.server.send(bytes(br, encoding="utf-8"))
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
        elif message['response'] == 'METRIC':
            if self.compliant:
                return 0, 0
            else:
                return -1, -1 * message['violations']
        elif message['response'] == 'DUAL-EVALUATION':
            subideal = message['sub-ideal']
            if self.compliant:
                return 0,0
            else:
                if subideal:
                    return -1,0
                else:
                    return -1,-1


    def build_query(self, labels, rt, actions=None, action=None):
        to = {}
        to['norms'] = self.norm_base
        to['reasoner'] = self.reasoner
        to['labels'] = labels
        to['name'] = 'merchant'
        to['id'] = self.id
        to['request'] = rt
        if rt == 'FILTER':
            to['possible'] = actions
        elif rt == 'EVALUATION':
            to['action'] = action
        elif rt == 'METRIC':
            to['action'] = action
        elif rt == 'DUAL-EVALUATION':
            to['possible'] = actions
            to['action'] = action
        return to

    def printEvaluation(self):
        num = 0
        total = 0
        for v in self.violations:
            num = num + v[0]
            total = total + v[1]
        print("Number of Violations:"+str(num))
        print("Total weight of Violations:" + str(total))



class NormativeFilter:

    def __init__(self, norms, acts, labels, port=PORT):
        self.norm_base = norms
        self.server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.port = port
        self.nfilter = {}
        self.labels = labels
        self.actions = acts


    def init(self):
        a = 1
        self.server.connect((HOST, self.port))
        sleep(1)
        to = self.build_query()
        self.send_request(to)
        self.server.close()

    def send_request(self, request):
        to_send = json.dumps(request)
        self.server.sendall(bytes(to_send,encoding="utf-8"))
        br = '\n'
        self.server.send(bytes(br, encoding="utf-8"))
        BUFF_SIZE = 4096  # 4 KiB
        data = b''
        while True:
            part = self.server.recv(BUFF_SIZE)
            data += part
            if len(part) < BUFF_SIZE:
                break
        answ = json.loads(data)
        self.process_message(answ)


    def process_message(self, message):
        for item in message:
            labs = item['labels']
            state = frozenset(labs)
            act = item['action']
            self.nfilter[(state, act)] = item['violations']

    def build_query(self):
        to = {}
        to['norms'] = self.norm_base
        to['labels'] = self.labels
        to['actions'] = list(self.actions)
        to['name'] = 'merchant'
        return to

    def filter(self, labels, actions=None):
        labs = list(set(labels) & set(self.labels))
        acts = {}
        for act in actions:
            key = (frozenset(labs), act)
            acts[act] = self.nfilter[key]
        minval = min(acts.values())
        best = [a for a, v in acts.items() if v == minval]
        return best

    def evaluate(self, labels, action):
        labs = list(set(labels) & set(self.labels))
        key = (frozenset(labs), action)
        return -self.nfilter[key]
        #if self.nfilter[key] > 0:
        #    return -1
        #else:
        #    return 0

    def dualEvaluate(self, labels, action):
        labs = list(set(labels) & set(self.labels))
        key = (frozenset(labs), action)
        compl = 0
        if self.nfilter[key] > 0:
           compl = -1
        return compl, self.nfilter[key]
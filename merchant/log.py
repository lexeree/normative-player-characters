import csv
import datetime

class Log:
    def __init__(self, agent, map, name):
        self.agent = agent
        self.map = map
        self.name = name
        self.trace = []
        self.summary = []

    def export_trace(self):
        header = ['X', 'Y', 'Action', 'Inventory', 'Damage', 'Attacked?', 'Choices']
        with open(self.agent+str(datetime.datetime.now())+'_log.csv', 'w', newline='') as csvfile:
            writer = csv.DictWriter(csvfile, fieldnames=header)
            writer.writeheader()
            for tr in self.trace:
                writer.writerow(tr)

    def record_state(self, state, action, possible):
        tr = {}
        tr['X'] = str(state.x)
        tr['Y'] = str(state.y)
        tr['Action'] = action
        tr['Inventory'] = ''.join(state.inventory)
        tr['Damage'] = str(state.damage)
        tr['Attacked?'] = str(state.attack)
        tr['Choices'] = possible
        self.trace.append(tr)

    def export_summary(self):
        header = ['Episode', 'Time Steps', 'Inventory Size', 'Value of Goods', 'Damage Taken']
        #with open(self.agent+str(datetime.datetime.now())+'_log_summary.csv', 'w', newline='') as csvfile:
        with open(self.name + '_log_summary.csv', 'w', newline='') as csvfile:
            writer = csv.DictWriter(csvfile, fieldnames=header)
            writer.writeheader()
            for sm in self.summary:
                writer.writerow(sm)

    def add_summary(self, game, steps, mass, value, damage):
        sm = {}
        sm['Episode'] = game
        sm['Time Steps'] = steps
        sm['Inventory Size'] = mass
        sm['Value of Goods'] = value
        sm['Damage Taken'] = damage
        self.summary.append(sm)

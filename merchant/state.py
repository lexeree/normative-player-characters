
class State:
    def __init__(self, x, y, label, bag, damage, attack, last=None, final=False):
        self.x = x
        self.y = y
        self.label = label
        self.inventory = bag
        self.damage = damage
        self.attack = attack
        self.last_move = last
        self.final = final

    def __key(self):
        return (self.x, self.y, self.label, self.attack, tuple(sorted(self.inventory))) #,

    def __hash__(self):
        return hash(self.__key())

    def __eq__(self, other):
        if isinstance(other, State):
            return self.__key() == other.__key()

    def copy(self):
        x = self.x
        y = self.y
        label = self.label
        bag = self.inventory
        damage = self.damage
        attack = self.attack
        last = self.last_move
        final = self.final
        return State(x, y, label, bag, damage, attack, last, final)

    def sPrint(self):
        return "("+str(self.x)+","+str(self.y)+")("+self.label+")("+str(self.attack)+")("+str(sorted(self.inventory))+")"

    def get_value(self):
        value = 0
        for obj in self.inventory:
            if obj == 'W':
                value += 1
            elif obj == 'O':
                value += 1
        return value




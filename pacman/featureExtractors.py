# featureExtractors.py
# --------------------
# Licensing Information:  You are free to use or extend these projects for
# educational purposes provided that (1) you do not distribute or publish
# solutions, (2) you retain this notice, and (3) you provide clear
# attribution to UC Berkeley, including a link to http://ai.berkeley.edu.
# 
# Attribution Information: The Pacman AI projects were developed at UC Berkeley.
# The core projects and autograders were primarily created by John DeNero
# (denero@cs.berkeley.edu) and Dan Klein (klein@cs.berkeley.edu).
# Student side autograding was added by Brad Miller, Nick Hay, and
# Pieter Abbeel (pabbeel@cs.berkeley.edu).


#Additional extractors added by emery


"Feature extractors for Pacman game states"

from game import Directions, Actions
import util

class FeatureExtractor:
    def getFeatures(self, state, action):
        """
          Returns a dict from features to counts
          Usually, the count will just be 1.0 for
          indicator functions.
        """
        util.raiseNotDefined()

class IdentityExtractor(FeatureExtractor):
    def getFeatures(self, state, action):
        feats = util.Counter()
        feats[(state,action)] = 1.0
        return feats

class CoordinateExtractor(FeatureExtractor):
    def getFeatures(self, state, action):
        feats = util.Counter()
        feats[state] = 1.0
        feats['x=%d' % state[0]] = 1.0
        feats['y=%d' % state[0]] = 1.0
        feats['action=%s' % action] = 1.0
        return feats

def closestFood(pos, food, walls):
    """
    closestFood -- this is similar to the function that we have
    worked on in the search project; here its all in one place
    """
    fringe = [(pos[0], pos[1], 0)]
    expanded = set()
    while fringe:
        pos_x, pos_y, dist = fringe.pop(0)
        if (pos_x, pos_y) in expanded:
            continue
        expanded.add((pos_x, pos_y))
        # if we find a food at this location then exit
        if food[pos_x][pos_y]:
            return dist
        # otherwise spread out from the location to its neighbours
        nbrs = Actions.getLegalNeighbors((pos_x, pos_y), walls)
        for nbr_x, nbr_y in nbrs:
            fringe.append((nbr_x, nbr_y, dist+1))
    # no food found
    return None

def ghostDistance(pac, ghost, walls):
    fringe = [(pac[0], pac[1], 0)]
    expanded = set()
    while fringe:
        pos_x, pos_y, dist = fringe.pop(0)
        if (pos_x, pos_y) in expanded:
            continue
        expanded.add((pos_x, pos_y))
        # if we find a food at this location then exit
        if pos_x == ghost[0] and pos_y == ghost[1]:
            return dist
        # otherwise spread out from the location to its neighbours
        nbrs = Actions.getLegalNeighbors((pos_x, pos_y), walls)
        for nbr_x, nbr_y in nbrs:
            fringe.append((nbr_x, nbr_y, dist+1))
    # no food found
    return None

class SimpleExtractor(FeatureExtractor):
    """
    Returns simple features for a basic reflex Pacman:
    - whether food will be eaten
    - how far away the next food is
    - whether a ghost collision is imminent
    - whether a ghost is one step away
    """

    def getFeatures(self, state, action):
        # extract the grid of food and wall locations and get the ghost locations
        food = state.getFood()
        walls = state.getWalls()
        ghosts = state.getGhostPositions()

        features = util.Counter()

        features["bias"] = 1.0

        # compute the location of pacman after he takes the action
        x, y = state.getPacmanPosition()
        dx, dy = Actions.directionToVector(action)
        next_x, next_y = int(x + dx), int(y + dy)

        # count the number of ghosts 1-step away
        features["#-of-ghosts-1-step-away"] = sum((next_x, next_y) in Actions.getLegalNeighbors(g, walls) for g in ghosts)

        # if there is no danger of ghosts then add the food feature
        if not features["#-of-ghosts-1-step-away"] and food[next_x][next_y]:
            features["eats-food"] = 1.0

        dist = closestFood((next_x, next_y), food, walls)
        if dist is not None:
            # make the distance a number less than one otherwise the update
            # will diverge wildly
            features["closest-food"] = float(dist) / (walls.width * walls.height)
        features.divideAll(10.0)
        return features




class HungryExtractor(FeatureExtractor):

    def getFeatures(self, state, action):
        # extract the grid of food and wall locations and get the ghost locations
        food = state.getFood()
        walls = state.getWalls()
        ghosts = state.getGhostStates()

        features = util.Counter()

        features["bias"] = 1.0

        # compute the location of pacman after he takes the action
        x, y = state.getPacmanPosition()
        dx, dy = Actions.directionToVector(action)
        next_x, next_y = int(x + dx), int(y + dy)

        # count the number of non-scared ghosts 1-step away
        features["#-of-ghosts-1-step-away"] = sum(((next_x, next_y) in Actions.getLegalNeighbors(g.getPosition(), walls) and not g.isScared()) for g in ghosts)

        # count the number of scared ghosts 1-step away
        features["#-of-scared-ghosts-1-step-away"] = sum(((next_x, next_y) in Actions.getLegalNeighbors(g.getPosition(), walls) and g.isScared()) for g in ghosts)

        # if there is no danger of ghosts then add the food feature
        if not features["#-of-ghosts-1-step-away"] and food[next_x][next_y]:
            features["eats-food"] = 1.0

        dist = closestFood((next_x, next_y), food, walls)
        if dist is not None:
            # make the distance a number less than one otherwise the update
            # will diverge wildly
            features["closest-food"] = float(dist) / (walls.width * walls.height)
        features.divideAll(10.0)
        return features



class BlueExtractor(FeatureExtractor):

    def getFeatures(self, state, action):
        # extract the grid of food and wall locations and get the ghost locations
        food = state.getFood()
        walls = state.getWalls()
        ghosts = state.getGhostStates()

        features = util.Counter()

        features["bias"] = 1.0

        # compute the location of pacman after he takes the action
        x, y = state.getPacmanPosition()
        dx, dy = Actions.directionToVector(action)
        next_x, next_y = int(x + dx), int(y + dy)

        # count the number of non-scared ghosts 1-step away
        features["#-of-ghosts1-1-step-away"] = ((next_x, next_y) in Actions.getLegalNeighbors(ghosts[0].getPosition(), walls) and not ghosts[0].isScared())

        # count the number of scared ghosts 1-step away
        features["#-of-scared-ghosts1-1-step-away"] = ((next_x, next_y) in Actions.getLegalNeighbors(ghosts[0].getPosition(), walls) and ghosts[0].isScared())

        # count the number of non-scared ghosts 1-step away
        features["#-of-ghosts2-1-step-away"] = (
                    (next_x, next_y) in Actions.getLegalNeighbors(ghosts[1].getPosition(), walls) and not ghosts[
                1].isScared())

        # count the number of scared ghosts 1-step away
        features["#-of-scared-ghosts2-1-step-away"] = (
                    (next_x, next_y) in Actions.getLegalNeighbors(ghosts[1].getPosition(), walls) and ghosts[
                1].isScared())

        # if there is no danger of ghosts then add the food feature
        if not features["#-of-ghosts-1-step-away"] and food[next_x][next_y]:
            features["eats-food"] = 1.0

        dist = closestFood((next_x, next_y), food, walls)
        if dist is not None:
            # make the distance a number less than one otherwise the update
            # will diverge wildly
            features["closest-food"] = float(dist) / (walls.width * walls.height)
        features.divideAll(10.0)
        return features


class LongExtractor1(FeatureExtractor):

    def getFeatures(self, state, action):
        # extract the grid of food and wall locations and get the ghost locations
        food = state.getFood()
        walls = state.getWalls()
        ghosts = state.getGhostStates()

        features = util.Counter()

        features["bias"] = 1.0

        # compute the location of pacman after he takes the action
        x, y = state.getPacmanPosition()
        dx, dy = Actions.directionToVector(action)
        next_x, next_y = int(x + dx), int(y + dy)

        # count the number of non-scared ghosts 1-step away
        features["#-of-ghosts-1-step-away"] = sum(((next_x, next_y) in Actions.getLegalNeighbors(g.getPosition(), walls) and not g.isScared()) for g in ghosts)

        # count the number of scared ghosts 1-step away
        features["#-of-scared-ghosts-1-step-away"] = sum(((next_x, next_y) in Actions.getLegalNeighbors(g.getPosition(), walls) and g.isScared()) for g in ghosts)

        # if there is no danger of ghosts then add the food feature
        if not features["#-of-ghosts-1-step-away"] and food[next_x][next_y]:
            features["eats-food"] = 1.0

        dist = closestFood((next_x, next_y), food, walls)
        dist1 = ghostDistance((next_x, next_y), state.getGhostPosition(1), walls)
        dist2 = ghostDistance((next_x, next_y), state.getGhostPosition(2), walls)
        if dist is not None:
            # make the distance a number less than one otherwise the update
            # will diverge wildly
            features["closest-food"] = float(dist) / (walls.width * walls.height)

        if dist1 is not None:
            features["closest-ghost-1"] = 1.0 - float(dist1) / (walls.width * walls.height)
        else:
            features["closest-ghost-1"] = 0.0

        if dist2 is not None:
            features["closest-ghost-2"] = 1.0 - float(dist2) / (walls.width * walls.height)
        else:
            features["closest-ghost-2"] = 0.0

        features.divideAll(10.0)
        return features

class LongExtractor2(FeatureExtractor):

    def getFeatures(self, state, action):
        # extract the grid of food and wall locations and get the ghost locations
        food = state.getFood()
        walls = state.getWalls()
        ghosts = state.getGhostStates()

        features = util.Counter()

        features["bias"] = 1.0

        # compute the location of pacman after he takes the action
        x, y = state.getPacmanPosition()
        dx, dy = Actions.directionToVector(action)
        next_x, next_y = int(x + dx), int(y + dy)

        # count the number of non-scared ghosts 1-step away
        #features["#-of-ghosts-1-step-away"] = sum(
         #       ((next_x, next_y) in Actions.getLegalNeighbors(g.getPosition(), walls) and not g.isScared()) for g in
       #         ghosts)

        # count the number of scared ghosts 1-step away
        #features["#-of-scared-ghosts-1-step-away"] = sum(
         #       ((next_x, next_y) in Actions.getLegalNeighbors(g.getPosition(), walls) and g.isScared()) for g in
          #      ghosts)

        # if there is no danger of ghosts then add the food feature
        if not features["#-of-ghosts-1-step-away"] and food[next_x][next_y]:
            features["eats-food"] = 1.0

        dist = closestFood((next_x, next_y), food, walls)
        dist1 = ghostDistance((next_x, next_y), state.getGhostPosition(1), walls)
        dist2 = ghostDistance((next_x, next_y), state.getGhostPosition(2), walls)
        if dist is not None:
            # make the distance a number less than one otherwise the update
            # will diverge wildly
            features["closest-food"] = float(dist) / (walls.width * walls.height)

        if dist1 is not None and ghosts[0].isScared():
            features["ghost-1-danger"] = 1.0 - float(dist1) / (walls.width * walls.height)
        else:
            features["ghost-1-danger"] = 0.0

        if dist2 is not None and ghosts[1].isScared():
            features["ghost-2-danger"] = 1.0 - float(dist2) / (walls.width * walls.height)
        else:
            features["ghost-2-danger"] = 0.0

        features.divideAll(10.0)
        return features



class ExtendedExtractor1(FeatureExtractor):

    def getFeatures(self, state, action):
        # extract the grid of food and wall locations and get the ghost locations
        food = state.getFood()
        walls = state.getWalls()
        ghosts = state.getGhostStates()

        features = util.Counter()

        features["bias"] = 1.0

        # compute the location of pacman after he takes the action
        x, y = state.getPacmanPosition()
        dx, dy = Actions.directionToVector(action)
        next_x, next_y = int(x + dx), int(y + dy)

        # compute possible next locations for pacman
        neighbors = Actions.getLegalNeighbors((next_x, next_y), walls)
        s_n = 0
        s_sc = 0
        for n in neighbors:
            s_n += sum((n in Actions.getLegalNeighbors(g.getPosition(), walls) and not g.isScared()) for g in ghosts)
            s_sc += sum((n in Actions.getLegalNeighbors(g.getPosition(), walls) and g.isScared()) for g in ghosts)


        # count the number of non-scared ghosts 2-step away
        features["#-of-ghosts-2-steps-away"] = s_n

        # count the number of scared ghosts 2-step away
        features["#-of-scared-ghosts-2-steps-away"] = s_sc

        # if there is no danger of ghosts then add the food feature
        if not features["#-of-ghosts-2-steps-away"] and food[next_x][next_y]:
            features["eats-food"] = 1.0

        dist = closestFood((next_x, next_y), food, walls)
        if dist is not None:
            # make the distance a number less than one otherwise the update
            # will diverge wildly
            features["closest-food"] = float(dist) / (walls.width * walls.height)
        features.divideAll(10.0)
        return features



class ExtendedExtractor2(FeatureExtractor):

    def getFeatures(self, state, action):
        # extract the grid of food and wall locations and get the ghost locations
        food = state.getFood()
        walls = state.getWalls()
        ghosts = state.getGhostStates()

        features = util.Counter()

        features["bias"] = 1.0

        # compute the location of pacman after he takes the action
        x, y = state.getPacmanPosition()
        dx, dy = Actions.directionToVector(action)
        next_x, next_y = int(x + dx), int(y + dy)

        # compute possible next locations for pacman
        neighbors = Actions.getLegalNeighbors((next_x, next_y), walls)
        s_n = 0
        s_sc = 0
        for n in neighbors:
            ns = Actions.getLegalNeighbors(n, walls)
            for i in ns:
                s_n += sum((i in Actions.getLegalNeighbors(g.getPosition(), walls) and not g.isScared()) for g in ghosts)
                s_sc += sum((i in Actions.getLegalNeighbors(g.getPosition(), walls) and g.isScared()) for g in ghosts)


        # count the number of non-scared ghosts 2-step away
        features["#-of-ghosts-3-steps-away"] = s_n

        # count the number of scared ghosts 2-step away
        features["#-of-scared-ghosts-3-steps-away"] = s_sc

        # if there is no danger of ghosts then add the food feature
        if not features["#-of-ghosts-3-steps-away"] and food[next_x][next_y]:
            features["eats-food"] = 1.0

        dist = closestFood((next_x, next_y), food, walls)
        if dist is not None:
            # make the distance a number less than one otherwise the update
            # will diverge wildly
            features["closest-food"] = float(dist) / (walls.width * walls.height)
        features.divideAll(10.0)
        return features



class ExtendedExtractor3(FeatureExtractor):

    def getFeatures(self, state, action):
        # extract the grid of food and wall locations and get the ghost locations
        food = state.getFood()
        walls = state.getWalls()
        ghosts = state.getGhostStates()

        features = util.Counter()

        features["bias"] = 1.0

        # compute the location of pacman after he takes the action
        x, y = state.getPacmanPosition()
        dx, dy = Actions.directionToVector(action)
        next_x, next_y = int(x + dx), int(y + dy)

        # compute possible next locations for pacman
        neighbors = Actions.getLegalNeighbors((next_x, next_y), walls)
        s_n = 0
        s_sc = 0
        for n in neighbors:
            s_n += sum((n in Actions.getLegalNeighbors(g.getPosition(), walls) and not g.isScared()) for g in ghosts)
            s_sc += sum((n in Actions.getLegalNeighbors(g.getPosition(), walls) and g.isScared()) for g in ghosts)


        # count the number of non-scared ghosts 2-step away
        features["#-of-ghosts-2-steps-away"] = s_n

        # count the number of scared ghosts 2-step away
        features["#-of-scared-ghosts-2-steps-away"] = s_sc

        # count the number of non-scared ghosts 1-step away
        features["#-of-ghosts-1-step-away"] = sum(((next_x, next_y) in Actions.getLegalNeighbors(g.getPosition(), walls) and not g.isScared()) for g in ghosts)

        # count the number of scared ghosts 1-step away
        features["#-of-scared-ghosts-1-step-away"] = sum(((next_x, next_y) in Actions.getLegalNeighbors(g.getPosition(), walls) and g.isScared()) for g in ghosts)

        # if there is no danger of ghosts then add the food feature
        if not features["#-of-ghosts-2-steps-away"] and food[next_x][next_y]:
            features["eats-food"] = 1.0

        dist = closestFood((next_x, next_y), food, walls)
        if dist is not None:
            # make the distance a number less than one otherwise the update
            # will diverge wildly
            features["closest-food"] = float(dist) / (walls.width * walls.height)
        features.divideAll(10.0)
        return features



class ExtendedExtractor4(FeatureExtractor):
    def getFeatures(self, state, action):
        # extract the grid of food and wall locations and get the ghost locations
        food = state.getFood()
        walls = state.getWalls()
        ghosts = state.getGhostStates()

        features = util.Counter()

        features["bias"] = 1.0

        # compute the location of pacman after he takes the action
        x, y = state.getPacmanPosition()
        dx, dy = Actions.directionToVector(action)
        next_x, next_y = int(x + dx), int(y + dy)

        # compute possible next locations for pacman
        neighbors = Actions.getLegalNeighbors((next_x, next_y), walls)
        s_n = 0
        s_sc = 0
        for n in neighbors:
            ns = Actions.getLegalNeighbors(n, walls)
            for i in ns:
                s_n += sum((i in Actions.getLegalNeighbors(g.getPosition(), walls) and not g.isScared()) for g in ghosts)
                s_sc += sum((i in Actions.getLegalNeighbors(g.getPosition(), walls) and g.isScared()) for g in ghosts)


        # count the number of non-scared ghosts 2-step away
        features["#-of-ghosts-3-steps-away"] = s_n

        # count the number of scared ghosts 2-step away
        features["#-of-scared-ghosts-3-steps-away"] = s_sc

        # count the number of non-scared ghosts 1-step away
        features["#-of-ghosts-1-step-away"] = sum(((next_x, next_y) in Actions.getLegalNeighbors(g.getPosition(), walls) and not g.isScared()) for g in ghosts)

        # count the number of scared ghosts 1-step away
        features["#-of-scared-ghosts-1-step-away"] = sum(((next_x, next_y) in Actions.getLegalNeighbors(g.getPosition(), walls) and g.isScared()) for g in ghosts)

        # if there is no danger of ghosts then add the food feature
        if not features["#-of-ghosts-3-steps-away"] and food[next_x][next_y]:
            features["eats-food"] = 1.0

        dist = closestFood((next_x, next_y), food, walls)
        if dist is not None:
            # make the distance a number less than one otherwise the update
            # will diverge wildly
            features["closest-food"] = float(dist) / (walls.width * walls.height)
        features.divideAll(10.0)
        return features



class ExtendedExtractor5(FeatureExtractor):
    def getFeatures(self, state, action):
        # extract the grid of food and wall locations and get the ghost locations
        food = state.getFood()
        walls = state.getWalls()
        ghosts = state.getGhostStates()

        features = util.Counter()

        features["bias"] = 1.0

        # compute the location of pacman after he takes the action
        x, y = state.getPacmanPosition()
        dx, dy = Actions.directionToVector(action)
        next_x, next_y = int(x + dx), int(y + dy)

        # compute possible next locations for pacman
        neighbors = Actions.getLegalNeighbors((next_x, next_y), walls)
        s_n2 = 0
        s_sc2 = 0
        for n in neighbors:
            s_n2 += sum((n in Actions.getLegalNeighbors(g.getPosition(), walls) and not g.isScared()) for g in ghosts)
            s_sc2 += sum((n in Actions.getLegalNeighbors(g.getPosition(), walls) and g.isScared()) for g in ghosts)
        s_n3 = 0
        s_sc3 = 0
        for n in neighbors:
            ns = Actions.getLegalNeighbors(n, walls)
            for i in ns:
                s_n3 += sum((i in Actions.getLegalNeighbors(g.getPosition(), walls) and not g.isScared()) for g in ghosts)
                s_sc3 += sum((i in Actions.getLegalNeighbors(g.getPosition(), walls) and g.isScared()) for g in ghosts)


        # count the number of non-scared ghosts 2-step away
        features["#-of-ghosts-2-steps-away"] = s_n2 

        # count the number of scared ghosts 2-step away
        features["#-of-scared-ghosts-2-steps-away"] = s_sc2 

        # count the number of non-scared ghosts 2-step away
        features["#-of-ghosts-3-steps-away"] = s_n3 

        # count the number of scared ghosts 2-step away
        features["#-of-scared-ghosts-3-steps-away"] = s_sc3 

        # count the number of non-scared ghosts 1-step away
        features["#-of-ghosts-1-step-away"] = sum(((next_x, next_y) in Actions.getLegalNeighbors(g.getPosition(), walls) and not g.isScared()) for g in ghosts)

        # count the number of scared ghosts 1-step away
        features["#-of-scared-ghosts-1-step-away"] = sum(((next_x, next_y) in Actions.getLegalNeighbors(g.getPosition(), walls) and g.isScared()) for g in ghosts)

        # if there is no danger of ghosts then add the food feature
        if not features["#-of-ghosts-1-steps-away"] and food[next_x][next_y]:
            features["eats-food"] = 1.0

        dist = closestFood((next_x, next_y), food, walls)
        if dist is not None:
            # make the distance a number less than one otherwise the update
            # will diverge wildly
            features["closest-food"] = float(dist) / (walls.width * walls.height)
        features.divideAll(10.0)
        return features





class ExtendedExtractor6(FeatureExtractor):
    def getFeatures(self, state, action):
        # extract the grid of food and wall locations and get the ghost locations
        food = state.getFood()
        walls = state.getWalls()
        ghosts = state.getGhostStates()

        features = util.Counter()

        features["bias"] = 1.0

        # compute the location of pacman after he takes the action
        x, y = state.getPacmanPosition()
        dx, dy = Actions.directionToVector(action)
        next_x, next_y = int(x + dx), int(y + dy)

        # compute possible next locations for pacman
        neighbors = Actions.getLegalNeighbors((next_x, next_y), walls)
        s_n2 = 0
        s_sc2 = 0
        for n in neighbors:
            s_n2 += sum((n in Actions.getLegalNeighbors(g.getPosition(), walls) and not g.isScared()) for g in ghosts)
            s_sc2 += sum((n in Actions.getLegalNeighbors(g.getPosition(), walls) and g.isScared()) for g in ghosts)
        s_n3 = 0
        s_sc3 = 0
        for n in neighbors:
            ns = Actions.getLegalNeighbors(n, walls)
            for i in ns:
                s_n3 += sum((i in Actions.getLegalNeighbors(g.getPosition(), walls) and not g.isScared()) for g in ghosts)
                s_sc3 += sum((i in Actions.getLegalNeighbors(g.getPosition(), walls) and g.isScared()) for g in ghosts)


        # count the number of non-scared ghosts 2-step away
        features["#-of-ghosts-2-steps-away"] = s_n2 + sum(((next_x, next_y) in Actions.getLegalNeighbors(g.getPosition(), walls) and not g.isScared()) for g in ghosts)

        # count the number of scared ghosts 2-step away
        features["#-of-scared-ghosts-2-steps-away"] = s_sc2 + sum(((next_x, next_y) in Actions.getLegalNeighbors(g.getPosition(), walls) and g.isScared()) for g in ghosts)

        # count the number of non-scared ghosts 2-step away
        features["#-of-ghosts-3-steps-away"] = s_n3 + s_n2 + sum(((next_x, next_y) in Actions.getLegalNeighbors(g.getPosition(), walls) and not g.isScared()) for g in ghosts)

        # count the number of scared ghosts 2-step away
        features["#-of-scared-ghosts-3-steps-away"] = s_sc3 + s_sc2 + sum(((next_x, next_y) in Actions.getLegalNeighbors(g.getPosition(), walls) and g.isScared()) for g in ghosts)

        # count the number of non-scared ghosts 1-step away
        features["#-of-ghosts-1-step-away"] = sum(((next_x, next_y) in Actions.getLegalNeighbors(g.getPosition(), walls) and not g.isScared()) for g in ghosts)

        # count the number of scared ghosts 1-step away
        features["#-of-scared-ghosts-1-step-away"] = sum(((next_x, next_y) in Actions.getLegalNeighbors(g.getPosition(), walls) and g.isScared()) for g in ghosts)

        # if there is no danger of ghosts then add the food feature
        if not features["#-of-ghosts-1-steps-away"] and food[next_x][next_y]:
            features["eats-food"] = 1.0

        dist = closestFood((next_x, next_y), food, walls)
        if dist is not None:
            # make the distance a number less than one otherwise the update
            # will diverge wildly
            features["closest-food"] = float(dist) / (walls.width * walls.height)
        features.divideAll(10.0)
        return features


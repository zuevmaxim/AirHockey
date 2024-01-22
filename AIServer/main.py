import numpy as np
from itertools import count
from collections import namedtuple

import torch
import torch.nn as nn
import torch.nn.functional as F
import torch.optim as optim
from torch.distributions import Categorical

import sys

SavedAction = namedtuple('SavedAction', ['action', 'value'])


class Policy(nn.Module):
    """
    implements both actor and critic in one model
    """

    def __init__(self, state_size, action_size):
        super(Policy, self).__init__()
        self.affine1 = nn.Linear(state_size, 128)

        # actor's layer
        self.action_head = nn.Linear(128, action_size)

        # critic's layer
        self.value_head = nn.Linear(128, 1)

        # action & reward buffer
        self.saved_actions = []
        self.rewards = []

    def forward(self, x):
        """
        forward of both actor and critic
        """
        x = F.relu(self.affine1(x))

        # actor: choses action to take from state s_t
        action = self.action_head(x)

        # critic: evaluates being in the state s_t
        state_values = self.value_head(x)

        # return values for both actor and critic as a tuple of 2 values:
        # 1. a list with the probability of each action over the action space
        # 2. the value from state s_t
        return action, state_values


state_size = 12
model = Policy(state_size, 2)
optimizer = optim.Adam(model.parameters(), lr=3e-2)
eps = np.finfo(np.float32).eps.item()
gamma = 0.99


def select_action(state):
    state = torch.from_numpy(state).float()
    action, state_value = model(state)

    # save to action buffer
    model.saved_actions.append(SavedAction(action, state_value))

    # the action to take (left or right)
    return action


def finish_episode():
    """
    Training code. Calculates actor and critic loss and performs backprop.
    """
    R = 0
    saved_actions = model.saved_actions
    policy_losses = []  # list to save actor (policy) loss
    value_losses = []  # list to save critic (value) loss
    returns = []  # list to save the true values

    # calculate the true value using rewards returned from the environment
    for r in model.rewards[::-1]:
        # calculate the discounted value
        R = r + gamma * R
        returns.insert(0, R)

    returns = torch.tensor(returns)
    returns = (returns - returns.mean()) / (returns.std() + eps)

    for (action, value), R in zip(saved_actions, returns):
        advantage = R - value.item()

        # calculate actor (policy) loss
        policy_losses.append(-log_prob * advantage)

        # calculate critic (value) loss using L1 smooth loss
        value_losses.append(F.smooth_l1_loss(value, torch.tensor([R])))

    # reset gradients
    optimizer.zero_grad()

    # sum up all the values of policy_losses and value_losses
    loss = torch.stack(policy_losses).sum() + torch.stack(value_losses).sum()

    # perform backprop
    loss.backward()
    optimizer.step()

    # reset rewards and action buffer
    del model.rewards[:]
    del model.saved_actions[:]


def random_action(self):
    x = self.rand.uniform(-1, 1)
    y = self.rand.uniform(-1, 1)
    return x, y


if __name__ == "__main__":
    in_file = open(sys.argv[1], "r")
    out_file = open(sys.argv[2], "w")
    is_random = len(sys.argv) > 3 and "random" == sys.argv[1]

    while True:
        command = in_file.readline().strip(" \n").split(" ")
        if len(command) < state_size:
            if command[0] == "success" or command[0] == "fail":
                reward = 1 if command[0] == "success" else 0
                model.rewards[-1] = reward
                finish_episode()
        else:
            state = np.array(list(map(float, command)))
            assert len(state) == state_size

            x, y = select_action(state)
            model.rewards.append(0)
            out_file.write("{} {}\n".format(x, y))
            out_file.flush()

import random
import sys
import tensorflow.compat.v1 as tf
import numpy as np

from keras.layers import Dense, Input, Add
from keras.models import Model
from tensorflow.keras.optimizers import Adam

tf.disable_v2_behavior()
tf.disable_resource_variables()
tf.logging.set_verbosity(tf.logging.ERROR)


def create_actor_model(state_size, action_size):
    state_input = Input(shape=state_size)
    h1 = Dense(24, activation='relu')(state_input)
    h2 = Dense(24, activation='relu')(h1)
    output = Dense(action_size, activation='tanh')(h2)

    model = Model(state_input, output)
    adam = Adam(learning_rate=0.001)
    model.compile(loss="mse", optimizer=adam)
    return state_input, model


def create_critic_model(state_size, action_size):
    state_input = Input(shape=state_size)
    state_h1 = Dense(24, activation='relu')(state_input)
    state_h2 = Dense(24)(state_h1)

    action_input = Input(shape=action_size)
    action_h1 = Dense(24)(action_input)

    merged = Add()([state_h2, action_h1])
    merged_h1 = Dense(24, activation='relu')(merged)
    output = Dense(1, activation='relu')(merged_h1)
    model = Model([state_input, action_input], output)

    adam = Adam(learning_rate=0.001)
    model.compile(loss="mse", optimizer=adam)
    return state_input, action_input, model


class MyModel:
    def __init__(self):
        self.actor_input, self.actor_model = create_actor_model(12, 2)
        self.critic_state_input, self.critic_action_input, self.critic_model = create_critic_model(12, 2)
        self.history = []
        self.gamma = 0.99

        # random config
        self.trained = False
        self.epsilon = 1.0
        self.epsilon_decay = .995
        self.rand = random.Random()

        # inter-action memory
        self.prev_state = None
        self.action = None

        # gradients
        self.actor_critic_grad = tf.placeholder(tf.float32, [None, 2])
        actor_model_weights = self.actor_model.trainable_weights
        self.actor_grads = tf.gradients(self.actor_model.output, actor_model_weights, -self.actor_critic_grad)
        grads = zip(self.actor_grads, actor_model_weights)
        self.optimize = tf.train.AdamOptimizer(0.01).apply_gradients(grads)
        self.critic_grads = tf.gradients(self.critic_model.output, self.critic_action_input)

    def random_action(self):
        x = self.rand.uniform(-1, 1)
        y = self.rand.uniform(-1, 1)
        return x, y

    def predict(self, state):
        if self.prev_state is not None:
            self.add((self.prev_state, self.action, state, 0, False))
        self.prev_state = state

        self.epsilon *= self.epsilon_decay
        if self.rand.uniform(0, 1) < self.epsilon or not self.trained:
            self.action = self.random_action()
        else:
            self.action = self.actor_model.predict([state])[0]
        return self.action

    def add(self, sample):
        self.history.append(sample)

    def on_session_end(self, success):
        reward = 1 if success else -1
        model.add((self.prev_state, self.action, self.prev_state, reward, True))
        self.prev_state = None
        self.action = None
        self.train()

    def train(self):
        print(len(self.history))
        for sample in self.history:
            state, action, new_state, reward, done = sample
            if not done:
                target_action = self.actor_model.predict([new_state])[0]
                future_reward = self.critic_model.predict([new_state, target_action])[0]
                reward += self.gamma * future_reward
            self.critic_model.fit([state, action], reward)

        for sample in self.history:
            state, action, new_state, reward, _ = sample
            grads = self.critic_grads({
                self.critic_state_input: state,
                self.critic_action_input: action
            })

            self.optimize({
                self.actor_input: state,
                self.actor_critic_grad: grads
            })

        self.trained = True
        self.history = []


if __name__ == "__main__":
    in_file = open(sys.argv[1], "r")
    out_file = open(sys.argv[2], "w")
    is_random = len(sys.argv) > 3 and "random" == sys.argv[1]

    shape = 12
    model = MyModel()

    while True:
        command = in_file.readline().strip(" \n").split(" ")
        if len(command) < shape:
            if command[0] == "success" or command[0] == "fail":
                model.on_session_end(command[0] == "success")
        else:
            state = list(map(float, command))
            assert len(state) == shape

            x, y = model.predict(state)
            out_file.write("{} {}\n".format(x, y))
            out_file.flush()

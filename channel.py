from user_2 import User
import random

class Channel:
  def __init__(self, user_list=[]):
    self.user_list = []

  # Returns the size of the channel
  def size(self):
    return len(self.user_list)

  def get_users(self):
    return self.user_list

  def num_list(self):
    return [user.get_number() for user in self.user_list]

  def present_num(self, user_num):
    nums = self.num_list()
    return user_num in nums

  # Returns an array of users enqueued
  def get_queue(self):
    return [user for user in self.user_list if user.get_status() == 'Queue']

  # Return size of queue
  def queue_size(self):
    print('Queue size: ', len(self.get_queue()))
    return len(self.get_queue())

  # Finds the data of a random user, who does not have the same number
  # as the person searching for a user.
  def find_random_queued(self, search_user):
    unpaired = self.get_queue()
    unpaired.remove(search_user)

    return random.choice(unpaired)

  def random_pair(self, user):
    # Find new random users
    partner = self.find_random_queued(user)

    # Pair these two
    user.connect(partner)

    # Message the partner
    user.msg_self("You've been connected!")
    user.msg_partner("You've been connected!")
    
    print ('Random pair complete: ', user, partner)

  def add(self, user):
    self.user_list.append(user)
    user.set_channel(self)

  def remove_user(self, user):
    self.user_list.remove(user)
    user.set_channel(None)

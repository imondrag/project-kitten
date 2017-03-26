import twiliocomms as comms
import random

class User:
  def __init__(self, number=None, partner=None, sms=True, channel=None):
        self.number = number    # string
        self.partner = partner  # User
        self.sms = sms          # bool
        self.status = 'Lobby'   # string - Lobby, Queue, or Chat
        self.channel = channel
  
  def get_partner(self):
    return self.partner

  def get_status(self):
    return self.status

  def get_channel(self):
    return self.channel

  def get_number(self):
    return self.number

  def has_partner(self):
    return self.status == 'Chat'

  def set_partner(self, partner):
    self.partner = partner

  def set_channel(self, channel):
    self.channel = channel

  def set_status(self, status):
    self.status = status

  def msg_self(self, msg):
    comms.send(self, msg)

  def msg_partner(self, msg):
    comms.send(self.partner, msg)

  def disconnect(self):
    # Update the partner status
    if self.partner:
      self.msg_partner("Your partner has disconnected. Type /find to find someone else!")
      self.partner.partner = None
      self.partner.status = 'Lobby'

    # Update the user
    self.partner = None
    self.status = 'Lobby'

  def connect(self, partner):
    # Update the partner status
    if self.partner:
      self.disconnect()

    partner.partner = self
    self.partner = partner

    partner.status = 'Chat'
    self.status = 'Chat'


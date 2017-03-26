from twilio import twiml
from twilio.rest import TwilioRestClient

f = open('.key.txt', "r")
key = f.readlines()
f.close()

MSG_HELP = "Valid commands:\n/find - find random partner\n/leave - leave current chat\n/share - share your phone number with partner\n/fun - more fun commands!"

MSG_FUN = "Fun fun! :D\n/count - shows how popular your channel is"

ACCOUNT_SID = key[0][:-1]
AUTH_TOKEN = key[1][:-1]

client = TwilioRestClient(ACCOUNT_SID, AUTH_TOKEN)

API_LOGIN_SUCCESS = "valid"
API_LOGIN_FAIL = "error"
API_CONNECT_SUCCESS = "connect"
API_QUEUE_SUCCESS = "queue"
API_NO_PENDING_MSG = "nopending"

MSG_LOGIN_SUCCESS = "Welcome to the chatroom!"
MSG_LOGIN_FAIL = "I didn't recognize that room key, was it typed correctly?"
MSG_CONNECT_SUCCESS = "You've been connected!"
MSG_QUEUE_SUCCESS = "Locating a new partner...\ntype /leave to cancel search"

api_message_queue = {}

def get_queued(number):
  if number in api_message_queue.keys():
    queue = api_message_queue[number]
    if len(queue) != 0:
      return respond(None, queue.pop(0))
  return respond(None, API_NO_PENDING_MSG)

def send(user, message):  
  if user and user.sms:
    client.messages.create(
      to=user.number,
      from_="+17342742718",
      body=message
    )
    return
  else:
    # if there is a queue for this number
    if user.number in api_message_queue.keys():
      api_message_queue[user.number].append(message)
      print( "Current message queue for ", user.number, " is ", api_message_queue[user.number], len(api_message_queue[user.number]))
    else:
      api_message_queue[user.number] = [message]
      print( "Creating queue for ", user.number );

# Respond a message to a user
def respond(user, msg):
  resp = twiml.Response()
  if msg != None:
    resp.message(msg)
  
  return str(resp)

# Respond the help message to an invalid user input
def respond_help(user):
  return respond(user, MSG_HELP)

# Respond the fun message to an invalid user input
def respond_fun(user):
  return respond(user, MSG_FUN)

# Responds to the user when a login was successful
def respond_roomkey_success(user):
  if user.sms:
    return respond(user, MSG_LOGIN_SUCCESS)
  else:
    return respond(user, API_LOGIN_SUCCESS)

# Responds to the user when a login failed
def respond_roomkey_fail(user):
  if user and user.sms:
    return respond(user, MSG_LOGIN_FAIL)
  else:
    return respond(user, API_LOGIN_FAIL)
# Responds to the user when a connection was succesfully made

def respond_connect(user):
  if user.sms:
    return respond(user, MSG_CONNECT_SUCCESS)
  else:
    send(user, MSG_CONNECT_SUCCESS)
    return respond(user, API_CONNECT_SUCCESS)

# Responds to the user when they are placed in a queue
def respond_queue(user):
  if user.sms:
    return respond(user, MSG_QUEUE_SUCCESS)
  else:
    send(user, MSG_QUEUE_SUCCESS)
    return respond(user, API_QUEUE_SUCCESS)


from flask import Flask, request, abort
import twiliocomms as comms
from user_2 import User
from channel import Channel
app = Flask(__name__)

commands = ["/find", "/leave", "/share"]

channels = {}
channels['mhacks9'] = Channel()

############################
#### PROJECT KITTEN API ####
############################

def find_user(user_num):
  
  for channel in channels.values():
    user = next((user for user in channel.get_users() if user_num == user.get_number()), None)
    if user:
      return user

  return None

def print_msg_num(msg, num):
  print('msg    :',msg)
  print('number :',num)

# Debug Hello World
@app.route('/')
def hello_world():
  return 'Hello, World!'

# Used to recieve messages via sms
@app.route('/sms', methods=['GET', 'POST'])
def handle_sms():
  rcv_msg = request.values.get('Body')
  rcv_number = request.values.get('From')
  print_msg_num(rcv_msg, rcv_number);
  
  return handle_message(rcv_msg, rcv_number, True)

# Used to receive messages via api
@app.route('/api', methods=['GET', 'POST'])
def handle_apis():
  if request.method == 'POST':
    rcv_msg = request.values.get('Body')
    rcv_number = request.values.get('From')
    print_msg_num(rcv_msg, rcv_number);

    return handle_message(rcv_msg, rcv_number, False)

  elif request.method == 'GET':
    rcv_number = request.values.get("To")
    user = find_user(rcv_number)
    if not user:
      abort(401)
    
    return comms.get_queued(rcv_number)

# Used to handle an incoming sms or api message
def handle_message(rcv_msg, rcv_number, isSMS):
  user = find_user(rcv_number)

  if not user:  # if user not found, create new user
    print('user:',rcv_number,'not in database')
    if rcv_msg:
      rcv_msg = rcv_msg.strip().lower()

    user = User(rcv_number, sms=isSMS)

    args = rcv_msg.split(' ')
    if args[0] == "/new" and len(args[-1]) >= 5:
      chan = args[-1]
      if chan in channels.keys():
        user.msg_self("Channel " + chan + " already exists!")
        return comms.respond_roomkey_fail(user)

      channels[chan] = Channel()
      channels[chan].add(user)
      user.msg_self("You created the channel: " + chan)
      print("creating NEW channel: ", chan)
      return comms.respond_roomkey_success(user)
      
    if rcv_msg in channels.keys():
      channels[rcv_msg].add(user)
      print("adding user to channel: ", rcv_msg)
      return comms.respond_roomkey_success(user)
    else:
      print("user rejected from channel: ", rcv_msg)
      return comms.respond_roomkey_fail(user)

  # Evaluate commands of user
  if rcv_msg and rcv_msg.strip().lower() in commands:
    rcv_msg = rcv_msg.strip().lower()

    if rcv_msg == "/find":
      print('command was /find!')

      # Disconnect from an existing chat
      if user.get_partner():
        user.disconnect()

      user.set_status('Queue')
      
      # And connect to a new random one
      chan = user.get_channel()
      if chan.queue_size() >= 2:
        chan.random_pair(user)
        return comms.respond_connect(user)
      else:
        return comms.respond_queue(user)

    # Share phone number to partner upon request
    elif rcv_msg == "/share":
      print('command was /share!')

      # Send info to the partner
      user.msg_partner("Your partner's number is: " + user.get_number())

      # Notify that info was sent
      return comms.respond(user, "You shared your phone number: " + user.get_number())
    
    # Leave chat
    elif rcv_msg == "/leave":
      print('command was /leave!')
      if user.get_status() == 'Lobby':
        user.get_channel().remove_user(user)
        print('user has left the channel')
        return comms.respond(user, "You have been disconnected from the channel")

      if user.get_status() == 'Queue':
        user.set_status('Lobby')
        print('user has cancelled the search')
        return comms.respond(user, "You canceled the search")

      partner = user.get_partner()
      
      # Disconnect the user and partner
      user.disconnect()

      partner.msg_self("You have been disconnected")
      return comms.respond(user, "You have been disconnected")

  # Not a command; forward the message to the partner
  if user.get_partner():
    print('sending message to partner')
    user.msg_partner(rcv_msg)
    return comms.respond(user,None)

  # No successful response
  print('sending help to user')
  return comms.respond_help(user)
  
if __name__ == '__main__':
  app.run(host='0.0.0.0')
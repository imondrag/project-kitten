import twilio.twiml

def echo():
  """Respond to incoming requests."""
  resp = twilio.twiml.Response()
  resp.say("echo:"+resp.Message)
  print(resp)
  print(str(resp))
  return str(resp)

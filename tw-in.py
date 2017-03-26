from flask import Flask, request, redirect
from twilio import twiml

@app.route("/sms", methods=['GET', 'POST'])
def onSMSIn( onSMSInCallback ):
  
  sendhelp = onSMSInCallback( request.values.get('From'), request.values.get('Body') )

  if sendhelp:
    resp = twiml.Response()
    resp.message(HELP_MESSAGE)
  return str(resp)

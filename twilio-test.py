from twilio.rest import TwilioRestClient
 
# put your own credentials here 
ACCOUNT_SID = "AC3778e24ca3d583206235e0e4c6730c59" 
AUTH_TOKEN = "2afa4cdb89be15a0903e9d0c3d4d9c85" 
  
client = TwilioRestClient(ACCOUNT_SID, AUTH_TOKEN)

client.messages.create(
  to="+19375646576", 
  from_="+17342742718", 
  body="We just ordered pizza *sadface*"
) 


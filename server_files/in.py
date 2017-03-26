from flask import Flask, request, redirect
from twilio import twiml

app = Flask(__name__)

@app.route("/api", methods=['GET', 'POST'])
def onPostGet():
  for key in request.values:
    print(key, ": ", request.values.get(key))
  resp = twiml.Response()
  resp.message("yo")
  return str(resp)


if __name__ == '__main__':
  app.run(host='0.0.0.0')

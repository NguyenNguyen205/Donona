import stripe
from flask import Flask, Response, request, make_response, jsonify
from flask_cors import CORS, cross_origin
import json
import firebase_admin
from firebase_admin import credentials, firestore
import requests

app = Flask(__name__)
cors = CORS(app=app)
app.config['Access-Control-Allow-Origin'] = '*'
stripe.api_key = "sk_test_51PSxSqRsoCurEVDXT1nXaQgwZ60fIhf3d2sGFVDoTzdUajfEVJ9h0iWwzDVU0jJNAVoEezzx6I6zOmG8RuTPHEgd00nBfA7YCE"
vietmapkey = "77080684e9ccee64241cc6682a316130a475ee2eb26bb04d"
cred = credentials.Certificate("serviceKey.json")
firebase_admin.initialize_app(cred)
db = firestore.client()

@app.route('/create-customer', methods=['POST'])
def create_customer():
    data = json.loads(request.data)
    email = data['email']
    name = data['name']
    print(email)
    customer = stripe.Customer.create(
        email=email,
        name=name,
        address={
            "city": "Brothers",
            "country": "US",
            "line1": "27 Fredrick Ave",
            "postal_code": "97712",
            "state": "CA",
        },
    )
    print(customer["id"])
    return jsonify(customer["id"])


@app.route('/create-subscription', methods=['POST'])
def create_subscription():
    data = json.loads(request.data)
    customer_id = data['customerId']
    price_id = data['priceId']

    try:
        # Create the subscription. Note we're expanding the Subscription's
        # latest invoice and that invoice's payment_intent
        # so we can pass it to the front end to confirm the payment
        subscription = stripe.Subscription.create(
            customer=customer_id,
            items=[{
                'price': price_id,
            }],
            payment_behavior='default_incomplete',
            payment_settings={'save_default_payment_method': 'on_subscription'},
            expand=['latest_invoice.payment_intent'],
        )
        return jsonify(subscriptionId=subscription.id, clientSecret=subscription.latest_invoice.payment_intent.client_secret)

    except Exception as e:
        return jsonify(error={'message': e.user_message}), 400

@app.route('/cancel-subscription', methods=['POST'])
def cancelSubscription():
    data = json.loads(request.data)
    try:
         # Cancel the subscription by deleting it
        deletedSubscription = stripe.Subscription.delete(data['subscriptionId'])
        return jsonify(deletedSubscription)
    except Exception as e:
        return jsonify(error=str(e)), 403

# No webhook for now
@app.route('/webhook', methods=['POST'])
def webhook_received():
  # You can use webhooks to receive information about asynchronous payment events.
  # For more about our webhook events check out https://stripe.com/docs/webhooks.
  webhook_secret = os.getenv('STRIPE_WEBHOOK_SECRET')
  request_data = json.loads(request.data)

  if webhook_secret:
    # Retrieve the event by verifying the signature using the raw body and secret if webhook signing is configured.
    signature = request.headers.get('stripe-signature')
    try:
      event = stripe.Webhook.construct_event(
        payload=request.data, sig_header=signature, secret=webhook_secret)
      data = event['data']
    except Exception as e:
      return e
    # Get the type of webhook event sent - used to check the status of PaymentIntents.
    event_type = event['type']
  else:
    data = request_data['data']
    event_type = request_data['type']

  data_object = data['object']

  if event_type == 'invoice.paid':
    # Used to provision services after the trial has ended.
    # The status of the invoice will show up as paid. Store the status in your
    # database to reference when a user accesses your service to avoid hitting rate
    # limits.
    print(data)

  if event_type == 'invoice.payment_failed':
    # If the payment fails or the customer does not have a valid payment method,
    # an invoice.payment_failed event is sent, the subscription becomes past_due.
    # Use this webhook to notify your user that their payment has
    # failed and to retrieve new card details.
    print(data)

  if event_type == 'customer.subscription.deleted':
    # handle subscription canceled automatically based
    # upon your subscription settings. Or if the user cancels it.
    print(data)

  return jsonify({'status': 'success'})

# Map API
@app.route('/api/search') 
def search():      
    text = request.args.get('text')
    if (text == ""):
      return Response("No text provided", status = 200)

    # return Response(res, status=200)
    suggestion = []
    suggestionMap = {}
    url = "https://maps.vietmap.vn/api/autocomplete/v3?apikey=" + vietmapkey + "&text=" + text + "&cityId=12"

    docs = db.collection("coffeePlace").stream()
    mid = {}
    key = ""
    value = ""

    for doc in docs:
        if (len(suggestion) >= 10): 
            break
        mid = doc.to_dict()
        if (text.lower().strip() in mid['name'].lower()):
            key = mid['name'] + " " + mid["address"]
            suggestion.append(key)
            suggestionMap[key] = mid["ref_id"]
        
    ## Call vietmap api
    res = requests.get(url)
    vals = res.json()
    for place in vals:
        if (len(suggestion) >= 10):
            break
        key = place["name"] + " " + place["address"]
        suggestion.append(key)
        suggestionMap[key] = place["ref_id"]

    ## May need to change format to include distance
    return Response(json.dumps(suggestionMap, indent = 4), status = 200)

@app.route('/api')
def home():
    return Response("Hello world", status=200)

def main():
    app.run(debug=True, port=5528)


if __name__ == "__main__":
    main()
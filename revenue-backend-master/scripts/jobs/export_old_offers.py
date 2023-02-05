import json

from pymongo import MongoClient

host = ["10.40.12.139", "10.40.12.140", "10.40.12.133"]

mongo_client = MongoClient(host=host)
db = mongo_client.get_database("sedb")
col = db.get_collection("OfferConfigTemp")
data = col.find({"_id.service": "airteltv"})
offers = []
for d in data:
    o = {"id": d['_id']['offerId'], 'isSystemOffer': d['isSystemOffer'], 'ruleExpression': d['ruleExpression'],
         'hierarchy': d['hierarchy'], 'provisionType': d['provisionType']}
    if 'packIds' in d:
        packs = []
        for k, v in d['packIds'].items():
            packs.append(k)
        o['packs'] = "[" + ','.join(packs) + ']'
    offers.append(o)

print(json.dumps(offers))

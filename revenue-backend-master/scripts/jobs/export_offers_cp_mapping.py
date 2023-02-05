import json

from pymongo import MongoClient

mongo_creds = {
    "host": ["10.40.12.162", "10.40.12.216", "10.40.12.45"],
    "username": "user_subscription",
    "password": "3Sy8wDck6Q",
    "authSource": "subscription"
}
# mongo_creds= {
#     "host": ["10.80.0.128","10.80.0.240"],
#     "username": "capi",
#     "password": "S>Y9hv%:#Z",
#     "authSource": "admin"
# }
service = "airteltv"
mongo_client = MongoClient(**mongo_creds)
db = mongo_client.get_database("subscription")
offer_col = db.get_collection("offers")
partners_col = db.get_collection("partners")
plans_col = db.get_collection("plans")
db_offer = offer_col.find({"service": service, 'provisionType': 'PAID'})
offers = []
# db_product = products_col.find({"service": service})
for d in db_offer:
    o = {"_id": d['_id'], 'ad_type': "VIDEO_AD"}
    if 'products' in d:
        cps = []
        pack_groups = list(d['products'].values())
        for cp in partners_col.find({'packGroup': {'$in': pack_groups}}, {'_id': 1}):
            cps.append(cp['_id'])
        o['ad_black_listed_cps'] = cps
    offers.append(o)

print(json.dumps(offers))

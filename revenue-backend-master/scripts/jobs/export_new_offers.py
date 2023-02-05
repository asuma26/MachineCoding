import json

from pymongo import MongoClient

# mongo_creds = {
#     "host": ["10.40.12.162", "10.40.12.216", "10.40.12.45"],
#     "username": "user_subscription",
#     "password": "3Sy8wDck6Q",
#     "authSource": "subscription"
# }
mongo_creds= {
    "host": ["10.80.0.128","10.80.0.240"],
    "username": "capi",
    "password": "S>Y9hv%:#Z",
    "authSource": "admin"
}
service = "music"
mongo_client = MongoClient(**mongo_creds)
db = mongo_client.get_database("subscription")
offer_col = db.get_collection("offers")
products_col = db.get_collection("products")
plans_col = db.get_collection("plans")
db_offer = offer_col.find({"service": service})
offers = []
# db_product = products_col.find({"service": service})
for d in db_offer:
    o = {"id": d['_id'], 'title': d['title'], 'ruleExpression': d['ruleExpression'],
         'hierarchy': d['hierarchy'], 'provisionType': d['provisionType']}
    if 'products' in d:
        products = []
        for k, v in d['products'].items():
            pack = {}
            product = products_col.find_one({"_id": int(k)})
            appIds = "[" + ','.join(product['eligibleAppIds']) + ']'
            pack["_id"] = k
            pack['appIds'] = appIds
            pack['cpName'] = product['cpName']
            pack['hierarchy'] = product['hierarchy']
            products.append(pack)
        o['products'] = products
    if 'plans' in d:
        plans = []
        for p in d['plans']:
            plan = {}
            db_plan = plans_col.find_one({"_id": p})
            plan["_id"] = p
            plan['duration'] = db_plan['period']['validity']
            plan['price'] = db_plan['price']['amount']
            plan['provisionType'] = db_plan['type']
            plan['hierarchy'] = db_plan['hierarchy']
            plans.append(plan)
        o['plan'] = plans
    offers.append(o)

print(json.dumps(offers))

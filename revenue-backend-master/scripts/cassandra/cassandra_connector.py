import pandas as pd
from cassandra.cluster import Cluster
from cassandra.auth import PlainTextAuthProvider

hostname="10.40.12.123"
username=""
password=""
keyspace="userdata"

nodes = []
nodes.append(hostname)
auth_provider = PlainTextAuthProvider(username=username, password=password)
cluster = Cluster(contact_points = nodes, port=9042,auth_provider=auth_provider)
session = cluster.connect(keyspace)

df = pd.read_excel (r'./Update16.xlsx')
print (df)
for index,row in df.iterrows():
    uid = row['uid']
    planId = row['planid']
    statement = "SELECT * FROM subscription where uid='{uid}' and service='music' and pack_group='wynk_music' ALLOW FILTERING".format(uid = uid)
    print(statement)
    rows = session.execute(statement)
    for row in rows:
        print(row.uid)
        statement2 = "Update user_plan_details Set end_date='{validity}' Where uid='{uid}' and service='music' and plan_id={planId}".format(validity = int(row.next_charging_date.timestamp()*1000+19800000), uid = uid, planId=int(planId))
        print(statement2)
        session.execute(statement2)

session.shutdown()
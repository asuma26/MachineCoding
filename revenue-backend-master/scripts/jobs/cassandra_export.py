#!/usr/bin/env python
# pip install cassandra-driver
# pip install boto3
import codecs
import csv
import json

import boto3
from cassandra.cluster import Cluster

cluster = Cluster(['10.40.12.123', '10.40.13.220'])
session = cluster.connect('userdata')
rows = session.execute('SELECT * FROM subscription limit 5')
for user_row in rows:
    print(user_row.uid, user_row.service)
user_lookup_stmt = session.prepare("SELECT * FROM subscription WHERE service = 'music' and uid=?")
path = "s3://data-science-prod-redshift-archive/hungama_data/single_airtel_user_having_pack_null_for_aman/month=2020" \
       "-07/part-00000-b97df420-8253-4ed2-9047-7782e4ddab27-c000.csv "

s3 = boto3.client('s3')
bucket_key = path.split("s3://")[1].split('/', 1)
bucket = bucket_key[0]
key = bucket_key[1]
obj = s3.get_object(Bucket=bucket, Key=key)
with open("nullPackUsers1.log", 'a') as out:
    for line in csv.DictReader(codecs.iterdecode(obj['Body'].iter_lines(), 'utf-8')):
        user = session.execute(user_lookup_stmt, [dict(line)['uid']])
        for u in user:
            row = {'service': u.service, 'uid': u.uid, 'pack_group': u.pack_group, 'active': u.active,
                   'auto_renewal_off': u.auto_renewal_off, 'deactivation_channel': u.deactivation_channel,
                   'next_charging_date': u.next_charging_date.timestamp() * 1000,
                   'partner_product_id': u.partner_product_id}
            if 'payment_meta_data' in row and row['payment_meta_data']:
                row['payment_meta_data'] = dict(u.payment_meta_data)
            row['payment_method'] = u.payment_method
            row['product_id'] = u.product_id
            row['renewal_under_process'] = u.renewal_under_process
            row['subscription_date'] = u.subscription_date.timestamp() * 1000
            row['subscription_end_date'] = u.subscription_end_date.timestamp() * 1000
            row['subscription_in_progress'] = u.subscription_in_progress
            row['subscription_status'] = u.subscription_status
            if u.unsubscription_date:
                row['unsubscription_date'] = u.unsubscription_date.timestamp() * 1000
            row['valid_till_date'] = u.valid_till_date.timestamp() * 1000
            out.write(json.dumps(row) + '\n')


with open("nullPackUsers1.log", 'a') as out:
    for line in csv.DictReader(codecs.iterdecode(obj['Body'].iter_lines(), 'utf-8')):
        uid = dict(line)['uid']
        user = session.execute(user_lookup_stmt, [dict(line)['uid']])
        if len(user.current_rows) > 0:
            for u in user:
                row = {'service': u.service, 'uid': uid, 'pack_group': u.pack_group, 'active': u.active,
                       'auto_renewal_off': u.auto_renewal_off, 'deactivation_channel': u.deactivation_channel,
                       'next_charging_date': u.next_charging_date.timestamp() * 1000,
                       'partner_product_id': u.partner_product_id}
                if row['payment_meta_data']:
                    row['payment_meta_data'] = dict(u.payment_meta_data)
                row['payment_method'] = u.payment_method
                row['product_id'] = u.product_id
                row['renewal_under_process'] = u.renewal_under_process
                row['subscription_date'] = u.subscription_date.timestamp() * 1000
                row['subscription_end_date'] = u.subscription_end_date.timestamp() * 1000
                row['subscription_in_progress'] = u.subscription_in_progress
                row['subscription_status'] = u.subscription_status
                if u.unsubscription_date:
                    row['unsubscription_date'] = u.unsubscription_date.timestamp() * 1000
                row['valid_till_date'] = u.valid_till_date.timestamp() * 1000
                out.write(json.dumps(row) + '\n')
            else:
                row = {'uid': uid}
                out.write(json.dumps(row) + '\n')
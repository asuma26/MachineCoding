import argparse
import base64
import hashlib
import hmac
import json
import time
from datetime import datetime

import requests
from cassandra.cluster import Cluster
from rediscluster import RedisCluster

from reader.file_reader import read_csv

startup_nodes = [{"host": "wcf-redis.8hya7e.clustercfg.aps1.cache.amazonaws.com", "port": "6379"}]
rc = RedisCluster(startup_nodes=startup_nodes, decode_responses=True, skip_full_coverage_check=True)

cluster = Cluster(['10.40.12.123', '10.40.13.220'])
session = cluster.connect('userdata')

service = 'music'
timestamp = int(datetime.today().timestamp() * 1000) - 86400000
fmf_plan_ids = (1000, 1010, 1011, 1012)


def offer_provision(uid, msisdn, service):
    timestamp_str = str(round(time.time() * 1000))
    path = "/wynk/s2s/v1/offer/provision"
    host = "http://subscription.wcf.internal"
    url = host + path
    data = {
        "appId": "mobility",
        "appVersion": "3.8.0.3",
        "buildNo": 3709,
        "deviceId": "7cb58f931970efee",
        "msisdn": f"{msisdn}",
        "os": "android",
        "uid": f"{uid}",
        "service": f"{service}"
    }
    http_method = "POST"
    response = requests.post(url, json=data,
                             headers=generate_headers(http_method, path, timestamp_str, json.dumps(data)))
    print(f'uid = {uid}, status_code = {response.status_code}')
    # print(f'uid = {uid}, response_body = {response.json()}')
    # response.raise_for_status()


def generate_headers(http_method, path, timestamp, data=''):
    message = f'{http_method}{path}{data}{timestamp}'
    # print(message)
    key = bytes("e9a467c8a8ce787a0787901b137ef70b", 'utf-8')
    message = bytes(message, 'utf-8')
    hashed = base64.b64encode(hmac.new(key, message, hashlib.sha1).digest())
    return {
        "x-wynk-timestamp": timestamp,
        "x-wynk-atkn": "340b596b576da4fc73b21f852d1390b2:" + str(hashed.decode('utf-8'))
    }


def correct_entry(doc):
    uid = doc['user_id']
    msisdn = doc['msisdn']
    plans = []
    result = session.execute(
        f"SELECT plan_id FROM user_plan_details WHERE service = '{service}' and uid='{uid}' and"
        f" plan_id in {fmf_plan_ids}")
    for r in result:
        plans.append(r.plan_id)
    # update_plan_stmt = f"Update user_plan_details Set end_date={timestamp},payment_channel='data_correction'
    # ,auto_renew=false,offer_id=10000,plan_count=1 where uid='{uid}'" \
    update_plan_stmt = f"Update user_plan_details Set end_date={timestamp} where uid='{uid}'" \
                       f" and service='{service}' and plan_id in {tuple(plans)}"
    if len(plans) == 1:
        update_plan_stmt = f"Update user_plan_details Set end_date={timestamp} where uid='{uid}'" \
                           f" and service='{service}' and plan_id = {plans[0]}"
    # print(update_plan_stmt)
    user_plan_redis_key = f"UserdataService::user-plan:{uid}:{service}"
    session.execute(update_plan_stmt)
    rc.delete(user_plan_redis_key)
    print(f"{uid}")
    # offer_provision(uid, msisdn, service)
    # break


def execute():
    print("Starting base correction")
    arg_parser = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    arg_parser.add_argument('command', help='Command')
    arg_parser.add_argument('--file_path', help='Source file path')
    args = arg_parser.parse_args()
    path = args.file_path
    for doc in read_csv(path):
        try:
            correct_entry(doc)
        except Exception:
            print(f"Error occurred for doc: {doc}")
    print("Completed execution for correction script")

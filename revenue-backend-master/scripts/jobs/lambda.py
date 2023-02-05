import base64
import hashlib
import hmac
import time

import requests
import datetime as dt


def start_renewals():
    timestamp = str(round(time.time() * 1000))
    path = "/wynk/s2s/v1/scheduler/start/renewals"
    host = "http://payment-staging.wcf.internal"
    url = host + path
    response = requests.get(url, headers=generate_headers(path, timestamp))
    print(f'status_code = {response.status_code}')
    print(f'response_body = {response.json()}')
    response.raise_for_status()


def start_se_renewals():
    timestamp = str(round(time.time() * 1000))
    path = "/wynk/s2s/v1/scheduler/start/seRenewal"
    host = "http://payment-staging.wcf.internal"
    url = host + path
    response = requests.get(url, headers=generate_headers(path, timestamp))
    print(f'status_code = {response.status_code}')
    print(f'response_body = {response.json()}')
    response.raise_for_status()


def generate_headers(path, timestamp):
    message = f'GET{path}{timestamp}'
    print(message)
    key = bytes("e9a467c8a8ce787a0787901b137ef70b", 'utf-8')
    message = bytes(message, 'utf-8')
    hashed = base64.b64encode(hmac.new(key, message, hashlib.sha1).digest())
    print(hashed)
    return {
        "x-wynk-timestamp": timestamp,
        "x-wynk-atkn": "340b596b576da4fc73b21f852d1390b2:" + str(hashed.decode('utf-8'))
    }


def handle_event(event, context):
    print(event)
    if 'Records' in event:
        s3 = event['Records'][0]['s3']
        bucket = s3['bucket']['name']
        key = s3['object']['key']
        if bucket == 'wcf-logs' and "ht_Wynk_9038" in key:
            date_str = key.split('ht_Wynk_9038_')[1].split('.csv.gz')[0]
            date_time_obj = dt.datetime.strptime(date_str, '%d_%b_%Y')
            day_before_yesterday = dt.datetime.today() - dt.timedelta(days = 2)
            if day_before_yesterday < date_time_obj:
                print("Invoking SE Renewals API")
                start_se_renewals()
            else:
                print(f"Not invoking due to old file: {bucket}/{key}")
        else:
            print("Not executing any api since file / bucket did not matched")
    elif 'detail-type' in event and event['detail-type'] == 'Scheduled Event':
        print("Invoking Renewals API")
        start_renewals()

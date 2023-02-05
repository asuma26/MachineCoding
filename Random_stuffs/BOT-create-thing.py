#!/usr/bin/env python3

import grequests
import urllib3
import time


urllib3.disable_warnings(urllib3.exceptions.InsecureRequestWarning)

CLIENT_ID = 'BotAdminUser'
FILE_NAME = 'asc0003-split_0004'
ENV = 'prod'
THREADS_COUNT = 10

INPUT_PATH = f'asc-data-correction-0003/files/{FILE_NAME}'
URL = f'https://bag-diagnostics.kmc-default.us-west-2.{ENV}.connectedtrips.expedia.com/users/create/things?clientId={CLIENT_ID}'

def send_data(expuserId, siteid, tripId):
     return grequests.post(
        URL,
        json={
            "source": {
                "name": "script",
                "reason": "duplicateThingKeyFix"
            },
            "userContext": {
                "expUserId": expuserId,
                "siteId": siteid
            },
            "tripId": tripId
        },
        verify=False
    )


def process_data():
    file = open(INPUT_PATH, "r")
    lines = file.readlines()[1:]
    total = len(lines)
    cont = 0
    calls = []
    for line in lines:
        cont+=1
        print(f'{cont} of {total}')
        elements = line.split(",")
        tripId = elements[0]
        expuserId = elements[1]
        siteid = elements[2]
        print("tripId = " + tripId, "expUserId = " + expuserId, "siteId="+  siteid )
        call = send_data(expuserId, siteid, tripId)
        calls.append(call)
        if(len(calls) == THREADS_COUNT or cont == total):
            grequests.map(calls)
            calls = []
            time.sleep(1)

process_data()

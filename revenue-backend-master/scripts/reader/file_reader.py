import codecs
import csv
import glob
import os
from os.path import expanduser

import boto3

home = expanduser("~")


def read_csv(path):
    if "s3://" in path:
        s3 = boto3.client('s3')
        bucket_key = path.split("s3://")[1].split('/', 1)
        bucket = bucket_key[0]
        key = bucket_key[1]
        obj = s3.get_object(Bucket=bucket, Key=key)
        for line in csv.DictReader(codecs.iterdecode(obj['Body'].iter_lines(), 'utf-8')):
            yield dict(line)
    elif os.path.isfile(path):
        with open(path, mode='r') as f:
            for line in csv.DictReader(f):
                yield dict(line)
    elif os.path.isdir(path):
        for file in glob.glob(f'{path}/*'):
            print(f'Working on file : {file}')
            with open(file, mode='r') as f:
                for line in csv.DictReader(f):
                    yield dict(line)


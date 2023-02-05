import argparse
from os.path import expanduser

from mongo.mongo_connector import MongoConnector
from reader.file_reader import read_csv

home = expanduser("~")


class UserBaseUpload:

    def __init__(self, db):
        self.mongo_dao = MongoConnector(db)

    def insert(self, path, collection_name):
        for doc in read_csv(path):
            self.mongo_dao.insert_one(collection_name, doc)

    def delete(self, path, collection_name):
        for doc in read_csv(path):
            self.mongo_dao.delete_one(collection_name, doc)


def execute():
    arg_parser = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    arg_parser.add_argument('command', help='Command')
    arg_parser.add_argument('--file_path', help='Source file path')
    arg_parser.add_argument('--db', default='test', help='pass db')
    arg_parser.add_argument('--col', default='test', help='pass collection')
    arg_parser.add_argument('--op', default='insert', help='Operation like delete / insert')
    args = arg_parser.parse_args()
    path = args.file_path
    collection = args.col
    db = args.db
    op = args.op
    ubu = UserBaseUpload(db)
    if op == 'insert':
        ubu.insert(path, collection)
    elif op == 'delete':
        ubu.delete(path, collection)
    else:
        print("NO OPERATION PERFORMED")
    print(f"Completed {op} for file at: {path} updated in : {db}.{collection}")

# local_file_path = f'{home}/Downloads/msisdn.txt'
# execute(None)

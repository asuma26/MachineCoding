from collections import abc
from time import time

from pymongo import MongoClient

from settings import fetch_properties


class MongoConnector:

    def __init__(self, props, database='filterdb'):
        if not props:
            props = fetch_properties('mongo')
        print(f"Creating mongo client with props: {props}")
        self.client = MongoClient(**props)
        self.db = self.client.get_database(database)

    def get_collection(self, collection_name):
        if not collection_name:
            raise ValueError("collection_name cannot be null")
        return self.db.get_collection(collection_name)

    def find(self, collection_name, query=None, projection=None):
        if query is None:
            query = {}
        col = self.get_collection(collection_name)
        return col.find(query, projection=projection)

    def insert_many(self, collection_name, documents, ordered=False):
        col = self.get_collection(collection_name)
        if not isinstance(documents, abc.Iterable) or not documents:
            raise TypeError("documents must be a non-empty list")
        ids = []
        for doc in documents:
            if '_id' in doc:
                ids.append(doc['_id'])
        delete_query = {'_id': {'$in': ids}}
        # existing_docs = self.find(collection_name, {'_id':{'$in':ids}})
        # TODO: For now removing doc. We might need to implement an upsert logic
        col.delete_many(delete_query)
        col.insert_many(documents, ordered=ordered)

    def insert_one(self, collection_name, doc):
        if doc:
            col = self.get_collection(collection_name)
            if '_id' in doc and not col.find_one(doc['_id']):
                doc['ca'] = int(time())
                col.insert_one(doc)

    def delete_one(self, collection_name, doc):
        if '_id' in doc:
            col = self.get_collection(collection_name)
            col.delete_one({'_id': doc['_id']})
        else:
            print(f"Ignoring doc: {doc}")
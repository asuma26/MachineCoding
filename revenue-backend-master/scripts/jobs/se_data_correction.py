import argparse
from datetime import datetime

from mongo.mongo_connector import MongoConnector
from reader.file_reader import read_csv
from utils.utils import days_to_millis
from wynk_cassandra.cassandra_connector import CassandraConnector
from wynk_redis.redis_connector import RedisConnector

mongo = {
    "host": ["10.40.12.162", "10.40.12.216", "10.40.12.45"],
    "username": "capi",
    "password": "g{6Jm[n43}ny;y:h",
    "authSource": "admin"
}
mongo_db = MongoConnector(mongo, 'subscription')
cassandra_contact_points = ['10.40.12.123', '10.40.13.220']
cassandra_connection = CassandraConnector(cassandra_contact_points, 'userdata')
redis_cluster_node = [{"host": "wcf-redis.8hya7e.clustercfg.aps1.cache.amazonaws.com", "port": "6379"}]
redis_db = RedisConnector(redis_cluster_node)
plan_duration_mapping = {1101: 30, 1102: 30, 1105: 30, 1205: 365, 11002: 30, 11001: 30, 1106: 30}
service = 'music'
yesterday = int(datetime.today().timestamp() * 1000) - 86400000


def correct_se_data(uid, plan_id, start_timestamp, transaction_id):
    entries = cassandra_connection.select(f"select * from user_plan_details where service='{service}' "
                                          f"and uid='{uid}' and plan_id={plan_id}")
    for entry in entries:
        plan_id = entry.plan_id
        if plan_id == 1103:
            update_plan_stmt = f"Update user_plan_details Set end_date={yesterday}, reference_id='{transaction_id}'" \
                               f" where uid='{uid}' and service='{service}' and plan_id = {plan_id}"
            cassandra_connection.insert(update_plan_stmt)
            plan_id = 1106
        days_in_millis = days_to_millis(plan_duration_mapping[int(plan_id)])
        end_date = int(days_in_millis + start_timestamp)
        update_plan_stmt = f"Update user_plan_details Set end_date={end_date} where uid='{uid}'" \
                           f" and service='{service}' and plan_id = {plan_id}"
        cassandra_connection.insert(update_plan_stmt)


def correct_user_plan_details(uid):
    entries = cassandra_connection.select(f"select * from user_plan_details where service='{service}' "
                                          f"and uid='{uid}' and plan_id=1106")
    for entry in entries:
        plan_id = entry.plan_id
        offer_id = entry.offer_id
        end_time = entry.end_date
        if not offer_id or not entry.auto_renew:
            days_in_millis = days_to_millis(plan_duration_mapping[int(plan_id)])
            start_date = int(end_time.timestamp() * 1000 - days_in_millis)
            update_plan_stmt = f"Update user_plan_details Set start_date={start_date},plan_count=1," \
                               f"reference_id='script', offer_id=11001, auto_renew={True}" \
                               f" where uid='{uid}' and service='{service}' and plan_id = {plan_id}"
            cassandra_connection.insert(update_plan_stmt)


def correct_data(path):
    for doc in read_csv(path):
        # try:
        # correct_se_data(doc['uid'], doc['plan_id'], get_timestamp_from_date(doc['dt'], date_format='%Y-%m-%d'), doc['transaction_id'])
        correct_user_plan_details(doc['uid'])
        # except Exception as ex:
        #     print(f"Error occurred for doc: {doc}, {ex}")


def execute():
    print("Starting base correction")
    arg_parser = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    arg_parser.add_argument('command', help='Command')
    arg_parser.add_argument('--file_path', help='Source file path')
    args = arg_parser.parse_args()
    path = args.file_path
    correct_data(path)
    print("Completed execution for correction script")


if __name__ == '__main__':
    execute()

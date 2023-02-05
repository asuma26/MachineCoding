from datetime import datetime


def get_timestamp_from_date(date_str, date_format='dd-MM-YYYY'):
    return datetime.strptime(date_str, date_format).timestamp() * 1000


def days_to_millis(days):
    return days * 24 * 60 * 60 * 1000

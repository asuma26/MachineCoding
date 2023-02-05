import os

PROPS = {
    "DEFAULT": {
        "mongo": {
            "host": ["10.80.0.128", "10.80.0.240"],
            "username": "capi",
            "password": "S>Y9hv%:#Z",
            "authSource": "admin"
        }
    },

    "STAGING": {
        "mongo": {
            "host": ["10.80.0.128", "10.80.0.240"],
            "username": "capi",
            "password": "S>Y9hv%:#Z",
            "authSource": "admin"
        }
    },

    "PROD": {
        "mongo": {
            "host": ["10.40.12.139", "10.40.12.140", "10.40.12.133"]
        }
    },

    "PROD_NEW": {
        "mongo": {
            "host": ["10.40.12.162", "10.40.12.216", "10.40.12.45"],
            "username": "capi",
            "password": "g{6Jm[n43}ny;y:h",
            "authSource": "admin"
        }
    }
}


def fetch_properties(var):
    """Method to fetch properties"""
    env = os.getenv("ENV", 'DEFAULT')
    return PROPS.get(env.upper()).get(var)

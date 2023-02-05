from rediscluster import RedisCluster


class RedisConnector:

    def __init__(self, startup_nodes, ty='cluster'):
        if isinstance(startup_nodes, str):
            startup_nodes = startup_nodes.split(',')
        if ty:
            if ty.lower() == 'cluster':
                self.rc = RedisCluster(startup_nodes=startup_nodes, decode_responses=True, skip_full_coverage_check=True)

    def get(self, key):
        return self.rc.get(key)

    def delete(self, key):
        return  self.rc.delete(key)
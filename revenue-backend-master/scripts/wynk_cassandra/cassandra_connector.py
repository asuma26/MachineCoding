from cassandra.cluster import Cluster


class CassandraConnector:

    def __init__(self, contact_points, db):
        if isinstance(contact_points, str):
            contact_points = contact_points.split(',')
        self.cluster = Cluster(contact_points)
        self.session = self.cluster.connect(db)

    def select(self, statement):
        for row in self.session.execute(statement):
            yield row

    def insert(self, statement):
        self.session.execute(statement)

package in.wynk.thanks.dao;

import com.datastax.driver.core.ConsistencyLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.data.cassandra.core.InsertOptions;
import org.springframework.stereotype.Repository;

import java.util.List;

@Deprecated
@Repository
public class CassandraDao<T> {

    private static final Logger logger = LoggerFactory.getLogger(CassandraDao.class);

    @Autowired
    @Qualifier("cassandraOperations")
    private CassandraOperations cassandraTemplate;

    private ConsistencyLevel consistencyLevelWrite;

    private ConsistencyLevel consistencyLevelRead;

    @Value("${cassandra.consistency.level.read}")
    public void setConsistencyLevelRead(String _consistencyLevel) {
        consistencyLevelRead = ConsistencyLevel.valueOf(_consistencyLevel);
        logger.info("CASSANDRA RUNNING ON Read ConsistencyLevel {}", consistencyLevelRead);
    }

    @Value("${cassandra.consistency.level.write}")
    public void setConsistencyLevelWrite(String _consistencyLevel) {
        consistencyLevelWrite = ConsistencyLevel.valueOf(_consistencyLevel);
        logger.info("CASSANDRA RUNNING ON Write ConsistencyLevel {}", consistencyLevelWrite);
    }

    public T insert(T object) {
        InsertOptions writeOptions = InsertOptions.builder().consistencyLevel(consistencyLevelWrite).build();
        return cassandraTemplate.insert(object, writeOptions).getEntity();

    }

    public T insert(T object, int ttl) {
        InsertOptions writeOptions = InsertOptions.builder().consistencyLevel(consistencyLevelWrite).ttl(ttl).build();
        return cassandraTemplate.insert(object, writeOptions).getEntity();

    }

    public List<T> insert(List<T> list) {
        InsertOptions writeOptions = InsertOptions.builder().consistencyLevel(consistencyLevelWrite).build();
        return cassandraTemplate.insert(list, writeOptions).getEntity();

    }

    public List<T> insert(List<T> list, int ttl) {
        InsertOptions writeOptions = InsertOptions.builder().consistencyLevel(consistencyLevelWrite).ttl(ttl).build();
        return cassandraTemplate.insert(list, writeOptions).getEntity();

    }

}

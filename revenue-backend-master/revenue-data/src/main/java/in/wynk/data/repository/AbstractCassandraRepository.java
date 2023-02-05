package in.wynk.data.repository;

import com.datastax.driver.core.ConsistencyLevel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.data.cassandra.core.InsertOptions;
import org.springframework.data.cassandra.core.cql.QueryOptions;
import org.springframework.data.cassandra.core.query.CriteriaDefinition;
import org.springframework.data.cassandra.core.query.Query;

import java.util.List;

@Slf4j
public abstract class AbstractCassandraRepository<T> {

    private final CassandraOperations cassandraTemplate;

    public AbstractCassandraRepository(CassandraOperations cassandraTemplate) {
        this.cassandraTemplate = cassandraTemplate;
    }

    public abstract Class<T> getEntityClass();

    public abstract ConsistencyLevel getReadConsistencyLevel();

    public abstract ConsistencyLevel getWriteConsistencyLevel();

    public T insert(T object) {
        InsertOptions writeOptions = InsertOptions.builder().consistencyLevel(getWriteConsistencyLevel()).build();
        log.debug("Inserting into table: {} data: {} at consistency level: {}", getEntityClass(), object, getWriteConsistencyLevel());
        return cassandraTemplate.insert(object, writeOptions).getEntity();

    }

    public T insert(T object, int ttl) {
        InsertOptions writeOptions = InsertOptions.builder().consistencyLevel(getWriteConsistencyLevel()).ttl(ttl).build();
        log.debug("Inserting into table: {} data: {} at consistency level: {} with ttl: {}", getEntityClass(), object, getWriteConsistencyLevel(), ttl);
        return cassandraTemplate.insert(object, writeOptions).getEntity();

    }

    public List<T> insert(List<T> list) {
        InsertOptions writeOptions = InsertOptions.builder().consistencyLevel(getWriteConsistencyLevel()).build();
        return cassandraTemplate.insert(list, writeOptions).getEntity();
    }

    public List<T> insert(List<T> list, int ttl) {
        InsertOptions writeOptions = InsertOptions.builder().consistencyLevel(getWriteConsistencyLevel()).ttl(ttl).build();
        return cassandraTemplate.insert(list, writeOptions).getEntity();
    }

    public List<T> select(CriteriaDefinition... criteriaDefinitions) {
        QueryOptions options = QueryOptions.builder().consistencyLevel(getReadConsistencyLevel()).build();
        Query query = Query.query(criteriaDefinitions).queryOptions(options);
        log.debug("Query: {} on table: {} at consistency level: {}", query, getEntityClass(), getReadConsistencyLevel());
        return cassandraTemplate.select(query, getEntityClass());
    }

    public T selectOne(CriteriaDefinition... criteriaDefinitions) {
        List<T> result = select(criteriaDefinitions);
        return result.isEmpty() ? null : result.get(0);
    }

}

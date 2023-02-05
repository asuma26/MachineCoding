package in.wynk.ut.base.batch.writer;

import com.datastax.driver.core.ConsistencyLevel;
import in.wynk.ut.base.constant.AppConstant;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.cassandra.core.ReactiveCassandraTemplate;
import org.springframework.data.cassandra.core.cql.WriteOptions;

import java.time.Duration;
import java.util.List;

public class UserTargetCassandraBatchItemWriter<T> implements ItemWriter<T> {

    protected static final Log logger = LogFactory.getLog(UserTargetCassandraBatchItemWriter.class);

    @Value("${app.batch.utJob.defaultTTL}")
    private long DEFAULT_TTL;

    @Value("${app.batch.utJob.consistencyLevel}")
    private String CONSISTENCY_LEVEL;

    private WriteOptions options;

    @Autowired
    private ReactiveCassandraTemplate reactiveCassandraTemplate;

    public UserTargetCassandraBatchItemWriter() {  }

    @BeforeStep
    public void buildWriteOptionOnNewStep(StepExecution stepExecution) throws Exception {
           Long ttl = stepExecution.getJobParameters().getLong(AppConstant.TTL);
           options = WriteOptions
                                .builder()
                                .consistencyLevel(ConsistencyLevel.valueOf(CONSISTENCY_LEVEL))
                                .ttl(Duration.ofDays(ttl != null ? ttl : DEFAULT_TTL))
                                .build();
    }

    @Override
    public void write(List<? extends T> items) {
        if(!items.isEmpty()) {
            logger.info("items batch is begin to process in batch of " + items.size());
            reactiveCassandraTemplate.batchOps().insert(items , options).execute().subscribe();
            logger.info("items batch is processed in batch of "+ items.size());
        }
        else
            logger.info("items list is null ... " + " by " + Thread.currentThread().getName() + "-" + Thread.currentThread().getId());
    }

}

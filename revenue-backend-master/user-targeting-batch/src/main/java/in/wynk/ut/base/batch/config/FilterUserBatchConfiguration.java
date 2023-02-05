package in.wynk.ut.base.batch.config;

import in.wynk.ut.base.batch.constant.BatchConstant;
import in.wynk.ut.base.batch.mapper.UserTargetFieldSetMapper;
import in.wynk.ut.base.batch.writer.UserTargetCassandraBatchItemWriter;
import in.wynk.ut.base.model.UserTarget;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

@Configuration
@EnableBatchProcessing
public class FilterUserBatchConfiguration {

    @Value("${app.batch.utJob.launcher.concurrencyLimit}")
    private int concurrencyLimit;

    @Value("${app.batch.utJob.chunks}")
    private int chunks;

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private JobRepository jobRepository;

    @Bean
    public JobLauncher asyncJobLauncher() {
        final SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
        jobLauncher.setJobRepository(jobRepository);
        final SimpleAsyncTaskExecutor simpleAsyncTaskExecutor = new SimpleAsyncTaskExecutor();
        simpleAsyncTaskExecutor.setConcurrencyLimit(concurrencyLimit);
        jobLauncher.setTaskExecutor(simpleAsyncTaskExecutor);
        return jobLauncher;
    }

    @Bean
    public Job userTargetJob(@Qualifier("filterUserBatch") Step userTargetStep) {
        return jobBuilderFactory
                .get(BatchConstant.USER_TARGET_JOB)
                .incrementer(new RunIdIncrementer())
                .flow(userTargetStep)
                .end()
                .build();
    }

    @Bean
    public Step userTargetStep(@Qualifier("filterUserReader") ItemReader<UserTarget> utBaseReader) {
        return stepBuilderFactory
                .get(BatchConstant.USER_TARGET_STEP_ONE)
                .<UserTarget, UserTarget>chunk(chunks)
                .reader(utBaseReader)
                .writer(userTargetItemWriter())
                .build();

    }

    @Bean
    @StepScope
    public FlatFileItemReader<UserTarget> utBaseReader(@Value("#{jobParameters[s3Path]}") String s3Path,
                                                       @Value("#{jobParameters[adid]}") String adid,
                                                       @Value("#{jobParameters[targeted]}") Boolean targeted) {
        return new FlatFileItemReaderBuilder<UserTarget>()
                .resource(resourceLoader.getResource(s3Path))
                .lineTokenizer(new DelimitedLineTokenizer(){{
                    setNames("uid");
                    setIncludedFields(0);
                }})
                .fieldSetMapper(UserTargetFieldSetMapper.instantiate(adid, targeted))
                .name(BatchConstant.USER_TARGET_READER_NAME)
                .build();
    }

    @Bean
    public ItemWriter<UserTarget> userTargetItemWriter() {
        return new UserTargetCassandraBatchItemWriter<UserTarget>();
    }

}

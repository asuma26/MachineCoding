package in.wynk.ut.base.service.impl;

import in.wynk.ut.base.batch.constant.BatchConstant;
import in.wynk.ut.base.constant.AppConstant;
import in.wynk.ut.base.model.request.BaseUploadRequest;
import in.wynk.ut.base.model.response.BatchJobStatus;
import in.wynk.ut.base.service.BatchJobService;
import in.wynk.ut.base.service.UserTargetService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service("utService")
public class UserTargetServiceImpl implements UserTargetService {

    protected static final Log logger = LogFactory.getLog(UserTargetServiceImpl.class);

    @Autowired
    private Job userTargetJob;

    @Autowired
    private JobLauncher asyncJobLauncher;

    @Autowired
    private BatchJobService batchJobService;

    @Autowired
    private ReactiveCassandraRepository userTargetRepo;

    @Override
    public Mono<BatchJobStatus> process(BaseUploadRequest request) throws Exception {
        logger.info(request);
        JobExecution execution = asyncJobLauncher.run(userTargetJob, buildJobParams(request));
        return batchJobService.getJobStatus(execution.getJobId());
    }

    private JobParameters buildJobParams(BaseUploadRequest request) {
        Map<String, JobParameter> jobParams = new HashMap<>();
        jobParams.put(AppConstant.S3PATH, new JobParameter(BatchConstant.DEFAULT_S3_BUCKET_PATH + request.getFileName()));
        jobParams.put(AppConstant.TTL, new JobParameter(request.getTtl()));
        jobParams.put(AppConstant.AID, new JobParameter(request.getAdid()));
        jobParams.put(AppConstant.TARGETED, new JobParameter(request.isTargeted().toString()));
        jobParams.put(AppConstant.INITIATOR, new JobParameter(request.getInitiatedBy()));
        return new JobParameters(jobParams);
    }

}

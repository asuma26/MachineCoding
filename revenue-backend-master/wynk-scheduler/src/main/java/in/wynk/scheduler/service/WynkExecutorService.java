package in.wynk.scheduler.service;

import in.wynk.common.context.WynkApplicationContext;
import in.wynk.common.utils.ChecksumUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static in.wynk.http.constant.HttpConstant.INTERNAL_SERVICE_REST_TEMPLATE;

@Service
public class WynkExecutorService {

    @Value("${payment.renewal.endpoint}")
    private String PAYMENT_RENEWAL_ENDPOINT;
    @Value("${payment.se.renewal.endpoint}")
    private String PAYMENT_SE_RENEWAL_ENDPOINT;

    private final RestTemplate restTemplate;
    private final WynkApplicationContext myApplicationContext;

    public WynkExecutorService(@Qualifier(INTERNAL_SERVICE_REST_TEMPLATE) RestTemplate restTemplate, WynkApplicationContext myApplicationContext) {
        this.restTemplate = restTemplate;
        this.myApplicationContext = myApplicationContext;
    }

    public void executeJob(String job){
        switch (StringUtils.upperCase(job)) {
            case "PAYMENT_RENEWAL":
                executePaymentRenewal();
                break;
            case "SE_PAYMENT_RENEWAL":
                executeSePaymentRenewal();
                break;
            default:
                throw new IllegalArgumentException("Invalid JOB ID " + job);

        }
    }

    private void executeSePaymentRenewal() {
        RequestEntity<String> requestEntity = ChecksumUtils.buildEntityWithAuthHeaders(PAYMENT_SE_RENEWAL_ENDPOINT, myApplicationContext.getClientId(), myApplicationContext.getClientSecret(), null, HttpMethod.GET);
        ResponseEntity<String> response = restTemplate.exchange(requestEntity, String.class);
        System.out.println(response);
    }

    private void executePaymentRenewal() {
        RequestEntity<String> requestEntity = ChecksumUtils.buildEntityWithAuthHeaders(PAYMENT_RENEWAL_ENDPOINT, myApplicationContext.getClientId(), myApplicationContext.getClientSecret(), null, HttpMethod.GET);
        ResponseEntity<String> response = restTemplate.exchange(requestEntity, String.class);
        System.out.println(response);
    }
}

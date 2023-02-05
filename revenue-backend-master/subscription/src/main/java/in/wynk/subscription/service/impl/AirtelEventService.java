package in.wynk.subscription.service.impl;

import com.github.annotation.analytic.core.service.AnalyticService;
import in.wynk.advice.TimeIt;
import in.wynk.common.utils.BCEncryptor;
import in.wynk.common.utils.MsisdnUtils;
import in.wynk.exception.WynkRuntimeException;
import in.wynk.subscription.core.constants.SubscriptionErrorType;
import in.wynk.subscription.core.dao.repository.filterUsers.IFilterDbDao;
import in.wynk.subscription.dto.request.AirtelEventRequest;
import in.wynk.subscription.service.IAirtelEventService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import static in.wynk.common.constant.BaseConstants.MSISDN;
import static in.wynk.logging.BaseLoggingMarkers.EXTERNAL_SERVICE_FAILURE;

@Service
@Slf4j
public class AirtelEventService implements IAirtelEventService {

    @Value("${service.capi.api.endpoint.airtelEvent}")
    private String capiAddUserEndpoint;
    private final RestTemplate capiServiceTemplate;
    private final IFilterDbDao iFilterDbDao;
    private final String airtelMsisdnEncryptionToken;

    public AirtelEventService(@Qualifier("internalServiceTemplate") RestTemplate capiServiceTemplate, IFilterDbDao iFilterDbDao, @Value("${airtel.encryption.token}") String airtelMsisdnEncryptionToken) {
        this.capiServiceTemplate = capiServiceTemplate;
        this.iFilterDbDao = iFilterDbDao;
        this.airtelMsisdnEncryptionToken = airtelMsisdnEncryptionToken;
    }

    @Override
    public void saveMsisdnToCollection(AirtelEventRequest airtelEventRequest) {
        String si = BCEncryptor.decrypt(airtelEventRequest.getSi(), airtelMsisdnEncryptionToken);
        String msisdn = MsisdnUtils.normalizePhoneNumber(si);
        AnalyticService.update(MSISDN, msisdn);
        if (StringUtils.isBlank(msisdn)) {
            throw new WynkRuntimeException(SubscriptionErrorType.SUB003);
        }
        airtelEventRequest.setSi(msisdn);
        iFilterDbDao.upsertFilterUserDetails(msisdn, airtelEventRequest.getCol());
//        sendToCapi(airtelEventRequest);
    }

    @TimeIt
    private void sendToCapi(AirtelEventRequest airtelEventRequest) {
        HttpEntity<AirtelEventRequest> httpEntity = new HttpEntity<>(airtelEventRequest);
        try {
            capiServiceTemplate.exchange(capiAddUserEndpoint, HttpMethod.POST, httpEntity, String.class);
        } catch (HttpStatusCodeException exception) {
            log.error(EXTERNAL_SERVICE_FAILURE, exception.getResponseBodyAsString(), exception);
            throw new WynkRuntimeException(SubscriptionErrorType.SUB004);
        }
    }

}

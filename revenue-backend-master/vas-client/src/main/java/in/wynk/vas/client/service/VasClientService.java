package in.wynk.vas.client.service;

import com.github.annotation.analytic.core.service.AnalyticService;
import in.wynk.advice.TimeIt;
import in.wynk.logging.BaseLoggingMarkers;
import in.wynk.vas.client.constant.HeaderConstants;
import in.wynk.vas.client.constant.VasConstants;
import in.wynk.vas.client.dto.DthUserInfo;
import in.wynk.vas.client.dto.ImsiUserInfo;
import in.wynk.vas.client.dto.MsisdnOperatorDetails;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

@Service
public class VasClientService {

    private static final Logger logger = LoggerFactory.getLogger(VasClientService.class);
    public static final String BASIC_AUTH = "Basic dTEwMTpwMTAx";

    @Value("${vas.endpoint}")
    private String vasEndpoint;

    @Value("${all.operator.details.path}")
    private String allOperatorDetailsPath;

    @Value("${imsi.user.info.path}")
    private String imsiUserInfoPath;

    @Value("${dth.user.info.path}")
    private String dthUserInfoPath;

    @Qualifier("vasRestTemplate")
    @Autowired
    private RestTemplate restTemplate;

    @TimeIt
    public MsisdnOperatorDetails allOperatorDetails(String msisdn) {
        try {
            URI uri = new URIBuilder(vasEndpoint + allOperatorDetailsPath).addParameter(VasConstants.MSISDN, msisdn).build();
            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.set(HttpHeaders.AUTHORIZATION, HeaderConstants.VAS_BASIC_AUTH);
            RequestEntity<String> requestEntity = new RequestEntity<>(requestHeaders, HttpMethod.GET, uri);
            ResponseEntity<MsisdnOperatorDetails> responseEntity = restTemplate.exchange(requestEntity, MsisdnOperatorDetails.class);
            AnalyticService.update("vasResponseCode", responseEntity.getStatusCodeValue());
            return responseEntity.getBody();
        } catch (HttpStatusCodeException ex) {
            AnalyticService.update("vasResponseCode", ex.getRawStatusCode());
            AnalyticService.update("vasErrorResponse", ex.getResponseBodyAsString());
            logger.error(BaseLoggingMarkers.VAS_ERROR, ex.getResponseBodyAsString(), ex);
        } catch (Exception ex) {
            logger.error(BaseLoggingMarkers.VAS_ERROR, ex.getMessage(), ex);
        }
        return MsisdnOperatorDetails.defaultInstance();
    }

    @TimeIt
    public ImsiUserInfo imsiUserInfo(String imsi) {
        try {
            URI uri = new URIBuilder(vasEndpoint + imsiUserInfoPath).addParameter("imsi", imsi).build();
            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.set(HttpHeaders.AUTHORIZATION, BASIC_AUTH);
            RequestEntity<String> requestEntity = new RequestEntity<>(requestHeaders, HttpMethod.GET, uri);
            ResponseEntity<ImsiUserInfo> responseEntity = restTemplate.exchange(requestEntity, ImsiUserInfo.class);
            AnalyticService.update("vasResponseCode", responseEntity.getStatusCodeValue());
            return responseEntity.getBody();
        } catch (HttpStatusCodeException ex) {
            AnalyticService.update("vasErrorResponseCode", ex.getRawStatusCode());
            AnalyticService.update("vasErrorResponse", ex.getResponseBodyAsString());
        } catch (Exception ex) {
            logger.error(BaseLoggingMarkers.VAS_ERROR, ex.getMessage(), ex);
        }
        return null;
    }

    @TimeIt
    public DthUserInfo dthUserInfo(String userDthId, String androidId) {
        try {
            URI uri = new URIBuilder(vasEndpoint + dthUserInfoPath).addParameter("andID", androidId).addParameter("dthCustId", userDthId).build();
            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.set(HttpHeaders.AUTHORIZATION, BASIC_AUTH);
            requestHeaders.setContentType(MediaType.APPLICATION_JSON);
            requestHeaders.setAcceptCharset(Collections.singletonList(StandardCharsets.UTF_8));
            RequestEntity<String> requestEntity = new RequestEntity<>(requestHeaders, HttpMethod.GET, uri);
            ResponseEntity<DthUserInfo> responseEntity = restTemplate.exchange(requestEntity, DthUserInfo.class);
            AnalyticService.update("vasResponseCode", responseEntity.getStatusCodeValue());
            return responseEntity.getBody();
        } catch (HttpStatusCodeException ex) {
            AnalyticService.update("vasResponseCode", ex.getRawStatusCode());
            AnalyticService.update("vasErrorResponse", ex.getResponseBodyAsString());
        } catch (Exception ex) {
            logger.error(BaseLoggingMarkers.VAS_ERROR, ex.getMessage(), ex);
        }
        return null;
    }
}

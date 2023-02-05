package in.wynk.utils.service.brandChannel.impl;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import in.wynk.exception.WynkRuntimeException;
import in.wynk.utils.constant.WcfUtilsErrorType;
import in.wynk.utils.request.BrandChannelRequest;
import in.wynk.utils.service.brandChannel.BrandChannelService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static in.wynk.common.constant.BaseConstants.*;
import static in.wynk.exception.WynkErrorType.UT999;
import static in.wynk.utils.constant.WcfUtilsLoggingMarkers.*;

@Slf4j
@Service
public class BrandChannelServiceImpl implements BrandChannelService {

    @Autowired
    private AmazonS3 amazonS3Client;

    @Value("${wcf.s3.bucket}")
    private String bucket;

    @Value("${wcf.brandChannel.hostname}")
    private String hostname;

    @Override
    public String storeBrandChannelS3(BrandChannelRequest brandChannelRequest) {
        String url;
        try {
            String dump = brandChannelRequest.getChannelJsonString();
            if (StringUtils.isEmpty(dump) || StringUtils.isEmpty(brandChannelRequest.getBrandName())) {
                log.error(BRAND_CHANNEL_EMPTY_REQUEST, "Brand Channel Request Information incorrect");
                throw new WynkRuntimeException(WcfUtilsErrorType.WCF019);
            }
            String brandCampaignUrl = brandChannelRequest.getBrandName() + SLASH +
                    UUID.randomUUID().toString();
            String urlSuffix = HASH + SLASH + BRAND_CHANNEL + SLASH + brandCampaignUrl;
            String filePath = brandCampaignUrl + BRAND_CHANNEL_DUMP_PATH_FORMAT;
            log.info("Putting BrandChannel dump on S3");
            putObjectOnAmazonS3(filePath, dump);
            url = hostname + SLASH + urlSuffix;
        } catch (AmazonServiceException ex) {
            log.error(AMAZON_SERVICE_ERROR, "AmazonServiceException " + ex.getErrorMessage());
            throw new WynkRuntimeException(WcfUtilsErrorType.WCF020);
        } catch (SdkClientException e) {
            log.error(SDK_CLIENT_ERROR, "SdkClientException " + e.getMessage());
            throw new WynkRuntimeException(WcfUtilsErrorType.WCF021);
        } catch (Exception ex) {
            log.error(APPLICATION_ERROR, ex.getMessage());
            throw new WynkRuntimeException(UT999);
        }
        return url;
    }

    private void putObjectOnAmazonS3(String filePath, String object) {
        try {
            amazonS3Client.putObject(bucket, filePath, object);
        } catch (Exception ex) {
            log.error(AMAZON_SERVICE_ERROR, "Amazon error occurred " + ex.getMessage());
            throw ex;
        }
    }
}

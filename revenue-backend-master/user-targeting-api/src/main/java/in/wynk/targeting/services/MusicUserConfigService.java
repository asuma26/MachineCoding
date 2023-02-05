package in.wynk.targeting.services;

import in.wynk.advice.TimeIt;
import in.wynk.targeting.core.dao.entity.mongo.music.UserConfig;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;

import static in.wynk.targeting.core.constant.UTLoggingMarkers.MUSIC_CONFIG_ERROR;

@Service
@NoArgsConstructor
public class MusicUserConfigService {

    private static final String X_BSY_UTKN = "x-bsy-utkn";
    private static final Logger logger = LoggerFactory.getLogger(MusicUserConfigService.class);
    @Autowired
    private MusicUserConfigConnector userConfigConnector;
    @Autowired
    private UTCachingService cachingService;
    @Value("${user.config.endpoint}")
    private String userConfigUrl;

//    @Cacheable(cacheName = "USER_CONFIG", cacheKey = "T(java.lang.String).format('%s:%s', #root.methodName, #userId)", l1CacheTtl = 20 * 60, l2CacheTtl = 24 * 60 * 60)
@TimeIt
    public UserConfig getUserConfig(String userId) {
        if(cachingService.isTestUser(userId)){
            return cachingService.getUserContext(userId).getUserconfig();
        }
        logger.info("Request received to fetch user config for uid {}", userId);
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.set(X_BSY_UTKN, new StringBuilder(userId).append(":").append("dawdvqwhjd").toString());
        try {
            return userConfigConnector.getWithAuth(userConfigUrl, requestHeaders, UserConfig.class);
        } catch (HttpStatusCodeException hex) {
            logger.error(MUSIC_CONFIG_ERROR, hex.getResponseBodyAsString(), hex);
        } catch (Exception ex) {
            logger.error(MUSIC_CONFIG_ERROR, ex.getMessage(), ex);
        }
        return null;
    }

}

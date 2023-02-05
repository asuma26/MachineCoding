package in.wynk.targeting.services;

import com.github.annotation.analytic.core.service.AnalyticService;
import in.wynk.exception.WynkErrorType;
import in.wynk.exception.WynkRuntimeException;
import in.wynk.targeting.constant.ContollerType;
import in.wynk.targeting.dto.UserTargetingSegmentsRequest;
import in.wynk.targeting.services.factory.UserTargetingSegments;
import in.wynk.targeting.services.factory.UserTargetingSegmentsFactory;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static in.wynk.common.constant.BaseConstants.UID;

@Service
public class UserTargetingService {

    private final Logger logger = LoggerFactory.getLogger(UserTargetingService.class);

    @Autowired
    private UserTargetingSegmentsFactory factory;

    public Map<String, String> getUserTargetingSegments(UserTargetingSegmentsRequest userTargetingSegmentsRequest, Integer daysBefore, ContollerType contollerType) {
            List<String> clientList = userTargetingSegmentsRequest.getClients();
        if(CollectionUtils.isEmpty(clientList)) {
            logger.error("Null/Empty client in params of getUserTargetingSegments");
            throw new WynkRuntimeException(WynkErrorType.UT002);
        }
        String client = clientList.get(0);
        UserTargetingSegments segments = factory.getUserTargetingSegmentsClient(client);
        if(segments == null) {
            logger.error("Invalid client in params of getUserTargetingSegments : " + client);
            throw new WynkRuntimeException(WynkErrorType.UT002);
        }
        String userId = userTargetingSegmentsRequest.getUid();
        if(ContollerType.S2S.equals(contollerType)) {
            List<String> userList = userTargetingSegmentsRequest.getUserIds();
            if(CollectionUtils.isEmpty(userList)) {
                logger.error("Null/Empty user in params of getUserTargetingSegmentsNew");
                throw new WynkRuntimeException(WynkErrorType.UT012);
            }
            userId = userList.get(0);
        }
        AnalyticService.update(UID, userId);
        return segments.getUserTargetingSegments(userId, userTargetingSegmentsRequest.getDeviceId(), daysBefore);
    }
}

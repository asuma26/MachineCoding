package in.wynk.targeting.listner;

import com.github.annotation.analytic.core.annotations.AnalyseTransaction;
import com.github.annotation.analytic.core.service.AnalyticService;
import in.wynk.auth.constant.AuthConstant;
import in.wynk.auth.event.BearerTokenGenerationSuccessEvent;
import in.wynk.auth.event.BearerTokenGenerationUnsuccessfulEvent;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import static in.wynk.targeting.core.constant.AdConstants.CLIENT_ID;

@Component
public class AuthenticationSuccessListener {

    @EventListener
    @AnalyseTransaction(name = "bearerTokenGenerationEvent")
    public void onApplicationEvent(BearerTokenGenerationSuccessEvent bearerTokenGenerationSuccessEvent) {
        String deviceId = bearerTokenGenerationSuccessEvent.getDeviceId();
        if(!StringUtils.isEmpty(deviceId)) {
            String[] tokens = deviceId.split(AuthConstant.PIPE_DELIMITER);
            AnalyticService.update("deviceId", tokens[0]);
            AnalyticService.update("deviceType", tokens[1]);
            AnalyticService.update("os", tokens[2]);
            AnalyticService.update("osVersion", tokens[3]);
            AnalyticService.update("buildNumber", tokens[4]);
            AnalyticService.update("appVersion", tokens[5]);
        } else {
            AnalyticService.update("did", "empty");
        }
        AnalyticService.update("uid", bearerTokenGenerationSuccessEvent.getPrincipal());
        AnalyticService.update(CLIENT_ID, bearerTokenGenerationSuccessEvent.getClientId());
    }

    @EventListener
    @AnalyseTransaction(name = "authFailureEvent")
    public void unSuccessfulEvent(BearerTokenGenerationUnsuccessfulEvent event) {
        String deviceId = event.getDeviceId();
        if(!StringUtils.isEmpty(deviceId)) {
            String[] tokens = deviceId.split(AuthConstant.PIPE_DELIMITER);
            AnalyticService.update("deviceId", tokens[0]);
            AnalyticService.update("deviceType", tokens[1]);
            AnalyticService.update("os", tokens[2]);
            AnalyticService.update("osVersion", tokens[3]);
            AnalyticService.update("buildNumber", tokens[4]);
            AnalyticService.update("appVersion", tokens[5]);
        }
        AnalyticService.update("uid", event.getPrincipal());
        AnalyticService.update(CLIENT_ID, event.getClientId());
    }
}

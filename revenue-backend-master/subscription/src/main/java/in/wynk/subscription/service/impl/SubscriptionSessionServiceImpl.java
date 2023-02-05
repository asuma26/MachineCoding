package in.wynk.subscription.service.impl;

import com.github.annotation.analytic.core.service.AnalyticService;
import in.wynk.auth.dao.entity.Client;
import in.wynk.auth.service.IClientDetailsService;
import in.wynk.auth.utils.EncryptUtils;
import in.wynk.client.core.dao.entity.ClientDetails;
import in.wynk.client.service.ClientDetailsCachingService;
import in.wynk.common.adapter.SessionDTOAdapter;
import in.wynk.common.dto.SessionDTO;
import in.wynk.common.dto.SessionRequest;
import in.wynk.common.dto.SessionResponse;
import in.wynk.common.enums.AppId;
import in.wynk.common.enums.Os;
import in.wynk.common.enums.WynkService;
import in.wynk.exception.WynkRuntimeException;
import in.wynk.session.context.SessionContextHolder;
import in.wynk.session.dto.Session;
import in.wynk.session.service.ISessionManager;
import in.wynk.subscription.core.constants.SubscriptionErrorType;
import in.wynk.subscription.service.ISubscriptionSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static in.wynk.common.constant.BaseConstants.*;

@Service
public class SubscriptionSessionServiceImpl implements ISubscriptionSessionService {

    private final ISessionManager sessionManager;
    private final ClientDetailsCachingService clientDetailsCachingService;
    @Value("${session.duration:15}")
    private Integer duration;
    @Value("${subscription.webview.purchaseOrManage.url.first}")
    private String SUBSCRIPTION_START_URL;
    @Value("${subscription.webview.purchase.url.second}")
    private String SUBSCRIPTION_PURCHASE_URL;
    @Value("${subscription.webview.manage.url.second}")
    private String SUBSCRIPTION_MANAGE_URL;

    @Autowired
    private IClientDetailsService<Client> clientDetailsService;

    public SubscriptionSessionServiceImpl(ISessionManager sessionManager, ClientDetailsCachingService clientDetailsCachingService) {
        this.sessionManager = sessionManager;
        this.clientDetailsCachingService = clientDetailsCachingService;
    }

    @Override
    public SessionResponse purchasePlans(SessionRequest request) {
        String clientId = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        ClientDetails clientDetails = (ClientDetails) clientDetailsCachingService.getClientById(clientId);
        try {
            AnalyticService.update(CLIENT, clientDetails.getAlias());
            SessionDTO dto = SessionDTOAdapter.generateSessionDTO(request);
            dto.put(CLIENT, clientDetails.getAlias());
            Session<SessionDTO> session = sessionManager.init(dto, duration, TimeUnit.MINUTES);
            StringBuilder host = new StringBuilder(SUBSCRIPTION_START_URL);
            String uri = new StringBuilder(request.getService().getValue())
                    .append(SUBSCRIPTION_PURCHASE_URL)
                    .append(session.getId().toString())
                    .append(SLASH)
                    .append(request.getOs().getValue()).toString();
            host
                    .append(uri)
                    .append(QUESTION_MARK)
                    .append(APP_ID)
                    .append(EQUAL)
                    .append(request.getAppId().getValue());
            if (request.getOs() != null && request.getOs() == Os.IOS) {
                host
                        .append(AND)
                        .append(BUILD_NO)
                        .append(EQUAL)
                        .append(request.getBuildNo());
                if (request.getBuildNo() <= 117) {
                    long tms = System.currentTimeMillis();
                    StringBuilder data = new StringBuilder(SLASH).append(HASH).append(SLASH).append(uri).append(request.getUid()).append(tms);
                    Optional<Client> client = clientDetailsService.getClientDetails("6ba1767f1b15f9748a914bcf765ba8b8");
                    String clientSecret = client.isPresent() ? client.get().getClientSecret() : "";
                    String signature = EncryptUtils.calculateRFC2104HMAC(data.toString(), clientSecret);
                    host.append(AND)
                            .append(UID)
                            .append(EQUAL)
                            .append(request.getUid())
                            .append(AND)
                            .append(DEVICE_ID_SHORT)
                            .append(EQUAL)
                            .append(request.getDeviceId()).append(AND)
                            .append(HASH_STR).append(EQUAL).append(signature).append(AND)
                            .append(TMS).append(EQUAL).append(tms);
                }
            }
            if (Objects.nonNull(request.getTheme())) {
                    host.append(AND).append(THEME).append(EQUAL).append(request.getTheme());
            }
            SessionResponse.SessionData response = SessionResponse.SessionData.builder().redirectUrl(host.toString()).sid(session.getId().toString()).build();
            return SessionResponse.builder().data(response).build();
        } catch (Exception e) {
            throw new WynkRuntimeException(SubscriptionErrorType.SUB997);
        }
    }

    @Override
    public SessionResponse managePlans(SessionRequest request) {
        String clientId = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        ClientDetails clientDetails = (ClientDetails) clientDetailsCachingService.getClientById(clientId);
        try {
            AnalyticService.update(CLIENT, clientDetails.getAlias());
            SessionDTO dto = SessionDTOAdapter.generateSessionDTO(request);
            dto.put(CLIENT, clientDetails.getAlias());
            int bn = request.getBuildNo();
            //TODO temp change which need to be reverted due to RG-986
            if (request.getOs() != null && request.getOs() == Os.IOS &&
                    request.getAppId() == AppId.XTREME && request.getBuildNo() > 117) {
                bn = 117;
                dto.put(BUILD_NO, bn);
            }
            Session<SessionDTO> session = sessionManager.init(dto, duration, TimeUnit.MINUTES);
            StringBuilder host = new StringBuilder(SUBSCRIPTION_START_URL);
            String uri = new StringBuilder(request.getService().getValue())
                    .append(SUBSCRIPTION_MANAGE_URL)
                    .append(session.getId().toString())
                    .append(SLASH)
                    .append(request.getOs().getValue()).toString();
            host.append(uri);
            host.append(QUESTION_MARK)
                    .append(APP_ID)
                    .append(EQUAL)
                    .append(request.getAppId().getValue());
            if (request.getOs() != null && request.getOs() == Os.IOS) {
                host
                        .append(AND)
                        .append(BUILD_NO)
                        .append(EQUAL)
                        .append(bn);
                if (request.getService() == WynkService.AIRTEL_TV && bn <= 117) {
                    long tms = System.currentTimeMillis();
                    StringBuilder data = new StringBuilder(SLASH).append(HASH).append(SLASH).append(uri).append(request.getUid()).append(tms);
                    Optional<Client> client = clientDetailsService.getClientDetails(clientId);
                    String clientSecret = client.isPresent() ? client.get().getClientSecret() : "";
                    String signature = EncryptUtils.calculateRFC2104HMAC(data.toString(), clientSecret);
                    host.append(AND)
                            .append(UID)
                            .append(EQUAL)
                            .append(request.getUid())
                            .append(AND)
                            .append(DEVICE_ID_SHORT)
                            .append(EQUAL)
                            .append(request.getDeviceId()).append(AND)
                            .append(HASH_STR).append(EQUAL).append(signature).append(AND)
                            .append(TMS).append(EQUAL).append(tms);
                }
            }
            if (Objects.nonNull(request.getTheme())) {
                    host.append(AND).append(THEME).append(EQUAL).append(request.getTheme());
            }
            SessionResponse.SessionData response = SessionResponse.SessionData.builder().redirectUrl(host.toString()).sid(session.getId().toString()).build();
            return SessionResponse.builder().data(response).build();
        } catch (Exception e) {
            throw new WynkRuntimeException(SubscriptionErrorType.SUB997);
        }
    }

}

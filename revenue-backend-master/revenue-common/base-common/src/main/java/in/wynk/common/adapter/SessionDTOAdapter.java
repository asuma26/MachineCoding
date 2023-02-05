package in.wynk.common.adapter;

import in.wynk.common.dto.SessionDTO;
import in.wynk.common.dto.SessionRequest;
import in.wynk.exception.WynkErrorType;
import in.wynk.exception.WynkRuntimeException;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static in.wynk.common.constant.BaseConstants.*;

public class SessionDTOAdapter {

    public static SessionDTO generateSessionDTO(SessionRequest request) {
        Map<String, Object> map = new HashMap<>();
        if (StringUtils.isAnyBlank(request.getUid(), request.getMsisdn())) {
            throw new WynkRuntimeException(WynkErrorType.UT001, "Invalid UID of MSISDN");
        }
        map.put(UID, request.getUid());
        map.put(MSISDN, request.getMsisdn());
        if (Objects.nonNull(request.getBuildNo())) {
            map.put(BUILD_NO, request.getBuildNo());
        }
        map.put(OS, request.getOs().getValue());
        if (StringUtils.isNotBlank(request.getDeviceId())) {
            map.put(DEVICE_ID, request.getDeviceId());
        }
        if (StringUtils.isNotBlank(request.getDeviceType())) {
            map.put(DEVICE_TYPE, request.getDeviceType());
        }
        if (StringUtils.isNotBlank(request.getPackGroup())) {
            map.put(PACK_GROUP, request.getPackGroup());
        }
        if (StringUtils.isNotBlank(request.getAppVersion())) {
            map.put(APP_VERSION, request.getAppVersion());
        }
        map.put(APP_ID, request.getAppId().getValue());
        map.put(SERVICE, request.getService().getValue());
        map.put(USER_CREATED_TIMESTAMP, request.getCreatedTimestamp());
        if (StringUtils.isNotBlank(request.getSuccessUrl())) {
            map.put(SUCCESS_WEB_URL, request.getSuccessUrl());
        }
        if (StringUtils.isNotBlank(request.getFailureUrl())) {
            map.put(FAILURE_WEB_URL, request.getFailureUrl());
        }
        if (StringUtils.isNotBlank(request.getItemId())) {
            map.put(ITEM_ID, request.getItemId());
        }
        if (Objects.nonNull(request.getItemPrice())) {
            map.put(POINT_PURCHASE_ITEM_PRICE, request.getItemPrice());
        }
        if (StringUtils.isNotBlank(request.getIntent())) {
            map.put(INGRESS_INTENT, request.getIntent());
        }
        return SessionDTO.builder().sessionPayload(map).build();
    }

}

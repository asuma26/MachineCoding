package in.wynk.targeting.services;

import com.github.annotation.analytic.core.service.AnalyticService;
import in.wynk.spel.IRuleEvaluator;
import in.wynk.spel.builder.DefaultStandardExpressionContextBuilder;
import in.wynk.subscription.common.dto.UserCachePurgeRequest;
import in.wynk.subscription.common.dto.UserProfile;
import in.wynk.targeting.core.constant.AdState;
import in.wynk.targeting.core.constant.AdSubType;
import in.wynk.targeting.core.constant.AdType;
import in.wynk.targeting.core.constant.DfpKey;
import in.wynk.targeting.core.dao.entity.cassandra.AdTargeting;
import in.wynk.targeting.core.dao.entity.mongo.*;
import in.wynk.targeting.core.dao.entity.mongo.music.UserConfig;
import in.wynk.targeting.core.dao.entity.mongo.persona.UserPersona;
import in.wynk.targeting.dto.response.*;
import in.wynk.targeting.dto.response.AdConfigResponse.AdConfigResponseBuilder;
import in.wynk.targeting.evaluation.AdConfigContext;
import in.wynk.targeting.evaluation.AdConfigContext.AdConfigContextBuilder;
import in.wynk.targeting.evaluation.AdConfigEligibility;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static in.wynk.common.constant.BaseConstants.UID;
import static in.wynk.common.enums.WynkService.AIRTEL_TV;
import static in.wynk.common.enums.WynkService.MUSIC;
import static in.wynk.targeting.core.constant.AdConstants.SLOT_CONFIGS;
import static io.netty.util.internal.StringUtil.COMMA;

@Service
public class AdService {

    private static final Logger logger = LoggerFactory.getLogger(AdConfigService.class);

    private final IRuleEvaluator ruleEvaluator;
    private final PersonaService personaService;
    private final AdClientService adClientService;
    private final AdConfigService adConfigService;
    private final MusicUserConfigService musicUserConfigService;
    private final AdRecommendationService adRecommendationService;
    private final AdPropertiesService adPropertiesService;
    private final CampaignTargetingFromCassandraTask campaignTargetingFromCassandraTask;
    private final UserProfileAdService userProfileAdService;
    private final UTCachingService cachingService;

    public AdService(IRuleEvaluator ruleEvaluator, PersonaService personaService, AdClientService adClientService, AdConfigService adConfigService, AdRecommendationService adRecommendationService, AdPropertiesService adPropertiesService, MusicUserConfigService musicUserConfigService, CampaignTargetingFromCassandraTask campaignTargetingFromCassandraTask, UserProfileAdService userProfileAdService, UTCachingService cachingService) {
        this.ruleEvaluator = ruleEvaluator;
        this.personaService = personaService;
        this.adClientService = adClientService;
        this.adConfigService = adConfigService;
        this.adRecommendationService = adRecommendationService;
        this.musicUserConfigService = musicUserConfigService;
        this.adPropertiesService = adPropertiesService;
        this.campaignTargetingFromCassandraTask = campaignTargetingFromCassandraTask;
        this.userProfileAdService = userProfileAdService;
        this.cachingService = cachingService;
    }

    private List<AdConfig> getAdConfigs(String uid, WynkClient client, AdConfigContext adConfigContext) {
        AdClient adClient = adClientService.findByClientId(client);
        if (adClient.isEnabled()) {
            logger.info("AdClient is active for uid {}", uid);
            List<AdConfig> adConfigs = adConfigService.findAllByStateAndByClientIdAndTypeIn(AdState.ACTIVE_STATE, client.getClientId(), adClient.getSupportedType().keySet());
            Stream<AdConfig> adConfigStream = adConfigs.stream().filter(adConfig -> adClient.getSupportedType().get(adConfig.getType()).contains(adConfig.getSubType()));
            logger.debug("AdConfigs are fetched for uid {}", uid);
            AdConfigEligibility adConfigEligibility = AdConfigEligibility.builder().adConfigContext(adConfigContext).build();
            StandardEvaluationContext context = DefaultStandardExpressionContextBuilder.builder().rootObject(adConfigEligibility).build();
            List<AdConfig> filteredAdConfigs = adConfigStream.filter(adConfig -> ruleEvaluator.evaluate(adConfig.getCondition(), () -> context, Boolean.class)).collect(Collectors.toList());
            return filteredAdConfigs;
        }
        return null;
    }

    public AdConfigResponse getAdConfig(String uid, WynkClient client) throws Exception {
        AdConfigContext adConfigContext = getAdConfigContextForUser(uid, client);
        List<AdConfig> filteredAdConfigs = getAdConfigs(uid, client, adConfigContext);
        if (CollectionUtils.isNotEmpty(filteredAdConfigs)) {
            AdConfigResponseBuilder responseBuilder = AdConfigResponse.builder(true);
            Set<String> adIds = filteredAdConfigs.stream().map(AdConfig::getSlotId).collect(Collectors.toSet());
            AnalyticService.update(SLOT_CONFIGS, StringUtils.join(adIds, COMMA));
            filteredAdConfigs.forEach(responseBuilder::populateAdMeta);
            populateAdRecommendations(responseBuilder, adConfigContext);
            if (!client.isExternalPartner())
                addDfpParams(uid, responseBuilder, adConfigContext);
            return responseBuilder.build();
        }
        return AdConfigResponse.defaultDisabledResponse();
    }

    public AdConfigResponse getAdConfigV3(String uid, WynkClient client) throws Exception {
        AdConfigContext adConfigContext = getAdConfigContextForUser(uid, client);
        List<AdConfig> filteredAdConfigs = getAdConfigs(uid, client, adConfigContext);
        if (CollectionUtils.isNotEmpty(filteredAdConfigs)) {
            AdConfigResponseBuilder responseBuilder = AdConfigResponse.builder(true);
            Set<String> adIds = filteredAdConfigs.stream().map(AdConfig::getSlotId).collect(Collectors.toSet());
            AnalyticService.update(SLOT_CONFIGS, StringUtils.join(adIds, COMMA));
            filteredAdConfigs.forEach(responseBuilder::populateAdMetaV3);
            populateAdRecommendations(responseBuilder, adConfigContext);
            if (!client.isExternalPartner())
                addDfpParams(uid, responseBuilder, adConfigContext);
            return responseBuilder.build();
        }
        return AdConfigResponse.defaultDisabledResponse();
    }

    private AdConfigContext getAdConfigContextForUser(final String uid, WynkClient client) throws Exception {
        AdConfigContextBuilder adConfigContextBuilder = AdConfigContext.builder();
        if (!client.isExternalPartner()) {
            if (StringUtils.equalsIgnoreCase(client.getService(), MUSIC.getValue())) {
                createMusicAdConfigContext(uid, adConfigContextBuilder);
            } else if (StringUtils.equalsIgnoreCase(client.getService(), AIRTEL_TV.getValue())) {
                createPersonaAdConfigContext(uid, adConfigContextBuilder);
            }
            createUserPlanProfileContext(uid, client.getService(), adConfigContextBuilder);
        }
        return adConfigContextBuilder.build();
    }

    private void populateAdRecommendations(AdConfigResponseBuilder responseBuilder, AdConfigContext adConfigContext) {
        Map<AdType, Set<String>> typeMap = addBlacklistedCpsForUserProfile(adConfigContext,responseBuilder);

        if (MapUtils.isNotEmpty(responseBuilder.getAudioAds())) {
            Map<String, AudioAdConfigResponse> temp = (Map<String, AudioAdConfigResponse>) responseBuilder.getAudioAds().get(SLOT_CONFIGS);
            AudioAdConfigResponse audioAdConfigResponse = temp.get(AdSubType.PRE_ROLL.name());
            Set<String> blackListedCps = typeMap.get(AdType.AUDIO_AD);
            populateAudioAdRecommendations(audioAdConfigResponse, blackListedCps);
            populateBlackListCps(audioAdConfigResponse,blackListedCps);
            audioAdConfigResponse = temp.get(AdSubType.MID_ROLL.name());
            populateBlackListCps(audioAdConfigResponse,blackListedCps);
            populateAudioAdRecommendations(audioAdConfigResponse, blackListedCps);
            AdProperties adProperties = adPropertiesService.findByAdType(AdType.AUDIO_AD);
            responseBuilder.addAudioProperties(adProperties.getProperties());
        }

        if (MapUtils.isNotEmpty(responseBuilder.getVideoAds())) {
            Map<String, VideoAdConfigResponse> temp = (Map<String, VideoAdConfigResponse>) responseBuilder.getVideoAds().get(SLOT_CONFIGS);
            VideoAdConfigResponse videoAdConfigResponse = temp.get(AdSubType.PRE_ROLL.name());
            Set<String> blackListedCps = typeMap.get(AdType.VIDEO_AD);
            populateVideoAdRecommendations(videoAdConfigResponse, blackListedCps);
            populateBlackListCps(videoAdConfigResponse,blackListedCps);
            videoAdConfigResponse = temp.get(AdSubType.MID_ROLL.name());
            populateBlackListCps(videoAdConfigResponse,blackListedCps);
            populateVideoAdRecommendations(videoAdConfigResponse, blackListedCps);
            AdProperties adProperties = adPropertiesService.findByAdType(AdType.VIDEO_AD);
            responseBuilder.addVideoProperties(adProperties.getProperties());
        }

        if (MapUtils.isNotEmpty(responseBuilder.getInterstitialAds())) {
            AdProperties adProperties = adPropertiesService.findByAdType(AdType.INTERSTITIAL_AD);
            responseBuilder.addInterstitialProperties(adProperties.getProperties());
        }
    }

    private void populateBlackListCps(VideoAdConfigResponse videoAdConfigResponse,Set<String> blackListedCps) {
        if (videoAdConfigResponse instanceof SingleVideoAdMetaDTO) {
            SingleVideoAdMetaDTO singleVideoAdMetaDTO = (SingleVideoAdMetaDTO) videoAdConfigResponse;
            if (CollectionUtils.isNotEmpty(blackListedCps) && Objects.nonNull(singleVideoAdMetaDTO) && Objects.nonNull(singleVideoAdMetaDTO.getVideoSlots())) {
                singleVideoAdMetaDTO.getVideoSlots().values().forEach(slot -> slot.getBlackListedCps().addAll(blackListedCps));
            }
        } else if (videoAdConfigResponse instanceof MultiVideoAdMetaDTO) {
            MultiVideoAdMetaDTO multiVideoAdMetaDTO = (MultiVideoAdMetaDTO) videoAdConfigResponse;
            if (CollectionUtils.isNotEmpty(blackListedCps) && Objects.nonNull(multiVideoAdMetaDTO) && Objects.nonNull(multiVideoAdMetaDTO.getVideoSlots())) {
                multiVideoAdMetaDTO.getVideoSlots().values().forEach(slot -> {
                    for (VideoAdMetaDTO videoAdMetaDTO : slot)
                        videoAdMetaDTO.getBlackListedCps().addAll(blackListedCps);
                });
            }
        }
    }


    private void populateBlackListCps(AudioAdConfigResponse audioAdConfigResponse,Set<String> blackListedCps) {
        if (audioAdConfigResponse instanceof SingleAudioAdMetaDTO) {
            SingleAudioAdMetaDTO singleAudioAdMetaDTO = (SingleAudioAdMetaDTO) audioAdConfigResponse;
            if (CollectionUtils.isNotEmpty(blackListedCps) && Objects.nonNull(singleAudioAdMetaDTO) && Objects.nonNull(singleAudioAdMetaDTO.getAudioSlots())) {
                singleAudioAdMetaDTO.getAudioSlots().values().forEach(slot -> slot.getBlacklistedCps().addAll(blackListedCps));
            }
        } else if (audioAdConfigResponse instanceof MultiAudioAdMetaDTO) {
            MultiAudioAdMetaDTO multiAudioAdMetaDTO = (MultiAudioAdMetaDTO) audioAdConfigResponse;
            if (CollectionUtils.isNotEmpty(blackListedCps) && Objects.nonNull(multiAudioAdMetaDTO) && Objects.nonNull(multiAudioAdMetaDTO.getAudioSlots())) {
                multiAudioAdMetaDTO.getAudioSlots().values().forEach(slot -> {
                    for (AudioAdMetaDTO audioAdMetaDTO : slot)
                        audioAdMetaDTO.getBlacklistedCps().addAll(blackListedCps);
                });
            }
        }
    }

    private void addDfpParams(String uid, AdConfigResponseBuilder responseBuilder, AdConfigContext adConfigContext) {
        Map<String, String> dfpParams = campaignTargetingFromCassandraTask.readCampaignTargeting(uid).stream().collect(Collectors.toMap(AdTargeting::getAdid, AdTargeting::targetedString, (k1, k2) -> k1));
        dfpParams.put(UID, uid);
        if (adConfigContext.getUserProfile().getSegments() != null && adConfigContext.getUserProfile().getSegments().containsKey(uid)) {
            dfpParams.put("thanks_segment", adConfigContext.getUserProfile().getSegments().get(uid).get(0));
        }
        responseBuilder.dfpParams(dfpParams);
        logger.debug("Dfp Params map of size {} fetched for uid {}", dfpParams.size(), uid);
        populateDfpParams(adConfigContext.getUserPersona(), responseBuilder);
        populateDfpParams(adConfigContext.getUserConfig(), responseBuilder);
    }

    private void populateDfpParams(UserConfig userConfig, AdConfigResponseBuilder responseBuilder) {
        if (userConfig != null && userConfig.getAttributes() != null) {
            if (CollectionUtils.isNotEmpty(userConfig.getAttributes().getTopGenre())) {
                responseBuilder.addDfpParam(DfpKey.GENRES.key(), userConfig.getAttributes().getTopGenre().get(0));
            }

            if (CollectionUtils.isNotEmpty(userConfig.getAttributes().getCircle())) {
                responseBuilder.addDfpParam(DfpKey.CIRCLE.key(), userConfig.getAttributes().getCircle().get(0));
            }

            if (CollectionUtils.isNotEmpty(userConfig.getAttributes().getSelectedLanguages())) {
                responseBuilder.addDfpParam(DfpKey.SELECTED_LANGUAGE.key(), userConfig.getAttributes().getSelectedLanguages().get(0));
            }

            if (CollectionUtils.isNotEmpty(userConfig.getAttributes().getHcl())) {
                responseBuilder.addDfpParam(DfpKey.LANGUAGES.key(), userConfig.getAttributes().getHcl().get(0));
            }
        } else {
            logger.info("UserConfigDetails is empty {}", userConfig);
        }
    }


    private void populateDfpParams(UserPersona personaDetails, AdConfigResponseBuilder responseBuilder) {
        if (personaDetails != null && personaDetails.getAtv() != null) {
            if (personaDetails.getAtv().getUser() != null) {
                responseBuilder.addDfpParam(DfpKey.CIRCLE.key(), personaDetails.getAtv().getUser().getCircle());
                responseBuilder.addDfpParam(DfpKey.SELECTED_LANGUAGE.key(), personaDetails.getAtv().getUser().getMobility());
                responseBuilder.addDfpParam(DfpKey.CURRENT_SEGMENT.key(), personaDetails.getAtv().getUser().getCurrSeg());
                responseBuilder.addDfpParam(DfpKey.PREVIOUS_SEGMENT.key(), personaDetails.getAtv().getUser().getPrevSeg());
                responseBuilder.addDfpParam(DfpKey.USER_TYPE.key(), personaDetails.getAtv().getUser().getUserType());
            }
            if (personaDetails.getAtv().getTopGenres() != null && personaDetails.getAtv().getTopGenres().size() > 0) {
                responseBuilder.addDfpParam(DfpKey.GENRES.key(), personaDetails.getAtv().getTopGenres().get(0).get(DfpKey.GENRES.key()));
            }
            if (personaDetails.getAtv().getTopLanguages() != null && personaDetails.getAtv().getTopLanguages().size() > 0) {
                responseBuilder.addDfpParam(DfpKey.LANGUAGES.key(), personaDetails.getAtv().getTopLanguages().get(0).get(DfpKey.LANGUAGES.key()));
            }

        } else {
            logger.info("PersonaDetails is empty {}", personaDetails);
        }
    }

    public void populateAudioAdRecommendations(AudioAdConfigResponse audioAdConfigResponse, Set<String> blackListCps) {
        if (audioAdConfigResponse != null) {
            List<String> adIds = null;
            if (audioAdConfigResponse instanceof SingleAudioAdMetaDTO) {
                SingleAudioAdMetaDTO singleAudioAdMetaDTO = (SingleAudioAdMetaDTO) audioAdConfigResponse;
                adIds = new ArrayList<>(singleAudioAdMetaDTO.getAudioSlots().keySet());
            } else if (audioAdConfigResponse instanceof MultiAudioAdMetaDTO) {
                MultiAudioAdMetaDTO multiAudioAdMetaDTO = (MultiAudioAdMetaDTO) audioAdConfigResponse;
                adIds = new ArrayList<>(multiAudioAdMetaDTO.getAudioSlots().keySet());
            }
            if (CollectionUtils.isNotEmpty(adIds)) {
                List<AdRecommendation> adRecommendations = adRecommendationService.getAdRecommendation(AdType.AUDIO_AD, adIds);
                Set<String> adRecommendationTypes = adRecommendations.stream().map(AdRecommendation::getType).collect(Collectors.toSet());
                if (CollectionUtils.isNotEmpty(blackListCps)) {
                    adRecommendationTypes = adRecommendationTypes.stream().filter(a -> !blackListCps.contains(a)).collect(Collectors.toSet());
                }
                for (String adRecommendationType : adRecommendationTypes) {
                    adRecommendations.stream()
                            .filter(adRecommendation -> adRecommendation.getType().equalsIgnoreCase(adRecommendationType))
                            .reduce((r1, r2) -> r1.getHierarchy() > r2.getHierarchy() ? r1 : r2)
                            .ifPresent(adRecommendation -> audioAdConfigResponse.getMeta().put(adRecommendationType, adRecommendation.getSlotId()));
                }
            }
        }
    }

    public void populateVideoAdRecommendations(VideoAdConfigResponse videoAdConfigResponse, Set<String> blackListCps) {
        if (videoAdConfigResponse != null) {
            List<String> adIds = null;
            if (videoAdConfigResponse instanceof SingleVideoAdMetaDTO) {
                SingleVideoAdMetaDTO singleVideoAdMetaDTO = (SingleVideoAdMetaDTO) videoAdConfigResponse;
                adIds = new ArrayList<>(singleVideoAdMetaDTO.getVideoSlots().keySet());
            } else if (videoAdConfigResponse instanceof MultiVideoAdMetaDTO) {
                MultiVideoAdMetaDTO multiVideoAdMetaDTO = (MultiVideoAdMetaDTO) videoAdConfigResponse;
                adIds = new ArrayList<>(multiVideoAdMetaDTO.getVideoSlots().keySet());
            }
            if (CollectionUtils.isNotEmpty(adIds)) {
                List<AdRecommendation> adRecommendations = adRecommendationService.getAdRecommendation(AdType.VIDEO_AD, adIds);
                Set<String> adRecommendationTypes = adRecommendations.stream().map(AdRecommendation::getType).collect(Collectors.toSet());
                if (CollectionUtils.isNotEmpty(blackListCps)) {
                    adRecommendationTypes = adRecommendationTypes.stream().filter(a -> !blackListCps.contains(a)).collect(Collectors.toSet());
                }
                for (String adRecommendationType : adRecommendationTypes) {
                    adRecommendations.stream()
                            .filter(adRecommendation -> adRecommendation.getType().equalsIgnoreCase(adRecommendationType))
                            .reduce((r1, r2) -> r1.getHierarchy() > r2.getHierarchy() ? r1 : r2)
                            .ifPresent(adRecommendation -> videoAdConfigResponse.getMeta().put(adRecommendationType, adRecommendation.getSlotId()));
                }
            }
        }
    }

    private void createPersonaAdConfigContext(String uid, AdConfigContextBuilder adConfigContextBuilder) throws Exception {
        logger.info("Fetching user persona details for uid {}", uid);
        UserPersona personaDetails = personaService.getUserPersona(uid);
        logger.debug("PersonaDetails {} is fetched for uid {}", personaDetails, uid);
        adConfigContextBuilder.userPersona(personaDetails);
    }

    private void createMusicAdConfigContext(String uid, AdConfigContextBuilder adConfigContextBuilder) throws Exception {
        logger.info("Fetching user config details for uid {}", uid);
        UserConfig userConfig = musicUserConfigService.getUserConfig(uid);
        logger.debug("UserConfigDetails {} is fetched for uid {}", userConfig, uid);
        adConfigContextBuilder.userConfig(userConfig);
    }

    private void createUserPlanProfileContext(String uid,String service, AdConfigContextBuilder adConfigContextBuilder) throws Exception {
        logger.info("Fetching user profile details for uid {}", uid);
        UserProfile userProfile = userProfileAdService.getUserProfile(uid,service);
        AnalyticService.update(userProfile);
        logger.debug("UserProfile {} is fetched for uid {}", userProfile, uid);
        adConfigContextBuilder.userProfile(userProfile);
    }

    private Map<AdType, Set<String>> addBlacklistedCpsForUserProfile(AdConfigContext adConfigContext, AdConfigResponseBuilder adResponseBuilder) {
        List<AdsOfferBlacklisted> adsOfferBlacklisted = new ArrayList<>();
        UserProfile userProfile = adConfigContext.getUserProfile();
        if(userProfile!=null && userProfile.getOfferId()!=null) {
            adsOfferBlacklisted = userProfileAdService.getAdsOfferBlacklistedFromUserProfile(userProfile);
            logger.info("ads offer blacklisted", adsOfferBlacklisted);
        }
        Map<AdType, Set<String>> adTypeBlacklistedCpsMap = new HashMap<>();
        if(adsOfferBlacklisted!=null) {
            adTypeBlacklistedCpsMap = userProfileAdService.getAdTypeBlackListedCpMap(adsOfferBlacklisted);
        }
        return adTypeBlacklistedCpsMap;
    }

    public void evictUserProfileAdCache(UserCachePurgeRequest userCachePurgeRequest) {
        userProfileAdService.evictUserProfileAdCache(userCachePurgeRequest.getUid(), userCachePurgeRequest.getService());
    }

}
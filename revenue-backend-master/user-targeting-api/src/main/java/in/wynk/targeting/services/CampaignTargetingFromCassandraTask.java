package in.wynk.targeting.services;

import com.datastax.driver.core.ConsistencyLevel;
import in.wynk.cache.aspect.advice.Cacheable;
import in.wynk.targeting.core.dao.entity.cassandra.AdTargeting;
import in.wynk.targeting.core.dao.repository.cassandra.AdTargetingRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.data.cassandra.core.cql.QueryOptions;
import org.springframework.data.cassandra.core.query.Criteria;
import org.springframework.data.cassandra.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static in.wynk.targeting.core.constant.AdConstants.*;

@Service
public class CampaignTargetingFromCassandraTask {

    @Autowired
    private AdTargetingRepository adTargetingRepository;

    @Autowired
    private CassandraOperations adTechCassandraOperations;

    private static final Pattern segmentPattern = Pattern.compile("^segment:(.+)$");


    public void addCampaignTargeting(Map<String, String> jsonObject,
                                     List<AdTargeting> campaignTargetings) {
        boolean blacklistedKeyExists = false;
        boolean combinedtgKeyExists = false;
        boolean adSeen = false;
        boolean adNotSeen = false;
        boolean isAdTypeSegment;

        jsonObject.put(DEFAULT_SEGMENT_30_DAYS, "true");
        jsonObject.put(DEFAULT_SEGMENT_90_DAYS, "true");
        jsonObject.put(DEFAULT_SEGMENT_180_DAYS, "true");
        jsonObject.put(DEFAULT_SEGMENT_365_DAYS, "true");

        for (AdTargeting campaignTargeting : campaignTargetings) {
            String adId = campaignTargeting.getAdid();
            if (StringUtils.isEmpty(adId)) {
                continue;
            }

            Matcher match = segmentPattern.matcher(adId);
            isAdTypeSegment = match.find();
            if (isAdTypeSegment) {
                adId = match.group(1);
            }

            jsonObject.put(adId, campaignTargeting.targetedString());

            if (adId != null && adId.equals("newblacklist")) {
                blacklistedKeyExists = true;
            }

            if (adId != null && adId.equals("combinedtg")) {
                combinedtgKeyExists = true;
            }

            if (adId != null && adId.equals("Adheard")) {
                adSeen = true;
            }

            if (adId != null && adId.equals("Adnotheard")) {
                adNotSeen = true;
            }

            if (!StringUtils.isEmpty(adId) && isAdTypeSegment && campaignTargeting.getTargeted()) {
                if (adId.endsWith("365")) {
                    jsonObject.put(DEFAULT_SEGMENT_365_DAYS, "false");
                } else if (adId.endsWith("180")) {
                    jsonObject.put(DEFAULT_SEGMENT_180_DAYS, "false");
                } else if (adId.endsWith("90")) {
                    jsonObject.put(DEFAULT_SEGMENT_90_DAYS, "false");
                } else if (adId.endsWith("30")) {
                    jsonObject.put(DEFAULT_SEGMENT_30_DAYS, "false");
                }

                jsonObject.put("segmentid", adId);
            }
        }

        if (!blacklistedKeyExists) {
            jsonObject.put("newblacklist", "false");
        }

        if (!combinedtgKeyExists) {
            jsonObject.put("combinedtg", "false");
        }

        if (!adSeen) {
            jsonObject.put("Adheard", "false");
        }

        if (!adNotSeen) {
            jsonObject.put("Adnotheard", "false");
        }

        jsonObject.putIfAbsent("hotstaradheard", "false");

    }

    @Cacheable(cacheName = "DFP_PARAMS", cacheKey = "T(java.lang.String).format('%s:%s', #root.methodName, #uid)", l1CacheTtl = 20 * 60, l2CacheTtl = 24 * 60 * 60)
    public List<AdTargeting> readCampaignTargeting(String uid) {
        if (uid == null) {
            return null;
        }

        List<AdTargeting> adTargetings = adTechCassandraOperations.select(Query.query(Criteria.where("uid").is(uid)).queryOptions(QueryOptions.builder().consistencyLevel(ConsistencyLevel.LOCAL_ONE).build()), AdTargeting.class);
//        List<AdTargeting> adTargetings = adTargetingRepository.findByUid(uid);
        if (CollectionUtils.isEmpty(adTargetings)) {
            return Collections.emptyList();
        } else {
            populateDefaultParam(uid, adTargetings);
        }
        return adTargetings;
    }

    private void populateDefaultParam(String uid, List<AdTargeting> targetings) {
        Arrays.asList("Adheard", "Adnotheard", "combinedtg", "hotstaradheard", "newblacklist").forEach(adid -> targetings.add(AdTargeting.builder().uid(uid).adid(adid).targeted(false).build()));
        Arrays.asList("defSegment_180", "defSegment_30", "defSegment_365", "defSegment_90").forEach(adid -> targetings.add(AdTargeting.builder().uid(uid).adid(adid).targeted(true).build()));
    }
}

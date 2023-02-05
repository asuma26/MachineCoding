package in.wynk.subscription.core.dao.repository.usermeta.impl;

import in.wynk.subscription.core.dao.entity.ThanksUserSegment;
import in.wynk.subscription.core.dao.repository.usermeta.ThanksSegmentRepository;
import in.wynk.subscription.core.dao.repository.usermeta.ThanksSegmentRepositoryCustom;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Abhishek
 * @created 19/06/20
 */
public class ThanksSegmentRepositoryImpl implements ThanksSegmentRepositoryCustom {

    public static final String ACTIVATION_MESSAGE = "Activation";
    public static final String DEACTIVATION_MESSAGE = "Deactivation";
    private final static Set<String> SEGMENTS_TO_BE_REMOVED = new HashSet<>(Arrays.asList("airteltv", "books", "music"));

    @Autowired
    private ThanksSegmentRepository thanksSegmentRepository;

    @Override
    public ThanksUserSegment getSegment(String si, String servicePack) {
        List<ThanksUserSegment> allUserSegments = thanksSegmentRepository.findBySi(si);
        return getActiveSegment(allUserSegments, servicePack);
    }

    private ThanksUserSegment getActiveSegment(List<ThanksUserSegment> allUserSegments, String servicePack) {
        if (CollectionUtils.isNotEmpty(allUserSegments)) {
            Optional<ThanksUserSegment> activationSegment = filterSegment(allUserSegments, ACTIVATION_MESSAGE, servicePack);
            Optional<ThanksUserSegment> deactivationSegment = filterSegment(allUserSegments, DEACTIVATION_MESSAGE, servicePack);
            if (activationSegment.isPresent()) {
                ThanksUserSegment userActivationSegment = activationSegment.get();
                if (SEGMENTS_TO_BE_REMOVED.contains(servicePack)) {
                    List<ThanksUserSegment> segmentsToBeRemoved = new ArrayList<>();
                    segmentsToBeRemoved.add(userActivationSegment);
                    deactivationSegment.ifPresent(segmentsToBeRemoved::add);
                    thanksSegmentRepository.deleteAll(segmentsToBeRemoved);
                } else if (!deactivationSegment.isPresent() || userActivationSegment.getThanksTimestamp().after(deactivationSegment.get().getThanksTimestamp())) {
                    return userActivationSegment;
                }
            }
        }
        return null;
    }

    @Override
    public Map<String, List<ThanksUserSegment>> getAllSegments(Set<String> allSi) {
        List<ThanksUserSegment> allThanksUserSegment = thanksSegmentRepository.findAllBySiIn(allSi);
        Map<String, List<ThanksUserSegment>> allSiThanksSegment = new HashMap<>();
        if (CollectionUtils.isNotEmpty(allThanksUserSegment)) {
            Set<String> allServicePacks = allThanksUserSegment.stream().map(ThanksUserSegment::getServicePack).collect(Collectors.toSet());
            for (String servicePack : allServicePacks) {
                ThanksUserSegment activeSegment = getActiveSegment(allThanksUserSegment, servicePack);
                if (activeSegment != null) {
                    List<ThanksUserSegment> thanksUserSegments = allSiThanksSegment.getOrDefault(activeSegment.getSi(), new ArrayList<>());
                    thanksUserSegments.add(activeSegment);
                    allSiThanksSegment.put(activeSegment.getSi(), thanksUserSegments);
                }
            }
        }
        return allSiThanksSegment;
    }

    private Optional<ThanksUserSegment> filterSegment(List<ThanksUserSegment> allUserSegments, String event, String servicePack) {
        return allUserSegments.stream()
                .filter(u -> StringUtils.equalsIgnoreCase(u.getEvent(), event) && StringUtils.equalsIgnoreCase(servicePack, u.getServicePack()))
                .reduce((u1, u2) -> u1.getThanksTimestamp().after(u2.getThanksTimestamp()) ? u1 : u2);
    }
}

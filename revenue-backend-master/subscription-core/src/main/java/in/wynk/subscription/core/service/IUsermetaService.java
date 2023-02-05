package in.wynk.subscription.core.service;

import in.wynk.subscription.core.dao.entity.ThanksUserSegment;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IUsermetaService {

    Map<String, List<ThanksUserSegment>> getAllThankSegments(String msisdn, Set<String> allSi);

    void saveThanksUserSegment(String msisdn, ThanksUserSegment thanksUserSegment);

}

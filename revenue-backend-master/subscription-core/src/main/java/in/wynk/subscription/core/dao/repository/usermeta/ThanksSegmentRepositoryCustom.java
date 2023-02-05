package in.wynk.subscription.core.dao.repository.usermeta;

import in.wynk.subscription.core.dao.entity.ThanksUserSegment;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Abhishek
 * @created 19/06/20
 */
public interface ThanksSegmentRepositoryCustom {

    ThanksUserSegment getSegment(String si, String servicePack);

    Map<String, List<ThanksUserSegment>> getAllSegments(Set<String> allSi);
}

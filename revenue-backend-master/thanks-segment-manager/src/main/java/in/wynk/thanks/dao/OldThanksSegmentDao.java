package in.wynk.thanks.dao;

import in.wynk.thanks.entity.ThanksOldSegment;
import in.wynk.thanks.entity.ThanksUserSegment;
import in.wynk.thanks.entity.UserSegment;
import in.wynk.thanks.logging.ThanksLoggingMarkers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Deprecated
@Repository
public class OldThanksSegmentDao implements IThanksSegmentDao<ThanksUserSegment> {
    private static final Logger logger = LoggerFactory.getLogger(OldThanksSegmentDao.class);
    @Autowired
    private CassandraDao<ThanksOldSegment> cassandraDao;
    @Autowired
    private CassandraDao<UserSegment> userSegmentDao;

    @Override
    public ThanksUserSegment insert(ThanksUserSegment entity) {
        try {
            ThanksOldSegment thanksOldSegment = new ThanksOldSegment(entity);
            UserSegment userSegment = new UserSegment(entity);
            cassandraDao.insert(thanksOldSegment);
            userSegmentDao.insert(userSegment);
        }catch (Exception ex){
            logger.error(ThanksLoggingMarkers.OLD_SEGMENT_ERROR, ex.getMessage(), ex);
        }

        return entity;
    }
}

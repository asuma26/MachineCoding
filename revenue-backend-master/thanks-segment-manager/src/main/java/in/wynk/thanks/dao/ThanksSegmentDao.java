package in.wynk.thanks.dao;

import in.wynk.thanks.entity.ThanksUserSegment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Deprecated
@Repository
public class ThanksSegmentDao implements IThanksSegmentDao<ThanksUserSegment>{

    @Autowired
    private CassandraDao<ThanksUserSegment> cassandraDao;

    public ThanksUserSegment insert(ThanksUserSegment segment){
        return cassandraDao.insert(segment);
    }

}

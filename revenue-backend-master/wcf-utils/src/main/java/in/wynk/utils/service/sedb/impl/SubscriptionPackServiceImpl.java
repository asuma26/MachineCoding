package in.wynk.utils.service.sedb.impl;

import in.wynk.utils.dao.sedb.ISubscriptionPackDao;
import in.wynk.utils.domain.SubscriptionPack;
import in.wynk.utils.dto.PackPeriodicElement;
import in.wynk.utils.service.sedb.ISubscriptionPackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Deprecated
public class SubscriptionPackServiceImpl implements ISubscriptionPackService {

    @Autowired
    private ISubscriptionPackDao packDao;


    @Override
    public SubscriptionPack save(SubscriptionPack pack) {
        pack.setCreateTimestamp(Calendar.getInstance().getTimeInMillis());
        return this.packDao.save(pack);
    }

    @Override
    public SubscriptionPack update(SubscriptionPack pack) {
        return this.save(pack);
    }

    @Override
    public SubscriptionPack getSubscriptionPackById(int packId) {
        return this.packDao.findByProductId(packId);
    }

    @Override
    public List<PackPeriodicElement> getPackPeriodicElements(String service) {
        return packDao.findByService(service).stream().map(pack -> new PackPeriodicElement().from(pack)).collect(Collectors.toList());
    }

    @Override
    public boolean switchPackPeriodicElementById(int packId, boolean isDeprecated) {
        SubscriptionPack pack = this.getSubscriptionPackById(packId);
        if (pack != null) {
            pack.setDeprecated(isDeprecated);
            this.update(pack);
            return true;
        }
        return false;
    }

    @Override
    public boolean switchPackPeriodicElements(List<Integer> packIds, boolean isDeprecated) {
        return packIds.parallelStream().map(_packId -> this.switchPackPeriodicElementById(_packId, isDeprecated)).reduce((arg1, arg2) -> arg1 && arg2).get();
    }

}

package in.wynk.utils.service.subscription.impl;

import in.wynk.data.enums.State;
import in.wynk.exception.WynkRuntimeException;
import in.wynk.subscription.core.dao.entity.Partner;
import in.wynk.subscription.core.dao.repository.subscription.PartnerDao;
import in.wynk.utils.constant.WcfUtilsErrorType;
import in.wynk.utils.service.subscription.IPartnersService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PartnersServiceImpl implements IPartnersService {

    private final PartnerDao partnersDao;

    public PartnersServiceImpl(PartnerDao partnersDao) {
        this.partnersDao = partnersDao;
    }

    @Override
    public Partner save(Partner partner) {
        return partnersDao.save(partner);
    }

    @Override
    public Partner update(Partner partner) {
        Partner partner1 = find(partner.getId());
        return save(partner);
    }

    @Override
    public void switchState(String id, State state) {
        Partner partner = find(id);
        partner.setState(state);
        save(partner);
    }

    @Override
    public Partner find(String id) {
        return partnersDao.findById(Integer.parseInt(id)).orElseThrow(() -> new WynkRuntimeException(WcfUtilsErrorType.WCF016));
    }

    @Override
    public List<Partner> findAll(Pageable pageable) {
        return partnersDao.findAll(pageable).getContent();
    }
}

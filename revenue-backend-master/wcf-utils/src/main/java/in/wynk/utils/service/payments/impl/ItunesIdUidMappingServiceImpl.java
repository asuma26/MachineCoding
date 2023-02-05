package in.wynk.utils.service.payments.impl;

import in.wynk.exception.WynkRuntimeException;
import in.wynk.payment.core.dao.entity.ItunesIdUidMapping;
import in.wynk.payment.core.dao.repository.receipts.ItunesIdUidDao;
import in.wynk.utils.constant.WcfUtilsErrorType;
import in.wynk.utils.service.payments.IItunesIdUidMappingService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItunesIdUidMappingServiceImpl implements IItunesIdUidMappingService {

    private final ItunesIdUidDao itunesIdUidMappingDao;

    public ItunesIdUidMappingServiceImpl(ItunesIdUidDao itunesIdUidMappingDao) {
        this.itunesIdUidMappingDao = itunesIdUidMappingDao;
    }

    @Override
    public ItunesIdUidMapping find(String id) {
        return itunesIdUidMappingDao.findById(id).orElseThrow(() -> new WynkRuntimeException(WcfUtilsErrorType.WCF012));
    }

    @Override
    public List<ItunesIdUidMapping> findAll(Pageable pageable) {
        return itunesIdUidMappingDao.findAll(pageable).getContent();
    }
}

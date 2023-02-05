package in.wynk.utils.service.payments;

import in.wynk.payment.core.dao.entity.ItunesIdUidMapping;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IItunesIdUidMappingService {

    ItunesIdUidMapping find(String id);

    List<ItunesIdUidMapping> findAll(Pageable pageable);

}

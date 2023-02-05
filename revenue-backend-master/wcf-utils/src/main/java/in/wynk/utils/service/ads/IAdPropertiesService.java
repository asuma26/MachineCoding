package in.wynk.utils.service.ads;

import in.wynk.targeting.core.dao.entity.mongo.AdProperties;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IAdPropertiesService {

    AdProperties save(AdProperties adProperties);

    AdProperties update(AdProperties adProperties);

    AdProperties find(String id);

    List<AdProperties> findAll(Pageable pageable);

}

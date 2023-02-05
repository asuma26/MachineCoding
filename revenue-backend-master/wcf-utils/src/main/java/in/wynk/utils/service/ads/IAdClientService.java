package in.wynk.utils.service.ads;

import in.wynk.targeting.core.dao.entity.mongo.AdClient;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IAdClientService {

    AdClient save(AdClient adClient);

    AdClient update(AdClient adClient);

    void switchClient(String id, Boolean state);

    AdClient find(String id);

    List<AdClient> findAll(Pageable pageable);

}

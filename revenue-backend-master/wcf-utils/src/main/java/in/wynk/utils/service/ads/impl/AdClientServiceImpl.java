package in.wynk.utils.service.ads.impl;

import in.wynk.exception.WynkRuntimeException;
import in.wynk.targeting.core.dao.entity.mongo.AdClient;
import in.wynk.targeting.core.dao.repository.mongo.AdClientsMongoRepository;
import in.wynk.utils.constant.WcfUtilsErrorType;
import in.wynk.utils.service.ads.IAdClientService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdClientServiceImpl implements IAdClientService {

    private final AdClientsMongoRepository adClientsMongoRepository;

    public AdClientServiceImpl(AdClientsMongoRepository adClientsMongoRepository) {
        this.adClientsMongoRepository = adClientsMongoRepository;
    }

    @Override
    public AdClient save(AdClient adClient) {
        return adClientsMongoRepository.save(adClient);
    }

    @Override
    public AdClient update(AdClient adClient) {
        AdClient client = find(adClient.getClientId());
        return save(adClient);
    }

    @Override
    public void switchClient(String id, Boolean state) {
        AdClient client = find(id);
        client.setEnabled(state);
        save(client);
    }

    @Override
    public AdClient find(String id) {
        return adClientsMongoRepository.findById(id).orElseThrow(() -> new WynkRuntimeException(WcfUtilsErrorType.WCF001));
    }

    @Override
    public List<AdClient> findAll(Pageable pageable) {
        return adClientsMongoRepository.findAll(pageable).getContent();
    }

}

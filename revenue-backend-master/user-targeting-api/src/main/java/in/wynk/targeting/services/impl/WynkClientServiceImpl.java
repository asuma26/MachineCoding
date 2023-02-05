package in.wynk.targeting.services.impl;

import in.wynk.targeting.core.dao.entity.mongo.WynkClient;
import in.wynk.targeting.core.dao.repository.mongo.WynkClientMongoRepository;
import in.wynk.targeting.services.IWynkClientService;
import org.springframework.stereotype.Service;

@Service
public class WynkClientServiceImpl implements IWynkClientService {

    private final WynkClientMongoRepository wynkClientMongoRepository;

    public WynkClientServiceImpl(WynkClientMongoRepository wynkClientMongoRepository) {
        this.wynkClientMongoRepository = wynkClientMongoRepository;
    }

    @Override
    public WynkClient lookup(String clientId, String os) {
        return wynkClientMongoRepository.getByClientIdAndOs(clientId.toUpperCase(), os.toUpperCase())
                .orElse(WynkClient.builder().id("UNKNOWN").build());
    }

}

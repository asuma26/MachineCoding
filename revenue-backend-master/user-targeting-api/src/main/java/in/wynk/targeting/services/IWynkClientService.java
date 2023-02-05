package in.wynk.targeting.services;

import in.wynk.targeting.core.dao.entity.mongo.WynkClient;

public interface IWynkClientService {

    WynkClient lookup(String clientId, String os);

}

package in.wynk.client.context;

import in.wynk.auth.dao.entity.Client;

import java.util.Optional;

public class ClientContext {

    private final static ThreadLocal<Client> LOCAL = new ThreadLocal<>();

    public static Optional<Client> getClient() {
        return Optional.ofNullable(LOCAL.get());
    }

    public static void setClient(Client client) {
        LOCAL.set(client);
    }

}

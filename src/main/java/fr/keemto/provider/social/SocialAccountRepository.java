package fr.keemto.provider.social;


import fr.keemto.core.*;
import fr.keemto.core.fetching.Fetcher;
import fr.keemto.core.fetching.FetcherLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.*;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.List;

@Component
public class SocialAccountRepository implements AccountRepository {

    private static final Logger log = LoggerFactory.getLogger(SocialAccountRepository.class);

    private final UsersConnectionRepository usersConnectionRepository;
    private final FetcherLocator fetcherLocator;

    @Autowired
    public SocialAccountRepository(UsersConnectionRepository usersConnectionRepository, FetcherLocator fetcherLocator) {
        this.usersConnectionRepository = usersConnectionRepository;
        this.fetcherLocator = fetcherLocator;
    }

    @Override
    public List<Account> getAccounts(User user) {
        ConnectionRepository connectionRepository = getConnectionRepository(user);
        MultiValueMap<String, Connection<?>> allConnections = connectionRepository.findAllConnections();
        if (allConnections.isEmpty()) {
            return new ArrayList<Account>();
        }
        List<Account> accounts = new ArrayList<Account>();
        for (List<Connection<?>> connectionsPerProvider : allConnections.values()) {
            for (Connection<?> connection : connectionsPerProvider) {
                Account account = createAccount(user, connection);
                accounts.add(account);
            }
        }
        return accounts;
    }

    @Override
    public Account getAccount(AccountKey key) {
        User user = key.getUser();
        ConnectionKey connectionKey = new ConnectionKey(key.getProviderId(), key.getProviderUserId());
        ConnectionRepository connectionRepository = getConnectionRepository(user);
        Connection<?> connection;
        try {
            connection = connectionRepository.getConnection(connectionKey);
        } catch (NoSuchConnectionException e) {
            throw new IllegalArgumentException("No account found for key: " + key, e);
        }
        return createAccount(user, connection);
    }

    @Override
    public boolean supports(String providerId) {
        return fetcherLocator.hasFetcherFor(providerId);
    }


    private Account createAccount(User user, Connection<?> connection) {
        Fetcher fetcher = findFetcherForConnection(connection);
        SocialAccountKey accountKey = new SocialAccountKey(connection.getKey(), user);
        return new SocialAccount(accountKey, fetcher, connection, new RevocationHanlder() {
            @Override
            public void revoke(AccountKey key) {
                ConnectionRepository connectionRepository = getConnectionRepository(key.getUser());
                connectionRepository.removeConnection(new ConnectionKey(key.getProviderId(), key.getProviderUserId()));
            }
        });
    }

    private Fetcher findFetcherForConnection(Connection<?> connection) {
        ConnectionKey key = connection.getKey();
        String providerId = key.getProviderId();
        return fetcherLocator.getFetcher(providerId);
    }

    private ConnectionRepository getConnectionRepository(User user) {
        return usersConnectionRepository.createConnectionRepository(user.getUsername());
    }

}

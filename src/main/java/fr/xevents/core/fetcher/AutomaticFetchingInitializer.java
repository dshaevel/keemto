package fr.xevents.core.fetcher;

import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.xevents.core.User;
import fr.xevents.core.UserResolver;

@Component
public class AutomaticFetchingInitializer implements InitializingBean {

    private UserResolver userResolver;
    private FetcherHandlerFactory handlerFactory;
    private FetcherRegistrar registrar;
    private FetcherResolver fetcherResolver;

    @Override
    public void afterPropertiesSet() throws Exception {
        List<Fetcher> fetchers = fetcherResolver.resolveAll();
        registerAllHandlers(fetchers);
    }

    protected void registerAllHandlers(List<Fetcher> fetchers) {
        for (User user : userResolver.getAllUsers()) {
            List<FetcherHandler> handlers = handlerFactory.createHandlers(user);
            registrar.registerHandlers(handlers);
        }
    }

    @Autowired
    public void setUserResolver(UserResolver userResolver) {
        this.userResolver = userResolver;
    }

    @Autowired
    public void setHandlerFactory(FetcherHandlerFactory handlerFactory) {
        this.handlerFactory = handlerFactory;
    }

    @Autowired
    public void setRegistrar(FetcherRegistrar registrar) {
        this.registrar = registrar;
    }

    @Autowired
    public void setFetcherResolver(FetcherResolver fetcherResolver) {
        this.fetcherResolver = fetcherResolver;
    }

}

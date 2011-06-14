package fr.xevents.core.fetcher;

import java.util.List;

import fr.xevents.core.Event;
import fr.xevents.core.EventRepository;
import fr.xevents.core.User;

public class FetcherHandler {

    private final Fetcher<?> fetcher;
    private final User user;
    private final EventRepository eventRepository;

    FetcherHandler(Fetcher<?> fetcher, User user, EventRepository eventRepository) {
        this.fetcher = fetcher;
        this.user = user;
        this.eventRepository = eventRepository;
    }

    public void fetch() throws FetchingException {
        Event mostRecentEvent = eventRepository.getMostRecentEvent(user);
        fetchAndPersist(mostRecentEvent.getTimestamp());
    }

    private void fetchAndPersist(long lastFetchedEventTime) {

        try {
            List<Event> events = fetcher.fetch(user, lastFetchedEventTime);
            eventRepository.persist(events);
        } catch (RuntimeException e) {
            handleFetchingException(e);
        }
    }

    protected void handleFetchingException(Exception e) throws FetchingException {
        StringBuilder message = new StringBuilder();
        message.append("An error has occured when trying to update events for user: ");
        message.append(user);
        message.append(" with fetcher: ");
        message.append(fetcher.getProviderId());
        message.append(". Same operations will be executed again during next handler invocation : "
                + fetcher.getDelay());
        throw new FetchingException(message.toString(), e);
    }

}
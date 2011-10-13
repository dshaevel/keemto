package fr.keemto.core.fetcher.social;

import com.google.common.collect.Lists;
import fr.keemto.core.*;
import fr.keemto.core.fetcher.Fetcher;
import fr.keemto.util.TestConnection;
import org.junit.Before;
import org.junit.Test;
import org.springframework.social.connect.Connection;

import java.util.List;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

public class SocialAccountTest {

    private Account account;
    private Connection<?> connection;
    private Fetcher fetcher;
    private MinimalConnectionRepository repository;

    @Before
    public void prepare() throws Exception {
        fetcher = mock(Fetcher.class);
        connection = new TestConnection("twitter", "userId");
        AccountKey key = new AccountKey("twitter", "userId", new User("bguerout"));
        repository = mock(MinimalConnectionRepository.class);
        account = new SocialAccount(key, fetcher, connection, repository);
    }

    @Test
    public void shouldDelegateFetchingToFetcher() throws Exception {

        account.fetch(200L);

        verify(fetcher).fetch(connection, 200L);

    }

    @Test
    public void shouldCreateEventWithAccount() throws Exception {

        EventData data = new EventData(1, "message", "twitter");
        when(fetcher.fetch(connection, 200L)).thenReturn(Lists.newArrayList(data));

        List<Event> events = account.fetch(200L);

        Event event = events.get(0);
        assertThat(event.getAccount().equals(account), is(true));
    }

    @Test
    public void shouldConvertDataToEvent() throws Exception {

        EventData data = new EventData(1, "message", "twitter");
        when(fetcher.fetch(connection, 200L)).thenReturn(Lists.newArrayList(data));

        List<Event> events = account.fetch(200L);

        assertThat(events.size(), equalTo(1));
        Event event = events.get(0);
        assertThat(event.getTimestamp(), equalTo(1L));
        assertThat(event.getMessage(), equalTo("message"));
        assertThat(event.getAccount().equals(account), is(true));
    }

    @Test
    public void shouldConvertManyDatasToEvents() throws Exception {

        EventData data = new EventData(1, "message", "twitter");
        EventData data2 = new EventData(2, "message", "twitter");
        when(fetcher.fetch(eq(connection), anyLong())).thenReturn(Lists.newArrayList(data, data2));

        List<Event> events = account.fetch(200L);

        assertThat(events.size(), equalTo(2));

    }

    @Test
    public void shouldRevokeAccount() throws Exception {
        account.revoke();
        verify(repository).revoke(connection.getKey());
    }
}
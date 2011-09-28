package fr.keemto.core.fetcher.social;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import fr.keemto.core.Event;
import org.springframework.social.yammer.api.impl.MessageInfo;
import org.springframework.social.yammer.api.impl.YammerMessage;
import org.springframework.social.yammer.api.impl.YammerTemplate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class YammerFetcher extends SocialFetcher<YammerTemplate, YammerMessage> {

    public YammerFetcher(ConnectionResolver<YammerTemplate> connectionResolver, long delay) {
        super(connectionResolver, delay);
    }

    public YammerFetcher(ConnectionResolver<YammerTemplate> connectionResolver) {
        this(connectionResolver, 60000);
    }

    /*
    * TODO we fetch all messages because yammer REST API cannot filter messages by date, this API allows filtering by id (eg. get messages older than an id)
     */
    @Override
    protected List<YammerMessage> fetchApi(YammerTemplate api, long lastFetchedEventTime) {

        MessageInfo messageInfo = api.messageOperations().getMessagesSent(0, 0, null, 0);
        List<YammerMessage> messages = messageInfo.getMessages();
        Collection<YammerMessage> filteredYammerMessages = removeAlreadyFetchedMessages(messages, lastFetchedEventTime);
        return new ArrayList<YammerMessage>(filteredYammerMessages);
    }

    private Collection<YammerMessage> removeAlreadyFetchedMessages(List<YammerMessage> messages, final long lastFetchedEventTime) {
        return Collections2.filter(messages, new Predicate<YammerMessage>() {

            @Override
            public boolean apply(YammerMessage message) {
                Date messageDate = message.getCreatedAt();
                return messageDate.after(new Date(lastFetchedEventTime));
            }

        });
    }

    @Override
    protected Event convertDataToEvent(YammerMessage message, EventBuilder builder) {
        YammerMessage.Body body = message.getBody();
        String messageContent = body.getPlain();
        long messageCreationTime = message.getCreatedAt().getTime();
        return builder.message(messageContent).timestamp(messageCreationTime).build();
    }


    @Override
    public String getProviderId() {
        return "yammer";
    }
}

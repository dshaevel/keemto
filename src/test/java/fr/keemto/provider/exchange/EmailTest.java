package fr.keemto.provider.exchange;

import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;

public class EmailTest {

    @Test
    public void shouldExposeRecipientsAsACommaSeparatedString() throws Exception {
        List<String> recipients = Lists.newArrayList("1@domain.fr", "2@domain.fr");
        Email email = new Email("id", "user@gmail.com", "subject", "body", System.currentTimeMillis(), recipients);

        String recipientsAsString = email.getRecipientsAsString();

        assertThat(recipientsAsString, equalTo("1@domain.fr,2@domain.fr"));
    }

    @Test
    public void shouldBuildMailWithACommaSeparatedRecipients() throws Exception {
        Email email = new Email("id", "user@gmail.com", "subject", "body", System.currentTimeMillis(), "1@domain.fr,2@domain.fr");

        List<String> recipients = email.getRecipients();

        assertThat(recipients, hasItems("1@domain.fr", "2@domain.fr"));
    }
}
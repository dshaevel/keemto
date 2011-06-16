package fr.xevents.social;

import org.springframework.social.oauth1.AbstractOAuth1ServiceProvider;
import org.springframework.social.oauth1.OAuth1Template;
import org.springframework.social.twitter.api.TwitterApi;
import org.springframework.social.twitter.api.impl.TwitterTemplate;

public class HttpTwitterServiceProvider extends AbstractOAuth1ServiceProvider<TwitterApi> {

    public HttpTwitterServiceProvider(String consumerKey, String consumerSecret) {
        super(consumerKey, consumerSecret, new OAuth1Template(consumerKey, consumerSecret,
                "http://api.twitter.com/oauth/request_token", "http://api.twitter.com/oauth/authorize",
                "http://api.twitter.com/oauth/authenticate", "http://api.twitter.com/oauth/access_token"));
    }

    @Override
    public TwitterApi getApi(String accessToken, String secret) {
        return new TwitterTemplate(getConsumerKey(), getConsumerSecret(), accessToken, secret);
    }
}
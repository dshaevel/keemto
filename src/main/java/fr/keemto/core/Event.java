/*
 * Copyright (C) 2010 Benoit Guerout <bguerout at gmail dot com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.keemto.core;

import com.google.common.base.Objects;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class Event {

    private final long timestamp;

    private final User user;

    private final String message;

    private final String providerId;

    public Event(long timestamp, User user, String message, String providerId) {
        super();
        this.timestamp = timestamp;
        this.user = user;
        this.message = message;
        this.providerId = providerId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public User getUser() {
        return user;
    }

    public String getMessage() {
        return message;
    }

    public String getProviderId() {
        return providerId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Event event = (Event) o;

        if (timestamp != event.timestamp) return false;
        if (message != null ? !message.equals(event.message) : event.message != null) return false;
        if (providerId != null ? !providerId.equals(event.providerId) : event.providerId != null) return false;
        if (user != null ? !user.equals(event.user) : event.user != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (timestamp ^ (timestamp >>> 32));
        result = 31 * result + (user != null ? user.hashCode() : 0);
        result = 31 * result + (message != null ? message.hashCode() : 0);
        result = 31 * result + (providerId != null ? providerId.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Event{" +
                "timestamp=" + timestamp +
                ", user=" + user +
                ", message='" + message + '\'' +
                ", providerId='" + providerId + '\'' +
                '}';
    }

    public static class Builder {

        private final User user;
        private final String providerId;

        private String message = "";
        private long timestamp = System.currentTimeMillis();

        public Builder(User user, String providerId) {
            this.user = user;
            this.providerId = providerId;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder timestamp(long timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Event build() {
            return new Event(this);
        }
    }

    private Event(Builder builder) {
        this.timestamp = builder.timestamp;
        this.user = builder.user;
        this.message = builder.message;
        this.providerId = builder.providerId;
    }

}

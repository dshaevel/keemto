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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class JdbcEventRepository implements EventRepository {

    private static final Logger log = LoggerFactory.getLogger(JdbcEventRepository.class);

    private final JdbcTemplate jdbcTemplate;

    @Inject
    public JdbcEventRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Event> getAllEvents() {
        return jdbcTemplate.query("select ts,username,message,providerId from events", new EventRowMapper());
    }

    @Override
    public void persist(List<Event> events) {
        for (Event event : events) {
            persist(event);
        }
    }

    private void persist(Event event) {
        try {
            User user = event.getUser();
            jdbcTemplate.update("insert into events (ts,username,message,providerId) values(?,?,?,?)",
                    new Object[]{event.getTimestamp(), user.getUsername(), event.getMessage(), event.getProviderId()});
        } catch (DuplicateKeyException e) {
            throw new DuplicateEventException("Unable to persist event " + event + " because another event exists with same eventTime: " + event.getTimestamp(), e);
        }
    }

    @Override
    public Event getMostRecentEvent(User user, String providerId) {
        String[] parameters = {user.getUsername(), providerId};
        try {
            return jdbcTemplate
                    .queryForObject(
                            "select TOP 1 ts,username,message,providerId from events where username=? AND providerId=? ORDER BY ts DESC",
                            parameters, new EventRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return createInitializationEvent(user, providerId);
        }
    }

    private Event createInitializationEvent(User user, String providerId) {
        //TODO check if null object has to be created in repository or in task
        log.info("User: "
                + user
                + " hasn't event yet for provider: " + providerId
                + ". This is propably the first time application tried to fetch user's connections. An initialization event is returned.");
        return new InitializationEvent(user, providerId);
    }

    private final class EventRowMapper implements RowMapper<Event> {
        @Override
        public Event mapRow(ResultSet rs, int rowNum) throws SQLException {
            String username = rs.getString("username");
            User user = new User(username);
            return new Event(rs.getLong("ts"), user, rs.getString("message"), rs.getString("providerId"));
        }
    }

}

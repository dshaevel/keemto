package fr.xevents.core;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcEventRepository implements EventRepository {

    private static final Logger log = LoggerFactory.getLogger(JdbcEventRepository.class);

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public JdbcEventRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Event> getAllEvents() {
        return jdbcTemplate.query("select ts,username,message from events", new EventRowMapper());
    }

    @Override
    public void persist(List<Event> events) {
        for (Event event : events) {
            persist(event);
        }
    }

    private void persist(Event event) {
        jdbcTemplate.update("insert into events (ts,username,message) values(?,?,?)",
                new Object[] { event.getTimestamp(), event.getUser(), event.getMessage() });
    }

    @Override
    public Event getMostRecentEvent(User user) {
        String[] parameters = { user.getUsername() };
        try {
            return jdbcTemplate.queryForObject(
                    "select TOP 1 ts,username,message from events where username=? ORDER BY ts DESC", parameters,
                    new EventRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return createInitializationEvent(user);
        }
    }

    private Event createInitializationEvent(User user) {
        log.info("User: " + user + " hasn't event yet. "
                + "This is propably the first time this user is fetched. Creating an initialization event.");
        return new InitializationEvent(user.getUsername());
    }

    private final class EventRowMapper implements RowMapper<Event> {
        @Override
        public Event mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Event(rs.getLong("ts"), rs.getString("username"), rs.getString("message"));
        }
    }

}

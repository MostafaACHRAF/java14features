package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

}

@Component
class Runner {
    private PersonService personService;

    public Runner(PersonService personService) {
        this.personService = personService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void handelEvent() {
        var p = new Person(1, "tofi", 1);
        System.out.println(">>> " + p);
        personService.create("mimi", EmotionalState.HAPPY);
        personService.create("chocho", EmotionalState.HAPPY);
        personService.create("riri", EmotionalState.HAPPY);
        personService.create("me", EmotionalState.HAPPY);
        var per = this.personService.findById(5);
        System.out.println(">>> Found : " + per);
    }
}


@Service
class PersonService {
    private final JdbcTemplate jdbcTemplate;

    public PersonService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Person findById(Integer id) {
        final String findByIdQuery = """ 
                select * from people 
                where id = ?
                """;
        final RowMapper<Person> rowMapper =
                (resultSet, i) -> new Person(resultSet.getInt("id"), resultSet.getString("name"), resultSet.getInt("emotional_state"));
        return jdbcTemplate.queryForObject(findByIdQuery, new Object[]{id}, rowMapper);
    }

    public Person create(String name, EmotionalState emotionalState) {
        //smart switch a new feature in java 14
        var emotional_state_index = switch (emotionalState) {
            case SAD -> -1;
            case HAPPY -> 1;
            case SO_SO -> 0;
        };

        final String insertQuery = """
                insert into people(name, emotional_state) values(?,?)
                """;

        var sqlParams = List.of(new SqlParameter(Types.VARCHAR, "name"), new SqlParameter(Types.INTEGER, "emotional_state"));
        var preparedStatementFactory = new PreparedStatementCreatorFactory(insertQuery, sqlParams) {
            {
                super.setReturnGeneratedKeys(true);
                setGeneratedKeysColumnNames("id");
            }
        };
        var preparedStatmentCreator = preparedStatementFactory.newPreparedStatementCreator(List.of(name, emotional_state_index));
        var kh = new GeneratedKeyHolder();

        this.jdbcTemplate.update(preparedStatmentCreator, kh);

        // smart cast feature; create a local instance 'id' so we don't need to cast to Integer twice.
        System.out.println("key = " + kh.getKey());
        if (kh.getKey() instanceof Integer id) {
            return findById(id);
        }
        throw new IllegalArgumentException("We couldn't create person " + Person.class.getName() + "!");
    }
}


// record feature; add a default args constructor; we can add a compact constructor
record Person(Integer id, String name, int emotional_state) {
    //This is the compact constructor
    public Person {
        this.name = name.toUpperCase();
    }
}


enum EmotionalState {
    SAD, HAPPY, SO_SO
}






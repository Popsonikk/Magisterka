package main;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Query;
import org.neo4j.driver.Record;

import java.util.List;

import static org.neo4j.driver.Values.parameters;

public class Neo4jConnector implements AutoCloseable {

    private final Driver driver;

    public Neo4jConnector(String uri, String user, String password) {
        driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
    }

    @Override
    public void close() throws RuntimeException {
        driver.close();
    }

    public void printGreeting(final String message) {
        try (var session = driver.session()) {
            var greeting = session.executeWrite(tx -> {
                var query = new Query("CREATE (a:Greeting) SET a.message = $message RETURN a.message + ', from node ' + id(a)", parameters("message", message));
                var result = tx.run(query);
                return result.single().get(0).asString();
            });
            System.out.println(greeting);
        }
    }
    public List<Record> getRecomendation(String us, String mv)
    {
        try (var session = driver.session()) {
            var greeting = session.executeWrite(tx -> {
                var query = new Query("""
                         MATCH (u:User)-[r:RATED {rating:5}]->(m:Movie {title:$myMovie})\s
                                    WHERE u.name <> $myUser
                                    WITH u.name AS findedUser\s
                                    ORDER BY RAND() LIMIT 1\s
                                    MATCH (u2:User )-[r2:RATED {rating:5}]->(m2:Movie)\s
                                    WHERE u2.name = findedUser AND m2 <> $myMovie \s
                                    RETURN m2.title AS recommendedMovies ORDER BY RAND() LIMIT 10\
                        """, parameters("myUser", us,"myMovie",mv));
                var result = tx.run(query);
                return result.list();
            });
            return greeting;

        }
    }




}

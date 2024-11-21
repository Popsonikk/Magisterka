package main;

import main.objects.AssociationRule;
import main.objects.SimplePattern;
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


    public boolean doesAnyRecordExist() {
        try (var session = driver.session()) {
            var result = session.executeRead(tx -> {
                var query = new Query("MATCH (n) RETURN count(n) > 0 AS exists");
                var record = tx.run(query).single();
                return record.get("exists").asBoolean();
            });
            return result;
        }
    }
    public void cleanBase() {
        try (var session = driver.session()) {
            session.executeWrite(tx -> {

                var query = new Query("MATCH (n) DETACH DELETE n;");
                return tx.run(query).list();
            });
        }
    }
    public void createNodes(List<SimplePattern> patterns)
    {
        try (var session = driver.session()) {

            for(SimplePattern pattern:patterns)
            {
                StringBuilder builder=new StringBuilder();
                if(pattern.getPattern().size()==1)
                {
                    builder.append("MERGE (p:Product {name: $n , support: $p}) RETURN p");
                    session.executeWrite(tx -> {
                        var query = new Query(builder.toString(),parameters("n",pattern.getPattern().get(0),"p",pattern.getSupport()));
                        return tx.run(query).list();
                    });
                }
                else
                {
                    builder.append("MERGE (g:ProductGroup {name: $n, support: $s})")
                            .append("WITH $p AS products, g UNWIND products AS item MATCH (it:Product {name: item})")
                            .append("MERGE (it)-[:PART_OF]->(g) RETURN g");
                    session.executeWrite(tx -> {
                        var query = new Query(builder.toString(),parameters("p",pattern.getPattern(),"s",pattern.getSupport(),"n",pattern.getPattern().toString()));
                        return tx.run(query).list();
                    });
                }
            }
        }
    }

}

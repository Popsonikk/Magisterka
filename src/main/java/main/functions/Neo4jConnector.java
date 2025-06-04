package main.functions;

import main.objects.AssociationRule;
import main.objects.SimplePattern;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Query;
import org.neo4j.driver.Record;

import java.util.List;
import java.util.Map;

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
    public void createNodes(List<SimplePattern> patterns) {
        System.out.println("Creating " + patterns.size() + " nodes");
        try (var session = driver.session()) {
            // Tworzymy indeks tylko raz
            session.executeWrite(tx -> {
                var query = new Query("CREATE INDEX patternIndex IF NOT EXISTS FOR (n:Pattern) ON (n.name)");
                return tx.run(query).list();
            });

            // Przygotowujemy listę danych do wczytania
            List<Map<String, String>> nodes = patterns.stream()
                    .map(pattern -> Map.of(
                            "name", GeneratePattern.getString(pattern.getPattern()),
                            "support", String.valueOf(pattern.getSupport())
                    ))
                    .toList();

            session.executeWrite(tx -> {
                var query = new Query("UNWIND $nodes AS node MERGE (p:Pattern {name: node.name, support:node.support})",
                        parameters("nodes", nodes));
                tx.run(query);
                return null;
            });
        }
    }
    public void createEdges(List<AssociationRule> rules)
    {
        System.out.println("Creating " + rules.size() + " edges");
        try (var session = driver.session()) {

            List<Map<String, Object>> ruleData = rules.stream()
                    .map(rule -> Map.<String, Object>of(
                            "antecedent", GeneratePattern.getString(rule.getAntecedent().getPattern()),
                            "consequent", GeneratePattern.getString(rule.getConsequent().getPattern()),
                            "confidence", String.valueOf(rule.getConfidence()),
                            "lift", String.valueOf(rule.getLift())
                    ))
                    .toList();
            StringBuilder builder=new StringBuilder();
            builder.append("UNWIND $rules AS rule MATCH (left:Pattern {name: rule.antecedent}),(right:Pattern {name: rule.consequent})")
                            .append("MERGE (left)-[:SUPPORTING {confidence: rule.confidence, lift:rule.lift}]->(right)");
            session.executeWrite(tx -> {
                var query = new Query(builder.toString(), parameters("rules", ruleData));
                tx.run(query);
                return null;
            });
        }
    }
    public List<Record> checkNeighbourhood()
    {
        try (var session = driver.session()) {
           var res= session.executeWrite(tx -> {
                var query = new Query("""
                        MATCH (n:Pattern)-[s:SUPPORTING]->(m:Pattern)
                        WITH n, count(m) AS connections, sum(toFloat(s.lift)) AS strength
                        RETURN n.name AS pattern,n.support AS patternSupport, connections,strength
                        ORDER BY connections DESC, strength DESC;""");

                return tx.run(query).list();
            });
           return res;
        }
    }


}

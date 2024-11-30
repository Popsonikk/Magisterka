package main;

import main.objects.DijkstraEdges;
import main.objects.Edge;
import main.objects.Graph;
import main.objects.Node;
import org.neo4j.driver.Record;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RecommendationFunctions {

    public static Map<String,Double> processNeo4jOutput(List<Record> records,List<String> productList)
    {
        Map<String,Double> productStrength=productList.stream().collect(Collectors.toMap(product -> product, product -> 0.0));
        for(Record r:records)
        {
            double strength = Double.parseDouble(String.valueOf(r.get(1)).replace("\"", ""))
                    * Double.parseDouble(String.valueOf(r.get(2)).replace("\"", ""))
                    * Double.parseDouble(String.valueOf(r.get(3)).replace("\"", ""));

            String[] patternProducts = String.valueOf(r.get(0)).replace("\"", "").split(",");
            for(String s:patternProducts)
                productStrength.merge(s, strength, Double::sum);
        }
        productStrength=productStrength.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue((k1,k2)->Double.compare(k2,k1))).collect(LinkedHashMap::new,
                        (m, e) -> m.put(e.getKey(), e.getValue()),
                        LinkedHashMap::putAll);
        return productStrength;

    }
    public static Map<Node, Integer> findShortestPaths(Graph graph, Node startNode) {
        Map<Node, Integer> distances = new HashMap<>();
        PriorityQueue<NodeDistance> pq = new PriorityQueue<>(Comparator.comparingInt(NodeDistance::getDistance));
        Set<Node> visited = new HashSet<>();

        // Inicjalizacja
        for (Node node : graph.getNodes()) {
            distances.put(node, Integer.MAX_VALUE);
        }
        distances.put(startNode, 0);
        pq.add(new NodeDistance(startNode, 0));

        while (!pq.isEmpty()) {
            NodeDistance current = pq.poll();
            Node currentNode = current.getNode();

            if (visited.contains(currentNode)) continue;
            visited.add(currentNode);

            // Aktualizuj sąsiadów
            for (Edge edge : currentNode.getOutgoingEdges()) {
                Node neighbor;
                if(currentNode.getId().contains(edge.getNodes().get(1).getId()))
                    neighbor = edge.getNodes().get(0);
                else
                    neighbor = edge.getNodes().get(1);
                if (visited.contains(neighbor)) continue;

                int newDist = distances.get(currentNode) + edge.getWeight();
                if (newDist < distances.get(neighbor)) {
                    distances.put(neighbor, newDist);
                    pq.add(new NodeDistance(neighbor, newDist));
                }
            }
        }
        distances=distances.entrySet().stream().sorted(Map.Entry.comparingByValue()).collect(LinkedHashMap::new,
                (m, e) -> m.put(e.getKey(), e.getValue()),
                LinkedHashMap::putAll);

        return distances;
    }

    // Klasa pomocnicza dla kolejki priorytetowej
    static class NodeDistance {
        private Node node;
        private int distance;

        public NodeDistance(Node node, int distance) {
            this.node = node;
            this.distance = distance;
        }

        public Node getNode() {
            return node;
        }

        public int getDistance() {
            return distance;
        }
    }

}

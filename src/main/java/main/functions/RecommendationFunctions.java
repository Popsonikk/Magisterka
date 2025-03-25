package main.functions;

import javafx.util.Pair;
import main.objects.Edge;
import main.objects.Graph;
import main.objects.Node;
import org.neo4j.driver.Record;

import java.util.*;
import java.util.stream.Collectors;

public class RecommendationFunctions {

    public static Map<String,Double> processNeo4jOutput(List<Record> records,Map<String,Double> productList)
    {
        //siła pojedynczego produktu to suma iloczynu wsparcia liftu i connektu wszystkich patternów w których wystąplił np
        //mając wrzorzec kawa, mleko o supp 0,2 ilosci połączeń 30 i total lift 35, czyli sile 35/30,mamy 2 produkty, który każdy z nich wystąpił
        // w 20% przypadków w 30 połączeniach o sile 35/30, czyli na konto kawy i mleka idzie 0.2*(35/30)
        Map<String,Double> productStrength=productList.keySet().stream()
                .collect(Collectors.toMap(key -> key, key -> 0.0));
        for(Record r:records)
        {
            double strength = Double.parseDouble(String.valueOf(r.get(1)).replace("\"", "")) // wsparcie
                    *( Double.parseDouble(String.valueOf(r.get(3)).replace("\"", "")) //totalLift
                    / Double.parseDouble(String.valueOf(r.get(2)).replace("\"", ""))); //stopień

            String[] patternProducts = String.valueOf(r.get(0)).replace("\"", "").split(",");
            for(String s:patternProducts) // podział na pojedyncze produkty
                productStrength.merge(s, strength, Double::sum);
        }
        //wyczyszczenie wartości zerowej siły
        Map<String, Double> zeroValueProducts = productStrength.entrySet().stream()
                .filter(entry -> entry.getValue() == 0.0)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        // Usunięcie produktów o wartości 0.0 z oryginalnej mapy
        productStrength.entrySet().removeIf(entry -> entry.getValue() == 0.0);
        //dodanie wsparcia dla zerowej siły
        zeroValueProducts.replaceAll((key, value) -> productList.getOrDefault(key, 0.0));
        //sortowanie zerowych ze wsparciem
        zeroValueProducts=zeroValueProducts.entrySet().stream()
                .sorted(Map.Entry.comparingByValue((k1,k2)->Double.compare(k2,k1))).collect(LinkedHashMap::new,
                        (m, e) -> m.put(e.getKey(), e.getValue()), LinkedHashMap::putAll);

        //zeroValueProducts.forEach((key, value) -> System.out.println(key +": "+value));
        //sortowanie siły
        productStrength=productStrength.entrySet().stream()
                .sorted(Map.Entry.comparingByValue((k1,k2)->Double.compare(k2,k1))).collect(LinkedHashMap::new,
                        (m, e) -> m.put(e.getKey(), e.getValue()), LinkedHashMap::putAll);
        Map<String, Double> res = new LinkedHashMap<>(productStrength);
        res.put("-----",0.0);
        res.putAll(zeroValueProducts);
        return res;

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
            for (Edge edge : currentNode.getOutgoingEdges()) {// Aktualizuj sąsiadów
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
                }}}
        distances=distances.entrySet().stream().sorted(Map.Entry.comparingByValue((v1,v2)->Integer.compare(v2,v1))).collect(LinkedHashMap::new,
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
    public  static Map<Node,String> neo4jRecommendation(List<String> products,List<Node> distances,List<Pair<String,String>> categories)
    {
        //znajduje najdalszy node i wrzuca tan najpopularniejszy item, jego sąsiadom dajemy produkty najmniej popularne
        //powtarzamy do wyczerpania listy
        List<String> productStrength=new ArrayList<>(products);
        Map<Node,String> res=new LinkedHashMap<>();
        int size= distances.size();
        while (res.size()<size)
        {
            Node n=distances.get(0);
            res.put(distances.get(0),productStrength.get(0));
            String cat="";
            if(!categories.isEmpty())
            {
                for(Pair<String,String>p:categories)
                {
                    if(p.getKey().equals(productStrength.get(0)))
                    {
                        cat=p.getValue();
                        categories.remove(p);
                        break ;
                    }
                }
            }


            distances.remove(0);
            productStrength.remove(0);
            if(productStrength.isEmpty())
                productStrength=new ArrayList<>(products);
            for (Edge e:n.getOutgoingEdges())
            {
                Node neighbor;
                if(n.getId().contains(e.getNodes().get(1).getId()))
                    neighbor = e.getNodes().get(0);
                else
                    neighbor = e.getNodes().get(1);
                if(res.containsKey(neighbor))
                    continue;
                if(cat.equals(""))
                {
                    res.put(neighbor,productStrength.get(productStrength.size()-1));
                    productStrength.remove(productStrength.size()-1);
                }
                else
                {
                    String item="";
                    mainLoop:
                    for(int i= productStrength.size()-1;i>=0;i--)
                    {
                        for(Pair<String,String>p:categories)
                        {
                            if(p.getKey().equals(productStrength.get(i))&&p.getValue().equals(cat))
                            {
                                item=p.getKey();
                                categories.remove(p);
                                break mainLoop;
                            }
                        }
                    }
                    if(!Objects.equals(item, ""))
                    {
                        res.put(neighbor,item);
                        productStrength.remove(item);
                    }
                    else {
                        res.put(neighbor,productStrength.get(productStrength.size()-1));
                        productStrength.remove(productStrength.size()-1);
                    }


                }
                distances.remove(neighbor);
                if(productStrength.isEmpty())
                    productStrength=new ArrayList<>(products);
            }
        }

        return res;
    }

}

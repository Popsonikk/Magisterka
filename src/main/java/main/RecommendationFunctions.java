package main;

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
}

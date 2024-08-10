package main;

import javafx.util.Pair;

import java.util.*;

public class AprioriManager {
    private  BasketManager basketManager;


    private List<Pair<List<String>,Double>> result;

    AprioriManager()
    {
        this.result=new ArrayList<>();
    }

    public void setBasketManager(BasketManager basketManager) {
        this.basketManager = basketManager;
    }

    public void Apriori(double minSup, int len)
    {

        result.clear();
        if(basketManager.getBasketSize()==0)
        {
            System.out.println("Brak koszyków");
            return;
        }
        List<List<String>> baskets=basketManager.getBaskets();
        Map<List<String>, Integer> itemSetCount = new HashMap<>();
        //Kandydaci jedno Elementowi
        for(List<String> basket:baskets)
        {
            for (String item : basket)
            {
                //lista jedno elementowa (klucze to całe listy)
                List<String> itemSet = new ArrayList<>(Collections.singletonList(item));
                //zliczanie pojedynczych wystąpień (pobranie wartości +1)
                itemSetCount.put(itemSet, itemSetCount.getOrDefault(itemSet, 0) + 1);
            }
        }
        //Częste wzorce jednoelementowe
        List<Pair<List<String>,Double>>  currentPatterns = filterItemSets(itemSetCount, baskets.size(),minSup);
        //dodanie wzorców do listy wynikowej
        result.addAll(currentPatterns);
        int i=2;

        // krok rekurencyjny
        while (!currentPatterns.isEmpty()&&i<=len) {
            // Generowanie k-wzorców częstych
            List<List<String>> candidates = generateCandidates(currentPatterns);
            //wyczyszczenie mapy zliczającej wystąpienia k-wzorców
            itemSetCount = new HashMap<>();

            for (List<String> basket : baskets) {
                //Struktura set zapobiega duplikatom w danych
                Set<String> basketSet = new HashSet<>(basket);
                for (List<String> candidate : candidates) {
                    //sprawdzenie czy konkretny wzorzec w całości znajduje się w koszyku
                    if (basketSet.containsAll(candidate)) {
                        //zliczanie występowania wzorca
                        itemSetCount.put(candidate, itemSetCount.getOrDefault(candidate, 0) + 1);
                    }
                }
            }
            //wyfiltrowanie wzorców po wymaganym wsparciu
            currentPatterns = filterItemSets(itemSetCount, baskets.size(),minSup);
            result.addAll(currentPatterns);
            i++;
        }
        printSupport();

    }

    private List<List<String>> generateCandidates(List<Pair<List<String>,Double>> frequentItemSets) {
        //set dla pozbycia się duplikatów
        Set<Set<String>> candidates = new HashSet<>(); // Używamy Set do eliminacji duplikatów

        //generowanie wszystkich możliwych par (bez powtórzeń)
        for (int i = 0; i < frequentItemSets.size(); i++) {
            for (int j = i + 1; j < frequentItemSets.size(); j++) {
                List<String> itemSet1 = frequentItemSets.get(i).getKey();
                List<String> itemSet2 = frequentItemSets.get(j).getKey();
                //stworzenie tymczasowej listy i dodanie pierwszego wzorca
                Set<String> union = new HashSet<>(itemSet1);
                //dodanie drugiego wzorca z pary(duplikaty usuwane za pomocą set)
                union.addAll(itemSet2);


                //jeżeli długość jest większa o 1 to mamy kandydata, duplikaty usuwane przez strukturę set
                if (union.size() == itemSet1.size() + 1) {
                    candidates.add(union);
                }
            }
        }
        // Konwertujemy Set na List przed zwróceniem
        List<List<String>> result = new ArrayList<>();
        for (Set<String> candidate : candidates) {
            result.add(new ArrayList<>(candidate));
        }
        return result;
    }

    private List<Pair<List<String>,Double>>  filterItemSets(Map<List<String>, Integer> itemSetCount, int transactionCount, double minSupport)
    {
        //pusta lista wynikowa
        List<Pair<List<String>,Double>>  filteredItemSets = new ArrayList<>();
        //filtrujemy po mapie
        for (Map.Entry<List<String>, Integer> entry : itemSetCount.entrySet()) {
            //wyliczamy wsparcie
            double supp=(entry.getValue() / (double) transactionCount);
            if ( supp>= minSupport) {
                filteredItemSets.add(new Pair<>(entry.getKey(),supp));
            }
        }
        return filteredItemSets;
    }
    public void printSupport()
    {
        for(Pair<List<String>,Double> pattern: result)
        {
            System.out.print(pattern.getValue()+": ");
            for (String s: pattern.getKey())
            {
                System.out.print(s+"; ");
            }
            System.out.println();
        }
    }


}

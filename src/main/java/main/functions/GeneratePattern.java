package main.functions;

import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import javafx.util.Pair;
import main.objects.AssociationRule;
import main.objects.SimplePattern;

import java.util.*;
import java.util.stream.Collectors;

public class GeneratePattern {
    //rekurencyjne szukanie kandydatów
    public static  List<List<String>> generateCandidates(List<String> candidates, int index, int size, List<String> currCandidate) {
        List<List<String>> patterns = new ArrayList<>();
        //dodanie kandydata do zbioru
        if (currCandidate.size() == size)
        {
            patterns.add(new ArrayList<>(currCandidate));
            return patterns;
        }
        // Warunek zakończenia, przekroczony rozmiar listy
        if (index >= candidates.size())
            return patterns;
        // Krok rekurencyjny
        currCandidate.add(candidates.get(index));
        patterns.addAll(generateCandidates(candidates, index + 1, size, currCandidate));
        // Backtracking rekurencji
        currCandidate.remove(currCandidate.size() - 1);
        patterns.addAll(generateCandidates(candidates, index + 1, size, currCandidate));
        return patterns;
    }
    public static Set<List<String>> improvedCandidates(Set<String> uniqueItems,List<List<String>> currCandidates) {
        Set<List<String>> candidates = new HashSet<>();
        for (List<String> pattern : currCandidates) {
            for (String item : uniqueItems) {
                if (!pattern.contains(item)) {
                    List<String> newPattern = new ArrayList<>(pattern);
                    newPattern.add(item);
                    Collections.sort(newPattern);
                    candidates.add(newPattern);
                }
            }
        }
        return candidates;
    }
    public static long getSilnia(int basket, int candidate)
    {
        if (basket == candidate)
            return 1;
        else{
            int i=1;
            int len=basket-candidate;
            for (int j=basket+1;j<=basket+len;j++)
                i*=j;
            return i/len;
        }
    }


    //algorytm controllers
    public static List<SimplePattern> apriori(double minSup, int len, List<Set<String>> baskets)
    {
        baskets.sort(Comparator.comparingInt(Set::size));

        long startTime = System.currentTimeMillis();
        double size= baskets.size();
        //struktura do zliczania występowania wzorca w zbiorze
        Map<List<String>, Integer> patternCount = new HashMap<>();
        //Kandydaci jedno Elementowi
        for(Set<String> basket:baskets)
        {
            for (String item : basket)
            {
                //lista jedno elementowa (klucze to całe listy)
                List<String> itemSet = (Collections.singletonList(item));
                //zliczanie pojedynczych wystąpień (pobranie wartości +1)
                patternCount.put(itemSet, patternCount.getOrDefault(itemSet, 0) + 1);
            }
        }
        //Częste wzorce jednoelementowe

        Pair<List<List<String>>,List<SimplePattern>> p=filterItemSets(patternCount, size,minSup);

        List<List<String>>  currentPatterns = p.getKey();
        List<SimplePattern> result = new ArrayList<>(p.getValue());
        int i=2;
        // krok rekurencyjny
        while (!currentPatterns.isEmpty()&&i<=len)
        {
            //wyszukanie unikalnych przedmiotów, znajdujących się w zbiorze N częstym
            Set<String> uniqueItems = currentPatterns.stream().flatMap(List::stream).collect(Collectors.toSet());
            baskets=cleanBase(baskets,i);
            // Generowanie k-wzorców częstych
            System.out.println("generowanie wzorców "+i+" elementowych czas "+(System.currentTimeMillis()-startTime));
            //List<List<String>> candidates = GeneratePattern.generateCandidates(new ArrayList<>(uniqueItems),0,i,new ArrayList<>());
            Set<List<String>> candidates =GeneratePattern.improvedCandidates(uniqueItems,currentPatterns);
            //wyczyszczenie mapy zliczającej wystąpienia k-wzorców
            System.out.println("liczba kandydatów: "+candidates.size() + " Liczba koszyków "+baskets.size()+" Liczba" +
                    " przedmiotów "+ uniqueItems.size());
            patternCount.clear();
            int a=0;
            System.out.print("koszyk: ");
            for (Set<String> basket : baskets) {
                long silnia=  getSilnia(basket.size(),i);
                a++;
                int count=0;
                System.out.print(a+" ");
                if(a%100==0)
                    System.out.print("\nkoszyk: ");

                //Struktura set zapobiega duplikatom w danych
                for (List<String> candidate : candidates)
                {

                    //sprawdzenie, czy konkretny wzorzec w całości znajduje się w koszyku
                    if (basket.containsAll(candidate)){
                        patternCount.put(candidate, patternCount.getOrDefault(candidate, 0) + 1);
                        count++;

                    }
                    if (count == silnia)
                        break;

                }
            }
            System.out.println("koniec zliczanie wsparcia wzorców "+i+" elementowych czas "+(System.currentTimeMillis()-startTime));
            //wyfiltrowanie wzorców po wymaganym wsparciu
            p=filterItemSets(patternCount, size,minSup);
            currentPatterns = p.getKey();
            result.addAll(p.getValue());
            i++;
        }
        long endTime = System.currentTimeMillis();
        System.out.println((double)((endTime - startTime)/1000.f));
        return result;
    }

    //funkcja sprawdzająca minimalne wsparcie
    public static Pair<List<List<String>>,List<SimplePattern>> filterItemSets(Map<List<String>, Integer> itemSetCount, double transactionCount, double minSupport)
    {
        //lista przechowująca wzorce spełniające próg wsparcia
        List<List<String>>  patterns = new ArrayList<>();
        List<SimplePattern> res=new ArrayList<>();
        //filtrujemy po mapie
        for (Map.Entry<List<String>, Integer> entry : itemSetCount.entrySet()) {
            //wyliczamy wsparcie
            double supp=(entry.getValue() /  transactionCount);
            if ( supp>= minSupport)
            {

                //dodanie wyniku do listy wzorców
                res.add(new SimplePattern(entry.getKey(),supp));
                if(supp>= minSupport*1.5)
                    patterns.add(entry.getKey());
            }

        }
        return new Pair<>(patterns,res);


    }
    public static List<Set<String>> cleanBase(List<Set<String>> baskets, int size) {
        return baskets.stream()
                .filter(basket -> basket.size() >= size )
                .toList();
    }
    public static List<AssociationRule> generateRules(double conf, double lt, List<SimplePattern> patterns) {

        for (SimplePattern pattern : patterns) {
            pattern.getPattern().sort(Comparator.naturalOrder());
        }
        List<List<String>> subsets=new ArrayList<>();
        List<AssociationRule> ruleList=new ArrayList<>();
        List<String> consequent;
        Map<String, Double> patternMap=new HashMap<>();
       // patterns.sort(Comparator.comparingInt(o -> o.getPattern().size()));
        for (SimplePattern pattern : patterns) {
            patternMap.put(getString(pattern.getPattern()),pattern.getSupport());
        }
        long startTime = System.currentTimeMillis();
        int k=0,size,h;
        double confidence,lift;
        //iterujemy po wszystkich możliwych wzorcach
        for (SimplePattern pattern:patterns)
        {

            k++;
            System.out.println("wzorzec: "+k+" "+ "długość "+ pattern.getPattern().size());

            if(pattern.getPattern().size()==1)
                continue;
            subsets.clear();
            size=pattern.getPattern().size();
           /* if(size%2==1)
                h=size/2+1;
            else
                h=size/2;*/
            //tworzymy podzbiory wzorca
            for(int i=1;i<size;i++)
                subsets.addAll(GeneratePattern.generateCandidates(pattern.getPattern(),0,i,new ArrayList<>()));
            for (List<String> antecedent:subsets)
            {
                //tworzenie prawej strony na zasadzie usunięcia lewej strony z głównego wzorca
                consequent=new ArrayList<>(pattern.getPattern());
                consequent.removeAll(antecedent);

                SimplePattern at = new SimplePattern(antecedent,patternMap.get(getString(antecedent)));

                SimplePattern ct= new SimplePattern(consequent,patternMap.get(getString(consequent)));

                confidence = pattern.getSupport() / at.getSupport();
                lift = confidence / ct.getSupport();
                if(conf<confidence&&lift>lt)
                    ruleList.add(new AssociationRule(at,ct, pattern.getSupport(), confidence,lift));
            }
        }
        System.out.println("Czas: "+((System.currentTimeMillis()-startTime)/1000.f));
        return ruleList;
    }
    public static SimplePattern getSupport(List<SimplePattern> patterns, List<String> subset) {
        Optional<SimplePattern> matchingPattern = patterns.stream()
                .filter(simplePattern -> new HashSet<>(simplePattern.getPattern()).containsAll(subset)&&simplePattern.getPattern().size()==subset.size()).findFirst();
        return matchingPattern.orElse(null);
    }

    public static String getFilename()
    {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText("Wprowadź nazwę pliku");
        Optional<String> res = dialog.showAndWait();
        String filename="";
        if (res.isPresent())
            filename = res.get();
        return filename;
    }
    public static String getString(List<String> list)
    {

        StringBuilder builder=new StringBuilder();
        for(int i=0;i<list.size()-1;i++)
            builder.append(list.get(i)).append(",");
        builder.append(list.get(list.size()-1));
        return builder.toString();
    }
}

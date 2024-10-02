package main;

import javafx.scene.control.Alert;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class AprioriManager {
    private  BasketManager basketManager;
    private List<SimplePattern> result;
    private String supportFilename;
    AprioriManager()
    {
        this.result=new ArrayList<>();
        supportFilename="";
    }

    public int getAprioriSize()
    {
        return result.size();
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
            Alert a=new Alert(Alert.AlertType.ERROR);
            a.setContentText("Nie wczytałeś koszyków");
            a.show();
            return;
        }
        List<List<String>> baskets=basketManager.getBaskets();
        //struktura do zliczania występowania wzorca w zbiorze
        Map<List<String>, Integer> patternCount = new HashMap<>();
        //Kandydaci jedno Elementowi
        for(List<String> basket:baskets)
        {
            for (String item : basket)
            {
                //lista jedno elementowa (klucze to całe listy)
                List<String> itemSet = new ArrayList<>(Collections.singletonList(item));
                //zliczanie pojedynczych wystąpień (pobranie wartości +1)
                patternCount.put(itemSet, patternCount.getOrDefault(itemSet, 0) + 1);
            }
        }
        //Częste wzorce jednoelementowe
        List<SimplePattern>  currentPatterns = filterItemSets(patternCount, baskets.size(),minSup);
        //dodanie wzorców do listy wynikowej
        result.addAll(currentPatterns);
        int i=2;

        // krok rekurencyjny
        while (!currentPatterns.isEmpty()&&i<=len) {
            // Generowanie k-wzorców częstych
            List<List<String>> candidates = generateCandidates(currentPatterns);
            //wyczyszczenie mapy zliczającej wystąpienia k-wzorców
            patternCount = new HashMap<>();
            for (List<String> basket : baskets) {
                //Struktura set zapobiega duplikatom w danych
                Set<String> basketSet = new HashSet<>(basket);
                for (List<String> candidate : candidates) {
                    //sprawdzenie czy konkretny wzorzec w całości znajduje się w koszyku
                    if (basketSet.containsAll(candidate)) {
                        //zliczanie występowania wzorca
                        patternCount.put(candidate, patternCount.getOrDefault(candidate, 0) + 1);
                    }
                }
            }
            //wyfiltrowanie wzorców po wymaganym wsparciu
            currentPatterns = filterItemSets(patternCount, baskets.size(),minSup);
            result.addAll(currentPatterns);
            i++;
        }
        printSupport();
    }
    private List<List<String>> generateCandidates(List<SimplePattern> candidatePatterns) {
        //set dla pozbycia się duplikatów
        Set<List<String>> candidates = new HashSet<>(); // Używamy Set do eliminacji duplikatów

        //generowanie wszystkich możliwych par (bez powtórzeń)
        for (int i = 0; i < candidatePatterns.size(); i++) {
            for (int j = i + 1; j < candidatePatterns.size(); j++) {
                List<String> itemSet1 = candidatePatterns.get(i).getPattern();
                List<String> itemSet2 = candidatePatterns.get(j).getPattern();
                //stworzenie tymczasowej listy i dodanie pierwszego wzorca
                Set<String> union = new HashSet<>(itemSet1);
                //dodanie drugiego wzorca z pary(duplikaty usuwane za pomocą set)
                union.addAll(itemSet2);
                //jeżeli długość jest większa o 1 to mamy kandydata, duplikaty usuwane przez strukturę set
                if (union.size() == itemSet1.size() + 1) {
                    candidates.add(new ArrayList<>(union));
                }
            }
        }
        return  new ArrayList<>(candidates);
    }

    private List<SimplePattern>  filterItemSets(Map<List<String>, Integer> itemSetCount, int transactionCount, double minSupport)
    {
        //pusta lista wynikowa
        List<SimplePattern>  patterns = new ArrayList<>();
        //filtrujemy po mapie
        for (Map.Entry<List<String>, Integer> entry : itemSetCount.entrySet()) {
            //wyliczamy wsparcie
            double supp=(entry.getValue() / (double) transactionCount);
            if ( supp>= minSupport) {
                patterns.add(new SimplePattern(entry.getKey(),supp));
            }
        }
        return patterns;
    }
    public void printSupport()
    {
        result.sort((o1, o2) -> Double.compare(o2.getSupport(), o1.getSupport()));
        for(SimplePattern pattern: result)
        {
            System.out.print(pattern.getSupport()+": ");
            for (String s: pattern.getPattern())
            {
                System.out.print(s+"; ");
            }
            System.out.println();
        }
    }

    public List<SimplePattern> getSupportList() {
        return result;
    }
    public int getSupportListSize()
    {
        return result.size();
    }

    public void createCSVFIle() {
        if(Objects.equals(basketManager.getFilename(), "")|| result.isEmpty())
        {
            Alert a=new Alert(Alert.AlertType.ERROR);
            a.setContentText("Brak danych do zapisania");
            a.show();
            return;
        }

        try {
            String filename;
            if(Objects.equals(supportFilename, ""))
                filename=basketManager.getFilename().split("\\.")[0];
            else
                filename=supportFilename;
            File file = new File("dane/" + filename+"_supportData.csv");
            file.createNewFile();
            FileWriter writer=new FileWriter(file);
            writer.write("id,support,pattern\n");
            int i=0;
            for(SimplePattern pattern:result)
            {
                writer.write(i+","+ pattern.getSupport()+",");
                List<String> patterns = pattern.getPattern();
                for(int j = 0; j < patterns.size()-1; j++)
                    writer.write(patterns.get(j)+";");
                writer.write(patterns.get(patterns.size()-1)+"\n");
                i++;
            }
            writer.close();
            Alert a=new Alert(Alert.AlertType.INFORMATION);
            a.setContentText("Utworzenie pliku zakończone pomyślnie");
            a.show();
        } catch (IOException e) {
            Alert a=new Alert(Alert.AlertType.ERROR);
            a.setContentText("Wystąpił błąd przy zapisie");
            a.show();
            throw new RuntimeException(e);
        }

    }


}

package main;

import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.stage.FileChooser;

import java.io.*;
import java.util.*;

public class AprioriManager {
    private  BasketManager basketManager;
    private List<SimplePattern> result;
    private List<SimplePattern> filtredList;
    private String supportFilename;
    private List<Integer> filteredId;
    AprioriManager()
    {
        this.result=new ArrayList<>();
        this.filtredList=new ArrayList<>();
        this.filteredId=new ArrayList<>();
        supportFilename="";
    }
    public void setBasketManager(BasketManager basketManager) {
        this.basketManager = basketManager;
    }
    public List<SimplePattern> getSupportList() {
        return result;
    }
    public List<SimplePattern> getFiltredList() {
        return filtredList;
    }
    public int getSupportListSize()
    {
        return result.size();
    }
    public int getFilteredListSize()
    {
        return filtredList.size();
    }
    public void clearSupportList()
    {
        result.clear();
    }
    public void clearFilteredList()
    {
        filtredList.clear();
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
        Alert a=new Alert(Alert.AlertType.INFORMATION);
        a.setContentText("Operacja zakończona pomyślnie");
        a.show();
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
    public void createCSVFIle() {
        if(result.isEmpty())
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
    public void loadFromCSV()
    {
        try {
            FileChooser fileChooser=new FileChooser();
            fileChooser.setTitle("Wybierz plik zawierający poziomy wsparcia");
            File file = fileChooser.showOpenDialog(null);
            supportFilename=file.getName();
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            reader.readLine(); //header
            line= reader.readLine();
            while (line!=null)
            {
                String []pattern=line.split(",");
                double support=Double.parseDouble(pattern[1]);
                pattern=pattern[2].split(";");
                List<String> list=new ArrayList<>();
                for(String s:pattern)
                    list.add(s.trim().toLowerCase());
                result.add(new SimplePattern(list,support));
                line= reader.readLine();
            }
            Alert a=new Alert(Alert.AlertType.INFORMATION);
            a.setContentText("Dane zostały wczytane poprawnie");
            a.show();
        } catch (IOException e) {
            Alert a=new Alert(Alert.AlertType.ERROR);
            a.setContentText("Wystąpił błąd przy zapisie");
            a.show();
            throw new RuntimeException(e);

        }
    }


    public void sortBySupportUp() {
        result.sort(Comparator.comparingDouble(SimplePattern::getSupport));
        if(filtredList.size()>0)
            filtredList.sort(Comparator.comparingDouble(SimplePattern::getSupport));

    }
    public void sortBySupportDown() {
        result.sort((o1, o2) -> Double.compare(o2.getSupport(), o1.getSupport()));
        if(filtredList.size()>0)
            filtredList.sort((o1, o2) -> Double.compare(o2.getSupport(), o1.getSupport()));

    }
    public void sortByPatternUp() {
        result.sort(Comparator.comparingInt(o -> o.getPattern().size()));
        if(filtredList.size()>0)
            filtredList.sort(Comparator.comparingInt(o -> o.getPattern().size()));

    }
    public void sortByPatternDown() {
        result.sort((o1, o2) -> Integer.compare(o2.getPattern().size(), o1.getPattern().size()));
        if(filtredList.size()>0)
            filtredList.sort((o1, o2) -> Integer.compare(o2.getPattern().size(), o1.getPattern().size()));

    }
    public int deleteSelectedRows(List<CheckBox> checkBoxes, int startID,boolean f) {
        int j = 0;
        //idziemy od tyłu, aby nie zaburzyć ciągłosci listy
        for (int i = checkBoxes.size() - 1; i >= 0; i--) {

            if (checkBoxes.get(i).isSelected())
            {
                if (checkBoxes.get(i).isSelected() && !f)
                {
                    result.remove(startID + i);
                    j++;
                } else if (checkBoxes.get(i).isSelected() && f)
                {
                    result.remove(filtredList.get(startID + i));
                    filtredList.remove(startID + i);
                    j++;
                }
            }
        }
        return j;
    }
    public void filtrPatternItems(List<String> items) {

        filtredList.clear();
        filteredId.clear();
        int i = 0;
        for (SimplePattern pattern : result) {
            if (new HashSet<>(pattern.getPattern()).containsAll(items)) {
                filtredList.add(pattern);
                filteredId.add(i);
            }
            i++;
        }
    }
    public void filtrSupportLevel(double supp,boolean f) {

        filtredList.clear();
        filteredId.clear();
        int i = 0;
        for (SimplePattern pattern : result) {
            if ((pattern.getSupport()<=supp &&f)||(pattern.getSupport()>=supp &&!f) )
            {
                filtredList.add(pattern);
                filteredId.add(i);
            }
            i++;
        }
    }






}

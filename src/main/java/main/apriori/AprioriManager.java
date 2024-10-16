package main.apriori;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.stage.FileChooser;
import main.baskets.BasketManager;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
public class AprioriManager {
    private BasketManager basketManager;
    private List<SimplePattern> supportList;
    private List<SimplePattern> filtredList;
    private String supportFilename;
    private List<Integer> filteredId;
    public AprioriManager()
    {
        this.supportList=new ArrayList<>();
        this.filtredList=new ArrayList<>();
        this.filteredId=new ArrayList<>();
        supportFilename="";
    }
    //funkcje dostępowe do tablic
    public void setBasketManager(BasketManager basketManager) {this.basketManager = basketManager;}
    public List<SimplePattern> getSupportList() {return supportList;}
    public List<SimplePattern> getFiltredList() {return filtredList;}
    public int getSupportListSize() {return supportList.size();}
    public int getFilteredListSize() {return filtredList.size();}
    public void clearSupportList() {supportList.clear();}
    public void clearFilteredList() {filtredList.clear();}
    public String getBasketName() {return basketManager.getFilename();}

    public String getSupportFilename() {return supportFilename;}

    //algorytm apriori
    public void Apriori(double minSup, int len)
    {
        //wyczyszczenie listy wynikowej (przy wywołaniu funkcji, jeżeli dane już istnieją)
        supportList.clear();
        //wiadomość przy braku danych koszykowych
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
        List<List<String>>  currentPatterns = filterItemSets(patternCount, baskets.size(),minSup);
        int i=2;
        // krok rekurencyjny
        while (!currentPatterns.isEmpty()&&i<=len)
        {
            //wyszukanie unikalnych przedmiotów, znajdujących się w zbiorze N częstym
            Set<String> uniqueItems = currentPatterns.stream().flatMap(List::stream).collect(Collectors.toSet());
            // Generowanie k-wzorców częstych
            List<List<String>> candidates = generateCandidates(new ArrayList<>(uniqueItems),0,i,new ArrayList<>());
            //wyczyszczenie mapy zliczającej wystąpienia k-wzorców
            patternCount = new HashMap<>();
            for (List<String> basket : baskets) {
                //Struktura set zapobiega duplikatom w danych
                Set<String> basketSet = new HashSet<>(basket);
                for (List<String> candidate : candidates)
                {
                    //sprawdzenie, czy konkretny wzorzec w całości znajduje się w koszyku
                    if (basketSet.containsAll(candidate))
                        patternCount.put(candidate, patternCount.getOrDefault(candidate, 0) + 1);
                }
            }
            //wyfiltrowanie wzorców po wymaganym wsparciu
            currentPatterns = filterItemSets(patternCount, baskets.size(),minSup);
            i++;
        }
        Alert a=new Alert(Alert.AlertType.INFORMATION);
        a.setContentText("Operacja zakończona pomyślnie");
        a.show();
    }
    //rekurencyjne szukanie kandydatów
    public List<List<String>> generateCandidates(List<String> candidates, int index, int size, List<String> currCandidate) {
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
    //funkcja sprawdzająca minimalne wsparcie
    private List<List<String>>  filterItemSets(Map<List<String>, Integer> itemSetCount, int transactionCount, double minSupport)
    {
        //lista przechowująca wzorce spełniające próg wsparcia
        List<List<String>>  patterns = new ArrayList<>();
        //filtrujemy po mapie
        for (Map.Entry<List<String>, Integer> entry : itemSetCount.entrySet()) {
            //wyliczamy wsparcie
            double supp=(entry.getValue() / (double) transactionCount);
            if ( supp>= minSupport)
            {
                patterns.add(entry.getKey());
                //dodanie wyniku do listy wzorców
                supportList.add(new SimplePattern(entry.getKey(),supp));
            }
        }
        return patterns;
    }
    //funkcja zapisująca do pliku
    public void createCSVFIle() {
        if(supportList.isEmpty())
        {
            Alert a=new Alert(Alert.AlertType.ERROR);
            a.setContentText("Brak danych do zapisania");
            a.show();
            return;
        }
        try {
            String filename;
            //pobranie nazwy pliku, na podstawie którego pracował algorytm
            if(Objects.equals(supportFilename, ""))
                filename=basketManager.getFilename().split("\\.")[0];
            else
                filename=supportFilename;
            File file = new File("dane/" + filename+"_supportData.csv");
            //pętla mająca na celu zablokowanie duplikowania nazw plików
            int k=1;
            while (file.exists())
            {
                file = new File("dane/" + filename+k+"_supportData.csv");
                k++;
            }
            file.createNewFile();
            FileWriter writer=new FileWriter(file);
            writer.write("id,support,pattern\n");
            int i=0;
            //ręczny zapis do pliku w formie CSV
            for(SimplePattern pattern:supportList)
            {
                writer.write(i+","+ String.format(Locale.US,"%.3f", pattern.getSupport())+",");
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
    //funkcja pobierająca dane z pliku
    public void loadFromCSV()
    {
        try {
            FileChooser fileChooser=new FileChooser();
            fileChooser.setTitle("Wybierz plik zawierający poziomy wsparcia");
            File file = fileChooser.showOpenDialog(null);
            //zapis nazwy pliku
            supportFilename=file.getName();
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            reader.readLine(); //header
            line= reader.readLine();
            while (line!=null)
            {
                //pobranie danych zgodnie z formatem
                String []pattern=line.split(",");
                double support=Double.parseDouble(pattern[1]);
                pattern=pattern[2].split(";");
                List<String> list=new ArrayList<>();
                for(String s:pattern)
                    list.add(s.trim().toLowerCase());
                supportList.add(new SimplePattern(list,support));
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
    //funkcje sortujące dla interfejsu
    public void sortBySupportUp() {
        supportList.sort(Comparator.comparingDouble(SimplePattern::getSupport));
    }
    public void sortBySupportDown() {
        supportList.sort((o1, o2) -> Double.compare(o2.getSupport(), o1.getSupport()));
    }
    public void sortByPatternUp() {
        supportList.sort(Comparator.comparingInt(o -> o.getPattern().size()));
    }
    public void sortByPatternDown() {
        supportList.sort((o1, o2) -> Integer.compare(o2.getPattern().size(), o1.getPattern().size()));
    }
    //funkcja usuwająca dane z listy
    public int deleteSelectedRows(List<CheckBox> checkBoxes, int startID,boolean f) {
        int j = 0;
        //idziemy od tyłu, aby nie zaburzyć ciągłości listy
        for (int i = checkBoxes.size() - 1; i >= 0; i--) {
            if (checkBoxes.get(i).isSelected())
            {
                //
                if (checkBoxes.get(i).isSelected() && !f)
                {
                    supportList.remove(startID + i);
                    j++;
                } else if (checkBoxes.get(i).isSelected() && f)
                {
                    supportList.remove(filtredList.get(startID + i));
                    filtredList.remove(startID + i);
                    j++;
                }
            }
        }
        return j;
    }
    //funkcja filtrująca po wzorcach
    public void filtrPatternItems(List<String> items) {
        filtredList.clear();
        filteredId.clear();
        int i = 0;
        for (SimplePattern pattern : supportList) {
            if (new HashSet<>(pattern.getPattern()).containsAll(items)) {
                filtredList.add(pattern);
                filteredId.add(i);
            }
            i++;
        }
    }
    //funkcja filtrująca po poziomie wsparcia, zależne od flagi
    public void filtrSupportLevel(double supp,boolean f) {
        filtredList.clear();
        filteredId.clear();
        int i = 0;
        for (SimplePattern pattern : supportList) {
            if ((pattern.getSupport()<=supp &&f)||(pattern.getSupport()>=supp &&!f) )
            {
                filtredList.add(pattern);
                filteredId.add(i);
            }
            i++;
        }
    }


}

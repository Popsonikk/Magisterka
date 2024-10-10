package main.baskets;
import javafx.scene.control.CheckBox;
import javafx.stage.FileChooser;
import java.io.*;
import java.util.*;
public class BasketManager {
    private List<List<String>> baskets;
    private List<List<String>> filteredBaskets;
    private List<Integer> filteredId;
    private String filename;
    //funkcje dostępowe
    public String getFilename() {return filename;}
    public void setFilename(String filename) {this.filename = filename;}
    public int getFilteredBasketSize() {return filteredBaskets.size();}
    public int getBasketSize() {return baskets.size();}
    public void clearBaskets() {baskets.clear();}
    public void clearFilteredBaskets() {filteredBaskets.clear();}
    public List<List<String>> getBaskets() {return baskets;}
    public List<List<String>> getFilteredBaskets() {return filteredBaskets;}
    public BasketManager() {
        this.filteredBaskets = new ArrayList<>();
        this.baskets = new LinkedList<>();
        this.filteredId = new ArrayList<>();
        this.filename="";
    }
    //wczytanie danych z pliku
    public void loadBaskets() throws IOException {
        baskets.clear();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Wybierz plik zawierający listę koszyków");
        File file = fileChooser.showOpenDialog(null);
        filename=file.getName();
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        line = reader.readLine(); // pominięcie lini nagłówka
        line = reader.readLine();
        while (line != null) {
            //formatowanie danych w celu uniknięcia tworzenia przez list.of
            String[]b=line.split(",")[1].split(";");
            List list=new ArrayList<>();
            for(String s:b)
                list.add(s.trim().toLowerCase());
            baskets.add(list);
            line = reader.readLine();
        }
    }
    //filtrowanie koszyków
    public void filtrBaskets(List<String> items) {

        filteredBaskets.clear();
        filteredId.clear();
        int i = 0;
        for (List<String> b : baskets) {
            if (new HashSet<>(b).containsAll(items)) {
                filteredBaskets.add(b);
                filteredId.add(i);
            }
            i++;
        }
    }
    // funkcja usuwająca wybrane wiersze
    public int deleteSelectedRows(List<CheckBox> checkBoxes, int startID, boolean f) {
        int j = 0;
        //idziemy od tyłu, aby nie zaburzyć ciągłości listy
        for (int i = checkBoxes.size() - 1; i >= 0; i--) {
            //usuwanie zależne od obecnego trtbu
            if (checkBoxes.get(i).isSelected() && !f) {
                baskets.remove(startID + i);
                j++;
            } else if (checkBoxes.get(i).isSelected() && f) {
                baskets.remove(filteredBaskets.get(startID + i));
                j++;
            }
        }
        return j;
    }
    // funkcja usuwająca wybrane przedmioty z wierszy
    public int deleteSelectedItems(List<CheckBox> checkBoxes, List<String> filtr, int startID) {

        int j = 0;
        for (int i = checkBoxes.size() - 1; i >= 0; i--) {
            if (checkBoxes.get(i).isSelected()) {
                j++;
                int k = filteredId.get(startID + i);
                List<String> list = new ArrayList<>(baskets.get(k));//pojedynczy koszyk jest niemodyfikowalny, bo powstał z list.of
                list.removeAll(filtr);
                baskets.set(k, list);
            }
        }
        return j;
    }
    //funkcje sortujące
    public void sortByLengthUp() {
        baskets.sort(Comparator.comparingInt(List::size));
        if(filteredBaskets.size()>0)
            filteredBaskets.sort(Comparator.comparingInt(List::size));
    }
    public void sortByLengthDown() {
        baskets.sort((o1, o2) -> Integer.compare(o2.size(), o1.size()));
        if(filteredBaskets.size()>0)
            filteredBaskets.sort((o1, o2) -> Integer.compare(o2.size(), o1.size()));
    }
}
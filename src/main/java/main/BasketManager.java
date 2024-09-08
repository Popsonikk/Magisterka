package main;

import javafx.stage.FileChooser;
import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


public class BasketManager {
    private List<List<String>> baskets;
    private List<List<String>> filteredBaskets;



    BasketManager()
    {
        this.filteredBaskets=new ArrayList<>();
        this.baskets=new ArrayList<>();
    }
    public void loadBaskets() throws IOException {
        baskets.clear();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Wybierz plik zawierający listę koszyków");
        File file = fileChooser.showOpenDialog(null);
        BufferedReader reader= new BufferedReader(new FileReader(file));
        String line=reader.readLine();
        while (line!=null)
        {
            baskets.add(List.of(line.split(",")));
            line=reader.readLine();
        }
    }

    public int getFilteredBasketSize()
    {
        return filteredBaskets.size();
    }
    public List<String> getFilteredSingleBasket(int i)
    {
        return filteredBaskets.get(i);
    }

    public int getBasketSize()
    {
        return baskets.size();
    }
    public List<String > getSingleBasket(int i)
    {
        return baskets.get(i);
    }

    public void clearBaskets()
    {
        baskets.clear();
    }

    public void clearFilteredBaskets()
    {
        filteredBaskets.clear();
    }

    public List<List<String>> getBaskets() {
        return baskets;
    }

    public List<List<String>> getFilteredBaskets() {
        return filteredBaskets;
    }

    public void filtrBaskets(List<String> items) {

        filteredBaskets.clear();
        filteredBaskets.addAll(baskets.stream()
                .filter(basket -> new HashSet<>(basket).containsAll(items))
                .toList());
    }





}

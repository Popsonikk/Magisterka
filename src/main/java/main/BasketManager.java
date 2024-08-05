package main;

import javafx.stage.FileChooser;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


public class BasketManager {
    private List<Set<String>> baskets;
    private List<Set<String>> filteredBaskets;



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
            baskets.add(Set.of(line.split(",")));
            line=reader.readLine();
        }
    }

    public int getFilteredBasketSize()
    {
        return filteredBaskets.size();
    }
    public Set<String> getFilteredSingleBasket(int i)
    {
        return filteredBaskets.get(i);
    }

    public int getBasketSize()
    {
        return baskets.size();
    }
    public Set<String > getSingleBasket(int i)
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

    public List<Set<String>> getBaskets() {
        return baskets;
    }

    public List<Set<String>> getFilteredBaskets() {
        return filteredBaskets;
    }

    public void filtrBaskets(String item) {
        filteredBaskets.clear();
        filteredBaskets.addAll(baskets.stream()
                .filter(basket -> basket.contains(item))
                .toList());
    }





}

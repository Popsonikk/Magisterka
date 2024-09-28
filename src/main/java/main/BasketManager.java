package main;

import javafx.scene.control.CheckBox;
import javafx.stage.FileChooser;
import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;


public class BasketManager {
    private List<List<String>> baskets;
    private List<List<String>> filteredBaskets;
    private List<Integer> filteredId;



    BasketManager()
    {
        this.filteredBaskets=new ArrayList<>();
        this.baskets=new LinkedList<>();
        this.filteredId=new ArrayList<>();
    }
    public void loadBaskets() throws IOException {
        baskets.clear();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Wybierz plik zawierający listę koszyków");
        File file = fileChooser.showOpenDialog(null);
        BufferedReader reader= new BufferedReader(new FileReader(file));
        String line;
        line=reader.readLine(); // pominięcie lini nagłówka
        line=reader.readLine();
        while (line!=null)
        {
            baskets.add(List.of(line.split(",")[1].split(";")));
            line=reader.readLine();
        }
    }

    public int getFilteredBasketSize()
    {
        return filteredBaskets.size();
    }


    public int getBasketSize()
    {
        return baskets.size();
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
        filteredId.clear();
        int i=0;
        for(List<String>b:baskets)
        {
            if(new HashSet<>(b).containsAll(items))
            {
                filteredBaskets.add(b);
                filteredId.add(i);
            }
            i++;


        }

    }
    public void deleteSelectedRows(List<CheckBox> checkBoxes,int startID,boolean f)
    {

        for (int i = checkBoxes.size() - 1; i >= 0; i--)
        {

            if (checkBoxes.get(i).isSelected()&&!f)
                baskets.remove(startID + i);
            else if(checkBoxes.get(i).isSelected()&&f)
                baskets.remove(filteredBaskets.get(startID + i));
        }
    }
    public void deleteSelectedItems(List<CheckBox> checkBoxes,List<String> filtr,int startID)
    {

        for (int i = checkBoxes.size() - 1; i >= 0; i--)
        {
            if (checkBoxes.get(i).isSelected())
            {
                int k=filteredId.get(startID+i);
                List<String> list = new ArrayList<>(baskets.get(k));//pojedynczy koszyk jest niemodyfikowalny, bo powstał z list.of
                list.removeAll(filtr);
                baskets.set(k, list);
            }

        }
    }






}

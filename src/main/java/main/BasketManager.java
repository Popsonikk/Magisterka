package main;

import javafx.stage.FileChooser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class BasketManager {
    private List<List<String>> baskets;

    BasketManager()
    {
        this.baskets=new ArrayList<>();
    }
    public void loadBaskets() {
        baskets.clear();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Wybierz plik zawierający algebrę");
        try
        {
            File file = fileChooser.showOpenDialog(null);
            System.out.println("Wybrano plik poprawnie");
            BufferedReader reader= new BufferedReader(new FileReader(file));
            String line=reader.readLine();
            while (line!=null)
            {
                baskets.add(List.of(line.split(",")));
                line=reader.readLine();
            }
        }
        catch (Exception e)
        {
            System.out.println("Wystąpił błąd podczas wczytania pliku: "+e);
        }



    }

    public void showBaskets() {
        for(List<String> bsk: baskets )
        {
            for(String b: bsk )
            {
                System.out.print(b+"; ");
            }
            System.out.print("\n");
        }
    }
    public int getBasketSize()
    {
        return baskets.size();
    }
    public List<String > getSingleBasket(int i)
    {
        return baskets.get(i);
    }



}
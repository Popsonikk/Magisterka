package main;


import javafx.stage.FileChooser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainWindowController {
    private List<List<String>> baskets;


    public void loadBaskets() {
        this.baskets=new ArrayList<>();
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
}

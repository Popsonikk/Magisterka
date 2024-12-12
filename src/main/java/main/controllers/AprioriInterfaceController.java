package main.controllers;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import main.functions.GeneratePattern;
import main.objects.FiltrType;
import main.functions.InterfaceTemplate;
import main.objects.SimplePattern;

import java.io.*;
import java.net.URL;
import java.util.*;

public class AprioriInterfaceController extends InterfaceTemplate<SimplePattern> implements Initializable {

    @Override
    //funkcja inicjująca interfejs
    public void initialize(URL url, ResourceBundle resourceBundle) {
        init();
        createCSVButton();
    }

    //funkcja bezpośrednio generująca widoczną tablicę
    @Override
    protected void createViewTable(List<SimplePattern> patterns)
    {
        int size=patterns.size();
        //warunek sprawdzający koniec listy
        int range=Math.min(size,(startId+boxSize));
        for(int i=startId,j=0;i<range;i++,j++)
        {
            SimplePattern pattern=patterns.get(i);
            //generowanie pojedynczego wiersza tabeli
            HBox box=createTableRow(pattern);
            contentVBox.getChildren().add(box);
        }
        //aktualizacja informacji o widocznej części tablicy
        Text tx= (Text) switchPageBox.lookup("#showInfo");
        tx.setText("Pokazano "+(startId+1)+"-"+(Math.min(startId+boxSize, size)+" z "+size+" elementów"));
    }

    @Override
    protected void loadFromCSV() {
        try
        {

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Wybierz plik zawierający poziomy wsparcia");
            File file = fileChooser.showOpenDialog(null);
            if(file==null)
                throw  new Exception();
            data.clearData();
            mainPane.getChildren().remove(header);
            //zapis nazwy pliku
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            line = reader.readLine(); //header
            try {
                if (line.split(",").length != 3) {
                    createAlert(2, "Błędny format pliku");
                    return;
                }
                line = reader.readLine();
                Double.parseDouble(line.split(",")[1]);
            } catch (Exception e) {
                createAlert(2, "Błędny format pliku");
                return;

            }

            while (line != null) {
                //pobranie danych zgodnie z formatem
                String[] pattern = line.split(",");
                double support = Double.parseDouble(pattern[1]);
                pattern = pattern[2].split(";");
                List<String> list = new ArrayList<>();
                for (String s : pattern)
                    list.add(s.trim().toLowerCase());
                data.getData().add(new SimplePattern(list, support));
                line = reader.readLine();
            }
            createAlert(1, "Dane zostały wczytane poprawnie");
            mainPane.getChildren().add(header);
            createView();
        }
        catch (Exception e)
        {
            createAlert(2,"Nastąpił błąd przy wyborze pliku");
        }
    }
    public void createCSVFIle() throws IOException {
        if (data.getDataSize() == 0) {
            createAlert(2, "Brak danych do zapisania");
            return;
        }
        String filename= GeneratePattern.getFilename();
        if(filename.equals(""))
        {
            createAlert(2,"Brak podanej nazwy pliku!");
            return;
        }
        File file = new File("dane/" + filename + "_supportData.csv");
        //pętla mająca na celu zablokowanie duplikowania nazw plików
        if (file.createNewFile())
            System.out.println("Stworzono plik");
        FileWriter writer = new FileWriter(file);
        writer.write("id,support,pattern\n");
        int i = 0;
        //ręczny zapis do pliku w formie CSV
        for (SimplePattern pattern : data.getData()) {
            writer.write(i + "," + String.format(Locale.US, "%.3f", pattern.getSupport()) + ",");
            List<String> patterns = pattern.getPattern();
            for (int j = 0; j < patterns.size() - 1; j++)
                writer.write(patterns.get(j) + ";");
            writer.write(patterns.get(patterns.size() - 1) + "\n");
            i++;
        }
        writer.close();
        createAlert(1, "Utworzenie pliku zakończone pomyślnie");

    }


    //generowanie pojedynczego wiersza tabeli
    private HBox createTableRow(SimplePattern pattern)
    {
        //tworzenie struktury wiersza
        HBox box=new HBox();
        CheckBox checkBox=new CheckBox();
        checkBoxes.add(checkBox);
        GridPane gridPane=new GridPane();
        gridPane.getColumnConstraints().add(new ColumnConstraints(200.0));
        gridPane.getColumnConstraints().add(new ColumnConstraints(750.0));
        //generowanie pojedynczej kolumny
        HBox box1=createTableColumn(String.format("%.3f", pattern.getSupport()),"basketBorder","basketText");
        box1.getChildren().add(0,checkBox);
        gridPane.add(box1,0,0);
        //generowanie pojedynczej kolumny
        HBox box2=createTableColumn("","basketBorder","basketText");
        box2.getChildren().remove(0);
        //text flow dla zmienienia koloru filtrowanych produktów
        List<Text> textList=createTextList(pattern.getPattern());
        TextFlow textFlow=new TextFlow();
        textFlow.getChildren().addAll(textList);
        box2.getChildren().add(textFlow);
        gridPane.add(box2,1,0);
        box.getChildren().add(gridPane);
        return box;
    }

    @Override
    //generowanie nagłówka tabeli
    protected void createHeader() {
        header=new HBox();
        header.setLayoutX(17.5);
        header.setLayoutY(66.0);
        header.setPrefWidth(950.0);
        header.setPrefHeight(40.0);
        GridPane gridPane=new GridPane();
        gridPane.getColumnConstraints().add(new ColumnConstraints(200.0));
        gridPane.getColumnConstraints().add(new ColumnConstraints(750.0));
        //generowanie kolumny
        HBox supportBox=createTableColumn("Wsparcie","basketHeader","basketHeaderText");
        Button supportButton=createSortButton(Comparator.comparingDouble(SimplePattern::getSupport),
                (o1, o2) -> Double.compare(o2.getSupport(), o1.getSupport()));
        supportBox.getChildren().add(supportButton);
        gridPane.add(supportBox,0,0);
        //generowanie kolumny
        HBox patternBox=createTableColumn("Wzorzec","basketHeader","basketHeaderText");
        Button patternButton=createSortButton(Comparator.comparingInt(o -> o.getPattern().size()),
                (o1, o2) -> Integer.compare(o2.getPattern().size(), o1.getPattern().size()));
        patternBox.getChildren().add(patternButton);
        gridPane.add(patternBox,1,0);
        header.getChildren().add(gridPane);
    }

    //generowanie przycisku do zapisu/odczytu z pliku

    //generowanie przycisku, pozwalającego wybrać rodzaj filtru
    @Override
    protected void createFiltrButton() {
        MenuButton menuButton=makeMenuButtonStyle("Wybierz Filtr",340.0,5.0);
        MenuItem p=new MenuItem("Pokaż wybrane elementy");
        p.setOnAction((event)->filtrData(FiltrType.pattern));
        MenuItem pl=new MenuItem("Pokaż elementy wybranej długości");
        pl.setOnAction((event)->filtrData(FiltrType.patternLen));
        MenuItem su=new MenuItem("Pokaż poziomy wsparcia poniżej");
        su.setOnAction((event)->filtrData(FiltrType.supportDown));
        MenuItem sd=new MenuItem("Pokaż poziomy wsparcia powyżej");
        sd.setOnAction((event)->filtrData(FiltrType.supportUp));
        menuButton.getItems().addAll(p,pl,su,sd);
    }
    @Override
    protected void filtrData(FiltrType ft) {
        data.getFiltrInfo().clearFiltr();
        String s=tx.getText();
        tx.clear();
        if (s.isEmpty()||data.getDataSize()==0) {
            createAlert(2,"Filtrujesz pustą tablicę!");
            return;
        }
        switch(ft){
            case pattern -> {
                filtrSwitchPattern(s);
                filtrPatternItems(data.getFiltrInfo().getPatternFiltrValue());
            }
            case supportUp,supportDown,patternLen -> {
                filtrSwitchMetrics(s,ft);
                filtrSupportLevel(data.getFiltrInfo().getIntegerFiltrValue(),ft);
            }
        }
        if(data.getFilteredDataSize()==0){
            data.getFiltrInfo().clearFiltr();
            createAlert(2,"Brak wierszy spełniających podany warunek");
            return;
        }
        createAlert(1,"Filtrowanie zakończone pomyślnie");
        startId=0;
        createView();

    }
    public void filtrPatternItems(List<String> selectedItems) {
        for (SimplePattern pattern : data.getData()) {
            if (new HashSet<>(pattern.getPattern()).containsAll(selectedItems))
                data.getFilteredData().add(pattern);
        }
    }
    private void filtrPatternLength(int val){
        for (SimplePattern pattern : data.getData()) {
            if (pattern.getPattern().size()==val)
                data.getFilteredData().add(pattern);
        }
    }
    //funkcja filtrująca po poziomie wsparcia, zależne od flagi
    public void filtrSupportLevel(double supp,FiltrType ft) {
        switch (ft){
            case supportDown ->{
                for (SimplePattern pattern : data.getData()) {
                    if ((pattern.getSupport()<=supp))
                        data.getFilteredData().add(pattern);
                }
            }
            case supportUp ->{
                for (SimplePattern pattern : data.getData()) {
                    if ((pattern.getSupport()>=supp))
                        data.getFilteredData().add(pattern);
                }
            }
        }

    }

    protected void refresh()
    {
        data.getFilteredData().clear();
        switch(data.getFiltrInfo().getFiltrType()){
            case pattern -> filtrPatternItems(data.getFiltrInfo().getPatternFiltrValue());
            case patternLen -> filtrPatternLength((int) data.getFiltrInfo().getIntegerFiltrValue());
            case supportDown -> filtrSupportLevel(data.getFiltrInfo().getIntegerFiltrValue(),FiltrType.supportDown);
            case supportUp -> filtrSupportLevel(data.getFiltrInfo().getIntegerFiltrValue(),FiltrType.supportUp);
        }
    }
    public List<String> getProductList()
    {
        List<String> res=new ArrayList<>();
        data.getData().sort(Comparator.comparingInt(o -> o.getPattern().size()));
        for(SimplePattern l:data.getData())
        {
            if(l.getPattern().size()>1)
                break;
            res.add(l.getPattern().get(0));
        }
        return  res;
    }



}

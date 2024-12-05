package main.controllers;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import main.objects.FiltrType;
import main.functions.InterfaceTemplate;

import java.io.*;
import java.net.URL;
import java.util.*;

public class BasketInterfaceController extends InterfaceTemplate<List<String>> implements Initializable {

    @Override
    //funkcja inicjalizująca interfejs
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //generowanie przycisków
        init();
        MenuItem item3=new MenuItem("Usuń filtrowane przedmioty z zaznaczonych wierszy");
        item3.setOnAction(actionEvent -> deleteRows());
        optionsButton.getItems().add(item3);
    }
    @Override
    protected void createViewTable(List<List<String>> baskets)
    {
        int size=baskets.size();
        int range=Math.min(size,(startId+boxSize));
        for(int i=startId;i<range;i++)
        {
            List<String> basket=baskets.get(i);
            //generowanie pojedynczego wiersza
            HBox box=createBox(createTextList(basket));
            contentVBox.getChildren().add(box);
        }
        //aktualizacja informacji o widocznej części tablicy
        Text tx= (Text) switchPageBox.lookup("#showInfo");
        tx.setText("Pokazano "+(startId+1)+"-"+(Math.min(startId+boxSize, size)+" z "+size+" elementów"));
    }

    //pojedyncza komórka wiersza
    private HBox createBox(List<Text> textList)
    {
        HBox box=new HBox();
        CheckBox checkBox=new CheckBox();
        checkBoxes.add(checkBox);
        box.getStyleClass().add("basketBorder");
        //text flow dla zmienienia koloru filtrowanych produktów
        TextFlow textFlow=new TextFlow();
        textFlow.getChildren().addAll(textList);
        box.getChildren().addAll(checkBox,textFlow);
        return  box;
    }
    @Override
    public void loadFromCSV() throws IOException {
        data.clearData();
        mainPane.getChildren().remove(header);
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Wybierz plik zawierający listę koszyków");
        File file = fileChooser.showOpenDialog(null);
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        line = reader.readLine(); // pominięcie lini nagłówka
        try {
            if(!line.split(",")[0].equals("id")||!line.split(",")[1].equals("basket"))
            {
                createAlert(2,"Błędny format pliku");
                return;
            }
        }
        catch (Exception e){
            createAlert(2,"Błędny format pliku");
            return;

        }
        line = reader.readLine();
        while (line != null) {
            //formatowanie danych w celu uniknięcia tworzenia przez list.of
            String[]b=line.split(",")[1].split(";");
            List<String> list=new ArrayList<>();
            for(String s:b)
                list.add(s.trim().toLowerCase());
            data.getData().add(list);
            line = reader.readLine();
        }
        mainPane.getChildren().add(header);
        createView();
        createAlert(1,"Koszyki zostały wczytane poprawnie");

    }

    //funkcja filtrującą tablicę
    @Override
    protected void filtrData(FiltrType ft) {
        //sczytanie filtru i jego zapis do tablicy
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
            case patternLen -> {
                filtrSwitchMetrics(s,ft);
                filtrPatternLength(data.getFiltrInfo().getIntegerFiltrValue());
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

    @Override
    protected void createFiltrButton() {
        MenuButton menuButton=makeMenuButtonStyle("Wybierz Filtr",340.0,5.0);
        MenuItem item=new MenuItem("Pokaż wybrane elementy");
        item.setOnAction((event)->filtrData(FiltrType.pattern));
        MenuItem item1=new MenuItem("Pokaż elementy wybranej długości");
        item1.setOnAction((event)->filtrData(FiltrType.patternLen));
        menuButton.getItems().addAll(item,item1);
    }

    @Override
    protected void createCSVFIle() throws IOException {

    }

    @Override
    //funkcja tworząca nagłówek tablicy
    protected void createHeader() {
        header=new HBox();
        header.setLayoutX(17.5);
        header.setLayoutY(65.0);
        header.setPrefWidth(950.0);
        header.getStyleClass().add("basketHeader");
        Text text=new Text("Koszyk");
        text.getStyleClass().add("basketHeaderText");
        Button button=createSortButton(Comparator.comparingInt(List::size),(o1, o2) -> Integer.compare(o2.size(), o1.size()));
        header.getChildren().addAll(text,button);
    }


    //funkcja usuwająca wybrane przedmioty z wiersza
    private void deleteRows()
    {
        boolean isAnyChecked = checkBoxes.stream().anyMatch(CheckBox::isSelected);
        if(!isAnyChecked) {
            createAlert(2,"Nie zaznaczyłeś żadnego checkBoxa!");
            return;
        }
        if (data.getFiltrInfo().getFiltrType()==FiltrType.none||data.getFiltrInfo().getPatternFiltrValue().isEmpty()) {
            createAlert(2,"Brak filtru!");
            return;
        }
        for (int i = checkBoxes.size() - 1; i >= 0; i--) {
            if (checkBoxes.get(i).isSelected()) {
                List<String> list=new ArrayList<>(data.getFilteredData().get(i));
                data.getData().remove(list);
                data.getFilteredData().remove(list);
                list.removeAll(data.getFiltrInfo().getPatternFiltrValue());
                if(!list.isEmpty())
                {
                    data.getData().add(list);
                    data.getFilteredData().add(list);
                }
            }
        }
        createView();
        createAlert(1,"Wybrane przedmioty zostały usunięte z koszyków");
    }

    private void filtrPatternItems(List<String> selectedItems){
        for (List<String> b : data.getData()) {
            if (new HashSet<>(b).containsAll(selectedItems))
                data.getFilteredData().add(b);
        }
    }
    private void filtrPatternLength(double val){
        for (List<String> b : data.getData()) {
            if (b.size()==val)
                data.getFilteredData().add(b);
        }
    }

    @Override
    protected void refresh()
    {
        data.getFilteredData().clear();
        switch(data.getFiltrInfo().getFiltrType()){
            case pattern -> filtrPatternItems(data.getFiltrInfo().getPatternFiltrValue());
            case patternLen -> filtrPatternLength(data.getFiltrInfo().getIntegerFiltrValue());
        }
    }
    public boolean isHeaderActive()
    {
        return mainPane.getChildren().contains(header);
    }

}

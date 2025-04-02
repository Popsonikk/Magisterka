package main.functions;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import main.objects.FiltrType;
import main.objects.InterfaceData;

import java.io.IOException;
import java.util.*;

public abstract class InterfaceTemplate <T>{

    @FXML
    public VBox contentVBox;
    @FXML
    public ScrollPane pane;
    @FXML
    public Pane mainPane;
    @FXML
    public HBox selectSizeBox;
    @FXML
    public HBox switchPageBox;
    @FXML
    //pole do wpisania filtru
    public TextField tx;
    protected HBox header;
    protected List<CheckBox> checkBoxes;
    protected int boxSize;
    //zmienna pokazująca start okna
    protected int startId;
    protected Scene mainScene;
    protected Stage mainStage;
    protected InterfaceData<T> data;
    protected MenuButton optionsButton;
    public void setMainScene(Scene mainScene) {this.mainScene = mainScene;}
    public void setMainStage(Stage mainStage) {this.mainStage = mainStage;}
    public void back() {
        mainPane.getChildren().remove(header);
        mainStage.setScene(mainScene);}

    public InterfaceData<T> getData() {
        return data;
    }

    //funkcja, generująca przycisk wyboru rozmiaru widocznego okna
    protected MenuButton createSizeButton()
    {
        MenuButton menuButton=new MenuButton();
        menuButton.setText("100");
        EventHandler<ActionEvent> event = e -> {
            MenuItem item = (MenuItem) e.getSource();
            boxSize = Integer.parseInt(item.getText());
            menuButton.setText(item.getText());
            createView();
        };
        MenuItem item1=new MenuItem("10");
        item1.setOnAction(event);
        MenuItem item2=new MenuItem("25");
        item2.setOnAction(event);
        MenuItem item3=new MenuItem("50");
        item3.setOnAction(event);
        MenuItem item4=new MenuItem("100");
        item4.setOnAction(event);
        MenuItem item5=new MenuItem("250");
        item5.setOnAction(event);
        menuButton.getItems().addAll(item1,item2,item3,item4,item5);
        menuButton.getStyleClass().add("boxButton");
        return menuButton;
    }
    //funkcja generująca okno wyboru rozmiar widocznego okna
    protected void createSelectSizeBox()
    {
        selectSizeBox.getStyleClass().add("infoBox");
        Text text1=new Text();
        text1.setText("Pokazuj");
        Text text2=new Text("elementów");
        text1.getStyleClass().add("infoBoxText");
        text2.getStyleClass().add("infoBoxText");
        MenuButton menuButton=createSizeButton();
        selectSizeBox.getChildren().addAll(text1,menuButton,text2);
    }
    //funkcja inicjalizująca zmienne dla interfejsu
    protected void init()
    {
        data=new InterfaceData<>();
        checkBoxes=new ArrayList<>();
        boxSize=100;
        startId=0;
        createSelectSizeBox();
        createFiltrButton();
        createSwitchPageBox();
        createHeader();
        createOptionButton();
        pane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        pane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
    }
    //funkcja pozwalająca przechodzić między kartami interfejsu
    protected void createSwitchPageBox()
    {
        switchPageBox.getStyleClass().add("infoBox");
        Button backButton = new Button("<");
        backButton.getStyleClass().add("boxButton");
        Button nextButton= new Button(">");
        nextButton.getStyleClass().add("boxButton");
        nextButton.setId("nButt");
        Text text=new Text("Pokazano 0 z 0 elementów");
        text.getStyleClass().add("infoBoxText");
        text.setId("showInfo");
        backButton.setOnAction(e -> {
            if(startId==0)
                return;
            startId=Math.max(0,startId-boxSize);
            createView();

        });
        nextButton.setOnAction(e -> {
            if((startId+boxSize>= data.getDataSize())||(data.getFiltrInfo().isFiltered()&&startId+boxSize>= data.getFilteredDataSize()))
                return;
            startId+=boxSize;
            createView();
        });
        switchPageBox.getChildren().addAll(backButton,text,nextButton);
    }
    //funkcja, ustawiająca wygląd przycisków rozwijanych interfejsów
    protected MenuButton makeMenuButtonStyle(String name,double x,double y){
        MenuButton menuButton=new MenuButton();
        menuButton.setPrefSize(150.0,50.0);
        menuButton.setText(name);
        menuButton.setLayoutX(x);
        menuButton.setLayoutY(y);
        menuButton.getStyleClass().add("filterButton");
        menuButton.setOnShowing(event -> menuButton.setStyle("-fx-background-color: #2e79ba; -fx-border-style: solid;"));
        menuButton.setOnHidden(event -> menuButton.setStyle("-fx-background-color: #5fc9f3; -fx-border-style: dashed;"));
        mainPane.getChildren().add(menuButton);
        return menuButton;
    }
    //zaznaczenie wszystkich dostępnych boxów
    protected void selectAllBoxes()
    {
        for(CheckBox box:checkBoxes)
            box.setSelected(true);
    }
    //generowanie listy obiektów text z różnymi kolorami w zależności od filtru
    protected List<Text> createTextList(List<String> pattern)
    {
        List<Text> textList=new ArrayList<>();
        for(String s:pattern)
        {
            Text text=new Text(s+"; ");
            if(data.getFiltrInfo().getPatternFiltrValue().contains(s))
                text.getStyleClass().add("basketTextFiltr");
            else
                text.getStyleClass().add("basketText");
            textList.add(text);
        }
        return textList;
    }
    //wygenerowanie nagłówka przy pierwszym wczytaniu wartości
    public void showTable()
    {
        mainPane.getChildren().add(header);
        createView();
    }
    //funkcja generująca podstawową komórkę kolumny
    protected HBox createTableColumn(String name,String styleName,String styleTextName)
    {
        HBox box=new HBox();
        Text text=new Text(name);
        text.setId("text");
        text.getStyleClass().add(styleTextName);
        box.getStyleClass().add(styleName);
        box.getChildren().addAll(text);
        return box;
    }
    //usunięcie nagłówka tabeli, przy wyczyszczeniu danych
    public void deleteHeader()
    {
        mainPane.getChildren().remove(header);
    }
    public void clearInterface(){
        mainPane.getChildren().remove(header);
        contentVBox.getChildren().clear();
        checkBoxes.clear();
        data.getFiltrInfo().clearFiltr();
        startId=0;
        data.clearData();
        data.clearFilteredData();
        createAlert(1,"Usunięcie koszyków zakończone pomyślnie");
        Text tx= (Text) switchPageBox.lookup("#showInfo");
        tx.setText("Pokazano 0 z 0 elementów");
    }
    public void createAlert(int type,String value){
        switch (type) {
            case 1 -> {
                Alert a = new Alert(Alert.AlertType.INFORMATION, value);
                a.show();
            }
            case 2 -> {
                Alert a = new Alert(Alert.AlertType.ERROR, value);
                a.show();
            }
        }
    }
    protected void clearFilter() {
        data.getFiltrInfo().clearFiltr();
        data.clearFilteredData();
        createView();
        createAlert(1,"Filtry wyczyszczone pomyślnie, przywrócono podstawową listę koszyków");
    }
    public void createView(){
        contentVBox.getChildren().clear();
        checkBoxes.clear();
        if(data.getDataSize()==0)
        {
            Text tx= (Text) switchPageBox.lookup("#showInfo");
            tx.setText("Pokazano 0 z 0 koszyków");
            return;
        }
        if(data.getFiltrInfo().isFiltered())
            createViewTable(data.getFilteredData());
        else
            createViewTable(data.getData());
    }
    protected abstract void createFiltrButton();
    protected  void createOptionButton()
    {
        optionsButton=makeMenuButtonStyle("Zarządzaj filtrami",505.0,5.0);
        MenuItem item1=new MenuItem("Wyczyść filtry");
        item1.setOnAction(actionEvent -> clearFilter());
        MenuItem item2=new MenuItem("Usuń zaznaczone wiersze");
        item2.setOnAction(actionEvent -> deleteItems());
        MenuItem item4=new MenuItem("Zaznacz wszystkie boxy");
        item4.setOnAction(actionEvent -> selectAllBoxes());
        optionsButton.getItems().addAll(item1,item2,item4);
    }
    //funkcja usuwająca wybrane wiersze
    protected void deleteItems()
    {
        boolean isAnyChecked = checkBoxes.stream().anyMatch(CheckBox::isSelected);
        if(!isAnyChecked) {
            createAlert(2,"Nie zaznaczyłeś żadnego checkBoxa!");
            return;
        }

        //idziemy od tyłu, aby nie zaburzyć ciągłości listy
        for (int i = checkBoxes.size() - 1; i >= 0; i--) {
            //usuwanie zależne od obecnego trtbu
            if (checkBoxes.get(i).isSelected() && !data.getFiltrInfo().isFiltered())
                data.getData().remove(startId + i);
            else if (checkBoxes.get(i).isSelected() && data.getFiltrInfo().isFiltered()) {
                data.getData().remove(data.getFilteredData().get(startId + i));
                data.getFilteredData().remove(startId + i);
            }
        }
        createView();
        createAlert(1,"Wybrane koszyki zostały usunięte");
    }
    protected Button createSortButton(Comparator<T> ascendingComparator,Comparator<T> descendingComparator)
    {
        Button button=new Button("↑");
        button.getStyleClass().add("boxButton");
        //przycisk filtrujący po długości koszyka
        button.setOnAction((event)->{
            if(data.getDataSize()==0)
                return;
            if(Objects.equals(button.getText(), "↑"))
            {
                data.getData().sort(ascendingComparator);
                button.setText("↓");
            } else{
                data.getData().sort(descendingComparator);
                button.setText("↑");
            }
            refresh();
            createView();
        });
        return button;
    }
    protected void filtrSwitchPattern(String s){
        String[] items = s.split("[:,;]");
        List<String> selectedItems=new ArrayList<>();
        for (String item : items)
            selectedItems.add(item.trim());
        data.getFiltrInfo().setPatternFiltrValue(selectedItems);
        data.getFiltrInfo().setFiltered(true);
        data.getFiltrInfo().setFiltrType(FiltrType.pattern);
        data.clearFilteredData();
    }

    protected void filtrSwitchMetrics(String s,FiltrType ft)
    {
        double val=Double.parseDouble(s);
        data.getFiltrInfo().setIntegerFiltrValue(val);
        data.getFiltrInfo().setFiltered(true);
        data.getFiltrInfo().setFiltrType(ft);
        data.clearFilteredData();
    }
    protected void createCSVButton()
    {
        MenuButton menuButton=makeMenuButtonStyle("Zarządzaj CSV",670.0,5.0);
        MenuItem item1=new MenuItem("Zapisz do pliku");
        item1.setOnAction((event)-> {
            try {
                createCSVFIle(data.getData());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        MenuItem item3=new MenuItem("Zapisz filtrowane dane do pliku");
        item3.setOnAction((event)-> {
            try {
                createCSVFIle(data.getFilteredData());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        MenuItem item2=new MenuItem("Wczytaj z pliku");
        item2.setOnAction((event)-> {
            try {
                loadFromCSV();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }});
        menuButton.getItems().addAll(item1,item2,item3);
    }
    public boolean isHeaderActive()
    {
        return mainPane.getChildren().contains(header);
    }



    protected abstract void createCSVFIle(List<T> data)throws IOException;
    protected abstract void createHeader();
    protected abstract void refresh();
    protected abstract void createViewTable(List<T> patterns);
    protected abstract void loadFromCSV() throws IOException;
    protected abstract void filtrData(FiltrType ft);

}

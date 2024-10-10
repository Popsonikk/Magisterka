package main;

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

import java.util.ArrayList;
import java.util.List;

public abstract class InterfaceTemplate {

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
    protected boolean filtered;
    protected List<String> filtr;
    protected Scene mainScene;
    protected Stage mainStage;
    public void setMainScene(Scene mainScene) {this.mainScene = mainScene;}
    public void setMainStage(Stage mainStage) {this.mainStage = mainStage;}
    public void back() {mainStage.setScene(mainScene);}
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
        filtered =false;
        filtr=new ArrayList<>();
        checkBoxes=new ArrayList<>();
        boxSize=100;
        startId=0;
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
        switchPageBox.getChildren().addAll(backButton,text,nextButton);
    }
    //funkcja, ustawiająca wygląd przycisków rozwijanych interfejsów
    protected MenuButton makeMenuButtonStyle(){
        MenuButton menuButton=new MenuButton();
        menuButton.setPrefSize(150.0,50.0);
        menuButton.getStyleClass().add("filterButton");
        menuButton.setOnShowing(event -> menuButton.setStyle("-fx-background-color: #2e79ba; -fx-border-style: solid;"));
        menuButton.setOnHidden(event -> menuButton.setStyle("-fx-background-color: #5fc9f3; -fx-border-style: dashed;"));
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
            if(filtr.contains(s))
                text.getStyleClass().add("basketTextFiltr");
            else
                text.getStyleClass().add("basketText");
            textList.add(text);
        }
        return textList;
    }
    protected abstract void deleteRows();
    protected abstract void clearFilter();
    protected abstract void createView();
    protected abstract void createFiltrButton();
    protected abstract void createHeader();

}

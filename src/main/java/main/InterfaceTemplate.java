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
    public TextField tx;


    protected List<CheckBox> checkBoxes;
    protected int boxSize;
    protected int startId;

    protected boolean filtered;

    protected List<String> filtr;
    protected Scene mainScene;

    protected Stage mainStage;



    public void setMainScene(Scene mainScene) {
        this.mainScene = mainScene;
    }

    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }


    public void back() {
        mainStage.setScene(mainScene);
    }
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
    protected abstract void createView();
    protected void createSelectSizeBox()
    {
        selectSizeBox.getStyleClass().add("infoBox");
        Text text1=new Text();
        text1.setText("Pokazuj");
        Text text2=new Text("Koszyków");
        text1.getStyleClass().add("infoBoxText");
        text2.getStyleClass().add("infoBoxText");
        MenuButton menuButton=createSizeButton();
        selectSizeBox.getChildren().addAll(text1,menuButton,text2);
    }

    protected void init()
    {
        filtered =false;
        filtr=new ArrayList<>();
        checkBoxes=new ArrayList<>();
        boxSize=100;
        startId=0;
        pane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        pane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
    }
    protected abstract void createFiltrButton();

}

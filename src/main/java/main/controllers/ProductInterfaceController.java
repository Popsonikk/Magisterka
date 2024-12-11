package main.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;
import main.objects.SimplePattern;

import java.io.*;
import java.net.URL;

import java.util.*;

public class ProductInterfaceController  implements Initializable {
    @FXML
    public Pane mainPane;
    @FXML
    public VBox contentVBox;
    private Stage mainStage;
    private Scene mainScene;
    @FXML
    public TextField tx;
    private List<Pair<String,String>> products;
    private HBox header;



    public void setMainStage(Stage stage) {
        this.mainStage = stage;
    }

    public void setMainScene(Scene mainScene) {
        this.mainScene = mainScene;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        products=new ArrayList<>();
        createHeader();
        createMenuButton();
    }

    public void back(ActionEvent actionEvent) {
        mainStage.setScene(mainScene);
    }

    protected void createHeader() {
        header=new HBox();
        header.setLayoutX(17.5);
        header.setLayoutY(66.0);
        header.setPrefWidth(950.0);
        header.setPrefHeight(40.0);
        GridPane gridPane=new GridPane();
        gridPane.getColumnConstraints().add(new ColumnConstraints(475.0));
        gridPane.getColumnConstraints().add(new ColumnConstraints(475.0));
        //generowanie kolumny
        HBox supportBox=createTableColumn("Produkt","basketHeader","basketHeaderText");
        gridPane.add(supportBox,0,0);
        //generowanie kolumny
        HBox patternBox=createTableColumn("Kategoria","basketHeader","basketHeaderText");
        gridPane.add(patternBox,1,0);
        header.getChildren().add(gridPane);
    }
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
    private void createView()
    {
        for(Pair<String,String>p:products)
            createRow(p.getKey(), p.getValue());

    }


    public void clearInterface(ActionEvent actionEvent) {
        contentVBox.getChildren().clear();
        mainPane.getChildren().remove(header);
        createAlert(1,"Baza została wyczyszczona");
    }

    public void loadFromCSV(ActionEvent actionEvent) throws IOException {
        products.clear();
        contentVBox.getChildren().clear();
        FileChooser fileChooser=new FileChooser();
        fileChooser.setTitle("Wybierz plik zawierający produkty");
        File file = fileChooser.showOpenDialog(null);
        //zapis nazwy pliku
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        line= reader.readLine(); //header
        try {
            if(!Objects.equals(line.split(",")[0], "id") ||
                    !Objects.equals(line.split(",")[1], "product") || !Objects.equals(line.split(",")[2], "category"))
            {
                createAlert(2,"Błędny format pliku");
                return;
            }
            line= reader.readLine();
        }
        catch (Exception e){
            createAlert(2,"Błędny format pliku");
            return;

        }

        while (line!=null)
        {
            //pobranie danych zgodnie z formatem
            products.add(new Pair<>(line.split(",")[1].toLowerCase().trim(),line.split(",")[2].toLowerCase().trim()));
            line= reader.readLine();
        }
        createAlert(1,"Dane zostały wczytane poprawnie");
        mainPane.getChildren().add(header);
        createView();
    }

    private void addItem() {
        if(tx.getText().isEmpty())
        {
            createAlert(2,"Brak danych!");
            return;
        }
        if(tx.getText().split(",").length!=2)
        {
            createAlert(2,"Zły format danych!");
            return;
        }
        String item=tx.getText().split(",")[0].toLowerCase().trim();
        String cat=tx.getText().split(",")[1].toLowerCase().trim();
        tx.clear();
        if(products.isEmpty())
            mainPane.getChildren().add(header);
        products.add(new Pair<>(item,cat));
        createRow(item, cat);
        createAlert(1,"Dodano produkt");
    }
    private void deleteItem()
    {
        if(tx.getText().isEmpty())
        {
            createAlert(2,"Brak danych!");
            return;
        }
        String item=tx.getText().toLowerCase().trim();
        tx.clear();
        for(Pair<String,String>p:products)
        {
            if(p.getKey().equals(item))
            {
                products.remove(p);
                break;
            }
        }
        contentVBox.getChildren().clear();
        createView();
        createAlert(1,"Usunięto przedmiot");
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
    private void createRow(String it,String cat)
    {
        HBox box=new HBox();
        GridPane gridPane=new GridPane();
        gridPane.getColumnConstraints().add(new ColumnConstraints(475.0));
        gridPane.getColumnConstraints().add(new ColumnConstraints(475.0));
        //generowanie kolumny
        HBox supportBox=createTableColumn(it,"basketBorder","basketText");
        gridPane.add(supportBox,0,0);
        //generowanie kolumny
        HBox patternBox=createTableColumn(cat,"basketBorder","basketText");
        gridPane.add(patternBox,1,0);
        box.getChildren().add(gridPane);
        contentVBox.getChildren().add(box);
    }
    private void createMenuButton()
    {
        MenuButton menuButton=new MenuButton();
        menuButton.setPrefSize(150.0,50.0);
        menuButton.setText("Wybierz opcję");
        menuButton.setLayoutX(290);
        menuButton.setLayoutY(5);
        menuButton.getStyleClass().add("filterButton");
        menuButton.setOnShowing(event -> menuButton.setStyle("-fx-background-color: #2e79ba; -fx-border-style: solid;"));
        menuButton.setOnHidden(event -> menuButton.setStyle("-fx-background-color: #5fc9f3; -fx-border-style: dashed;"));
        MenuItem add=new MenuItem("Dodaj");
        add.setOnAction(e->addItem());
        MenuItem del=new MenuItem("Usuń");
        del.setOnAction(e->deleteItem());
        menuButton.getItems().addAll(add,del);
        mainPane.getChildren().add(menuButton);
    }

}

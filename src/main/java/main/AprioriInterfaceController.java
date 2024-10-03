package main;

import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class AprioriInterfaceController extends InterfaceTemplate implements Initializable {

    private AprioriManager aprioriManager;

    public void setAprioriManager(AprioriManager aprioriManager) {
        this.aprioriManager = aprioriManager;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        init();
        createSelectSizeBox();
        createSwitchPageBox();
        createCSVButton();
        createFiltrButton();
        createOptionButton();
        createHeader();

        Button nextButton=(Button) switchPageBox.lookup("#nButt");

        nextButton.setOnAction(e -> {
            if(startId+boxSize>= aprioriManager.getAprioriSize())
                return;
            startId+=boxSize;
            createView();

        });
    }
    @Override
    protected void createView() {

    }

    @Override
    protected void createFiltrButton() {
        MenuButton menuButton=makeMenuButtonStyle();
        menuButton.setText("Zarządzaj filtrami");
        menuButton.setLayoutX(505.0);
        menuButton.setLayoutY(5.0);
        mainPane.getChildren().add(menuButton);

    }

    @Override
    protected void createHeader() {
        header=new HBox();
        header.setLayoutX(17.0);
        header.setLayoutY(66.0);
        header.setPrefWidth(950.0);
        header.setPrefHeight(40.0);
        //header.getStyleClass().add("basketHeader");
        GridPane gridPane=new GridPane();
        gridPane.getColumnConstraints().add(new ColumnConstraints(200.0));
        gridPane.getColumnConstraints().add(new ColumnConstraints(750.0));
        gridPane.add(createColumn("Wsparcie"),0,0);
        gridPane.add(createColumn("Wzorzec"),1,0);
        header.getChildren().add(gridPane);
        mainPane.getChildren().add(header);
    }

    private HBox createColumn(String name)
    {
        HBox box=new HBox();
        Text text=new Text(name);
        text.getStyleClass().add("basketHeaderText");
        Button button=new Button("↑");
        button.getStyleClass().add("boxButton");
        box.getStyleClass().add("basketHeader");
        box.getChildren().addAll(text,button);
        return box;
    }
    private void createCSVButton()
    {
        MenuButton menuButton=makeMenuButtonStyle();
        menuButton.setText("Zarządzaj CSV");
        menuButton.setLayoutX(670.0);
        menuButton.setLayoutY(5.0);
        MenuItem item1=new MenuItem("Zapisz do pliku");
        item1.setOnAction((event)->aprioriManager.createCSVFIle());
        MenuItem item2=new MenuItem("Wczytaj z pliku");
        item2.setOnAction((event)->aprioriManager.loadFromCSV());
        menuButton.getItems().addAll(item1,item2);
        mainPane.getChildren().add(menuButton);

    }
    protected void createOptionButton() {
        MenuButton menuButton=makeMenuButtonStyle();
        menuButton.setText("Wybierz Filtr");
        menuButton.setLayoutX(340.0);
        menuButton.setLayoutY(5.0);
        mainPane.getChildren().add(menuButton);

    }




}

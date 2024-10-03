package main;

import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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
        contentVBox.getChildren().clear();
        checkBoxes.clear();
        if(aprioriManager.getSupportListSize()==0)
        {
            Text tx= (Text) switchPageBox.lookup("#showInfo");
            tx.setText("Pokazano 0 z 0 elementów");
            return;
        }
        createViewTable(aprioriManager.getSupportList());

    }
    private void createViewTable(List<SimplePattern> patterns)
    {
        int size=patterns.size();
        int range=Math.min(size,(startId+boxSize));
        for(int i=startId,j=0;i<range;i++,j++)
        {
            SimplePattern pattern=patterns.get(i);

           HBox box=createTableRow(pattern);
           contentVBox.getChildren().add(box);
        }
        Text tx= (Text) switchPageBox.lookup("#showInfo");
        tx.setText("Pokazano "+(startId+1)+"-"+(Math.min(startId+boxSize, size)+" z "+size+" elementów"));
    }
    private HBox createTableRow(SimplePattern pattern)
    {
        HBox box=new HBox();
        CheckBox checkBox=new CheckBox();
        checkBoxes.add(checkBox);
        GridPane gridPane=new GridPane();
        gridPane.getColumnConstraints().add(new ColumnConstraints(200.0));
        gridPane.getColumnConstraints().add(new ColumnConstraints(750.0));
        HBox box1=new HBox();
        box1.getStyleClass().add("basketBorder");
        Text text1 = new Text(String.format("%.3f", pattern.getSupport()));
        text1.getStyleClass().add("basketText");
        box1.getChildren().add(text1);
        gridPane.add(box1,0,0);
        HBox box2=new HBox();
        box2.getStyleClass().add("basketBorder");
        StringBuilder builder=new StringBuilder();
        for(String s: pattern.getPattern())
            builder.append(s).append("; ");
        Text text2=new Text(builder.toString());
        text2.getStyleClass().add("basketText");
        box2.getChildren().add(text2);
        gridPane.add(box2,1,0);
        box.getChildren().add(gridPane);
        return box;
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
        GridPane gridPane=new GridPane();
        gridPane.getColumnConstraints().add(new ColumnConstraints(200.0));
        gridPane.getColumnConstraints().add(new ColumnConstraints(750.0));
        gridPane.add(createHeaderColumn("Wsparcie"),0,0);
        gridPane.add(createHeaderColumn("Wzorzec"),1,0);
        header.getChildren().add(gridPane);

    }


    private HBox createHeaderColumn(String name)
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
        item2.setOnAction((event)-> {
            aprioriManager.loadFromCSV();
            showTable();});

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
    public void showTable()
    {
        mainPane.getChildren().add(header);
        createView();
    }
    public void deleteHeader()
    {
        mainPane.getChildren().remove(header);
    }





}

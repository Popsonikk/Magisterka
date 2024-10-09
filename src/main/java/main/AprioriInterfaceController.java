package main;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
            if((startId+boxSize>= aprioriManager.getSupportListSize())||(filtered&&startId+boxSize>= aprioriManager.getFilteredListSize()))
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
        if(!filtered)
            createViewTable(aprioriManager.getSupportList());
        else
            createViewTable(aprioriManager.getFiltredList());
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
        HBox box1=createTableColumn(String.format("%.3f", pattern.getSupport()),"basketBorder","basketText");
        box1.getChildren().add(0,checkBox);
        gridPane.add(box1,0,0);
        HBox box2=createTableColumn("","basketBorder","basketText");
        box2.getChildren().remove(0);
        List<Text> textList=createTextList(pattern.getPattern());
        TextFlow textFlow=new TextFlow();
        textFlow.getChildren().addAll(textList);
        box2.getChildren().add(textFlow);
        gridPane.add(box2,1,0);
        box.getChildren().add(gridPane);
        return box;
    }

    protected void createOptionButton() {
        MenuButton menuButton=makeMenuButtonStyle();
        menuButton.setText("Zarządzaj filtrami");
        menuButton.setLayoutX(505.0);
        menuButton.setLayoutY(5.0);
        MenuItem item=new MenuItem("Zaznacz wszystkie boxy");
        item.setOnAction(actionEvent -> selectAllBoxes());
        MenuItem item2=new MenuItem("Usuń zaznaczone wiersze");
        item2.setOnAction(actionEvent -> deleteRows());
        MenuItem item3=new MenuItem("Wyczyść filtr");
        item3.setOnAction(actionEvent -> clearFilter());
        menuButton.getItems().addAll(item,item2,item3);
        mainPane.getChildren().add(menuButton);
    }
    protected void clearFilter() {
        filtered =false;
        filtr.clear();
        aprioriManager.clearFilteredList();
        createView();
        Alert a=new Alert(Alert.AlertType.INFORMATION);
        a.setContentText("Filtry wyczyszczone pomyślnie, przywrócono podstawową listę koszyków");
        a.show();

    }
    @Override
    protected void deleteRows() {
        Alert a=new Alert(Alert.AlertType.INFORMATION);
        int j= aprioriManager.deleteSelectedRows(checkBoxes,startId,filtered);
        if(j==0)
        {
            a.setContentText("Brak wybranych elementów do usunięcia");
            a.show();
            return;
        }
        createView();
        a.setContentText("Wybrane elementów zostały usunięte");
        a.show();
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
        HBox box1=createTableColumn("Wsparcie","basketHeader","basketHeaderText");
        Button button1=new Button("↑");
        button1.getStyleClass().add("boxButton");
        button1.setOnAction((event)->{
            if(aprioriManager.getSupportListSize()==0)
                return;
            if(Objects.equals(button1.getText(), "↑"))
            {
                aprioriManager.sortBySupportUp();
                button1.setText("↓");
            } else{
                aprioriManager.sortBySupportDown();
                button1.setText("↑");
            }
            createView();
        });
        box1.getChildren().add(button1);
        gridPane.add(box1,0,0);
        HBox box2=createTableColumn("Wzorzec","basketHeader","basketHeaderText");
        Button button2=new Button("↑");
        button2.getStyleClass().add("boxButton");
        button2.setOnAction((event)->{
            if(aprioriManager.getSupportListSize()==0)
                return;
            if(Objects.equals(button2.getText(), "↑"))
            {
                aprioriManager.sortByPatternUp();
                button2.setText("↓");
            } else{
                aprioriManager.sortByPatternDown();
                button2.setText("↑");
            }
            createView();
        });
        box2.getChildren().add(button2);
        gridPane.add(box2,1,0);
        header.getChildren().add(gridPane);
    }
    private HBox createTableColumn(String name,String styleName,String styleTextName)
    {
        HBox box=new HBox();
        Text text=new Text(name);
        text.setId("text");
        text.getStyleClass().add(styleTextName);
        box.getStyleClass().add(styleName);
        box.getChildren().addAll(text);
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
    @Override
    protected void createFiltrButton() {
        MenuButton menuButton=makeMenuButtonStyle();
        menuButton.setText("Wybierz Filtr");
        menuButton.setLayoutX(340.0);
        menuButton.setLayoutY(5.0);
        MenuItem menuItem=new MenuItem("Pokaż wybrane elementy");
        menuItem.setOnAction((event)->filtrPatternItems());
        MenuItem item=new MenuItem("Pokaż poziomy wsparcia poniżej");
        item.setOnAction((event)->filtrSupportLevel(true));
        MenuItem item1=new MenuItem("Pokaż poziomy wsparcia powyżej");
        item1.setOnAction((event)->filtrSupportLevel(false));
        menuButton.getItems().addAll(menuItem,item,item1);
        mainPane.getChildren().add(menuButton);
    }
    private void filtrPatternItems()
    {
        String s=tx.getText();
        if (s.isEmpty()||aprioriManager.getSupportListSize()==0)
            return;
        String[] items = s.split("[:,;]");
        for (String item : items) {
            filtr.add(item.trim());
        }
        aprioriManager.filtrPatternItems(filtr);
        System.out.println("Filtrowanie zakończone pomyślnie");
        if(aprioriManager.getFilteredListSize()==0)
        {
            filtr.clear();
            Alert a=new Alert(Alert.AlertType.INFORMATION);
            a.setContentText("Brak wzorców spełniających podane warunki");
            a.show();
        }
        else
        {
            Alert a=new Alert(Alert.AlertType.INFORMATION);
            a.setContentText("Filtrowanie zakończone pomyślnie");
            a.show();
            filtered =true;
            startId=0;
            tx.clear();
            createView();
        }
    }
    private void filtrSupportLevel( boolean f)
    {
        String s=tx.getText();
        if (s.isEmpty()||aprioriManager.getSupportListSize()==0)
            return;
        double supp=Double.parseDouble(s);
        aprioriManager.filtrSupportLevel(supp,f);
        Alert a=new Alert(Alert.AlertType.INFORMATION);
        a.setContentText("Filtrowanie zakończone pomyślnie");
        a.show();
        filtered =true;
        startId=0;
        tx.clear();
        createView();
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
    public void clearBase() {
        try
        {
            mainPane.getChildren().remove(header);
            aprioriManager.clearSupportList();
            contentVBox.getChildren().clear();
            checkBoxes.clear();
            filtered=false;
            filtr.clear();
            startId=0;
            System.out.println("Usunięcie listy wsparcia zakończone pomyślnie");
            Text tx= (Text) switchPageBox.lookup("#showInfo");
            tx.setText("Pokazano 0 z 0 elementów");
            Alert a=new Alert(Alert.AlertType.INFORMATION);
            a.setContentText("Usunięcie koszyków zakończone pomyślnie");
            a.show();
        }
        catch (Exception e)
        {
            System.out.println("Nastąpił błąd podczas usuwania koszyków: "+e);
            Alert a=new Alert(Alert.AlertType.ERROR);
            a.setContentText("Nastąpił błąd podczas usuwania koszyków");
            a.show();
        }
    }
}

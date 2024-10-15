package main.rules;

import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import main.InterfaceTemplate;
import main.apriori.SimplePattern;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class RuleInterfaceController extends InterfaceTemplate implements Initializable {
    private RuleManager ruleManager;

    public void setRuleManager(RuleManager ruleManager) {
        this.ruleManager = ruleManager;
    }
    @Override
    //funkcja inicjująca interfejs
    public void initialize(URL url, ResourceBundle resourceBundle) {
        init();
        //tworzenie elementów interfejsu
        createSelectSizeBox();
        createSwitchPageBox();
        createFiltrButton();
        createHeader();
        createCSVButton();
        createOptionButton();
        //edycja przycisku przewijającego strony do przodu
        Button nextButton=(Button) switchPageBox.lookup("#nButt");
        nextButton.setOnAction(e -> {
            if((startId+boxSize>= ruleManager.getRuleListSize())||(filtered&&startId+boxSize>= ruleManager.getFilteredRuleListSize()))
                return;
            startId+=boxSize;
            createView();
        });
    }
    private void createCSVButton()
    {
        MenuButton menuButton=makeMenuButtonStyle("Zarządzaj CSV",670.0,5.0);
        MenuItem item1=new MenuItem("Zapisz do pliku");
        item1.setOnAction((event)->ruleManager.createCSVFIle());
        MenuItem item2=new MenuItem("Wczytaj z pliku");
        item2.setOnAction((event)-> {
            ruleManager.loadFromCSV();
            //wywołanie wygenerowania nagłówka tabeli
            showTable();});
        menuButton.getItems().addAll(item1,item2);
    }
    protected void createOptionButton() {
        MenuButton menuButton=makeMenuButtonStyle("Zarządzaj filtrami",505.0,5.0);
    }


    @Override
    protected void deleteRows() {

    }

    @Override
    protected void clearFilter() {

    }

    @Override
    protected void createView() {
        //wyczyszczenie obecnego widoku
        contentVBox.getChildren().clear();
        checkBoxes.clear();
        if(ruleManager.getRuleListSize()==0)
        {
            Text tx= (Text) switchPageBox.lookup("#showInfo");
            tx.setText("Pokazano 0 z 0 elementów");
            return;
        }
        //wybór tablicy do wyświetlenia, zależnie od aktywnego filtru
        if(!filtered)
            createViewTable(ruleManager.getRuleList());
        else
            createViewTable(ruleManager.getFilteredRuleList());

    }
    private void createViewTable(List<AssociationRule> rules)
    {
        int size=rules.size();
        //warunek sprawdzający koniec listy
        int range=Math.min(size,(startId+boxSize));
        for(int i=startId,j=0;i<range;i++,j++)
        {
            AssociationRule rule=rules.get(i);
            //generowanie pojedynczego wiersza tabeli
            HBox box=createTableRow(rule);
            contentVBox.getChildren().add(box);
        }
        //aktualizacja informacji o widocznej części tablicy
        Text tx= (Text) switchPageBox.lookup("#showInfo");
        tx.setText("Pokazano "+(startId+1)+"-"+(Math.min(startId+boxSize, size)+" z "+size+" elementów"));
    }
    private HBox createTableRow(AssociationRule rule)
    {
        //tworzenie struktury wiersza
        HBox box=new HBox();
        CheckBox checkBox=new CheckBox();
        checkBoxes.add(checkBox);
        GridPane gridPane=new GridPane();
        gridPane.getColumnConstraints().add(new ColumnConstraints(575.0));
        gridPane.getColumnConstraints().add(new ColumnConstraints(125.0));
        gridPane.getColumnConstraints().add(new ColumnConstraints(125.0));
        gridPane.getColumnConstraints().add(new ColumnConstraints(125.0));
        //generowanie pojedynczej kolumny

        HBox box1=createTableColumn("","basketBorder","basketText");
        box1.getChildren().remove(0);
        box1.getChildren().add(0,checkBox);
        //text flow dla zmienienia koloru filtrowanych produktów
        List<Text> textList=createTextList(rule.getAntecedent().getPattern());
        List<Text> textList2=createTextList(rule.getConsequent().getPattern());
        TextFlow textFlow=new TextFlow();
        textFlow.getChildren().addAll(textList);
        Text text=new Text(" -> ");
        text.getStyleClass().add("basketTextFiltr");
        textFlow.getChildren().add(text);
        textFlow.getChildren().addAll(textList2);
        box1.getChildren().add(textFlow);

        gridPane.add(box1,0,0);
        //generowanie pojedynczej kolumny

        HBox box2=createTableColumn(String.format("%.3f", rule.getSupport()),"basketBorder","basketText");
        gridPane.add(box2,1,0);

        HBox box4=createTableColumn(String.format("%.3f", rule.getConfidence()),"basketBorder","basketText");
        gridPane.add(box4,2,0);

        HBox box5=createTableColumn(String.format("%.3f", rule.getLift()),"basketBorder","basketText");
        gridPane.add(box5,3,0);

        box.getChildren().add(gridPane);
        return box;
    }
    @Override
    protected void createFiltrButton() {
        MenuButton menuButton=makeMenuButtonStyle("Wybierz Filtr",340.0,5.0);
    }

    @Override
    protected void createHeader() {
        header=new HBox();
        header.setLayoutX(17.0);
        header.setLayoutY(66.0);
        header.setPrefWidth(950.0);
        header.setPrefHeight(40.0);
        GridPane gridPane=new GridPane();
        gridPane.getColumnConstraints().add(new ColumnConstraints(575.0));
        gridPane.getColumnConstraints().add(new ColumnConstraints(125.0));
        gridPane.getColumnConstraints().add(new ColumnConstraints(125.0));
        gridPane.getColumnConstraints().add(new ColumnConstraints(125.0));
        HBox box1=createTableColumn("Reguła","basketHeader","basketHeaderText");
        gridPane.add(box1,0,0);
        HBox box2=createTableColumn("Wsparcie","basketHeader","basketHeaderText");
        gridPane.add(box2,1,0);
        HBox box4=createTableColumn("Ufność","basketHeader","basketHeaderText");
        gridPane.add(box4,2,0);
        HBox box5=createTableColumn("Lift","basketHeader","basketHeaderText");
        gridPane.add(box5,3,0);
        header.getChildren().add(gridPane);
    }

    @Override
    public void clearBase() {
        clearFlags();
        ruleManager.clearList();
        ruleManager.clearFilteredList();
        Alert a=new Alert(Alert.AlertType.INFORMATION);
        a.setContentText("Usunięcie reguł zakończone pomyślnie");
        a.show();

    }
}

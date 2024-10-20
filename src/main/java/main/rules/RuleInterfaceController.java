package main.rules;

import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import main.FiltrType;
import main.InterfaceTemplate;
import main.apriori.SimplePattern;

import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class RuleInterfaceController extends InterfaceTemplate implements Initializable {
    private RuleManager ruleManager;
    FiltrType filtrType;
    double filtrValue;

    public void setRuleManager(RuleManager ruleManager) {
        this.ruleManager = ruleManager;
    }
    @Override
    //funkcja inicjująca interfejs
    public void initialize(URL url, ResourceBundle resourceBundle) {
        init();
        filtrType=FiltrType.none;
        filtrValue=0.0;
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
        MenuItem item=new MenuItem("Zaznacz wszystkie boxy");
        item.setOnAction(actionEvent -> selectAllBoxes());
        MenuItem item2=new MenuItem("Usuń zaznaczone wiersze");
        item2.setOnAction(actionEvent -> deleteRows());
        MenuItem item3=new MenuItem("Wyczyść filtr");
        item3.setOnAction(actionEvent -> clearFilter());
        menuButton.getItems().addAll(item,item2,item3);

    }


    @Override
    protected void deleteRows() {
        Alert a=new Alert(Alert.AlertType.INFORMATION);
        //wywołanie funkcji usuwającej
        int j= ruleManager.deleteSelectedRows(checkBoxes,startId,filtered);
        if(j==0)
        {
            a.setContentText("Brak wybranych elementów do usunięcia");
            a.show();
            return;
        }
        //odświeżenie widoku
        createView();
        a.setContentText("Wybrane elementów zostały usunięte");
        a.show();

    }

    @Override
    protected void clearFilter() {
        filtrType=FiltrType.none;
        filtrValue=0.0;
        filtered =false;
        filtr.clear();
        ruleManager.clearFilteredList();
        //odświeżenie wyglądu po zdjęciu filtru
        createView();
        Alert a=new Alert(Alert.AlertType.INFORMATION);
        a.setContentText("Filtry wyczyszczone pomyślnie, przywrócono podstawową listę koszyków");
        a.show();

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
        MenuItem menuItem=new MenuItem("Pokaż wybrane elementy");
        menuItem.setOnAction((event)->filtrPatternItems());
        MenuItem item=new MenuItem("Pokaż poziomy wsparcia poniżej");
        item.setOnAction((event)->filtrSupportLevel(FiltrType.supportDown));
        MenuItem item1=new MenuItem("Pokaż poziomy wsparcia powyżej");
        item1.setOnAction((event)->filtrSupportLevel(FiltrType.supportUp));
        MenuItem item2=new MenuItem("Pokaż poziomy ufności poniżej");
        item2.setOnAction((event)->filtrSupportLevel(FiltrType.confidenceDown));
        MenuItem item3=new MenuItem("Pokaż poziomy ufności powyżej");
        item3.setOnAction((event)->filtrSupportLevel(FiltrType.confidenceUp));
        MenuItem item4=new MenuItem("Pokaż poziom lift poniżej");
        item4.setOnAction((event)->filtrSupportLevel(FiltrType.liftDown));
        MenuItem item5=new MenuItem("Pokaż poziom lift powyżej");
        item5.setOnAction((event)->filtrSupportLevel(FiltrType.liftUp));
        menuButton.getItems().addAll(menuItem,item,item1,item2,item3,item4,item5);
    }
    //funkcja filtrująca wyświetlaną tabele
    private void filtrPatternItems()
    {
        //sczytanie filtru i jego zapis do tablicy
        String s=tx.getText();
        if (s.isEmpty()||ruleManager.getRuleListSize()==0)
            return;
        String[] items = s.split("[:,;]");
        for (String item : items) {
            filtr.add(item.trim());
        }
        //wywołanie funkcji filtrującej
        ruleManager.filtrPatternItems(filtr);
        filtrType= FiltrType.pattern;
        System.out.println("Filtrowanie zakończone pomyślnie");
        //wyświetlenie komunikatu, zależnego od wyniku filtrowania
        if(ruleManager.getFilteredRuleListSize()==0)
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
            //ustawianie flag informujących o istnieniu filtru
            filtered =true;
            startId=0;
            tx.clear();
            createView();
        }
    }
    //filtr po wartości wsparcia
    private void filtrSupportLevel( FiltrType ft)
    {
        if(filtered)
            filtr.clear();
        String s=tx.getText();
        if (s.isEmpty()||ruleManager.getRuleListSize()==0)
            return;
        //pobranie wartości liczbowej z napisu
        filtrValue=Double.parseDouble(s);
        ruleManager.filtrSupportLevel(filtrValue,ft);
        filtrType=ft;
        Alert a=new Alert(Alert.AlertType.INFORMATION);
        a.setContentText("Filtrowanie zakończone pomyślnie");
        a.show();
        //ustawienie flag
        filtered =true;
        startId=0;
        tx.clear();
        createView();
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
        Button button3=new Button("↑");
        button3.getStyleClass().add("boxButton");
        //funkcja sortująca
        button3.setOnAction((event)->{
            if(ruleManager.getRuleListSize()==0)
                return;
            if(Objects.equals(button3.getText(), "↑"))
            {
                ruleManager.sortByPatternUp();
                button3.setText("↓");
            } else{
                ruleManager.sortByPatternDown();
                button3.setText("↑");
            }
            refresh();
            //odświeżenie widoku po sortowaniu
            createView();
        });
        box1.getChildren().add(button3);
        gridPane.add(box1,0,0);
        HBox box2=createTableColumn("Wsparcie","basketHeader","basketHeaderText");
        Button button4=new Button("↑");
        button4.getStyleClass().add("boxButton");
        //funkcja sortująca
        button4.setOnAction((event)->{
            if(ruleManager.getRuleListSize()==0)
                return;
            if(Objects.equals(button4.getText(), "↑"))
            {
                ruleManager.sortBySortUp();
                button4.setText("↓");
            } else{
                ruleManager.sortBySortDown();
                button4.setText("↑");
            }
            //odświeżenie widoku po sortowaniu
            refresh();
            createView();
        });
        box2.getChildren().add(button4);
        gridPane.add(box2,1,0);
        HBox box4=createTableColumn("Ufność","basketHeader","basketHeaderText");
        Button button1=new Button("↑");
        button1.getStyleClass().add("boxButton");
        //funkcja sortująca
        button1.setOnAction((event)->{
            if(ruleManager.getRuleListSize()==0)
                return;
            if(Objects.equals(button1.getText(), "↑"))
            {
                ruleManager.sortByConfidenceUp();
                button1.setText("↓");
            } else{
                ruleManager.sortByConfidenceDown();
                button1.setText("↑");
            }
            //odświeżenie widoku po sortowaniu
            refresh();
            createView();
        });
        box4.getChildren().add(button1);
        gridPane.add(box4,2,0);
        HBox box5=createTableColumn("Lift","basketHeader","basketHeaderText");
        Button button2=new Button("↑");
        button2.getStyleClass().add("boxButton");
        //funkcja sortująca
        button2.setOnAction((event)->{
            if(ruleManager.getRuleListSize()==0)
                return;
            if(Objects.equals(button2.getText(), "↑"))
            {
                ruleManager.sortByLiftUp();
                button2.setText("↓");
            } else{
                ruleManager.sortByLiftDown();
                button2.setText("↑");
            }
            //odświeżenie widoku po sortowaniu
            refresh();
            createView();
        });
        box5.getChildren().add(button2);
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
    private void refresh()
    {
        switch(filtrType){
            case pattern -> ruleManager.filtrPatternItems(filtr);
            case supportDown -> ruleManager.filtrSupportLevel(filtrValue,FiltrType.supportDown);
            case supportUp -> ruleManager.filtrSupportLevel(filtrValue,FiltrType.supportUp);
            case confidenceDown -> ruleManager.filtrSupportLevel(filtrValue,FiltrType.confidenceDown);
            case confidenceUp -> ruleManager.filtrSupportLevel(filtrValue,FiltrType.confidenceUp);
            case liftDown -> ruleManager.filtrSupportLevel(filtrValue,FiltrType.liftDown);
            case liftUp -> ruleManager.filtrSupportLevel(filtrValue,FiltrType.liftUp);
        }
    }
}

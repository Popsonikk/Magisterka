package main.apriori;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import main.FiltrType;
import main.InterfaceTemplate;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
public class AprioriInterfaceController extends InterfaceTemplate implements Initializable {
    private AprioriManager aprioriManager;
    private FiltrType filtrType;
    double supportFiltr;
    public void setAprioriManager(AprioriManager aprioriManager) {
        this.aprioriManager = aprioriManager;
    }
    @Override
    //funkcja inicjująca interfejs
    public void initialize(URL url, ResourceBundle resourceBundle) {
        filtrType=FiltrType.none;
        supportFiltr=0.0;
        init();
        //tworzenie elementów interfejsu
        createSelectSizeBox();
        createSwitchPageBox();
        createCSVButton();
        createFiltrButton();
        createOptionButton();
        createHeader();
        //edycja przycisku przewijającego strony do przodu
        Button nextButton=(Button) switchPageBox.lookup("#nButt");
        nextButton.setOnAction(e -> {
            if((startId+boxSize>= aprioriManager.getSupportListSize())||(filtered&&startId+boxSize>= aprioriManager.getFilteredListSize()))
                return;
            startId+=boxSize;
            createView();
        });

    }
    @Override
    //funkcja wywołująca wygenerowanie tablicy
    protected void createView() {
        //wyczyszczenie obecnego widoku
        contentVBox.getChildren().clear();
        checkBoxes.clear();
        if(aprioriManager.getSupportListSize()==0)
        {
            Text tx= (Text) switchPageBox.lookup("#showInfo");
            tx.setText("Pokazano 0 z 0 elementów");
            return;
        }
        //wybór tablicy do wyświetlenia, zależnie od aktywnego filtru
        if(!filtered)
            createViewTable(aprioriManager.getSupportList());
        else
            createViewTable(aprioriManager.getFiltredList());
    }
    //funkcja bezpośrednio generująca widoczną tablicę
    private void createViewTable(List<SimplePattern> patterns)
    {
        int size=patterns.size();
        //warunek sprawdzający koniec listy
        int range=Math.min(size,(startId+boxSize));
        for(int i=startId,j=0;i<range;i++,j++)
        {
            SimplePattern pattern=patterns.get(i);
            //generowanie pojedynczego wiersza tabeli
            HBox box=createTableRow(pattern);
            contentVBox.getChildren().add(box);
        }
        //aktualizacja informacji o widocznej części tablicy
        Text tx= (Text) switchPageBox.lookup("#showInfo");
        tx.setText("Pokazano "+(startId+1)+"-"+(Math.min(startId+boxSize, size)+" z "+size+" elementów"));
    }
    //generowanie pojedynczego wiersza tabeli
    private HBox createTableRow(SimplePattern pattern)
    {
        //tworzenie struktury wiersza
        HBox box=new HBox();
        CheckBox checkBox=new CheckBox();
        checkBoxes.add(checkBox);
        GridPane gridPane=new GridPane();
        gridPane.getColumnConstraints().add(new ColumnConstraints(200.0));
        gridPane.getColumnConstraints().add(new ColumnConstraints(750.0));
        //generowanie pojedynczej kolumny
        HBox box1=createTableColumn(String.format("%.3f", pattern.getSupport()),"basketBorder","basketText");
        box1.getChildren().add(0,checkBox);
        gridPane.add(box1,0,0);
        //generowanie pojedynczej kolumny
        HBox box2=createTableColumn("","basketBorder","basketText");
        box2.getChildren().remove(0);
        //text flow dla zmienienia koloru filtrowanych produktów
        List<Text> textList=createTextList(pattern.getPattern());
        TextFlow textFlow=new TextFlow();
        textFlow.getChildren().addAll(textList);
        box2.getChildren().add(textFlow);
        gridPane.add(box2,1,0);
        box.getChildren().add(gridPane);
        return box;
    }
    //generowanie przycisku z opcjami interfejsu
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
    //funkcja czyszcząca filtr
    protected void clearFilter() {
        filtrType=FiltrType.none;
        supportFiltr=0.0;
        filtered =false;
        filtr.clear();
        aprioriManager.clearFilteredList();
        //odświeżenie wyglądu po zdjęciu filtru
        createView();
        Alert a=new Alert(Alert.AlertType.INFORMATION);
        a.setContentText("Filtry wyczyszczone pomyślnie, przywrócono podstawową listę koszyków");
        a.show();
    }
    //funkcja usuwająca wiersze z tabeli
    @Override
    protected void deleteRows() {
        Alert a=new Alert(Alert.AlertType.INFORMATION);
        //wywołanie funkcji usuwającej
        int j= aprioriManager.deleteSelectedRows(checkBoxes,startId,filtered);
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
    //generowanie nagłówka tabeli
    protected void createHeader() {
        header=new HBox();
        header.setLayoutX(17.0);
        header.setLayoutY(66.0);
        header.setPrefWidth(950.0);
        header.setPrefHeight(40.0);
        GridPane gridPane=new GridPane();
        gridPane.getColumnConstraints().add(new ColumnConstraints(200.0));
        gridPane.getColumnConstraints().add(new ColumnConstraints(750.0));
        //generowanie kolumny
        HBox box1=createTableColumn("Wsparcie","basketHeader","basketHeaderText");
        Button button1=new Button("↑");
        button1.getStyleClass().add("boxButton");
        //funkcja sortująca
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
            refresh();
            //odświeżenie widoku po sortowaniu
            createView();
        });
        box1.getChildren().add(button1);
        gridPane.add(box1,0,0);
        //generowanie kolumny
        HBox box2=createTableColumn("Wzorzec","basketHeader","basketHeaderText");
        Button button2=new Button("↑");
        button2.getStyleClass().add("boxButton");
        //funkcja sortująca po długości
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
            refresh();
            //odświeżenie widoku po sortowaniu
            createView();
        });
        box2.getChildren().add(button2);
        gridPane.add(box2,1,0);
        header.getChildren().add(gridPane);
    }

    //generowanie przycisku do zapisu/odczytu z pliku
    private void createCSVButton()
    {
        MenuButton menuButton=makeMenuButtonStyle("Zarządzaj CSV",670.0,5.0);
        MenuItem item1=new MenuItem("Zapisz do pliku");
        item1.setOnAction((event)->aprioriManager.createCSVFIle());
        MenuItem item2=new MenuItem("Wczytaj z pliku");
        item2.setOnAction((event)-> {
            aprioriManager.loadFromCSV();
            //wywołanie wygenerowania nagłówka tabeli
            showTable();});
        menuButton.getItems().addAll(item1,item2);
    }
    //generowanie przycisku, pozwalającego wybrać rodzaj filtru
    @Override
    protected void createFiltrButton() {
        MenuButton menuButton=makeMenuButtonStyle("Wybierz Filtr",340.0,5.0);
        MenuItem menuItem=new MenuItem("Pokaż wybrane elementy");
        menuItem.setOnAction((event)->filtrPatternItems());
        MenuItem item=new MenuItem("Pokaż poziomy wsparcia poniżej");
        item.setOnAction((event)->filtrSupportLevel(true));
        MenuItem item1=new MenuItem("Pokaż poziomy wsparcia powyżej");
        item1.setOnAction((event)->filtrSupportLevel(false));
        menuButton.getItems().addAll(menuItem,item,item1);
    }
    //funkcja filtrująca wyświetlaną tabele
    private void filtrPatternItems()
    {
        //sczytanie filtru i jego zapis do tablicy
        String s=tx.getText();
        if (s.isEmpty()||aprioriManager.getSupportListSize()==0)
            return;
        String[] items = s.split("[:,;]");
        for (String item : items) {
            filtr.add(item.trim());
        }
        //wywołanie funkcji filtrującej
        aprioriManager.filtrPatternItems(filtr);
        filtrType=FiltrType.pattern;
        System.out.println("Filtrowanie zakończone pomyślnie");
        //wyświetlenie komunikatu, zależnego od wyniku filtrowania
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
            //ustawianie flag informujących o istnieniu filtru
            filtered =true;
            startId=0;
            tx.clear();
            createView();
        }
    }
    //filtr po wartości wsparcia
    private void filtrSupportLevel( boolean f)
    {
        if(filtered)
            filtr.clear();
        String s=tx.getText();
        if (s.isEmpty()||aprioriManager.getSupportListSize()==0)
            return;
        //pobranie wartości liczbowej z napisu
        double supp=Double.parseDouble(s);
        supportFiltr=supp;
        aprioriManager.filtrSupportLevel(supp,f);
        if(f)
            filtrType=FiltrType.supportDown;
        else
            filtrType=FiltrType.supportUp;
        Alert a=new Alert(Alert.AlertType.INFORMATION);
        a.setContentText("Filtrowanie zakończone pomyślnie");
        a.show();
        //ustawienie flag
        filtered =true;
        startId=0;
        tx.clear();
        createView();
    }


    //funkcja czyszcząca całą bazę interfejsu
    @Override
    public void clearBase() {
        try
        {
            //wyczyszczenie wszystkich tabel, flag i kontenerów
            clearFlags();
            filtrType=FiltrType.none;
            supportFiltr=0.0;
            aprioriManager.clearSupportList();
            aprioriManager.clearFilteredList();
            Alert a=new Alert(Alert.AlertType.INFORMATION);
            a.setContentText("Usunięcie listy wsparcia zakończone pomyślnie");
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
    private void refresh()
    {
        switch(filtrType){
            case pattern -> aprioriManager.filtrPatternItems(filtr);
            case supportDown -> aprioriManager.filtrSupportLevel(supportFiltr,true);
            case supportUp -> aprioriManager.filtrSupportLevel(supportFiltr,false);
        }
    }

}

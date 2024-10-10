package main.baskets;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import main.InterfaceTemplate;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class BasketInterfaceController extends InterfaceTemplate implements Initializable {
    protected BasketManager basketManager;
    public void setBasketManager(BasketManager basketManager) {
        this.basketManager = basketManager;
    }
    @Override
    //funkcja inicjalizująca interfejs
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //generowanie przycisków
        init();
        createSelectSizeBox();
        createFiltrButton();
        createSwitchPageBox();
        createHeader();
        Button nextButton=(Button) switchPageBox.lookup("#nButt");
        //edycja przycisku przewijającego strony do przodu
        nextButton.setOnAction(e -> {
            if((startId+boxSize>= basketManager.getBasketSize())||(filtered&&startId+boxSize>= basketManager.getFilteredBasketSize()))
                return;
            startId+=boxSize;
            createView();
        });
    }
    @Override
    //funkcja tworząca widok tabeli
    public void createView()
    {
        contentVBox.getChildren().clear();
        checkBoxes.clear();
        if(basketManager.getBasketSize()==0)
        {
            Text tx= (Text) switchPageBox.lookup("#showInfo");
            tx.setText("Pokazano 0 z 0 koszyków");
            return;
        }
        if(filtered)
            createViewTable(basketManager.getFilteredBaskets());
        else
            createViewTable(basketManager.getBaskets());
    }
    //funkcja bezpośrednio generująca widoczną tablicę
    private void createViewTable(List<List<String>> baskets)
    {
        int size=baskets.size();
        int range=Math.min(size,(startId+boxSize));
        for(int i=startId;i<range;i++)
        {
            List<String> basket=baskets.get(i);
            //generowanie pojedynczego wiersza
            HBox box=createBox(createTextList(basket));
            contentVBox.getChildren().add(box);
        }
        //aktualizacja informacji o widocznej części tablicy
        Text tx= (Text) switchPageBox.lookup("#showInfo");
        tx.setText("Pokazano "+(startId+1)+"-"+(Math.min(startId+boxSize, size)+" z "+size+" elementów"));
    }
    //pojedyncza komórka wiersza
    private HBox createBox(List<Text> textList)
    {
        HBox box=new HBox();
        CheckBox checkBox=new CheckBox();
        checkBoxes.add(checkBox);
        box.getStyleClass().add("basketBorder");
        //text flow dla zmienienia koloru filtrowanych produktów
        TextFlow textFlow=new TextFlow();
        textFlow.getChildren().addAll(textList);
        box.getChildren().addAll(checkBox,textFlow);
        return  box;
    }
    //funkcja wczytująca koszyki
    public void loadBaskets() {
        try
        {
            //wywołanie funkcji wczytujacej
            basketManager.loadBaskets();
            mainPane.getChildren().add(header);
            createView();
            System.out.println("Dane wczytane poprawnie");
            Alert a=new Alert(Alert.AlertType.INFORMATION);
            a.setContentText("Koszyki zostały wczytane poprawnie");
            a.show();
        }
        catch (Exception e)
        {
            System.out.println("Nastąpił błąd w czasie wczytywania danych: "+e);
            Alert a=new Alert(Alert.AlertType.ERROR);
            a.setContentText("Ne udało się wczytać koszyków");
            a.show();
        }
    }
    //funkcja czyszcząca całą bazę interfejsu
    public void clearBaskets() {
        mainPane.getChildren().remove(header);
        basketManager.clearBaskets();
        basketManager.clearFilteredBaskets();
        contentVBox.getChildren().clear();
        checkBoxes.clear();
        filtered=false;
        filtr.clear();
        basketManager.setFilename("");
        startId=0;
        System.out.println("Usunięcie koszyków zakończone pomyślnie");
        Text tx= (Text) switchPageBox.lookup("#showInfo");
        tx.setText("Pokazano 0 z 0 elementów");
        Alert a=new Alert(Alert.AlertType.INFORMATION);
        a.setContentText("Usunięcie koszyków zakończone pomyślnie");
        a.show();
    }
    //funkcja filtrującą tablicę
    public void filtrBaskets() {
        //sczytanie filtru i jego zapis do tablicy
        String s=tx.getText();
        if (s.isEmpty()||basketManager.getBasketSize()==0)
            return;
        String[] items = s.split("[:,;]");
        for (String item : items) {
            filtr.add(item.trim());
        }
        //wywołanie funkcji filtrującej
        basketManager.filtrBaskets(filtr);
        System.out.println("Filtrowanie zakończone pomyślnie");
        if(basketManager.getFilteredBasketSize()==0)
        {
            filtr.clear();
            Alert a=new Alert(Alert.AlertType.INFORMATION);
            a.setContentText("Brak koszyków spełniających podane warunki");
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
    //wyczyszczenie filtru
    protected void clearFilter() {
        filtered =false;
        filtr.clear();
        basketManager.clearFilteredBaskets();
        createView();
        Alert a=new Alert(Alert.AlertType.INFORMATION);
        a.setContentText("Filtry wyczyszczone pomyślnie, przywrócono podstawową listę koszyków");
        a.show();

    }
    @Override
    //funkcja tworząca nagłówek tablicy
    protected void createHeader() {
        header=new HBox();
        header.setLayoutX(17.0);
        header.setLayoutY(66.0);
        header.setPrefWidth(950.0);
        header.getStyleClass().add("basketHeader");
        Text text=new Text("Koszyk");
        text.getStyleClass().add("basketHeaderText");
        Button button=new Button("↑");
        button.getStyleClass().add("boxButton");
        //przycisk filtrujący po długości koszyka
        button.setOnAction((event)->{
            if(basketManager.getBasketSize()==0)
                return;
            if(Objects.equals(button.getText(), "↑"))
            {
                basketManager.sortByLengthUp();
                button.setText("↓");
            } else{
                basketManager.sortByLengthDown();
                button.setText("↑");
            }
            createView();
        });
        header.getChildren().addAll(text,button);
    }
    @Override
    //generowanie przycisku zawierającego opcje interfejsu
    protected void createFiltrButton()
    {
        MenuButton menuButton=makeMenuButtonStyle();
        menuButton.setText("Zarządzaj filtrami");
        menuButton.setLayoutX(505.0);
        menuButton.setLayoutY(5.0);
        MenuItem item1=new MenuItem("Wyczyść filtry");
        item1.setOnAction(actionEvent -> clearFilter());
        MenuItem item2=new MenuItem("Usuń zaznaczone wiersze");
        item2.setOnAction(actionEvent -> deleteRows());
        MenuItem item3=new MenuItem("Usuń filtrowane przedmioty");
        item3.setOnAction(actionEvent -> deleteItems());
        MenuItem item4=new MenuItem("Zaznacz wszystkie boxy");
        item4.setOnAction(actionEvent -> selectAllBoxes());
        menuButton.getItems().addAll(item1,item2,item3,item4);
        mainPane.getChildren().add(menuButton);
    }
    //funkcja usuwająca wybrane wiersze
    protected void deleteRows()
    {
        Alert a=new Alert(Alert.AlertType.INFORMATION);
        //wywołanie funkcji
        int j= basketManager.deleteSelectedRows(checkBoxes,startId,filtered);
        if(j==0)
        {
            a.setContentText("Brak wybranych koszyków do usunięcia");
            a.show();
            return;
        }
        if(filtered)
            basketManager.filtrBaskets(filtr);
        //odświeżenie widoku
        createView();
        a.setContentText("Wybrane koszyki zostały usunięte");
        a.show();

    }
    //funkcja usuwająca wybrane przedmioty z wiersza
    private void deleteItems()
    {
        Alert a=new Alert(Alert.AlertType.INFORMATION);
        if (!filtered)
            return;
        //wywołanie funkcji
        int j=basketManager.deleteSelectedItems(checkBoxes, filtr,startId);
        if(j==0)
        {
            a.setContentText("Brak wybranych przedmiotów do usunięcia");
            a.show();
            return;
        }
        basketManager.filtrBaskets(filtr);
        createView();
        a.setContentText("Wybrane przedmioty zostały usunięte z koszyków");
        a.show();
    }
}

package main;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class BasketInterfaceController extends  InterfaceTemplate implements Initializable {

    protected BasketManager basketManager;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        init();
        createSelectSizeBox();
        createFiltrButton();
        createSwitchPageBox();
        createHeader();
        Button nextButton=(Button) switchPageBox.lookup("#nButt");

        nextButton.setOnAction(e -> {
            if(filtered)
            {
                if(startId+boxSize>=basketManager.getFilteredBasketSize())
                    return;
            }
            else
            {
                if(startId+boxSize>=basketManager.getBasketSize())
                    return;
            }
            startId+=boxSize;
            createView();

        });
    }
    public void setBasketManager(BasketManager basketManager) {
        this.basketManager = basketManager;
    }
    @Override
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
            createViewFromSet(basketManager.getFilteredBaskets());
        else
            createViewFromSet(basketManager.getBaskets());
    }
    private void createViewFromSet(List<List<String>> baskets)
    {
        int size=baskets.size();
        int range=Math.min(size,(startId+boxSize));
        for(int i=startId,j=0;i<range;i++,j++)
        {
            List<String> basket=baskets.get(i);
            List<Text> textList=new ArrayList<>();
            for(String s:basket)
            {
                Text text=new Text(s+"; ");
                if(filtr.contains(s))
                    text.getStyleClass().add("basketTextFiltr");
                else
                    text.getStyleClass().add("basketText");
                textList.add(text);
            }
            HBox box=createBox(textList,j);
            contentVBox.getChildren().add(box);
        }
        Text tx= (Text) switchPageBox.lookup("#showInfo");
        tx.setText("Pokazano "+(startId+1)+"-"+(Math.min(startId+boxSize, size)+" z "+size+" elementów"));
    }
    private HBox createBox(List<Text> textList,int i)
    {
        HBox box=new HBox();
        CheckBox checkBox=new CheckBox();
        checkBoxes.add(checkBox);
        box.setLayoutX(15.0);
        box.setLayoutY(20.0+(50.0*(i+1)));
        box.getStyleClass().add("basketBorder");
        TextFlow textFlow=new TextFlow();
        textFlow.getChildren().addAll(textList);
        box.getChildren().addAll(checkBox,textFlow);
        return  box;
    }

    public void loadBaskets() {
        try
        {

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
    public void clearBaskets() {

        try
        {
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
        catch (Exception e)
        {
            System.out.println("Nastąpił błąd podczas usuwania koszyków: "+e);
            Alert a=new Alert(Alert.AlertType.ERROR);
            a.setContentText("Nastąpił błąd podczas usuwania koszyków");
            a.show();
        }

    }

    public void filtrBaskets() {
        try
        {
            String s=tx.getText();
            if (s.isEmpty()||basketManager.getBasketSize()==0)
                return;
            String[] items = s.split("[:,;]");
            for (String item : items) {
                filtr.add(item.trim());
            }
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
        catch (Exception e)
        {
            System.out.println("Nastąpił błąd podczas filtrowania koszyków: "+e);
        }

    }

    public void clearFilter() {
        filtered =false;
        filtr.clear();
        basketManager.clearFilteredBaskets();
        createView();
        Alert a=new Alert(Alert.AlertType.INFORMATION);
        a.setContentText("Filtry wyczyszczone pomyślnie, przywrócono podstawową listę koszyków");
        a.show();

    }
    @Override
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



    private void deleteRows()
    {
        Alert a=new Alert(Alert.AlertType.INFORMATION);
        int j= basketManager.deleteSelectedRows(checkBoxes,startId,filtered);
        if(j==0)
        {
            a.setContentText("Brak wybranych koszyków do usunięcia");
            a.show();
            return;
        }
        if(filtered)
            basketManager.filtrBaskets(filtr);
        createView();
        a.setContentText("Wybrane koszyki zostały usunięte");
        a.show();

    }
    private void deleteItems()
    {
        Alert a=new Alert(Alert.AlertType.INFORMATION);
        if (!filtered)
            return;
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
    private void selectAllBoxes()
    {
        for(CheckBox box:checkBoxes)
            box.setSelected(true);
    }




}

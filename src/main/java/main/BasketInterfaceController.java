package main;


import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.CacheHint;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class BasketInterfaceController implements Initializable {
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


    private List<CheckBox> checkBoxes;
    private int boxSize;
    private int startId;

    private boolean filtered;

    private Scene mainScene;

    private Stage mainStage;

    private BasketManager basketManager;

    public void setMainScene(Scene mainScene) {
        this.mainScene = mainScene;
    }

    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }

    public void setBasketManager(BasketManager basketManager) {
        this.basketManager = basketManager;
    }

    private List<String> filtr;

    public void createView()
    {
        contentVBox.getChildren().clear();
        checkBoxes.clear();
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
            //StringBuilder builder=new StringBuilder();
          //  for(String s:basket)
               // builder.append(s.trim()).append("; ");
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
        tx.setText("Pokazano "+(startId+1)+"-"+(Math.min(startId+boxSize, size)+" z "+size+" koszyków"));
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
            createView();
            System.out.println("Dane wczytane poprawnie");
        }
        catch (Exception e)
        {
            System.out.println("Nastąpił błąd w czasie wczytywania danych: "+e);
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        filtered =false;
        filtr=new ArrayList<>();
        checkBoxes=new ArrayList<>();
        pane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        pane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        selectSizeBox.getStyleClass().add("infoBox");
        switchPageBox.getStyleClass().add("infoBox");

        Text text1=new Text();
        text1.setText("Pokazuj");
        Text text2=new Text("Koszyków");
        text1.getStyleClass().add("infoBoxText");
        text2.getStyleClass().add("infoBoxText");

        MenuButton menuButton=new MenuButton();
        menuButton.setText("50");
        boxSize=50;
        startId=0;
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
        MenuItem item4=new MenuItem("75");
        item4.setOnAction(event);
        MenuItem item5=new MenuItem("100");
        item5.setOnAction(event);
        menuButton.getItems().addAll(item1,item2,item3,item4,item5);
        menuButton.getStyleClass().add("boxButton");
        selectSizeBox.getChildren().addAll(text1,menuButton,text2);

        Button backButton = new Button("<");
        backButton.getStyleClass().add("boxButton");
        Button nextButton= new Button(">");
        nextButton.getStyleClass().add("boxButton");
        Text text=new Text("Pokazano 0 z 0 koszyków");
        text.getStyleClass().add("infoBoxText");
        text.setId("showInfo");

        backButton.setOnAction(e -> {
            if(startId==0)
                return;
            startId=Math.max(0,startId-boxSize);
            createView();

        });
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
        switchPageBox.getChildren().addAll(backButton,text,nextButton);
        createFiltrButton();

    }

    public void clearBaskets() {
        try
        {
            basketManager.clearBaskets();
            contentVBox.getChildren().clear();
            checkBoxes.clear();
            startId=0;
            System.out.println("Usunięcie koszyków zakończone pomyślnie");
            Text tx= (Text) switchPageBox.lookup("#showInfo");
            tx.setText("Pokazano 0 z 0 koszyków");
        }
        catch (Exception e)
        {
            System.out.println("Nastąpił błąd podczas usuwania koszyków: "+e);
        }

    }

    public void filtrBaskets() {
        String s=tx.getText();

        if (s.isEmpty()||basketManager.getBasketSize()==0)
            return;
        String[] items = s.split(":|,|;");
        for (String item : items) {
            filtr.add(item.trim());
        }
        basketManager.filtrBaskets(filtr);
        filtered =true;
        tx.clear();
        createView();
    }

    public void clearFilter() {
        filtered =false;
        filtr.clear();
        startId=0;
        basketManager.clearFilteredBaskets();
        createView();

    }

    public void back() {
        mainStage.setScene(mainScene);
    }
    private void createFiltrButton()
    {
        MenuButton menuButton=new MenuButton("Zarządzaj filtrami");
        menuButton.setLayoutX(505.0);
        menuButton.setLayoutY(15.0);
        menuButton.setPrefSize(150.0,50.0);
        menuButton.getStyleClass().add("filterButton");
        MenuItem item1=new MenuItem("Wyczyść filtry");
        item1.setOnAction(actionEvent -> clearFilter());
        MenuItem item2=new MenuItem("Usuń zaznaczone wiersze");
        item2.setOnAction(actionEvent -> deleteRows());
        MenuItem item3=new MenuItem("Usuń filtrowane przedmioty");
        item3.setOnAction(actionEvent -> deleteItems());
        MenuItem item4=new MenuItem("Zaznacz wszystkie boxy");
        item4.setOnAction(actionEvent -> selectAllBoxes());
        menuButton.getItems().addAll(item1,item2,item3,item4);
        menuButton.setOnShowing(event -> {
            menuButton.setStyle("-fx-background-color: #2e79ba; -fx-border-style: solid;");
        });

        menuButton.setOnHidden(event -> {
            menuButton.setStyle("-fx-background-color: #5fc9f3; -fx-border-style: dashed;");
        });
        mainPane.getChildren().add(menuButton);
    }
    private void deleteRows()
    {
        basketManager.deleteSelectedRows(checkBoxes,startId,filtered);
        if(filtered)
            basketManager.filtrBaskets(filtr);
        createView();
    }
    private void deleteItems()
    {
        if (!filtered)
            return;
        basketManager.deleteSelectedItems(checkBoxes, filtr,startId);
        basketManager.filtrBaskets(filtr);
        createView();
    }
    private void selectAllBoxes()
    {
        for(CheckBox box:checkBoxes)
            box.setSelected(true);
    }

}

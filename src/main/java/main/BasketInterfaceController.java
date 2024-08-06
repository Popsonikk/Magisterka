package main;


import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.net.URL;
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


    private int boxSize;
    private int startId;

    private boolean filtred;

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

    public void createView()
    {
        contentVBox.getChildren().clear();
        if(filtred)
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
            StringBuilder builder=new StringBuilder();
            builder.append("ID: ").append(i).append(" - ");
            for(String s:basket)
                builder.append(s.trim()).append("; ");
            HBox box=createBox(builder,j);
            contentVBox.getChildren().add(box);
        }


        Text tx= (Text) switchPageBox.lookup("#showInfo");
        tx.setText("  Pokazano "+(startId+1)+"-"+(Math.min(startId+boxSize, size)+" z "+size+" koszyków  "));
    }


    private HBox createBox(StringBuilder builder,int i)
    {
        HBox box=new HBox();
        box.setLayoutX(15.0);
        box.setLayoutY(20.0+50.0*(i+1));
        box.setPrefWidth(950);
        box.setPrefHeight(25);
        box.setPadding(new Insets(5.0));
        box.setBorder(new Border(new BorderStroke(
                Color.BLACK,
                BorderStrokeStyle.SOLID,
                CornerRadii.EMPTY,
                new BorderWidths(2) )));
        Text text=new Text(builder.toString());
        text.setFont(new Font(18.0));
        //text.setId("text");
        box.getChildren().add(text);
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
        filtred=false;

        Text text1=new Text("Pokazuj  ");
        Text text2=new Text("  Koszyków");
        text1.setFont(new Font(18.0));
        text2.setFont(new Font(18.0));

        pane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        pane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
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

        selectSizeBox.setBorder(new Border(new BorderStroke(
                Color.BLACK,
                BorderStrokeStyle.SOLID,
                CornerRadii.EMPTY,
                new BorderWidths(2) )));

        selectSizeBox.setPadding(new Insets(5.0));
        selectSizeBox.getChildren().addAll(text1,menuButton,text2);

        switchPageBox.setBorder(new Border(new BorderStroke(
                Color.BLACK,
                BorderStrokeStyle.SOLID,
                CornerRadii.EMPTY,
                new BorderWidths(2) )));

        switchPageBox.setPadding(new Insets(5.0));
        Button backButton = new Button("<");
        //backButton.setFont(new Font(18.0));
        backButton.setPrefSize(50,20);
        Button nextButton= new Button(">");
       // nextButton.setFont(new Font(18.0));
        nextButton.setPrefSize(50,20);
        Text text=new Text("  Pokazano 0 z 0 koszyków  ");
       // text.setFont(new Font(18.0));
        text.setId("showInfo");

        backButton.setOnAction(e -> {
            if(startId==0)
                return;
            startId=Math.max(0,startId-boxSize);
            createView();

        });
        nextButton.setOnAction(e -> {
            if(filtred)
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

    }

    public void clearBaskets() {
        try
        {
            basketManager.clearBaskets();
            contentVBox.getChildren().clear();
            System.out.println("Usunięcie koszyków zakończone pomyślnie");
            Text tx= (Text) switchPageBox.lookup("#showInfo");
            tx.setText("  Pokazano 0 z 0 koszyków  ");
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
        basketManager.filtrBaskets(s);
        filtred=true;
        createView();
    }

    public void clearFilter() {
        filtred=false;
        tx.clear();
        basketManager.clearFilteredBaskets();
        createView();

    }

    public void back() {
        mainStage.setScene(mainScene);
    }
}

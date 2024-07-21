package main;


import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class BasketInterfaceController implements Initializable {
    @FXML
    public VBox contentVBox;
    @FXML
    public ScrollPane pane;


    private BasketManager basketManager;

    public void setBasketManager(BasketManager basketManager) {
        this.basketManager = basketManager;
    }

    public void init()
    {
        int size= basketManager.getBasketSize();
        if(size==0)
            return;
        for(int i=0;i<size;i++)
        {
            List<String> basket =basketManager.getSingleBasket(i);
            StringBuilder builder=new StringBuilder();
            for(String s:basket)
            {

                builder.append(s.trim()).append("; ");
            }
            HBox box=new HBox();
            box.setLayoutX(15.0);
            box.setLayoutY(20.0+50.0*(i+1));
            box.setPrefWidth(940);
            box.setPrefHeight(25);
            box.setPadding(new Insets(5.0));
            box.setBorder(new Border(new BorderStroke(
                    Color.BLACK,
                    BorderStrokeStyle.SOLID,
                    CornerRadii.EMPTY,
                    new BorderWidths(2) )));
            Text text=new Text(builder.toString());
            text.setFont(new Font(18.0));
            text.setId("text");
            Button button=new Button();
            button.setText("edit");
            button.setLayoutX(900.0);
            Button button1=new Button();
            button1.setText("detete");
            button.setLayoutX(1050.0);
            box.getChildren().addAll(text);
            //box.getChildren().add(button);
            //box.getChildren().add(button1);
            contentVBox.getChildren().add(box);
            contentVBox.setPadding(new Insets(15.0));



        }

    }
    public void loadBaskets() {
        try
        {
            basketManager.loadBaskets();
            init();
            System.out.println("Dane wczytane poprawnie");
        }
        catch (Exception e)
        {
            System.out.println("Nastąpił błąd w czasie wczytywania danych: "+e);
        }




    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        pane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        pane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
    }

    public void clearBaskets() {
        try
        {
            basketManager.clearBaskets();
            contentVBox.getChildren().clear();
            System.out.println("Usunięcie koszyków zakończone pomyślnie");
        }
        catch (Exception e)
        {
            System.out.println("Nastąpił błąd podczas usuwania koszyków: "+e);
        }

    }
}

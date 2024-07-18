package main;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.util.List;

public class BasketInterfaceController {
    @FXML
    private ScrollPane pane;
    @FXML
    private Pane mainPane;

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
                builder.append(s).append("; ");
            }
            HBox box=new HBox();
            box.setLayoutX(15.0);
            box.setLayoutY(20.0+50.0*(i+1));
            box.setPrefWidth(900);
            box.setPrefHeight(50);
            box.setBorder(new Border(new BorderStroke(
                    Color.BLACK,
                    BorderStrokeStyle.SOLID,
                    CornerRadii.EMPTY,
                    new BorderWidths(3) )));
            Text text=new Text(builder.toString());
            text.setId("text");
            Button button=new Button();
            button.setText("edit");
            button.setLayoutX(900.0);
            Button button1=new Button();
            button1.setText("detete");
            button.setLayoutX(1050.0);
            box.getChildren().add(text);
            box.getChildren().add(button);
            box.getChildren().add(button1);
            mainPane.getChildren().add(box);



        }

    }
}

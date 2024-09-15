package main;


import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.net.URL;
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
    }
    @Override
    protected void createView() {

    }

    @Override
    protected void createFiltrButton() {

    }

    private void createSwitchPageBox()
    {
        switchPageBox.getStyleClass().add("infoBox");
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
            if(startId+boxSize>= aprioriManager.getAprioriSize())
                return;
            startId+=boxSize;
            createView();

        });
        switchPageBox.getChildren().addAll(backButton,text,nextButton);
    }
}

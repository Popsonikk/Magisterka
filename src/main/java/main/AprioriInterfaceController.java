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

        Button nextButton=(Button) switchPageBox.lookup("#nButt");

        nextButton.setOnAction(e -> {
            if(startId+boxSize>= aprioriManager.getAprioriSize())
                return;
            startId+=boxSize;
            createView();

        });
    }
    @Override
    protected void createView() {

    }

    @Override
    protected void createFiltrButton() {

    }

    @Override
    protected void createHeader() {

    }


}

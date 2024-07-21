package main;


import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Scanner;

public class MainWindowController {
    private BasketManager basketManager;

    private Stage mainStage;

    private Scene basketScene;
    private BasketInterfaceController bik;

    public void setBasketManager(BasketManager basketManager) {
        this.basketManager = basketManager;
    }

    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }

    public void setBasketScene(Scene basketScene) {
        this.basketScene = basketScene;
    }

    public void setBik(BasketInterfaceController bik) {
        this.bik = bik;
    }



    public void showBasketInterface() {
        bik.init();
        mainStage.setScene(basketScene);
    }
}

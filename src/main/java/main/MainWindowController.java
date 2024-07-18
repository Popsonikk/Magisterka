package main;


import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Scanner;

public class MainWindowController {
    private BasketManager basketManager;

    private Stage mainStage;

    private Scene basketScene;

    public void setBasketManager(BasketManager basketManager) {
        this.basketManager = basketManager;
    }

    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }

    public void setBasketScene(Scene basketScene) {
        this.basketScene = basketScene;
    }

    public void loadBaskets() {

        basketManager.loadBaskets();


    }

    public void showBasketInterface() {
        mainStage.setScene(basketScene);
    }
}

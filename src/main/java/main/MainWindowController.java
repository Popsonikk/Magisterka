package main;


import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.stage.Stage;



public class MainWindowController {


    private Stage mainStage;

    private Scene basketScene;
    private AprioriManager aprioriManager;

    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }

    public void setBasketScene(Scene basketScene) {
        this.basketScene = basketScene;
    }

    public void setAprioriManager(AprioriManager aprioriManager) {
        this.aprioriManager = aprioriManager;
    }

    public void showBasketInterface() {
        mainStage.setScene(basketScene);
    }

    public void useApriori() {
        aprioriManager.initStage();
    }
}

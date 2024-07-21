package main;


import javafx.scene.Scene;
import javafx.stage.Stage;



public class MainWindowController {


    private Stage mainStage;

    private Scene basketScene;

    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }

    public void setBasketScene(Scene basketScene) {
        this.basketScene = basketScene;
    }





    public void showBasketInterface() {
        mainStage.setScene(basketScene);
    }
}

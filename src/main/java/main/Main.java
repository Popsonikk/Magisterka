package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.controllers.AprioriInterfaceController;
import main.controllers.BasketInterfaceController;
import main.controllers.MainWindowController;
import main.controllers.RuleInterfaceController;

public class Main extends Application {


    @Override
    public void start(Stage stage) throws Exception {

        //handle do kontrolerów interfejsów
        FXMLLoader mainWindowLoader = new FXMLLoader(getClass().getResource("MainWindow.fxml"));
        Parent mainWindow = mainWindowLoader.load();
        MainWindowController mainWindowController = mainWindowLoader.getController();

        FXMLLoader basketWindowLoader = new FXMLLoader(getClass().getResource("BasketInterface.fxml"));
        Parent basketWindow = basketWindowLoader.load();
        BasketInterfaceController basketInterfaceController = basketWindowLoader.getController();

        FXMLLoader aprioriWindowLoader = new FXMLLoader(getClass().getResource("AprioriInterface.fxml"));
        Parent aprioriWindow = aprioriWindowLoader.load();
        AprioriInterfaceController aprioriInterfaceController = aprioriWindowLoader.getController();

        FXMLLoader ruleWindowLoader = new FXMLLoader(getClass().getResource("RuleInterface.fxml"));
        Parent ruleWindow = ruleWindowLoader.load();
        RuleInterfaceController ruleInterfaceController=ruleWindowLoader.getController();

        mainWindowController.setMainStage(stage);
        mainWindowController.setBasketScene(new Scene(basketWindow,1000,750));
        mainWindowController.setAprioriScene(new Scene(aprioriWindow,1000,750));
        mainWindowController.setRuleScene(new Scene(ruleWindow,1000,750));

        mainWindowController.setBasketInterfaceController(basketInterfaceController);
        mainWindowController.setAprioriInterfaceController(aprioriInterfaceController);
        mainWindowController.setRuleInterfaceController(ruleInterfaceController);

        Scene mainScene=new Scene(mainWindow,1000,750);

        //ustawienie głównej sceny w każdym kontrolerze
        basketInterfaceController.setMainScene(mainScene);
        basketInterfaceController.setMainStage(stage);
        aprioriInterfaceController.setMainScene(mainScene);
        aprioriInterfaceController.setMainStage(stage);
        ruleInterfaceController.setMainScene(mainScene);
        ruleInterfaceController.setMainStage(stage);

        stage.setTitle("Analiza koszykowa");
        stage.setScene(mainScene);
        stage.show();

    }
}
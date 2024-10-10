package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.apriori.AprioriInterfaceController;
import main.apriori.AprioriManager;
import main.baskets.BasketInterfaceController;
import main.baskets.BasketManager;
import main.rules.RuleManager;

public class Main extends Application {


    @Override
    public void start(Stage stage) throws Exception {
        BasketManager basketManager=new BasketManager();
        AprioriManager aprioriManager=new AprioriManager();
        RuleManager ruleManager=new RuleManager();
        aprioriManager.setBasketManager(basketManager);
        ruleManager.setAprioriManager(aprioriManager);

        FXMLLoader mainWindowLoader = new FXMLLoader(getClass().getResource("MainWindow.fxml"));
        Parent mainWindow = mainWindowLoader.load();
        MainWindowController mainWindowController = mainWindowLoader.getController();

        FXMLLoader basketWindowLoader = new FXMLLoader(getClass().getResource("BasketInterface.fxml"));
        Parent basketWindow = basketWindowLoader.load();
        BasketInterfaceController basketInterfaceController = basketWindowLoader.getController();

        FXMLLoader aprioriWindowLoader = new FXMLLoader(getClass().getResource("AprioriInterface.fxml"));
        Parent aprioriWindow = aprioriWindowLoader.load();
        AprioriInterfaceController aprioriInterfaceController = aprioriWindowLoader.getController();

        mainWindowController.setMainStage(stage);
        mainWindowController.setBasketScene(new Scene(basketWindow,1000,750));
        mainWindowController.setAprioriScene(new Scene(aprioriWindow,1000,750));
        mainWindowController.setAprioriManager(aprioriManager);
        mainWindowController.setRuleManager(ruleManager);
        mainWindowController.setAprioriInterfaceController(aprioriInterfaceController);

        Scene mainScene=new Scene(mainWindow,1000,750);
        basketInterfaceController.setBasketManager(basketManager);
        basketInterfaceController.setMainScene(mainScene);
        basketInterfaceController.setMainStage(stage);
        aprioriInterfaceController.setMainScene(mainScene);
        aprioriInterfaceController.setMainStage(stage);
        aprioriInterfaceController.setAprioriManager(aprioriManager);

        stage.setTitle("Analiza koszykowa");
        stage.setScene(mainScene);
        stage.show();

    }
}
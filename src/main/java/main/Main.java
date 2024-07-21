package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {


    @Override
    public void start(Stage stage) throws Exception {
        BasketManager basketManager=new BasketManager();

        FXMLLoader mainWindowLoader = new FXMLLoader(getClass().getResource("MainWindow.fxml"));
        Parent mainWindow = mainWindowLoader.load();
        MainWindowController mainWindowController = mainWindowLoader.getController();

        FXMLLoader basketWindowLoader = new FXMLLoader(getClass().getResource("BasketInterface.fxml"));
        Parent basketWindow = basketWindowLoader.load();
        BasketInterfaceController basketInterfaceController = basketWindowLoader.getController();

        mainWindowController.setMainStage(stage);
        mainWindowController.setBasketScene(new Scene(basketWindow,1000,750));

        basketInterfaceController.setBasketManager(basketManager);

        stage.setTitle("Analiza koszykowa");
        stage.setScene(new Scene(mainWindow,1000,750));
        stage.show();

    }
}
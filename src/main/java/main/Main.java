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
        mainWindowController.setBasketManager(basketManager);
        stage.setTitle("Analiza koszykowa");

        stage.setScene(new Scene(mainWindow,800,600));
        stage.show();

    }
}
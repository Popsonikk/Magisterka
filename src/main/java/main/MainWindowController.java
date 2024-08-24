package main;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;



public class MainWindowController {
    @FXML
    private TextField support;
    @FXML
    private TextField length;


    private Stage mainStage;

    private Scene basketScene;
    private AprioriManager aprioriManager;
    private RuleManager ruleManager;

    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }

    public void setBasketScene(Scene basketScene) {
        this.basketScene = basketScene;
    }

    public void setAprioriManager(AprioriManager aprioriManager) {
        this.aprioriManager = aprioriManager;
    }

    public void setRuleManager(RuleManager ruleManager) {this.ruleManager = ruleManager;}

    public void showBasketInterface() {
        mainStage.setScene(basketScene);
    }

    public void useApriori() {
        aprioriManager.Apriori(Double.parseDouble(support.getText()),Integer.parseInt(length.getText()));
        support.setText("0.1");
        length.setText("3");

    }

    public void useRules() {
        ruleManager.generateRules();
    }
}

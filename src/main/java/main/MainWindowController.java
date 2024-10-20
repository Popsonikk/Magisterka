package main;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import main.apriori.AprioriInterfaceController;
import main.apriori.AprioriManager;
import main.rules.RuleInterfaceController;
import main.rules.RuleManager;
public class MainWindowController {
    @FXML
    public TextField confidence;
    @FXML
    public TextField lift;
    @FXML
    private TextField support;
    @FXML
    private TextField length;
    private AprioriInterfaceController aprioriInterfaceController;
    private RuleInterfaceController ruleInterfaceController;
    private Stage mainStage;
    private Scene basketScene;
    private Scene aprioriScene;
    private Scene ruleScene;
    private AprioriManager aprioriManager;
    private RuleManager ruleManager;
    public void setMainStage(Stage mainStage) {this.mainStage = mainStage;}
    public void setBasketScene(Scene basketScene) {this.basketScene = basketScene;}
    public void setAprioriScene(Scene aprioriScene) {this.aprioriScene = aprioriScene;}
    public void setRuleScene(Scene ruleScene) {this.ruleScene = ruleScene;}

    public void setRuleInterfaceController(RuleInterfaceController ruleInterfaceController) {
        this.ruleInterfaceController = ruleInterfaceController;
    }

    public void setAprioriManager(AprioriManager aprioriManager) {this.aprioriManager = aprioriManager;}
    public void setRuleManager(RuleManager ruleManager) {this.ruleManager = ruleManager;}
    public void showBasketInterface() {mainStage.setScene(basketScene);}
    public void setAprioriInterfaceController(AprioriInterfaceController aprioriInterfaceController) {
        this.aprioriInterfaceController = aprioriInterfaceController;
    }
    public void useApriori() {
        aprioriManager.Apriori(Double.parseDouble(support.getText()),Integer.parseInt(length.getText()));
        support.setText("0.1");
        length.setText("3");
    }
    public void useRules() {
        ruleManager.generateRules(Double.parseDouble(confidence.getText()),Integer.parseInt(lift.getText()));
        confidence.setText("0.25");
        lift.setText("0.75");
    }

    public void showAprioriInterface() {
        if(aprioriManager.getSupportListSize()>0)
        {
            aprioriInterfaceController.deleteHeader();
            aprioriInterfaceController.showTable();
        }
        mainStage.setScene(aprioriScene);
    }
    public void showRuleInterface() {
        if(ruleManager.getRuleListSize()>0)
        {
            ruleInterfaceController.deleteHeader();
            ruleInterfaceController.showTable();
        }
        mainStage.setScene(aprioriScene);
        mainStage.setScene(ruleScene);
    }
}

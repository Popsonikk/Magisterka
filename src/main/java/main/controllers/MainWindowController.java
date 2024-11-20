package main.controllers;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import main.Neo4jConnector;
import main.objects.AssociationRule;
import main.functions.GeneratePattern;
import main.objects.SimplePattern;
import org.neo4j.driver.Record;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MainWindowController implements Initializable {
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
    private BasketInterfaceController basketInterfaceController;
    private GraphInterfaceController graphInterfaceController;
    private Stage mainStage;
    private Scene basketScene;
    private Scene aprioriScene;
    private Scene ruleScene;
    private Scene graphScene;
    public void setMainStage(Stage mainStage) {this.mainStage = mainStage;}
    public void setBasketScene(Scene basketScene) {this.basketScene = basketScene;}
    public void setAprioriScene(Scene aprioriScene) {this.aprioriScene = aprioriScene;}
    public void setRuleScene(Scene ruleScene) {this.ruleScene = ruleScene;}
    public void setGraphScene(Scene graphScene) {this.graphScene = graphScene;}

    public void setRuleInterfaceController(RuleInterfaceController ruleInterfaceController) {
        this.ruleInterfaceController = ruleInterfaceController;
    }

    public void setAprioriInterfaceController(AprioriInterfaceController aprioriInterfaceController) {
        this.aprioriInterfaceController = aprioriInterfaceController;
    }

    public void setBasketInterfaceController(BasketInterfaceController basketInterfaceController) {
        this.basketInterfaceController = basketInterfaceController;
    }

    public void setGraphInterfaceController(GraphInterfaceController graphInterfaceController) {
        this.graphInterfaceController = graphInterfaceController;
    }

    public void showBasketInterface() {mainStage.setScene(basketScene);}

    public void useApriori() {
        if(basketInterfaceController.getData().getDataSize()==0)
        {
            basketInterfaceController.createAlert(1,"Brak danych wejściowych!");
            return;
        }
        List<SimplePattern> patterns= GeneratePattern.apriori(Double.parseDouble(support.getText()),Integer.parseInt(length.getText())
                ,basketInterfaceController.getData().getData());
        aprioriInterfaceController.getData().setData(patterns);
        support.setText("0.1");
        length.setText("3");
    }
    public void useRules() {
        if(aprioriInterfaceController.getData().getDataSize()==0)
        {
            aprioriInterfaceController.createAlert(1,"Brak danych wejściowych!");
            return;
        }
        List<AssociationRule> rules= GeneratePattern.generateRules(Double.parseDouble(confidence.getText()),Double.parseDouble(lift.getText())
        ,aprioriInterfaceController.getData().getData());
        ruleInterfaceController.getData().setData(rules);
        confidence.setText("0.25");
        lift.setText("0.75");
    }

    public void showAprioriInterface() {
        if(aprioriInterfaceController.getData().getDataSize()>0)
            aprioriInterfaceController.createView();
        mainStage.setScene(aprioriScene);
    }
    public void showRuleInterface() {
        if(ruleInterfaceController.getData().getDataSize()>0)
            ruleInterfaceController.createView();
        mainStage.setScene(ruleScene);
    }

    public void showGraphInterface() {
        mainStage.setScene(graphScene);
    }

    public void openNeo()
    {
        try (var greeter = new Neo4jConnector("neo4j+s://89d4ca68.databases.neo4j.io", "neo4j", "1XJ3eBvRUKicjfVdeIDmdsnc-b7tEOGMGnnLWyCem8c")) {
            greeter.printGreeting("hello, world");
            List<Record> res=greeter.getRecomendation("Angela Thompson","Forrest Gump");
            StringBuilder builder=new StringBuilder("Polecane filmy \n");
            for (Record r:res)
            {
                builder.append(r.get(0)).append("\n");
            }
            Alert a=new Alert(Alert.AlertType.INFORMATION);
            a.setContentText(builder.toString());
            a.show();
        }


    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}

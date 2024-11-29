package main.controllers;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import main.Neo4jConnector;
import main.RecommendationFunctions;
import main.objects.AssociationRule;
import main.functions.GeneratePattern;
import main.objects.SimplePattern;
import org.neo4j.driver.Record;

import java.net.URL;
import java.util.*;

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
        confidence.setText("0.1");
        lift.setText("0.75");
    }

    public void showAprioriInterface() {
        if(aprioriInterfaceController.getData().getDataSize()>0)
            aprioriInterfaceController.showTable();
        mainStage.setScene(aprioriScene);
    }
    public void showRuleInterface() {
        if(ruleInterfaceController.getData().getDataSize()>0)
            ruleInterfaceController.showTable();;
        mainStage.setScene(ruleScene);
    }

    public void showGraphInterface() {
        mainStage.setScene(graphScene);
    }

    public void openNeo()
    {

        try (var conn = new Neo4jConnector("neo4j+s://b80ce237.databases.neo4j.io", "neo4j", "STRyE_-kb6p8vYYFkPA2U23Ax-okWxlHd6Jjw_LxYow")) {
            if(conn.doesAnyRecordExist())
                conn.cleanBase();
            Date d1 = new Date();
            conn.createNodes(aprioriInterfaceController.getData().getData());
            Date d2 = new Date();
            System.out.println(d2.getTime()-d1.getTime());
            d1=new Date();
            conn.createEdges(ruleInterfaceController.getData().getData());
            d2 = new Date();
            System.out.println(d2.getTime()-d1.getTime());
            aprioriInterfaceController.createAlert(1,"Pomyślnie wczytano dane do bazy neo4j");
            System.out.println(RecommendationFunctions.processNeo4jOutput(conn.checkNeighbourhood(),aprioriInterfaceController.getProductList()));

        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}

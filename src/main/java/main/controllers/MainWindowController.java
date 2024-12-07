package main.controllers;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import main.Neo4jConnector;
import main.RecommendationFunctions;
import main.objects.AssociationRule;
import main.functions.GeneratePattern;
import main.objects.Node;
import main.objects.SimplePattern;

import java.net.URL;
import java.util.*;


public class MainWindowController implements Initializable {

    @FXML
    public Pane pane;

    private AprioriInterfaceController aprioriInterfaceController;
    private RuleInterfaceController ruleInterfaceController;
    private BasketInterfaceController basketInterfaceController;
    private GraphInterfaceController graphInterfaceController;
    private ResultInterfaceController resultInterfaceController;
    private Stage mainStage;
    private Scene basketScene;
    private Scene aprioriScene;
    private Scene ruleScene;
    private Scene graphScene;
    private Scene resScene;
    private Map<String, Double> productStrength;
    private Map <Node,Integer> distances;
    private Map<Node,String> recommendation;
    public void setMainStage(Stage mainStage) {this.mainStage = mainStage;}
    public void setBasketScene(Scene basketScene) {this.basketScene = basketScene;}
    public void setAprioriScene(Scene aprioriScene) {this.aprioriScene = aprioriScene;}
    public void setRuleScene(Scene ruleScene) {this.ruleScene = ruleScene;}
    public void setGraphScene(Scene graphScene) {this.graphScene = graphScene;}
    public void setResultInterfaceController(ResultInterfaceController resultInterfaceController) {
        this.resultInterfaceController = resultInterfaceController;
    }
    public void setResScene(Scene resScene) {
        this.resScene = resScene;
    }
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
    public void showBasketInterface() {
        if (!basketInterfaceController.isHeaderActive()&&!basketInterfaceController.getData().getData().isEmpty())
            basketInterfaceController.showTable();
        mainStage.setScene(basketScene);
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
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        productStrength = new HashMap<>();
        distances= new HashMap<>();
        recommendation=new HashMap<>();
        //pojedyncze pudło z danym segmentem aplikacji
        VBox sklep=createBox(25,25,"Sklep");
        sklep.setSpacing(50.0);
        Button basketInterface=createButton("Zarządzaj koszykami");
        basketInterface.setOnAction(e->showBasketInterface());
        Button graphInterface=createButton("Zarządzaj grafami");
        graphInterface.setOnAction(e->showGraphInterface());
        sklep.getChildren().addAll(basketInterface,graphInterface);

        VBox apriori=createBox(350,25,"Apriori");
        apriori.setSpacing(15.0);
        HBox suppBox=createEnterBox("Podaj wsparcie");
        HBox lenBox=createEnterBox("Podaj długość");
        Button aprioriInterface=createButton("Zarządzaj wsparciem");
        aprioriInterface.setOnAction(e->showAprioriInterface());
        Button startApriori=createButton("Uruchom Apriori");
        startApriori.setOnAction(e->{
            if(basketInterfaceController.getData().getDataSize()==0)
            {
                createAlert(2,"Brak wczytanych koszyków!");
                return;
            }
            TextField s= (TextField) suppBox.getChildren().get(1);
            TextField l= (TextField) lenBox.getChildren().get(1);
            if(s.getText().isEmpty()||l.getText().isEmpty())
            {
                createAlert(2,"Brak podanych parametrów!");
                return;
            }

            List<SimplePattern> patterns= GeneratePattern.apriori(Double.parseDouble(s.getText()),Integer.parseInt(l.getText())
                    ,basketInterfaceController.getData().getData());
            aprioriInterfaceController.getData().setData(patterns);
            s.clear();
            l.clear();
            createAlert(1,"Dane wygenerowane pomyślnie");
        });
        apriori.getChildren().addAll(aprioriInterface,suppBox,lenBox,startApriori);

        VBox reguly=createBox(675,25,"Reguły");
        reguly.setSpacing(15.0);
        HBox confBox=createEnterBox("Podaj ufność");
        HBox liftBox=createEnterBox("Podaj lift");
        Button ruleInterface=createButton("Zarządzaj regułami");
        ruleInterface.setOnAction(e->showRuleInterface());
        Button startRule=createButton("Uruchom Reguły");
        startRule.setOnAction(e->{
            if(aprioriInterfaceController.getData().getDataSize()==0)
            {
                createAlert(2,"Brak wczytanego wsparcia!");
                return;
            }
            TextField c= (TextField) confBox.getChildren().get(1);
            TextField l= (TextField) liftBox.getChildren().get(1);
            if(c.getText().isEmpty()||l.getText().isEmpty())
            {
                createAlert(2,"Brak podanych parametrów!");
                return;
            }

            List<AssociationRule> rules=GeneratePattern.generateRules(Double.parseDouble(c.getText()),
                    Double.parseDouble(l.getText()),aprioriInterfaceController.getData().getData());
            ruleInterfaceController.getData().setData(rules);
            c.clear();
            l.clear();
            createAlert(1,"Dane wygenerowane pomyślnie");
        });
        reguly.getChildren().addAll(ruleInterface,confBox,liftBox,startRule);

        VBox neo4j=createBox(200,375,"Neo4j");
        neo4j.setSpacing(30.0);
        HBox urlBox=createEnterBox("Podaj url");
        HBox passBox=createEnterBox("Podaj hasło");
        Button startNeo=createButton("Uruchom Reguły");
        startNeo.setOnAction((e)->{
            if(aprioriInterfaceController.getData().getDataSize()==0)
            {
                createAlert(2,"Brak wczytanego wsparcia!");
                return;
            }
            if(ruleInterfaceController.getData().getDataSize()==0)
            {
                createAlert(2,"Brak wczytanych reguł!");
                return;
            }
            TextField u= (TextField) urlBox.getChildren().get(1);
            TextField p= (TextField) passBox.getChildren().get(1);

            if(u.getText().isEmpty()||p.getText().isEmpty())
            {
                createAlert(2,"Brak podanych parametrów!");
                return;
            }
            try (var conn = new Neo4jConnector(u.getText(), "neo4j", p.getText()))
            {

                if(conn.doesAnyRecordExist())
                    conn.cleanBase();
                conn.createNodes(aprioriInterfaceController.getData().getData());
                conn.createEdges(ruleInterfaceController.getData().getData());

                productStrength=RecommendationFunctions.processNeo4jOutput(conn.checkNeighbourhood(), aprioriInterfaceController.getProductList());
            }
            StringBuilder builder=new StringBuilder();
            builder.append("Produkt").append(": ").append("Siła").append("\n");
            productStrength.forEach((k,v)-> builder.append(k).append(": ").append(String.format(Locale.US, "%.3f", v)).append("\n"));
            Alert alert=new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Współczynnik jakości produktów");
            alert.setContentText(builder.toString());
            alert.show();

        });
        neo4j.getChildren().addAll(urlBox,passBox,startNeo);



        VBox sat=createBox(525,375,"Rekomendacja");
        sat.setSpacing(30.0);
        Button alg=createButton("Uruchom algorytm");
        HBox nodeBox=createEnterBox("Podaj nodeId");
        alg.setOnAction(e->{
            if(graphInterfaceController.getGraph().getNodes().isEmpty())
            {
                createAlert(2,"Brak wczytanego grafu!");
                return;
            }
            TextField n= (TextField) nodeBox.getChildren().get(1);
            if(n.getText().isEmpty())
            {
                createAlert(2,"Brak podanych parametrów!");
                return;
            }
            Node startNode;
            startNode=graphInterfaceController.getGraph().findNode(n.getText());
            if(startNode==null)
            {
                createAlert(2,"Brak podanego node!");
                return;
            }
            distances=RecommendationFunctions.findShortestPaths(graphInterfaceController.getGraph(),startNode);
            recommendation=RecommendationFunctions.neo4jRecommendation(new ArrayList<>(productStrength.keySet()),
                    new ArrayList<>(distances.keySet()),graphInterfaceController.getGraph());
            createAlert(1,"Przetwarzanie danych zakończone pomyślnie");
        });
        Button showRes=createButton("Pokaż wyniki");
        showRes.setOnAction(e->{
            if(distances.isEmpty()||recommendation.isEmpty())
            {
                createAlert(2,"Brak danych!");
                return;
            }
            if(graphInterfaceController.getGraph().getNodes().isEmpty())
            {
                createAlert(2,"Brak wczytanego grafu!");
                return;
            }
            resultInterfaceController.showResult(new HashMap<>(distances),new HashMap<>(recommendation),graphInterfaceController.getGraph());
            mainStage.setScene(resScene);
        });

        sat.getChildren().addAll(nodeBox,alg,showRes);
        pane.getChildren().addAll(sklep,apriori,reguly,neo4j,sat);
    }
    private VBox createBox(int x,int y,String title)
    {
        VBox box=new VBox();
        box.setLayoutY(y);
        box.setLayoutX(x);
        box.getStyleClass().add("mainWindowBox");
        Text t=new Text(title);
        t.getStyleClass().add("mainWindowText");
        t.setWrappingWidth(275);
        box.getChildren().addAll(t);
        return box;
    }
    private Button createButton(String name)
    {
        Button button=new Button(name);
        button.getStyleClass().add("interfaceButton");
        button.setStyle("-fx-pref-width: 175px;");
        return button;
    }
    private HBox createEnterBox(String t)
    {
        HBox box=new HBox();
        Text text=new Text(t);
        text.getStyleClass().add("mainWindowText");
        text.setWrappingWidth(140);
        TextField textField=new TextField();
        textField.setPrefWidth(135);
        box.getChildren().addAll(text,textField);
        return box;

    }
    public void createAlert(int type,String value){
        switch (type) {
            case 1 -> {
                Alert a = new Alert(Alert.AlertType.INFORMATION, value);
                a.show();
            }
            case 2 -> {
                Alert a = new Alert(Alert.AlertType.ERROR, value);
                a.show();
            }
        }
    }

}

package main.controllers;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import main.functions.Neo4jConnector;
import main.functions.RecommendationFunctions;
import main.objects.AssociationRule;
import main.functions.GeneratePattern;
import main.objects.Node;
import main.objects.SimplePattern;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
    private ProductInterfaceController productInterfaceController;
    private Stage mainStage;
    private Scene basketScene;
    private Scene aprioriScene;
    private Scene ruleScene;
    private Scene graphScene;
    private Scene resScene;
    private Scene productScene;
    private Map<String, Double> productStrength;
    private Map <Node,Integer> distances;
    private Map<Node,String> recommendation;
    public void setMainStage(Stage mainStage) {this.mainStage = mainStage;}
    public void setBasketScene(Scene basketScene) {this.basketScene = basketScene;}
    public void setAprioriScene(Scene aprioriScene) {this.aprioriScene = aprioriScene;}
    public void setRuleScene(Scene ruleScene) {this.ruleScene = ruleScene;}
    public void setGraphScene(Scene graphScene) {this.graphScene = graphScene;}

    public void setProductInterfaceController(ProductInterfaceController productInterfaceController) {
        this.productInterfaceController = productInterfaceController;
    }

    public void setProductScene(Scene productScene) {
        this.productScene = productScene;
    }

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
    public void showProductInterface() {

        mainStage.setScene(productScene);
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
        sklep.setSpacing(25.0);
        Button basketInterface=createButton("Zarządzaj koszykami");
        basketInterface.setOnAction(e->showBasketInterface());
        Button graphInterface=createButton("Zarządzaj grafami");
        graphInterface.setOnAction(e->showGraphInterface());
        Button itemInterface=createButton("Zarządzaj produktami");
        itemInterface.setOnAction(e->showProductInterface());
        sklep.getChildren().addAll(basketInterface,graphInterface,itemInterface);

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
            Stage stage = getStage();
            aprioriInterfaceController.getData().getFiltrInfo().clearFiltr();
            Task<Void> task = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    List<SimplePattern> patterns= GeneratePattern.apriori(Double.parseDouble(s.getText()),Integer.parseInt(l.getText())
                            ,basketInterfaceController.getData().getData());
                    aprioriInterfaceController.getData().setData(patterns);
                    s.clear();
                    l.clear();
                    return null;
                }
            };
            task.setOnSucceeded(event -> {
                stage.close();
                createAlert(1,"Dane wygenerowane pomyślnie");
            });

            // Uruchomienie zadania w tle
            new Thread(task).start();

            // Wyświetlenie okna "Proszę czekać"
            stage.show();



        });
        apriori.getChildren().addAll(aprioriInterface,suppBox,lenBox,startApriori);

        VBox reguly=createBox(675,25,"Reguły");
        reguly.setSpacing(15.0);
        HBox confBox=createEnterBox("Podaj ufność");
        HBox liftBox=createEnterBox("Podaj dźwignię");
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
            Stage stage = getStage();
            ruleInterfaceController.getData().getFiltrInfo().clearFiltr();
            Task<Void> task = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    List<AssociationRule> rules=GeneratePattern.generateRules(Double.parseDouble(c.getText()),
                            Double.parseDouble(l.getText()),aprioriInterfaceController.getData().getData());
                    ruleInterfaceController.getData().setData(rules);
                    c.clear();
                    l.clear();
                    return null;
                }
            };
            task.setOnSucceeded(event -> {
                stage.close();
                createAlert(1,"Dane wygenerowane pomyślnie");
            });
            // Uruchomienie zadania w tle
            new Thread(task).start();
            // Wyświetlenie okna "Proszę czekać"
            stage.show();

        });
        reguly.getChildren().addAll(ruleInterface,confBox,liftBox,startRule);

        VBox neo4j=createBox(200,375,"Neo4j");
        neo4j.setSpacing(15.0);
        HBox urlBox=createEnterBox("Podaj url");
        HBox passBox=createEnterBox("Podaj hasło");
        Button startNeo=createButton("Uruchom Neo4j");
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
            String urlVal,passVal;
            if(u.getText().isEmpty()&&p.getText().isEmpty())
            {
                urlVal="neo4j+s://bf4d3846.databases.neo4j.io";
                passVal="Y50WLbzeZrwCQy-XeioQXrPHHhNV_Z3-6Z_HlTn3vdI";
            }
            else {
                if(u.getText().isEmpty()||p.getText().isEmpty())
                {
                    createAlert(2,"Brak podanych parametrów!");
                    return;
                }
                else {
                    urlVal="neo4j+s://"+u.getText()+".databases.neo4j.io";
                    passVal=p.getText();
                }
            }


            Stage stage = getStage();
            Task<Void> task = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    try (var conn = new Neo4jConnector(urlVal, "neo4j", passVal))
                    {

                        if(conn.doesAnyRecordExist())
                            conn.cleanBase();
                        conn.createNodes(aprioriInterfaceController.getData().getData());
                        conn.createEdges(ruleInterfaceController.getData().getData());
                        productStrength=RecommendationFunctions.processNeo4jOutput(conn.checkNeighbourhood(), aprioriInterfaceController.getProductList());

                    }
                    catch(Exception exception)
                    {
                        createAlert(2,"Błąd połączenia");
                        return null;
                    }
                    return null;
                }
            };
            task.setOnSucceeded(event -> {
                stage.close();
                createAlert(1,"Dane przetworzone pomyślnie");
            });
            // Uruchomienie zadania w tle
            new Thread(task).start();
            // Wyświetlenie okna "Proszę czekać"
            stage.show();

        });
        Button showNeo4jOutput=createButton("Wynik neo4j");
        showNeo4jOutput.setOnAction(e->{
            if(productStrength.isEmpty())
            {
                createAlert(2,"Brak danych!");
                return;
            }
            File file=new File("dane/neo4j-output.txt");
            try {
                BufferedWriter writer=new BufferedWriter(new FileWriter(file));
                writer.write("Produkt"+","+"Siła"+"\n");
                productStrength.forEach((k,v)->
                {
                    try {
                        writer.write(k+","+String.format(Locale.US, "%.3f", v)+"\n");
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                });
                writer.close();
                createAlert(1,"Wygenerowano pomyślnie plik");

            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

        });
        neo4j.getChildren().addAll(urlBox,passBox,startNeo,showNeo4jOutput);



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
                    new ArrayList<>(distances.keySet()),new ArrayList<>(productInterfaceController.getProducts()));
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
            resultInterfaceController.showResult(new LinkedHashMap<>(distances),new LinkedHashMap<>(recommendation),graphInterfaceController.getGraph(),productInterfaceController.getProducts());
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
    private Stage getStage()
    {
        Stage stage = new Stage();
        stage.setTitle("Proszę czekać");

        ProgressIndicator progressIndicator = new ProgressIndicator();
        Label messageLabel = new Label("Trwa operacja...");

        VBox layout = new VBox(10, progressIndicator, messageLabel);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
        stage.setScene(new Scene(layout));
        return stage;
    }

}

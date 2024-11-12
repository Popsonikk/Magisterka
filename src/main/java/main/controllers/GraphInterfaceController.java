package main.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import main.objects.Edge;
import main.objects.Graph;
import main.objects.Node;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class GraphInterfaceController implements Initializable {
    @FXML
    public ScrollPane pane;
    @FXML
    public Pane canvas;
    @FXML
    public Pane mainPane;
    @FXML HBox menuBox;
    private Scene mainScene;
    private Stage mainStage;
    private Graph graph;

    public void setMainScene(Scene mainScene) {this.mainScene = mainScene;}
    public void setMainStage(Stage mainStage) {this.mainStage = mainStage;}
    public void back() {mainStage.setScene(mainScene);}

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Button button=new Button("Powrót");
        button.getStyleClass().add("backButton");
        button.setOnAction(e->back());
        menuBox.getChildren().add(button);
        graph=new Graph();
        createAddCircleBox();
        createAddEdgeBox();
    }
    //tworzenie pojedynczego węzła, domyslnie tworzymy w wyznaczonych miejscach
    private void createNode( int x, int y) {
        //węzeł reprezentowany jako koło
        Circle node = new Circle((25+x*50.0), (25+y*50.0), 20);
        node.getStyleClass().add("circle");
        //tekst z nazwą węzła
        int s=graph.getNodes().size();
        Text text = new Text(); // Ustawienie tekstu w centrum węzła
        text.setX(node.getCenterX() - 5);
        text.setY(node.getCenterY() + 5);
        text.setText(Integer.toString(s));
        text.getStyleClass().add("circleText");
        Group nodeGroup=new Group(node,text);
        //przesuwanie węzła na planszy
        nodeGroup.setOnMousePressed(event -> {
            //zapamiętanie obecnej pozycji
            node.setUserData(new double[]{event.getSceneX(), event.getSceneY()});
        });
        nodeGroup.setOnMouseDragged(event -> {
            //pobranie zapamiętanej pozycji
            double[] offset = (double[]) node.getUserData();
            double deltaX = event.getSceneX() - offset[0];
            double deltaY = event.getSceneY() - offset[1];
            //przesunięcie o różnice
            node.setCenterX(node.getCenterX() + deltaX);
            node.setCenterY(node.getCenterY() + deltaY);
            text.setX(node.getCenterX() - 5);
            text.setY(node.getCenterY() + 5);
            node.setUserData(new double[]{event.getSceneX(), event.getSceneY()});

        });
        canvas.getChildren().addAll(nodeGroup);
        graph.getNodes().add(new Node(node,s));
        createAlert(1,"Węzeł stworzony pomyślnie");
    }
    private void createEdge(Node n1, Node n2) {
        graph.getEdges().add(new Edge(n1.getId(), n2.getId(), 0));
        Circle start=n1.getCircle();
        Circle end=n2.getCircle();
        Line edge = new Line();

        Text weightText = new Text();
        updateEdgePosition(edge, start, end,weightText);
        weightText.getStyleClass().add("basketText");
        edge.getStyleClass().add("line");
        // Pole do wpisywania wagi, które pojawia się po kliknięciu na linię


        canvas.getChildren().addAll(edge, weightText);

        // Aktualizacja pozycji krawędzi i wagi, gdy węzeł jest przeciągany
        start.centerXProperty().addListener((obs, oldVal, newVal) -> updateEdgePosition(edge, start, end,weightText));
        start.centerYProperty().addListener((obs, oldVal, newVal) -> updateEdgePosition(edge, start, end,weightText));
        end.centerXProperty().addListener((obs, oldVal, newVal) -> updateEdgePosition(edge, start, end,weightText));
        end.centerYProperty().addListener((obs, oldVal, newVal) -> updateEdgePosition(edge, start, end,weightText));

        // Kliknięcie na linię, aby wprowadzić wagę
        edge.setOnMouseClicked((MouseEvent event) -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setHeaderText("Wprowadź wartosć wagi");
            Optional<String> res = dialog.showAndWait();
            String weight="";
            if (res.isPresent())
                weight = res.get();
            if(weight.equals(""))
            {
                createAlert(2,"NIe podałes wagi krawędzi!");
                return;
            }
            weightText.setText(weight);
            for(Edge l:graph.getEdges())
            {
                if(l.getNode1ID()== n1.getId()&&l.getNode2ID()== n2.getId()||(l.getNode2ID()== n1.getId()&&l.getNode1ID()== n2.getId()))
                    l.setWeight(Integer.parseInt(weightText.getText()));
            }
        });
        createAlert(1,"Połączenie stworzone pomyślnie");
    }
    // Aktualizacja pozycji krawędzi, aby zaczynała i kończyła się na krawędzi okręgu
    private void updateEdgePosition(Line edge, Circle start, Circle end,Text text) {
        double startX = start.getCenterX();
        double startY = start.getCenterY();
        double endX = end.getCenterX();
        double endY = end.getCenterY();

        // Obliczenia dla punktu początkowego (krawędź okręgu startowego)
        double angleStart = Math.atan2(endY - startY, endX - startX);
        edge.setStartX(startX + start.getRadius() * Math.cos(angleStart));
        edge.setStartY(startY + start.getRadius() * Math.sin(angleStart));

        // Obliczenia dla punktu końcowego (krawędź okręgu końcowego)
        double angleEnd = Math.atan2(startY - endY, startX - endX);
        edge.setEndX(endX + end.getRadius() * Math.cos(angleEnd));
        edge.setEndY(endY + end.getRadius() * Math.sin(angleEnd));
        text.setX(((edge.getStartX() + edge.getEndX()) / 2)+5);
        text.setY(((edge.getStartY() + edge.getEndY()) / 2)+5);
    }
    private void createAddCircleBox()
    {
        HBox menu=new HBox();
        menu.getStyleClass().add("infoBoxGraph");
        menu.setPrefHeight(50.0);
        Text x=new Text("X: ");
        x.getStyleClass().add("infoBoxTextGraph");
        Text y=new Text("Y: ");
        y.getStyleClass().add("infoBoxTextGraph");
        TextField xField=new TextField();
        xField.getStyleClass().add("filterBoxGraph");
        TextField yField=new TextField();
        yField.getStyleClass().add("filterBoxGraph");
        Button add=new Button("Dodaj węzeł");
        add.prefHeight(50.0);
        add.setOnAction(e->{
            createNode(Integer.parseInt(xField.getText()),Integer.parseInt(yField.getText()));
            xField.clear();
            yField.clear();
        });
        add.getStyleClass().add("boxButton");
        menu.getChildren().addAll(x,xField,y,yField,add);
        menuBox.getChildren().add(menu);
    }
    private void createAddEdgeBox()
    {
        HBox menu=new HBox();
        menu.getStyleClass().add("infoBoxGraph");
        menu.setPrefHeight(50.0);
        Text n1=new Text("n1: ");
        n1.getStyleClass().add("infoBoxTextGraph");
        Text n2=new Text("n2: ");
        n2.getStyleClass().add("infoBoxTextGraph");
        TextField n1Field=new TextField();
        n1Field.getStyleClass().add("filterBoxGraph");
        TextField n2Field=new TextField();
        n2Field.getStyleClass().add("filterBoxGraph");
        Button add=new Button("Dodaj krawędź");
        add.setOnAction(e->{
            int node1=Integer.parseInt(n1Field.getText());
            int node2=Integer.parseInt(n2Field.getText());
            if(node2>graph.getNodes().size()||node1>graph.getNodes().size())
            {
                createAlert(2,"Podany węzeł nie istnieje");
                return;
            }
            n1Field.clear();
            n2Field.clear();
            for(Edge l:graph.getEdges())
            {
                if(l.getNode1ID()== node1&&l.getNode2ID()== node2||(l.getNode2ID()== node1&&l.getNode1ID()== node2))
                {
                    createAlert(2,"Krawędź już istnieje!");
                    return;
                }
            }
            createEdge(graph.getNodes().get(node1),graph.getNodes().get(node2));
        });
        add.getStyleClass().add("boxButton");
        menu.getChildren().addAll(n1,n1Field,n2,n2Field,add);
        menuBox.getChildren().add(menu);

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

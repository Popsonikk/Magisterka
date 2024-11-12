package main.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class GraphInterfaceController implements Initializable {
    @FXML
    public ScrollPane pane;
    @FXML
    public Pane canvas;
    @FXML
    public Pane mainPane;
    private Scene mainScene;
    private Stage mainStage;
    private List<Circle> nodeList;

    public void setMainScene(Scene mainScene) {this.mainScene = mainScene;}
    public void setMainStage(Stage mainStage) {this.mainStage = mainStage;}
    public void back() {mainStage.setScene(mainScene);}

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        nodeList=new ArrayList<>();
        createAddCircleBox();

        /*




        Circle node2 = createNode(pane,300, 200, "B",radius);

        // Dodawanie węzłów do panelu


        // Tworzenie krawędzi (Line) między dwoma węzłami z możliwością dodania wagi
        Line edge = createEdgeWithWeight(pane, node1, node2);

        Scene scene = new Scene(pane, 400, 400);
        primaryStage.setTitle("Graph Interface Example");
        primaryStage.setScene(scene);
        primaryStage.show();
    }





    private Line createEdgeWithWeight(Pane pane, Circle start, Circle end) {
        Line edge = new Line();
        updateEdgePosition(edge, start, end);
        edge.setStrokeWidth(3);
        edge.setStroke(Color.GRAY);

        // Tekst dla wagi krawędzi
        Text weightText = new Text();
        weightText.setX((edge.getStartX() + edge.getEndX()) / 2);
        weightText.setY((edge.getStartY() + edge.getEndY()) / 2);
        weightText.setFill(Color.BLACK);

        // Pole do wpisywania wagi, które pojawia się po kliknięciu na linię
        TextField weightInput = new TextField();
        weightInput.setVisible(false);
        pane.getChildren().addAll(edge, weightText, weightInput);

        // Aktualizacja pozycji krawędzi i wagi, gdy węzeł jest przeciągany
        start.centerXProperty().addListener((obs, oldVal, newVal) -> updateEdgePosition(edge, start, end));
        start.centerYProperty().addListener((obs, oldVal, newVal) -> updateEdgePosition(edge, start, end));
        end.centerXProperty().addListener((obs, oldVal, newVal) -> updateEdgePosition(edge, start, end));
        end.centerYProperty().addListener((obs, oldVal, newVal) -> updateEdgePosition(edge, start, end));

        // Kliknięcie na linię, aby wprowadzić wagę
        edge.setOnMouseClicked((MouseEvent event) -> {
            weightInput.setLayoutX((edge.getStartX() + edge.getEndX()) / 2);
            weightInput.setLayoutY((edge.getStartY() + edge.getEndY()) / 2);
            weightInput.setVisible(true);
            weightInput.requestFocus();
        });

        // Zdarzenie po zatwierdzeniu wagi
        weightInput.setOnAction(e -> {
            weightText.setText(weightInput.getText());
            weightInput.setVisible(false);
        });
        pane.setOnMouseClicked(event -> {



        });
        pane.setOnMouseClicked(event -> {
            // Ukryj pole tekstowe przy kliknięciu poza linią
            if (!edge.contains(event.getX(), event.getY())) {
                if(weightInput.isVisible())
                {
                    weightInput.setVisible(false);
                    weightInput.clear();
                }
            }
        });

        return edge;
    }

    // Aktualizacja pozycji krawędzi, aby zaczynała i kończyła się na krawędzi okręgu
    private void updateEdgePosition(Line edge, Circle start, Circle end) {
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
    }*/
    }
    //tworzenie pojedynczego węzła, domyslnie tworzymy w wyznaczonych miejscach
    private void createNode( int x, int y) {
        //węzeł reprezentowany jako koło
        Circle node = new Circle((25+x*50.0), (25+y*50.0), 20);
        node.getStyleClass().add("circle");
        //tekst z nazwą węzła
        int s=nodeList.size();
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
        nodeList.add(node);
        createAlert(1,"Węzeł stworzony pomyślnie");
    }
    private void createAddCircleBox()
    {
        HBox menu=new HBox();
        menu.getStyleClass().add("infoBoxGraph");
        menu.setLayoutX(125.0);
        menu.setLayoutY(5.0);
        menu.setPrefHeight(50.0);
        Text x=new Text("X: ");
        x.getStyleClass().add("infoBoxTextGraph");
        Text y=new Text("Y: ");
        y.getStyleClass().add("infoBoxTextGraph");
        TextField xField=new TextField();
        xField.getStyleClass().add("filterBoxGraph");
        TextField yField=new TextField();
        yField.getStyleClass().add("filterBoxGraph");
        Button add=new Button("Dodaj");
        add.prefHeight(50.0);
        add.setOnAction(e->{
            createNode(Integer.parseInt(xField.getText()),Integer.parseInt(yField.getText()));
            xField.clear();
            yField.clear();
        });
        add.getStyleClass().add("boxButton");
        menu.getChildren().addAll(x,xField,y,yField,add);
        mainPane.getChildren().add(menu);
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

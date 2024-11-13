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
import java.util.*;

public class GraphInterfaceController implements Initializable {
    @FXML
    public ScrollPane pane;
    @FXML
    public Pane canvas;
    @FXML
    public Pane mainPane;
    @FXML
    public HBox menuBox;
    @FXML
    public HBox bottomBox;
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
        Button clear=new Button("Wyczyść");
        clear.getStyleClass().add("backButton");
        clear.setOnAction(e->clearCanvas());
        bottomBox.getChildren().addAll(button,clear);
        bottomBox.setSpacing(5.0);
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
        int s=graph.getSize();
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
            double xPos=node.getCenterX() + deltaX;
            double yPos=node.getCenterY() + deltaY;
            if(xPos<20)
                node.setCenterX(20);
            else
                node.setCenterX(xPos);
            if(yPos<20)
                node.setCenterY(20);
            else
                node.setCenterY(yPos);
            text.setX(node.getCenterX() - 5);
            text.setY(node.getCenterY() + 5);
            node.setUserData(new double[]{event.getSceneX(), event.getSceneY()});

        });
        nodeGroup.setOnMouseClicked(event -> {
            if(event.getClickCount()==2)
            {
                Alert a=new Alert(Alert.AlertType.CONFIRMATION);
                a.setContentText("Czy na pewno chcesz usunąć ten węzeł?");
                a.getButtonTypes().clear();
                ButtonType ok=new ButtonType("Tak");
                ButtonType cancel=new ButtonType("Nie");

                a.getButtonTypes().addAll(ok,cancel);
                a.showAndWait().ifPresent(response ->
                {
                    if (Objects.equals(response.getText(), "Tak"))
                    {
                       Text t=(Text)nodeGroup.getChildren().get(1);
                       int id=Integer.parseInt(t.getText());
                       for (Node n: graph.getNodes())
                       {
                           if(n.getId()==id)
                           {
                               graph.getNodes().remove(n);
                               break;
                           }
                       }
                       for(int i= graph.getEdges().size()-1;i>=0;i--)
                       {
                           if(graph.getEdges().get(i).getNodes().stream().anyMatch(n -> n.getId() == id))
                           {
                               Group g=graph.getEdges().get(i).getGroup();
                               canvas.getChildren().remove(g);
                               graph.getEdges().remove(i);
                           }
                       }
                       canvas.getChildren().remove(nodeGroup);
                    }

                });
            }
        });
        canvas.getChildren().addAll(nodeGroup);
        graph.getNodes().add(new Node(node,s));
        graph.setSize(graph.getSize()+1);
        createAlert(1,"Węzeł stworzony pomyślnie");

    }
    private void createEdge(Node n1, Node n2) {

        Circle start=n1.getCircle();
        Circle end=n2.getCircle();
        Line edge = new Line();
        Text weightText = new Text();
        Group nodeGroup=new Group(edge, weightText);
        canvas.getChildren().addAll(nodeGroup);
        graph.getEdges().add(new Edge(List.of(n1,n2),0,nodeGroup));

        updateEdgePosition(edge, start, end,weightText);
        weightText.getStyleClass().add("basketText");
        edge.getStyleClass().add("line");
        // Pole do wpisywania wagi, które pojawia się po kliknięciu na linię

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
                if((l.getNodes().stream().anyMatch(node -> node.getId() == n1.getId()))&&
                        (l.getNodes().stream().anyMatch(node -> node.getId() == n2.getId())))
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
        MenuButton button=createEdgeButton();
        MenuItem add=new MenuItem("Dodaj");
        add.setOnAction(e->{
            if (n1Field.getText().isEmpty()||n2Field.getText().isEmpty())
            {
                createAlert(2,"Nie podałeś krawędzi!");
                return;
            }
            int node1=Integer.parseInt(n1Field.getText());
            int node2=Integer.parseInt(n2Field.getText());
            if(node2==node1)
            {
                createAlert(2,"Tworzysz krawędź do samego siebie!");
                return;
            }

            for(Edge l:graph.getEdges())
            {
                if((l.getNodes().stream().anyMatch(node -> node.getId() == node1))&&
                        (l.getNodes().stream().anyMatch(node -> node.getId() == node2)))
                {
                    createAlert(2,"Krawędź już istnieje!");
                    return;
                }
            }
            n1Field.clear();
            n2Field.clear();
            Node c1=null,c2=null;
            for (Node n: graph.getNodes())
            {
                if(n.getId()==node1)
                    c1=n;
                if(n.getId()==node2)
                    c2=n;
            }
            if(c1==null||c2==null)
            {
                createAlert(2,"Podany węzeł nie istnieje!");
                return ;
            }
            createEdge(c1,c2);


        });
        MenuItem delete=new MenuItem("Usuń");
        delete.setOnAction(actionEvent -> {
            if (n1Field.getText().isEmpty()||n2Field.getText().isEmpty())
            {
                createAlert(2,"Nie podałeś krawędzi!");
                return;
            }
            int node1=Integer.parseInt(n1Field.getText());
            int node2=Integer.parseInt(n2Field.getText());
            if(node2==node1)
            {
                createAlert(2,"Tworzysz krawędź do samego siebie!");
                return;
            }
            Node c1=null,c2=null;
            for (Node n: graph.getNodes())
            {
                if(n.getId()==node1)
                    c1=n;
                if(n.getId()==node2)
                    c2=n;
            }
            if(c1==null||c2==null)
            {
                createAlert(2,"Podany węzeł nie istnieje!");
                return ;
            }
            n1Field.clear();
            n2Field.clear();
            for(Edge l:graph.getEdges())
            {
                if((l.getNodes().stream().anyMatch(node -> node.getId() == node1))&&
                        (l.getNodes().stream().anyMatch(node -> node.getId() == node2)))
                {
                    Group g=l.getGroup();
                    canvas.getChildren().remove(g);
                    graph.getEdges().remove(l);
                    break;
                }
            }
            createAlert(1,"Połączenie usunięte pomyślnie");

        });
        button.getItems().addAll(add,delete);
        menu.getChildren().addAll(n1,n1Field,n2,n2Field,button);
        menuBox.getChildren().add(menu);

    }

    private MenuButton createEdgeButton()
    {
        MenuButton menuButton=new MenuButton();
        menuButton.setText("Wybierz");
        menuButton.getStyleClass().add("filterButton");
        menuButton.setOnShowing(event -> menuButton.setStyle("-fx-background-color: #2e79ba; -fx-border-style: solid;"));
        menuButton.setOnHidden(event -> menuButton.setStyle("-fx-background-color: #5fc9f3; -fx-border-style: dashed;"));
        return menuButton;
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
    private void clearCanvas()
    {
        graph.clear();
        canvas.getChildren().clear();
        createAlert(1, "Wyczyszczono całą planszę");
    }

}

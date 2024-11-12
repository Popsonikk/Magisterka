package main.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class GraphInterfaceController implements Initializable {
    @FXML
    public ScrollPane pane;
    @FXML
    public VBox contentVBox;
    @FXML
    public Pane mainPane;
    private Scene mainScene;
    private Stage mainStage;

    public void setMainScene(Scene mainScene) {this.mainScene = mainScene;}
    public void setMainStage(Stage mainStage) {this.mainStage = mainStage;}
    public void back() {mainStage.setScene(mainScene);}

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        /*
        *  Pane pane = new Pane();
        int radius=20;

        // Tworzenie przykładowych węzłów (Circle) z tekstem
        Circle node1 = createNode(pane,100, 100, "A",radius);
        Circle node2 = createNode(pane,300, 200, "B",radius);

        // Dodawanie węzłów do panelu


        // Tworzenie krawędzi (Line) między dwoma węzłami z możliwością dodania wagi
        Line edge = createEdgeWithWeight(pane, node1, node2);

        Scene scene = new Scene(pane, 400, 400);
        primaryStage.setTitle("Graph Interface Example");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Metoda do tworzenia węzła (Circle) z tekstem i możliwością przeciągania
    private Circle createNode(Pane pane,double x, double y, String label, int r) {
        Circle node = new Circle(x, y, r); // x, y - pozycja; 15 - promień
        node.setFill(Color.CORNFLOWERBLUE);
        node.setStroke(Color.BLACK);
        node.setStrokeWidth(2);

        Text text = new Text(x - 5, y + 5, label); // Ustawienie tekstu w centrum węzła
        text.setFill(Color.WHITE);

        node.setOnMousePressed(event -> {
            node.setUserData(new double[]{event.getSceneX(), event.getSceneY()});
        });

        node.setOnMouseDragged(event -> {
            double[] offset = (double[]) node.getUserData();
            double deltaX = event.getSceneX() - offset[0];
            double deltaY = event.getSceneY() - offset[1];

            node.setCenterX(node.getCenterX() + deltaX);
            node.setCenterY(node.getCenterY() + deltaY);
            text.setX(node.getCenterX() - 5);
            text.setY(node.getCenterY() + 5);

            node.setUserData(new double[]{event.getSceneX(), event.getSceneY()});
        });


        pane.getChildren().addAll(node,text);
        return node;
    }

    // Metoda do tworzenia krawędzi z możliwością ustawienia wagi
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
}

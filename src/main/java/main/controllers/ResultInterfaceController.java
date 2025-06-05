package main.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import javafx.util.Pair;
import main.objects.Edge;
import main.objects.Graph;
import main.objects.Node;

import java.net.URL;
import java.util.*;

public class ResultInterfaceController implements Initializable {
    @FXML
    public ScrollPane sp;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        sp.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
    }

    @FXML
    public Pane mainPane;
    @FXML
    public Pane canvas;
    @FXML
    public VBox resCanvas;

    private Scale scale;
    private float meshSize;
    private float scaleRadius;
    private int radius;
    public HBox bottomBox;


    private Scene mainScene;
    private Stage mainStage;
    public void setMainScene(Scene mainScene) {this.mainScene = mainScene;}
    public void setMainStage(Stage mainStage) {this.mainStage = mainStage;}
    public void back() {

        canvas.getChildren().clear();
        canvas.getTransforms().clear();
        resCanvas.getChildren().clear();
        mainStage.setScene(mainScene);
    }
    public void showResult(Map<Node,Integer> dijkstra, Map<Node,String> result, Graph graph,List<Pair<String,String>> categories)
    {

        scale=new Scale(0.5,0.5,0,0);
        scaleRadius= 0.5F;
        scaleController();
        dijkstra=dijkstra.entrySet().stream().sorted(Map.Entry.comparingByValue((v1,v2)->Integer.compare(v2,v1))).collect(LinkedHashMap::new,
                (m, e) -> m.put(e.getKey(), e.getValue()),
                LinkedHashMap::putAll);
        //podglądowe narysowanie grafu dla wyświetlenia wyniku, tylko do wyglądu
        for(Edge edge: graph.getEdges())
            createEdge(edge.getNodes().get(0),edge.getNodes().get(1),edge.getWeight());
        for(Node node: graph.getNodes())
            createNode(node.getCircle().getCenterX(),node.getCircle().getCenterY(),node.getCircle().getRadius(),node.getId());



        canvas.getTransforms().add(scale);
        List<Node> resultList=new ArrayList<>(result.keySet());
        HBox box=new HBox();
        box.getStyleClass().add("resultBorder");
        box.setAlignment(Pos.CENTER);
        Text nodeText=new Text("Node");
        nodeText.getStyleClass().add("basketText");

        Text itemText=new Text("Found_item");
        itemText.getStyleClass().add("basketText");
        Text catText=new Text("Category");
        catText.getStyleClass().add("basketText");
        box.getChildren().addAll(nodeText,itemText,catText);
        resCanvas.getChildren().add(box);

        while (!resultList.isEmpty())
        {
            Node n=resultList.get(0);
            resultList.remove(n);
            resCanvas.getChildren().add(createBox(n.getId(),result.get(n),categories));
        }
    }
    //funkcja wyświetlająca wynik programu
    private HBox createBox(String text,String item,List<Pair<String,String>> categories)
    {
        String cat="";
        for(Pair<String,String>p:categories)
        {
            if(p.getKey().equals(item))
            {
                cat=p.getValue();
                break;
            }
        }
        if(cat.equals(""))
            cat="-----";
        HBox box=new HBox();
        box.getStyleClass().add("resultBorder");
        box.setAlignment(Pos.CENTER);
       // Group g=createNode(15,15,20, node.getId());

        Text idText=new Text(text);
        idText.getStyleClass().add("resText");
        Text itemText=new Text(item);
        itemText.getStyleClass().add("resText");
        Text catText=new Text(cat);
        catText.getStyleClass().add("resText");
        box.getChildren().addAll(idText,itemText,catText);
        return box;
    }



    private Group createNode(double x,double y, double radius, String id)
    {
        Circle node = new Circle(x,y, radius);
        node.getStyleClass().add("circle");
        Text text = new Text(id);
        double len=radius/Math.sqrt(2);
        text.setX(node.getCenterX()-len*1.5);
        text.setY(node.getCenterY()+len/2);
        text.setWrappingWidth(len*3);
        text.getStyleClass().add("circleText");
        Group g=new Group(node,text);
        canvas.getChildren().add(g);
        return g;
    }
    private void createEdge(Node n1, Node n2,int wt) {

        //dane potrzebne do narysowania linii na planszy
        Circle start=n1.getCircle();
        Circle end=n2.getCircle();
        Line edge = new Line();
        Text weightText = new Text(String.valueOf(wt));
        weightText.getStyleClass().add("circleText");
        weightText.setWrappingWidth(30);
        edge.getStyleClass().add("line");
        double startX = start.getCenterX();
        double startY = start.getCenterY();
        double endX = end.getCenterX();
        double endY = end.getCenterY();
        // Obliczenia dla punktu początkowego (krawędź okręgu startowego)
        double angleStart = Math.atan2(endY - startY, endX - startX);
        edge.setStartX(start.getCenterX() + start.getRadius() * Math.cos(angleStart));
        edge.setStartY(startY + start.getRadius() * Math.sin(angleStart));
        // Obliczenia dla punktu końcowego (krawędź okręgu końcowego)
        double angleEnd = Math.atan2(startY - endY, startX - endX);
        edge.setEndX(endX + end.getRadius() * Math.cos(angleEnd));
        edge.setEndY(endY + end.getRadius() * Math.sin(angleEnd));
        weightText.setX(((edge.getStartX() + edge.getEndX()) / 2)-15);
        weightText.setY(((edge.getStartY() + edge.getEndY()) / 2));
        canvas.getChildren().addAll(edge, weightText);
    }
    private void scaleController(){
        VBox box=new VBox();
        box.getChildren().addAll(createZoomButton("Przybliż",0.05f),createZoomButton("Oddal",-0.05f));
        mainPane.getChildren().add(box);

    }
    private Button createZoomButton(String s, float d)
    {
        Button zoomButton=new Button(s);
        zoomButton.getStyleClass().add("zoomButton");
        zoomButton.setOnAction(e->{
            scaleRadius+=d;
            if(scaleRadius<0.2)
                scaleRadius=0.2f;
            if(scaleRadius>2.0)
                scaleRadius=2.0f;
            scale.setX(scaleRadius);
            scale.setY(scaleRadius);
            canvas.setPrefSize(meshSize*scaleRadius,meshSize*scaleRadius);
        });
        return zoomButton;
    }

}

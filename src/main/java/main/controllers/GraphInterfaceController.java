package main.controllers;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import main.functions.GeneratePattern;
import main.objects.Edge;
import main.objects.Graph;
import main.objects.Node;

import java.io.*;
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

        graph=new Graph();
        createBottomBox();
        createAddCircleBox();
        createAddEdgeBox();
        createCSVFileButton();
    }
    //tworzenie pojedynczego węzła, domyślnie tworzymy w wyznaczonych miejscach
    private void createNode( int x, int y) {
        //węzeł reprezentowany jako koło
        Circle node = new Circle((25+x*50.0), (25+y*50.0), 20);
        node.getStyleClass().add("circle");
        //tekst z nazwą węzła
        int s=graph.getSize();
        // Ustawienie tekstu z id w centrum node
        Text text = new Text();
        text.setX(node.getCenterX() - 5);
        text.setY(node.getCenterY() + 5);
        text.setText(Integer.toString(s));
        text.getStyleClass().add("circleText");
        Group nodeGroup=new Group(node,text);
        //dodanie node do danych
        canvas.getChildren().addAll(nodeGroup);
        graph.getNodes().add(new Node(node,s));
        graph.setSize(graph.getSize()+1);
        addNodeMouseEvents(nodeGroup,node,text);
    }
    private void createNode(double x,double y, int id)
    {
        Circle node = new Circle(x,y, 20);
        node.getStyleClass().add("circle");
        Text text = new Text();
        text.setX(node.getCenterX() - 5);
        text.setY(node.getCenterY() + 5);
        text.setText(Integer.toString(id));
        text.getStyleClass().add("circleText");
        Group nodeGroup=new Group(node,text);
        canvas.getChildren().addAll(nodeGroup);
        graph.getNodes().add(new Node(node,id));
        addNodeMouseEvents(nodeGroup,node,text);
    }
    private void addNodeMouseEvents(Group nodeGroup,Circle node,Text text)
    {

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
            //blokowanie wyjścia na ujemne koordynaty
            if(xPos<20)
                node.setCenterX(20);
            else
                node.setCenterX(xPos);
            if(yPos<20)
                node.setCenterY(20);
            else
                node.setCenterY(yPos);
            //przesunięcie tekstu id wraz z node
            text.setX(node.getCenterX() - 5);
            text.setY(node.getCenterY() + 5);
            node.setUserData(new double[]{event.getSceneX(), event.getSceneY()});

        });
        //usunięcie node z planszy
        nodeGroup.setOnMouseClicked(event -> {
            if(event.getClickCount()==2)
            {
                //okno potwierdzające usunięcie node
                Alert a=new Alert(Alert.AlertType.CONFIRMATION);
                a.setContentText("Czy na pewno chcesz usunąć ten węzeł?");
                a.getButtonTypes().clear();
                ButtonType ok=new ButtonType("Tak");
                ButtonType cancel=new ButtonType("Nie");
                a.getButtonTypes().addAll(ok,cancel);
                a.showAndWait().ifPresent(response ->
                {
                    //logika usunięcia
                    if (Objects.equals(response.getText(), "Tak"))
                    {
                        //pobranie id node do usunięcia
                        Text t=(Text)nodeGroup.getChildren().get(1);
                        int id=Integer.parseInt(t.getText());
                        for (Node n: graph.getNodes())
                        {
                            if(n.getId()==id)
                            {
                                //usunięcie node z tablicy
                                graph.getNodes().remove(n);
                                break;
                            }
                        }
                        for(int i= graph.getEdges().size()-1;i>=0;i--)
                        {
                            //usunięcie linii powiązanych z usuwanym node
                            if(graph.getEdges().get(i).getNodes().stream().anyMatch(n -> n.getId() == id))
                            {
                                Group g=graph.getEdges().get(i).getGroup();
                                //usunięcie z planszy
                                canvas.getChildren().remove(g);
                                graph.getEdges().remove(i);
                            }
                        }
                        //usunięcie z planszy
                        canvas.getChildren().remove(nodeGroup);
                    }
                });
            }
        });
    }
    private void createEdge(Node n1, Node n2,int wt) {

        //dane potrzebne do narysowania linii na planszy
        Circle start=n1.getCircle();
        Circle end=n2.getCircle();
        Line edge = new Line();
        Text weightText = new Text(String.valueOf(wt));
        Group nodeGroup=new Group(edge, weightText);
        canvas.getChildren().addAll(nodeGroup);
        graph.getEdges().add(new Edge(List.of(n1,n2),wt,nodeGroup));
        weightText.getStyleClass().add("basketText");
        edge.getStyleClass().add("line");
        updateEdgePosition(edge, start, end,weightText);

        // Pole do wpisywania wagi, które pojawia się po kliknięciu linii

        // Aktualizacja pozycji krawędzi i wagi, gdy węzeł jest przeciągany
        start.centerXProperty().addListener((obs, oldVal, newVal) -> updateEdgePosition(edge, start, end,weightText));
        start.centerYProperty().addListener((obs, oldVal, newVal) -> updateEdgePosition(edge, start, end,weightText));
        end.centerXProperty().addListener((obs, oldVal, newVal) -> updateEdgePosition(edge, start, end,weightText));
        end.centerYProperty().addListener((obs, oldVal, newVal) -> updateEdgePosition(edge, start, end,weightText));

        // Kliknięcie, na linię, aby wprowadzić wagę
        edge.setOnMouseClicked((MouseEvent event) -> {
            //okno dialogu do podania wartości wagi
            TextInputDialog dialog = new TextInputDialog();
            dialog.setHeaderText("Wprowadź wartość wagi");
            Optional<String> res = dialog.showAndWait();
            String weight="";
            if (res.isPresent())
                weight = res.get();
            //komunikat, przy braku wprowadzenia wartości
            if(weight.equals(""))
            {
                createAlert(2,"Nie podałeś wagi krawędzi!");
                return;
            }
            //wyświetlenie wartości
            weightText.setText(weight);

            for(Edge l:graph.getEdges())
            {
                //znalezienie odpowiedniej krawędzi, w celu zaktualizowania wartości
                if((l.getNodes().stream().anyMatch(node -> node.getId() == n1.getId()))&&
                        (l.getNodes().stream().anyMatch(node -> node.getId() == n2.getId())))
                    l.setWeight(Integer.parseInt(weightText.getText()));
            }
        });
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
        HBox menu=createBoxTemplate("X:","Y:");
        Button add=new Button("Dodaj węzeł");
        //pobranie uchwytu z planszy
        TextField x=(TextField)menu.getChildren().get(1);
        TextField y=(TextField)menu.getChildren().get(3);
        add.prefHeight(50.0);
        add.setOnAction(e->{
            if (!parseCanvasInput(x,y))
                return;
            createNode(Integer.parseInt(x.getText()),Integer.parseInt(y.getText()));
            x.clear();
            y.clear();
            createAlert(1,"Węzeł stworzony pomyślnie");
        });
        add.getStyleClass().add("boxButton");
        menu.getChildren().addAll(add);
        menuBox.getChildren().add(menu);
    }
    private void createAddEdgeBox()
    {
        HBox menu=createBoxTemplate("n1:","n2:");
        MenuButton button= createMenuButtonTemplate();
        TextField x=(TextField)menu.getChildren().get(1);
        TextField y=(TextField)menu.getChildren().get(3);
        MenuItem add=new MenuItem("Dodaj");
        add.setOnAction(e->{
            if (!parseCanvasInput(x,y)||!parseEdgeInput(Integer.parseInt(x.getText()),Integer.parseInt(y.getText())))
                return;
            int node1=Integer.parseInt(x.getText());
            int node2=Integer.parseInt(y.getText());
            x.clear();
            y.clear();
            for(Edge l:graph.getEdges())
            {
                if((l.getNodes().stream().anyMatch(node -> node.getId() == node1))&&
                        (l.getNodes().stream().anyMatch(node -> node.getId() == node2)))
                {
                    createAlert(2,"Krawędź już istnieje!");
                    return;
                }
            }
            Node c1=null,c2=null;
            for (Node n: graph.getNodes())
            {
                if(n.getId()==node1)
                    c1=n;
                if(n.getId()==node2)
                    c2=n;
            }
            createEdge(c1,c2,1);
            createAlert(1,"Połączenie stworzone pomyślnie");


        });
        MenuItem delete=new MenuItem("Usuń");
        delete.setOnAction(actionEvent -> {
            if (!parseCanvasInput(x,y)||!parseEdgeInput(Integer.parseInt(x.getText()),Integer.parseInt(y.getText())))
                return;
            int node1=Integer.parseInt(x.getText());
            int node2=Integer.parseInt(y.getText());
            x.clear();
            y.clear();
            //usuwanie wskazanej krawędzi
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
        menu.getChildren().addAll(button);
        menuBox.getChildren().add(menu);

    }

    private MenuButton createMenuButtonTemplate()
    {
        MenuButton menuButton=new MenuButton();
        menuButton.setText("Wybierz");
        menuButton.getStyleClass().add("filterButton");
        menuButton.setOnShowing(event -> menuButton.setStyle("-fx-background-color: #2e79ba; -fx-border-style: solid;"));
        menuButton.setOnHidden(event -> menuButton.setStyle("-fx-background-color: #5fc9f3; -fx-border-style: dashed;"));
        return menuButton;
    }
    //funkcja, tworząca szkielet pól dodawania elementów do planszy
    private HBox createBoxTemplate(String s1,String s2)
    {
        HBox box=new HBox();
        box.getStyleClass().add("infoBoxGraph");
        box.setPrefHeight(50.0);
        Text x=new Text(s1);
        x.getStyleClass().add("infoBoxTextGraph");
        Text y=new Text(s2);
        y.getStyleClass().add("infoBoxTextGraph");
        TextField xField=new TextField();
        xField.getStyleClass().add("filterBoxGraph");
        TextField yField=new TextField();
        yField.getStyleClass().add("filterBoxGraph");
        box.getChildren().addAll(x,xField,y,yField);
        return box;
    }


    private void createBottomBox()
    {
        Button button=new Button("Powrót");
        button.getStyleClass().add("backButton");
        button.setOnAction(e->back());
        Button clear=new Button("Wyczyść");
        clear.getStyleClass().add("backButton");
        clear.setOnAction(e->clearCanvas());
        bottomBox.getChildren().addAll(button,clear);
        bottomBox.setSpacing(5.0);
    }
    private void createCSVFileButton()
    {
        MenuButton menuButton=createMenuButtonTemplate();
        menuButton.setPrefHeight(50.0);
        MenuItem save=new MenuItem("Zapisz do pliku");
        save.setOnAction(e-> {
            try {
                saveToCSV();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        MenuItem load=new MenuItem("Wczytaj z pliku");
        load.setOnAction(e-> {
            try {
                loadFromCSV();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        menuButton.getItems().addAll(save,load);
        menuBox.getChildren().add(menuButton);
    }
    private void loadFromCSV() throws IOException {
        graph.clear();
        FileChooser fileChooser=new FileChooser();
        fileChooser.setTitle("Wybierz plik zawierający gotowe reguły");
        File file = fileChooser.showOpenDialog(null);
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String []header=reader.readLine().split(",");
        graph.setSize(Integer.parseInt(header[2]));
        int s=Integer.parseInt(header[0]);
        for(int i=0;i<s;i++)
        {
            String[] line=reader.readLine().split(",");
            createNode(Double.parseDouble(line[0]),(Double.parseDouble(line[1])),Integer.parseInt(line[2]));
        }
        s=Integer.parseInt(header[1]);
        for(int i=0;i<s;i++)
        {
            String[] line=reader.readLine().split(",");
            int n1=Integer.parseInt(line[0]);
            int n2=Integer.parseInt(line[1]);
            Node c1=null,c2=null;
            for (Node n: graph.getNodes())
            {
                if(n.getId()==n1)
                    c1=n;
                if(n.getId()==n2)
                    c2=n;
            }
            createEdge(c1,c2,Integer.parseInt(line[2]));

        }
        createAlert(1,"Graf wczytany pomyślnie");

    }
    //zapis grafu do pliku
    private void saveToCSV() throws IOException {
        if(graph.getNodes().size()==0)
        {
            createAlert(2,"Brak danych do zapisania");
            return;
        }
        String filename= GeneratePattern.getFilename();
        File file = new File("dane/" + filename+"_graph.csv");
        if (file.createNewFile())
            System.out.println("Stworzono plik");
        FileWriter writer=new FileWriter(file);
        //zapis odpowiednio: liczba node, liczba krawędzi, najwyższy index node
        writer.write(graph.getNodes().size()+","+graph.getEdges().size()+","+ graph.getSize()+"\n");
        for(Node n: graph.getNodes())
        {
            Circle c=n.getCircle();
            //zapis odpowiednio: środek X, środek Y, wartość id node
            writer.write(String.format(Locale.US,"%.3f", c.getCenterX())+",");
            writer.write(String.format(Locale.US,"%.3f", c.getCenterY())+",");
            writer.write(n.getId()+"\n");
        }
        for(Edge e: graph.getEdges())
        {
            //zapis odpowiednio: id node1, id node2, waga krawędzi
            writer.write(e.getNodes().get(0).getId()+",");
            writer.write(e.getNodes().get(1).getId()+",");
            writer.write(e.getWeight()+"\n");
        }
        writer.close();
        createAlert(1, "zapisano pomyślnie");

    }
    private void clearCanvas()
    {
        graph.clear();
        canvas.getChildren().clear();
        createAlert(1, "Wyczyszczono całą planszę");
    }
    private boolean parseCanvasInput(TextField x, TextField y)
    {
        if (x.getText().isEmpty()||y.getText().isEmpty())
        {
            createAlert(2,"Nie podałeś wartości!");
            return false;
        }
        if(Integer.parseInt(x.getText())<0||Integer.parseInt(y.getText())<0)
        {
            createAlert(2,"Podałeś ujemne koordynaty");
            return false;
        }
        return true;
    }
    private boolean parseEdgeInput(int x, int y)
    {
        if(x==y)
        {
            createAlert(2,"Niedozwolona krawędź do samego siebie!");
            return false;
        }
        Node c1=null,c2=null;
        for (Node n: graph.getNodes())
        {
            if(n.getId()==x)
                c1=n;
            if(n.getId()==y)
                c2=n;
        }
        if(c1==null||c2==null)
        {
            createAlert(2,"Podany węzeł nie istnieje!");
            return false ;
        }
        return true;

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

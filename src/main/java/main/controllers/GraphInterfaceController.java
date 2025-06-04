package main.controllers;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Scale;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import main.functions.GeneratePattern;
import main.objects.Edge;
import main.objects.Graph;
import main.objects.GraphTemplateData;
import main.objects.Node;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

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
    private List<Line> mesh;
    private List<Text> textMesh;
    private Scale scale;
    private float meshSize;
    private float scaleRadius;
    private int radius;
    public void setMainScene(Scene mainScene) {this.mainScene = mainScene;}
    public void setMainStage(Stage mainStage) {this.mainStage = mainStage;}
    public void back() {mainStage.setScene(mainScene);}

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        radius=25;
        meshSize=5000.0f;
        scale=new Scale(1,1,0,0);
        scaleRadius=1;
        mesh=new ArrayList<>();
        textMesh=new ArrayList<>();
        graph=new Graph();
        createMesh(meshSize);
        createBottomBox();
        createAddCircleBox();
        createAddEdgeBox();
        createCSVFileButton();
        createGraphTemplateButton();
        canvas.getChildren().addAll(mesh);
        canvas.getChildren().addAll(textMesh);
        canvas.getTransforms().add(scale);
        canvas.setPrefSize(meshSize,meshSize);
        scaleController();
    }
    private void scaleController(){
        VBox box=new VBox();
        box.getChildren().addAll(createZoomButton("Przybliż",0.05f),createZoomButton("Oddal",-0.05f));
        bottomBox.getChildren().add(box);

    }
    private Button createZoomButton(String s,float d)
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

    //tworzenie pojedynczego węzła, domyślnie tworzymy w wyznaczonych miejscach
    private Node createNode( int x, int y, String id) {
        //węzeł reprezentowany jako koło
        Circle node = new Circle((radius+x*60.0), (radius+y*60.0), radius);
        node.getStyleClass().add("circle");
        return  addNodeMouseEvents(node,id);


    }
    private Node createNode(double x,double y, String id)
    {
        Circle node = new Circle(x,y, radius);
        node.getStyleClass().add("circle");
        return addNodeMouseEvents(node,id);

    }
    private Node addNodeMouseEvents(Circle node,String id)
    {
        Text text = new Text();
        double len=radius/Math.sqrt(2);
        text.setX(node.getCenterX()-len);
        text.setY(node.getCenterY()+len/2);
        text.setText(id);
        text.setWrappingWidth(len*2);
        text.getStyleClass().add("circleText");

        Group nodeGroup=new Group(node,text);
        nodeGroup.setOnMouseEntered(event -> node.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.75), 5, 0.5, 0, 0);"));
        nodeGroup.setOnMouseExited(event -> node.setStyle("-fx-effect: null;"));
        canvas.getChildren().addAll(nodeGroup);
        Node nd=new Node(node,id);
        graph.getNodes().add(nd);
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
            if(xPos<radius)
                node.setCenterX(radius);
            else
                node.setCenterX(xPos);
            if(yPos<radius)
                node.setCenterY(radius);
            else
                node.setCenterY(yPos);
            //przesunięcie tekstu id wraz z node
            text.setX(node.getCenterX()-len);
            text.setY(node.getCenterY()+len/2);
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
                        for (Node n: graph.getNodes())
                        {
                            if(n.getId().equals(id))
                            {
                                //usunięcie node z tablicy
                                graph.getNodes().remove(n);
                                break;
                            }
                        }
                        for(int i= graph.getEdges().size()-1;i>=0;i--)
                        {
                            //usunięcie linii powiązanych z usuwanym node
                            if(graph.getEdges().get(i).getNodes().stream().anyMatch(n -> n.getId().equals(id)))
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
        return nd;
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
        weightText.getStyleClass().add("circleText");
        weightText.setWrappingWidth(30);
        weightText.setOnMouseEntered(e-> weightText.setStyle("-fx-stroke-width: 2px;-fx-stroke: blue;"));
        weightText.setOnMouseExited(e-> weightText.setStyle("-fx-stroke-width: 0px;-fx-stroke: transparent;"));
        edge.getStyleClass().add("line");
        updateEdgePosition(edge, start, end,weightText);

        // Pole do wpisywania wagi, które pojawia się po kliknięciu linii

        // Aktualizacja pozycji krawędzi i wagi, gdy węzeł jest przeciągany
        start.centerXProperty().addListener((obs, oldVal, newVal) -> updateEdgePosition(edge, start, end,weightText));
        start.centerYProperty().addListener((obs, oldVal, newVal) -> updateEdgePosition(edge, start, end,weightText));
        end.centerXProperty().addListener((obs, oldVal, newVal) -> updateEdgePosition(edge, start, end,weightText));
        end.centerYProperty().addListener((obs, oldVal, newVal) -> updateEdgePosition(edge, start, end,weightText));

        // Kliknięcie, na linię, aby wprowadzić wagę
        weightText.setOnMouseClicked((MouseEvent event) -> {
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
                if((l.getNodes().stream().anyMatch(node -> Objects.equals(node.getId(), n1.getId())))&&
                        (l.getNodes().stream().anyMatch(node -> Objects.equals(node.getId(), n2.getId()))))
                {
                    l.setWeight(Integer.parseInt(weightText.getText()));
                    break;
                }

            }
        });
        edge.setOnMouseClicked(e-> {
            //usuwanie wskazanej krawędzi
            if (e.getClickCount() == 2) {
                //okno potwierdzające usunięcie node
                Alert a = new Alert(Alert.AlertType.CONFIRMATION);
                a.setContentText("Czy na pewno chcesz usunąć ten węzeł?");
                a.getButtonTypes().clear();
                ButtonType ok = new ButtonType("Tak");
                ButtonType cancel = new ButtonType("Nie");
                a.getButtonTypes().addAll(ok, cancel);
                a.showAndWait().ifPresent(response ->
                {
                    if (Objects.equals(response.getText(), "Tak")) {
                        for (Edge l : graph.getEdges()) {
                            if ((l.getNodes().stream().anyMatch(node -> Objects.equals(node.getId(), n1.getId()))) &&
                                    (l.getNodes().stream().anyMatch(node -> Objects.equals(node.getId(), n2.getId())))) {
                                Group g = l.getGroup();
                                canvas.getChildren().remove(g);
                                graph.getEdges().remove(l);
                                break;
                            }
                        }
                    }
                });
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
        text.setX(((edge.getStartX() + edge.getEndX()) / 2)-15);
        text.setY(((edge.getStartY() + edge.getEndY()) / 2));
    }
    private void createAddCircleBox()
    {
        HBox menu=createBoxTemplate("X:","Y:","ID");
        Button add=new Button("Node");
        //pobranie uchwytu z planszy
        TextField x=(TextField)menu.getChildren().get(1);
        TextField y=(TextField)menu.getChildren().get(3);
        TextField id=(TextField)menu.getChildren().get(5);
        add.prefHeight(50.0);
        add.setOnAction(event->{
            try{
                Integer.parseInt(x.getText());
                Integer.parseInt(y.getText());
            }
            catch (Exception e) {
                createAlert(2,"Współrzędne nie są liczbą!");
                return;
            }
            if (!parseCanvasInput(x,y,id))
                return;
            if(Integer.parseInt(x.getText())<0||Integer.parseInt(y.getText())<0){
                createAlert(2,"Podałeś ujemne koordynaty!");
                return;
            }
            for(Node n: graph.getNodes())
            {
                if(n.getId().equals(id.getText())){
                    createAlert(2,"ID już istnieje!");
                    return;
                }
            }
            createNode(Integer.parseInt(x.getText()),Integer.parseInt(y.getText()), id.getText());
            createAlert(1,"Węzeł stworzony pomyślnie");
        });
        add.getStyleClass().add("boxButton");
        menu.getChildren().addAll(add);
        menuBox.getChildren().add(menu);
    }
    private void createAddEdgeBox()
    {
        HBox menu=createBoxTemplate("n1:","n2:","wg:");
        TextField x=(TextField)menu.getChildren().get(1);
        TextField y=(TextField)menu.getChildren().get(3);
        TextField wg=(TextField)menu.getChildren().get(5);
        Button add=new Button("Edge");
        add.getStyleClass().add("boxButton");
        add.prefHeight(50.0);
        add.setOnAction(event->{
            try{
                Integer.parseInt(wg.getText());
            }
            catch (Exception e) {
                createAlert(2,"Waga nie są liczbą!");
                return;
            }
            if (!parseCanvasInput(x,y,wg))
                return;
            if(Integer.parseInt(wg.getText())<0)
            {
                createAlert(2,"Ujemna waga!");
                return;
            }
            String node1=x.getText();
            String node2=y.getText();
            if(node1.equals(node2)) {
                createAlert(2,"Niedozwolona krawędź do samego siebie!");
                return;
            }

            for(Edge l:graph.getEdges())
            {
                if((l.getNodes().stream().anyMatch(n ->n.getId().equals(node1)))&&
                        (l.getNodes().stream().anyMatch(n ->n.getId().equals(node2))))
                {
                    createAlert(2,"Krawędź już istnieje!");
                    return;
                }
            }
            Node c1=null,c2=null;
            for (Node n: graph.getNodes())
            {
                if(n.getId().equals(node1))
                    c1=n;
                if(n.getId().equals(node2))
                    c2=n;
            }
            if(c1==null||c2==null) {
                createAlert(2,"Podany węzeł nie istnieje!");
                return;
            }
            createEdge(c1,c2,Integer.parseInt(wg.getText()));
            createAlert(1,"Połączenie stworzone pomyślnie");
        });
        menu.getChildren().add(add);
        menuBox.getChildren().add(menu);

    }

    private MenuButton createMenuButtonTemplate(String s)
    {
        MenuButton menuButton=new MenuButton();
        menuButton.setText(s);
        menuButton.setPrefHeight(55);
        menuButton.getStyleClass().add("filterButton");
        menuButton.setOnShowing(event -> menuButton.setStyle("-fx-background-color: #2e79ba; -fx-border-style: solid;"));
        menuButton.setOnHidden(event -> menuButton.setStyle("-fx-background-color: #5fc9f3; -fx-border-style: dashed;"));
        return menuButton;
    }
    //funkcja, tworząca szkielet pól dodawania elementów do planszy
    private HBox createBoxTemplate(String s1,String s2,String s3)
    {
        HBox box=new HBox();
        box.getStyleClass().add("infoBoxGraph");
        box.setPrefHeight(50.0);
        Text x=new Text(s1);
        x.getStyleClass().add("infoBoxTextGraph");
        Text y=new Text(s2);
        y.getStyleClass().add("infoBoxTextGraph");
        Text idText=new Text(s3);
        idText.getStyleClass().add("infoBoxTextGraph");
        TextField xField=new TextField();
        xField.getStyleClass().add("filterBoxGraph");
        TextField yField=new TextField();
        yField.getStyleClass().add("filterBoxGraph");

        TextField idField=new TextField();
        idField.getStyleClass().add("filterBoxGraph");
        box.getChildren().addAll(x,xField,y,yField,idText,idField);
        return box;
    }


    private void createBottomBox()
    {
        Button button=new Button("Powrót");
        button.getStyleClass().add("backButton");
        button.setOnAction(e->back());
        Button clear=new Button("Wyczyść");
        clear.getStyleClass().add("backButton");
        clear.setOnAction(e-> {
            clearCanvas();
            createAlert(1, "Wyczyszczono całą planszę");
        });
        bottomBox.getChildren().addAll(button,clear);

        bottomBox.setSpacing(5.0);
    }
    private void createCSVFileButton()
    {
        MenuButton menuButton=createMenuButtonTemplate("Zarządzaj plikiem");

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
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Wybierz plik zawierający gotowe reguły");
            File file = fileChooser.showOpenDialog(null);
            if(file==null)
                throw  new Exception();
            graph.clear();
            canvas.getChildren().clear();
            canvas.getChildren().addAll(mesh);
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String[] header = reader.readLine().split(",");
            try {
                Integer.parseInt(header[0]);
                Integer.parseInt(header[1]);
            } catch (Exception e) {
                createAlert(2, "Błędny format pliku!");
                return;
            }
            int s = Integer.parseInt(header[0]);
            for (int i = 0; i < s; i++) {
                String[] line = reader.readLine().split(",");
                createNode(Double.parseDouble(line[0]), (Double.parseDouble(line[1])), line[2]);
            }
            s = Integer.parseInt(header[1]);
            for (int i = 0; i < s; i++) {
                String[] line = reader.readLine().split(",");
                String n1 = line[0];
                String n2 = line[1];
                Node c1 = null, c2 = null;
                for (Node n : graph.getNodes()) {
                    if (Objects.equals(n.getId(), n1))
                        c1 = n;
                    if (Objects.equals(n.getId(), n2))
                        c2 = n;
                }
                createEdge(c1, c2, Integer.parseInt(line[2]));

            }
            createAlert(1, "Graf wczytany pomyślnie");

        }catch (Exception e)
        {
            createAlert(2,"Nastąpił błąd przy wyborze pliku");
        }

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
        writer.write(graph.getNodes().size()+","+graph.getEdges().size()+"\n");
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
        scaleRadius=1;
        scale.setX(1);
        scale.setY(1);
        canvas.setPrefSize(meshSize,meshSize);
        canvas.getChildren().clear();
        canvas.getChildren().addAll(mesh);
        canvas.getChildren().addAll(textMesh);

    }
    private boolean parseCanvasInput(TextField x, TextField y,TextField z)
    {
        if (x.getText().isEmpty()||y.getText().isEmpty()||z.getText().isEmpty())
        {
            createAlert(2,"Nie podałeś wartości!");
            return false;
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
    //siatka interfejsu
    private void createMesh(float size)
    {
        //poziome
        for(int i=radius,j=0;i<size;i+=60,j++)
            createMeshLine(i,0,i, (int) size,j,(i-15),20);
         //poziome
        for(int i=radius,j=0;i<size;i+=60,j++)
            createMeshLine(0,i, (int) size,i,j,10,i);
    }
    private void createMeshLine(int sx,int sy,int ex,int ey,int j,int ti, int ty)
    {
        Line line=new Line();
        line.setStartX(sx);
        line.setStartY(sy);
        line.setEndX(ex);
        line.setEndY(ey);
        if(j%5==0&&j>0)
        {
            line.getStyleClass().add("lineMesh5");
            Text text=new Text(String.valueOf(j));
            text.setX(ti);
            text.setY(ty);
            text.setWrappingWidth(30);
            text.getStyleClass().add("circleText");
            textMesh.add(text);
        }

        else
            line.getStyleClass().add("lineMesh");
        mesh.add(line);
    }
    private  void createGraphTemplateButton()
    {
        final String[] patternSize = new String[1];
        final String[] patternStartPoint = new String[1];
        final boolean[] toClear = new boolean[1];

        Stage stage=new Stage();
        MenuButton button=createMenuButtonTemplate("Wybierz wzorzec grafu");




        MenuItem blank=new MenuItem("Graf bez połączeń");
        blank.setOnAction(event->{

            Pane pane1= getGraphTemplatePane(stage,data -> {
                int a,b,x,y;

                patternSize[0] = data.size;
                patternStartPoint[0] = data.startPoint;
                toClear[0] = data.clearBoard;
                try{

                    b=Integer.parseInt(patternSize[0].split("x")[0].trim());
                    a=Integer.parseInt(patternSize[0].split("x")[1].trim());
                    y=Integer.parseInt(patternStartPoint[0].split(";")[0].trim());
                    x=Integer.parseInt(patternStartPoint[0].split(";")[1].trim());
                    if(toClear[0])
                        clearCanvas();
                    createBlankGraph(a,b,x,y,graph.getNodes().size());
                }
                catch (Exception e) {
                    createAlert(2,"Zły format rozmiaru!");
                    return;
                }


            });
            Scene scene =new Scene(pane1);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/main/style.css")).toExternalForm());
            stage.setScene(scene);
            stage.show();
        });
        MenuItem shelvesRows=new MenuItem("Rzędy regałów");
        shelvesRows.setOnAction(actionEvent -> {

            Pane pane1= getGraphTemplatePane(stage,data -> {
                int a,b,x,y;

                patternSize[0] = data.size;
                patternStartPoint[0] = data.startPoint;
                toClear[0] = data.clearBoard;
                try{

                    b=Integer.parseInt(patternSize[0].split("x")[0].trim());
                    a=Integer.parseInt(patternSize[0].split("x")[1].trim());
                    y=Integer.parseInt(patternStartPoint[0].split(";")[0].trim());
                    x=Integer.parseInt(patternStartPoint[0].split(";")[1].trim());
                    if(toClear[0])
                        clearCanvas();
                    int id=graph.getNodes().size();
                    createBlankGraph(a,b,x,y,graph.getNodes().size());
                    for(int i=0;i<a-1;i++)
                    {
                        createEdge(graph.getNodes().get(id+b*i),graph.getNodes().get(id+b*(i+1)),3);
                        createEdge(graph.getNodes().get(id+b*(i+1)-1),graph.getNodes().get(id+b*(i+2)-1),3);
                        for(int j=0;j<b-1;j++)
                            createEdge(graph.getNodes().get(id+j+b*i),graph.getNodes().get(id+j+1+b*i),3);
                    }
                    for(int j=0;j<b-1;j++)
                        createEdge(graph.getNodes().get(id+j+b*(a-1)),graph.getNodes().get(id+j+b*(a-1)+1),3);

                }
                catch (Exception e) {
                    createAlert(2,"Zły format rozmiaru!");
                    return;
                }


            });
            Scene scene =new Scene(pane1);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/main/style.css")).toExternalForm());
            stage.setScene(scene);
            stage.show();



        });
        MenuItem islands=new MenuItem("Wyspy promocyjne");
        islands.setOnAction(actionEvent -> {


            Pane pane1= getGraphTemplatePane(stage,data -> {
                int a,b,x,y;

                patternSize[0] = data.size;
                patternStartPoint[0] = data.startPoint;
                toClear[0] = data.clearBoard;
                try{


                    a=Integer.parseInt(patternSize[0].split("x")[0].trim());
                    y=Integer.parseInt(patternStartPoint[0].split(";")[0].trim());
                    x=Integer.parseInt(patternStartPoint[0].split(";")[1].trim());
                    if(toClear[0])
                        clearCanvas();
                    int id=graph.getNodes().size()-1;
                    for(int i=0,j=0;;i++)
                    {
                        createIslandEdges(createNode(x+5*i+1,1+y,String.valueOf(++id)),
                                createNode(x+5*(i+1)-1,1+y,String.valueOf(++id)),
                                createNode(x+5*i+1,4+y,String.valueOf(++id)),
                                createNode(x+5*(i+1)-1,4+y,String.valueOf(++id)),
                                createNode((double) radius+((x+2.5+(5*i))*60.0),(double)radius+((2.5+y)*60.0),String.valueOf(++id)));
                        if(++j==a)
                            break;
                        createIslandEdges(createNode(x+5*i+1,6+y,String.valueOf(++id)),
                                createNode(x+5*(i+1)-1,6+y,String.valueOf(++id)),
                                createNode(x+5*i+1,9+y,String.valueOf(++id)),
                                createNode(x+5*(i+1)-1,9+y,String.valueOf(++id)),
                                createNode((double) radius+((x+2.5+(5*i))*60.0),(double)radius+((7.5+y)*60.0),String.valueOf(++id)));
                        if(++j==a)
                            break;
                    }
                }
                catch (Exception e) {
                    createAlert(2,"Zły format rozmiaru!");
                    return;
                }


            });
            Scene scene =new Scene(pane1);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/main/style.css")).toExternalForm());
            stage.setScene(scene);
            stage.show();




        });
        MenuItem promotion=new MenuItem("Regały prostopadłe");
        promotion.setOnAction(ev->{



            Pane pane1= getGraphTemplatePane(stage,data -> {
                int a,b,x,y;

                patternSize[0] = data.size;
                patternStartPoint[0] = data.startPoint;
                toClear[0] = data.clearBoard;
                try{

                    a=Integer.parseInt(patternSize[0].split("x")[0].trim());
                    b=Integer.parseInt(patternSize[0].split("x")[1].trim());
                    y=Integer.parseInt(patternStartPoint[0].split(";")[0].trim());
                    x=Integer.parseInt(patternStartPoint[0].split(";")[1].trim());
                    if(toClear[0])
                        clearCanvas();
                    int startId=graph.getNodes().size();
                    int id = startId;

// Lewa półka (a+2 węzły pionowo)
                    for (int i = 0; i < a + 2; i++) {
                        createNode(x, y + 2 * i, String.valueOf(id++));
                    }

// Krawędzie lewej półki
                    for (int i = 0; i < a + 1; i++) {
                        createEdge(graph.getNodes().get(startId + i), graph.getNodes().get(startId + i + 1), 1);
                    }

// Regały prostopadłe między półkami
                    for (int i = 0; i < a; i++) {
                        for (int j = 0; j < b; j++) {
                            createNode(x + 2 + 2 * j, y + 2 + 2 * i, String.valueOf(id++));
                        }
                    }

// Krawędzie poziome między regałami
                    for (int i = 0; i < a; i++) {
                        for (int j = 0; j < b - 1; j++) {
                            int offset = (a + 2) + (b * i) + j + startId;
                            createEdge(graph.getNodes().get(offset), graph.getNodes().get(offset + 1), 1);
                        }
                    }

// Prawa półka (a+2 węzły)
                    for (int i = 0; i < a + 2; i++) {
                        createNode(x + 2 * b + 3, y + 2 * i, String.valueOf(id++));
                    }

// Krawędzie prawej półki
                    int rightStart = (a + 2) + (a * b) + startId;
                    for (int i = rightStart; i < rightStart + a + 1; i++) {
                        createEdge(graph.getNodes().get(i), graph.getNodes().get(i + 1), 1);
                    }

// Połączenia regałów do półek (góra/dół)
                    for (int i = 0; i < a; i++) {
                        int leftIndex = startId + i + 1;
                        int regałStartIndex = startId + (a + 2) + (b * i);
                        int rightIndex = rightStart + i;

                        createEdge(graph.getNodes().get(leftIndex), graph.getNodes().get(regałStartIndex), 3);
                        createEdge(graph.getNodes().get(regałStartIndex + b - 1), graph.getNodes().get(rightIndex + 1), 3);
                    }
                }
                catch (Exception e) {
                    createAlert(2,"Zły format rozmiaru!");
                    return;
                }


            });
            Scene scene =new Scene(pane1);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/main/style.css")).toExternalForm());
            stage.setScene(scene);
            stage.show();






        });
        MenuItem item=new MenuItem("Schowki");
        item.setOnAction(ev->{
            Pane pane1= getGraphTemplatePane(stage,data -> {
                int a,b,x,y;

                patternSize[0] = data.size;
                patternStartPoint[0] = data.startPoint;
                toClear[0] = data.clearBoard;
                try{

                    a=Integer.parseInt(patternSize[0].split("x")[0].trim());
                    y=Integer.parseInt(patternStartPoint[0].split(";")[0].trim());
                    x=Integer.parseInt(patternStartPoint[0].split(";")[1].trim());
                    if(toClear[0])
                        clearCanvas();
                    int startId=graph.getNodes().size();
                    int id = startId;

// Lewa część regałów (x = x+1 i x+3)
                    for (int i = 0; i < 2; i++) {
                        for (int j = 0; j < a; j++) {
                            createNode(x + 1 + 2 * i, y + 3 + 2 * j, String.valueOf(id++));
                        }
                    }

// Prawa część regałów (x = x+9 i x+11)
                    for (int i = 0; i < 2; i++) {
                        for (int j = 0; j < a; j++) {
                            createNode(x + 9 + 2 * i, y + 3 + 2 * j, String.valueOf(id++));
                        }
                    }

// Środkowa kolumna (x = x+6)
                    for (int i = 0; i < a + 2; i++) {
                        createNode(x + 6, y + 1 + 2 * i, String.valueOf(id++));
                    }

// Połączenia w poziomie (4 grupy po a węzłów)
                    for (int group = 0; group < 4; group++) {
                        for (int j = 0; j < a - 1; j++) {
                            int index = startId + group * a + j;
                            createEdge(graph.getNodes().get(index), graph.getNodes().get(index + 1), 1);
                        }
                    }

// Połączenia międzyregalowe (na końcach regałów)
                    createEdge(graph.getNodes().get(startId), graph.getNodes().get(startId + a), 3);                 // 0 → a
                    createEdge(graph.getNodes().get(startId + 2 * a), graph.getNodes().get(startId + 3 * a), 3);     // 2a → 3a
                    createEdge(graph.getNodes().get(startId + a - 1), graph.getNodes().get(startId + 2 * a - 1), 3); // a-1 → 2a-1
                    createEdge(graph.getNodes().get(startId + 3 * a - 1), graph.getNodes().get(startId + 4 * a - 1), 3); // 3a-1 → 4a-1

// Połączenia środka (w pionie)
                    int centerStart = startId + 4 * a;
                    for (int i = centerStart; i < centerStart + a + 1; i++) {
                        createEdge(graph.getNodes().get(i), graph.getNodes().get(i + 1), 1);
                    }

                }
                catch (Exception e) {
                    createAlert(2,"Zły format rozmiaru!");
                    return;
                }


            });
            Scene scene =new Scene(pane1);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/main/style.css")).toExternalForm());
            stage.setScene(scene);
            stage.show();




        });
        button.getItems().addAll(blank,shelvesRows,islands,promotion,item);
        menuBox.getChildren().add(button);
    }
    private  void createIslandEdges(Node n1,Node n2,Node n3,Node n4,Node n5)
    {
        createEdge(n1,n2,2);
        createEdge(n2,n4,2);
        createEdge(n3,n4,2);
        createEdge(n3,n1,2);
        createEdge(n1,n5,3);
        createEdge(n2,n5,3);
        createEdge(n3,n5,3);
        createEdge(n4,n5,3);
    }


    private void createBlankGraph(int a,int b,int x,int y,int size)
    {
        System.out.println(a);
        System.out.println(b);
        System.out.println(x);
        System.out.println(y);
        int id=size;
        for(int i=x;i<x+a*2;i+=2)
        {

            for(int j=y;j<y+b*2;j+=2)
            {

                createNode((i),(j),String.valueOf(id));
                id++;
            }

        }

    }

    public Graph getGraph() {
        return graph;
    }


    private Pane getGraphTemplatePane(Stage stage, Consumer<GraphTemplateData> callback)
    {
        Pane pane=new Pane();
        pane.setPrefWidth(325);
        pane.setPrefHeight(225);



        Text t1=createText("Wybierz rozmiar szablonu",200,20);
        TextField tf1= getTextField(200,8,100,25);
        Text t2=createText("Czy wyczyścić planszę?",200,65);
        CheckBox cb1=new CheckBox();
        cb1.setLayoutX(245);
        cb1.setLayoutY(50);
        Text t3=createText("Wybierz punkt startowy \n (Lewy gorny róg) ",200,110);
        TextField tf3=getTextField(200,100,100,25);
        Button b1=new Button("Potwierdź");
        b1.getStyleClass().add("interfaceButton");
        b1.setLayoutX(75);
        b1.setLayoutY(150);
        b1.setOnAction(e->{
            String sizeText = tf1.getText();
            boolean clearBoard = cb1.isSelected();
            String startPointText = tf3.getText();


            callback.accept(new GraphTemplateData(sizeText, startPointText, clearBoard));


            stage.close();

        });
        pane.getChildren().addAll(t1,tf1,t2,tf3,cb1,t3,b1);
        return pane;
    }
    private Text createText(String text ,int width,int y)
    {
        Text t=new Text(text);
        t.setFont(new Font("Arial",16));
        t.setWrappingWidth(width);
        t.setLayoutY(y);
        t.setTextAlignment(TextAlignment.CENTER);
        return t;
    }
    private TextField getTextField(int x,int y, int w, int h)
    {
        TextField tf=new TextField();
        tf.setLayoutX(x);
        tf.setLayoutY(y);
        tf.setPrefWidth(w);
        tf.setPrefHeight(h);
        return tf;
    }





}

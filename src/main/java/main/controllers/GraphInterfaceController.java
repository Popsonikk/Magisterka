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
import javafx.scene.text.Text;
import javafx.scene.transform.Scale;
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
        meshSize=2500.0f;
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
        box.getChildren().addAll(createZoomButton("Przybliż",0.1f),createZoomButton("Oddal",-0.1f));
        bottomBox.getChildren().add(box);

    }
    private Button createZoomButton(String s,float d)
    {
        Button zoomButton=new Button(s);
        zoomButton.getStyleClass().add("zoomButton");
        zoomButton.setOnAction(e->{
            scaleRadius+=d;
            if(scaleRadius<0.4)
                scaleRadius=0.4f;
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
        MenuButton button=createMenuButtonTemplate("Wybierz wzorzec grafu");
        MenuItem blank=new MenuItem("Graf bez połączeń");
        blank.setOnAction(event->{
            createBlankGraph();

        });
        MenuItem shelvesRows=new MenuItem("Rzędy regałów");
        shelvesRows.setOnAction(actionEvent -> {
            String s=createBlankGraph();
            if(s.isEmpty())
                return;
            int a=Integer.parseInt(s.split("x")[0]);
            int b=Integer.parseInt(s.split("x")[1]);
            for(int i=0;i<a;i++)
            {
                for(int j=0;j<b-1;j++)
                    createEdge(graph.getNodes().get(b*i+j),graph.getNodes().get(b*i+j+1),1);
            }
            for(int i=0;i<a-1;i++)
            {
                createEdge(graph.getNodes().get(b*i),graph.getNodes().get(b*(i+1)),3);
                createEdge(graph.getNodes().get(b*(i+1)-1),graph.getNodes().get(b*(i+2)-1),3);
            }
        });
        MenuItem islands=new MenuItem("Wyspy promocyjne");
        islands.setOnAction(actionEvent -> {
            graph.clear();
            clearCanvas();
            TextInputDialog dialog=new TextInputDialog();
            dialog.setHeaderText("Podaj ilość wysp");
            Optional<String> res=dialog.showAndWait();
            int a;
            if(res.isPresent())
            {
                try{
                    a=Integer.parseInt(res.get());
                }
                catch (Exception e) {
                    createAlert(2,"Zły format rozmiaru!");
                    return ;
                }
            }
            else
                return;
            for(int i=0,j=0,id=-1;;i++)
            {
                createIslandEdges(createNode(5*i+1,1,String.valueOf(++id)),
                        createNode(5*(i+1)-1,1,String.valueOf(++id)),
                        createNode(5*i+1,4,String.valueOf(++id)),
                        createNode(5*(i+1)-1,4,String.valueOf(++id)),
                        createNode((double) radius+((2.5+(5*i))*60.0),(double)radius+((2.5)*60.0),String.valueOf(++id)));
                if(++j==a)
                    break;
                createIslandEdges(createNode(5*i+1,6,String.valueOf(++id)),
                        createNode(5*(i+1)-1,6,String.valueOf(++id)),
                        createNode(5*i+1,9,String.valueOf(++id)),
                        createNode(5*(i+1)-1,9,String.valueOf(++id)),
                        createNode((double) radius+((2.5+(5*i))*60.0),(double)radius+((7.5)*60.0),String.valueOf(++id)));
                if(++j==a)
                    break;
            }
        });
        MenuItem promotion=new MenuItem("Regały prostopadłe");
        promotion.setOnAction(ev->{
            graph.clear();
            clearCanvas();
            TextInputDialog dialog=new TextInputDialog();
            dialog.setHeaderText("Podaj ilość wysp");
            Optional<String> res=dialog.showAndWait();
            String [] line;
            int a,b;
            if(res.isPresent())
            {
                try{
                    line=res.get().split("x");
                    a=Integer.parseInt(line[0]);
                    b=Integer.parseInt(line[1]);
                }
                catch (Exception e) {
                    createAlert(2,"Zły format rozmiaru!");
                    return ;
                }
            }
            else
                return;
            int id=-1;
            for(int i=0;i<a+2;i++)
                createNode(1,1+2*i,String.valueOf(++id));
            for(int i=0;i<a+1;i++)
                createEdge(graph.getNodes().get(i),graph.getNodes().get(i+1),1);
            for(int i=0;i<a;i++)
            {
                for(int j=0;j<b;j++)
                    createNode((3+2*j),(3+2*i),String.valueOf(++id));
            }
            for(int i=0;i<a;i++)
            {
                for(int j=0;j<b-1;j++)
                    createEdge(graph.getNodes().get(a+2+b*i+j),graph.getNodes().get(a+3+b*i+j),1);
            }

            for(int i=0;i<a+2;i++)
                createNode(2*b+3,1+2*i,String.valueOf(++id));
            for(int i=a+2+(a*b);i<2*a+3+(a*b);i++)
                createEdge(graph.getNodes().get(i),graph.getNodes().get(i+1),1);
            for(int i=0;i<a;i++)
            {
                createEdge(graph.getNodes().get(i+1),graph.getNodes().get(a+2+b*i),3);
                createEdge(graph.getNodes().get(a+1+b*(i+1)),graph.getNodes().get(a*b+a+3+i),3);
            }


        });
        MenuItem item=new MenuItem("Schowki");
        item.setOnAction(ev->{
            graph.clear();
            clearCanvas();
            TextInputDialog dialog=new TextInputDialog();
            dialog.setHeaderText("Podaj rozmiar grafu");
            Optional<String> res=dialog.showAndWait();
            int a;
            if(res.isPresent())
            {
                try{

                    a=Integer.parseInt(res.get().split("x")[0].trim());

                }
                catch (Exception e) {
                    createAlert(2,"Zły format rozmiaru!");
                    return;
                }
            }
            else
                return;

            int id=-1;
            for(int i=0;i<2;i++)
            {
                for(int j=0;j<a;j++)
                    createNode((1+2*i),(3+2*j),String.valueOf(++id));
            }
            for(int i=0;i<2;i++)
            {
                for(int j=0;j<a;j++)
                    createNode((9+2*i),(3+2*j),String.valueOf(++id));
            }
            for(int i=0;i<a+2;i++)
                createNode(6,(1+2*i),String.valueOf(++id));
            for(int i=0;i<4;i++)
            {
                for(int j=0;j<a-1;j++)
                    createEdge(graph.getNodes().get(a*i+j),graph.getNodes().get(a*i+j+1),1);
            }

            createEdge(graph.getNodes().get(0),graph.getNodes().get(a),3);
            createEdge(graph.getNodes().get(2*a),graph.getNodes().get(a*3),3);
            createEdge(graph.getNodes().get(a-1),graph.getNodes().get(2*a-1),3);
            createEdge(graph.getNodes().get(3*a-1),graph.getNodes().get(a*4-1),3);
            for(int i=4*a;i<5*a+1;i++)
                createEdge(graph.getNodes().get(i),graph.getNodes().get(i+1),1);





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


    private String createBlankGraph()
    {
        graph.clear();
        clearCanvas();
        TextInputDialog dialog=new TextInputDialog();
        dialog.setHeaderText("Podaj rozmiar grafu");
        Optional<String> res=dialog.showAndWait();
        String size;
        int a,b;
        if(res.isPresent())
        {
            try{
                size=res.get();
                a=Integer.parseInt(size.split("x")[0].trim());
                b=Integer.parseInt(size.split("x")[1].trim());
            }
            catch (Exception e) {
                createAlert(2,"Zły format rozmiaru!");
                return "";
            }
        }
        else
            return" ";

        int id=0;
        for(int i=0;i<a;i++)
        {
            for(int j=0;j<b;j++)
            {
                createNode((1+2*i),(1+2*j),String.valueOf(id));
                id++;
            }

        }
        return size;
    }

    public Graph getGraph() {
        return graph;
    }



}

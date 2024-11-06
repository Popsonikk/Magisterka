package main.controllers;

import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import main.objects.AssociationRule;
import main.objects.FiltrType;
import main.functions.InterfaceTemplate;
import main.objects.SimplePattern;

import java.io.*;
import java.net.URL;
import java.util.*;

public class RuleInterfaceController extends InterfaceTemplate<AssociationRule> implements Initializable {


    @Override
    //funkcja inicjująca interfejs
    public void initialize(URL url, ResourceBundle resourceBundle) {
        init();
        createCSVButton();
    }

    @Override
    protected void createViewTable(List<AssociationRule> rules)
    {
        int size=rules.size();
        //warunek sprawdzający koniec listy
        int range=Math.min(size,(startId+boxSize));
        for(int i=startId,j=0;i<range;i++,j++)
        {
            AssociationRule rule=rules.get(i);
            //generowanie pojedynczego wiersza tabeli
            HBox box=createTableRow(rule);
            contentVBox.getChildren().add(box);
        }
        //aktualizacja informacji o widocznej części tablicy
        Text tx= (Text) switchPageBox.lookup("#showInfo");
        tx.setText("Pokazano "+(startId+1)+"-"+(Math.min(startId+boxSize, size)+" z "+size+" elementów"));
    }

    @Override
    protected void loadFromCSV() throws IOException{

        data.clearData();
        mainPane.getChildren().remove(header);
        FileChooser fileChooser=new FileChooser();
        fileChooser.setTitle("Wybierz plik zawierający gotowe reguły");
        File file = fileChooser.showOpenDialog(null);
        //zapis nazwy pliku
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        reader.readLine(); //header
        line= reader.readLine();
        while (line!=null)
        {
            //pobranie danych zgodnie z formatem
            String []pattern=line.split(",");
            double left_support=Double.parseDouble(pattern[2]);
            double right_support=Double.parseDouble(pattern[4]);
            double support=Double.parseDouble(pattern[5]);
            double confidence=Double.parseDouble(pattern[6]);
            double lift=Double.parseDouble(pattern[7]);
            String[] left_pattern=pattern[1].split(";");
            List<String> left_list=new ArrayList<>();
            for(String s:left_pattern)
                left_list.add(s.trim().toLowerCase());
            String[] right_pattern=pattern[3].split(";");
            List<String> right_list=new ArrayList<>();
            for(String s:right_pattern)
                right_list.add(s.trim().toLowerCase());
            data.getData().add(new AssociationRule(new SimplePattern(left_list,left_support),new SimplePattern(right_list,right_support),
                    support,confidence,lift));
            line= reader.readLine();
        }
        createAlert(1,"Dane zostały wczytane poprawnie");
        mainPane.getChildren().add(header);
        createView();
    }

    @Override
    protected void filtrData(FiltrType ft) {
        String s=tx.getText();
        tx.clear();
        if (s.isEmpty()||data.getDataSize()==0) {
            createAlert(2,"Filtrujesz pustą tablicę!");
            return;
        }
        switch(ft){
            case pattern -> {
                filtrSwitchPattern(s);
                filtrPatternItems(data.getFiltrInfo().getPatternFiltrValue());
            }
            case supportUp,supportDown,confidenceDown,confidenceUp,liftDown,liftUp,patternLen -> {
                filtrSwitchMetrics(s,ft);
                filtrMetricsLevel(data.getFiltrInfo().getIntegerFiltrValue(),ft);
            }
        }
        if(data.getFilteredDataSize()==0){
            data.getFiltrInfo().clearFiltr();
            createAlert(2,"Brak wierszy spełniających podany warunek");
            return;
        }
        createAlert(1,"Filtrowanie zakończone pomyślnie");
        startId=0;
        createView();

    }

    private HBox createTableRow(AssociationRule rule)
    {
        //tworzenie struktury wiersza
        HBox box=new HBox();
        CheckBox checkBox=new CheckBox();
        checkBoxes.add(checkBox);
        GridPane gridPane=new GridPane();
        gridPane.getColumnConstraints().add(new ColumnConstraints(575.0));
        gridPane.getColumnConstraints().add(new ColumnConstraints(125.0));
        gridPane.getColumnConstraints().add(new ColumnConstraints(125.0));
        gridPane.getColumnConstraints().add(new ColumnConstraints(125.0));
        //generowanie pojedynczej kolumny

        HBox box1=createTableColumn("","basketBorder","basketText");
        box1.getChildren().remove(0);
        box1.getChildren().add(0,checkBox);
        //text flow dla zmienienia koloru filtrowanych produktów
        List<Text> textList=createTextList(rule.getAntecedent().getPattern());
        List<Text> textList2=createTextList(rule.getConsequent().getPattern());
        TextFlow textFlow=new TextFlow();
        textFlow.getChildren().addAll(textList);
        Text text=new Text(" -> ");
        text.getStyleClass().add("basketTextFiltr");
        textFlow.getChildren().add(text);
        textFlow.getChildren().addAll(textList2);
        box1.getChildren().add(textFlow);

        gridPane.add(box1,0,0);
        //generowanie pojedynczej kolumny

        HBox box2=createTableColumn(String.format("%.3f", rule.getSupport()),"basketBorder","basketText");
        gridPane.add(box2,1,0);

        HBox box4=createTableColumn(String.format("%.3f", rule.getConfidence()),"basketBorder","basketText");
        gridPane.add(box4,2,0);

        HBox box5=createTableColumn(String.format("%.3f", rule.getLift()),"basketBorder","basketText");
        gridPane.add(box5,3,0);

        box.getChildren().add(gridPane);
        return box;
    }

    @Override
    protected void createFiltrButton() {
        MenuButton menuButton=makeMenuButtonStyle("Wybierz Filtr",340.0,5.0);
        MenuItem p=new MenuItem("Pokaż wybrane elementy");
        p.setOnAction((event)->filtrData(FiltrType.pattern));
        MenuItem pl=new MenuItem("Pokaż elementy wybranej długości");
        pl.setOnAction((event)->filtrData(FiltrType.patternLen));
        MenuItem sd=new MenuItem("Pokaż poziomy wsparcia poniżej");
        sd.setOnAction((event)->filtrData(FiltrType.supportDown));
        MenuItem su=new MenuItem("Pokaż poziomy wsparcia powyżej");
        su.setOnAction((event)->filtrData(FiltrType.supportUp));
        MenuItem cu=new MenuItem("Pokaż poziomy ufności poniżej");
        cu.setOnAction((event)->filtrData(FiltrType.confidenceDown));
        MenuItem cd=new MenuItem("Pokaż poziomy ufności powyżej");
        cd.setOnAction((event)->filtrData(FiltrType.confidenceUp));
        MenuItem ld=new MenuItem("Pokaż poziom lift poniżej");
        ld.setOnAction((event)->filtrData(FiltrType.liftDown));
        MenuItem lu=new MenuItem("Pokaż poziom lift powyżej");
        lu.setOnAction((event)->filtrData(FiltrType.liftUp));
        menuButton.getItems().addAll(p,pl,su,sd,cu,cd,lu,ld);
    }

    @Override
    protected void createCSVFIle() throws IOException {
        if (data.getDataSize() == 0) {
            createAlert(2, "Brak danych do zapisania");
            return;
        }
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText("Wprowadź nazwę pliku");
        Optional<String> res = dialog.showAndWait();
        String filename="";
        if (res.isPresent())
            filename = res.get();
        File file = new File("dane/" + filename+"_ruleData.csv");

        if (file.createNewFile())
            System.out.println("Stworzono plik");
        FileWriter writer=new FileWriter(file);
        writer.write("id,left_pattern,left_support,right_pattern,right_support,support,confidence,lift\n");
        int i=0;
        //ręczny zapis do pliku w formie CSV
        for(AssociationRule rule:data.getData())
        {
            writer.write(i+",");
            List<String> left_pattern = rule.getAntecedent().getPattern();
            for(int j = 0; j < left_pattern.size()-1; j++)
                writer.write(left_pattern.get(j)+";");
            writer.write(left_pattern.get(left_pattern.size()-1)+",");
            writer.write(String.format(Locale.US,"%.3f", rule.getAntecedent().getSupport())+",");
            List<String> right_pattern = rule.getConsequent().getPattern();
            for(int j = 0; j < right_pattern.size()-1; j++)
                writer.write(right_pattern.get(j)+";");
            writer.write(right_pattern.get(right_pattern.size()-1)+",");
            writer.write(String.format(Locale.US,"%.3f", rule.getConsequent().getSupport())+",");
            writer.write(String.format(Locale.US,"%.3f", rule.getSupport())+",");
            writer.write(String.format(Locale.US,"%.3f", rule.getConfidence())+",");
            writer.write(String.format(Locale.US,"%.3f", rule.getLift())+"\n");
            i++;
        }
        writer.close();
        createAlert(1,"Utworzenie pliku zakończone pomyślnie");
    }

    @Override
    protected void createHeader() {
        header=new HBox();
        header.setLayoutX(17.0);
        header.setLayoutY(66.0);
        header.setPrefWidth(950.0);
        header.setPrefHeight(40.0);
        GridPane gridPane=new GridPane();
        gridPane.getColumnConstraints().add(new ColumnConstraints(575.0));
        gridPane.getColumnConstraints().add(new ColumnConstraints(125.0));
        gridPane.getColumnConstraints().add(new ColumnConstraints(125.0));
        gridPane.getColumnConstraints().add(new ColumnConstraints(125.0));

        HBox patternBox=createTableColumn("Reguła","basketHeader","basketHeaderText");
        Button patternButton=createSortButton(Comparator.comparingInt(o -> o.getAntecedent().getPattern().size()),
                (o1,o2)->Integer.compare(o2.getAntecedent().getPattern().size(), o1.getAntecedent().getPattern().size()));
        patternBox.getChildren().add(patternButton);
        gridPane.add(patternBox,0,0);

        HBox supportBox=createTableColumn("Wsparcie","basketHeader","basketHeaderText");
        Button supportButton=createSortButton(Comparator.comparingDouble(AssociationRule::getSupport)
                ,(o1, o2) -> Double.compare(o2.getSupport(), o1.getSupport()));
        supportBox.getChildren().add(supportButton);
        gridPane.add(supportBox,1,0);

        HBox confidenceBox=createTableColumn("Ufność","basketHeader","basketHeaderText");
        Button confidenceButton=createSortButton(Comparator.comparingDouble(AssociationRule::getConfidence)
                ,(o1,o2)->Double.compare(o2.getConfidence(),o1.getConfidence()));
        confidenceBox.getChildren().add(confidenceButton);
        gridPane.add(confidenceBox,2,0);

        HBox liftBox=createTableColumn("Lift","basketHeader","basketHeaderText");
        Button liftButton=createSortButton(Comparator.comparingDouble(AssociationRule::getLift)
                ,(o1,o2)->Double.compare(o2.getLift(),o1.getLift()));
        liftBox.getChildren().add(liftButton);
        gridPane.add(liftBox,3,0);
        header.getChildren().add(gridPane);
    }

    private void filtrPatternLength(int val){
        for (AssociationRule rule : data.getData()) {
            if (rule.getAntecedent().getPattern().size()==val||rule.getConsequent().getPattern().size()==val)
                data.getFilteredData().add(rule);
        }
    }
    public void filtrPatternItems(List<String> selectedItems) {
        for (AssociationRule rule : data.getData()) {
            if (new HashSet<>(rule.getAntecedent().getPattern()).containsAll(selectedItems)||new HashSet<>(rule.getConsequent().getPattern()).containsAll(selectedItems))
                data.getFilteredData().add(rule);
        }
    }
    public void filtrMetricsLevel(double val, FiltrType type) {

        for (AssociationRule rule : data.getData()) {
            switch (type) {
                case liftUp -> {
                    if (rule.getLift() >= val)
                        data.getFilteredData().add(rule);
                }
                case liftDown -> {
                    if (rule.getLift() <= val)
                        data.getFilteredData().add(rule);
                }
                case confidenceUp -> {
                    if (rule.getConfidence() >= val)
                        data.getFilteredData().add(rule);
                }
                case confidenceDown -> {
                    if (rule.getConfidence() <= val)
                        data.getFilteredData().add(rule);
                }
                case supportUp -> {
                    if (rule.getSupport() >= val)
                        data.getFilteredData().add(rule);
                }
                case supportDown -> {
                    if (rule.getSupport() <= val)
                        data.getFilteredData().add(rule);
                }
                case patternLen -> {
                    if (rule.getConsequent().getPattern().size() == val)
                        data.getFilteredData().add(rule);
                }
            }
        }
    }
    @Override
    protected void refresh()
    {
        data.getFilteredData().clear();
        switch(data.getFiltrInfo().getFiltrType()){
            case pattern -> filtrPatternItems(data.getFiltrInfo().getPatternFiltrValue());
            case patternLen -> filtrPatternLength((int) data.getFiltrInfo().getIntegerFiltrValue());
            case supportUp -> filtrMetricsLevel(data.getFiltrInfo().getIntegerFiltrValue(),FiltrType.supportUp);
            case supportDown -> filtrMetricsLevel(data.getFiltrInfo().getIntegerFiltrValue(),FiltrType.supportDown);
            case confidenceDown -> filtrMetricsLevel(data.getFiltrInfo().getIntegerFiltrValue(),FiltrType.confidenceDown);
            case confidenceUp -> filtrMetricsLevel(data.getFiltrInfo().getIntegerFiltrValue(),FiltrType.confidenceUp);
            case liftDown -> filtrMetricsLevel(data.getFiltrInfo().getIntegerFiltrValue(),FiltrType.liftDown);
            case liftUp -> filtrMetricsLevel(data.getFiltrInfo().getIntegerFiltrValue(),FiltrType.liftUp);
        }
    }
}

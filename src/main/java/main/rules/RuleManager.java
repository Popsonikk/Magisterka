package main.rules;

import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import main.apriori.AprioriManager;
import main.apriori.SimplePattern;

import java.io.*;
import java.util.*;

public class RuleManager {

    private AprioriManager aprioriManager;
    private List<AssociationRule> ruleList;
    private List<AssociationRule> filteredRuleList;
    private String ruleFilename;

    public RuleManager()
    {
        this.ruleList=new ArrayList<>();
        this.filteredRuleList=new ArrayList<>();
        this.ruleFilename="";
    }

    public void setAprioriManager(AprioriManager aprioriManager) {
        this.aprioriManager = aprioriManager;
    }

    public void generateRules() {

        List<List<String>> subsets=new ArrayList<>();
        if (aprioriManager.getSupportListSize()==0)
        {
            System.out.println("Brak wyznaczonych wzorców");
            Alert alert=new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Brak wyznaczonych wzorców");
            alert.show();
            return;
        }
        ruleList.clear();
        List<SimplePattern> patterns=aprioriManager.getSupportList();
        //iterujemy po wszystkich możliwych wzorcach
        for (SimplePattern pattern:patterns)
        {
            if(pattern.getPattern().size()==1)
                continue;
            if(pattern.getPattern().size()==2)
            {
                SimplePattern at = getSupport(patterns, Collections.singletonList(pattern.getPattern().get(0)));
                SimplePattern ct= getSupport(patterns,Collections.singletonList(pattern.getPattern().get(1)));
                double confidence = pattern.getSupport() / at.getSupport();
                double lift = confidence / ct.getSupport();
                ruleList.add(new AssociationRule(at,ct, pattern.getSupport(), confidence,lift));
                continue;
            }
            subsets.clear();
            int size=pattern.getPattern().size();
            int h;
            if(size%2==1)
                h=size/2+1;
            else
                h=size/2;
            //tworzymy podzbiory wzorca
            for(int i=h;i<size;i++)
                subsets.addAll(aprioriManager.generateCandidates(pattern.getPattern(),0,i,new ArrayList<>()));

            for (List<String> antecedent:subsets)
            {

                //tworzenie prawej strony na zasadzie usunięcia lewej strony z głównego wzorca
                List<String> consequent=new ArrayList<>(pattern.getPattern());
                consequent.removeAll(antecedent);
                SimplePattern at = getSupport(patterns,antecedent);
                SimplePattern ct= getSupport(patterns, consequent);
                double confidence = pattern.getSupport() / at.getSupport();
                double lift = confidence / ct.getSupport();
                ruleList.add(new AssociationRule(at,ct, pattern.getSupport(),confidence,lift));
            }
        }
        Alert alert=new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("Reguły wygenerowane pomyślnie");
        alert.show();
    }
    private SimplePattern getSupport(List<SimplePattern> patterns, List<String> subset) {
         Optional<SimplePattern> matchingPattern = patterns.stream()
                .filter(simplePattern -> new HashSet<>(simplePattern.getPattern()).containsAll(subset)&&simplePattern.getPattern().size()==subset.size()).findFirst();
        return matchingPattern.orElse(null);
    }

    //generowanie wszystkich możliwych podzbiorów dla wzorca

    public int getRuleListSize() {return  ruleList.size();}
    public int getFilteredRuleListSize() {return  filteredRuleList.size();}

    public List<AssociationRule> getRuleList() {
        return ruleList;
    }

    public List<AssociationRule> getFilteredRuleList() {
        return filteredRuleList;
    }
    public void clearList()
    {
        ruleList.clear();
    }
    public void clearFilteredList()
    {
        filteredRuleList.clear();
    }



    public void createCSVFIle(){
        if(ruleList.isEmpty())
        {
            Alert a=new Alert(Alert.AlertType.ERROR);
            a.setContentText("Brak danych do zapisania");
            a.show();
            return;
        }
        try {
            String filename="";
            //pobranie nazwy pliku, na podstawie którego pracował algorytm
            if(Objects.equals(ruleFilename, ""))
            {
                if(!Objects.equals(aprioriManager.getSupportFilename(), ""))
                    filename=aprioriManager.getSupportFilename().split("\\.")[0];
                else if(!Objects.equals(aprioriManager.getBasketName(), ""))
                    filename=aprioriManager.getBasketName().split("\\.")[0];
            }
            else
                filename=ruleFilename;
            File file = new File("dane/" + filename+"_ruleData.csv");
            //pętla mająca na celu zablokowanie duplikowania nazw plików
            int k=1;
            while (file.exists())
            {
                file = new File("dane/" + filename+k+"_ruleData.csv");
                k++;
            }
            file.createNewFile();
            FileWriter writer=new FileWriter(file);
            writer.write("id,left_pattern,left_support,right_pattern,right_support,support,confidence,lift\n");
            int i=0;
            //ręczny zapis do pliku w formie CSV
            for(AssociationRule rule:ruleList)
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
            Alert a=new Alert(Alert.AlertType.INFORMATION);
            a.setContentText("Utworzenie pliku zakończone pomyślnie");
            a.show();
        } catch (IOException e) {
            Alert a=new Alert(Alert.AlertType.ERROR);
            a.setContentText("Wystąpił błąd przy zapisie");
            a.show();
            throw new RuntimeException(e);
        }

    }
    public void loadFromCSV(){
        try {
            FileChooser fileChooser=new FileChooser();
            fileChooser.setTitle("Wybierz plik zawierający poziomy wsparcia");
            File file = fileChooser.showOpenDialog(null);
            //zapis nazwy pliku
            ruleFilename=file.getName();
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
                ruleList.add(new AssociationRule(new SimplePattern(left_list,left_support),new SimplePattern(right_list,right_support),
                        support,confidence,lift));


                line= reader.readLine();
            }
            Alert a=new Alert(Alert.AlertType.INFORMATION);
            a.setContentText("Dane zostały wczytane poprawnie");
            a.show();
        } catch (IOException e) {
            Alert a=new Alert(Alert.AlertType.ERROR);
            a.setContentText("Wystąpił błąd przy zapisie");
            a.show();
            throw new RuntimeException(e);
        }

    }
    public void sortByConfidenceUp() {
        ruleList.sort(Comparator.comparingDouble(AssociationRule::getConfidence));
    }
    public void sortByConfidenceDown() {
        ruleList.sort((o1, o2) -> Double.compare(o2.getConfidence(), o1.getConfidence()));
    }
    public void sortByLiftUp() {
        ruleList.sort(Comparator.comparingDouble(AssociationRule::getLift));
    }
    public void sortByLiftDown() {
        ruleList.sort((o1, o2) -> Double.compare(o2.getLift(), o1.getLift()));
    }
    public void sortByPatternUp() {
        ruleList.sort((o1, o2) -> {
            int sizeComparison=Integer.compare(o1.getAntecedent().getPattern().size(), o2.getAntecedent().getPattern().size());
            if (sizeComparison != 0) {
                return sizeComparison;
            }
            String f1 = o1.getAntecedent().getPattern().get(0);
            String f2 = o2.getAntecedent().getPattern().get(0);
            return f1.compareTo(f2);
        });
    }
    public void sortByPatternDown() {
        ruleList.sort((o1, o2) -> {
            int sizeComparison=Integer.compare(o2.getAntecedent().getPattern().size(), o1.getAntecedent().getPattern().size());
            if (sizeComparison != 0) {
                return sizeComparison;
            }
            String f1 = o1.getAntecedent().getPattern().get(0);
            String f2 = o2.getAntecedent().getPattern().get(0);
            return f2.compareTo(f1);
        });
    }
    public void sortBySortUp() {
        ruleList.sort(Comparator.comparingDouble(AssociationRule::getSupport));
    }
    public void sortBySortDown() {
        ruleList.sort((o1, o2) -> Double.compare(o2.getSupport(), o1.getSupport()));
    }




}

package main.rules;

import javafx.scene.control.Alert;
import main.apriori.AprioriManager;
import main.apriori.SimplePattern;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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

            //tworzymy podzbiory wzorca
            List<List<String>> subsets=generateNonEmptySubsets(pattern.getPattern());
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
    private  List<List<String>> generateNonEmptySubsets(List<String> list) {
        List<List<String>> subsets = new ArrayList<>();
        int n = list.size();
        //Rozpatrujemy wzorzec jako liczbę bitową (1 << n) == 2^n.
        //zaczynamy od 1, ponieważ nie chcemy podzbiory niepuste, konczymy na 2^n-1 bo nie interesuje nas podzbiór pełny
        int k;
        if (n%2==0)
            k=n/2;
        else
            k=n/2+1;
        for (int i = 1; i < ((1 << k)); i++) {
            List<String> subset = new ArrayList<>();
            for (int j = 0; j < n; j++) {
                //Liczba i jest traktowana jak binarna. Liczna j ma zapalony tylko 1 bit, którego przesuwamy ciągle w lewo.
                //Sprawia to, że jeżeli bit w liczbie i jest zapalony, to dzięki and zostanie dodany odpowiedni item do wzorca
                // np i=5 -> 101  j(0-2) a) 101&001=001 b) 101&010=000 c) 101&100=100. a i c spełniają warunek więc to one zostaną dodane do wzorca
                if ((i & (1 << j)) > 0) {
                    subset.add(list.get(j));
                }
            }
            subsets.add(subset);
        }
        return subsets;
    }
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
                writer.write(String.format("%.3f", rule.getAntecedent().getSupport())+",");
                List<String> right_pattern = rule.getConsequent().getPattern();
                for(int j = 0; j < right_pattern.size()-1; j++)
                    writer.write(right_pattern.get(j)+";");
                writer.write(right_pattern.get(right_pattern.size()-1)+",");
                writer.write(String.format("%.3f", rule.getConsequent().getSupport())+",");
                writer.write(String.format("%.3f", rule.getSupport())+",");
                writer.write(String.format("%.3f", rule.getConfidence())+",");
                writer.write(String.format("%.3f", rule.getLift())+"\n");
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

    }




}

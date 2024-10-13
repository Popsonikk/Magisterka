package main.rules;

import javafx.scene.control.Alert;
import main.apriori.AprioriManager;
import main.apriori.SimplePattern;

import java.util.*;

public class RuleManager {

    private AprioriManager aprioriManager;
    private List<AssociationRule> ruleList;

    public RuleManager()
    {
        this.ruleList=new ArrayList<>();
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
                ruleList.add(new AssociationRule(at,ct,confidence,lift));
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

                ruleList.add(new AssociationRule(at,ct,confidence,lift));
            }

        }
        showRules();
        Alert alert=new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("Reguły wygenerowane pomyślnie");
        alert.show();

    }

    private SimplePattern getSupport(List<SimplePattern> patterns, List<String> subset) {
         double res=0.0;
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
    public void showRules()
    {
        for(AssociationRule rule: ruleList)
        {
           System.out.println( rule.toString());
        }
    }


}

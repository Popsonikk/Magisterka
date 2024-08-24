package main;

import java.util.ArrayList;
import java.util.List;

public class RuleManager {

    private AprioriManager aprioriManager;
    private List<AssociationRule> ruleList;

    RuleManager()
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
            return;
        }
        ruleList.clear();
        List<SimplePattern> patterns=aprioriManager.getSupportList();
        //iterujemy po wszystkich możliwych wzorcach
        for (SimplePattern pattern:patterns)
        {
            //tworzymy podzbiory wzorca
            List<List<String>> subsets=generateNonEmptySubsets(pattern.getPattern());
            for (List<String> antecedent:subsets)
            {
                //tworzenie prawej strony na zasadzie usunięcia lewej strony z głównego wzorca
                List<String> consequent=pattern.getPattern();
                consequent.removeAll(antecedent);
                double antecedentSupport = getSupport(patterns, antecedent);
                double consequentSupport = getSupport(patterns, consequent);

                double confidence = pattern.getSupport() / antecedentSupport;
                double lift = confidence / consequentSupport;
                //potencjalne nabijanie złożoności pamięciowej
                ruleList.add(new AssociationRule(new SimplePattern(antecedent,antecedentSupport),
                             new SimplePattern(consequent,consequentSupport),confidence,lift));
            }

        }
        showRules();

    }
    //każdorazowe wyszukiwanie wzorca pesymistyczne O(n) :(
    private  double getSupport(List<SimplePattern> patterns, List<String> subset) {
        for (SimplePattern pattern : patterns) {
            if (pattern.getPattern().equals(subset)) {
                return pattern.getSupport();
            }
        }
        return 0.0;
    }

    //generowanie wszystkich możliwych podzbiorów dla wzorca
    private  List<List<String>> generateNonEmptySubsets(List<String> list) {
        List<List<String>> subsets = new ArrayList<>();
        int n = list.size();
        //Rozpatrujemy wzorzec jako liczbę bitową (1 << n) == 2^n.
        //zaczynamy od 1, ponieważ nie chcemy podzbiory niepuste, konczymy na 2^n-1 bo nie interesuje nas podzbiór pełny
        for (int i = 1; i < ((1 << n)-1); i++) {
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
           System.out.println( rule.toString()+"\n");
        }
    }


}

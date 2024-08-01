package main;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AprioriManager {
    private  BasketManager basketManager;
    private Map<String,Integer> initStep;

    AprioriManager()
    {
        this.initStep=new HashMap<>();
    }

    public void setBasketManager(BasketManager basketManager) {
        this.basketManager = basketManager;
    }
    public void initStage()
    {
        List<List<String>> baskets=basketManager.getBaskets();
        if(basketManager.getBasketSize()==0)
        {
            System.out.println("Brak koszyków");
            return;
        }
        for(List<String> basket:baskets)
        {
            for (String item : basket)
                initStep.put(item, initStep.getOrDefault(item, 0) + 1);
        }
        System.out.println(initStep);
    }
}

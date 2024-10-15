package main.rules;

import main.apriori.SimplePattern;

public class AssociationRule {

    private SimplePattern antecedent;
    private SimplePattern consequent;
    private double confidence;
    private double lift;
    private double support;


    public AssociationRule(SimplePattern antecedent, SimplePattern consequent,double support, double confidence, double lift) {
        this.antecedent = antecedent;
        this.consequent = consequent;
        this.confidence = confidence;
        this.lift = lift;
        this.support=support;
    }

    public SimplePattern getAntecedent() {
        return antecedent;
    }

    public SimplePattern getConsequent() {
        return consequent;
    }

    public double getConfidence() {
        return confidence;
    }

    public double getLift() {
        return lift;
    }

    public double getSupport() {
        return support;
    }
}

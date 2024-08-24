package main;

public class AssociationRule {

    private SimplePattern antecedent;
    private SimplePattern consequent;
    private double confidence;
    private double lift;


    public AssociationRule(SimplePattern antecedent, SimplePattern consequent, double confidence, double lift) {
        this.antecedent = antecedent;
        this.consequent = consequent;
        this.confidence = confidence;
        this.lift = lift;
    }

    @Override
    public String toString() {
        return antecedent.getPattern() + " -> " + consequent.getPattern() + " (Antecedent support: "+antecedent.getSupport()+
                ", consequent support "+consequent.getSupport()+
                ", Confidence: " + confidence + ", Lift: " + lift + ")";
    }


}

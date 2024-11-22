package main.objects;

import java.util.List;

public class SimplePattern {
    private List<String> pattern;
    private double support;

    public SimplePattern(List<String> pattern, double support) {
        this.pattern = pattern;
        this.support = support;
    }

    public List<String> getPattern() {
        return pattern;
    }

    public double getSupport() {
        return support;
    }

    @Override
    public String toString(){
        StringBuilder builder=new StringBuilder();
        for(int i=0;i<pattern.size()-1;i++)
            builder.append(pattern.get(i)).append(",");
        builder.append(pattern.get(pattern.size()-1));
        return builder.toString();
    }
}

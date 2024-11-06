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
}

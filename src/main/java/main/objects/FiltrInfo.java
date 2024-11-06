package main.objects;

import java.util.ArrayList;
import java.util.List;

public class FiltrInfo {
    private FiltrType filtrType;
    private List<String> patternFiltrValue;
    private double integerFiltrValue;
    private boolean isFiltered;
    FiltrInfo()
    {
        this.filtrType=FiltrType.none;
        this.patternFiltrValue=new ArrayList<>();
        this.integerFiltrValue=0.0;
        this.isFiltered=false;
    }

    public FiltrType getFiltrType() {
        return filtrType;
    }

    public void setFiltrType(FiltrType filtrType) {
        this.filtrType = filtrType;
    }

    public List<String> getPatternFiltrValue() {
        return patternFiltrValue;
    }

    public void setPatternFiltrValue(List<String> patternFiltrValue) {
        this.patternFiltrValue = patternFiltrValue;
    }

    public double getIntegerFiltrValue() {
        return integerFiltrValue;
    }

    public void setIntegerFiltrValue(double integerFiltrValue) {
        this.integerFiltrValue = integerFiltrValue;
    }

    public boolean isFiltered() {
        return isFiltered;
    }

    public void setFiltered(boolean filtered) {
        isFiltered = filtered;
    }

    public void clearFiltr()
    {
        filtrType=FiltrType.none;
        patternFiltrValue.clear();
        integerFiltrValue=0.0;
        isFiltered=false;
    }
}

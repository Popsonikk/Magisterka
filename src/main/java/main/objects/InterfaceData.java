package main.objects;

import main.objects.FiltrInfo;

import java.util.ArrayList;
import java.util.List;

public class InterfaceData <T>{
    private List<T> data;
    private List<T> filteredData;
    private FiltrInfo filtrInfo;
    public InterfaceData() {
        this.data=new ArrayList<>();
        this.filteredData=new ArrayList<>();
        this.filtrInfo=new FiltrInfo();
    }

    public List<T> getData() {
        return data;
    }
    public void setData(List<T> data) {
        this.data = data;
    }
    public List<T> getFilteredData() {
        return filteredData;
    }
    public void setFilteredData(List<T> filteredData) {
        this.filteredData = filteredData;
    }
    public FiltrInfo getFiltrInfo() {
        return filtrInfo;
    }
    public void setFiltrInfo(FiltrInfo filtrInfo) {
        this.filtrInfo = filtrInfo;
    }
    public int getDataSize() {
        return data.size();
    }
    public int getFilteredDataSize(){
        return filteredData.size();
    }
    public void clearData() {
        data.clear();
    }
    public void clearFilteredData() {
        filteredData.clear();
    }

}

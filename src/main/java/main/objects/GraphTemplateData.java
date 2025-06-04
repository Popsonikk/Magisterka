package main.objects;

public class GraphTemplateData {
    public final String size;
    public final String startPoint;
    public final boolean clearBoard;

    public GraphTemplateData(String size, String startPoint, boolean clearBoard) {
        this.size = size;
        this.startPoint = startPoint;
        this.clearBoard = clearBoard;
    }
}

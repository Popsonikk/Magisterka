package main.objects;

import java.util.ArrayList;
import java.util.List;

public class Graph {
    private List<Node> nodes;
    private List<Edge> edges;
    private int size;
    public Graph()
    {
        nodes=new ArrayList<>();
        edges=new ArrayList<>();
        size=0;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
    public void clear()
    {
        nodes.clear();
        edges.clear();
        size=0;
    }
}

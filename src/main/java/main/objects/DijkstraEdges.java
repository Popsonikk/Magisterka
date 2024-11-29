package main.objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class DijkstraEdges {

    private List<String> nodes;
    private List<Integer> weights;
    private boolean visited;
    DijkstraEdges(String s,int w)
    {
        nodes = new ArrayList<>();
        weights = new ArrayList<>();
        nodes.add(s);
        weights.add(w);
        visited=false;
    }

    public List<String> getNodes() {
        return nodes;
    }

    public List<Integer> getWeights() {
        return weights;
    }

    public void add(String s,int w){
        nodes.add(s);
        weights.add(w);
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }
}

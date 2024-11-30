package main.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Graph {
    private List<Node> nodes;
    private List<Edge> edges;

    public Graph()
    {
        nodes=new ArrayList<>();
        edges=new ArrayList<>();

    }
    public Node findNode(String id)
    {
        for(Node n:nodes) {
            if(n.getId().contains(id))
                return n;
        }
        return null;
    }


    public List<Node> getNodes() {
        return nodes;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public void clear()
    {
        nodes.clear();
        edges.clear();

    }

}

package main.objects;

import javafx.scene.shape.Circle;

import java.util.ArrayList;
import java.util.List;

public class Node {

    private Circle circle; //obiekt widoczny na planszy
    private String id;
    private List<Edge> outgoingEdges;

    public Node(Circle circle, String id) {
        this.circle = circle;
        this.id = id;
        this.outgoingEdges=new ArrayList<>();
    }

    public Circle getCircle() {
        return circle;
    }

    public String getId() {
        return id;
    }
    public void addEdge(Edge edge) {
        outgoingEdges.add(edge);
    }

    public List<Edge> getOutgoingEdges() {
        return outgoingEdges;
    }
}

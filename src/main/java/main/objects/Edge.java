package main.objects;

import javafx.scene.Group;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

import java.util.List;

public class Edge {
    private List<Node> nodes;
    private int weight;

    private Group group; //line + weightText widoczne na planszy


    public Edge(List<Node> nodes, int weight, Group group) {
        this.nodes = nodes;
        this.weight = weight;
        this.group = group;
        nodes.get(0).addEdge(this);
        nodes.get(1).addEdge(this);
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public Group getGroup() {
        return group;
    }
}



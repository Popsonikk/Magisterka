package main.objects;

public class Edge {
    private int node1ID;
    private int node2ID;
    private int weight;

    public Edge(int node1ID, int node2ID, int weight) {
        this.node1ID = node1ID;
        this.node2ID = node2ID;
        this.weight = weight;
    }

    public int getNode1ID() {
        return node1ID;
    }

    public int getNode2ID() {
        return node2ID;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}

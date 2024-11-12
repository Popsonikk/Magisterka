package main.objects;

import javafx.scene.shape.Circle;

public class Node {
    private Circle circle;
    private int id;

    public Node(Circle circle, int id) {
        this.circle = circle;
        this.id = id;
    }

    public Circle getCircle() {
        return circle;
    }

    public int getId() {
        return id;
    }
}

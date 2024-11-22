package main.objects;

import javafx.scene.shape.Circle;

public class Node {
    private Circle circle;
    private String id;

    public Node(Circle circle, String id) {
        this.circle = circle;
        this.id = id;
    }

    public Circle getCircle() {
        return circle;
    }

    public String getId() {
        return id;
    }
}

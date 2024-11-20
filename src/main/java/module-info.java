module main {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.neo4j.driver;

    opens main to javafx.fxml;
    exports main;
    exports main.controllers;
    opens main.controllers to javafx.fxml;
    exports main.objects;
    opens main.objects to javafx.fxml;
    exports main.functions;
    opens main.functions to javafx.fxml;
}
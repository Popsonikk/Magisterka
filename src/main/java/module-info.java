module main {
    requires javafx.controls;
    requires javafx.fxml;

    opens main to javafx.fxml;
    exports main;
    exports main.apriori;
    opens main.apriori to javafx.fxml;
    exports main.rules;
    opens main.rules to javafx.fxml;
    exports main.baskets;
    opens main.baskets to javafx.fxml;
}
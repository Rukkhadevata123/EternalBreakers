module com.eternalbreaker {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.eternalbreaker to javafx.fxml;
    exports com.eternalbreaker;
}

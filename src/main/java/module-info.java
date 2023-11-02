module com.nolte.beerolympics2024 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires json.simple;
    requires java.desktop;
    requires javafx.swing;

    opens com.nolte.beerolympics2024 to javafx.fxml;
    exports com.nolte.beerolympics2024;
}
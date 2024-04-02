module com.coolawesome.integrativeproject {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.fxyz3d.core;
    requires com.google.gson;

    opens com.coolawesome.integrativeproject to javafx.fxml;
    exports com.coolawesome.integrativeproject.utils to com.google.gson;
    exports com.coolawesome.integrativeproject;
}
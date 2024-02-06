module com.coolawesome.integrativeproject {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;

    opens com.coolawesome.integrativeproject to javafx.fxml;
    exports com.coolawesome.integrativeproject;
}
module com.trocadevolucao {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.trocadevolucao to javafx.fxml;
    opens com.trocadevolucao.controller to javafx.fxml;
    opens com.trocadevolucao.model to javafx.fxml;

    exports com.trocadevolucao;
    exports com.trocadevolucao.controller;
    exports com.trocadevolucao.model;
}
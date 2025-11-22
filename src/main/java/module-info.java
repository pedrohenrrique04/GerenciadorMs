module GerenciadorMs {
    // ----------------------------------------------------
    // REQUIRES: Módulos JavaFX e JDBC
    // ----------------------------------------------------
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;
    requires io.github.cdimascio.dotenv.java;

    // 1. VIEW
    exports com.trocadevolucao.view;
    opens com.trocadevolucao.view to javafx.fxml, javafx.graphics;

    // 2. CONTROLLERS
    exports com.trocadevolucao.controller;
    opens com.trocadevolucao.controller to javafx.fxml;

    // 3. DAO
    exports com.trocadevolucao.dao;
    opens com.trocadevolucao.dao to javafx.fxml;

    // 4. MODEL
    exports com.trocadevolucao.model;
}

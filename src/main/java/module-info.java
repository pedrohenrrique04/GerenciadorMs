module GerenciadorMs {
    // ----------------------------------------------------
    // REQUIRES: Módulos JavaFX e JDBC
    // ----------------------------------------------------
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;
    requires io.github.cdimascio.dotenv.java; // Necessário para DBConnection e DAO

    // ----------------------------------------------------
    // EXPORTS E OPENS: Acesso aos pacotes do projeto
    // ----------------------------------------------------

    // 1. Pacote da View: Contém a classe 'main' (ponto de entrada)
    // O nome do pacote deve ser 'view' em MINÚSCULO.
    exports com.trocadevolucao.view;
    opens com.trocadevolucao.view to javafx.fxml, javafx.graphics;

    // 2. Pacote dos Controllers: Contém os controllers (TrocaDevolucaoController)
    exports com.trocadevolucao.controller;
    opens com.trocadevolucao.controller to javafx.fxml;

    // 3. Pacote do DAO
    exports com.trocadevolucao.dao;
    opens com.trocadevolucao.dao to javafx.fxml;

    // 4. Pacote do Model
    exports com.trocadevolucao.model;
}
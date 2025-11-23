module gerenciador {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires com.jfoenix;

    // Conexão e Banco
    requires java.sql;
    requires mysql.connector.j;
    requires io.github.cdimascio.dotenv.java;

    exports view;
    exports controller;
    exports Model;
    exports Dao;
    exports util; // <--- Adicione o seu pacote util aqui

    opens view to javafx.graphics, javafx.fxml;
    opens controller to javafx.fxml;
    opens Model to javafx.base;
}
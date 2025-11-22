package com.trocadevolucao.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class main extends Application {

    // APONTANDO PARA O FXML QUE VOCÊ ENVIOU PARA TESTAR
    private static final String FXML_FILE = "Produto.fxml";

    @Override
    public void start(Stage primaryStage) {
        try {
            // Tenta carregar o FXML
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/trocadevolucao/view/" + FXML_FILE));

            // Note: O FXML que você enviou é BorderPane
            Scene scene = new Scene(fxmlLoader.load(), 1000, 700);

            primaryStage.setTitle("Teste de Inicialização - PRODUTOS");
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (Exception e) {
            // Captura qualquer erro (incluindo NullPointerException se o FXML não for encontrado)
            System.err.println("APLICACAO FALHOU AO INICIALIZAR. Verifique o caminho e o module-info.java.");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
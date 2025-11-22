package com.trocadevolucao.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class main extends Application {

    // === ADICIONADO AQUI ===
    private static final String FXML_FILE = "main.fxml";
    // ========================

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    getClass().getResource("/com/trocadevolucao/view/" + FXML_FILE)
            );

            Scene scene = new Scene(fxmlLoader.load(), 1000, 700);

            primaryStage.setTitle("Teste de Inicialização - PRODUTOS");
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (Exception e) {
            System.err.println("APLICACAO FALHOU AO INICIALIZAR. Verifique o caminho e o module-info.java.");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

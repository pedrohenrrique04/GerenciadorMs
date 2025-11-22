package view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/Relatorios.fxml"));

        System.out.println(getClass().getResource("/view/Relatorios.fxml")); // deve imprimir algo != null

        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Relatórios");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}

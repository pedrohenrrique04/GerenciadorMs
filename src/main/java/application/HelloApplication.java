package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {

        System.out.println("CARREGANDO: " + HelloApplication.class.getResource("/view/hello-view.fxml"));

        // CARREGA O DASHBOARD CORRETO
        FXMLLoader fxmlLoader = new FXMLLoader(
                HelloApplication.class.getResource("/view/dashboard-content-view.fxml")
        );

        Scene scene = new Scene(fxmlLoader.load());

        stage.setTitle("Dashboard");
        stage.setScene(scene);
        stage.show();
    }
}

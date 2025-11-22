package controller;

import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerSlideCloseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class RelatoriosProdutosController implements Initializable {
    @FXML
    private JFXHamburger h2;
    @FXML private JFXDrawer drawer02;
    private HamburgerSlideCloseTransition transition;
    @FXML private ComboBox<String> comboOpcoes02;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initSideMenu();
        initAnimation();
        initOutsideClickClose();

        h2.setStyle("-fx-background-color: transparent;"); // fundo transparente
        h2.getChildrenUnmodifiable().forEach(node -> {
            if (node instanceof StackPane) {
                ((StackPane) node).setStyle("-fx-background-color: white;");
            }
        });
    }

    // ------------- MENU LATERAL ----------------------

    private void initSideMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MenuLateral.fxml"));
            VBox box = loader.load();
            drawer02.setSidePane(box);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao carregar MenuLateral.fxml", e);
        }

        drawer02.setDefaultDrawerSize(240);
        drawer02.setOverLayVisible(true);
        drawer02.setResizableOnDrag(false);
        drawer02.close();
    }

    private void initAnimation() {
        transition = new HamburgerSlideCloseTransition(h2);
        transition.setRate(-1);

        h2.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
            transition.setRate(transition.getRate() * -1);
            transition.play();

            if (drawer02.isOpened()) {
                drawer02.close();
            } else {
                drawer02.toFront();
                drawer02.open();
            }
        });

        drawer02.setOnDrawerClosed(e -> {
            drawer02.toBack();
            if (transition.getRate() > 0) {
                transition.setRate(-1);
                transition.play();
            }
        });
    }

    private void initOutsideClickClose() {
        h2.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {

                    boolean clickedOutside =
                            drawer02.isOpened()
                                    && !drawer02.isHover()
                                    && !h2.isHover();

                    if (clickedOutside) {
                        drawer02.close();
                    }
                });
            }
        });
        comboOpcoes02.setOnAction(event -> {
            if ("Vendas".equals(comboOpcoes02.getValue())) {
                abrirTelaRelatorios();
            }
        });
    }
    private void abrirTelaRelatorios() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/Relatorios.fxml"));
            Scene scene = comboOpcoes02.getScene();
            scene.setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package controller;

import Dao.RelatoriosDao;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerSlideCloseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

public class RelatoriosController implements Initializable {

    @FXML private JFXHamburger h1;
    @FXML private JFXDrawer drawer;
    @FXML private ComboBox<String> comboOpcoes;

    @FXML private ComboBox<String> comboPeriodo;
    @FXML private Label lblTotalVendas;

    @FXML private Label lblLucroBruto;
    @FXML private Label lblMargemLucro;   // ← ADICIONADO

    private HamburgerSlideCloseTransition transition;

    private RelatoriosDao relatorioDAO = new RelatoriosDao();

    private LocalDateTime dataInicial;
    private LocalDateTime dataFinal;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initSideMenu();
        initAnimation();
        initOutsideClickClose();

        h1.setStyle("-fx-background-color: transparent;"); // fundo transparente
        h1.getChildrenUnmodifiable().forEach(node -> {
            if (node instanceof StackPane) {
                ((StackPane) node).setStyle("-fx-background-color: white;");
            }
        });

        comboPeriodo.setValue("Diário");
        atualizarPeriodo("Diário");

        comboPeriodo.setOnAction(event -> {
            atualizarPeriodo(comboPeriodo.getValue());
        });
    }

    private void atualizarPeriodo(String opcao) {
        LocalDate hoje = LocalDate.now();

        switch (opcao) {

            case "Diário":
                dataInicial = hoje.atStartOfDay();
                dataFinal = LocalDateTime.now();
                break;

            case "Semanal":
                dataInicial = hoje.minusDays(7).atStartOfDay();
                dataFinal = LocalDateTime.now();
                break;

            case "Mensal":
                dataInicial = hoje.withDayOfMonth(1).atStartOfDay();
                dataFinal = hoje.withDayOfMonth(hoje.lengthOfMonth()).atTime(23, 59, 59);
                break;

            case "Anual":
                dataInicial = hoje.withDayOfYear(1).atStartOfDay();
                dataFinal = hoje.withDayOfYear(hoje.lengthOfYear()).atTime(23, 59, 59);
                break;
        }

        carregarTotalDeVendas();
        carregarLucroBruto();
        carregarMargemLucro();   // ← ADICIONADO
    }

    private void carregarTotalDeVendas() {
        if (dataInicial == null || dataFinal == null) return;

        double total = relatorioDAO.getTotalVendas(dataInicial, dataFinal);
        lblTotalVendas.setText(String.format("R$ %.2f", total));
    }

    private void carregarLucroBruto() {
        if (dataInicial == null || dataFinal == null) return;

        double lucro = relatorioDAO.getLucroBruto(dataInicial, dataFinal);
        lblLucroBruto.setText(String.format("R$ %.2f", lucro));
    }

    private void carregarMargemLucro() {   // ← ADICIONADO
        if (dataInicial == null || dataFinal == null) return;

        double margem = relatorioDAO.getMargemLucro(dataInicial, dataFinal);
        lblMargemLucro.setText(String.format("%.2f%%", margem));
    }

    // ------------- MENU LATERAL ----------------------

    private void initSideMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MenuLateral.fxml"));
            VBox box = loader.load();
            drawer.setSidePane(box);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao carregar MenuLateral.fxml", e);
        }

        drawer.setDefaultDrawerSize(240);
        drawer.setOverLayVisible(true);
        drawer.setResizableOnDrag(false);
        drawer.close();
    }

    private void initAnimation() {
        transition = new HamburgerSlideCloseTransition(h1);
        transition.setRate(-1);

        h1.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
            transition.setRate(transition.getRate() * -1);
            transition.play();

            if (drawer.isOpened()) {
                drawer.close();
            } else {
                drawer.toFront();
                drawer.open();
            }
        });

        drawer.setOnDrawerClosed(e -> {
            drawer.toBack();
            if (transition.getRate() > 0) {
                transition.setRate(-1);
                transition.play();
            }
        });
    }

    private void initOutsideClickClose() {
        h1.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {

                    boolean clickedOutside =
                            drawer.isOpened()
                                    && !drawer.isHover()
                                    && !h1.isHover();

                    if (clickedOutside) {
                        drawer.close();
                    }
                });
            }
        });

        comboOpcoes.setOnAction(event -> {
            if ("Produtos".equals(comboOpcoes.getValue())) {
                abrirTelaRelatoriosProdutos();
            }
        });
    }

    private void abrirTelaRelatoriosProdutos() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/RelatoriosProdutos.fxml"));
            Scene scene = comboOpcoes.getScene();
            scene.setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

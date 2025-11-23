package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.Node;
import java.io.IOException;

public class DashboardController {

    @FXML
    private BorderPane rootPane;

    @FXML
    private BorderPane contentArea;

    @FXML
    private VBox sideMenu;

    @FXML
    public void initialize() {
        if (contentArea == null) {
            System.out.println("⚠ ERRO: contentArea NÃO foi injetado. Verifique o fx:id no FXML!");
        } else {
            System.out.println("OK: contentArea foi injetado!");
        }

        if (rootPane == null) {
            System.out.println("⚠ ERRO: rootPane NÃO foi injetado!");
        }

        if (sideMenu == null) {
            System.out.println("⚠ ERRO: sideMenu NÃO foi injetado!");
        }
    }

    // ▶ LÓGICA PADRÃO CARREGAR PÁGINAS
    private void loadView(String fxmlName) {
        try {
            Node view = FXMLLoader.load(getClass().getResource("/view/" + fxmlName));
            contentArea.setCenter(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void loadDashboard() {
        loadView("dashboard.fxml");
    }

    @FXML
    private void loadRealizarVendas() {
        loadView("realizarVendas.fxml");
    }

    @FXML
    private void loadProdutos() {
        loadView("produtos.fxml");
    }

    @FXML
    private void loadTrocaDevolucao() {
        loadView("trocaDevolucao.fxml");
    }

    @FXML
    private void loadRelatorios() {
        loadView("relatorios.fxml");
    }

    @FXML
    private void loadNotificacoes() {
        loadView("notificacoes.fxml");
    }

    @FXML
    private void loadUsuariosView() {
        loadView("usuariosView.fxml");
    }

    // ▶ ABRIR/FECHAR MENU LATERAL
    @FXML
    private void handleMenuToggle() {
        if (sideMenu.isVisible()) {
            sideMenu.setVisible(false);
            sideMenu.setManaged(false);
        } else {
            sideMenu.setVisible(true);
            sideMenu.setManaged(true);
        }
    }
}

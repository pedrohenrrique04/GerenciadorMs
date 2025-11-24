package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

public class MenuLateralController {

    // RECEBE O container principal do Dashboard
    private static AnchorPane conteudoPrincipal;

    public static void setConteudoPrincipal(AnchorPane pane) {
        conteudoPrincipal = pane;
    }

    private void trocarTela(String caminho) {
        try {
            AnchorPane tela = FXMLLoader.load(getClass().getResource(caminho));
            conteudoPrincipal.getChildren().setAll(tela);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void abrirDashboard() {
        trocarTela("dashboard-content-view.fxml");
    }

    @FXML
    public void abrirVendas() {
        trocarTela("/view/Vendas.fxml");
    }

    @FXML
    public void abrirProdutos() {
        trocarTela("/view/Produtos.fxml");
    }

    @FXML
    public void abrirTroca() {
        trocarTela("/view/Troca.fxml");
    }

    @FXML
    public void abrirRelatorios() {
        trocarTela("/view/Relatorios.fxml");
    }

    @FXML
    public void abrirNotificacoes() {
        trocarTela("/view/Notificacoes.fxml");
    }

    @FXML
    public void abrirPerfil() {
        trocarTela("/view/Perfil.fxml");
    }
}

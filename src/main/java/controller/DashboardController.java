package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.Node;
import java.io.IOException;
import java.net.URL;

// IMPORTAÇÕES NECESSÁRIAS PARA ACESSAR AS VIEWS EM JAVA PURO
import view.TelaRealizarVenda;
import view.TelaNotificacoes;

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
    }

    // ▶ LÓGICA DE CARREGAMENTO PADRÃO PARA ARQUIVOS FXML
    private void loadView(String fxmlName) {
        try {
            // Verifica se o arquivo existe ANTES de tentar carregar
            URL resource = getClass().getResource("/view/" + fxmlName);

            if (resource == null) {
                System.err.println("❌ ERRO CRÍTICO: Arquivo não encontrado: /view/" + fxmlName);
                System.err.println("   -> Verifique se o nome está exato (maiúsculas/minúsculas importam).");
                return; // Para a execução aqui para não travar o app
            }

            Node view = FXMLLoader.load(resource);
            contentArea.setCenter(view);
            System.out.println("✅ Sucesso ao carregar: " + fxmlName);

        } catch (IOException e) {
            System.err.println("❌ Erro ao processar o FXML: " + fxmlName);
            e.printStackTrace();
        }
    }

    // ====================================================================
    // CARREGAR A TELA DE VENDAS (JAVA PURO)
    // ====================================================================
    @FXML
    private void loadRealizarVendas() {
        System.out.println("🔄 Abrindo tela de Vendas (TelaRealizarVenda.java)...");
        try {
            TelaRealizarVenda telaVendas = new TelaRealizarVenda();
            BorderPane conteudoVendas = telaVendas.getTela(); // Chama getTela()
            contentArea.setCenter(conteudoVendas);
            System.out.println("✅ Sucesso ao carregar TelaRealizarVenda.");

        } catch (Exception e) {
            System.err.println("❌ Erro ao carregar a tela de vendas construída em Java.");
            e.printStackTrace();
        }
    }
    // ====================================================================

    // ====================================================================
    // CARREGAR A TELA DE NOTIFICAÇÕES (JAVA PURO)
    // ====================================================================
    @FXML
    private void loadNotificacoes() {
        System.out.println("🔄 Abrindo tela de Notificações (TelaNotificacoes.java)...");
        try {
            TelaNotificacoes telaNotificacoes = new TelaNotificacoes();

            // CHAMA O MÉTODO getTela() QUE DEVE ESTAR NA CLASSE TelaNotificacoes
            BorderPane conteudoNotificacoes = telaNotificacoes.getTela();

            contentArea.setCenter(conteudoNotificacoes);
            System.out.println("✅ Sucesso ao carregar TelaNotificacoes.");

        } catch (Exception e) {
            System.err.println("❌ Erro ao carregar a tela de notificações construída em Java.");
            e.printStackTrace();
        }
    }
    // ====================================================================


    // 👇 CARREGAMENTO DE OUTRAS TELAS FXML

    @FXML
    private void loadDashboard() {
        loadView("dashboard-content-view.fxml");
    }

    @FXML
    private void loadRelatorios() {
        loadView("Relatorios.fxml");
    }

    @FXML
    private void loadProdutos() {
        loadView("RelatoriosProdutos.fxml");
    }

    @FXML
    private void loadUsuariosView() {
        loadView("usuario-view.fxml");
    }

    // 👇 TELAS QUE PROVAVELMENTE AINDA PRECISAM DE ARQUIVO FXML

    @FXML
    private void loadTrocaDevolucao() {
        loadView("trocaDevolucao.fxml");
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
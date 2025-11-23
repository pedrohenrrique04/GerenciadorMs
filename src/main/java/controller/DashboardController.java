package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.Node;
import java.io.IOException;
import java.net.URL;

public class DashboardController {

    @FXML
    private BorderPane rootPane;

    @FXML
    private BorderPane contentArea;

    @FXML
    private VBox sideMenu;

    @FXML
    public void initialize() {
        // Seus logs de verificação estavam ótimos, mantive eles
        if (contentArea == null) {
            System.out.println("⚠ ERRO: contentArea NÃO foi injetado. Verifique o fx:id no FXML!");
        } else {
            System.out.println("OK: contentArea foi injetado!");
        }
    }

    // ▶ LÓGICA DE CARREGAMENTO (Com proteção contra erros)
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

    // 👇 AQUI ESTAVAM OS ERROS DE NOMES

    @FXML
    private void loadDashboard() {
        // Nome na pasta: dashboard-content-view.fxml
        loadView("dashboard-content-view.fxml");
    }

    @FXML
    private void loadRelatorios() {
        // Nome na pasta: Relatorios.fxml (O 'R' maiúsculo é obrigatório!)
        loadView("Relatorios.fxml");
    }

    @FXML
    private void loadProdutos() {
        // Nome na pasta: RelatoriosProdutos.fxml (Parece ser esse pela imagem)
        // Se você tiver uma tela de cadastro separada, precisa CRIAR o arquivo produtos.fxml
        loadView("RelatoriosProdutos.fxml");
    }

    @FXML
    private void loadUsuariosView() {
        // Nome na pasta: usuario-view.fxml
        loadView("usuario-view.fxml");
    }

    // 👇 ESTES ARQUIVOS NÃO APARECEM NA SUA IMAGEM
    // Eles vão dar erro "Arquivo não encontrado" no console, mas o app não vai fechar.
    // Você precisa criar esses arquivos na pasta resources/view

    @FXML
    private void loadRealizarVendas() {
        loadView("realizarVendas.fxml"); // ⚠ Arquivo faltando na pasta
    }

    @FXML
    private void loadTrocaDevolucao() {
        loadView("trocaDevolucao.fxml"); // ⚠ Arquivo faltando na pasta
    }

    @FXML
    private void loadNotificacoes() {
        loadView("notificacoes.fxml"); // ⚠ Arquivo faltando na pasta
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
package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.Node;
import java.io.IOException;
import java.net.URL;

// IMPORTA&Ccedil;&Otilde;ES NECESS&Aacute;RIAS PARA ACESSAR AS VIEWS EM JAVA PURO
import view.TelaRealizarVenda;
import view.TelaNotificacoes;
import view.TelaProdutos; // <--- NOVO IMPORT ADICIONADO

public class DashboardController {

    @FXML
    private BorderPane rootPane;

    @FXML
    private BorderPane contentArea;

    @FXML
    private VBox sideMenu;

    // --- INSTÂNCIAS DE TELAS EM JAVA PURO ---
    // Variáveis para manter as instâncias das telas (melhora a performance)
    // A instância é criada uma única vez, garantindo acesso em toda a classe (Opção 1)
    private final TelaProdutos telaProdutosInstance = new TelaProdutos();

    // ... (Instâncias das outras telas, se necessário, como telaVendas, etc.)
    // private final TelaRealizarVenda telaVendasInstance = new TelaRealizarVenda();

    @FXML
    public void initialize() {
        if (contentArea == null) {
            System.out.println("⚠ ERRO: contentArea NÃO foi injetado. Verifique o fx:id no FXML!");
        } else {
            System.out.println("OK: contentArea foi injetado!");
            // Carrega uma tela padrão (opcional)
            // loadDashboard();
        }
    }

    // ▶ LÓGICA DE CARREGAMENTO PADRÃO PARA ARQUIVOS FXML
    private void loadView(String fxmlName) {
        try {
            // Verifica se o arquivo existe ANTES de tentar carregar
            URL resource = getClass().getResource("/view/" + fxmlName);

            if (resource == null) {
                System.err.println(" ERRO CRITICO: Arquivo não encontrado: /view/" + fxmlName);
                System.err.println("   -> Verifique se o nome está exato (maiúsculas/minúsculas importam).");
                return; // Para a execução aqui para não travar o app
            }

            Node view = FXMLLoader.load(resource);
            contentArea.setCenter(view);
            System.out.println(" Sucesso ao carregar: " + fxmlName);

        } catch (IOException e) {
            System.err.println(" Erro ao processar o FXML: " + fxmlName);
            e.printStackTrace();
        }
    }

    // ====================================================================
    // CARREGAR A TELA DE PRODUTOS (JAVA PURO)
    // ====================================================================
    @FXML
    private void loadProdutosView() {
        System.out.println("🔄 Abrindo tela de Produtos (TelaProdutos.java)...");
        try {
            // Usa a instância única criada acima
            BorderPane conteudoProdutos = telaProdutosInstance.getTela();
            contentArea.setCenter(conteudoProdutos);
            System.out.println(" Sucesso ao carregar TelaProdutos.");

        } catch (Exception e) {
            System.err.println(" Erro ao carregar a tela de produtos construída em Java.");
            e.printStackTrace();
        }
    }
    // ====================================================================


    // ====================================================================
    // CARREGAR A TELA DE VENDAS (JAVA PURO)
    // ====================================================================
    @FXML
    private void loadRealizarVendas() {
        System.out.println("🔄 Abrindo tela de Vendas (TelaRealizarVenda.java)...");
        try {
            // Recomendo usar uma instância de classe para evitar recriação constante:
            TelaRealizarVenda telaVendas = new TelaRealizarVenda();
            BorderPane conteudoVendas = telaVendas.getTela(); // Chama getTela()
            contentArea.setCenter(conteudoVendas);
            System.out.println(" Sucesso ao carregar TelaRealizarVenda.");

        } catch (Exception e) {
            System.err.println(" Erro ao carregar a tela de vendas construída em Java.");
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
            System.err.println(" Erro ao carregar a tela de notificações construída em Java.");
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

    @FXML // <--- MÉTODO ORIGINALMENTE LIGADO A UM FXML AGORA DESLIGADO/REMOVIDO
    private void loadProdutos() {
        // loadView("RelatoriosProdutos.fxml"); // Foi substituído por loadProdutosView()
        // Se você quer carregar o catálogo de produtos, chame o novo método:
        loadProdutosView();
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

    // Inclua este método no seu DashboardController e ligue-o ao evento de fechamento
    // da janela principal (Stage) para salvar os dados de produtos.
    public void salvarDadosAoEncerrar() {
        //  CORREÇÃO CRITICA APLICADA:
        // A chamada 'telaProdutosInstance.salvarProdutos();' FOI REMOVIDA.
        // Motivo: A persistência de produtos agora é feita via ProdutoDAO (MySQL)
        // e ocorre em tempo real (instantaneamente) na classe TelaProdutos,
        // quando um novo produto é criado. O salvamento em arquivo (.dat) foi descontinuado.

        System.out.println("O Dashboard está sendo encerrado. A persistência de produtos está garantida pelo MySQL.");
    }
}
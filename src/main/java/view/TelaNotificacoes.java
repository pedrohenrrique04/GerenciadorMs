package view;

import Model.Notification;
import Model.NotificationType;
import Dao.NotificacaoDAO; // --- 1. IMPORTAR O NOVO DAO ---
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node; // Importar Node
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.time.LocalDateTime;

public class TelaNotificacoes extends Application {

    // --- ESTILIZAÇÃO (Consistente com a Tela de Venda) ---
    private static final String COR_FUNDO = "#f7f9fc";
    private static final String COR_BORDA = "#e0e0e0";
    private static final String COR_TEXTO_TITULO = "#333";
    private static final String COR_TEXTO_NORMAL = "#555";
    private static final String COR_TEXTO_SUBTILO = "#888";

    // --- MUDANÇA AQUI ---
    // A lista agora é lida do banco de dados
    private final ObservableList<Notification> todasNotificacoes =
            NotificacaoDAO.buscarTodasNotificacoes();
    // --- FIM DA MUDANÇA ---

    // Lista filtrada que será exibida na tela
    private FilteredList<Notification> notificacoesFiltradas;
    private ToggleGroup filtroCategoria;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Central de Notificações");

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + COR_FUNDO + ";");

        // --- 1. BARRA SUPERIOR (Título) ---
        root.setTop(createTopBar());

        // --- 2. FILTROS (Esquerda) ---
        root.setLeft(createFilterSidebar());

        // --- 3. LISTA DE NOTIFICAÇÕES (Centro) ---
        root.setCenter(createNotificationList());

        // Define o predicado inicial (mostrar tudo)
        aplicarFiltro();

        Scene scene = new Scene(root, 900, 600);
        primaryStage.setScene(scene);
        primaryStage.setMinHeight(500);
        primaryStage.setMinWidth(800);
        primaryStage.show();
    }

    /**
     * Cria a barra superior com o ícone de menu e o título.
     */
    private Node createTopBar() {
        HBox topBar = new HBox(20);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(15, 25, 15, 25));
        topBar.setStyle("-fx-background-color: white; -fx-border-color: " + COR_BORDA + "; -fx-border-width: 0 0 1 0;");

        // Ícone de Menu (como no wireframe)
        Label lblMenuIcon = new Label("☰");
        lblMenuIcon.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        lblMenuIcon.setTextFill(Color.web(COR_TEXTO_NORMAL));

        // Título
        Label lblTitulo = new Label("NOTIFICAÇÕES");
        lblTitulo.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        lblTitulo.setTextFill(Color.web(COR_TEXTO_TITULO));

        topBar.getChildren().addAll(lblMenuIcon, lblTitulo);
        return topBar;
    }

    /**
     * Cria a barra lateral de filtros (como no wireframe).
     */
    private Node createFilterSidebar() {
        VBox sidebar = new VBox(15);
        sidebar.setPadding(new Insets(20));
        sidebar.setStyle("-fx-background-color: white; -fx-border-color: " + COR_BORDA + "; -fx-border-width: 0 1 0 0;");
        sidebar.setPrefWidth(220);

        Label lblFiltro = new Label("Filtro");
        lblFiltro.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        lblFiltro.setTextFill(Color.web(COR_TEXTO_TITULO));

        Separator separator = new Separator();

        // Categorias de Filtro
        Label lblCategoria = new Label("Categoria");
        lblCategoria.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        lblCategoria.setTextFill(Color.web(COR_TEXTO_NORMAL));

        filtroCategoria = new ToggleGroup();

        // Adiciona os botões de rádio
        RadioButton rbTodas = createFilterRadioButton("Todas", "todas");
        rbTodas.setSelected(true); // Começa selecionado
        RadioButton rbVendas = createFilterRadioButton("Venda realizada", "venda");
        RadioButton rbEstoque = createFilterRadioButton("Produto em falta", "estoque");
        RadioButton rbNaoLidas = createFilterRadioButton("Não lida", "nao_lida");
        RadioButton rbLidas = createFilterRadioButton("Lida", "lida");

        // Adiciona um listener para aplicar o filtro quando o botão mudar
        filtroCategoria.selectedToggleProperty().addListener((obs, oldVal, newVal) -> aplicarFiltro());

        sidebar.getChildren().addAll(
                lblFiltro, separator, lblCategoria,
                rbTodas, rbVendas, rbEstoque, rbNaoLidas, rbLidas
        );

        return sidebar;
    }

    /**
     * Helper para criar um RadioButton padronizado.
     */
    private RadioButton createFilterRadioButton(String text, String userData) {
        RadioButton rb = new RadioButton(text);
        rb.setToggleGroup(filtroCategoria);
        rb.setUserData(userData); // Valor que usaremos para filtrar
        rb.setFont(Font.font("Arial", 13));
        rb.setTextFill(Color.web(COR_TEXTO_SUBTILO));
        return rb;
    }

    /**
     * Cria a lista central de notificações.
     */
    private Node createNotificationList() {
        // Envolve a lista principal em uma lista filtrável
        notificacoesFiltradas = new FilteredList<>(todasNotificacoes, p -> true);

        // Cria a ListView e associa à lista filtrada
        ListView<Notification> listView = new ListView<>(notificacoesFiltradas);

        listView.setCellFactory(param -> new NotificationCell());

        listView.setStyle("-fx-background-color: transparent;"); // Fundo transparente

        // Adiciona padding ao container da lista
        StackPane containerLista = new StackPane(listView);
        containerLista.setPadding(new Insets(20));

        return containerLista;
    }

    /**
     * Aplica o filtro selecionado à 'FilteredList'.
     */
    private void aplicarFiltro() {
        Toggle selecionado = filtroCategoria.getSelectedToggle();
        if (selecionado == null) {
            notificacoesFiltradas.setPredicate(p -> true); // Mostra tudo
            return;
        }

        String filtro = (String) selecionado.getUserData();

        notificacoesFiltradas.setPredicate(notificacao -> {
            switch (filtro) {
                case "venda":
                    return notificacao.getType() == NotificationType.INFO;
                case "estoque":
                    return notificacao.getType() == NotificationType.ALERTA;
                case "lida":
                    return notificacao.isRead();
                case "nao_lida":
                    return !notificacao.isRead();
                case "todas":
                default:
                    return true;
            }
        });
    }
}
package view;

import service.NotificationService;
import Model.Notification;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.beans.binding.Bindings;
import javafx.beans.Observable; // Import necessário

/**
 * Constrói a tela de Notificações, carregando dados do NotificationService.
 * Requer que o NotificationCell.java esteja implementado para renderização correta
 * e que o NotificationService.java tenha os métodos getAllNotifications() e marcarTodasComoLidas().
 */
public class TelaNotificacoes {

    private final BorderPane tela;
    private final ListView<Notification> listView;
    private final NotificationService service;

    public TelaNotificacoes() {
        // Inicializa o serviço (Singleton), assumindo que ele já foi corrigido
        service = NotificationService.getInstance();

        tela = new BorderPane();
        tela.setStyle("-fx-background-color: #f4f7fa;");

        // 1. Componente ListView
        listView = new ListView<>();
        // Define a fábrica de células (Assumindo que NotificationCell existe)
        // Se NotificationCell não estiver implementada, substitua por -> new ListCell<Notification>()
        listView.setCellFactory(lv -> new NotificationCell());

        // --- CORRIGIDO: Obtém a lista observável do Service (resolve o erro de getAllNotifications) ---
        ObservableList<Notification> notificacoes = service.getAllNotifications();
        listView.setItems(notificacoes);

        listView.setStyle("-fx-background-color: transparent; -fx-padding: 0;");

        // 2. Área Principal
        VBox contentBox = new VBox(10, listView);
        contentBox.setPadding(new Insets(20));
        contentBox.setStyle("-fx-background-color: #f4f7fa;");

        // 3. Configuração do Layout
        tela.setTop(createHeader(notificacoes));
        tela.setCenter(contentBox);
    }

    /**
     * MÉTODO OBRIGATÓRIO: Retorna o layout principal da tela.
     */
    public BorderPane getTela() {
        return tela;
    }

    private VBox createHeader(ObservableList<Notification> data) {
        Label title = new Label("Central de Notificações");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setTextFill(Color.web("#333333"));

        Label subtitle = new Label();
        subtitle.setFont(Font.font("Arial", 14));
        subtitle.setTextFill(Color.web("#666666"));

        // --- CORREÇÃO DO BINDING REATIVO ---

        // O Bindings.createStringBinding espera uma lista de objetos Observable como dependências.
        // Como o NotificationService inicializa a lista 'data' com um PropertyExtractor
        // (Notification::lidaProperty), observar apenas a lista é suficiente para reagir a
        // adições/remoções e mudanças na propriedade 'lida' de qualquer item.

        // CORRIGIDO: Removida a sintaxe incorreta que causava o erro "incompatible types"
        Observable[] dependencies = new Observable[] { data };


        // Cria o Binding que calcula a contagem
        subtitle.textProperty().bind(
                Bindings.createStringBinding(() -> {
                    // Contagem de não lidas
                    long unreadCount = data.stream()
                            .filter(n -> !n.isLida()) // Usa o método correto do Model
                            .count();

                    return String.format("Você tem %d itens não lidos. (Total: %d)", unreadCount, data.size());
                }, dependencies)
        );

        // --- FIM DA CORREÇÃO DO BINDING ---

        VBox titleBox = new VBox(5, title, subtitle);

        // Botão de Ação Rápida
        Button btnMarcarTodosLidos = new Button("Marcar Todos Como Lidos");
        btnMarcarTodosLidos.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 15; -fx-background-radius: 5;");
        btnMarcarTodosLidos.setCursor(javafx.scene.Cursor.HAND);

        // --- CORRIGIDO: Usa o método implementado 'marcarTodasComoLidas()' (resolve o segundo erro) ---
        btnMarcarTodosLidos.setOnAction(e -> service.marcarTodasComoLidas());

        // HBox para Título e Botão
        HBox headerContent = new HBox(15, titleBox, btnMarcarTodosLidos);
        headerContent.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(titleBox, javafx.scene.layout.Priority.ALWAYS);

        // VBox Principal do Cabeçalho com padding
        VBox header = new VBox(15, headerContent);
        header.setPadding(new Insets(30, 20, 20, 20));
        header.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-width: 0 0 1 0; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 5, 0.1, 0, 1);");

        return header;
    }
}
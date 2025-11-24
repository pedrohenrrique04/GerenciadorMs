package view;

import Dao.NotificacaoDAO;
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
import javafx.beans.binding.Bindings; // Import necessário

/**
 * Constrói a tela de Notificações, carregando dados do DAO e utilizando
 * a NotificationCell customizada para renderização.
 * Contém o método obrigatório getTela().
 */
public class TelaNotificacoes {

    private final BorderPane tela;
    private final ListView<Notification> listView;

    public TelaNotificacoes() {
        tela = new BorderPane();
        tela.setStyle("-fx-background-color: #f4f7fa;"); // Fundo suave

        // 1. Componente ListView
        listView = new ListView<>();
        // Define a fábrica de células para usar a sua classe NotificationCell
        listView.setCellFactory(lv -> new NotificationCell());
        listView.setStyle("-fx-background-color: transparent; -fx-padding: 0;");

        // 2. Carrega os dados Mock
        ObservableList<Notification> notificacoes = NotificacaoDAO.buscarTodasNotificacoes();
        listView.setItems(notificacoes);

        // 3. &Aacute;rea Principal (ListView dentro de um VBox para melhor padding)
        VBox contentBox = new VBox(10, listView);
        contentBox.setPadding(new Insets(20));
        contentBox.setStyle("-fx-background-color: #f4f7fa;");

        // 4. Configuração do Layout
        tela.setTop(createHeader(notificacoes));
        tela.setCenter(contentBox);
    }

    /**
     * MÉTODO OBRIGATÓRIO: Retorna o layout principal da tela.
     * Este método é chamado pelo DashboardController.
     * @return O BorderPane completo da tela de notificações.
     */
    public BorderPane getTela() {
        return tela;
    }

    private VBox createHeader(ObservableList<Notification> data) {
        // Título e Subtítulo
        Label title = new Label("Central de Notificações");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setTextFill(Color.web("#333333"));

        Label subtitle = new Label();
        subtitle.setFont(Font.font("Arial", 14));
        subtitle.setTextFill(Color.web("#666666"));

        // --- IN&Iacute;CIO DA CORRE&Ccedil;&Atilde;O DO BINDING ---
        // 1. Coleta todas as readProperty() em um array de ObservableValue
        javafx.beans.value.ObservableValue<?>[] readProperties = data.stream()
                .map(Notification::readProperty)
                .toArray(javafx.beans.value.ObservableValue[]::new);

        // 2. Combina a lista observável 'data' e o array de propriedades
        // O Binding precisa observar a lista 'data' (para adições/remoções)
        // e todas as 'readProperty' (para mudanças de status de leitura)

        // Cria um array de dependências, incluindo a lista 'data'
        javafx.beans.Observable[] dependencies = new javafx.beans.Observable[readProperties.length + 1];
        dependencies[0] = data; // A lista em si
        System.arraycopy(readProperties, 0, dependencies, 1, readProperties.length);


        // 3. Aplica o Binding (que agora recebe o array combinado)
        subtitle.textProperty().bind(
                Bindings.createStringBinding(() -> {
                    long unreadCount = data.filtered(n -> !n.isRead()).size();
                    return String.format("Você tem %d itens não lidos. (Total: %d)", unreadCount, data.size());
                }, dependencies)
        );
        // --- FIM DA CORREÇÃO DO BINDING ---

        VBox titleBox = new VBox(5, title, subtitle);

        // Botão de Ação Rápida
        Button btnMarcarTodosLidos = new Button("Marcar Todos Como Lidos");
        btnMarcarTodosLidos.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 15; -fx-background-radius: 5;");
        btnMarcarTodosLidos.setCursor(javafx.scene.Cursor.HAND);

        btnMarcarTodosLidos.setOnAction(e -> marcarTodosComoLidos(data));

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

    /**
     * Marca todas as notificações como lidas.
     * @param notificacoes Lista observável de notificações.
     */
    private void marcarTodosComoLidos(ObservableList<Notification> notificacoes) {
        // Itera sobre a lista e chama o setter (o Binding faz o resto)
        for (Notification n : notificacoes) {
            if (!n.isRead()) { // Apenas marca se ainda não foi lido
                n.setRead(true);
            }
        }
        System.out.println("✅ Todas as notificações marcadas como lidas.");
    }
}
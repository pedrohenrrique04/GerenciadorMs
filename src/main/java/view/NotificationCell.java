package view;

import Model.Notification;
import Model.NotificationType;
import service.NotificationService; // Essencial para chamar a lógica de marcação de leitura
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Célula customizada para a ListView de Notificações.
 * Desenha cada item da lista com cores, checkbox e layout,
 * sincronizando o estado de leitura com o NotificationService.
 */
public class NotificationCell extends ListCell<Notification> {

    // Instância do serviço para operações de marcação de leitura no banco
    private final NotificationService service = NotificationService.getInstance();

    // --- Cores baseadas no tipo de notificação (ajustadas para o seu código) ---
    private static final String COR_CRITICO_FUNDO = "#fce4ec"; // Vermelho/Rosa claro
    private static final String COR_CRITICO_BORDA = "#e74c3c"; // Vermelho forte (Usando cor do Enum do usuário)
    private static final String COR_ALERTA_FUNDO = "#fffde7";  // Amarelo bem claro
    private static final String COR_ALERTA_BORDA = "#f39c12";  // Laranja (Usando cor do Enum do usuário)
    private static final String COR_INFO_FUNDO = "#e8f5e9";    // Verde claro
    private static final String COR_INFO_BORDA = "#3498db";    // Azul (Usando cor do Enum do usuário)

    private final HBox layout;
    private final CheckBox checkBox;
    private final Label lblType;
    private final Label lblMessage;
    private final Label lblTimestamp;
    private final Region spacer;

    private Notification currentNotification;

    public NotificationCell() {
        super();

        // 1. Componentes da Célula
        checkBox = new CheckBox();
        checkBox.setCursor(javafx.scene.Cursor.HAND);

        lblType = new Label();
        lblType.setFont(Font.font("Arial", FontWeight.BOLD, 12));

        lblMessage = new Label();
        lblMessage.setFont(Font.font("Arial", 14));
        lblMessage.setWrapText(true);

        lblTimestamp = new Label();
        lblTimestamp.setFont(Font.font("Arial", 12));
        lblTimestamp.setTextFill(Color.web("#777"));

        spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // 2. Layout Principal da Célula (HBox)
        VBox messageContainer = new VBox(2, lblType, lblMessage);

        layout = new HBox(15);
        layout.setAlignment(Pos.CENTER_LEFT);
        layout.setPadding(new Insets(12, 15, 12, 15));
        layout.getChildren().addAll(checkBox, messageContainer, spacer, lblTimestamp);

        // Estilo base do layout
        layout.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(8), Insets.EMPTY)));
        layout.setBorder(new Border(new BorderStroke(Color.web("#e0e0e0"), BorderStrokeStyle.SOLID, new CornerRadii(8), BorderWidths.DEFAULT)));
        layout.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 8, 0.1, 0, 2);");

        // Listener para o CheckBox que aciona a marcação de leitura no Service/DAO.
        checkBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            // Garante que a notificação é válida e que o estado realmente mudou (evita loops)
            if (currentNotification != null && newVal != oldVal) {
                // Se a caixa for marcada, marca como lida no serviço
                if (newVal) {
                    service.markAsRead(currentNotification);
                    // O service cuida de remover o item da lista observável, atualizando o Dashboard.
                }
                // Nota: O método markAsRead apenas remove da lista de 'Não Lidas'.
                // Se o usuário desmarcar (false), o item não volta para a lista até o próximo reload.
            }
        });
    }

    /**
     * Este método é chamado pelo JavaFX para desenhar/atualizar a célula.
     */
    @Override
    protected void updateItem(Notification notification, boolean empty) {
        super.updateItem(notification, empty);

        // Limpa o item anterior
        if (currentNotification != null) {
            checkBox.selectedProperty().unbind();
            layout.opacityProperty().unbind();
            currentNotification = null;
        }

        if (empty || notification == null) {
            setGraphic(null);
            setText(null);
            setStyle("-fx-background-color: transparent; -fx-padding: 0;");
        } else {
            // 1. Guarda a referência do novo item
            currentNotification = notification;

            // 2. Define os dados usando os getters do Model (propriedades)
            // Chamadas ajustadas para os nomes definidos no Model/Notification.java:
            lblMessage.setText(notification.getMensagem());
            lblTimestamp.setText(notification.getFormattedTimestamp());
            lblType.setText(notification.getTipo().getDisplay());

            // 3. Conecta o checkbox à propriedade 'lida' do Model (unidirecional)
            // A mudança na tela é tratada no listener acima
            checkBox.selectedProperty().bind(currentNotification.lidaProperty());

            // 4. Define a Cor (baseado no tipo)
            NotificationType type = notification.getTipo();
            String backgroundColor;
            String borderColor;
            String messageColor;

            // Usando as constantes ajustadas para cores do seu Enum:
            switch (type) {
                case CRITICO:
                    backgroundColor = COR_CRITICO_FUNDO;
                    borderColor = COR_CRITICO_BORDA;
                    break;
                case ALERTA:
                    backgroundColor = COR_ALERTA_FUNDO;
                    borderColor = COR_ALERTA_BORDA;
                    break;
                case INFO:
                default:
                    backgroundColor = COR_INFO_FUNDO;
                    borderColor = COR_INFO_BORDA;
                    break;
            }
            messageColor = borderColor; // A cor da mensagem segue a cor da borda/prioridade

            // Aplica os estilos
            layout.setBorder(new Border(new BorderStroke(Color.web(borderColor), BorderStrokeStyle.SOLID, new CornerRadii(8), new BorderWidths(1))));
            layout.setBackground(new Background(new BackgroundFill(Color.web(backgroundColor), new CornerRadii(8), Insets.EMPTY)));
            lblMessage.setTextFill(Color.web(messageColor));
            lblType.setTextFill(Color.web(borderColor).darker());

<<<<<<< HEAD
=======
            // --- INÍCIO DA CORREÇÃO ---
>>>>>>> parent of ba0e6b0 (telaprodutos&telarealizarvendacomBANCO)
            // 5. Define a Opacidade (baseado no status 'lida')
            // Binda a opacidade à propriedade 'lida'
            layout.opacityProperty().bind(
                    Bindings.when(currentNotification.lidaProperty())
                            .then(0.6)   // Se 'lida' for true, opacidade = 0.6
                            .otherwise(1.0) // Se 'lida' for false, opacidade = 1.0
            );

            // 6. Define o gráfico e espaçamento da célula
            setGraphic(layout);
            setStyle("-fx-background-color: transparent; -fx-padding: 5 0 5 0;");
        }
    }
}
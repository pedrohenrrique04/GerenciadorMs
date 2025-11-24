package view;

import Model.Notification;
import Model.NotificationType;
// --- MUDANÇA ---
import javafx.beans.binding.Bindings; // Import necessário
// --- FIM DA MUDANÇA ---
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.util.Objects;

/**
 * Célula customizada para a ListView de Notificações.
 * Desenha cada item da lista com cores, checkbox e layout.
 * --- VERSÃO CORRIGIDA 3 ---
 */
public class NotificationCell extends ListCell<Notification> {

    // --- Cores baseadas no tipo de notificação ---
    private static final String COR_ALERTA_FUNDO = "#ffebee"; // Vermelho claro
    private static final String COR_ALERTA_BORDA = "#f44336"; // Vermelho
    private static final String COR_INFO_FUNDO = "#e8f5e9";   // Verde claro
    private static final String COR_INFO_BORDA = "#4CAF50";   // Verde

    private final HBox layout;
    private final CheckBox checkBox;
    private final Label lblMessage;
    private final Label lblTimestamp;
    private final Region spacer; // Espaçador flexível

    private Notification currentNotification;

    public NotificationCell() {
        super();

        // 1. Componentes da Célula
        checkBox = new CheckBox();
        checkBox.setCursor(javafx.scene.Cursor.HAND);

        lblMessage = new Label();
        lblMessage.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        lblTimestamp = new Label();
        lblTimestamp.setFont(Font.font("Arial", 12));
        lblTimestamp.setTextFill(Color.web("#777"));

        spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS); // Ocupa todo o espaço

        // 2. Layout Principal da Célula (HBox)
        layout = new HBox(15); // 15px de espaçamento
        layout.setAlignment(Pos.CENTER_LEFT);
        layout.setPadding(new Insets(12, 15, 12, 15));
        layout.getChildren().addAll(checkBox, lblMessage, spacer, lblTimestamp);

        // Estilo base do layout
        layout.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(8), Insets.EMPTY)));
        layout.setBorder(new Border(new BorderStroke(Color.web("#e0e0e0"), BorderStrokeStyle.SOLID, new CornerRadii(8), BorderWidths.DEFAULT)));
        layout.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 8, 0.1, 0, 2);");
    }

    /**
     * Este método é chamado pelo JavaFX para desenhar/atualizar a célula.
     */
    @Override
    protected void updateItem(Notification notification, boolean empty) {
        super.updateItem(notification, empty);

        // Se a célula estiver sendo reciclada (tinha um item antigo)
        if (currentNotification != null) {
            // Desconecte (unbind) os listeners do item ANTIGO
            checkBox.selectedProperty().unbindBidirectional(currentNotification.readProperty());
            layout.opacityProperty().unbind();
            currentNotification = null; // Limpa a referência
        }

        if (empty || notification == null) {
            setText(null);
            setGraphic(null);
            // Zera o estilo para células vazias
            setStyle("-fx-background-color: transparent; -fx-padding: 0;");
        } else {
            // 1. Guarda a referência do novo item
            currentNotification = notification;

            // 2. Define os dados
            lblMessage.setText(notification.getMessage());
            lblTimestamp.setText(notification.getFormattedTimestamp());

            // 3. Binda (conecta) o checkbox à propriedade 'read' da notificação
            checkBox.selectedProperty().bindBidirectional(notification.readProperty());

            // 4. Define a Cor (baseado no tipo)
            if (notification.getType() == NotificationType.ALERTA) {
                layout.setBorder(new Border(new BorderStroke(Color.web(COR_ALERTA_BORDA), BorderStrokeStyle.SOLID, new CornerRadii(8), new BorderWidths(1))));
                layout.setBackground(new Background(new BackgroundFill(Color.web(COR_ALERTA_FUNDO), new CornerRadii(8), Insets.EMPTY)));
                lblMessage.setTextFill(Color.web("#c62828"));
            } else { // INFO
                layout.setBorder(new Border(new BorderStroke(Color.web(COR_INFO_BORDA), BorderStrokeStyle.SOLID, new CornerRadii(8), new BorderWidths(1))));
                layout.setBackground(new Background(new BackgroundFill(Color.web(COR_INFO_FUNDO), new CornerRadii(8), Insets.EMPTY)));
                lblMessage.setTextFill(Color.web("#2e7d32"));
            }

            // --- INÍCIO DA CORREÇÃO ---
            // 5. Define a Opacidade (baseado no status 'lida')
            // Binda a opacidade à propriedade 'read' usando Bindings.when()
            layout.opacityProperty().bind(
                    Bindings.when(notification.readProperty())
                            .then(0.6)   // Se 'read' for true, opacidade = 0.6
                            .otherwise(1.0) // Se 'read' for false, opacidade = 1.0
            );
            // --- FIM DA CORREÇÃO ---

            // 6. Define o gráfico e remove o preenchimento padrão da célula
            setGraphic(layout);
            setStyle("-fx-background-color: transparent; -fx-padding: 5 0 5 0;"); // Espaçamento entre os itens
        }
    }
}
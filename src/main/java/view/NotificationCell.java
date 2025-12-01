package view;

import Model.Notification;
import Model.NotificationType;
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

public class NotificationCell extends ListCell<Notification> {

    // Cores
    private static final String COR_ALERTA_FUNDO = "#ffebee";
    private static final String COR_ALERTA_BORDA = "#f44336";
    private static final String COR_INFO_FUNDO = "#e8f5e9";
    private static final String COR_INFO_BORDA = "#4CAF50";

    private final HBox layout;
    private final CheckBox checkBox;
    private final Label lblMessage;
    private final Label lblTimestamp;
    private final Region spacer;

    private Notification currentNotification;

    public NotificationCell() {
        super();

        checkBox = new CheckBox();
        checkBox.setCursor(javafx.scene.Cursor.HAND);

        lblMessage = new Label();
        lblMessage.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        lblTimestamp = new Label();
        lblTimestamp.setFont(Font.font("Arial", 12));
        lblTimestamp.setTextFill(Color.web("#777"));

        spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        layout = new HBox(15);
        layout.setAlignment(Pos.CENTER_LEFT);
        layout.setPadding(new Insets(12, 15, 12, 15));
        layout.getChildren().addAll(checkBox, lblMessage, spacer, lblTimestamp);

        layout.setBackground(new Background(
                new BackgroundFill(Color.WHITE, new CornerRadii(8), Insets.EMPTY)
        ));
        layout.setBorder(new Border(
                new BorderStroke(Color.web("#e0e0e0"),
                        BorderStrokeStyle.SOLID,
                        new CornerRadii(8),
                        BorderWidths.DEFAULT)
        ));

        layout.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 8, 0.1, 0, 2);");
    }

    @Override
    protected void updateItem(Notification notification, boolean empty) {
        super.updateItem(notification, empty);

        // Desbind antigo
        if (currentNotification != null) {
            checkBox.selectedProperty().unbindBidirectional(currentNotification.lidaProperty());
            layout.opacityProperty().unbind();
            currentNotification = null;
        }

        if (empty || notification == null) {
            setGraphic(null);
            setText(null);
            setStyle("-fx-background-color: transparent;");
            return;
        }

        currentNotification = notification;

        // Corrigido: usando getters em português
        lblMessage.setText(notification.getMensagem());
        lblTimestamp.setText(notification.getFormattedTimestamp());

        // Corrigido: usando lidaProperty()
        checkBox.selectedProperty().bindBidirectional(notification.lidaProperty());

        // Estilo por tipo
        if (notification.getTipo() == NotificationType.ALERTA) {
            layout.setBorder(new Border(new BorderStroke(
                    Color.web(COR_ALERTA_BORDA),
                    BorderStrokeStyle.SOLID,
                    new CornerRadii(8),
                    new BorderWidths(1)
            )));
            layout.setBackground(new Background(
                    new BackgroundFill(Color.web(COR_ALERTA_FUNDO), new CornerRadii(8), Insets.EMPTY)
            ));
            lblMessage.setTextFill(Color.web("#c62828"));
        } else {
            layout.setBorder(new Border(new BorderStroke(
                    Color.web(COR_INFO_BORDA),
                    BorderStrokeStyle.SOLID,
                    new CornerRadii(8),
                    new BorderWidths(1)
            )));
            layout.setBackground(new Background(
                    new BackgroundFill(Color.web(COR_INFO_FUNDO), new CornerRadii(8), Insets.EMPTY)
            ));
            lblMessage.setTextFill(Color.web("#2e7d32"));
        }

        // Corrigido: opacidade usando lidaProperty()
        layout.opacityProperty().bind(
                Bindings.when(notification.lidaProperty())
                        .then(0.5)
                        .otherwise(1.0)
        );

        setGraphic(layout);
        setStyle("-fx-background-color: transparent; -fx-padding: 5 0 5 0;");
    }
}

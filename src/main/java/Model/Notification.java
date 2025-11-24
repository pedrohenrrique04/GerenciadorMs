package Model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Modelo de dados para uma Notificação.
 * Usa JavaFX Properties para data-binding com a UI.
 */
public class Notification {

    private final StringProperty message;
    private final ObjectProperty<LocalDateTime> timestamp;
    private final ObjectProperty<NotificationType> type;
    private final BooleanProperty read;

    // Formato para exibir a data e hora
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yy 'às' HH:mm");

    public Notification(String message, LocalDateTime timestamp, NotificationType type, boolean isRead) {
        this.message = new SimpleStringProperty(message);
        this.timestamp = new SimpleObjectProperty<>(timestamp);
        this.type = new SimpleObjectProperty<>(type);
        this.read = new SimpleBooleanProperty(isRead);
    }

    // --- Getters e Setters (padrão) ---
    public String getMessage() { return message.get(); }
    public LocalDateTime getTimestamp() { return timestamp.get(); }
    public NotificationType getType() { return type.get(); }
    public boolean isRead() { return read.get(); }
    public void setRead(boolean isRead) { this.read.set(isRead); }

    /**
     * Retorna a data/hora formatada para a UI.
     */
    public String getFormattedTimestamp() {
        return timestamp.get() != null ? timestamp.get().format(FORMATTER) : "";
    }

    // --- Property Getters (para o JavaFX) ---
    public StringProperty messageProperty() { return message; }
    public ObjectProperty<LocalDateTime> timestampProperty() { return timestamp; }
    public ObjectProperty<NotificationType> typeProperty() { return type; }
    public BooleanProperty readProperty() { return read; }
}
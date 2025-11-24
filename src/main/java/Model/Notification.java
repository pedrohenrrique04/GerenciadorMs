package Model;

import javafx.beans.property.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Representa uma notificação no sistema, usando JavaFX Properties
 * para permitir o binding reativo (opacidade e checkbox).
 */
public class Notification {

    // Propriedades do JavaFX para permitir o binding
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty title = new SimpleStringProperty();
    private final StringProperty message = new SimpleStringProperty();
    private final ObjectProperty<NotificationType> type = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDateTime> timestamp = new SimpleObjectProperty<>();
    private final BooleanProperty isRead = new SimpleBooleanProperty();

    // Formato de data usado para exibição
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // Construtor
    public Notification(int id, String title, String message, NotificationType type, LocalDateTime timestamp, boolean isRead) {
        this.id.set(id);
        this.title.set(title);
        this.message.set(message);
        this.type.set(type);
        this.timestamp.set(timestamp);
        this.isRead.set(isRead);
    }

    // --- Métodos de Propriedade (Obrigatórios para o binding) ---

    // Método que a NotificationCell está usando: checkBox.selectedProperty().bindBidirectional(notification.readProperty());
    public BooleanProperty readProperty() {
        return isRead;
    }

    // --- Getters e Setters (Padrão) ---

    public boolean isRead() {
        return isRead.get();
    }

    public void setRead(boolean read) {
        this.isRead.set(read);
    }

    // Método que gera a String formatada, usada na NotificationCell
    public String getFormattedTimestamp() {
        LocalDateTime time = timestamp.get();
        return time != null ? time.format(FORMATTER) : "N/A";
    }

    public NotificationType getType() {
        return type.get();
    }

    // Outros Getters (mantidos para completude)
    public int getId() { return id.get(); }
    public String getTitle() { return title.get(); }
    public String getMessage() { return message.get(); }
    public LocalDateTime getTimestamp() { return timestamp.get(); }
}
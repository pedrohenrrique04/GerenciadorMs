package Model;

import javafx.beans.property.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Modelo de dados para uma notificação.
 * Utiliza JavaFX Properties para permitir observação na interface.
 */
public class Notification {

    // Contador estático para simular IDs no lado do cliente
    // Inicializado em 0 ou 1, dependendo da sua preferência de ID.
    // Garante que o ID atribuído manualmente no DAO seja maior que o ID_COUNTER.
    private static final AtomicInteger ID_COUNTER = new AtomicInteger(100); // Começa um pouco mais alto para diferenciar mock

    // Formatador para exibição da data/hora
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    // Propriedades observáveis
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty titulo = new SimpleStringProperty();
    private final StringProperty mensagem = new SimpleStringProperty();
    private final ObjectProperty<NotificationType> tipo = new SimpleObjectProperty<>();
    private final BooleanProperty lida = new SimpleBooleanProperty(false);
    private final ObjectProperty<LocalDateTime> timestamp = new SimpleObjectProperty<>();

    // Construtor completo
    public Notification(int id, String titulo, String mensagem, NotificationType tipo, boolean lida, LocalDateTime timestamp) {
        this.id.set(id);
        this.titulo.set(titulo);
        this.mensagem.set(mensagem);
        this.tipo.set(tipo);
        this.lida.set(lida);
        this.timestamp.set(timestamp);
        // Garante que o contador estático não volte
        if (id >= ID_COUNTER.get()) {
            ID_COUNTER.set(id + 1);
        }
    }

    // Construtor simplificado para novas notificações (gera ID e timestamp)
    public Notification(String titulo, String mensagem, NotificationType tipo) {
        // ID temporário 0, será ajustado no DAO se necessário
        this(0, titulo, mensagem, tipo, false, LocalDateTime.now());
    }

    /**
     * @return O timestamp formatado para exibição na UI.
     */
    public String getFormattedTimestamp() {
        if (getTimestamp() == null) {
            return "Data Indisponível";
        }
        return getTimestamp().format(FORMATTER);
    }

    // --- Getters Simples ---
    public int getId() { return id.get(); }
    public String getTitulo() { return titulo.get(); }
    public String getMensagem() { return mensagem.get(); }
    public NotificationType getTipo() { return tipo.get(); }
    public boolean isLida() { return lida.get(); }
    public LocalDateTime getTimestamp() { return timestamp.get(); }

    // --- Setters Simples (Correção: Adicionado setId) ---
    public void setId(int id) { this.id.set(id); }
    public void setLida(boolean lida) { this.lida.set(lida); }

    // --- Propriedades JavaFX ---
    public IntegerProperty idProperty() { return id; }
    public StringProperty tituloProperty() { return titulo; }
    public StringProperty mensagemProperty() { return mensagem; }
    public ObjectProperty<NotificationType> tipoProperty() { return tipo; }
    public BooleanProperty lidaProperty() { return lida; }
    public ObjectProperty<LocalDateTime> timestampProperty() { return timestamp; }
}
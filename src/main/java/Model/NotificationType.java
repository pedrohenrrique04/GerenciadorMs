package Model;

/**
 * Enum para definir o tipo de notificação, influenciando a cor e a prioridade.
 */
public enum NotificationType {
    INFO("Informação", "#3498db"), // Azul (e.g., Venda realizada)
    ALERTA("Alerta", "#f39c12"),   // Laranja (e.g., Estoque baixo)
    CRITICO("Crítico", "#e74c3c"); // <--- ESTA LINHA ESTAVA FALTANDO OU ERRADA. Vermelho (e.g., Produto fora de estoque)

    private final String display;
    private final String color;

    NotificationType(String display, String color) {
        this.display = display;
        this.color = color;
    }

    public String getDisplay() {
        return display;
    }

    public String getColor() {
        return color;
    }
}
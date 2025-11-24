package Model;

/**
 * Enum que define os possíveis tipos de notificação no sistema.
 * Usado pelo NotificationService para categorizar e processar alertas.
 */
public enum NotificationType {

    CRITICO("CRÍTICO", "Alerta Imediato, requer atenção urgente."),
    ALERTA("ALERTA", "Atenção Necessária, mas não bloqueia a operação."),
    INFO("INFO", "Informação Geral, feedback de sucesso ou rotina.");

    private final String display;
    private final String description;

    NotificationType(String display, String description) {
        this.display = display;
        this.description = description;
    }

    public String getDisplay() {
        return display;
    }

    public String getDescription() {
        return description;
    }
}
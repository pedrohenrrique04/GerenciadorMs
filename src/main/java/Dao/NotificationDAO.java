package Dao;

import Model.Notification;
import Model.NotificationType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Data Access Object (DAO) para a entidade Notification.
 * Esta implementação é uma SIMULAÇÃO (MOCK) de banco de dados usando uma lista estática.
 * Esta classe é utilizada pelo NotificationService para buscar e atualizar o estado das notificações.
 */
public class NotificationDAO {

    // Simulação do banco de dados: Lista estática de notificações
    private static final List<Notification> MOCK_DATABASE = new ArrayList<>();
    private static int nextId = 1;

    // Bloco estático para popular o "banco" com dados iniciais de teste
    static {
        // CORREÇÃO FINAL: Garantindo a ordem do construtor:
        // ID, TÍTULO, MENSAGEM, TIPO, LIDA(boolean), DATA_CRIACAO(LocalDateTime)

        // 1. Notificação crítica (não lida)
        MOCK_DATABASE.add(new Notification(nextId++,
                "Erro de Estoque",
                "O estoque do Produto X atingiu o limite crítico (3 unidades).",
                NotificationType.CRITICO,
                false, // Lida
                LocalDateTime.now().minusMinutes(10)));

        // 2. Notificação de alerta (não lida)
        MOCK_DATABASE.add(new Notification(nextId++,
                "Baixa de Estoque",
                "O Produto Y está com menos de 10 unidades. Necessário reabastecer.",
                NotificationType.ALERTA,
                false, // Lida
                LocalDateTime.now().minusHours(1)));

        // 3. Notificação de informação (lida)
        MOCK_DATABASE.add(new Notification(nextId++,
                "Novo Usuário",
                "Novo usuário 'João Silva' cadastrado no sistema.",
                NotificationType.INFO,
                true, // Lida
                LocalDateTime.now().minusDays(2)));
    }

    /**
     * Lista todas as notificações que ainda não foram marcadas como lidas (lida = false).
     * @return Lista de notificações não lidas.
     */
    public List<Notification> listarNaoLidas() {
        // Simula a busca e filtragem no banco de dados
        return MOCK_DATABASE.stream()
                .filter(n -> !n.isLida())
                .collect(Collectors.toList());
    }

    /**
     * Marca uma notificação específica como lida no "banco de dados".
     * @param id O ID da notificação a ser marcada.
     * @return true se a notificação foi encontrada e marcada, false caso contrário.
     */
    public boolean marcarComoLida(int id) {
        for (Notification notification : MOCK_DATABASE) {
            if (notification.getId() == id) {
                // Simula a atualização do campo 'lida' no Model
                notification.setLida(true);
                return true;
            }
        }
        return false;
    }

    /**
     * Salva uma nova notificação no "banco de dados".
     * @param notification A notificação a ser salva.
     * @return true se salvo com sucesso.
     */
    public boolean salvar(Notification notification) {
        // Atribui um novo ID (se for nova) e adiciona à lista
        if (notification.getId() == 0) {
            notification.setId(nextId++);
        }
        MOCK_DATABASE.add(notification);
        return true;
    }
}
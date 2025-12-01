package service;

import Model.Notification; // CORRIGIDO: Garante que o import para a classe Model.Notification esteja presente
import java.time.LocalDateTime;
import Model.NotificationType;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Serviço responsável por gerenciar e fornecer notificações.
 * Contém os métodos exigidos pela View para compilar.
 */
public class NotificationService {

    // Lista simulada para armazenar notificações.
    private final List<Notification> notificationDatabase; // CORRIGIDO: A classe Notification é usada aqui

    public NotificationService() {
        this.notificationDatabase = new ArrayList<>();
        // Adicionar dados mock para garantir que a listagem não esteja vazia
        // Usando os tipos CRITICO, ALERTA e INFO que você forneceu:
        this.notificationDatabase.add(new Notification(1, "Estoque Crítico", "O produto 'Camiseta P' atingiu o nível de estoque mínimo.", NotificationType.CRITICO, false, LocalDateTime.now().minusHours(5))); // CORRIGIDO: A classe Notification é usada aqui
        this.notificationDatabase.add(new Notification(2, "Nova Devolução", "Uma solicitação de devolução para o Pedido #1002 está pendente.", NotificationType.ALERTA, false, LocalDateTime.now().minusHours(2))); // CORRIGIDO: A classe Notification é usada aqui
        this.notificationDatabase.add(new Notification(3, "Venda Concluída", "A venda do Pedido #1001 foi processada com sucesso.", NotificationType.INFO, true, LocalDateTime.now().minusDays(1))); // CORRIGIDO: A classe Notification é usada aqui
    }

    /**
     * Retorna todas as notificações.
     * @return Lista de todas as notificações.
     */
    public List<Notification> getAllNotifications() {
        return notificationDatabase;
    }

    /**
     * Marca todas as notificações como lidas.
     */
    public void marcarTodasComoLidas() {
        notificationDatabase.forEach(n -> n.setLida(true));
    }

    // Outros métodos que seriam necessários
    public Optional<Notification> buscarPorId(int id) { // CORRIGIDO: A classe Notification é usada aqui
        return notificationDatabase.stream()
                .filter(n -> n.getId() == id)
                .findFirst();
    }
}
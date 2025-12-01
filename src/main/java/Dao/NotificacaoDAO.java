package Dao;

import Model.Notification;
import Model.NotificationType;
import Model.Venda;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class NotificacaoDAO {

    // Lista mockada simulando banco de dados
    private static final ObservableList<Notification> notificacoes =
            FXCollections.observableArrayList(
                    new Notification(
                            "Nova venda registrada!",
                            "A venda #1021 foi concluída.",
                            NotificationType.INFO
                    ),
                    new Notification(
                            "Estoque baixo!",
                            "O produto Notebook Acer está quase esgotado.",
                            NotificationType.ALERTA
                    ),
                    new Notification(
                            "Nova atualização disponível",
                            "O sistema recebeu melhorias.",
                            NotificationType.INFO
                    ),
                    new Notification(
                            "Funcionário cadastrado",
                            "Novo funcionário adicionado ao sistema.",
                            NotificationType.INFO
                    )
            );

    /**
     * Retorna todas as notificações mockadas.
     */
    public static ObservableList<Notification> buscarTodasNotificacoes() {
        return notificacoes;
    }

    /**
     * Marca todas como lidas.
     */
    public static void marcarTodasComoLidas() {
        for (Notification n : notificacoes) {
            n.setLida(true);
        }
    }

    /**
     * Adiciona nova notificação.
     */
    public static void adicionarNotificacao(Notification n) {
        notificacoes.add(n);
    }

    /**
     * Cria uma notificação automática ao registrar uma venda.
     */
    public static void notificarVenda(Venda venda) {

        String titulo = "Venda registrada!";
        String mensagem = "Venda #" + venda.getId() +
                " concluída. Total: R$ " + venda.getTotalVenda();

        Notification n = new Notification(
                titulo,
                mensagem,
                NotificationType.INFO
        );

        notificacoes.add(n);
    }
}

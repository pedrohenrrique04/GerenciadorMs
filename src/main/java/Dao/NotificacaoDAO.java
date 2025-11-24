package Dao;
import Model.CartItem;


import Model.Notification;
import Model.NotificationType;
import Model.Venda;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.time.LocalDateTime;

public class NotificacaoDAO {

    private static final ObservableList<Notification> listaNotificacoes = FXCollections.observableArrayList();

    // Salvar notificação mock
    public static void salvarNotificacao(String message, NotificationType type) {
        Notification n = new Notification(
                listaNotificacoes.size() + 1,
                type.getDisplay(),
                message,
                type,
                LocalDateTime.now(),
                false
        );
        listaNotificacoes.add(n);

        System.out.println("🔔 Nova Notificação gerada:");
        System.out.println("   Tipo: " + type.getDisplay());
        System.out.println("   Mensagem: " + message);
    }

    public static void notificarVenda(Venda venda) {
        if (venda == null || venda.getItens() == null || venda.getItens().isEmpty()) return;

        StringBuilder itens = new StringBuilder();
        for (CartItem item : venda.getItens()) {
            itens.append(item.getNome()).append(" x").append(item.getQuantidade()).append(", ");
        }
        if (itens.length() > 2) itens.setLength(itens.length() - 2);

        String mensagem = "Venda ID #" + venda.getId() + " salva no BD. Itens: " + itens;
        salvarNotificacao(mensagem, NotificationType.INFO);
    }

    public static ObservableList<Notification> buscarTodasNotificacoes() {
        return listaNotificacoes;
    }
}

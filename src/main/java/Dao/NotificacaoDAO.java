package Dao;

import Model.Notification;
import Model.NotificationType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import util.Conexao; // Assumindo que sua classe de conexão é esta

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * DAO para gerenciar as Notificações no banco de dados.
 */
public class NotificacaoDAO {

    /**
     * Salva uma nova notificação no banco de dados.
     * @param mensagem O texto da notificação.
     * @param type O tipo (INFO ou ALERTA).
     */
    public static void salvarNotificacao(String mensagem, NotificationType type) {
        // SQL para inserir na tabela (AJUSTE O NOME DA TABELA/COLUNAS SE NECESSÁRIO)
        String sql = "INSERT INTO notificacoes (mensagem, timestamp, tipo, lida) VALUES (?, ?, ?, ?)";

        try (Connection conn = Conexao.getConn(); // Pega a conexão do seu 'util'
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, mensagem);
            stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now())); // Pega a hora atual
            stmt.setString(3, type.name()); // Salva "INFO" ou "ALERTA"
            stmt.setBoolean(4, false); // Novas notificações nunca são lidas

            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Erro ao salvar notificação no banco:");
            e.printStackTrace();
            // Seria bom mostrar um alerta de erro aqui
        }
    }

    /**
     * Busca todas as notificações do banco de dados.
     * @return Uma ObservableList de Notificações.
     */
    public static ObservableList<Notification> buscarTodasNotificacoes() {
        ObservableList<Notification> notificacoes = FXCollections.observableArrayList();
        // SQL para buscar (AJUSTE OS NOMES SE NECESSÁRIO)
        String sql = "SELECT mensagem, timestamp, tipo, lida FROM notificacoes ORDER BY timestamp DESC";

        try (Connection conn = Conexao.getConn();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String mensagem = rs.getString("mensagem");
                LocalDateTime timestamp = rs.getTimestamp("timestamp").toLocalDateTime();
                NotificationType type = NotificationType.valueOf(rs.getString("tipo"));
                boolean lida = rs.getBoolean("lida");

                notificacoes.add(new Notification(mensagem, timestamp, type, lida));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar notificações do banco:");
            e.printStackTrace();
        }

        return notificacoes;
    }

    // NOTA: Você também precisará de um método para ATUALIZAR o status 'lida'
    // quando o usuário marcar o CheckBox. Por simplicidade,
    // o código atual só atualiza na memória.
}
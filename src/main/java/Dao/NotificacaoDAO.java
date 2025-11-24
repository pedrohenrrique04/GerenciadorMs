package Dao;

import Model.Notification;
import Model.NotificationType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.time.LocalDateTime;

// --- Imports de JDBC (Adicione estes quando for implementar o banco) ---
/*
import util.Conexao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
*/

/**
 * Data Access Object para notificações.
 * Contém dados MOCK (fictícios) para permitir o funcionamento da UI.
 */
public class NotificacaoDAO {

    // QUERY DE INSERÇÃO (PARA USO FUTURO COM JDBC)
    /*
    private static final String INSERT_NOTIFICACAO =
        "INSERT INTO notificacoes (titulo, mensagem, tipo, is_read, timestamp) " +
        "VALUES (?, ?, ?, FALSE, NOW())";
    */


    /**
     * Adiciona uma nova notificação.
     * Este método está atualmente implementado como MOCK.
     * @param message A mensagem da notificação.
     * @param type O tipo da notificação (INFO, ALERTA, CRITICO).
     */
    public static void salvarNotificacao(String message, NotificationType type) {
        // --- 1. LÓGICA MOCK (PARA EVITAR ERRO DE COMPILAÇÃO AGORA) ---
        // Aqui, apenas imprimimos para o console que a notificação seria salva.
        System.out.println("🔔 [MOCK SALVAR] Nova Notificação gerada:");
        System.out.println("   Tipo: " + type.getDisplay());
        System.out.println("   Mensagem: " + message);
        System.out.println("   Status: Não Salva no DB (MOCK)");


        // --- 2. LÓGICA DE JDBC (DESCOMENTE E IMPLEMENTE MAIS TARDE) ---
        /*
        Connection conn = null;
        try {
            conn = Conexao.getConn();
            try (PreparedStatement stmt = conn.prepareStatement(INSERT_NOTIFICACAO)) {

                // Define o TÍTULO (Simplificação: pegamos o display name do enum)
                stmt.setString(1, type.getDisplay());

                // Define a MENSAGEM
                stmt.setString(2, message);

                // Define o TIPO (Salva o nome do enum no banco)
                stmt.setString(3, type.name());

                stmt.executeUpdate();
                System.out.println("✅ Notificação salva no banco de dados.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Erro ao salvar notificação no banco: " + e.getMessage());
        } finally {
            if (conn != null) {
                try { conn.close(); } catch (SQLException e) { }
            }
        }
        */
    }


    /**
     * Busca todas as notificações do banco de dados (atualmente, retorna dados MOCK).
     */
    public static ObservableList<Notification> buscarTodasNotificacoes() {
        ObservableList<Notification> mockList = FXCollections.observableArrayList();

        // Dados Mock... (Mantidos os mesmos do arquivo anterior)

        mockList.add(new Notification(
                1,
                "Estoque Baixo - Camiseta Padrão",
                "Apenas 5 unidades restantes da Camiseta Padrão, cor Azul.",
                NotificationType.ALERTA,
                LocalDateTime.now().minusHours(1),
                false
        ));

        mockList.add(new Notification(
                2,
                "Venda Confirmada (R$ 450.00)",
                "Uma nova venda foi registrada no sistema. Cliente: João.",
                NotificationType.INFO,
                LocalDateTime.now().minusMinutes(30),
                false
        ));

        mockList.add(new Notification(
                3,
                "Produto Crítico - Tênis de Corrida",
                "Estoque zerado para Tênis de Corrida, cor Vermelha.",
                NotificationType.CRITICO,
                LocalDateTime.now().minusHours(24),
                true
        ));

        mockList.add(new Notification(
                4,
                "Nova Promoção Ativa",
                "A promoção de Inverno foi ativada com sucesso.",
                NotificationType.INFO,
                LocalDateTime.now().minusDays(2),
                true
        ));

        return mockList;
    }
}
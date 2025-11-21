package Test;

import util.Conexao;
import java.sql.Connection;
import java.sql.SQLException;

public class TesteConexao {
    public static void main(String[] args) {
        System.out.println("🔍 Testando conexão com o banco...");

        Connection conn = Conexao.getConn();

        if (conn != null) {
            try {
                if (!conn.isClosed()) {
                    System.out.println("✅ Conexão bem-sucedida!");
                } else {
                    System.out.println("⚠️ Conexão foi aberta mas está fechada.");
                }
            } catch (SQLException e) {
                System.out.println("❌ Erro ao verificar conexão: " + e.getMessage());
            }
        } else {
            System.out.println("❌ Falha: Conexão retornou null.");
        }
    }
}

package Dao;

import util.Conexao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RelatoriosProdutosDao {

    // RETORNA O SOMATÓRIO DO ESTOQUE DE TODOS OS PRODUTOS
    public int getEstoqueTotal() {

        String sql = "SELECT COALESCE(SUM(estoque), 0) AS estoque_total FROM produtos";

        try (Connection conn = Conexao.getConn();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("estoque_total");
            }

        } catch (SQLException e) {
            System.out.println("Erro ao buscar estoque total: " + e.getMessage());
        }

        return 0;
    }
}
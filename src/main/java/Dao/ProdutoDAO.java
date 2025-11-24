package Dao;

import Model.Produto;
import util.Conexao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;

/**
 * Data Access Object para Produto.
 * Implementa o método listarTodos() esperado pela TelaRealizarVenda.
 */
public class ProdutoDAO {

    private final String SELECT_ALL = "SELECT id, nome, preco FROM produtos";

    /**
     * Busca todos os produtos ativos no banco de dados.
     * CORRIGIDO: O método esperado pela tela é listarTodos().
     * @return Uma lista de objetos Produto.
     */
    public List<Produto> listarTodos() {
        List<Produto> produtos = new ArrayList<>();
        Connection conn = null;

        try {
            conn = Conexao.getConn();
            if (conn == null) {
                System.err.println("❌ Falha ao obter conexão, lista de produtos vazia.");
                return produtos;
            }

            try (PreparedStatement stmt = conn.prepareStatement(SELECT_ALL);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    // Usa o construtor da sua classe Model.Produto (int, String, double)
                    Produto p = new Produto(
                            rs.getInt("id"),
                            rs.getString("nome"),
                            rs.getDouble("preco")
                    );
                    produtos.add(p);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erro SQL ao buscar produtos: " + e.getMessage());
        } finally {
            if (conn != null) {
                try { conn.close(); } catch (SQLException e) { /* Ignora */ }
            }
        }
        return produtos;
    }
}
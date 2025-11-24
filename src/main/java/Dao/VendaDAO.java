package Dao;

import Model.Venda;
import Model.CartItem;
import util.Conexao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

/**
 * DAO para vendas, integrando geração de notificação.
 */
public class VendaDAO {

    private final String INSERT_VENDA_ITEM =
            "INSERT INTO vendas (produto_id, produto_nome, quantidade, preco_unitario, desconto, forma_pagamento, valor_total) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";

    /**
     * Salva a venda e gera notificação.
     */
    public boolean salvarVenda(Venda venda) {
        Connection conn = null;
        try {
            conn = Conexao.getConn();
            if (conn == null) {
                System.err.println("❌ Falha ao obter conexão, venda não registrada.");
                return false;
            }

            conn.setAutoCommit(false);

            try (PreparedStatement stmtItem = conn.prepareStatement(INSERT_VENDA_ITEM, Statement.RETURN_GENERATED_KEYS)) {

                if (venda.getItens() == null || venda.getItens().isEmpty()) {
                    System.err.println("⚠️ Venda sem itens. Transação desfeita.");
                    conn.rollback();
                    return false;
                }

                for (CartItem item : venda.getItens()) {
                    stmtItem.setInt(1, item.getId());                  // produto_id
                    stmtItem.setString(2, item.getNome());            // produto_nome
                    stmtItem.setInt(3, item.getQuantidade());         // quantidade
                    stmtItem.setDouble(4, item.getPreco());           // preco_unitario
                    stmtItem.setDouble(5, 0.00);                      // desconto
                    stmtItem.setString(6, venda.getFormaPagamento()); // forma_pagamento
                    stmtItem.setDouble(7, item.getTotal());           // valor_total

                    stmtItem.addBatch();
                }

                stmtItem.executeBatch();

                // Pegando o ID da venda (considerando que só há 1 venda no momento)
                ResultSet rs = stmtItem.getGeneratedKeys();
                if (rs.next()) {
                    venda.setId(rs.getInt(1));
                }
            }

            conn.commit();
            System.out.println("✅ Venda registrada com sucesso! " + venda.getItens().size() + " itens salvos.");

            // Gerar notificação
            NotificacaoDAO.notificarVenda(venda);

            return true;

        } catch (Exception e) {
            System.err.println("❌ Erro transacional ao salvar a Venda. Detalhe: " + e.getMessage());
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {}
            return false;
        } finally {
            if (conn != null) {
                try { conn.close(); } catch (SQLException e) {}
            }
        }
    }
}

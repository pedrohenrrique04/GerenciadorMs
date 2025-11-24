package Dao;

import Model.Venda;
import Model.CartItem;
import util.Conexao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Data Access Object para a entidade Venda.
 * Adaptado à estrutura de tabela onde o ID do produto é inserido na coluna 'produto'.
 */
public class VendaDAO {

    // CORRIGIDO: O nome da coluna no INSERT foi alterado de 'produto_id' para 'produto'
    private final String INSERT_VENDA_ITEM =
            "INSERT INTO vendas (produto, quantidade, preco_unitario, desconto, forma_pagamento, valor_total) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

    /**
     * Salva a venda e todos os seus itens como linhas separadas na tabela 'vendas'.
     * @param venda O objeto Venda contendo os itens.
     * @return true se a transação for bem-sucedida.
     */
    public boolean salvarVenda(Venda venda) {
        Connection conn = null;
        try {
            conn = Conexao.getConn();
            if (conn == null) {
                System.err.println("❌ Falha ao obter conexão, venda não registrada.");
                return false;
            }

            conn.setAutoCommit(false); // 1. Inicia a transação

            // 2. Insere os Itens da Venda em lote (batch)
            try (PreparedStatement stmtItem = conn.prepareStatement(INSERT_VENDA_ITEM)) {

                if (venda.getItens() == null || venda.getItens().isEmpty()) {
                    System.err.println("⚠️ Venda sem itens. Transação desfeita.");
                    conn.rollback();
                    return false;
                }

                for (CartItem item : venda.getItens()) {
                    // Mapeamento dos 6 parâmetros (na ordem do INSERT):

                    // 1. produto (Inserindo o ID do produto na coluna 'produto' do DB)
                    stmtItem.setInt(1, item.getId());

                    // 2. quantidade
                    stmtItem.setInt(2, item.getQuantidade());

                    // 3. preco_unitario
                    stmtItem.setDouble(3, item.getPreco());

                    // 4. desconto
                    stmtItem.setDouble(4, 0.00);

                    // 5. forma_pagamento
                    stmtItem.setString(5, venda.getFormaPagamento());

                    // 6. valor_total
                    stmtItem.setDouble(6, item.getTotal());

                    stmtItem.addBatch();
                }
                stmtItem.executeBatch();
            }

            // 3. Confirma a transação
            conn.commit();
            System.out.println("✅ Venda registrada com sucesso! " + venda.getItens().size() + " itens salvos.");
            return true;

        } catch (Exception e) {
            System.err.println("❌ Erro transacional ao salvar a Venda. Detalhe: " + e.getMessage());
            try {
                // 4. Em caso de erro, desfaz a transação
                if (conn != null) {
                    conn.rollback();
                    System.err.println("🔙 Transação desfeita (rollback).");
                }
            } catch (SQLException ex) {
                // Ignora erro no rollback
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    // Ignora erro ao fechar
                }
            }
        }
    }
}
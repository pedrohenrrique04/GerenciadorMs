package Dao;

import Model.Venda;
import java.util.Optional;

/**
 * DAO mockado (Data Access Object) para a entidade Venda.
 * Nesta versão, apenas simula o registro de vendas, sem conexão real com banco de dados.
 */
public class VendaDAO {

    // Simulação de ID sequencial para novas vendas
    private static int nextId = 1000;

    /**
     * Simula o registro de uma nova venda no banco de dados.
     * Atribui um ID sequencial à venda.
     * @param venda O objeto Venda a ser registrado.
     * @return true se o registro foi bem-sucedido (sempre true no mock).
     */
<<<<<<< HEAD
    public boolean registrarVenda(Venda venda) {
        // Simula a inserção e atribuição de ID
        venda.setId(nextId++);
        System.out.println("✅ Venda registrada no DB (MOCK) com sucesso! ID: " + venda.getId());
        // Em um DAO real, aqui ocorreria a lógica JDBC (INSERT INTO VENDAS...)
        return true;
=======
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
>>>>>>> parent of ba0e6b0 (telaprodutos&telarealizarvendacomBANCO)
    }

    /**
     * Simula a busca de uma Venda pelo ID.
     * @param id O ID da venda a ser buscada.
     * @return Um Optional vazio (Mock).
     */
    public Optional<Venda> buscarPorId(int id) {
        // Mock: Implemente a busca real aqui, se necessário.
        return Optional.empty();
    }
}
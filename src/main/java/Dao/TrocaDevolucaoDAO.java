package Dao;

import Model.TrocaDevolucao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object (DAO) para a entidade TrocaDevolucao.
 * Implementa métodos para persistência de dados (CRUD).
 */
public class TrocaDevolucaoDAO {

    /**
     * Salva ou atualiza uma solicitação de troca/devolução.
     * @param td A solicitação a ser salva.
     * @return A solicitação com o ID atualizado, se for uma nova inserção.
     */
    public TrocaDevolucao salvar(TrocaDevolucao td) { // MÉTODO SALVAR ADICIONADO/CORRIGIDO
        String sql;
        Connection conn = null;
        try {
            conn = Conexao.conectar();

            if (td.getId() == 0) {
                // Inserção
                sql = "INSERT INTO trocaDevolucao (idProduto, numeroPedido, tipo, motivo, dataSolicitacao, status, quantidade, precoVenda, observacoes) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    preencherStatement(stmt, td, false);
                    stmt.executeUpdate();

                    try (ResultSet rs = stmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            td.setId(rs.getInt(1));
                        }
                    }
                }
            } else {
                // Atualização
                sql = "UPDATE trocaDevolucao SET idProduto = ?, numeroPedido = ?, tipo = ?, motivo = ?, dataSolicitacao = ?, dataProcessamento = ?, status = ?, quantidade = ?, precoVenda = ?, observacoes = ? WHERE id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    preencherStatement(stmt, td, true);
                    stmt.setInt(11, td.getId());
                    stmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao salvar troca/devolução: " + e.getMessage());
        } finally {
            Conexao.desconectar(conn);
        }
        return td;
    }

    /**
     * Método auxiliar para preencher os parâmetros do PreparedStatement.
     */
    private void preencherStatement(PreparedStatement stmt, TrocaDevolucao td, boolean incluirDataProcessamento) throws SQLException {
        int index = 1;
        stmt.setInt(index++, td.getIdProduto());
        stmt.setString(index++, td.getNumeroPedido());
        stmt.setString(index++, td.getTipo());
        stmt.setString(index++, td.getMotivo());
        stmt.setDate(index++, java.sql.Date.valueOf(td.getDataSolicitacao()));

        if (incluirDataProcessamento) {
            if (td.getDataProcessamento() != null) {
                stmt.setTimestamp(index++, java.sql.Timestamp.valueOf(td.getDataProcessamento()));
            } else {
                stmt.setNull(index++, java.sql.Types.TIMESTAMP);
            }
        }

        stmt.setString(index++, td.getStatus());
        stmt.setInt(index++, td.getQuantidade());
        stmt.setDouble(index++, td.getPrecoVenda());
        stmt.setString(index++, td.getObservacoes());
    }

    /**
     * Busca uma solicitação pelo ID.
     * @param id O ID da solicitação.
     * @return Um Optional contendo a solicitação, se encontrada.
     */
    public Optional<TrocaDevolucao> buscarPorId(int id) {
        String sql = "SELECT * FROM trocaDevolucao WHERE id = ?";
        Connection conn = null;
        try {
            conn = Conexao.conectar();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return Optional.of(criarTrocaDevolucao(rs));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar troca/devolução por ID: " + e.getMessage());
        } finally {
            Conexao.desconectar(conn);
        }
        return Optional.empty();
    }

    /**
     * Deleta uma solicitação pelo ID.
     * @param id O ID da solicitação a ser deletada.
     * @return true se a operação foi bem-sucedida, false caso contrário.
     */
    public boolean deletar(int id) { // MÉTODO DELETAR ADICIONADO/CORRIGIDO
        String sql = "DELETE FROM trocaDevolucao WHERE id = ?";
        Connection conn = null;
        try {
            conn = Conexao.conectar();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, id);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Erro ao deletar troca/devolução: " + e.getMessage());
            return false;
        } finally {
            Conexao.desconectar(conn);
        }
    }

    /**
     * Lista todas as solicitações.
     * @return Uma lista de todas as solicitações.
     */
    public List<TrocaDevolucao> listarTodos() {
        List<TrocaDevolucao> tds = new ArrayList<>();
        String sql = "SELECT * FROM trocaDevolucao ORDER BY dataSolicitacao DESC";
        Connection conn = null;
        try {
            conn = Conexao.conectar();
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    tds.add(criarTrocaDevolucao(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar trocas/devoluções: " + e.getMessage());
        } finally {
            Conexao.desconectar(conn);
        }
        return tds;
    }

    /**
     * Cria um objeto TrocaDevolucao a partir de um ResultSet.
     */
    private TrocaDevolucao criarTrocaDevolucao(ResultSet rs) throws SQLException {
        LocalDateTime dataProcessamento = rs.getTimestamp("dataProcessamento") != null
                ? rs.getTimestamp("dataProcessamento").toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
                : null;

        return new TrocaDevolucao(
                rs.getInt("id"),
                rs.getInt("idProduto"),
                rs.getString("numeroPedido"),
                rs.getString("tipo"),
                rs.getString("motivo"),
                rs.getDate("dataSolicitacao").toLocalDate(),
                dataProcessamento,
                rs.getString("status"),
                rs.getInt("quantidade"),
                rs.getDouble("precoVenda"),
                rs.getString("observacoes")
        );
    }
}
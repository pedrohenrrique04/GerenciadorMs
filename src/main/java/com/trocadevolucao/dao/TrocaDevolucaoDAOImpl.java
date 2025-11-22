package com.trocadevolucao.dao;

import com.trocadevolucao.model.TrocaDevolucao;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementação concreta da interface TrocaDevolucaoDAO,
 * responsável por toda a comunicação com o banco de dados MySQL.
 */
public class TrocaDevolucaoDAOImpl implements TrocaDevolucaoDAO {

    private static final String INSERT_SQL = "INSERT INTO trocas_devolucoes (produto_id, num_pedido, tipo_solicitacao, motivo, data_solicitacao, status, quantidade, valor_total, observacoes) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SELECT_ALL_SQL = "SELECT id, produto_id, num_pedido, tipo_solicitacao, motivo, data_solicitacao, status, quantidade, valor_total, observacoes, data_processamento FROM trocas_devolucoes";
    private static final String UPDATE_SQL = "UPDATE trocas_devolucoes SET produto_id=?, num_pedido=?, tipo_solicitacao=?, motivo=?, status=?, quantidade=?, valor_total=?, observacoes=?, data_processamento=? WHERE id=?";
    private static final String DELETE_SQL = "DELETE FROM trocas_devolucoes WHERE id=?";
    private static final String SELECT_BY_ID_SQL = "SELECT id, produto_id, num_pedido, tipo_solicitacao, motivo, data_solicitacao, status, quantidade, valor_total, observacoes, data_processamento FROM trocas_devolucoes WHERE id=?";


    // ----------------------------------------------------
    // IMPLEMENTAÇÃO DOS MÉTODOS DE CRUD (Resolvendo os erros)
    // ----------------------------------------------------

    @Override
    public boolean salvar(TrocaDevolucao solicitacao) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_SQL)) {

            stmt.setInt(1, solicitacao.getProdutoId());
            stmt.setString(2, solicitacao.getNumeroPedido());
            stmt.setString(3, solicitacao.getTipoSolicitacao());
            stmt.setString(4, solicitacao.getMotivo());
            stmt.setDate(5, Date.valueOf(solicitacao.getDataSolicitacao()));
            stmt.setString(6, solicitacao.getStatus());
            stmt.setInt(7, solicitacao.getQuantidade());
            stmt.setDouble(8, solicitacao.getValorTotal());
            stmt.setString(9, solicitacao.getObservacoes());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao salvar solicitação: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<TrocaDevolucao> carregarTodos() {
        List<TrocaDevolucao> lista = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL_SQL)) {

            while (rs.next()) {
                lista.add(extrairTrocaDevolucao(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao carregar solicitações: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public boolean atualizar(TrocaDevolucao solicitacao) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_SQL)) {

            stmt.setInt(1, solicitacao.getProdutoId());
            stmt.setString(2, solicitacao.getNumeroPedido());
            stmt.setString(3, solicitacao.getTipoSolicitacao());
            stmt.setString(4, solicitacao.getMotivo());
            stmt.setString(5, solicitacao.getStatus());
            stmt.setInt(6, solicitacao.getQuantidade());
            stmt.setDouble(7, solicitacao.getValorTotal());
            stmt.setString(8, solicitacao.getObservacoes());

            // Lidar com data_processamento nula
            LocalDate dataProcessamento = solicitacao.getDataProcessamento();
            if (dataProcessamento != null) {
                stmt.setDate(9, Date.valueOf(dataProcessamento));
            } else {
                stmt.setNull(9, java.sql.Types.DATE);
            }

            // A condição WHERE usa o ID (último parâmetro)
            stmt.setInt(10, solicitacao.getId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao atualizar solicitação ID " + solicitacao.getId() + ": " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean excluir(int id) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_SQL)) {

            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao excluir solicitação ID " + id + ": " + e.getMessage());
            return false;
        }
    }

    @Override
    public TrocaDevolucao buscarPorId(int id) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ID_SQL)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extrairTrocaDevolucao(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar solicitação ID " + id + ": " + e.getMessage());
        }
        return null;
    }

    // ----------------------------------------------------
    // MÉTODO AUXILIAR
    // ----------------------------------------------------

    private TrocaDevolucao extrairTrocaDevolucao(ResultSet rs) throws SQLException {
        TrocaDevolucao td = new TrocaDevolucao(
                rs.getInt("produto_id"),
                rs.getString("num_pedido"),
                rs.getString("tipo_solicitacao"),
                rs.getString("motivo"),
                rs.getDate("data_solicitacao").toLocalDate(),
                rs.getString("status"),
                rs.getInt("quantidade"),
                rs.getDouble("valor_total")
        );
        td.setId(rs.getInt("id"));
        td.setObservacoes(rs.getString("observacoes"));

        Date dataProcessamentoSql = rs.getDate("data_processamento");
        if (dataProcessamentoSql != null) {
            td.setDataProcessamento(dataProcessamentoSql.toLocalDate());
        }
        return td;
    }
}
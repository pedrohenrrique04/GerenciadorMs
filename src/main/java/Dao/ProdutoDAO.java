package Dao;

import Model.ProdutoModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object (DAO) para a entidade ProdutoModel.
 * Lida com as operações de persistência de dados no banco de dados.
 */
public class ProdutoDAO {

    /**
     * Salva um produto no banco de dados. Se o ID for 0, insere; caso contrário, atualiza.
     * @param produto O produto a ser salvo.
     * @return O produto, com o ID preenchido se for uma nova inserção.
     */
    public ProdutoModel salvar(ProdutoModel produto) {
        String sql;
        Connection conn = null;
        try {
            conn = Conexao.conectar();

            if (produto.getId() == 0) {
                // Inserção
                sql = "INSERT INTO produto (nome, quantidade, precoCusto, precoVenda, dataEntrada, categoria, genero, cor) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    preencherStatement(stmt, produto);
                    stmt.executeUpdate();

                    try (ResultSet rs = stmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            produto.setId(rs.getInt(1));
                        }
                    }
                }
            } else {
                // Atualização
                sql = "UPDATE produto SET nome = ?, quantidade = ?, precoCusto = ?, precoVenda = ?, dataEntrada = ?, dataReposicao = ?, categoria = ?, genero = ?, cor = ? WHERE id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    preencherStatement(stmt, produto);
                    // Adiciona dataReposicao (coluna extra na atualização)
                    if (produto.getDataReposicao() != null) {
                        stmt.setDate(6, java.sql.Date.valueOf(produto.getDataReposicao()));
                    } else {
                        stmt.setNull(6, java.sql.Types.DATE);
                    }
                    stmt.setInt(10, produto.getId());
                    stmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao salvar produto: " + e.getMessage());
        } finally {
            Conexao.desconectar(conn);
        }
        return produto;
    }

    /**
     * Método auxiliar para preencher os parâmetros do PreparedStatement.
     */
    private void preencherStatement(PreparedStatement stmt, ProdutoModel produto) throws SQLException {
        stmt.setString(1, produto.getNome());
        stmt.setInt(2, produto.getQuantidade());
        stmt.setDouble(3, produto.getPrecoCusto());
        stmt.setDouble(4, produto.getPrecoVenda());
        stmt.setDate(5, java.sql.Date.valueOf(produto.getDataEntrada()));
        stmt.setString(6, produto.getCategoria());
        stmt.setString(7, produto.getGenero());
        stmt.setString(8, produto.getCor());
    }

    /**
     * Busca um produto pelo ID.
     * @param id O ID do produto.
     * @return Um Optional contendo o produto, se encontrado.
     */
    public Optional<ProdutoModel> buscarPorId(int id) {
        String sql = "SELECT * FROM produto WHERE id = ?";
        Connection conn = null;
        try {
            conn = Conexao.conectar();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return Optional.of(criarProduto(rs));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar produto por ID: " + e.getMessage());
        } finally {
            Conexao.desconectar(conn);
        }
        return Optional.empty();
    }

    /**
     * Deleta um produto pelo ID.
     * @param id O ID do produto a ser deletado.
     * @return true se a operação foi bem-sucedida, false caso contrário.
     */
    public boolean deletar(int id) {
        String sql = "DELETE FROM produto WHERE id = ?";
        Connection conn = null;
        try {
            conn = Conexao.conectar();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, id);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Erro ao deletar produto: " + e.getMessage());
            return false;
        } finally {
            Conexao.desconectar(conn);
        }
    }

    /**
     * Lista todos os produtos.
     * @return Uma lista de todos os produtos.
     */
    public List<ProdutoModel> listarTodos() {
        List<ProdutoModel> produtos = new ArrayList<>();
        String sql = "SELECT * FROM produto";
        Connection conn = null;
        try {
            conn = Conexao.conectar();
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    produtos.add(criarProduto(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar produtos: " + e.getMessage());
        } finally {
            Conexao.desconectar(conn);
        }
        return produtos;
    }

    /**
     * Cria um objeto ProdutoModel a partir de um ResultSet.
     */
    private ProdutoModel criarProduto(ResultSet rs) throws SQLException {
        return new ProdutoModel(
                rs.getInt("id"),
                rs.getString("nome"),
                rs.getInt("quantidade"),
                rs.getDouble("precoCusto"),
                rs.getDouble("precoVenda"),
                rs.getDate("dataEntrada").toLocalDate(),
                rs.getDate("dataReposicao") != null ? rs.getDate("dataReposicao").toLocalDate() : null,
                rs.getString("categoria"),
                rs.getString("genero"),
                rs.getString("cor")
        );
    }
}
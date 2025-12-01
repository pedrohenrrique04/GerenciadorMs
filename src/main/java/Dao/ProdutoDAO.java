package Dao;

import Model.ProdutoModel;
import util.Conexao;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * ProdutoDAO para MySQL usando try-with-resources e tratamento de SQLException.
 */
public class ProdutoDAO {

    // 🚨 CORREÇÃO NO INSERT: Adicionado 'preco' (12 placeholders)
    private static final String INSERT_PRODUTO =
            "INSERT INTO produtos (nome, quantidade, preco_custo, preco_venda, preco, data_entrada, data_reposicao, categoria, genero, cor, descricao, imagem_path) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String SELECT_ALL_PRODUTOS =
            "SELECT id, nome, quantidade, preco_custo, preco_venda, data_entrada, data_reposicao, categoria, genero, cor, descricao, imagem_path FROM produtos";

    private static final String SELECT_PRODUTO_BY_ID =
            "SELECT id, nome, quantidade, preco_custo, preco_venda, data_entrada, data_reposicao, categoria, genero, cor, descricao, imagem_path FROM produtos WHERE id = ?";

    // 🚨 CORREÇÃO NO UPDATE: Adicionado 'preco' (12 campos para atualizar)
    private static final String UPDATE_PRODUTO =
            "UPDATE produtos SET nome = ?, quantidade = ?, preco_custo = ?, preco_venda = ?, preco = ?, data_entrada = ?, data_reposicao = ?, categoria = ?, genero = ?, cor = ?, descricao = ?, imagem_path = ? WHERE id = ?";
    // Total de 13 placeholders: 12 para SET + 1 para WHERE

    private static final String UPDATE_ESTOQUE =
            "UPDATE produtos SET quantidade = ? WHERE id = ?";

    private static final String DELETE_PRODUTO =
            "DELETE FROM produtos WHERE id = ?";

    // --- MÉTODOS AUXILIARES ---
    private ProdutoModel mapResultSetToProduto(ResultSet rs) throws SQLException {
        // Assume que ProdutoModel tem o construtor correto de 10 argumentos
        ProdutoModel produto = new ProdutoModel(
                rs.getInt("id"),
                rs.getString("nome"),
                rs.getInt("quantidade"),
                rs.getDouble("preco_custo"),
                rs.getDouble("preco_venda"),
                null,
                null,
                rs.getString("categoria"),
                rs.getString("genero"),
                rs.getString("cor")
        );

        Date sqlDataEntrada = rs.getDate("data_entrada");
        produto.setDataEntrada(sqlDataEntrada != null ? sqlDataEntrada.toLocalDate() : null);

        Date sqlDataReposicao = rs.getDate("data_reposicao");
        produto.setDataReposicao(sqlDataReposicao != null ? sqlDataReposicao.toLocalDate() : null);

        produto.setDescricao(rs.getString("descricao"));
        produto.setImagemPath(rs.getString("imagem_path"));

        return produto;
    }

    // --- MÉTODOS CRUD PRINCIPAIS ---

    public ProdutoModel criar(ProdutoModel produto) {
        // Uso de try-with-resources para fechar Connection e PreparedStatement automaticamente
        try (Connection conn = Conexao.getConn();
             PreparedStatement stmt = conn.prepareStatement(INSERT_PRODUTO, Statement.RETURN_GENERATED_KEYS)) {

            // Mapeamento dos 12 parâmetros
            stmt.setString(1, produto.getNome());
            stmt.setInt(2, produto.getQuantidade());
            stmt.setDouble(3, produto.getPrecoCusto());
            stmt.setDouble(4, produto.getPrecoVenda());
            stmt.setDouble(5, produto.getPrecoVenda()); // 🚨 Mapeamento da coluna 'preco'
            stmt.setDate(6, Date.valueOf(produto.getDataEntrada()));

            if (produto.getDataReposicao() != null) {
                stmt.setDate(7, Date.valueOf(produto.getDataReposicao()));
            } else {
                stmt.setNull(7, Types.DATE);
            }
            stmt.setString(8, produto.getCategoria());
            stmt.setString(9, produto.getGenero());
            stmt.setString(10, produto.getCor());
            stmt.setString(11, produto.getDescricao());
            stmt.setString(12, produto.getImagemPath());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        produto.setId(rs.getInt(1));
                    }
                }
            } else {
                System.err.println("Falha ao criar produto: nenhuma linha afetada.");
            }

        } catch (SQLException e) {
            System.err.println("\u274C[ Erro SQL ao criar produto: " + e.getMessage());
            e.printStackTrace(); // 🚨 Permite ver o erro de 'NOT NULL' ou outro erro SQL
        }
        return produto;
    }

    public List<ProdutoModel> listarTodos() {
        List<ProdutoModel> produtos = new ArrayList<>();

        try (Connection conn = Conexao.getConn();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_PRODUTOS);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                produtos.add(mapResultSetToProduto(rs));
            }

        } catch (SQLException e) {
            System.err.println("\u274C Erro SQL ao listar produtos: " + e.getMessage());
            e.printStackTrace();
        }

        return produtos;
    }

    public ProdutoModel buscarPorId(int id) {
        ProdutoModel produto = null;

        try (Connection conn = Conexao.getConn();
             PreparedStatement stmt = conn.prepareStatement(SELECT_PRODUTO_BY_ID)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    produto = mapResultSetToProduto(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("\u274C Erro SQL ao buscar produto por ID: " + e.getMessage());
            e.printStackTrace();
        }

        return produto;
    }

    public boolean atualizar(ProdutoModel produto) {
        try (Connection conn = Conexao.getConn();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_PRODUTO)) {

            stmt.setString(1, produto.getNome());
            stmt.setInt(2, produto.getQuantidade());
            stmt.setDouble(3, produto.getPrecoCusto());
            stmt.setDouble(4, produto.getPrecoVenda());
            stmt.setDouble(5, produto.getPrecoVenda()); // 🚨 Mapeamento da coluna 'preco'
            stmt.setDate(6, Date.valueOf(produto.getDataEntrada()));

            if (produto.getDataReposicao() != null) {
                stmt.setDate(7, Date.valueOf(produto.getDataReposicao()));
            } else {
                stmt.setNull(7, Types.DATE);
            }
            stmt.setString(8, produto.getCategoria());
            stmt.setString(9, produto.getGenero());
            stmt.setString(10, produto.getCor());
            stmt.setString(11, produto.getDescricao());
            stmt.setString(12, produto.getImagemPath());

            stmt.setInt(13, produto.getId()); // ID para a cláusula WHERE

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("\u274C Erro SQL ao atualizar produto: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // ... (Métodos excluir e atualizarEstoque mantidos por estarem corretos)

    public boolean excluir(int id) {
        try (Connection conn = Conexao.getConn();
             PreparedStatement stmt = conn.prepareStatement(DELETE_PRODUTO)) {

            stmt.setInt(1, id);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Erro SQL ao excluir produto: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean atualizarEstoque(int idProduto, int novaQuantidade) {
        try (Connection conn = Conexao.getConn();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_ESTOQUE)) {

            stmt.setInt(1, novaQuantidade);
            stmt.setInt(2, idProduto);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Erro SQL ao atualizar estoque: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
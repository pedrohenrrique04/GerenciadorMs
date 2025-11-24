package Dao;

import Model.ProdutoModel;
import util.Conexao;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * ProdutoDAO para MySQL, com métodos CRUD completos e manipulação de estoque.
 */
public class ProdutoDAO {

    // Query para inserir um novo produto
    private static final String INSERT_PRODUTO =
            "INSERT INTO produtos (nome, quantidade, preco_custo, preco_venda, data_entrada, data_reposicao, categoria, genero, cor, descricao, imagem_path) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    // Query para listar todos
    private static final String SELECT_ALL_PRODUTOS =
            "SELECT id, nome, quantidade, preco_custo, preco_venda, data_entrada, data_reposicao, categoria, genero, cor, descricao, imagem_path FROM produtos";

    // 🚨 NOVO: Query para buscar um produto por ID
    private static final String SELECT_PRODUTO_BY_ID =
            "SELECT id, nome, quantidade, preco_custo, preco_venda, data_entrada, data_reposicao, categoria, genero, cor, descricao, imagem_path FROM produtos WHERE id = ?";

    // 🚨 NOVO: Query para atualizar um produto (usado no salvarProduto() do Controller)
    private static final String UPDATE_PRODUTO =
            "UPDATE produtos SET nome = ?, quantidade = ?, preco_custo = ?, preco_venda = ?, data_entrada = ?, data_reposicao = ?, categoria = ?, genero = ?, cor = ?, descricao = ?, imagem_path = ? WHERE id = ?";

    // Query para atualizar apenas o estoque (usado na TelaRealizarVenda)
    private static final String UPDATE_ESTOQUE =
            "UPDATE produtos SET quantidade = ? WHERE id = ?";

    // 🚨 NOVO: Query para deletar um produto
    private static final String DELETE_PRODUTO =
            "DELETE FROM produtos WHERE id = ?";


    // --- MÉTODOS AUXILIARES ---

    // Método auxiliar para mapear um ResultSet para um objeto ProdutoModel
    private ProdutoModel mapResultSetToProduto(ResultSet rs) throws SQLException {
        ProdutoModel produto = new ProdutoModel(
                rs.getInt("id"),
                rs.getString("nome"),
                rs.getInt("quantidade"),
                rs.getDouble("preco_custo"),
                rs.getDouble("preco_venda"),
                null, // Datas serão mapeadas abaixo
                null, // Datas serão mapeadas abaixo
                rs.getString("categoria"),
                rs.getString("genero"),
                rs.getString("cor")
        );

        // Mapeamento de Datas (SQL Date para Java LocalDate)
        Date sqlDataEntrada = rs.getDate("data_entrada");
        produto.setDataEntrada((sqlDataEntrada != null) ? sqlDataEntrada.toLocalDate() : null);
        Date sqlDataReposicao = rs.getDate("data_reposicao");
        produto.setDataReposicao((sqlDataReposicao != null) ? sqlDataReposicao.toLocalDate() : null);

        produto.setDescricao(rs.getString("descricao"));
        produto.setImagemPath(rs.getString("imagem_path"));

        return produto;
    }


    // --- MÉTODOS CRUD PRINCIPAIS ---

    /**
     * Insere um novo produto no banco.
     */
    public ProdutoModel criar(ProdutoModel produto) {
        // Implementação do INSERT_PRODUTO (mantida da versão anterior)
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = Conexao.getConn();
            stmt = conn.prepareStatement(INSERT_PRODUTO, Statement.RETURN_GENERATED_KEYS);

            // 1. Mapeamento dos parâmetros
            stmt.setString(1, produto.getNome());
            stmt.setInt(2, produto.getQuantidade());
            stmt.setDouble(3, produto.getPrecoCusto());
            stmt.setDouble(4, produto.getPrecoVenda());

            stmt.setDate(5, Date.valueOf(produto.getDataEntrada()));

            if (produto.getDataReposicao() != null) {
                stmt.setDate(6, Date.valueOf(produto.getDataReposicao()));
            } else {
                stmt.setNull(6, Types.DATE);
            }

            stmt.setString(7, produto.getCategoria());
            stmt.setString(8, produto.getGenero());
            stmt.setString(9, produto.getCor());
            stmt.setString(10, produto.getDescricao());
            stmt.setString(11, produto.getImagemPath());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    produto.setId(rs.getInt(1));
                }
            } else {
                System.err.println("Falha ao criar produto, nenhuma linha afetada.");
            }

        } catch (SQLException e) {
            System.err.println("❌ Erro SQL ao criar produto: " + e.getMessage());
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { }
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { }
            try { if (conn != null) conn.close(); } catch (SQLException e) { }
        }
        return produto;
    }

    /**
     * Lista todos os produtos (usado na TelaRealizarVenda para carregar a lista)
     */
    public List<ProdutoModel> listarTodos() {
        List<ProdutoModel> produtos = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = Conexao.getConn();
            stmt = conn.prepareStatement(SELECT_ALL_PRODUTOS);
            rs = stmt.executeQuery();

            while (rs.next()) {
                // Utiliza o método auxiliar para evitar repetição de código
                produtos.add(mapResultSetToProduto(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Erro SQL ao buscar produtos: " + e.getMessage());
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) {}
            try { if (stmt != null) stmt.close(); } catch (SQLException e) {}
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
        return produtos;
    }

    /**
     * 🚨 NOVO MÉTODO: Busca um produto pelo ID.
     * Usado para carregar a tela de detalhes/edição (ProdutoController.carregarProduto).
     */
    public ProdutoModel buscarPorId(int id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        ProdutoModel produto = null;

        try {
            conn = Conexao.getConn();
            stmt = conn.prepareStatement(SELECT_PRODUTO_BY_ID);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                produto = mapResultSetToProduto(rs);
            }

        } catch (SQLException e) {
            System.err.println("❌ Erro SQL ao buscar produto por ID: " + e.getMessage());
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) {}
            try { if (stmt != null) stmt.close(); } catch (SQLException e) {}
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
        return produto;
    }

    /**
     * 🚨 NOVO MÉTODO: Atualiza todos os dados de um produto.
     * Usado no ProdutoController.salvarProduto().
     */
    public boolean atualizar(ProdutoModel produto) {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean sucesso = false;

        try {
            conn = Conexao.getConn();
            stmt = conn.prepareStatement(UPDATE_PRODUTO);

            // Mapeamento dos parâmetros (1 a 11)
            stmt.setString(1, produto.getNome());
            stmt.setInt(2, produto.getQuantidade());
            stmt.setDouble(3, produto.getPrecoCusto());
            stmt.setDouble(4, produto.getPrecoVenda());

            stmt.setDate(5, Date.valueOf(produto.getDataEntrada()));

            if (produto.getDataReposicao() != null) {
                stmt.setDate(6, Date.valueOf(produto.getDataReposicao()));
            } else {
                stmt.setNull(6, Types.DATE);
            }

            stmt.setString(7, produto.getCategoria());
            stmt.setString(8, produto.getGenero());
            stmt.setString(9, produto.getCor());
            stmt.setString(10, produto.getDescricao());
            stmt.setString(11, produto.getImagemPath());

            // ID do produto para a cláusula WHERE
            stmt.setInt(12, produto.getId());

            int affectedRows = stmt.executeUpdate();
            sucesso = affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("❌ Erro SQL ao atualizar produto: " + e.getMessage());
        } finally {
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { }
            try { if (conn != null) conn.close(); } catch (SQLException e) { }
        }
        return sucesso;
    }

    /**
     * 🚨 NOVO MÉTODO: Exclui um produto pelo ID.
     * Usado no ProdutoController.excluirProduto().
     */
    public boolean excluir(int id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean sucesso = false;

        try {
            conn = Conexao.getConn();
            stmt = conn.prepareStatement(DELETE_PRODUTO);
            stmt.setInt(1, id);

            int affectedRows = stmt.executeUpdate();
            sucesso = affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("❌ Erro SQL ao excluir produto: " + e.getMessage());
        } finally {
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { }
            try { if (conn != null) conn.close(); } catch (SQLException e) { }
        }
        return sucesso;
    }

    /**
     * Atualiza a quantidade de estoque de um produto específico (usado na venda).
     */
    public boolean atualizarEstoque(int idProduto, int novaQuantidade) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = Conexao.getConn();
            stmt = conn.prepareStatement(UPDATE_ESTOQUE);
            stmt.setInt(1, novaQuantidade);
            stmt.setInt(2, idProduto);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("❌ Erro SQL ao atualizar estoque: " + e.getMessage());
            return false;
        } finally {
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { }
            try { if (conn != null) conn.close(); } catch (SQLException e) { }
        }
    }
}
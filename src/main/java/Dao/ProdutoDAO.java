package Dao;

import Model.ProdutoModel;
import util.Conexao; // <--- Importando SUA classe de conexão
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProdutoDAO {

    public void salvar(ProdutoModel p) {
        String sql = "INSERT INTO produtos (nome, quantidade, preco_custo, preco_venda, data_entrada, data_reposicao, categoria, genero, cor, descricao, imagem_path) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        // Usando o seu método Conexao.getConn()
        try (Connection conn = Conexao.getConn();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, p.getNome());
            stmt.setInt(2, p.getQuantidade());
            stmt.setDouble(3, p.getPrecoCusto());
            stmt.setDouble(4, p.getPrecoVenda());
            stmt.setDate(5, java.sql.Date.valueOf(p.getDataEntrada()));
            stmt.setDate(6, p.getDataReposicao() != null ? java.sql.Date.valueOf(p.getDataReposicao()) : null);
            stmt.setString(7, p.getCategoria());
            stmt.setString(8, p.getGenero());
            stmt.setString(9, p.getCor());
            stmt.setString(10, p.getDescricao());
            stmt.setString(11, p.getImagemPath());

            stmt.execute();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                p.setId(rs.getInt(1));
            }
            System.out.println("✅ Produto salvo no Railway com sucesso!");

        } catch (SQLException e) {
            System.err.println("Erro ao salvar produto: " + e.getMessage());
        }
    }

    public List<ProdutoModel> listar() {
        List<ProdutoModel> produtos = new ArrayList<>();
        String sql = "SELECT * FROM produtos";

        try (Connection conn = Conexao.getConn();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                ProdutoModel p = new ProdutoModel(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getInt("quantidade"),
                        rs.getDouble("preco_custo"),
                        rs.getDouble("preco_venda"),
                        rs.getDate("data_entrada").toLocalDate(),
                        rs.getDate("data_reposicao") != null ? rs.getDate("data_reposicao").toLocalDate() : null,
                        rs.getString("categoria"),
                        rs.getString("genero"),
                        rs.getString("cor")
                );
                p.setDescricao(rs.getString("descricao"));
                p.setImagemPath(rs.getString("imagem_path"));
                produtos.add(p);
            }

        } catch (SQLException e) {
            System.err.println("Erro ao listar produtos: " + e.getMessage());
        }
        return produtos;
    }

    public void atualizar(ProdutoModel p) {
        String sql = "UPDATE produtos SET nome=?, quantidade=?, preco_custo=?, preco_venda=?, data_reposicao=?, descricao=?, imagem_path=? WHERE id=?";

        try (Connection conn = Conexao.getConn();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, p.getNome());
            stmt.setInt(2, p.getQuantidade());
            stmt.setDouble(3, p.getPrecoCusto());
            stmt.setDouble(4, p.getPrecoVenda());
            stmt.setDate(5, p.getDataReposicao() != null ? java.sql.Date.valueOf(p.getDataReposicao()) : null);
            stmt.setString(6, p.getDescricao());
            stmt.setString(7, p.getImagemPath());
            stmt.setInt(8, p.getId());

            stmt.execute();
            System.out.println("✅ Produto atualizado no Railway!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void excluir(int id) {
        String sql = "DELETE FROM produtos WHERE id=?";

        try (Connection conn = Conexao.getConn();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.execute();
            System.out.println("✅ Produto excluído!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
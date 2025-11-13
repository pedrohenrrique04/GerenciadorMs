package Dao;

import Model.Produto;
import util.Conexao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProdutoDAO {

    public List<Produto> listarProdutos() {
        List<Produto> produtos = new ArrayList<>();
        String sql = "SELECT id, nome, preco FROM produtos";

        try (Connection conn = Conexao.getConn();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Produto p = new Produto(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getDouble("preco")
                );
                produtos.add(p);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return produtos;
    }
}

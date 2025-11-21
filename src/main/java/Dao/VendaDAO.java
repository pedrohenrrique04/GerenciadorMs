package Dao;

import Model.Venda;
import util.Conexao;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class VendaDAO {

    public void registrarVenda(Venda v) {
        String sql = "INSERT INTO vendas (produto, quantidade, preco, desconto, forma_pagamento, total) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = Conexao.getConn();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, v.getProduto());
            stmt.setInt(2, v.getQuantidade());
            stmt.setDouble(3, v.getPreco());
            stmt.setDouble(4, v.getDesconto());
            stmt.setString(5, v.getFormaPagamento());
            stmt.setDouble(6, v.getTotal());

            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

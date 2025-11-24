package Dao;

import Model.Produto;
import Model.RelatorioProduto;
import util.Conexao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RelatoriosProdutosDao {

    // RETORNA O SOMATÓRIO DO ESTOQUE DE TODOS OS PRODUTOS
    public int getEstoqueTotal() {

        String sql = "SELECT COALESCE(SUM(estoque), 0) AS estoque_total FROM produtos";

        try (Connection conn = Conexao.getConn();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("estoque_total");
            }

        } catch (SQLException e) {
            System.out.println("Erro ao buscar estoque total: " + e.getMessage());
        }

        return 0;
    }


    // ===========================================
    // 🔥 NOVO MÉTODO: ESTOQUE TOTAL POR CATEGORIA
    // ===========================================
    public int getEstoquePorCategoria(String categoria) {

        String sql = "SELECT COALESCE(SUM(estoque), 0) AS estoque_categoria "
                + "FROM produtos WHERE categoria = ?";

        try (Connection conn = Conexao.getConn();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, categoria);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("estoque_categoria");
            }

        } catch (SQLException e) {
            System.out.println("Erro ao buscar estoque por categoria: " + e.getMessage());
        }

        return 0;
    }

    public List<Produto> listarProdutosEstoqueMinimo() {

        List<Produto> lista = new ArrayList<>();

        // Estoque mínimo fixo
        final int ESTOQUE_MINIMO = 10;

        String sql = "SELECT * FROM produtos WHERE estoque <= ?";

        try (Connection conn = Conexao.getConn();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, ESTOQUE_MINIMO);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Produto p = new Produto();
                p.setId(rs.getInt("id"));
                p.setNome(rs.getString("nome"));
                p.setPreco(rs.getBigDecimal("preco"));
                p.setPrecoCusto(rs.getBigDecimal("preco_custo"));
                p.setEstoque(rs.getInt("estoque"));
                p.setCusto(rs.getBigDecimal("custo"));
                p.setCategoria(rs.getString("categoria"));

                lista.add(p);
            }

        } catch (SQLException e) {
            System.out.println("Erro ao listar produtos com estoque mínimo: " + e.getMessage());
        }

        return lista;
    }

    // 🔥 MÉTODO PRINCIPAL PARA A TABLEVIEW
    // ===========================================
    public List<RelatorioProduto> gerarRelatorioGeral() {
        List<RelatorioProduto> lista = new ArrayList<>();

        // Query com LEFT JOIN para trazer produtos mesmo sem vendas
        String sql = "SELECT " +
                "   p.nome AS produto, " +
                "   p.categoria, " +
                "   p.estoque AS quantidade, " +
                "   p.preco_custo AS preco_unit, " +
                "   p.preco AS preco_vend, " +
                "   MAX(v.data_venda) AS ultima_venda " +
                "FROM produtos p " +
                "LEFT JOIN vendas v ON p.id = v.produto_id " +
                "GROUP BY p.id, p.nome, p.categoria, p.estoque, p.preco_custo, p.preco";

        try (Connection conn = Conexao.getConn();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                // 1. Extrair dados com segurança (tratando nulos se necessário)
                String nome = rs.getString("produto");
                String cat = rs.getString("categoria");
                int qtd = rs.getInt("quantidade");

                // Pegamos timestamp pois data_venda é datetime
                java.util.Date dataUltima = null;
                if (rs.getTimestamp("ultima_venda") != null) {
                    dataUltima = new java.util.Date(rs.getTimestamp("ultima_venda").getTime());
                }

                BigDecimal pUnit = rs.getBigDecimal("preco_unit");
                BigDecimal pVend = rs.getBigDecimal("preco_vend");

                // 2. Criar o objeto (O lucro é calculado no construtor)
                RelatorioProduto item = new RelatorioProduto(nome, cat, qtd, dataUltima, pUnit, pVend);

                lista.add(item);
            }

        } catch (SQLException e) {
            System.out.println("Erro ao gerar relatório geral: " + e.getMessage());
            e.printStackTrace();
        }

        return lista;
    }

}

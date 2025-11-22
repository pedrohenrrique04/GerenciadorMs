package Dao;

import util.Conexao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.sql.Timestamp;

public class RelatoriosDao {

    public double getTotalVendas(LocalDateTime dataInicial, LocalDateTime dataFinal) {

        // GARANTE QUE AS DATAS NUNCA CHEGAM NULL
        if (dataInicial == null || dataFinal == null) {
            return 0.0;
        }

        String sql = "SELECT COALESCE(SUM(total), 0) AS total_vendas " +
                "FROM vendas WHERE data_venda BETWEEN ? AND ?";

        try (Connection conn = Conexao.getConn();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setTimestamp(1, Timestamp.valueOf(dataInicial));
            ps.setTimestamp(2, Timestamp.valueOf(dataFinal));

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getDouble("total_vendas");
            }

        } catch (SQLException e) {
            System.out.println("Erro ao buscar total de vendas: " + e.getMessage());
        }

        return 0.0;
    }

    public double getLucroBruto(LocalDateTime dataInicial, LocalDateTime dataFinal) {

        if (dataInicial == null || dataFinal == null) {
            return 0.0;
        }

        String sql = "SELECT COALESCE(SUM((v.preco - p.custo) * v.quantidade), 0) AS lucro_bruto " +
                "FROM vendas v " +
                "JOIN produtos p ON p.nome = v.produto " +
                "WHERE v.data_venda BETWEEN ? AND ?";

        try (Connection conn = Conexao.getConn();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setTimestamp(1, Timestamp.valueOf(dataInicial));
            ps.setTimestamp(2, Timestamp.valueOf(dataFinal));

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getDouble("lucro_bruto");
            }

        } catch (SQLException e) {
            System.out.println("Erro ao buscar lucro bruto: " + e.getMessage());
        }

        return 0.0;
    }

    public double getMargemLucro(LocalDateTime dataInicial, LocalDateTime dataFinal) {

        if (dataInicial == null || dataFinal == null) {
            return 0.0;
        }

        double totalVendas = getTotalVendas(dataInicial, dataFinal);
        double lucroBruto = getLucroBruto(dataInicial, dataFinal);

        if (totalVendas <= 0) {
            return 0.0; // evita divisão por zero
        }

        return (lucroBruto / totalVendas) * 100.0;
    }
}

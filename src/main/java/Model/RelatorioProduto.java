package Model;

import java.math.BigDecimal;
import java.util.Date;

public class RelatorioProduto {
    private String produto;
    private String categoria;
    private int quantidadeDisponivel;
    private Date ultimaVenda;
    private BigDecimal precoUnit;    // Preço de Custo
    private BigDecimal precoVendido; // Preço de Venda
    private BigDecimal lucroUnit;    // Calculado

    public RelatorioProduto(String produto, String categoria, int quantidadeDisponivel,
                            Date ultimaVenda, BigDecimal precoUnit, BigDecimal precoVendido) {
        this.produto = produto;
        this.categoria = categoria;
        this.quantidadeDisponivel = quantidadeDisponivel;
        this.ultimaVenda = ultimaVenda;
        this.precoUnit = precoUnit;
        this.precoVendido = precoVendido;

        // Calcula Lucro automaticamente: Venda - Custo
        if (precoVendido != null && precoUnit != null) {
            this.lucroUnit = precoVendido.subtract(precoUnit);
        } else {
            this.lucroUnit = BigDecimal.ZERO;
        }
    }

    // Getters e Setters (Essenciais para a TableView)
    public String getProduto() { return produto; }
    public void setProduto(String produto) { this.produto = produto; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public int getQuantidadeDisponivel() { return quantidadeDisponivel; }
    public void setQuantidadeDisponivel(int quantidadeDisponivel) { this.quantidadeDisponivel = quantidadeDisponivel; }

    public Date getUltimaVenda() { return ultimaVenda; }
    public void setUltimaVenda(Date ultimaVenda) { this.ultimaVenda = ultimaVenda; }

    public BigDecimal getPrecoUnit() { return precoUnit; }
    public void setPrecoUnit(BigDecimal precoUnit) { this.precoUnit = precoUnit; }

    public BigDecimal getPrecoVendido() { return precoVendido; }
    public void setPrecoVendido(BigDecimal precoVendido) { this.precoVendido = precoVendido; }

    public BigDecimal getLucroUnit() { return lucroUnit; }
    // Não precisa de setter para lucroUnit, pois é calculado
}
package Model;

public class Venda {
    private String produto;
    private int quantidade;
    private double preco;
    private double desconto;
    private String formaPagamento;
    private double total;

    public Venda(String produto, int quantidade, double preco, double desconto, String formaPagamento, double total) {
        this.produto = produto;
        this.quantidade = quantidade;
        this.preco = preco;
        this.desconto = desconto;
        this.formaPagamento = formaPagamento;
        this.total = total;
    }

    public String getProduto() { return produto; }
    public int getQuantidade() { return quantidade; }
    public double getPreco() { return preco; }
    public double getDesconto() { return desconto; }
    public String getFormaPagamento() { return formaPagamento; }
    public double getTotal() { return total; }
}

package Model;

/**
 * Representa um item no carrinho de compras antes de ser salvo como ItemVenda no banco.
 * Contém informações do produto (ID, nome, preço unitário) e a quantidade comprada.
 */
public class CartItem {
    private int id; // ID do Produto (referência)
    private String nome;
    private double preco; // Preço unitário no momento da venda
    private int quantidade;

    public CartItem(int id, String nome, double preco, int quantidade) {
        this.id = id;
        this.nome = nome;
        this.preco = preco;
        this.quantidade = quantidade;
    }

    // --- Getters ---

    /**
     * Retorna o ID do Produto (chave estrangeira para a tabela de Produtos).
     * ESSENCIAL para o VendaDAO.
     */
    public int getId() { return id; }

    public String getNome() { return nome; }
    public double getPreco() { return preco; }
    public int getQuantidade() { return quantidade; }

    // --- Setters ---
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

    // --- Cálculo ---
    public double getTotal() {
        return preco * quantidade;
    }
}
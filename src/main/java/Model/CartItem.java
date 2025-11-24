package Model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleDoubleProperty;

/**
 * Modelo de dados que representa um item no carrinho de compras ou na venda finalizada.
 * Combina um ProdutoModel com a quantidade desejada.
 * Utiliza SimpleProperties para integração eficiente com a TableView do JavaFX.
 */
public class CartItem {

    private ProdutoModel produto;
    private SimpleIntegerProperty quantidade;
    private SimpleDoubleProperty subtotal;

    /**
     * Construtor para criar um novo item de carrinho.
     * @param produto O ProdutoModel que está sendo adicionado.
     * @param quantidade A quantidade inicial deste produto.
     */
    public CartItem(ProdutoModel produto, int quantidade) {
        this.produto = produto;
        this.quantidade = new SimpleIntegerProperty(quantidade);
        // Inicializa o subtotal calculando o preço de venda * quantidade
        this.subtotal = new SimpleDoubleProperty(produto.getPrecoVenda() * quantidade);
    }

    // --- Getters e Setters ---

    public ProdutoModel getProduto() {
        return produto;
    }

    public int getQuantidade() {
        return quantidade.get();
    }

    /**
     * Define a nova quantidade e recalcula automaticamente o subtotal.
     */
    public void setQuantidade(int quantidade) {
        this.quantidade.set(quantidade);
        this.subtotal.set(this.produto.getPrecoVenda() * quantidade);
    }

    public double getSubtotal() {
        return subtotal.get();
    }

    // --- Propriedades para JavaFX TableView (Colunas) ---

    // Nome do produto (String)
    public SimpleStringProperty nomeProperty() {
        return new SimpleStringProperty(produto.getNome());
    }

    // Preço unitário do produto (Double)
    public SimpleDoubleProperty precoUnitarioProperty() {
        return new SimpleDoubleProperty(produto.getPrecoVenda());
    }

    // Quantidade do item no carrinho (Integer)
    public SimpleIntegerProperty quantidadeProperty() {
        return quantidade;
    }

    // Subtotal calculado (Double)
    public SimpleDoubleProperty subtotalProperty() {
        return subtotal;
    }
}
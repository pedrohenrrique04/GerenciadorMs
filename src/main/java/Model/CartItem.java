package Model;

import javafx.beans.property.*;

/**
 * Representa um item dentro do carrinho de compras.
 * Usa JavaFX Properties para funcionar corretamente com a TableView.
 */
public class CartItem {

    private final IntegerProperty id;
    private final StringProperty nome;
    private final DoubleProperty preco;
    private final IntegerProperty quantidade;
    private final DoubleProperty total;

    public CartItem(int id, String nome, double preco, int quantidade) {
        this.id = new SimpleIntegerProperty(id);
        this.nome = new SimpleStringProperty(nome);
        this.preco = new SimpleDoubleProperty(preco);
        this.quantidade = new SimpleIntegerProperty(quantidade);
        this.total = new SimpleDoubleProperty(preco * quantidade);

        // Listener para recalcular o total automaticamente
        this.quantidade.addListener((obs, oldVal, newVal) ->
                this.total.set(getPreco() * newVal.doubleValue())
        );
        this.preco.addListener((obs, oldVal, newVal) ->
                this.total.set(newVal.doubleValue() * getQuantidade())
        );
    }

    // --- Getters (padrão) ---
    public int getId() { return id.get(); }
    public String getNome() { return nome.get(); }
    public double getPreco() { return preco.get(); }
    public int getQuantidade() { return quantidade.get(); }
    public double getTotal() { return total.get(); }

    // --- Setters ---
    public void setQuantidade(int qtd) { this.quantidade.set(qtd); }

    // --- Property Getters (para o JavaFX) ---
    public IntegerProperty idProperty() { return id; }
    public StringProperty nomeProperty() { return nome; }
    public DoubleProperty precoProperty() { return preco; }
    public IntegerProperty quantidadeProperty() { return quantidade; }
    public DoubleProperty totalProperty() { return total; }
}
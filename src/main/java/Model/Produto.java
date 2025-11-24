package Model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Representa um produto "do banco de dados".
 * Usa JavaFX Properties para a tabela de seleção.
 */
public class Produto {

    private final IntegerProperty id;
    private final StringProperty nome;
    private final DoubleProperty preco;

    public Produto(int id, String nome, double preco) {
        this.id = new SimpleIntegerProperty(id);
        this.nome = new SimpleStringProperty(nome);
        this.preco = new SimpleDoubleProperty(preco);
    }

    // --- Getters (padrão) ---
    public int getId() { return id.get(); }
    public String getNome() { return nome.get(); }
    public double getPreco() { return preco.get(); }

    // --- Property Getters (para o JavaFX) ---
    public IntegerProperty idProperty() { return id; }
    public StringProperty nomeProperty() { return nome; }
    public DoubleProperty precoProperty() { return preco; }
}
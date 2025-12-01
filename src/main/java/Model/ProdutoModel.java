package Model;

import java.time.LocalDate;

/**
 * Modelo de dados para representar um produto no estoque.
 * Contém todas as informações detalhadas sobre o item.
 */
public class ProdutoModel {
    private int id;
    private String nome;
    private int quantidade; // Estoque
    private double precoCusto;
    private double precoVenda;
    private LocalDate dataEntrada;
    private LocalDate dataReposicao;
    private String categoria;
    private String genero;
    private String cor;

    /**
     * Construtor Completo.
     */
    public ProdutoModel(int id, String nome, int quantidade, double precoCusto, double precoVenda,
                        LocalDate dataEntrada, LocalDate dataReposicao, String categoria, String genero, String cor) {
        this.id = id;
        this.nome = nome;
        this.quantidade = quantidade;
        this.precoCusto = precoCusto;
        this.precoVenda = precoVenda;
        this.dataEntrada = dataEntrada;
        this.dataReposicao = dataReposicao;
        this.categoria = categoria;
        this.genero = genero;
        this.cor = cor;
    }

    /**
     * Construtor para novos produtos (ID é gerado pelo DAO).
     */
    public ProdutoModel(String nome, int quantidade, double precoCusto, double precoVenda,
                        String categoria, String genero, String cor) {
        this(0, nome, quantidade, precoCusto, precoVenda, LocalDate.now(), null, categoria, genero, cor);
    }

    // --- Getters e Setters ---

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

    public double getPrecoCusto() { return precoCusto; }
    public void setPrecoCusto(double precoCusto) { this.precoCusto = precoCusto; }

    public double getPrecoVenda() { return precoVenda; }
    public void setPrecoVenda(double precoVenda) { this.precoVenda = precoVenda; }

    public LocalDate getDataEntrada() { return dataEntrada; }
    public void setDataEntrada(LocalDate dataEntrada) { this.dataEntrada = dataEntrada; }

    public LocalDate getDataReposicao() { return dataReposicao; }
    public void setDataReposicao(LocalDate dataReposicao) { this.dataReposicao = dataReposicao; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }

    public String getCor() { return cor; }
    public void setCor(String cor) { this.cor = cor; }
}
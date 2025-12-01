package Model; // Pacote: model

import java.io.Serializable;
import java.time.LocalDate;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Classe de modelo de dados para representar um Produto.
 */
public class ProdutoModel implements Serializable { // CLASSE RENOMEADA

    private static final long serialVersionUID = 1L;

    // Construtor 1 (Simplificado)
    public ProdutoModel(int id, String nome, double precoVenda) {
        this.id = id;
        this.nome = nome;
        this.precoVenda = precoVenda;
        this.quantidade = 0;
        this.precoCusto = 0;
        this.dataEntrada = null;
        this.dataReposicao = null;
        this.categoria = "";
        this.genero = "";
        this.cor = "";
        this.descricao = "";
        this.imagemPath = "";
    }


    private int id;
    private String nome;
    private int quantidade;
    private double precoCusto;
    private double precoVenda;

    private LocalDate dataEntrada;
    private LocalDate dataReposicao;

    private String categoria;
    private String genero;
    private String cor;
    private String descricao;
    private String imagemPath;

    // Construtor 2 (Completo)
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
        this.descricao = "";
        this.imagemPath = "";
    }

    // --- Getters e Setters (Mantidos) ---

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

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getImagemPath() { return imagemPath; }
    public void setImagemPath(String imagemPath) { this.imagemPath = imagemPath; }

    // 🚨 CORREÇÃO: Sobrescreve o método toString para exibir o produto de forma amigável
    @Override
    public String toString() {
        // Formata o preço para R$ X.XX usando o locale BR
        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        String precoFormatado = nf.format(this.precoVenda);

        // Exibição amigável: Nome | Preço Formatado | Estoque
        return this.nome + " | " + precoFormatado + " | Estoque: " + this.quantidade;
    }
}
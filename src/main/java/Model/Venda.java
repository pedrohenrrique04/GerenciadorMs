package Model;

import java.util.Date;
import java.util.List;

/**
 * Classe Model para representar a Venda principal (cabeçalho).
 * CONTÉM DOIS CONSTRUTORES para atender:
 * 1. A tela PDV (com 3 args, incluindo lista de itens).
 * 2. O VendaController (com 6 args, que usa campos detalhados).
 */
public class Venda {
    private int id;
    private Date dataHora;
    private double totalVenda;
    private String formaPagamento;
    private List<CartItem> itens; // Lista de itens (Pode ser null se usado pelo construtor de 6 args)

    // --- CONSTRUTOR 1: Para PDV (3 argumentos) ---
    public Venda(double totalVenda, String formaPagamento, List<CartItem> itens) {
        this.dataHora = new Date();
        this.totalVenda = totalVenda;
        this.formaPagamento = formaPagamento;
        this.itens = itens;
    }

    // --- CONSTRUTOR 2: CORRIGIDO! Para o VendaController (6 argumentos) ---
    // Este é o construtor que o compilador estava procurando:
    public Venda(String cliente, int id, double totalProduto, double desconto, String formaPagamento, double acrescimo) {
        // Assume que 'cliente' é o nome/CPF e 'id' pode ser o ID de cliente
        this.id = id;
        this.dataHora = new Date();
        this.formaPagamento = formaPagamento;

        // Calcula o total da venda baseado nos parâmetros:
        this.totalVenda = (totalProduto - desconto) + acrescimo;

        this.itens = null; // Como a lista de itens não foi passada, ela é nula aqui.
    }

    // --- Getters e Setters (Necessários para DAO) ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public double getTotalVenda() { return totalVenda; }
    public String getFormaPagamento() { return formaPagamento; }
    public List<CartItem> getItens() { return itens; }
    public Date getDataHora() { return dataHora; }
}
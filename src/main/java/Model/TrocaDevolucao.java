package Model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Modelo de dados para representar uma solicitação de Troca ou Devolução.
 */
public class TrocaDevolucao {

    private int id;
    private int idProduto;
    private String numeroPedido;
    private String tipo; // Ex: "TROCA", "DEVOLUCAO"
    private String motivo;
    private LocalDate dataSolicitacao;
    private LocalDateTime dataProcessamento;
    private String status; // Ex: "PENDENTE", "PROCESSADA", "REJEITADA"
    private int quantidade;
    private double precoVenda;
    private String observacoes;

    /**
     * Construtor Completo.
     */
    public TrocaDevolucao(int id, int idProduto, String numeroPedido, String tipo, String motivo,
                          LocalDate dataSolicitacao, LocalDateTime dataProcessamento, String status,
                          int quantidade, double precoVenda, String observacoes) {
        this.id = id;
        this.idProduto = idProduto;
        this.numeroPedido = numeroPedido;
        this.tipo = tipo;
        this.motivo = motivo;
        this.dataSolicitacao = dataSolicitacao;
        this.dataProcessamento = dataProcessamento;
        this.status = status;
        this.quantidade = quantidade;
        this.precoVenda = precoVenda;
        this.observacoes = observacoes;
    }

    /**
     * Construtor para novas solicitações (ID é gerado pelo DAO).
     */
    public TrocaDevolucao(int idProduto, String numeroPedido, String tipo, String motivo,
                          int quantidade, double precoVenda, String observacoes) {
        this(0, idProduto, numeroPedido, tipo, motivo, LocalDate.now(), null, "PENDENTE",
                quantidade, precoVenda, observacoes);
    }

    // --- Getters e Setters ---

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdProduto() { return idProduto; }
    public void setIdProduto(int idProduto) { this.idProduto = idProduto; }

    public String getNumeroPedido() { return numeroPedido; }
    public void setNumeroPedido(String numeroPedido) { this.numeroPedido = numeroPedido; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public LocalDate getDataSolicitacao() { return dataSolicitacao; }
    public void setDataSolicitacao(LocalDate dataSolicitacao) { this.dataSolicitacao = dataSolicitacao; }

    public LocalDateTime getDataProcessamento() { return dataProcessamento; }
    public void setDataProcessamento(LocalDateTime dataProcessamento) { this.dataProcessamento = dataProcessamento; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

    public double getPrecoVenda() { return precoVenda; }
    public void setPrecoVenda(double precoVenda) { this.precoVenda = precoVenda; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
}
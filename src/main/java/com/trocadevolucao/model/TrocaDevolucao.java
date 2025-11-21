package com.trocadevolucao.model;

import java.io.Serializable;
import java.time.LocalDate;

public class TrocaDevolucao implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int produtoId;
    private String numeroPedido;
    private String tipoSolicitacao;
    private String motivo;
    private String observacoes;
    private LocalDate dataSolicitacao;
    private LocalDate dataProcessamento;
    private String status;
    private int quantidade;
    private double valorUnitario;
    private double valorTotal;
    private String acaoTomada;
    private String produtoTrocado;
    private int produtoTrocadoId;

    public TrocaDevolucao() {}

    public TrocaDevolucao(int id, int produtoId, String numeroPedido, String tipoSolicitacao,
                          String motivo, LocalDate dataSolicitacao, String status,
                          int quantidade, double valorUnitario) {
        this.id = id;
        this.produtoId = produtoId;
        this.numeroPedido = numeroPedido;
        this.tipoSolicitacao = tipoSolicitacao;
        this.motivo = motivo;
        this.dataSolicitacao = dataSolicitacao;
        this.status = status;
        this.quantidade = quantidade;
        this.valorUnitario = valorUnitario;
        this.valorTotal = quantidade * valorUnitario;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getProdutoId() { return produtoId; }
    public void setProdutoId(int produtoId) { this.produtoId = produtoId; }

    public String getNumeroPedido() { return numeroPedido; }
    public void setNumeroPedido(String numeroPedido) { this.numeroPedido = numeroPedido; }

    public String getTipoSolicitacao() { return tipoSolicitacao; }
    public void setTipoSolicitacao(String tipoSolicitacao) {
        this.tipoSolicitacao = tipoSolicitacao;
    }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    public LocalDate getDataSolicitacao() { return dataSolicitacao; }
    public void setDataSolicitacao(LocalDate dataSolicitacao) {
        this.dataSolicitacao = dataSolicitacao;
    }

    public LocalDate getDataProcessamento() { return dataProcessamento; }
    public void setDataProcessamento(LocalDate dataProcessamento) {
        this.dataProcessamento = dataProcessamento;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
        calcularValorTotal();
    }

    public double getValorUnitario() { return valorUnitario; }
    public void setValorUnitario(double valorUnitario) {
        this.valorUnitario = valorUnitario;
        calcularValorTotal();
    }

    public double getValorTotal() { return valorTotal; }
    public void setValorTotal(double valorTotal) { this.valorTotal = valorTotal; }

    public String getAcaoTomada() { return acaoTomada; }
    public void setAcaoTomada(String acaoTomada) { this.acaoTomada = acaoTomada; }

    public String getProdutoTrocado() { return produtoTrocado; }
    public void setProdutoTrocado(String produtoTrocado) { this.produtoTrocado = produtoTrocado; }

    public int getProdutoTrocadoId() { return produtoTrocadoId; }
    public void setProdutoTrocadoId(int produtoTrocadoId) { this.produtoTrocadoId = produtoTrocadoId; }

    private void calcularValorTotal() {
        this.valorTotal = this.quantidade * this.valorUnitario;
    }

    public boolean isPendente() {
        return "PENDENTE".equals(this.status);
    }

    public boolean isAprovada() {
        return "APROVADA".equals(this.status);
    }

    public boolean isProcessada() {
        return "PROCESSADA".equals(this.status);
    }

    public boolean isTroca() {
        return "TROCA".equals(this.tipoSolicitacao);
    }

    public boolean isDevolucao() {
        return "DEVOLUCAO".equals(this.tipoSolicitacao);
    }

    public void aprovar() {
        this.status = "APROVADA";
        this.dataProcessamento = LocalDate.now();
    }

    public void rejeitar(String observacoes) {
        this.status = "REJEITADA";
        this.dataProcessamento = LocalDate.now();
        if (observacoes != null) {
            this.observacoes = observacoes;
        }
    }

    public void processar(String acaoTomada, String produtoTrocado, Integer produtoTrocadoId) {
        this.status = "PROCESSADA";
        this.dataProcessamento = LocalDate.now();
        this.acaoTomada = acaoTomada;

        if (isTroca() && produtoTrocado != null) {
            this.produtoTrocado = produtoTrocado;
            this.produtoTrocadoId = produtoTrocadoId != null ? produtoTrocadoId : 0;
        }
    }

    @Override
    public String toString() {
        return "TrocaDevolucao{" +
                "id=" + id +
                ", produtoId=" + produtoId +
                ", numeroPedido='" + numeroPedido + '\'' +
                ", tipoSolicitacao='" + tipoSolicitacao + '\'' +
                ", status='" + status + '\'' +
                ", dataSolicitacao=" + dataSolicitacao +
                ", valorTotal=" + valorTotal +
                '}';
    }
}
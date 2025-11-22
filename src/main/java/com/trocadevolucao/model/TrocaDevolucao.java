package com.trocadevolucao.model;

import java.time.LocalDate;

public class TrocaDevolucao {

    // ----------------------------------------------------
    // ATRIBUTOS
    // ----------------------------------------------------
    private int id;
    private int produtoId;
    private String numeroPedido;
    private String tipoSolicitacao;
    private String motivo;
    private LocalDate dataSolicitacao;
    private String status;
    private int quantidade;
    private double valorTotal;
    private String observacoes;
    private LocalDate dataProcessamento;

    // ----------------------------------------------------
    // CONSTRUTORES (Corrigidos para flexibilidade)
    // ----------------------------------------------------

    // 1. CONSTRUTOR VAZIO (Default)
    public TrocaDevolucao() {
        // Inicialização padrão
    }

    // 2. CONSTRUTOR PRINCIPAL (8 PARÂMETROS - Usado para novas solicitações no NovaSolicitacaoController)
    public TrocaDevolucao(int produtoId, String numeroPedido, String tipoSolicitacao,
                          String motivo, LocalDate dataSolicitacao, String status,
                          int quantidade, double valorTotal) {
        this.produtoId = produtoId;
        this.numeroPedido = numeroPedido;
        this.tipoSolicitacao = tipoSolicitacao;
        this.motivo = motivo;
        this.dataSolicitacao = dataSolicitacao;
        this.status = status;
        this.quantidade = quantidade;
        this.valorTotal = valorTotal;
    }

    // 3. CONSTRUTOR COM ID (9 PARÂMETROS - Usado no TrocaDevolucaoController para o exemplo de carregamento)
    public TrocaDevolucao(int id, int produtoId, String numeroPedido, String tipoSolicitacao,
                          String motivo, LocalDate dataSolicitacao, String status,
                          int quantidade, double valorTotal) {
        this(produtoId, numeroPedido, tipoSolicitacao, motivo, dataSolicitacao, status, quantidade, valorTotal);
        this.id = id;
    }

    // ----------------------------------------------------
    // MÉTODOS DE CHECAGEM (isTroca, isDevolucao, isPendente - Corrigidos)
    // ----------------------------------------------------

    public boolean isTroca() {
        return this.tipoSolicitacao != null && this.tipoSolicitacao.equalsIgnoreCase("TROCA");
    }

    public boolean isDevolucao() {
        return this.tipoSolicitacao != null && this.tipoSolicitacao.equalsIgnoreCase("DEVOLUÇÃO");
    }

    public boolean isPendente() {
        return this.status != null && this.status.equalsIgnoreCase("PENDENTE");
    }

    // ----------------------------------------------------
    // MÉTODO DE LÓGICA (processar - CORREÇÃO para o Controller)
    // ----------------------------------------------------

    /**
     * Define o resultado do processamento da solicitação, atualizando o status e data.
     * Este método resolve o erro 'cannot find symbol: processar()'
     * @param tipoProcessamento O tipo de ação (ex: TROCA_PRODUTO ou ESTORNO).
     * @param produtoRecebido (Opcional) O nome do produto recebido na troca.
     * @param valorEstornado (Opcional) O valor estornado.
     */
    public void processar(String tipoProcessamento, String produtoRecebido, Integer valorEstornado) {
        // Altera o status para indicar que a solicitação foi tratada
        this.status = "PROCESSADA";
        this.dataProcessamento = LocalDate.now();

        // Lógica de logging ou de negócio simplificada
        if (tipoProcessamento.equals("TROCA_PRODUTO")) {
            System.out.println("Solicitação " + this.id + " processada como Troca por produto: " + produtoRecebido);
        } else if (tipoProcessamento.equals("ESTORNO")) {
            System.out.println("Solicitação " + this.id + " processada como Estorno no valor de: " + valorEstornado);
        }
    }

    // ----------------------------------------------------
    // GETTERS E SETTERS (Completos para o DAO)
    // ----------------------------------------------------

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getProdutoId() { return produtoId; }
    public void setProdutoId(int produtoId) { this.produtoId = produtoId; }

    public String getNumeroPedido() { return numeroPedido; }
    public void setNumeroPedido(String numeroPedido) { this.numeroPedido = numeroPedido; }

    public String getTipoSolicitacao() { return tipoSolicitacao; }
    public void setTipoSolicitacao(String tipoSolicitacao) { this.tipoSolicitacao = tipoSolicitacao; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public LocalDate getDataSolicitacao() { return dataSolicitacao; }
    public void setDataSolicitacao(LocalDate dataSolicitacao) { this.dataSolicitacao = dataSolicitacao; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

    public double getValorTotal() { return valorTotal; }
    public void setValorTotal(double valorTotal) { this.valorTotal = valorTotal; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    public LocalDate getDataProcessamento() { return dataProcessamento; }
    public void setDataProcessamento(LocalDate dataProcessamento) { this.dataProcessamento = dataProcessamento; }
}
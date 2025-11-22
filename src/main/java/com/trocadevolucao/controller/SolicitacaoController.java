package com.trocadevolucao.controller;

import com.trocadevolucao.dao.TrocaDevolucaoDAO;
import com.trocadevolucao.dao.TrocaDevolucaoDAOImpl;
import com.trocadevolucao.model.TrocaDevolucao;

import java.time.LocalDate;
import java.util.List;

/**
 * Classe Controller que atua como a ponte entre a View (main.java, FXML)
 * e o Model/DAO (TrocaDevolucao).
 */
public class SolicitacaoController {

    // Instância do DAO para acesso ao banco de dados
    private final TrocaDevolucaoDAO dao = new TrocaDevolucaoDAOImpl();

    /**
     * Carrega todas as solicitações do banco de dados.
     * @return Lista de TrocaDevolucao.
     */
    public List<TrocaDevolucao> carregarTodasSolicitacoes() {
        return dao.carregarTodos();
    }

    /**
     * Salva uma nova solicitação no banco de dados.
     * @param solicitacao O objeto TrocaDevolucao a ser salvo.
     * @return true se a operação for bem-sucedida.
     */
    public boolean salvarNovaSolicitacao(TrocaDevolucao solicitacao) {
        // A lógica de negócio pode ser inserida aqui antes de salvar
        return dao.salvar(solicitacao);
    }

    /**
     * Atualiza uma solicitação existente no banco de dados.
     * @param solicitacao O objeto TrocaDevolucao com os dados atualizados.
     * @return true se a operação for bem-sucedida.
     */
    public boolean atualizarSolicitacao(TrocaDevolucao solicitacao) {
        return dao.atualizar(solicitacao);
    }

    /**
     * Exclui uma solicitação do banco de dados.
     * @param id O ID da solicitação a ser excluída.
     * @return true se a operação for bem-sucedida.
     */
    public boolean excluirSolicitacao(int id) {
        return dao.excluir(id);
    }


    /**
     * MÉTODO CORRIGIDO: Modifica o status de uma solicitação existente e atualiza no DB.
     * Este método resolve o erro "Cannot resolve method 'modificarStatus'" na View.
     * * @param solicitacao O objeto TrocaDevolucao a ser atualizado.
     * @param novoStatus O novo status a ser aplicado (ex: "APROVADA", "REPROVADA", "PENDENTE").
     * @return true se a atualização no banco de dados for bem-sucedida.
     */
    public boolean modificarStatus(TrocaDevolucao solicitacao, String novoStatus) {
        // 1. Atualiza o objeto localmente
        solicitacao.setStatus(novoStatus);

        // 2. Se for aprovada/processada, define a data de processamento.
        if (novoStatus.equals("APROVADA") || novoStatus.equals("PROCESSADA")) {
            solicitacao.setDataProcessamento(LocalDate.now());
        }

        // 3. Persiste a alteração no banco de dados (reutiliza o método de atualização)
        return dao.atualizar(solicitacao);
    }
}
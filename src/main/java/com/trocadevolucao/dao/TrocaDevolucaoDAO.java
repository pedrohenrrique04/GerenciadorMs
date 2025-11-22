package com.trocadevolucao.dao;

import com.trocadevolucao.model.TrocaDevolucao;
import java.util.List;

/**
 * Interface DAO (Data Access Object) para a entidade TrocaDevolucao.
 * Define os métodos de CRUD (Create, Read, Update, Delete) que o DAOImpl deve implementar.
 * * A falta destes métodos definidos causou os erros "Cannot resolve method..."
 * no SolicitacaoController.
 */
public interface TrocaDevolucaoDAO {

    /**
     * NOVO MÉTODO: Salva uma nova solicitação no banco de dados.
     * @param solicitacao O objeto TrocaDevolucao a ser persistido.
     * @return true se o registro for inserido com sucesso.
     */
    boolean salvar(TrocaDevolucao solicitacao);

    /**
     * NOVO MÉTODO: Carrega todas as solicitações do banco de dados.
     * @return Uma lista de objetos TrocaDevolucao.
     */
    List<TrocaDevolucao> carregarTodos();

    /**
     * NOVO MÉTODO: Atualiza uma solicitação existente no banco de dados.
     * @param solicitacao O objeto TrocaDevolucao com os dados a serem atualizados.
     * @return true se a atualização for bem-sucedida.
     */
    boolean atualizar(TrocaDevolucao solicitacao);

    /**
     * NOVO MÉTODO: Remove uma solicitação do banco de dados pelo ID.
     * @param id O ID da solicitação a ser excluída.
     * @return true se a exclusão for bem-sucedida.
     */
    boolean excluir(int id);

    /**
     * (Opcional) Busca uma solicitação pelo ID.
     * @param id O ID da solicitação.
     * @return O objeto TrocaDevolucao correspondente ou null.
     */
    TrocaDevolucao buscarPorId(int id);
}
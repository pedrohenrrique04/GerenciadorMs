package controller;

import Dao.ProdutoDAO;
import Dao.TrocaDevolucaoDAO;
import Model.TrocaDevolucao;
import Model.ProdutoModel;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import javax.swing.JOptionPane;
// AS IMPORTAÇÕES DE view.* FORAM REMOVIDAS PARA RESOLVER O ERRO "cannot find symbol"

/**
 * Controlador para gerenciar as operações de Troca e Devolução.
 * ATENÇÃO: Métodos de interação com a View foram substituídos por JOptionPanes
 * ou comentários placeholders até que as classes de View sejam definidas.
 */
public class TrocaDevolucaoController {

    private final TrocaDevolucaoDAO trocaDevolucaoDAO;
    private final ProdutoDAO produtoDAO;
    // Tipo de View alterado para Object para compilar, use null para não depender do construtor
    private final Object viewPlaceholder;

    // O parâmetro 'view' agora é Object para compilar, mas NÃO DEVE ser usado como view.
    public TrocaDevolucaoController(Object view) {
        this.trocaDevolucaoDAO = new TrocaDevolucaoDAO();
        this.produtoDAO = new ProdutoDAO();
        this.viewPlaceholder = view;
        carregarTabelaTrocaDevolucao();
    }

    // Métodos utilitários para conversão de ID
    private int getIdInt(String idStr) {
        try {
            return Integer.parseInt(idStr.trim());
        } catch (NumberFormatException e) {
            return 0; // 0 indica ID inválido
        }
    }

    // Método para carregar a tabela na view
    public void carregarTabelaTrocaDevolucao() {
        List<TrocaDevolucao> trocasDevolucoes = trocaDevolucaoDAO.listarTodos();
        // Ação de carregar tabela na view removida - A view deve implementar seu próprio método
    }

    // Método de busca principal
    public void buscarTrocaDevolucao(String tipoBusca, String valor) {
        List<TrocaDevolucao> resultados;
        if ("ID".equalsIgnoreCase(tipoBusca)) {
            int id = getIdInt(valor);
            if (id > 0) {
                Optional<TrocaDevolucao> td = trocaDevolucaoDAO.buscarPorId(id);
                if (td.isPresent()) {
                    resultados = List.of(td.get());
                } else {
                    resultados = List.of();
                    JOptionPane.showMessageDialog(null, "Solicitação de Troca/Devolução não encontrada.", "Busca", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                resultados = trocaDevolucaoDAO.listarTodos();
            }
        } else {
            resultados = trocaDevolucaoDAO.listarTodos();
        }
        // Ação de carregar tabela na view removida
    }

    // --- Lógica de Cadastro e Edição ---

    public void abrirCadastro(TrocaDevolucao td) {
        // Implementação da abertura de tela de cadastro removida para evitar o erro "cannot find symbol".
        if (td != null) {
            JOptionPane.showMessageDialog(null, "Abrir tela de Edição para ID: " + td.getId());
        } else {
            JOptionPane.showMessageDialog(null, "Abrir tela de Novo Cadastro.");
        }
    }

    public void salvarTrocaDevolucao(TrocaDevolucao td) {
        if (td.getIdProduto() <= 0 || td.getNumeroPedido().isEmpty() || td.getTipo().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Preencha todos os campos obrigatórios.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Optional<ProdutoModel> produtoOpt = produtoDAO.buscarPorId(td.getIdProduto());
        if (produtoOpt.isEmpty()) {
            JOptionPane.showMessageDialog(null, "O ID do Produto (" + td.getIdProduto() + ") não foi encontrado no estoque.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (td.getId() == 0) {
            td.setStatus("PENDENTE");
            td.setDataSolicitacao(LocalDate.now());
        }

        trocaDevolucaoDAO.salvar(td);

        JOptionPane.showMessageDialog(null, "Solicitação salva com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        carregarTabelaTrocaDevolucao();
    }

    // --- Lógica de Processamento ---

    public void abrirProcessamento(String idStr) {
        int id = getIdInt(idStr);
        if (id <= 0) {
            JOptionPane.showMessageDialog(null, "Selecione uma linha válida para processar.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Optional<TrocaDevolucao> tdOpt = trocaDevolucaoDAO.buscarPorId(id);

        // CORRIGIDO: Uso correto do Optional
        if (tdOpt.isPresent()) {
            TrocaDevolucao td = tdOpt.get();
            ProdutoModel produto = produtoDAO.buscarPorId(td.getIdProduto()).orElse(null);

            if (produto == null) {
                JOptionPane.showMessageDialog(null, "Produto associado à solicitação não encontrado. Não é possível processar.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Implementação da abertura de tela de processamento removida
            JOptionPane.showMessageDialog(null, "Abrir tela de Processamento para ID: " + td.getId());

        } else {
            JOptionPane.showMessageDialog(null, "Solicitação não encontrada.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- Lógica de Exclusão ---

    public void deletarTrocaDevolucao(String idStr) {
        int id = getIdInt(idStr);
        if (id <= 0) {
            JOptionPane.showMessageDialog(null, "Selecione uma linha válida para exclusão.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirmacao = JOptionPane.showConfirmDialog(null,
                "Tem certeza que deseja deletar a solicitação ID: " + id + "?",
                "Confirmação de Exclusão",
                JOptionPane.YES_NO_OPTION);

        if (confirmacao == JOptionPane.YES_OPTION) {
            boolean sucesso = trocaDevolucaoDAO.deletar(id);
            if (sucesso) {
                JOptionPane.showMessageDialog(null, "Solicitação deletada com sucesso.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                carregarTabelaTrocaDevolucao();
            } else {
                JOptionPane.showMessageDialog(null, "Erro ao deletar a solicitação. Tente novamente.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // --- Lógica de Aprovação e Rejeição ---

    public void processarAprovacao(TrocaDevolucao td, ProdutoModel produto) {
        if (!"PENDENTE".equals(td.getStatus())) {
            JOptionPane.showMessageDialog(null, "Apenas solicitações PENDENTES podem ser aprovadas.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int novoEstoque = produto.getQuantidade() + td.getQuantidade();
        produto.setQuantidade(novoEstoque);
        produtoDAO.salvar(produto);

        td.setStatus("PROCESSADA");
        // CORREÇÃO: Converte LocalDate para LocalDateTime
        td.setDataProcessamento(LocalDate.now().atStartOfDay());
        trocaDevolucaoDAO.salvar(td);

        JOptionPane.showMessageDialog(null,
                td.getTipo() + " ID " + td.getId() + " APROVADA e Produto " + produto.getNome() + " retornado ao estoque.",
                "Processamento Concluído",
                JOptionPane.INFORMATION_MESSAGE);
        carregarTabelaTrocaDevolucao();
    }

    public void processarRejeicao(TrocaDevolucao td) {
        if (!"PENDENTE".equals(td.getStatus())) {
            JOptionPane.showMessageDialog(null, "Apenas solicitações PENDENTES podem ser rejeitadas.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        td.setStatus("REJEITADA");
        // CORREÇÃO: Converte LocalDate para LocalDateTime
        td.setDataProcessamento(LocalDate.now().atStartOfDay());
        trocaDevolucaoDAO.salvar(td);

        JOptionPane.showMessageDialog(null,
                td.getTipo() + " ID " + td.getId() + " REJEITADA.",
                "Processamento Concluído",
                JOptionPane.INFORMATION_MESSAGE);
        carregarTabelaTrocaDevolucao();
    }

    /**
     * Busca um TrocaDevolucao pelo ID.
     * @param id O ID da TrocaDevolucao.
     * @return Um Optional contendo a TrocaDevolucao, se encontrado.
     * CORREÇÃO: O método retorna Optional<TrocaDevolucao>
     */
    public Optional<TrocaDevolucao> buscarPorId(int id) {
        return trocaDevolucaoDAO.buscarPorId(id);
    }
}
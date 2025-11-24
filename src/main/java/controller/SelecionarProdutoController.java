package controller;

import Dao.ProdutoDAO; // CORREÇÃO: Importação da classe ProdutoDAO
import Model.ProdutoModel;
import java.util.List;
import java.util.Optional;
import javax.swing.JOptionPane;
// Assumindo que há uma interface ou view que este controlador gerencia

/**
 * Controlador responsável por gerenciar a seleção de produtos,
 * geralmente usado em contextos como adicionar itens a uma venda.
 */
public class SelecionarProdutoController {

    private final ProdutoDAO produtoDAO;
    // Tipicamente, este controlador teria uma referência para a View ou outro Controller (e.g., VendaController)
    // private final VendaController vendaController;

    // Constructor que inicializa a DAO
    public SelecionarProdutoController() {
        // Linha 29:19 e 29:47 (onde o erro foi reportado) agora devem ser resolvidas
        this.produtoDAO = new ProdutoDAO();
        // this.vendaController = vendaController; // Se aplicável
    }

    /**
     * Lista todos os produtos disponíveis no banco de dados.
     * @return Lista de ProdutoModel.
     */
    public List<ProdutoModel> listarProdutos() {
        return produtoDAO.listarTodos();
    }

    /**
     * Busca um produto por ID.
     * @param id O ID do produto.
     * @return Um Optional contendo o ProdutoModel, se encontrado.
     */
    public Optional<ProdutoModel> buscarProdutoPorId(int id) {
        return produtoDAO.buscarPorId(id);
    }

    /**
     * Lógica para adicionar o produto selecionado a um carrinho de compras.
     * Este é um esqueleto e deve ser implementado de acordo com a regra de negócio.
     * * @param idProduto ID do produto a ser adicionado.
     * @param quantidade Quantidade a ser adicionada.
     */
    public void adicionarProdutoSelecionado(int idProduto, int quantidade) {
        Optional<ProdutoModel> produtoOpt = buscarProdutoPorId(idProduto);

        if (produtoOpt.isPresent()) {
            ProdutoModel produto = produtoOpt.get();

            if (produto.getQuantidade() >= quantidade) {
                // Aqui você chamaria o método no VendaController ou em outra classe
                // responsável por gerenciar o carrinho de vendas.
                // Exemplo: vendaController.adicionarProdutoAoCarrinho(idProduto, quantidade);
                JOptionPane.showMessageDialog(null,
                        "Produto '" + produto.getNome() + "' adicionado ao carrinho (" + quantidade + " unidades).",
                        "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null,
                        "Estoque insuficiente para o produto: " + produto.getNome(),
                        "Erro", JOptionPane.ERROR_MESSAGE);
            }

        } else {
            JOptionPane.showMessageDialog(null, "Produto não encontrado.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
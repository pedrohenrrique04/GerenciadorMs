package controller;

import Dao.VendaDAO;
import Dao.ProdutoDAO;
import Model.Venda;
import Model.CartItem;
import Model.ProdutoModel;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.swing.JOptionPane;
// Imports da View mantidos como placeholders se existirem

/**
 * Controlador para gerenciar as operações de Vendas.
 */
public class VendaController { // <-- Abertura da Classe

    private final VendaDAO vendaDAO;
    private final ProdutoDAO produtoDAO;
    private List<CartItem> carrinho;
    private double totalVenda;

    // Tipo de View alterado para Object
    public VendaController(Object view) {
        this.vendaDAO = new VendaDAO();
        this.produtoDAO = new ProdutoDAO();
        this.carrinho = new ArrayList<>();
        this.totalVenda = 0.0;
        // Inicialização da view removida ou adaptada
    }

    public void adicionarProdutoAoCarrinho(int idProduto, int quantidade) { // <-- Abertura do Método
        if (quantidade <= 0) {
            JOptionPane.showMessageDialog(null, "A quantidade deve ser maior que zero.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Optional<ProdutoModel> produtoOpt = produtoDAO.buscarPorId(idProduto);

        if (produtoOpt.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Produto não encontrado.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        ProdutoModel produto = produtoOpt.get();

        if (produto.getQuantidade() < quantidade) {
            JOptionPane.showMessageDialog(null, "Estoque insuficiente. Disponível: " + produto.getQuantidade(), "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        CartItem itemExistente = carrinho.stream()
                .filter(item -> item.getIdProduto() == idProduto)
                .findFirst()
                .orElse(null);

        if (itemExistente != null) {
            itemExistente.setQuantidade(itemExistente.getQuantidade() + quantidade);
        } else {
            carrinho.add(new CartItem(idProduto, produto.getNome(), quantidade, produto.getPrecoVenda()));
        }

        recalcularTotal();
    } // <-- Fechamento do Método

    public void removerItemDoCarrinho(int index) { // <-- Abertura do Método
        if (index >= 0 && index < carrinho.size()) {
            carrinho.remove(index);
            recalcularTotal();
        }
    } // <-- Fechamento do Método

    private void recalcularTotal() { // <-- Abertura do Método
        totalVenda = carrinho.stream()
                .mapToDouble(item -> item.getPrecoVenda() * item.getQuantidade())
                .sum();
    } // <-- Fechamento do Método

    // Método para finalizar a venda com dados completos
    public void finalizarVenda(String cpf, int idFuncionario, double valorTotal, double valorPago, String tipoPagamento, double troco) { // <-- Abertura do Método
        if (carrinho.isEmpty()) {
            JOptionPane.showMessageDialog(null, "O carrinho está vazio.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Chamada de construtor ajustada para a assinatura Venda(List<CartItem>, double)
        Venda novaVenda = new Venda(carrinho, totalVenda);

        // Chamada de método ajustada para salvar()
        vendaDAO.salvar(novaVenda);

        // Atualizar estoque
        for (CartItem item : carrinho) {
            produtoDAO.buscarPorId(item.getIdProduto()).ifPresent(p -> {
                p.setQuantidade(p.getQuantidade() - item.getQuantidade());
                produtoDAO.salvar(p);
            });
        }

        JOptionPane.showMessageDialog(null, "Venda finalizada com sucesso! Total: R$ " + totalVenda, "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        limparCarrinho();
    } // <-- Fechamento do Método

    // Método para finalizar a venda simplificado
    public void finalizarVendaSimplificada(double valorTotal, String tipoPagamento) { // <-- Abertura do Método
        if (carrinho.isEmpty()) {
            JOptionPane.showMessageDialog(null, "O carrinho está vazio.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Chamada de construtor ajustada para a assinatura Venda(List<CartItem>, double)
        Venda novaVenda = new Venda(carrinho, totalVenda);

        // Chamada de método ajustada para salvar()
        vendaDAO.salvar(novaVenda);

        // Atualizar estoque
        for (CartItem item : carrinho) {
            produtoDAO.buscarPorId(item.getIdProduto()).ifPresent(p -> {
                p.setQuantidade(p.getQuantidade() - item.getQuantidade());
                produtoDAO.salvar(p);
            });
        }

        JOptionPane.showMessageDialog(null, "Venda finalizada com sucesso! Total: R$ " + totalVenda, "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        limparCarrinho();
    } // <-- Fechamento do Método

    public void limparCarrinho() { // <-- Abertura do Método
        carrinho.clear();
        recalcularTotal();
    } // <-- Fechamento do Método

    public List<CartItem> getCarrinho() { // <-- Abertura do Método
        return carrinho;
    } // <-- Fechamento do Método

    public double getTotalVenda() { // <-- Abertura do Método
        return totalVenda;
    } // <-- Fechamento do Método

    public List<Venda> listarVendas() { // <-- Abertura do Método
        return vendaDAO.listarTodos();
    } // <-- Fechamento do Método

    public Optional<Venda> buscarVendaPorId(int id) { // <-- Abertura do Método
        return vendaDAO.buscarPorId(id);
    } // <-- Fechamento do Método

    public void deletarVenda(int id) { // <-- Abertura do Método
        if (vendaDAO.deletar(id)) {
            JOptionPane.showMessageDialog(null, "Venda deletada com sucesso.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "Erro ao deletar venda.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    } // <-- Fechamento do Método
} // <-- Fechamento da Classe
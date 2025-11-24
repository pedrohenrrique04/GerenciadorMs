package controller;

import Model.Venda;
import Dao.VendaDAO;
import java.util.List;
import Model.CartItem; // Importação assumida

/**
 * Controlador responsável por mediar as operações de venda entre a View e o VendaDAO.
 * A implementação aqui é simplificada para apenas demonstrar a correção do erro.
 */
public class VendaController {

    private final VendaDAO vendaDAO;

    public VendaController() {
        this.vendaDAO = new VendaDAO();
    }

    // Método que estava causando o erro de construtor (agora resolvido pela Model.Venda)
    public boolean criarVenda(String cliente, int id, double totalProduto, double desconto, String formaPagamento, double acrescimo) {

        // 1. Resolve o erro de construtor (Venda agora tem 6 argumentos)
        Venda novaVenda = new Venda(
                cliente,
                id,
                totalProduto,
                desconto,
                formaPagamento,
                acrescimo
        );

        // 2. Resolve o erro do método: registrarVenda -> salvarVenda
        return vendaDAO.salvarVenda(novaVenda);
    }

    // Exemplo de outro método que poderia ser chamado pela tela de PDV
    public boolean criarVendaPDV(double totalVenda, String formaPagamento, List<CartItem> itens) {
        Venda novaVenda = new Venda(totalVenda, formaPagamento, itens);
        return vendaDAO.salvarVenda(novaVenda);
    }
}
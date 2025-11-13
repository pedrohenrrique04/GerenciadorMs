package controller;

import Dao.VendaDAO;
import Model.Venda;
import javax.swing.*;

public class VendaController {

    private VendaDAO vendaDAO = new VendaDAO();

    public void finalizarVenda(String produto, String qtd, String preco, String desconto, String pagamento) {
        try {
            int quantidade = Integer.parseInt(qtd);
            double valor = Double.parseDouble(preco);
            double desc = desconto.isEmpty() ? 0 : Double.parseDouble(desconto);

            double total = valor * quantidade;
            total -= total * (desc / 100);

            Venda v = new Venda(produto, quantidade, valor, desc, pagamento, total);
            vendaDAO.registrarVenda(v);

            JOptionPane.showMessageDialog(null,
                    "Venda registrada com sucesso!\nProduto: " + produto +
                            "\nTotal: R$ " + String.format("%.2f", total)
            );

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro ao registrar venda: " + e.getMessage());
        }
    }
}

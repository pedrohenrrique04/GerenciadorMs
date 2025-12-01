package controller;

import Model.Venda;
import Dao.VendaDAO;
import Dao.ProdutoDAO; // 🚨 Importação necessária
import Model.CartItem;
import Model.ProdutoModel; // 🚨 Importação necessária

import java.util.List;

import javafx.collections.FXCollections; // 🚨 Importação necessária
import javafx.collections.ObservableList; // 🚨 Importação necessária
import javafx.fxml.FXML; // 🚨 Importação necessária para usar @FXML (se aplicável)
import javafx.scene.control.ListView; // 🚨 Importação do componente de lista (AJUSTE se for TableView)

/**
 * Controlador responsável por mediar as operações de venda entre a View e o VendaDAO.
 */
public class VendaController {

    // 🚨 1. ADICIONE O CAMPO FXML DA SUA LISTA DE PRODUTOS
    // Se a lista de produtos é um componente visível na sua tela de vendas:
    // AJUSTE o nome da variável se for diferente no seu código.
    @FXML
    private ListView<ProdutoModel> listaProdutosDisponiveis;

    private final VendaDAO vendaDAO;
    private final ProdutoDAO produtoDAO; // 🚨 Adicionado ProdutoDAO
    private ObservableList<ProdutoModel> produtosObservableList; // 🚨 Lista observável para o JavaFX

    public VendaController() {
        this.vendaDAO = new VendaDAO();
        this.produtoDAO = new ProdutoDAO(); // Inicializa o ProdutoDAO
    }

    // 🚨 2. ADICIONE O MÉTODO DE INICIALIZAÇÃO DA INTERFACE
    // Este método é chamado automaticamente após a injeção dos componentes FXML.
    @FXML
    public void initialize() {
        // Inicializa a lista observável
        produtosObservableList = FXCollections.observableArrayList();

        // Associa a lista ao componente visual
        if (listaProdutosDisponiveis != null) {
            listaProdutosDisponiveis.setItems(produtosObservableList);
        }

        // 🚨 CHAMA O RECARREGAMENTO FORÇADO
        recarregarProdutosDoBanco();
    }

    // 🚨 3. MÉTODO CHAVE DA CORREÇÃO: Força o recarregamento do banco
    public void recarregarProdutosDoBanco() {
        if (produtosObservableList == null) {
            // Garante a inicialização
            produtosObservableList = FXCollections.observableArrayList();
        }

        // 1. Limpa a lista atual (libera o cache)
        produtosObservableList.clear();

        // 2. Busca dados frescos do banco (incluindo o novo produto)
        List<ProdutoModel> produtosDoBanco = produtoDAO.listarTodos();

        // 3. Adiciona os novos dados à lista da UI
        produtosObservableList.addAll(produtosDoBanco);
    }

    // --- Métodos Existentes (Mantidos) ---

    public boolean criarVenda(String cliente, int id, double totalProduto, double desconto, String formaPagamento, double acrescimo) {
        // ... (lógica existente) ...
        Venda novaVenda = new Venda(cliente, id, totalProduto, desconto, formaPagamento, acrescimo);
        return vendaDAO.salvarVenda(novaVenda);
    }

    public boolean criarVendaPDV(double totalVenda, String formaPagamento, List<CartItem> itens) {
        // ... (lógica existente) ...
        Venda novaVenda = new Venda(totalVenda, formaPagamento, itens);
        return vendaDAO.salvarVenda(novaVenda);
    }

    // 🚨 4. ADICIONE UM MÉTODO QUE DEVE SER CHAMADO APÓS CADA CADASTRO BEM-SUCEDIDO
    // Se o seu código que cadastra um produto chama este controller,
    // ele pode chamar este método para atualizar.
    public void notificarCadastroDeProduto() {
        recarregarProdutosDoBanco();
    }
}
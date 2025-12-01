package controller;

import Dao.ProdutoDAO;
import Model.ProdutoModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class SelecionarProdutoController implements Initializable {

    // --- Componentes FXML ---
    @FXML
    private ListView<ProdutoModel> listaProdutos;
    @FXML
    private TextField campoPesquisa;
    @FXML
    private Button adicionarAoCarrinhoBtn;

    // --- Variáveis de Dados ---
    private final ProdutoDAO produtoDAO = new ProdutoDAO();
    private ObservableList<ProdutoModel> produtosObservableList;
    private ProdutoModel produtoSelecionado;

    // --- MÉTODOS DE INICIALIZAÇÃO ---

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Inicializa a lista observável
        produtosObservableList = FXCollections.observableArrayList();

        // Associa a lista à sua ListView
        listaProdutos.setItems(produtosObservableList);

        // 🚨 Configura o listener para a pesquisa (filtro)
        campoPesquisa.textProperty().addListener((obs, oldValue, newValue) -> filtrarProdutos(newValue));

        // 🚨 Configura a ação de seleção
        listaProdutos.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            produtoSelecionado = newVal;
            adicionarAoCarrinhoBtn.setDisable(newVal == null);
        });

        // O Initialize agora apenas prepara a lista. O recarregamento será forçado.
    }

    // --- MÉTODO CHAVE DA CORREÇÃO ---

    /**
     * 🚨 Recarrega a lista de produtos do banco de dados, corrigindo o problema de cache.
     * Deve ser chamado pelo VendaController ANTES de a janela ser exibida.
     */
    public void recarregarProdutosDoBanco() {
        if (listaProdutos == null) return; // Garante que os componentes FXML foram carregados

        // 1. Limpa a lista atual (libera o cache)
        produtosObservableList.clear();

        // 2. Busca dados frescos do banco (chama o listarTodos() que está OK)
        List<ProdutoModel> produtosDoBanco = produtoDAO.listarTodos();

        // 3. Adiciona os novos dados à lista da UI
        produtosObservableList.addAll(produtosDoBanco);
    }

    // --- MÉTODOS DE AÇÃO ---

    /**
     * Lógica para filtrar a lista de produtos baseada no texto digitado.
     */
    private void filtrarProdutos(String filtro) {
        if (filtro == null || filtro.isEmpty()) {
            listaProdutos.setItems(produtosObservableList);
            return;
        }

        String filtroLowerCase = filtro.toLowerCase();

        // Filtra a lista observável completa (para evitar buscar no banco a cada caractere)
        List<ProdutoModel> listaFiltrada = produtosObservableList.stream()
                .filter(produto -> produto.getNome().toLowerCase().contains(filtroLowerCase))
                .collect(Collectors.toList());

        listaProdutos.setItems(FXCollections.observableArrayList(listaFiltrada));
    }

    /**
     * Ação ao clicar no botão 'Adicionar ao Carrinho'.
     */
    @FXML
    private void adicionarAoCarrinho() {
        if (produtoSelecionado != null) {
            // 🚨 AQUI VOCÊ DEVE CHAMAR A LÓGICA DO SEU VENDACONTROLLER/PDV
            // PARA ADICIONAR O 'produtoSelecionado' AO CARRINHO.

            System.out.println("Adicionando ao carrinho: " + produtoSelecionado.getNome());

            // Fecha a janela após a seleção
            Stage stage = (Stage) adicionarAoCarrinhoBtn.getScene().getWindow();
            stage.close();
        }
    }

    /**
     * Retorna o produto que foi selecionado (se precisar ser usado após o fechamento).
     */
    public ProdutoModel getProdutoSelecionado() {
        return produtoSelecionado;
    }
}
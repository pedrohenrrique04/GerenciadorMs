package view;

import Dao.ProdutoDAO;
import Dao.VendaDAO;
import Model.CartItem;
import Model.ProdutoModel;
import Model.NotificationType;
import Model.Venda;
import service.NotificationService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.ArrayList;

/**
 * Controller da tela de Realizar Venda.
 * Gerencia a busca de produtos, adição ao carrinho e finalização da transação.
 */
public class TelaRealizarVenda {

    // DAOs e Services (Instanciação de dependências)
    private final ProdutoDAO produtoDAO = new ProdutoDAO();
    private final VendaDAO vendaDAO = new VendaDAO();
    private final NotificationService notificationService = NotificationService.getInstance();

    // Estado da Aplicação
    private final ObservableList<CartItem> carrinho = FXCollections.observableArrayList();
    private double valorTotal = 0.0;

    // Variável para armazenar o produto atualmente em foco após a busca
    private ProdutoModel produtoSelecionado = null;

    // FXML - Produtos (Componentes da interface)
    @FXML private TextField buscaProdutoField;
    @FXML private Button buscarProdutoBtn;
    @FXML private Label produtoNomeLabel;
    @FXML private Label produtoPrecoLabel;
    @FXML private Label produtoEstoqueLabel;
    @FXML private TextField quantidadeVendaField;
    @FXML private Button adicionarAoCarrinhoBtn;

    // FXML - Carrinho (TableView)
    @FXML private TableView<CartItem> carrinhoTableView;
    @FXML private TableColumn<CartItem, String> colunaNome;
    @FXML private TableColumn<CartItem, Double> colunaPreco;
    @FXML private TableColumn<CartItem, Integer> colunaQuantidade;
    @FXML private TableColumn<CartItem, Double> colunaSubtotal;

    // FXML - Finalização
    @FXML private Label totalVendaLabel;
    @FXML private Button finalizarVendaBtn;
    @FXML private Button removerItemBtn;

    // Inicialização da tela (Chamado após o FXML ser carregado)
    @FXML
    public void initialize() {
        // Mapeamento das colunas do TableView com as propriedades do CartItem
        // Nota: O método .asObject() é necessário para tipos primitivos em TableColumn
        colunaNome.setCellValueFactory(cellData -> cellData.getValue().nomeProperty());
        colunaPreco.setCellValueFactory(cellData -> cellData.getValue().precoUnitarioProperty().asObject());
        colunaQuantidade.setCellValueFactory(cellData -> cellData.getValue().quantidadeProperty().asObject());
        colunaSubtotal.setCellValueFactory(cellData -> cellData.getValue().subtotalProperty().asObject());

        // Define a lista de itens do carrinho na TableView
        carrinhoTableView.setItems(carrinho);

        // Configuração de formatação monetária (R$ X.XX)
        configurarFormatacaoMonetaria(colunaPreco);
        configurarFormatacaoMonetaria(colunaSubtotal);

        // Formata o campo de quantidade para aceitar apenas números (Inteiro)
        formatarParaInteiro(quantidadeVendaField);

        // Inicializa o total
        atualizarTotalVenda();
    }

    // Método auxiliar para formatar colunas como moeda R$
    private void configurarFormatacaoMonetaria(TableColumn<CartItem, Double> coluna) {
        coluna.setCellFactory(tc -> new TableCell<CartItem, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    // Formatação para moeda Brasileira
                    setText(String.format("R$ %.2f", price));
                }
            }
        });
    }

    // --- Ações FXML ---

    @FXML
    private void buscarProduto() {
        String termo = buscaProdutoField.getText();
        produtoSelecionado = null; // Limpa o produto anterior

        if (termo == null || termo.trim().isEmpty()) {
            exibirAlerta(Alert.AlertType.WARNING, "Busca Vazia", "Por favor, digite o ID do produto.");
            return;
        }

        try {
            // Tenta buscar por ID
            int id = Integer.parseInt(termo);
            produtoSelecionado = produtoDAO.buscarPorId(id);
        } catch (NumberFormatException e) {
            exibirAlerta(Alert.AlertType.INFORMATION, "Busca Inválida", "A busca deve ser feita pelo ID numérico do produto.");
            return;
        }

        if (produtoSelecionado != null) {
            exibirDetalhesProduto(produtoSelecionado);
        } else {
            limparDetalhesProduto();
            exibirAlerta(Alert.AlertType.ERROR, "Produto Não Encontrado", "Nenhum produto encontrado com o ID fornecido.");
        }
    }

    @FXML
    private void adicionarAoCarrinho() {
        if (produtoSelecionado == null) {
            exibirAlerta(Alert.AlertType.ERROR, "Erro", "Busque e selecione um produto válido primeiro.");
            return;
        }

        int quantidade;
        try {
            quantidade = Integer.parseInt(quantidadeVendaField.getText());
            if (quantidade <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            exibirAlerta(Alert.AlertType.WARNING, "Quantidade Inválida", "A quantidade deve ser um número inteiro maior que zero.");
            return;
        }

        // Verifica estoque disponível
        int estoqueAtual = produtoSelecionado.getQuantidade();
        if (quantidade > estoqueAtual) {
            notificationService.sendNotification(NotificationType.ALERTA, "Estoque baixo para " + produtoSelecionado.getNome() + ". Apenas " + estoqueAtual + " em estoque.");
            exibirAlerta(Alert.AlertType.WARNING, "Estoque Insuficiente", "Estoque insuficiente. Máximo disponível: " + estoqueAtual);
            return;
        }

        // Verifica se o item já está no carrinho
        CartItem itemExistente = carrinho.stream()
                .filter(item -> item.getProduto().getId() == produtoSelecionado.getId())
                .findFirst()
                .orElse(null);

        int novaQuantidadeTotalNoCarrinho = quantidade;

        if (itemExistente != null) {
            // Atualiza quantidade
            novaQuantidadeTotalNoCarrinho = itemExistente.getQuantidade() + quantidade;

            if (novaQuantidadeTotalNoCarrinho > estoqueAtual) {
                exibirAlerta(Alert.AlertType.WARNING, "Estoque Excedido", "A quantidade total no carrinho (" + novaQuantidadeTotalNoCarrinho + ") excede o estoque disponível.");
                return;
            }
            itemExistente.setQuantidade(novaQuantidadeTotalNoCarrinho);
            carrinhoTableView.refresh(); // Força a atualização da linha na TableView
        } else {
            // Adiciona novo item
            carrinho.add(new CartItem(produtoSelecionado, quantidade));
        }

        atualizarTotalVenda();
        limparDetalhesProduto(); // Limpa a área de detalhes após adicionar ao carrinho
    }

    @FXML
    private void removerItem() {
        CartItem itemSelecionado = carrinhoTableView.getSelectionModel().getSelectedItem();
        if (itemSelecionado != null) {
            carrinho.remove(itemSelecionado);
            atualizarTotalVenda();
        } else {
            exibirAlerta(Alert.AlertType.WARNING, "Nenhuma Seleção", "Selecione um item no carrinho para remover.");
        }
    }

    @FXML
    private void finalizarVenda() {
        if (carrinho.isEmpty()) {
            exibirAlerta(Alert.AlertType.ERROR, "Carrinho Vazio", "O carrinho está vazio. Adicione itens para finalizar a venda.");
            return;
        }

        // 1. Cria o objeto Venda
        // Cria uma cópia da lista do carrinho para a Venda, pois o carrinho pode ser limpo
        Venda novaVenda = new Venda(new ArrayList<>(carrinho), valorTotal);

        // 2. Registra no DAO
        boolean sucesso = vendaDAO.registrarVenda(novaVenda);

        if (sucesso) {
            // 3. Atualiza o estoque no ProdutoDAO
            for (CartItem item : carrinho) {
                ProdutoModel produto = item.getProduto();
                // Reduz o estoque pela quantidade vendida
                produto.setQuantidade(produto.getQuantidade() - item.getQuantidade());
                produtoDAO.atualizar(produto);
            }

            // 4. Envia notificação de sucesso
            notificationService.sendNotification(NotificationType.INFO,
                    "Venda ID " + novaVenda.getId() + " finalizada. Total: R$ " + String.format("%.2f", valorTotal));

            // 5. Limpa a tela
            carrinho.clear();
            atualizarTotalVenda();
            exibirAlerta(Alert.AlertType.INFORMATION, "Venda Concluída", "Venda finalizada com sucesso! ID: " + novaVenda.getId());
        } else {
            notificationService.sendNotification(NotificationType.CRITICO, "Falha ao registrar venda!");
            exibirAlerta(Alert.AlertType.ERROR, "Erro", "Falha ao registrar a venda no sistema.");
        }
    }

    // --- Métodos Auxiliares ---

    private void exibirDetalhesProduto(ProdutoModel produto) {
        produtoNomeLabel.setText(produto.getNome());
        produtoPrecoLabel.setText(String.format("R$ %.2f", produto.getPrecoVenda()));
        produtoEstoqueLabel.setText(String.valueOf(produto.getQuantidade()));
        quantidadeVendaField.setText("1");
    }

    private void limparDetalhesProduto() {
        produtoSelecionado = null;
        produtoNomeLabel.setText("N/A");
        produtoPrecoLabel.setText("R$ 0.00");
        produtoEstoqueLabel.setText("0");
        buscaProdutoField.clear();
        quantidadeVendaField.clear();
    }

    private void atualizarTotalVenda() {
        // Recalcula o total somando os subtotais de todos os itens do carrinho
        valorTotal = carrinho.stream().mapToDouble(CartItem::getSubtotal).sum();
        totalVendaLabel.setText(String.format("Total: R$ %.2f", valorTotal));
    }

    private void exibirAlerta(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void formatarParaInteiro(TextField field) {
        // Listener para garantir que apenas dígitos sejam inseridos
        field.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                field.setText(oldValue);
            }
        });
    }
}
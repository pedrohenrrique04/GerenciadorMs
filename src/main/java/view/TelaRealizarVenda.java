package view;

import Model.CartItem;
import Model.Produto;
import Model.Venda;
import Model.NotificationType;
import Dao.NotificacaoDAO;
import Dao.ProdutoDAO; // Importação do DAO de Produto
import Dao.VendaDAO; // Importação do DAO de Venda

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.text.DecimalFormat;
import java.util.Optional;
import java.util.stream.Collectors;

public class TelaRealizarVenda {

    // --- ESTILIZAÇÃO ---
    private static final String COR_FUNDO = "#f7f9fc";
    private static final String COR_PRINCIPAL = "#007bff";
    private static final String COR_PRINCIPAL_HOVER = "#0056b3";
    private static final String COR_SUCESSO = "#28a745";
    private static final String COR_SUCESSO_HOVER = "#218838";
    private static final String COR_PERIGO = "#dc3545";
    private static final String COR_TEXTO_TITULO = "#333";
    private static final String ESTILO_BOTAO_PADRAO = "-fx-background-color: %s; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-background-radius: 8px; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 5, 0.1, 0, 2);";
    private static final String ESTILO_BOTAO_HOVER = "-fx-background-color: %s; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-background-radius: 8px; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 8, 0.2, 0, 3);";

    private final ObservableList<CartItem> carrinho = FXCollections.observableArrayList();
    private final DecimalFormat df = new DecimalFormat("R$ #,##0.00");
    private Label lblSubtotal, lblTotal;
    private ToggleGroup grupoPagamento;

    // CARREGA PRODUTOS DO BANCO VIA DAO
    private final ObservableList<Produto> produtosDoBanco = FXCollections.observableArrayList(
            new ProdutoDAO().listarTodos()
    );

    // --- MÉTODO PRINCIPAL PARA O DASHBOARD ---
    public BorderPane getTela() {
        // Exibe um alerta se a lista de produtos estiver vazia (problema de conexão ou banco vazio)
        if (produtosDoBanco.isEmpty()) {
            Label lblErro = new Label("❌ ERRO: Não foi possível carregar os produtos do banco. Verifique a ConnectionFactory e o driver JDBC.");
            lblErro.setTextFill(Color.web(COR_PERIGO));
            lblErro.setFont(Font.font("Arial", FontWeight.BOLD, 18));
            BorderPane errorPane = new BorderPane(lblErro);
            errorPane.setPadding(new Insets(50));
            return errorPane;
        }

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + COR_FUNDO + ";");
        root.setPadding(new Insets(25));

        Label lblTitulo = new Label("Ponto de Venda");
        lblTitulo.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        lblTitulo.setTextFill(Color.web(COR_TEXTO_TITULO));
        BorderPane.setAlignment(lblTitulo, Pos.CENTER_LEFT);
        BorderPane.setMargin(lblTitulo, new Insets(0, 0, 20, 0));
        root.setTop(lblTitulo);

        TableView<CartItem> tabela = createCarrinhoTable();
        VBox.setVgrow(tabela, Priority.ALWAYS);
        tabela.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-font-size: 13px;");
        root.setCenter(tabela);

        GridPane painelInferior = criarPainelInferior();
        root.setBottom(painelInferior);

        return root;
    }

    private GridPane criarPainelInferior() {
        GridPane painelInferior = new GridPane();
        painelInferior.setHgap(20);
        painelInferior.setPadding(new Insets(20, 0, 0, 0));

        Button btnAdd = new Button("+ Adicionar Produto");
        String cssAdd = String.format(ESTILO_BOTAO_PADRAO, COR_PRINCIPAL);
        btnAdd.setStyle(cssAdd);
        btnAdd.setOnMouseEntered(e -> btnAdd.setStyle(String.format(ESTILO_BOTAO_HOVER, COR_PRINCIPAL_HOVER)));
        btnAdd.setOnMouseExited(e -> btnAdd.setStyle(cssAdd));
        btnAdd.setOnAction(e -> abrirTelaSelecaoProduto());
        painelInferior.add(btnAdd, 0, 0, 1, 2);

        lblSubtotal = new Label("Subtotal: R$ 0,00");
        lblSubtotal.setFont(Font.font("Arial", FontWeight.NORMAL, 16));

        lblTotal = new Label("Total: R$ 0,00");
        lblTotal.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        lblTotal.setTextFill(Color.web(COR_TEXTO_TITULO));

        VBox boxTotais = new VBox(5, lblSubtotal, lblTotal);
        boxTotais.setAlignment(Pos.CENTER_RIGHT);
        painelInferior.add(boxTotais, 1, 0, 1, 2);

        grupoPagamento = new ToggleGroup();
        RadioButton rbDinheiro = criarRadio("Dinheiro", true);
        RadioButton rbPix = criarRadio("Pix", false);
        RadioButton rbCartao = criarRadio("Cartão", false);

        VBox boxPagamento = new VBox(5, new Label("Forma de Pagamento:"), rbDinheiro, rbPix, rbCartao);
        boxPagamento.setAlignment(Pos.CENTER_LEFT);

        Button btnFinalizar = new Button("Finalizar Venda");
        String cssFinalizar = String.format(ESTILO_BOTAO_PADRAO, COR_SUCESSO);
        btnFinalizar.setStyle(cssFinalizar);
        btnFinalizar.setOnMouseEntered(e -> btnFinalizar.setStyle(String.format(ESTILO_BOTAO_HOVER, COR_SUCESSO_HOVER)));
        btnFinalizar.setOnMouseExited(e -> btnFinalizar.setStyle(cssFinalizar));
        btnFinalizar.setPrefHeight(50);
        btnFinalizar.setPrefWidth(200);
        btnFinalizar.setOnAction(e -> finalizarVenda());

        VBox boxFinalizar = new VBox(10, boxPagamento, btnFinalizar);
        boxFinalizar.setAlignment(Pos.CENTER_RIGHT);
        painelInferior.add(boxFinalizar, 2, 0, 1, 2);

        ColumnConstraints col1 = new ColumnConstraints(); col1.setHgrow(Priority.NEVER);
        ColumnConstraints col2 = new ColumnConstraints(); col2.setHgrow(Priority.ALWAYS);
        ColumnConstraints col3 = new ColumnConstraints(); col3.setHgrow(Priority.NEVER);
        painelInferior.getColumnConstraints().addAll(col1, col2, col3);

        return painelInferior;
    }

    private RadioButton criarRadio(String texto, boolean selecionado) {
        RadioButton rb = new RadioButton(texto);
        rb.setToggleGroup(grupoPagamento);
        rb.setUserData(texto);
        rb.setSelected(selecionado);
        return rb;
    }

    private TableView<CartItem> createCarrinhoTable() {
        TableView<CartItem> table = new TableView<>(carrinho);

        TableColumn<CartItem, String> colNome = new TableColumn<>("Produto");
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colNome.setPrefWidth(250);

        TableColumn<CartItem, Double> colPreco = new TableColumn<>("Preço Unit.");
        colPreco.setCellValueFactory(new PropertyValueFactory<>("preco"));
        colPreco.setStyle("-fx-alignment: CENTER_RIGHT;");

        // CORREÇÃO: Tipo genérico explícito para Java 8
        colPreco.setCellFactory(tc -> new TableCell<CartItem, Double>() {
            @Override
            protected void updateItem(Double preco, boolean empty) {
                super.updateItem(preco, empty);
                setText(empty ? null : df.format(preco));
            }
        });

        TableColumn<CartItem, Integer> colQtd = new TableColumn<>("Qtd");
        colQtd.setCellValueFactory(new PropertyValueFactory<>("quantidade"));
        colQtd.setStyle("-fx-alignment: CENTER;");

        TableColumn<CartItem, Double> colTotal = new TableColumn<>("Total");
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        colTotal.setStyle("-fx-alignment: CENTER_RIGHT;");

        // CORREÇÃO: Tipo genérico explícito para Java 8
        colTotal.setCellFactory(tc -> new TableCell<CartItem, Double>() {
            @Override
            protected void updateItem(Double total, boolean empty) {
                super.updateItem(total, empty);
                setText(empty ? null : df.format(total));
            }
        });

        TableColumn<CartItem, Void> colRemover = new TableColumn<>("Remover");
        colRemover.setStyle("-fx-alignment: CENTER;");

        // CORREÇÃO: Tipo genérico explícito para Java 8
        colRemover.setCellFactory(param -> new TableCell<CartItem, Void>() {
            private final Button btn = new Button("🗑");

            {
                btn.setOnAction((ActionEvent e) -> {
                    CartItem item = getTableView().getItems().get(getIndex());
                    carrinho.remove(item);
                    recalcularTotais();
                });
                btn.setStyle(
                        "-fx-background-color: transparent; -fx-text-fill: " + COR_PERIGO + "; " +
                                "-fx-font-size: 16px; -fx-cursor: hand;"
                );
                btn.setOnMouseEntered(e -> btn.setStyle(
                        "-fx-background-color: transparent; -fx-text-fill: #a11; " +
                                "-fx-font-size: 16px; -fx-cursor: hand; -fx-scale-x: 1.1; -fx-scale-y: 1.1;"
                ));
                btn.setOnMouseExited(e -> btn.setStyle(
                        "-fx-background-color: transparent; -fx-text-fill: " + COR_PERIGO + "; " +
                                "-fx-font-size: 16px; -fx-cursor: hand;"
                ));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });

        table.getColumns().addAll(colNome, colPreco, colQtd, colTotal, colRemover);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPlaceholder(new Label("Seu carrinho está vazio."));
        return table;
    }

    private void abrirTelaSelecaoProduto() {
        if (produtosDoBanco.isEmpty()) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro de Produto", "Não há produtos para adicionar. Verifique a conexão com o banco.");
            return;
        }

        Stage modalStage = new Stage();
        modalStage.initModality(Modality.APPLICATION_MODAL);
        modalStage.setTitle("Selecionar Produto");

        BorderPane modalRoot = new BorderPane();
        modalRoot.setStyle("-fx-background-color: " + COR_FUNDO + ";");
        modalRoot.setPadding(new Insets(20));

        Label lblTitulo = new Label("Produtos Disponíveis");
        lblTitulo.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        lblTitulo.setTextFill(Color.web(COR_TEXTO_TITULO));
        BorderPane.setMargin(lblTitulo, new Insets(0, 0, 15, 0));
        modalRoot.setTop(lblTitulo);

        TextField txtPesquisa = new TextField();
        txtPesquisa.setPromptText("Pesquisar produto pelo nome...");
        txtPesquisa.setStyle("-fx-font-size: 14px; -fx-padding: 5px; -fx-background-radius: 8px;");

        FilteredList<Produto> filteredProdutos = new FilteredList<>(produtosDoBanco, p -> true);

        txtPesquisa.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredProdutos.setPredicate(produto -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return produto.getNome().toLowerCase().contains(lowerCaseFilter);
            });
        });

        TableView<Produto> tabelaProdutos = new TableView<>(filteredProdutos);

        tabelaProdutos.setStyle(
                "-fx-background-color: white; -fx-border-color: #e0e0e0; " +
                        "-fx-border-radius: 8px; -fx-background-radius: 8px; -fx-font-size: 13px;"
        );

        TableColumn<Produto, String> colNome = new TableColumn<>("Produto");
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colNome.setPrefWidth(200);

        TableColumn<Produto, Double> colPreco = new TableColumn<>("Preço");
        colPreco.setCellValueFactory(new PropertyValueFactory<>("preco"));
        colPreco.setStyle("-fx-alignment: CENTER_RIGHT;");

        // CORREÇÃO: Tipo genérico explícito para Java 8
        colPreco.setCellFactory(tc -> new TableCell<Produto, Double>() {
            @Override
            protected void updateItem(Double preco, boolean empty) {
                super.updateItem(preco, empty);
                setText(empty ? null : df.format(preco));
            }
        });

        tabelaProdutos.getColumns().addAll(colNome, colPreco);
        tabelaProdutos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        VBox layoutBuscaTabela = new VBox(10);
        layoutBuscaTabela.getChildren().addAll(txtPesquisa, tabelaProdutos);
        VBox.setVgrow(tabelaProdutos, Priority.ALWAYS);

        modalRoot.setCenter(layoutBuscaTabela);

        HBox painelBotoes = new HBox(15);
        painelBotoes.setAlignment(Pos.CENTER_RIGHT);
        painelBotoes.setPadding(new Insets(15, 0, 0, 0));

        Label lblQtd = new Label("Qtd:");
        Spinner<Integer> spinnerQtd = new Spinner<>(1, 100, 1);
        spinnerQtd.setPrefWidth(80);

        Button btnAdicionar = new Button("Adicionar ao Carrinho");
        String cssAdicionar = String.format(ESTILO_BOTAO_PADRAO, COR_PRINCIPAL);
        btnAdicionar.setStyle(cssAdicionar);
        btnAdicionar.setOnMouseEntered(e -> btnAdicionar.setStyle(String.format(ESTILO_BOTAO_HOVER, COR_PRINCIPAL_HOVER)));
        btnAdicionar.setOnMouseExited(e -> btnAdicionar.setStyle(cssAdicionar));

        btnAdicionar.setOnAction(e -> {
            Produto produtoSelecionado = tabelaProdutos.getSelectionModel().getSelectedItem();
            if (produtoSelecionado != null) {
                int quantidade = spinnerQtd.getValue();
                adicionarProdutoAoCarrinho(produtoSelecionado, quantidade);
                modalStage.close();
            } else {
                mostrarAlerta(Alert.AlertType.WARNING, "Nenhum produto selecionado", "Por favor, selecione um produto da lista.");
            }
        });

        painelBotoes.getChildren().addAll(lblQtd, spinnerQtd, btnAdicionar);
        modalRoot.setBottom(painelBotoes);

        Scene modalScene = new Scene(modalRoot, 500, 450);
        modalStage.setScene(modalScene);
        modalStage.showAndWait();
    }

    private void adicionarProdutoAoCarrinho(Produto produto, int quantidade) {
        Optional<CartItem> itemExistente = carrinho.stream()
                .filter(item -> item.getId() == produto.getId())
                .findFirst();

        if (itemExistente.isPresent()) {
            CartItem item = itemExistente.get();
            item.setQuantidade(item.getQuantidade() + quantidade);
            carrinho.set(carrinho.indexOf(item), item);
        } else {
            // Usa o ID real do produto vindo do banco
            carrinho.add(new CartItem(produto.getId(), produto.getNome(), produto.getPreco(), quantidade));
        }

        recalcularTotais();
    }

    private void recalcularTotais() {
        double subtotal = carrinho.stream().mapToDouble(CartItem::getTotal).sum();
        lblSubtotal.setText("Subtotal: " + df.format(subtotal));
        lblTotal.setText("Total: " + df.format(subtotal));
    }

    // Método FINALIZAR VENDA INTEGRADO COM VENDA DAO
    private void finalizarVenda() {
        if (carrinho.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Carrinho Vazio", "O carrinho está vazio!");
            return;
        }
        Toggle pagamentoSelecionado = grupoPagamento.getSelectedToggle();
        if (pagamentoSelecionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Pagamento Não Selecionado", "Por favor, selecione uma forma de pagamento.");
            return;
        }

        String formaPagamento = (String) pagamentoSelecionado.getUserData();
        double totalVenda = carrinho.stream().mapToDouble(CartItem::getTotal).sum();

        // 1. CRIA O OBJETO VENDA
        Venda novaVenda = new Venda(
                totalVenda,
                formaPagamento,
                carrinho
        );

        // 2. CHAMA O DAO PARA SALVAR A VENDA (Transacional)
        VendaDAO dao = new VendaDAO();
        boolean sucesso = dao.salvarVenda(novaVenda); // O DAO cuida do rollback/commit

        if (sucesso) {
            String msgSucesso = String.format(
                    "Venda ID #%d finalizada com sucesso!\nTotal: %s\nPagamento: %s",
                    novaVenda.getId(), // O ID é populado dentro do VendaDAO após o insert
                    df.format(totalVenda),
                    formaPagamento
            );
            mostrarAlerta(Alert.AlertType.INFORMATION, "Venda Concluída", msgSucesso);

            // Notificação (Opcional)
            String itemDetails = carrinho.stream()
                    .map(item -> String.format("%dx %s", item.getQuantidade(), item.getNome()))
                    .collect(Collectors.joining(", "));
            String msgNotificacao = String.format(
                    "Venda ID #%d salva no BD. Itens: %s",
                    novaVenda.getId(),
                    itemDetails
            );
            if (msgNotificacao.length() > 250) {
                msgNotificacao = msgNotificacao.substring(0, 250) + "...";
            }
            NotificacaoDAO.salvarNotificacao(msgNotificacao, NotificationType.INFO);

            // 3. Limpa o carrinho e totais
            carrinho.clear();
            recalcularTotais();

        } else {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro no Banco de Dados", "Falha crítica ao salvar a venda. A transação foi desfeita (rollback).");
        }
    }


    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String conteudo) {
        Alert alert = new Alert(tipo, conteudo, ButtonType.OK);
        alert.setTitle(titulo);
        alert.setHeaderText(null);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: #f7f9fc;");
        dialogPane.lookup(".content.label").setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");

        alert.showAndWait();
    }
}
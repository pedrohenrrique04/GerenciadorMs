package view;

import Model.CartItem;
import Model.Produto;
import Dao.NotificacaoDAO;
import Model.NotificationType;
import javafx.application.Application;
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
import java.util.stream.Collectors; // --- 1. IMPORT NECESSÁRIO ---

public class TelaRealizarVenda extends Application {

    // --- ESTILIZAÇÃO ---
    private static final String COR_FUNDO = "#f7f9fc";
    private static final String COR_PRINCIPAL = "#007bff"; // Azul
    private static final String COR_PRINCIPAL_HOVER = "#0056b3";
    private static final String COR_SUCESSO = "#28a745"; // Verde
    private static final String COR_SUCESSO_HOVER = "#218838";
    private static final String COR_PERIGO = "#dc3545"; // Vermelho (para o lixo)
    private static final String COR_TEXTO_TITULO = "#333";
    private static final String COR_TEXTO_NORMAL = "#555";
    private static final String ESTILO_BOTAO_PADRAO =
            "-fx-background-color: %s; -fx-text-fill: white; -fx-font-weight: bold; " +
                    "-fx-font-size: 14px; -fx-background-radius: 8px; -fx-cursor: hand; " +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 5, 0.1, 0, 2);";

    // --- ESTILIZAÇÃO HOVER ---
    private static final String ESTILO_BOTAO_HOVER =
            "-fx-background-color: %s; -fx-text-fill: white; -fx-font-weight: bold; " +
                    "-fx-font-size: 14px; -fx-background-radius: 8px; -fx-cursor: hand; " +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 8, 0.2, 0, 3);";

    private final ObservableList<CartItem> carrinho = FXCollections.observableArrayList();
    private final DecimalFormat df = new DecimalFormat("R$ #,##0.00");
    private Label lblSubtotal, lblTotal;

    private ToggleGroup grupoPagamento;

    // Lista de produtos "do banco de dados" (simulação)
    private final ObservableList<Produto> produtosDoBanco = FXCollections.observableArrayList(
            new Produto(1, "Camisa Polo", 79.90),
            new Produto(2, "Calça Jeans", 129.99),
            new Produto(3, "Tênis Esportivo", 249.50),
            new Produto(4, "Boné", 45.00),
            new Produto(5, "Meia (Par)", 19.90),
            new Produto(6, "Cinto de Couro", 89.90),
            new Produto(7, "Relógio Digital", 199.00),
            new Produto(8, "Mochila Urbana", 149.99)
    );

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Realizar Venda");

        // Layout principal
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + COR_FUNDO + ";");
        root.setPadding(new Insets(25));

        // Título
        Label lblTitulo = new Label("Ponto de Venda");
        lblTitulo.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        lblTitulo.setTextFill(Color.web(COR_TEXTO_TITULO));
        BorderPane.setAlignment(lblTitulo, Pos.CENTER_LEFT);
        BorderPane.setMargin(lblTitulo, new Insets(0, 0, 20, 0));
        root.setTop(lblTitulo);

        // Tabela do Carrinho
        TableView<CartItem> tabela = createCarrinhoTable();
        VBox.setVgrow(tabela, Priority.ALWAYS); // Faz a tabela crescer
        tabela.setStyle(
                "-fx-background-color: white; -fx-border-color: #e0e0e0; " +
                        "-fx-border-radius: 8px; -fx-background-radius: 8px; -fx-font-size: 13px;"
        );

        root.setCenter(tabela);

        // Painel inferior (Botões e Totais)
        GridPane painelInferior = new GridPane();
        painelInferior.setHgap(20);
        painelInferior.setPadding(new Insets(20, 0, 0, 0));

        // Coluna 1: Botão Adicionar
        Button btnAdd = new Button("+ Adicionar Produto");
        String cssAdd = String.format(ESTILO_BOTAO_PADRAO, COR_PRINCIPAL);
        btnAdd.setStyle(cssAdd);
        btnAdd.setOnMouseEntered(e -> btnAdd.setStyle(String.format(ESTILO_BOTAO_HOVER, COR_PRINCIPAL_HOVER)));
        btnAdd.setOnMouseExited(e -> btnAdd.setStyle(cssAdd));
        btnAdd.setOnAction(e -> abrirTelaSelecaoProduto());
        painelInferior.add(btnAdd, 0, 0, 1, 2); // Ocupa 2 linhas

        // Coluna 2: Totais
        lblSubtotal = new Label("Subtotal: R$ 0,00");
        lblSubtotal.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        lblSubtotal.setTextFill(Color.web(COR_TEXTO_NORMAL));

        lblTotal = new Label("Total: R$ 0,00");
        lblTotal.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        lblTotal.setTextFill(Color.web(COR_TEXTO_TITULO));

        VBox boxTotais = new VBox(5, lblSubtotal, lblTotal);
        boxTotais.setAlignment(Pos.CENTER_RIGHT);
        painelInferior.add(boxTotais, 1, 0, 1, 2);

        // Coluna 3: Métodos de Pagamento e Finalizar
        grupoPagamento = new ToggleGroup();
        RadioButton rbDinheiro = new RadioButton("Dinheiro");
        rbDinheiro.setToggleGroup(grupoPagamento);
        rbDinheiro.setUserData("Dinheiro");
        rbDinheiro.setSelected(true);

        RadioButton rbPix = new RadioButton("Pix");
        rbPix.setToggleGroup(grupoPagamento);
        rbPix.setUserData("Pix");

        RadioButton rbCartao = new RadioButton("Cartão");
        rbCartao.setToggleGroup(grupoPagamento);
        rbCartao.setUserData("Cartão");

        Label lblPagamento = new Label("Forma de Pagamento:");
        lblPagamento.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        lblPagamento.setTextFill(Color.web(COR_TEXTO_NORMAL));

        VBox boxPagamento = new VBox(5, lblPagamento, rbDinheiro, rbPix, rbCartao);
        boxPagamento.setPadding(new Insets(0, 0, 10, 0));
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

        // Configuração das colunas do GridPane
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setHgrow(Priority.NEVER);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHgrow(Priority.ALWAYS); // Coluna dos totais expande
        ColumnConstraints col3 = new ColumnConstraints();
        col3.setHgrow(Priority.NEVER);
        painelInferior.getColumnConstraints().addAll(col1, col2, col3);

        root.setBottom(painelInferior);

        Scene scene = new Scene(root, 850, 600);

        primaryStage.setScene(scene);
        primaryStage.setMinHeight(500);
        primaryStage.setMinWidth(750);
        primaryStage.show();
    }

    private TableView<CartItem> createCarrinhoTable() {
        TableView<CartItem> table = new TableView<>(carrinho);

        TableColumn<CartItem, String> colNome = new TableColumn<>("Produto");
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colNome.setPrefWidth(250);

        TableColumn<CartItem, Double> colPreco = new TableColumn<>("Preço Unit.");
        colPreco.setCellValueFactory(new PropertyValueFactory<>("preco"));
        colPreco.setStyle("-fx-alignment: CENTER_RIGHT;");
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
        colTotal.setCellFactory(tc -> new TableCell<CartItem, Double>() {
            @Override
            protected void updateItem(Double total, boolean empty) {
                super.updateItem(total, empty);
                setText(empty ? null : df.format(total));
            }
        });

        TableColumn<CartItem, Void> colRemover = new TableColumn<>("Remover");
        colRemover.setStyle("-fx-alignment: CENTER;");
        colRemover.setCellFactory(param -> new TableCell<CartItem, Void>() {
            private final Button btn = new Button("🗑");

            {
                btn.setOnAction((ActionEvent e) -> {
                    CartItem item = getTableView().getItems().get(getIndex());
                    carrinho.remove(item);
                    recalcularTotais();
                });
                // Estilo do botão de remover
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

    /**
     * Abre a nova janela (Modal) para seleção de produtos.
     */
    private void abrirTelaSelecaoProduto() {
        Stage modalStage = new Stage();
        modalStage.initModality(Modality.APPLICATION_MODAL); // Bloqueia a janela principal
        modalStage.setTitle("Selecionar Produto");

        BorderPane modalRoot = new BorderPane();
        modalRoot.setStyle("-fx-background-color: " + COR_FUNDO + ";");
        modalRoot.setPadding(new Insets(20));

        Label lblTitulo = new Label("Produtos Disponíveis");
        lblTitulo.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        lblTitulo.setTextFill(Color.web(COR_TEXTO_TITULO));
        BorderPane.setMargin(lblTitulo, new Insets(0, 0, 15, 0));
        modalRoot.setTop(lblTitulo);

        // 1. Criar a barra de pesquisa (TextField)
        TextField txtPesquisa = new TextField();
        txtPesquisa.setPromptText("Pesquisar produto pelo nome...");
        txtPesquisa.setStyle("-fx-font-size: 14px; -fx-padding: 5px; -fx-background-radius: 8px;");

        // 2. Criar a FilteredList (Lista Filtrada)
        FilteredList<Produto> filteredProdutos = new FilteredList<>(produtosDoBanco, p -> true);

        // 3. Adicionar o listener na barra de pesquisa
        txtPesquisa.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredProdutos.setPredicate(produto -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return produto.getNome().toLowerCase().contains(lowerCaseFilter);
            });
        });

        // 4. Criar a Tabela de Produtos usando a LISTA FILTRADA
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
        colPreco.setCellFactory(tc -> new TableCell<Produto, Double>() {
            @Override
            protected void updateItem(Double preco, boolean empty) {
                super.updateItem(preco, empty);
                setText(empty ? null : df.format(preco));
            }
        });

        tabelaProdutos.getColumns().addAll(colNome, colPreco);
        tabelaProdutos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Colocar a barra de pesquisa e a tabela em um VBox
        VBox layoutBuscaTabela = new VBox(10); // 10px de espaçamento
        layoutBuscaTabela.getChildren().addAll(txtPesquisa, tabelaProdutos);
        VBox.setVgrow(tabelaProdutos, Priority.ALWAYS); // Faz a tabela crescer

        modalRoot.setCenter(layoutBuscaTabela); // Adiciona o VBox ao centro

        // Painel inferior do Modal
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

        Scene modalScene = new Scene(modalRoot, 500, 450); // Aumentei um pouco a altura
        modalStage.setScene(modalScene);
        modalStage.showAndWait(); // Mostra e espera
    }

    /**
     * Adiciona um produto selecionado ao carrinho.
     * Verifica se o item já existe para apenas somar a quantidade.
     */
    private void adicionarProdutoAoCarrinho(Produto produto, int quantidade) {
        // Verifica se o item já está no carrinho
        Optional<CartItem> itemExistente = carrinho.stream()
                .filter(item -> item.getId() == produto.getId())
                .findFirst();

        if (itemExistente.isPresent()) {
            // Se existe, apenas atualiza a quantidade
            CartItem item = itemExistente.get();
            item.setQuantidade(item.getQuantidade() + quantidade);
            carrinho.set(carrinho.indexOf(item), item);
        } else {
            // Se não existe, adiciona novo item
            carrinho.add(new CartItem(produto.getId(), produto.getNome(), produto.getPreco(), quantidade));
        }

        recalcularTotais();
    }

    private void recalcularTotais() {
        double subtotal = carrinho.stream().mapToDouble(CartItem::getTotal).sum();
        lblSubtotal.setText("Subtotal: " + df.format(subtotal));
        lblTotal.setText("Total: " + df.format(subtotal));
    }

    // --- MUDANÇA AQUI ---
    // Método `finalizarVenda` foi atualizado para salvar a notificação com os nomes dos itens
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

        String msgSucesso = String.format(
                "Venda finalizada com sucesso!\n\nTotal: %s\nPagamento: %s",
                df.format(totalVenda),
                formaPagamento
        );

        mostrarAlerta(Alert.AlertType.INFORMATION, "Venda Concluída", msgSucesso);

        // --- 5. SALVAR NOTIFICAÇÃO NO BANCO (COM DETALHES) ---

        // Coleta os nomes e quantidades dos itens
        String itemDetails = carrinho.stream()
                .map(item -> String.format("%dx %s", item.getQuantidade(), item.getNome()))
                .collect(Collectors.joining(", ")); // Ex: "1x Camisa Polo, 2x Calça Jeans"

        // (Assumindo que o funcionário é 'Admin' por enquanto)
        String msgNotificacao = String.format(
                "Funcionário 'Admin' vendeu: %s (%s)",
                itemDetails,
                df.format(totalVenda)
        );

        // Trunca a mensagem se for muito longa para o banco (VARCHAR 255)
        if (msgNotificacao.length() > 250) {
            msgNotificacao = msgNotificacao.substring(0, 250) + "...";
        }

        NotificacaoDAO.salvarNotificacao(msgNotificacao, NotificationType.INFO);
        // --- FIM DA MUDANÇA ---

        carrinho.clear();
        recalcularTotais();
    }


    /**
     * Helper para mostrar Alertas de forma padronizada.
     */
    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String conteudo) {
        Alert alert = new Alert(tipo, conteudo, ButtonType.OK);
        alert.setTitle(titulo);
        alert.setHeaderText(null); // Remove o cabeçalho feio

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: #f7f9fc;");
        dialogPane.lookup(".content.label").setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");

        alert.showAndWait();
    }
}
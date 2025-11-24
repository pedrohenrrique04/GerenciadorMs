package view;

import Dao.ProdutoDAO;
import Dao.VendaDAO;
import Dao.NotificacaoDAO;
import Model.CartItem;
import Model.ProdutoModel;
import Model.Venda;
import Model.NotificationType;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.util.Callback;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class TelaRealizarVenda {

    private static final String COR_FUNDO = "#f7f9fc";
    private static final String COR_PRINCIPAL = "#007bff";
    private static final String COR_SUCESSO = "#28a745";
    private static final String COR_PERIGO = "#dc3545";
    private static final String COR_TEXTO_TITULO = "#333";
    private static final String ESTILO_BOTAO_PADRAO = "-fx-background-color: %s; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-background-radius: 8px; -fx-cursor: hand;";
    private static final String ESTILO_BOTAO_HOVER = "-fx-background-color: %s; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-background-radius: 8px; -fx-cursor: hand;";

    private final ObservableList<CartItem> carrinho = FXCollections.observableArrayList();
    private final DecimalFormat df = new DecimalFormat("R$ #,##0.00");
    private Label lblSubtotal, lblTotal;
    private ToggleGroup grupoPagamento;

    private final List<ProdutoModel> produtosDoBanco;

    public TelaRealizarVenda() {
        ProdutoDAO dao = new ProdutoDAO();
        produtosDoBanco = dao.listarTodos();
    }

    public BorderPane getTela() {
        if (produtosDoBanco.isEmpty()) {
            Label lblErro = new Label(" ERRO: Não foi possível carregar os produtos do banco.");
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

        TableView tabelaCarrinho = createCarrinhoTable();
        VBox.setVgrow(tabelaCarrinho, Priority.ALWAYS);
        tabelaCarrinho.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-font-size: 13px;");
        root.setCenter(tabelaCarrinho);

        GridPane painelInferior = criarPainelInferior();
        root.setBottom(painelInferior);

        return root;
    }

    private GridPane criarPainelInferior() {
        GridPane painel = new GridPane();
        painel.setHgap(20);
        painel.setPadding(new Insets(20,0,0,0));

        Button btnAdd = new Button("+ Adicionar Produto");
        btnAdd.setStyle(String.format(ESTILO_BOTAO_PADRAO, COR_PRINCIPAL));
        btnAdd.setOnAction(new javafx.event.EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                abrirTelaSelecaoProduto();
            }
        });
        painel.add(btnAdd, 0,0);

        lblSubtotal = new Label("Subtotal: R$ 0,00");
        lblTotal = new Label("Total: R$ 0,00");
        lblTotal.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        lblTotal.setTextFill(Color.web(COR_TEXTO_TITULO));

        VBox boxTotais = new VBox(5, lblSubtotal, lblTotal);
        boxTotais.setAlignment(Pos.CENTER_RIGHT);
        painel.add(boxTotais, 1,0);

        grupoPagamento = new ToggleGroup();
        RadioButton rbDinheiro = criarRadio("Dinheiro", true);
        RadioButton rbPix = criarRadio("Pix", false);
        RadioButton rbCartao = criarRadio("Cartão", false);
        VBox boxPagamento = new VBox(5, new Label("Forma de Pagamento:"), rbDinheiro, rbPix, rbCartao);

        Button btnFinalizar = new Button("Finalizar Venda");
        btnFinalizar.setStyle(String.format(ESTILO_BOTAO_PADRAO, COR_SUCESSO));
        btnFinalizar.setOnAction(new javafx.event.EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                finalizarVenda();
            }
        });

        VBox boxFinalizar = new VBox(10, boxPagamento, btnFinalizar);
        boxFinalizar.setAlignment(Pos.CENTER_RIGHT);
        painel.add(boxFinalizar, 2,0);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setHgrow(Priority.NEVER);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHgrow(Priority.ALWAYS);
        ColumnConstraints col3 = new ColumnConstraints();
        col3.setHgrow(Priority.NEVER);
        painel.getColumnConstraints().addAll(col1, col2, col3);

        return painel;
    }

    private RadioButton criarRadio(String texto, boolean selecionado) {
        RadioButton rb = new RadioButton(texto);
        rb.setToggleGroup(grupoPagamento);
        rb.setUserData(texto);
        rb.setSelected(selecionado);
        return rb;
    }

    private TableView createCarrinhoTable() {
        TableView table = new TableView(carrinho);

        TableColumn colNome = new TableColumn("Produto");
        colNome.setCellValueFactory(new PropertyValueFactory("nome"));
        colNome.setPrefWidth(200);

        TableColumn colPreco = new TableColumn("Preço Unit.");
        colPreco.setCellValueFactory(new PropertyValueFactory("preco"));
        colPreco.setCellFactory(new Callback() {
            @Override
            public TableCell call(Object param) {
                return new TableCell() {
                    @Override
                    protected void updateItem(Object item, boolean empty) {
                        super.updateItem(item, empty);
                        if(empty || item==null) {
                            setText(null);
                        } else {
                            setText(df.format((Double)item));
                        }
                    }
                };
            }
        });

        TableColumn colQtd = new TableColumn("Qtd");
        colQtd.setCellValueFactory(new PropertyValueFactory("quantidade"));

        TableColumn colTotal = new TableColumn("Total");
        colTotal.setCellValueFactory(new PropertyValueFactory("total"));
        colTotal.setCellFactory(new Callback() {
            @Override
            public TableCell call(Object param) {
                return new TableCell() {
                    @Override
                    protected void updateItem(Object item, boolean empty) {
                        super.updateItem(item, empty);
                        if(empty || item==null) {
                            setText(null);
                        } else {
                            setText(df.format((Double)item));
                        }
                    }
                };
            }
        });

        table.getColumns().addAll(colNome, colPreco, colQtd, colTotal);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPlaceholder(new Label("Carrinho vazio."));
        return table;
    }

    private void abrirTelaSelecaoProduto() {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.setTitle("Selecionar Produto");

        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        TextField txtPesquisa = new TextField();
        txtPesquisa.setPromptText("Pesquisar produto...");
        root.getChildren().add(txtPesquisa);

        final ListView<ProdutoModel> lvProdutos = new ListView();
        final ObservableList<ProdutoModel> obsProdutos = FXCollections.observableArrayList(produtosDoBanco);
        lvProdutos.setItems(obsProdutos);
        root.getChildren().add(lvProdutos);

        Button btnAdd = new Button("Adicionar ao Carrinho");
        btnAdd.setOnAction(new javafx.event.EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ProdutoModel selecionado = lvProdutos.getSelectionModel().getSelectedItem();
                if(selecionado!=null) {
                    adicionarProdutoAoCarrinho(selecionado, 1);
                    modal.close();
                }
            }
        });
        root.getChildren().add(btnAdd);

        txtPesquisa.textProperty().addListener(new javafx.beans.value.ChangeListener() {
            @Override
            public void changed(javafx.beans.value.ObservableValue observable, Object oldVal, Object newVal) {
                String filtro = newVal.toString().toLowerCase();
                List<ProdutoModel> listaFiltrada = new ArrayList<ProdutoModel>();
                for(ProdutoModel p : produtosDoBanco) {
                    if(p.getNome().toLowerCase().contains(filtro)) {
                        listaFiltrada.add(p);
                    }
                }
                obsProdutos.setAll(listaFiltrada);
            }
        });

        Scene scene = new Scene(root, 400, 400);
        modal.setScene(scene);
        modal.showAndWait();
    }

    private void adicionarProdutoAoCarrinho(ProdutoModel produto, int quantidade) {
        CartItem existente = null;
        for(CartItem item : carrinho) {
            if(item.getId() == produto.getId()) {
                existente = item;
                break;
            }
        }
        if(existente!=null) {
            existente.setQuantidade(existente.getQuantidade() + quantidade);
        } else {
            carrinho.add(new CartItem(produto.getId(), produto.getNome(), produto.getPrecoVenda(), quantidade));
        }
        recalcularTotais();
    }

    private void recalcularTotais() {
        double total = 0;
        for(CartItem item : carrinho) {
            total += item.getPreco() * item.getQuantidade();
        }
        lblSubtotal.setText("Subtotal: " + df.format(total));
        lblTotal.setText("Total: " + df.format(total));
    }

    private void finalizarVenda() {
        if(carrinho.isEmpty()) return;
        Toggle selecionado = grupoPagamento.getSelectedToggle();
        if(selecionado==null) return;
        String formaPagamento = (String) selecionado.getUserData();

        double total = 0;
        for(CartItem item : carrinho) {
            total += item.getPreco() * item.getQuantidade();
        }

        Venda venda = new Venda(total, formaPagamento, new ArrayList<CartItem>(carrinho));
        VendaDAO dao = new VendaDAO();
        boolean sucesso = dao.salvarVenda(venda);

        if(sucesso) {
            NotificacaoDAO.notificarVenda(venda);
            carrinho.clear();
            recalcularTotais();
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Venda finalizada!");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erro ao salvar venda!");
            alert.showAndWait();
        }
    }
}

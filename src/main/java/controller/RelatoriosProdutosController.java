package controller;

import Dao.RelatoriosProdutosDao;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerSlideCloseTransition;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import Model.Produto;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;

import Model.RelatorioProduto; // <--- O Model novo com JOIN
import java.text.NumberFormat; // <--- Para formatar R$
import java.text.SimpleDateFormat; // <--- Para formatar Data
import java.util.Locale;
import javafx.scene.control.TableCell; // <--- Para customizar as células


public class RelatoriosProdutosController implements Initializable {
    @FXML
    private JFXHamburger h2;
    @FXML private JFXDrawer drawer02;
    private HamburgerSlideCloseTransition transition;
    @FXML private ComboBox<String> comboOpcoes02;
    @FXML
    private Label labelEstoqueTotal;

    @FXML
    private ComboBox<String> comboCategoria;

    // TABELA ESTOQUE M&Iacute;NIMO
    @FXML private TableView<Produto> tableEstoqueMinimo;

    @FXML private TableColumn<Produto, String> colNomeMin;
    @FXML private TableColumn<Produto, String> colCategoriaMin;
    @FXML private TableColumn<Produto, Integer> colEstoqueMin;

    @FXML private TableColumn<Produto, BigDecimal> colPrecoMin;
    @FXML private TableColumn<Produto, BigDecimal> colPrecoCustoMin;
    @FXML private TableColumn<Produto, BigDecimal> colLucroMin;

    // ... variáveis da tabela estoque mínimo ...

    // ==========================================
    // ⬇ ADICIONAR ISSO: TABELA RELATÓRIO GERAL
    // ==========================================
    @FXML private TableView<RelatorioProduto> tableRelatorios; // Note que usa RelatorioProduto

    @FXML private TableColumn<RelatorioProduto, String> colProduto;
    @FXML private TableColumn<RelatorioProduto, String> colCategoria;
    @FXML private TableColumn<RelatorioProduto, Integer> colQnt;
    @FXML private TableColumn<RelatorioProduto, java.util.Date> colUltimaVenda;
    @FXML private TableColumn<RelatorioProduto, BigDecimal> colPrecoUnit;
    @FXML private TableColumn<RelatorioProduto, BigDecimal> colPrecoVend;
    @FXML private TableColumn<RelatorioProduto, BigDecimal> colLucro;


    private RelatoriosProdutosDao dao;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initSideMenu();
        initAnimation();
        initOutsideClickClose();

        dao = new RelatoriosProdutosDao();

        h2.setStyle("-fx-background-color: transparent;"); // fundo transparente
        h2.getChildrenUnmodifiable().forEach(node -> {
            if (node instanceof StackPane) {
                ((StackPane) node).setStyle("-fx-background-color: white;");
            }
        });

        // Inicialmente, estoque total = 0
        labelEstoqueTotal.setText("0");

        // Listener para atualizar estoque quando mudar a categoria
        comboCategoria.setOnAction(event -> atualizarEstoqueTotal());

        configurarTabelaEstoqueMinimo();
        carregarProdutosEstoqueMinimo();

        configurarTabelaRelatorios();
        carregarDadosRelatorios();
    }

    private void configurarTabelaRelatorios() {
        // Vincula as colunas aos atributos de RelatorioProduto
        colProduto.setCellValueFactory(new PropertyValueFactory<>("produto"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colQnt.setCellValueFactory(new PropertyValueFactory<>("quantidadeDisponivel"));
        colUltimaVenda.setCellValueFactory(new PropertyValueFactory<>("ultimaVenda"));
        colPrecoUnit.setCellValueFactory(new PropertyValueFactory<>("precoUnit"));
        colPrecoVend.setCellValueFactory(new PropertyValueFactory<>("precoVendido"));
        colLucro.setCellValueFactory(new PropertyValueFactory<>("lucroUnit"));

        // --- FORMATAÇÃO VISUAL (Bônus Importante) ---

        // 1. Formatar Data (dd/MM/yyyy)
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        colUltimaVenda.setCellFactory(column -> new TableCell<RelatorioProduto, java.util.Date>() {
            @Override
            protected void updateItem(java.util.Date item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(sdf.format(item));
                }
            }
        });

        // 2. Formatar Dinheiro (R$) para as colunas de preço e lucro
        formatarColunaMoeda(colPrecoUnit);
        formatarColunaMoeda(colPrecoVend);
        formatarColunaMoeda(colLucro);
    }

    private void carregarDadosRelatorios() {
        // Chama o método do DAO com o JOIN que criamos
        List<RelatorioProduto> dados = dao.gerarRelatorioGeral();
        tableRelatorios.setItems(FXCollections.observableArrayList(dados));
    }

    // Método auxiliar para não repetir código de formatação de moeda
    private void formatarColunaMoeda(TableColumn<RelatorioProduto, BigDecimal> col) {
        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

        col.setCellFactory(column -> new TableCell<RelatorioProduto, BigDecimal>() {
            @Override
            protected void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(nf.format(item));

                    // (Opcional) Pintar Lucro negativo de vermelho
                    if (getText().contains("-")) {
                        setStyle("-fx-text-fill: red;");
                    } else {
                        setStyle("-fx-text-fill: black;");
                    }
                }
            }
        });
    }

    private void configurarTabelaEstoqueMinimo() {
        colNomeMin.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colCategoriaMin.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colEstoqueMin.setCellValueFactory(new PropertyValueFactory<>("estoque"));
        colPrecoMin.setCellValueFactory(new PropertyValueFactory<>("preco"));
        colPrecoCustoMin.setCellValueFactory(new PropertyValueFactory<>("precoCusto"));

        colLucroMin.setCellValueFactory(cellData -> {
            Produto p = cellData.getValue();

            BigDecimal preco = p.getPreco() == null ? BigDecimal.ZERO : p.getPreco();
            BigDecimal custo = p.getPrecoCusto() == null ? BigDecimal.ZERO : p.getPrecoCusto();

            return new ReadOnlyObjectWrapper<>(preco.subtract(custo));
        });
    }

    private void carregarProdutosEstoqueMinimo() {
        List<Produto> produtos = dao.listarProdutosEstoqueMinimo();
        ObservableList<Produto> lista = FXCollections.observableArrayList(produtos);
        tableEstoqueMinimo.setItems(lista);
    }


    private void atualizarEstoqueTotal() {
        String categoria = comboCategoria.getValue();

        if (categoria == null || categoria.isEmpty()) {
            labelEstoqueTotal.setText("0");
            return;
        }

        int total = dao.getEstoquePorCategoria(categoria);
        labelEstoqueTotal.setText(String.valueOf(total));
    }


    // ------------- MENU LATERAL ----------------------

    private void initSideMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MenuLateral.fxml"));
            VBox box = loader.load();
            drawer02.setSidePane(box);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao carregar MenuLateral.fxml", e);
        }

        drawer02.setDefaultDrawerSize(240);
        drawer02.setOverLayVisible(true);
        drawer02.setResizableOnDrag(false);
        drawer02.close();
    }

    private void initAnimation() {
        transition = new HamburgerSlideCloseTransition(h2);
        transition.setRate(-1);

        h2.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
            transition.setRate(transition.getRate() * -1);
            transition.play();

            if (drawer02.isOpened()) {
                drawer02.close();
            } else {
                drawer02.toFront();
                drawer02.open();
            }
        });

        drawer02.setOnDrawerClosed(e -> {
            drawer02.toBack();
            if (transition.getRate() > 0) {
                transition.setRate(-1);
                transition.play();
            }
        });
    }

    private void initOutsideClickClose() {
        h2.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {

                    boolean clickedOutside =
                            drawer02.isOpened()
                                    && !drawer02.isHover()
                                    && !h2.isHover();

                    if (clickedOutside) {
                        drawer02.close();
                    }
                });
            }
        });
        comboOpcoes02.setOnAction(event -> {
            if ("Vendas".equals(comboOpcoes02.getValue())) {
                abrirTelaRelatorios();
            }
        });
    }
    private void abrirTelaRelatorios() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/Relatorios.fxml"));
            Scene scene = comboOpcoes02.getScene();
            scene.setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package view;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import Model.CartItem;
import java.text.DecimalFormat;

public class TelaRealizarVenda extends Application {

    private final ObservableList<CartItem> carrinho = FXCollections.observableArrayList();
    private final DecimalFormat df = new DecimalFormat("0.00");
    private Label lblSubtotal, lblTotal;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Realizar Venda");

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));

        TableView<CartItem> tabela = createCarrinhoTable();

        Button btnAdd = new Button("+ Adicionar Produto");
        btnAdd.setOnAction(e -> adicionarProdutoExemplo());

        lblSubtotal = new Label("Subtotal: R$ 0,00");
        lblTotal = new Label("Total: R$ 0,00");

        HBox valores = new HBox(20, lblSubtotal, lblTotal);
        valores.setPadding(new Insets(10));

        Button btnFinalizar = new Button("Finalizar Venda");
        btnFinalizar.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        btnFinalizar.setOnAction(e -> finalizarVenda());

        root.getChildren().addAll(tabela, btnAdd, valores, btnFinalizar);

        Scene scene = new Scene(root, 700, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private TableView<CartItem> createCarrinhoTable() {
        TableView<CartItem> table = new TableView<>(carrinho);

        TableColumn<CartItem, String> colNome = new TableColumn<>("Produto");
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));

        TableColumn<CartItem, Double> colPreco = new TableColumn<>("Preço (R$)");
        colPreco.setCellValueFactory(new PropertyValueFactory<>("preco"));

        TableColumn<CartItem, Integer> colQtd = new TableColumn<>("Qtd");
        colQtd.setCellValueFactory(new PropertyValueFactory<>("quantidade"));

        TableColumn<CartItem, Double> colTotal = new TableColumn<>("Total (R$)");
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));

        TableColumn<CartItem, Void> colRemover = new TableColumn<>("Remover");
        colRemover.setCellFactory(param -> new TableCell<CartItem, Void>() {
            private final Button btn = new Button("🗑");

            {
                btn.setOnAction((ActionEvent e) -> {
                    CartItem item = getTableView().getItems().get(getIndex());
                    carrinho.remove(item);
                    recalcularTotais();
                });
                btn.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });

        table.getColumns().addAll(colNome, colPreco, colQtd, colTotal, colRemover);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        return table;
    }

    private void adicionarProdutoExemplo() {
        // Exemplo — futuramente você pode abrir um diálogo para escolher produtos do banco
        carrinho.add(new CartItem(1, "Camisa Polo", 79.90, 1));
        recalcularTotais();
    }

    private void recalcularTotais() {
        double subtotal = carrinho.stream().mapToDouble(CartItem::getTotal).sum();
        lblSubtotal.setText("Subtotal: R$ " + df.format(subtotal));
        lblTotal.setText("Total: R$ " + df.format(subtotal));
    }

    private void finalizarVenda() {
        if (carrinho.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Carrinho vazio!", ButtonType.OK);
            alert.show();
            return;
        }
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Venda finalizada com sucesso!", ButtonType.OK);
        alert.show();
        carrinho.clear();
        recalcularTotais();
    }
}

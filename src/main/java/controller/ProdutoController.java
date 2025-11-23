package controller;

import Model.ProdutoModel; // Importação corrigida
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class ProdutoController implements Initializable {

    @FXML private TabPane mainTabPane;

    // Filtros
    @FXML private CheckBox roupasCheck, calcadosCheck, acessoriosCheck;
    @FXML private RadioButton masculinoRadio, femininoRadio, unisexRadio;
    @FXML private CheckBox brancoCheck, pretoCheck, verdeCheck;

    // Campos do produto
    @FXML private TextField nomeField, quantidadeField, precoCustoField, precoVendaField;
    @FXML private TextField dataEntradaField, dataReposicaoField;
    @FXML private Button excluirBtn, editarBtn, salvarBtn;
    @FXML private Label idLabel, nomeProdutoLabel, precoLabel;

    // Tipo alterado para ProdutoModel
    private ProdutoModel produtoAtual;
    private boolean editando = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarComponentes();
        carregarProdutoExemplo();
        toggleEdicaoCampos(false);
    }

    private void configurarComponentes() {
        // Configurar ToggleGroup para gênero
        ToggleGroup generoGroup = new ToggleGroup();
        masculinoRadio.setToggleGroup(generoGroup);
        femininoRadio.setToggleGroup(generoGroup);
        unisexRadio.setToggleGroup(generoGroup);

        // Configurar eventos dos botões
        excluirBtn.setOnAction(e -> excluirProduto());
        editarBtn.setOnAction(e -> toggleEdicao());
        salvarBtn.setOnAction(e -> salvarProduto());

        // Estilizar botões
        excluirBtn.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white;");
        editarBtn.setStyle("-fx-background-color: #ffc107; -fx-text-fill: black;");
        salvarBtn.setStyle("-fx-background-color: #28a745; -fx-text-fill: white;");
    }

    private void carregarProdutoExemplo() {
        // Instanciação alterada para ProdutoModel
        produtoAtual = new ProdutoModel(
                99,
                "Tênis Nike Air Force 1 '07 Masculino",
                99,
                9.99,
                799.99,
                LocalDate.now(),
                null,
                "Calçados",
                "Masculino",
                "Branco"
        );

        atualizarCamposProduto();
    }

    private void atualizarCamposProduto() {
        if (produtoAtual != null) {
            idLabel.setText(String.valueOf(produtoAtual.getId()));
            nomeProdutoLabel.setText(produtoAtual.getNome());
            precoLabel.setText(String.format("R$ %.2f", produtoAtual.getPrecoVenda()));

            nomeField.setText(produtoAtual.getNome());
            quantidadeField.setText(String.valueOf(produtoAtual.getQuantidade()));
            precoCustoField.setText(String.format("R$ %.2f", produtoAtual.getPrecoCusto()));
            precoVendaField.setText(String.format("R$ %.2f", produtoAtual.getPrecoVenda()));

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            dataEntradaField.setText(produtoAtual.getDataEntrada().format(formatter));

            if (produtoAtual.getDataReposicao() != null) {
                dataReposicaoField.setText(produtoAtual.getDataReposicao().format(formatter));
            } else {
                dataReposicaoField.setText("");
            }

            // Atualizar filtros baseados no produto
            atualizarFiltros();
        }
    }

    private void atualizarFiltros() {
        // Categoria
        if ("Roupas".equals(produtoAtual.getCategoria())) roupasCheck.setSelected(true);
        else if ("Calçados".equals(produtoAtual.getCategoria())) calcadosCheck.setSelected(true);
        else if ("Acessórios".equals(produtoAtual.getCategoria())) acessoriosCheck.setSelected(true);

        // Gênero
        if ("Masculino".equals(produtoAtual.getGenero())) masculinoRadio.setSelected(true);
        else if ("Feminino".equals(produtoAtual.getGenero())) femininoRadio.setSelected(true);
        else if ("Unisex".equals(produtoAtual.getGenero())) unisexRadio.setSelected(true);

        // Cor
        if ("Branco".equals(produtoAtual.getCor())) brancoCheck.setSelected(true);
        else if ("Preto".equals(produtoAtual.getCor())) pretoCheck.setSelected(true);
        else if ("Verde".equals(produtoAtual.getCor())) verdeCheck.setSelected(true);
    }

    @FXML
    private void toggleEdicao() {
        editando = !editando;
        toggleEdicaoCampos(editando);

        if (editando) {
            editarBtn.setText("Cancelar");
            editarBtn.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white;");
        } else {
            editarBtn.setText("Editar");
            editarBtn.setStyle("-fx-background-color: #ffc107; -fx-text-fill: black;");
            // Recarregar dados originais
            atualizarCamposProduto();
        }
    }

    private void toggleEdicaoCampos(boolean editavel) {
        nomeField.setEditable(editavel);
        quantidadeField.setEditable(editavel);
        precoCustoField.setEditable(editavel);
        precoVendaField.setEditable(editavel);
        dataEntradaField.setEditable(editavel);
        dataReposicaoField.setEditable(editavel);
    }

    @FXML
    private void salvarProduto() {
        try {
            // Atualizar objeto produto com os dados dos campos
            produtoAtual.setNome(nomeField.getText());
            produtoAtual.setQuantidade(Integer.parseInt(quantidadeField.getText().replaceAll("[^0-9]", "")));
            produtoAtual.setPrecoCusto(Double.parseDouble(precoCustoField.getText().replace("R$", "").replace(",", ".").trim()));
            produtoAtual.setPrecoVenda(Double.parseDouble(precoVendaField.getText().replace("R$", "").replace(",", ".").trim()));

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            produtoAtual.setDataEntrada(LocalDate.parse(dataEntradaField.getText(), formatter));

            if (!dataReposicaoField.getText().isEmpty()) {
                produtoAtual.setDataReposicao(LocalDate.parse(dataReposicaoField.getText(), formatter));
            }

            // Atualizar labels
            nomeProdutoLabel.setText(produtoAtual.getNome());
            precoLabel.setText(String.format("R$ %.2f", produtoAtual.getPrecoVenda()));

            // Sair do modo edição
            toggleEdicao();

            // Mostrar mensagem de sucesso
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Sucesso");
            alert.setHeaderText(null);
            alert.setContentText("Produto salvo com sucesso!");
            alert.showAndWait();

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText("Erro ao salvar produto");
            alert.setContentText("Verifique os dados informados.");
            alert.showAndWait();
        }
    }

    @FXML
    private void excluirProduto() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmação");
        alert.setHeaderText("Excluir Produto");
        alert.setContentText("Tem certeza que deseja excluir este produto?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Lógica para excluir produto
                System.out.println("Produto excluído: " + produtoAtual.getNome());

                Alert info = new Alert(Alert.AlertType.INFORMATION);
                info.setTitle("Sucesso");
                info.setHeaderText(null);
                info.setContentText("Produto excluído com sucesso!");
                info.showAndWait();
            }
        });
    }

    @FXML
    private void comprarProduto() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Compra");
        alert.setHeaderText(null);
        alert.setContentText("Produto adicionado ao carrinho!");
        alert.showAndWait();
    }
}
package controller;

import Model.ProdutoModel; // <-- IMPORT CORRIGIDO!
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.StringConverter;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ResourceBundle;

public class ProdutoController implements Initializable {

    // --- Componentes FXML ---
    @FXML private TabPane mainTabPane;

    // Filtros (usados como campos de seleção para a edição)
    @FXML private CheckBox roupasCheck, calcadosCheck, acessoriosCheck;
    @FXML private RadioButton masculinoRadio, femininoRadio, unisexRadio;
    @FXML private CheckBox brancoCheck, pretoCheck, verdeCheck;

    // Campos do produto
    @FXML private TextField nomeField, quantidadeField, precoCustoField, precoVendaField;
    @FXML private TextField dataEntradaField, dataReposicaoField;
    @FXML private Button excluirBtn, editarBtn, salvarBtn;
    @FXML private Label idLabel, nomeProdutoLabel, precoLabel;

    // --- Variáveis de Estado ---
    private ProdutoModel produtoAtual; // TIPO CORRIGIDO
    private boolean editando = false;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarComponentes();
        carregarProdutoExemplo();
        toggleEdicaoCampos(false);
        salvarBtn.setDisable(true);
    }

    private void configurarComponentes() {
        // Configurar ToggleGroup para gênero
        ToggleGroup generoGroup = new ToggleGroup();
        masculinoRadio.setToggleGroup(generoGroup);
        femininoRadio.setToggleGroup(generoGroup);
        unisexRadio.setToggleGroup(generoGroup);

        // Configurar botões
        excluirBtn.setOnAction(e -> excluirProduto());
        editarBtn.setOnAction(e -> toggleEdicao());
        salvarBtn.setOnAction(e -> salvarProduto());

        // Estilizar botões (Cores Bootstrap)
        excluirBtn.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-font-weight: bold;");
        editarBtn.setStyle("-fx-background-color: #ffc107; -fx-text-fill: black; -fx-font-weight: bold;");
        salvarBtn.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-weight: bold;");

        // Adiciona um listener para garantir que apenas uma categoria seja selecionada
        configurarSelecaoCategoria();
    }

    // Garante que apenas um CheckBox de categoria possa estar selecionado por vez.
    private void configurarSelecaoCategoria() {
        CheckBox[] categorias = {roupasCheck, calcadosCheck, acessoriosCheck};
        for (CheckBox cb : categorias) {
            cb.selectedProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal) {
                    for (CheckBox other : categorias) {
                        if (other != cb && other.isSelected()) {
                            other.setSelected(false);
                        }
                    }
                }
            });
        }
    }

    private void carregarProdutoExemplo() {
        // Objeto ProdutoModel de exemplo (usando o construtor da classe ProdutoModel)
        produtoAtual = new ProdutoModel(
                1001,
                "Tênis Nike Air Force 1 '07 Masculino",
                99,
                350.00, // Preço de Custo
                799.99, // Preço de Venda
                LocalDate.of(2023, 10, 15),
                LocalDate.now(), // Data de Reposição
                "Calçados",
                "Masculino",
                "Branco"
        );

        atualizarCamposProduto();
    }

    private void atualizarCamposProduto() {
        if (produtoAtual != null) {
            // Atualiza Labels de Display
            idLabel.setText(String.valueOf(produtoAtual.getId()));
            nomeProdutoLabel.setText(produtoAtual.getNome());
            precoLabel.setText(String.format("R$ %.2f", produtoAtual.getPrecoVenda()));

            // Atualiza Campos de Texto (para edição, sem R$)
            nomeField.setText(produtoAtual.getNome());
            quantidadeField.setText(String.valueOf(produtoAtual.getQuantidade()));
            // Carrega valor sem formatação R$ para facilitar a edição
            precoCustoField.setText(String.format("%.2f", produtoAtual.getPrecoCusto()));
            precoVendaField.setText(String.format("%.2f", produtoAtual.getPrecoVenda()));

            dataEntradaField.setText(produtoAtual.getDataEntrada().format(DATE_FORMATTER));

            if (produtoAtual.getDataReposicao() != null) {
                dataReposicaoField.setText(produtoAtual.getDataReposicao().format(DATE_FORMATTER));
            } else {
                dataReposicaoField.setText("");
            }

            // Atualizar CheckBoxes/RadioButtons baseados no produto
            atualizarFiltrosUI();
        }
    }

    // Define o estado dos CheckBoxes/RadioButtons com base no objeto produtoAtual
    private void atualizarFiltrosUI() {
        // Categoria
        roupasCheck.setSelected("Roupas".equals(produtoAtual.getCategoria()));
        calcadosCheck.setSelected("Calçados".equals(produtoAtual.getCategoria()));
        acessoriosCheck.setSelected("Acessórios".equals(produtoAtual.getCategoria()));

        // Gênero
        masculinoRadio.setSelected("Masculino".equals(produtoAtual.getGenero()));
        femininoRadio.setSelected("Feminino".equals(produtoAtual.getGenero()));
        unisexRadio.setSelected("Unisex".equals(produtoAtual.getGenero()));

        // Cor
        brancoCheck.setSelected("Branco".equals(produtoAtual.getCor()));
        pretoCheck.setSelected("Preto".equals(produtoAtual.getCor()));
        verdeCheck.setSelected("Verde".equals(produtoAtual.getCor()));
    }

    // Lê o estado atual dos CheckBoxes/RadioButtons para o objeto produtoAtual
    private void lerFiltrosParaProduto() {
        // Categoria
        if (roupasCheck.isSelected()) produtoAtual.setCategoria("Roupas");
        else if (calcadosCheck.isSelected()) produtoAtual.setCategoria("Calçados");
        else if (acessoriosCheck.isSelected()) produtoAtual.setCategoria("Acessórios");
        else produtoAtual.setCategoria(""); // Se nenhuma estiver marcada

        // Gênero
        if (masculinoRadio.isSelected()) produtoAtual.setGenero("Masculino");
        else if (femininoRadio.isSelected()) produtoAtual.setGenero("Feminino");
        else if (unisexRadio.isSelected()) produtoAtual.setGenero("Unisex");
        else produtoAtual.setGenero(""); // Se nenhuma estiver marcada

        // Cor
        if (brancoCheck.isSelected()) produtoAtual.setCor("Branco");
        else if (pretoCheck.isSelected()) produtoAtual.setCor("Preto");
        else if (verdeCheck.isSelected()) produtoAtual.setCor("Verde");
        else produtoAtual.setCor(""); // Se nenhuma estiver marcada
    }

    @FXML
    private void toggleEdicao() {
        editando = !editando;
        toggleEdicaoCampos(editando);

        if (editando) {
            editarBtn.setText("Cancelar");
            editarBtn.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white; -fx-font-weight: bold;");
        } else {
            editarBtn.setText("Editar");
            editarBtn.setStyle("-fx-background-color: #ffc107; -fx-text-fill: black; -fx-font-weight: bold;");
            // Recarregar dados originais se o cancelamento for acionado
            atualizarCamposProduto();
        }
    }

    // Habilita/Desabilita todos os campos de edição, incluindo os filtros.
    private void toggleEdicaoCampos(boolean editavel) {
        nomeField.setEditable(editavel);
        quantidadeField.setEditable(editavel);
        precoCustoField.setEditable(editavel);
        precoVendaField.setEditable(editavel);
        dataEntradaField.setEditable(editavel);
        dataReposicaoField.setEditable(editavel);

        // Habilita/Desabilita CheckBoxes e RadioButtons
        roupasCheck.setDisable(!editavel);
        calcadosCheck.setDisable(!editavel);
        acessoriosCheck.setDisable(!editavel);
        masculinoRadio.setDisable(!editavel);
        femininoRadio.setDisable(!editavel);
        unisexRadio.setDisable(!editavel);
        brancoCheck.setDisable(!editavel);
        pretoCheck.setDisable(!editavel);
        verdeCheck.setDisable(!editavel);

        salvarBtn.setDisable(!editavel); // Garante que o botão salvar só esteja ativo em modo edição
    }

    @FXML
    private void salvarProduto() {
        try {
            // 1. Atualizar campos de texto/número
            produtoAtual.setNome(nomeField.getText());
            // Quantidade: aceita apenas números inteiros
            produtoAtual.setQuantidade(Integer.parseInt(quantidadeField.getText().trim()));

            // Preços: assume que o ponto (.) é o separador decimal e remove R$ caso exista
            double custo = Double.parseDouble(precoCustoField.getText().replace("R$", "").replace(",", ".").trim());
            double venda = Double.parseDouble(precoVendaField.getText().replace("R$", "").replace(",", ".").trim());

            produtoAtual.setPrecoCusto(custo);
            produtoAtual.setPrecoVenda(venda);

            // Datas
            produtoAtual.setDataEntrada(LocalDate.parse(dataEntradaField.getText(), DATE_FORMATTER));

            if (!dataReposicaoField.getText().isEmpty()) {
                produtoAtual.setDataReposicao(LocalDate.parse(dataReposicaoField.getText(), DATE_FORMATTER));
            } else {
                produtoAtual.setDataReposicao(null);
            }

            // 2. LER e atualizar as propriedades do produto pelos CheckBoxes/RadioButtons
            lerFiltrosParaProduto();

            // 3. Atualizar Labels de Display e sair do modo edição
            atualizarCamposProduto();
            toggleEdicao();

            // Mostrar mensagem de sucesso
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Sucesso");
            alert.setHeaderText(null);
            alert.setContentText("Produto '" + produtoAtual.getNome() + "' salvo com sucesso!");
            alert.showAndWait();

        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro de Formato");
            alert.setHeaderText("Erro ao processar números");
            alert.setContentText("Verifique se Quantidade, Preço de Custo e Preço de Venda são números válidos.");
            alert.showAndWait();
        } catch (DateTimeParseException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro de Data");
            alert.setHeaderText("Erro ao processar datas");
            alert.setContentText("Verifique se as datas estão no formato dd/MM/yyyy.");
            alert.showAndWait();
        } catch (Exception e) {
            // Catch-all para outros erros
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText("Erro Inesperado ao salvar produto");
            alert.setContentText("Ocorreu um erro: " + e.getMessage());
            alert.showAndWait();
            System.err.println("Erro ao salvar: " + e.getMessage());
        }
    }

    @FXML
    private void excluirProduto() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmação");
        alert.setHeaderText("Excluir Produto: " + produtoAtual.getNome());
        alert.setContentText("Tem certeza que deseja excluir este produto?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Lógica de exclusão simulada
                System.out.println("Produto excluído: " + produtoAtual.getNome() + " (ID: " + produtoAtual.getId() + ")");

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
        // Método mantido (provavelmente ligado via FXML), mas a função é de exemplo.
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Compra");
        alert.setHeaderText(null);
        alert.setContentText("Produto '" + produtoAtual.getNome() + "' adicionado ao carrinho!");
        alert.showAndWait();
    }
}
package controller;

import Dao.ProdutoDAO;
import Model.ProdutoModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ResourceBundle;

public class ProdutoController implements Initializable {

    // --- Componentes FXML ---
    @FXML private TabPane mainTabPane;

    // Filtros
    @FXML private CheckBox roupasCheck, calcadosCheck, acessoriosCheck;
    @FXML private RadioButton masculinoRadio, femininoRadio, unisexRadio;
    @FXML private CheckBox brancoCheck, pretoCheck, verdeCheck;

    // Campos do produto
    @FXML private TextField nomeField, quantidadeField, precoCustoField, precoVendaField;
    @FXML private TextField dataEntradaField, dataReposicaoField;
    @FXML private TextArea descricaoArea;
    @FXML private Button excluirBtn, editarBtn, salvarBtn;
    @FXML private Label idLabel, nomeProdutoLabel, precoLabel;

    // --- Variáveis de Estado ---
    private ProdutoModel produtoAtual;
    private boolean editando = false;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // INTEGRAÇÃO COM DAO
    private final ProdutoDAO produtoDAO = new ProdutoDAO();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarComponentes();
        // Método de inicialização para exemplo. O ideal é usar carregarProduto(id) ou iniciarNovoCadastro()
        carregarProdutoExemplo();
        toggleEdicaoCampos(false);
        salvarBtn.setDisable(true);
    }

    private void configurarComponentes() {
        // ... (Configuração dos ToggleGroup, Listeners, Estilos - Mantido) ...
        ToggleGroup generoGroup = new ToggleGroup();
        masculinoRadio.setToggleGroup(generoGroup);
        femininoRadio.setToggleGroup(generoGroup);
        unisexRadio.setToggleGroup(generoGroup);

        excluirBtn.setOnAction(e -> excluirProduto());
        editarBtn.setOnAction(e -> toggleEdicao());
        salvarBtn.setOnAction(e -> salvarProduto());

        excluirBtn.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-font-weight: bold;");
        editarBtn.setStyle("-fx-background-color: #ffc107; -fx-text-fill: black; -fx-font-weight: bold;");
        salvarBtn.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-weight: bold;");

        configurarSelecaoCategoria();
    }

    private void configurarSelecaoCategoria() {
        // ... (Lógica para seleção de categoria única - Mantida) ...
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

    // Método para inicializar a tela com dados vazios para um NOVO produto
    public void iniciarNovoCadastro() {
        produtoAtual = new ProdutoModel(
                0, // ID 0 indica que este produto é NOVO
                "", 0, 0.0, 0.0,
                LocalDate.now(), null, "", "", ""
        );
        produtoAtual.setDescricao("");

        // Limpar campos de UI e preparar para edição
        idLabel.setText("Novo");
        nomeProdutoLabel.setText("N/A");
        precoLabel.setText("R$ 0,00");
        nomeField.setText("");
        quantidadeField.setText("0");
        precoCustoField.setText("0,00"); // Usando vírgula como padrão na UI
        precoVendaField.setText("0,00"); // Usando vírgula como padrão na UI
        dataEntradaField.setText(LocalDate.now().format(DATE_FORMATTER));
        dataReposicaoField.setText("");
        if (descricaoArea != null) descricaoArea.setText("");

        atualizarFiltrosUI();
        toggleEdicaoCampos(true);
    }

    // Método de exemplo (apenas para simulação)
    private void carregarProdutoExemplo() {
        produtoAtual = new ProdutoModel(
                1001,
                "Tênis Nike Air Force 1 '07 Masculino",
                99,
                350.00,
                799.99,
                LocalDate.of(2023, 10, 15),
                LocalDate.now(),
                "Calçados",
                "Masculino",
                "Branco"
        );
        produtoAtual.setDescricao("Descrição de exemplo.");
        atualizarCamposProduto();
    }

    // Método para carregar um produto REAL do banco (por ID - requer buscarPorId no DAO)
    public void carregarProduto(int id) {
        ProdutoModel produtoDoBanco = produtoDAO.buscarPorId(id);
        if (produtoDoBanco != null) {
            this.produtoAtual = produtoDoBanco;
            atualizarCamposProduto();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Produto ID " + id + " não encontrado.");
            alert.showAndWait();
        }
    }

    private void atualizarCamposProduto() {
        // ... (Lógica de atualização dos campos da UI - Mantida) ...
        if (produtoAtual != null) {
            idLabel.setText(String.valueOf(produtoAtual.getId()));
            nomeProdutoLabel.setText(produtoAtual.getNome());
            precoLabel.setText(String.format("R$ %.2f", produtoAtual.getPrecoVenda()));

            nomeField.setText(produtoAtual.getNome());
            quantidadeField.setText(String.valueOf(produtoAtual.getQuantidade()));
            // Formata com vírgula para a exibição na tela de edição (amigável ao usuário)
            precoCustoField.setText(String.format("%.2f", produtoAtual.getPrecoCusto()).replace(".", ","));
            precoVendaField.setText(String.format("%.2f", produtoAtual.getPrecoVenda()).replace(".", ","));

            if (descricaoArea != null) {
                descricaoArea.setText(produtoAtual.getDescricao());
            }

            dataEntradaField.setText(produtoAtual.getDataEntrada() != null ? produtoAtual.getDataEntrada().format(DATE_FORMATTER) : "");

            if (produtoAtual.getDataReposicao() != null) {
                dataReposicaoField.setText(produtoAtual.getDataReposicao().format(DATE_FORMATTER));
            } else {
                dataReposicaoField.setText("");
            }

            atualizarFiltrosUI();
        }
    }
    // ... (Métodos auxiliares: atualizarFiltrosUI, lerFiltrosParaProduto, toggleEdicao, toggleEdicaoCampos) ...

    private void atualizarFiltrosUI() {
        roupasCheck.setSelected("Roupas".equals(produtoAtual.getCategoria()));
        calcadosCheck.setSelected("Calçados".equals(produtoAtual.getCategoria()));
        acessoriosCheck.setSelected("Acessórios".equals(produtoAtual.getCategoria()));

        masculinoRadio.setSelected("Masculino".equals(produtoAtual.getGenero()));
        femininoRadio.setSelected("Feminino".equals(produtoAtual.getGenero()));
        unisexRadio.setSelected("Unisex".equals(produtoAtual.getGenero()));

        brancoCheck.setSelected("Branco".equals(produtoAtual.getCor()));
        pretoCheck.setSelected("Preto".equals(produtoAtual.getCor()));
        verdeCheck.setSelected("Verde".equals(produtoAtual.getCor()));
    }

    private void lerFiltrosParaProduto() {
        if (roupasCheck.isSelected()) produtoAtual.setCategoria("Roupas");
        else if (calcadosCheck.isSelected()) produtoAtual.setCategoria("Calçados");
        else if (acessoriosCheck.isSelected()) produtoAtual.setCategoria("Acessórios");
        else produtoAtual.setCategoria("");

        if (masculinoRadio.isSelected()) produtoAtual.setGenero("Masculino");
        else if (femininoRadio.isSelected()) produtoAtual.setGenero("Feminino");
        else if (unisexRadio.isSelected()) produtoAtual.setGenero("Unisex");
        else produtoAtual.setGenero("");

        if (brancoCheck.isSelected()) produtoAtual.setCor("Branco");
        else if (pretoCheck.isSelected()) produtoAtual.setCor("Preto");
        else if (verdeCheck.isSelected()) produtoAtual.setCor("Verde");
        else produtoAtual.setCor("");
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
        if (descricaoArea != null) {
            descricaoArea.setEditable(editavel);
        }

        roupasCheck.setDisable(!editavel);
        calcadosCheck.setDisable(!editavel);
        acessoriosCheck.setDisable(!editavel);
        masculinoRadio.setDisable(!editavel);
        femininoRadio.setDisable(!editavel);
        unisexRadio.setDisable(!editavel);
        brancoCheck.setDisable(!editavel);
        pretoCheck.setDisable(!editavel);
        verdeCheck.setDisable(!editavel);

        salvarBtn.setDisable(!editavel);
    }

    @FXML
    private void salvarProduto() {
        if (produtoAtual == null) return;

        try {
            // 1. Atualiza o objeto ProdutoModel com os dados da UI

            // Limpeza e padronização do formato numérico:
            // Remove pontos de milhar, troca vírgula por ponto para o Java (ex: 50.000,00 -> 50000.00)
            String textoCusto = precoCustoField.getText().replace("R$", "").trim();
            String textoVenda = precoVendaField.getText().replace("R$", "").trim();
            String textoQuantidade = quantidadeField.getText().trim();

            textoCusto = textoCusto.replace(".", "").replace(",", ".");
            textoVenda = textoVenda.replace(".", "").replace(",", ".");

            if (textoCusto.isEmpty() || textoVenda.isEmpty() || textoQuantidade.isEmpty()) {
                throw new NumberFormatException("Campos numéricos não podem estar vazios.");
            }

            // Conversão e atribuição ao Model
            produtoAtual.setNome(nomeField.getText());
            produtoAtual.setQuantidade(Integer.parseInt(textoQuantidade));
            produtoAtual.setPrecoCusto(Double.parseDouble(textoCusto));
            produtoAtual.setPrecoVenda(Double.parseDouble(textoVenda));

            if (descricaoArea != null) {
                produtoAtual.setDescricao(descricaoArea.getText());
            }

            // Datas
            produtoAtual.setDataEntrada(LocalDate.parse(dataEntradaField.getText(), DATE_FORMATTER));

            String dataReposicaoTexto = dataReposicaoField.getText().trim();
            if (!dataReposicaoTexto.isEmpty()) {
                produtoAtual.setDataReposicao(LocalDate.parse(dataReposicaoTexto, DATE_FORMATTER));
            } else {
                produtoAtual.setDataReposicao(null);
            }

            lerFiltrosParaProduto();

            // 🚨 2. LÓGICA DE CRIAÇÃO vs. ATUALIZAÇÃO
            boolean sucesso = false;
            String acao = "";

            if (produtoAtual.getId() == 0) { // NOVO PRODUTO
                ProdutoModel novoProduto = produtoDAO.criar(produtoAtual);
                sucesso = (novoProduto != null && novoProduto.getId() > 0);
                if (sucesso) {
                    produtoAtual = novoProduto;
                    acao = "criado";
                }
            } else { // PRODUTO EXISTENTE
                sucesso = produtoDAO.atualizar(produtoAtual);
                acao = "atualizado";
            }

            if (sucesso) {
                // 3. Sucesso e feedback
                atualizarCamposProduto();
                toggleEdicao();

                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Produto '" + produtoAtual.getNome() + "' " + acao + " com sucesso!");
                alert.setTitle("Sucesso");
                alert.setHeaderText(null);
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Erro ao salvar no banco de dados. Verifique a conexão e o console.");
                alert.showAndWait();
            }

        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erro de formato. Verifique se Quantidade é um número inteiro e se os Preços são números válidos (use vírgula ou ponto como separador decimal: ex: 50,00 ou 50.00).");
            alert.showAndWait();
            System.err.println("Erro de conversão numérica: " + e.getMessage());
        } catch (DateTimeParseException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erro de Data. Verifique se as datas estão no formato dd/MM/yyyy.");
            alert.showAndWait();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erro Inesperado ao salvar produto: " + e.getMessage());
            alert.showAndWait();
            System.err.println("Erro geral ao salvar: " + e.getMessage());
        }
    }

    @FXML
    private void excluirProduto() {
        // ... (Lógica de exclusão que chama DAO.excluir(id) - Mantida) ...
        if (produtoAtual == null || produtoAtual.getId() == 0) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmação");
        alert.setHeaderText("Excluir Produto: " + produtoAtual.getNome());
        alert.setContentText("Tem certeza que deseja excluir este produto? Esta ação é permanente.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                boolean sucesso = produtoDAO.excluir(produtoAtual.getId());

                if (sucesso) {
                    Alert info = new Alert(Alert.AlertType.INFORMATION, "Produto excluído com sucesso!");
                    info.showAndWait();
                    iniciarNovoCadastro();
                } else {
                    Alert error = new Alert(Alert.AlertType.ERROR, "Erro ao excluir produto no banco de dados.");
                    error.showAndWait();
                }
            }
        });
    }

    @FXML
    private void comprarProduto() {
        // Mantido
    }
}
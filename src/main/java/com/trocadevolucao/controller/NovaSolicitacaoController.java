package com.trocadevolucao.controller;

import com.trocadevolucao.model.TrocaDevolucao;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;

public class NovaSolicitacaoController {

    // ----------------------------------------------------
    // INJEÇÃO DE CAMPOS DO FXML
    // ----------------------------------------------------
    // Dados da Venda
    @FXML private TextField campoIdVenda;
    @FXML private TextField campoIdCliente; // Novo campo
    @FXML private Button btnBuscarVenda;

    // Tipo de Operação
    @FXML private ToggleGroup tipoOperacaoGroup;
    @FXML private RadioButton radioTroca;
    @FXML private RadioButton radioDevolucao;
    @FXML private Button btnNovoProduto; // Novo botão
    @FXML private Button btnDiferencaValor; // Novo botão

    // Tabela de Produtos (usando tipo genérico 'Object' por enquanto)
    @FXML private TableView<Object> tabelaProdutos;

    // Observações
    @FXML private TextArea campoObservacoes;

    // Forma de Reembolso
    @FXML private ToggleGroup reembolsoGroup;
    @FXML private RadioButton radioPix;
    @FXML private RadioButton radioDinheiro;

    // Botões de Ação
    @FXML private Button btnConfirmar;
    @FXML private Button btnCancelar;

    // ----------------------------------------------------
    // CAMADA DE NEGÓCIO
    // ----------------------------------------------------
    private final SolicitacaoController solicitacaoController = new SolicitacaoController();
    private Stage dialogStage;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    @FXML
    public void initialize() {
        // Desabilitar confirmação se campos essenciais estiverem vazios
        btnConfirmar.disableProperty().bind(campoIdVenda.textProperty().isEmpty());
    }

    // ----------------------------------------------------
    // MÉTODOS DE AÇÃO (Chamados via onAction no FXML)
    // ----------------------------------------------------

    @FXML
    private void buscarVenda() {
        // Lógica para buscar a venda e popular a tabelaProdutos
        String idVenda = campoIdVenda.getText();

        if (idVenda.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Busca", "Informe o ID da Venda.");
            return;
        }

        mostrarAlerta(Alert.AlertType.INFORMATION, "Busca", "Venda " + idVenda + " encontrada. (Simulação)");
    }

    @FXML
    private void adicionarNovoProduto() {
        mostrarAlerta(Alert.AlertType.INFORMATION, "Ação", "Abrir janela para adicionar um novo produto à troca. (A implementar)");
    }

    @FXML
    private void calcularDiferencaValor() {
        mostrarAlerta(Alert.AlertType.INFORMATION, "Ação", "Calcular e registrar a diferença de valor. (A implementar)");
    }

    @FXML
    private void confirmarTrocaDevolucao() {
        try {
            // 1. Coleta e Validação dos Dados
            String idVendaStr = campoIdVenda.getText();

            if (tipoOperacaoGroup.getSelectedToggle() == null || reembolsoGroup.getSelectedToggle() == null) {
                mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Selecione o Tipo de Operação e a Forma de Reembolso.");
                return;
            }

            String tipoOperacao = ((RadioButton) tipoOperacaoGroup.getSelectedToggle()).getText().toUpperCase();
            String formaReembolso = ((RadioButton) reembolsoGroup.getSelectedToggle()).getText().toUpperCase();
            String obs = campoObservacoes.getText();

            // Usando dados fictícios para simular a criação do objeto
            int produtoIdFicticio = 100;
            int quantidadeFicticia = 1;
            double valorTotalFicticio = 250.00;
            String motivoFicticio = "Motivo: " + tipoOperacao;

            // 2. Cria o objeto Model
            TrocaDevolucao novaSolicitacao = new TrocaDevolucao(
                    produtoIdFicticio,
                    "PED-" + idVendaStr,
                    tipoOperacao,
                    motivoFicticio,
                    LocalDate.now(),
                    "PENDENTE",
                    quantidadeFicticia,
                    valorTotalFicticio
            );
            novaSolicitacao.setObservacoes(obs + " | Reembolso: " + formaReembolso);

            // 3. Persiste no Banco
            if (solicitacaoController.salvarNovaSolicitacao(novaSolicitacao)) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Solicitação salva.");
                dialogStage.close();
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Falha ao salvar no MySQL.");
            }

        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Ocorreu um erro: " + e.getMessage());
        }
    }

    @FXML
    private void cancelar() {
        dialogStage.close();
    }

    // ----------------------------------------------------
    // AUXILIAR
    // ----------------------------------------------------
    private void mostrarAlerta(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
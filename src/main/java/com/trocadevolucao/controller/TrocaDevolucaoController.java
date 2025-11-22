package com.trocadevolucao.controller;

import com.trocadevolucao.model.TrocaDevolucao;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class TrocaDevolucaoController implements Initializable {

    @FXML private TabPane mainTabPane;

    // Campos realmente existentes no FXML
    @FXML private TextField nomeField, quantidadeField, precoVendaField;
    @FXML private Label idLabel, nomeProdutoLabel, precoLabel;

    @FXML private TextField motivoField, numeroPedidoField;
    @FXML private DatePicker dataTrocaField;
    @FXML private RadioButton trocaRadio, devolucaoRadio;
    @FXML private ToggleGroup tipoSolicitacaoGroup;
    @FXML private TextArea observacoesArea;
    @FXML private Button excluirBtn, editarBtn, salvarBtn, processarBtn, cancelarBtn;

    private TrocaDevolucao trocaDevolucaoAtual;
    private boolean editando = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarComponentes();
        carregarTrocaDevolucaoExemplo();
        toggleEdicaoCampos(false);
    }

    private void configurarComponentes() {

        // ToggleGroup já vem setado no FXML, NÃO recrie aqui.
        tipoSolicitacaoGroup.selectToggle(trocaRadio);

        excluirBtn.setOnAction(e -> excluirSolicitacao());
        editarBtn.setOnAction(e -> toggleEdicao());
        salvarBtn.setOnAction(e -> salvarSolicitacao());
        processarBtn.setOnAction(e -> processarSolicitacao());
        cancelarBtn.setOnAction(e -> cancelarSolicitacao());

        excluirBtn.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white;");
        editarBtn.setStyle("-fx-background-color: #ffc107; -fx-text-fill: black;");
        salvarBtn.setStyle("-fx-background-color: #28a745; -fx-text-fill: white;");
        processarBtn.setStyle("-fx-background-color: #007bff; -fx-text-fill: white;");
        cancelarBtn.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white;");

        dataTrocaField.setValue(LocalDate.now());
    }

    private void carregarTrocaDevolucaoExemplo() {

        trocaDevolucaoAtual = new TrocaDevolucao(
                99,                       // Produto ID
                "PED-2024-001",           // Número do Pedido
                "TROCA",                  // Tipo
                "Produto com defeito",    // Motivo
                LocalDate.now(),          // Data
                "PENDENTE",               // Status
                1,                        // Quantidade
                799.99                    // Valor Total
        );

        trocaDevolucaoAtual.setId(1);

        atualizarCamposTrocaDevolucao();
    }

    private void atualizarCamposTrocaDevolucao() {
        if (trocaDevolucaoAtual == null) return;

        idLabel.setText(String.valueOf(trocaDevolucaoAtual.getId()));
        nomeProdutoLabel.setText("Produto ID: " + trocaDevolucaoAtual.getProdutoId());
        precoLabel.setText(String.format("R$ %.2f", trocaDevolucaoAtual.getValorTotal()));

        numeroPedidoField.setText(trocaDevolucaoAtual.getNumeroPedido());
        motivoField.setText(trocaDevolucaoAtual.getMotivo());
        observacoesArea.setText(trocaDevolucaoAtual.getObservacoes());
        dataTrocaField.setValue(trocaDevolucaoAtual.getDataSolicitacao());

        if (trocaDevolucaoAtual.isTroca()) trocaRadio.setSelected(true);
        else devolucaoRadio.setSelected(true);

        quantidadeField.setText(String.valueOf(trocaDevolucaoAtual.getQuantidade()));
        double valorUnitario = trocaDevolucaoAtual.getValorTotal() / trocaDevolucaoAtual.getQuantidade();
        precoVendaField.setText(String.format("R$ %.2f", valorUnitario));
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
            atualizarCamposTrocaDevolucao();
        }
    }

    private void toggleEdicaoCampos(boolean editavel) {
        nomeField.setEditable(editavel);
        quantidadeField.setEditable(editavel);
        precoVendaField.setEditable(editavel);

        motivoField.setEditable(editavel);
        numeroPedidoField.setEditable(editavel);
        dataTrocaField.setDisable(!editavel);
        observacoesArea.setEditable(editavel);

        trocaRadio.setDisable(!editavel);
        devolucaoRadio.setDisable(!editavel);
    }

    @FXML
    private void salvarSolicitacao() {
        try {
            if (motivoField.getText().isEmpty() || numeroPedidoField.getText().isEmpty()) {
                alertWarn("Preencha o motivo e número do pedido.");
                return;
            }

            if (!trocaRadio.isSelected() && !devolucaoRadio.isSelected()) {
                alertWarn("Selecione Troca ou Devolução.");
                return;
            }

            trocaDevolucaoAtual.setNumeroPedido(numeroPedidoField.getText());
            trocaDevolucaoAtual.setMotivo(motivoField.getText());
            trocaDevolucaoAtual.setObservacoes(observacoesArea.getText());
            trocaDevolucaoAtual.setDataSolicitacao(dataTrocaField.getValue());

            String tipo = trocaRadio.isSelected() ? "TROCA" : "DEVOLUCAO";
            trocaDevolucaoAtual.setTipoSolicitacao(tipo);

            int quantidade = Integer.parseInt(quantidadeField.getText().replaceAll("[^0-9]", ""));
            double unitario = Double.parseDouble(precoVendaField.getText().replace("R$", "").replace(",", ".").trim());

            trocaDevolucaoAtual.setQuantidade(quantidade);
            trocaDevolucaoAtual.setValorTotal(unitario * quantidade);

            precoLabel.setText(String.format("R$ %.2f", trocaDevolucaoAtual.getValorTotal()));

            toggleEdicao();

            alertInfo("Solicitação salva com sucesso!");

        } catch (Exception e) {
            alertError("Erro ao salvar: " + e.getMessage());
        }
    }

    @FXML
    private void excluirSolicitacao() {
        if (alertConfirm("Tem certeza que deseja excluir?")) {
            alertInfo("Solicitação excluída com sucesso!");
        }
    }

    @FXML
    private void processarSolicitacao() {
        try {
            if (motivoField.getText().isEmpty() || numeroPedidoField.getText().isEmpty()) {
                alertWarn("Preencha todos os campos obrigatórios.");
                return;
            }

            String tipo = trocaRadio.isSelected() ? "Troca" : "Devolução";

            System.out.println("Processando " + tipo + "...");

            alertInfo(tipo + " processada com sucesso!");

        } catch (Exception e) {
            alertError("Erro ao processar.");
        }
    }

    @FXML
    private void cancelarSolicitacao() {
        if (!alertConfirm("Cancelar esta solicitação?")) return;

        motivoField.clear();
        numeroPedidoField.clear();
        observacoesArea.clear();
        dataTrocaField.setValue(LocalDate.now());
        trocaRadio.setSelected(false);
        devolucaoRadio.setSelected(false);

        alertInfo("Solicitação cancelada.");
    }

    // ------------------------
    // Métodos utilitários
    // ------------------------

    private void alertInfo(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    private void alertWarn(String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    private void alertError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setHeaderText("Erro");
        a.setContentText(msg);
        a.showAndWait();
    }

    private boolean alertConfirm(String msg) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setHeaderText(null);
        a.setContentText(msg);
        return a.showAndWait().filter(b -> b == ButtonType.OK).isPresent();
    }

    @FXML
    private void buscarPedido() {
        String numeroPedido = numeroPedidoField.getText();

        if (numeroPedido == null || numeroPedido.isBlank()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aviso");
            alert.setHeaderText("Número do pedido vazio");
            alert.setContentText("Digite um número de pedido para buscar.");
            alert.showAndWait();
            return;
        }

        // Apenas simulação de busca (tira o erro e funciona na UI)
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Busca de Pedido");
        alert.setHeaderText("Resultado da busca");
        alert.setContentText("Pedido '" + numeroPedido + "' localizado!");
        alert.showAndWait();
    }

}

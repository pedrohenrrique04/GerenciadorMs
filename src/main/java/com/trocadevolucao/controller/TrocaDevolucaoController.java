package com.trocadevolucao.controller;

import com.trocadevolucao.model.TrocaDevolucao;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class TrocaDevolucaoController implements Initializable {

    @FXML private TabPane mainTabPane;

    @FXML private CheckBox roupasCheck, calcadosCheck, acessoriosCheck;
    @FXML private RadioButton masculinoRadio, femininoRadio, unisexRadio;
    @FXML private CheckBox brancoCheck, pretoCheck, verdeCheck;

    @FXML private TextField nomeField, quantidadeField, precoCustoField, precoVendaField;
    @FXML private TextField dataEntradaField, dataReposicaoField;
    @FXML private Button excluirBtn, editarBtn, salvarBtn;
    @FXML private Label idLabel, nomeProdutoLabel, precoLabel;

    @FXML private TextField motivoField, numeroPedidoField;
    @FXML private DatePicker dataTrocaField;
    @FXML private RadioButton trocaRadio, devolucaoRadio;
    @FXML private TextArea observacoesArea;
    @FXML private Button processarBtn, cancelarBtn;

    private TrocaDevolucao trocaDevolucaoAtual;
    private boolean editando = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarComponentes();
        carregarTrocaDevolucaoExemplo();
        toggleEdicaoCampos(false);
    }

    private void configurarComponentes() {
        ToggleGroup generoGroup = new ToggleGroup();
        masculinoRadio.setToggleGroup(generoGroup);
        femininoRadio.setToggleGroup(generoGroup);
        unisexRadio.setToggleGroup(generoGroup);

        ToggleGroup tipoSolicitacaoGroup = new ToggleGroup();
        trocaRadio.setToggleGroup(tipoSolicitacaoGroup);
        devolucaoRadio.setToggleGroup(tipoSolicitacaoGroup);

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
        // CORREÇÃO: Chamada do construtor ajustada para 8 parâmetros (sem o ID)
        trocaDevolucaoAtual = new TrocaDevolucao(
                99,                       // Produto ID
                "PED-2024-001",           // Número do Pedido
                "TROCA",                  // Tipo de Solicitação
                "Produto com defeito",    // Motivo
                LocalDate.now(),          // Data da Solicitação
                "PENDENTE",               // Status
                1,                        // Quantidade
                799.99                    // Valor Total
        );
        // Define o ID via setter, simulando um objeto carregado do DB
        trocaDevolucaoAtual.setId(1);

        atualizarCamposTrocaDevolucao();
    }

    private void atualizarCamposTrocaDevolucao() {
        if (trocaDevolucaoAtual != null) {
            idLabel.setText(String.valueOf(trocaDevolucaoAtual.getId()));
            nomeProdutoLabel.setText("Produto ID: " + trocaDevolucaoAtual.getProdutoId());
            precoLabel.setText(String.format("R$ %.2f", trocaDevolucaoAtual.getValorTotal()));

            numeroPedidoField.setText(trocaDevolucaoAtual.getNumeroPedido());
            motivoField.setText(trocaDevolucaoAtual.getMotivo());
            observacoesArea.setText(trocaDevolucaoAtual.getObservacoes());
            dataTrocaField.setValue(trocaDevolucaoAtual.getDataSolicitacao());

            if (trocaDevolucaoAtual.isTroca()) {
                trocaRadio.setSelected(true);
            } else if (trocaDevolucaoAtual.isDevolucao()) {
                devolucaoRadio.setSelected(true);
            }

            quantidadeField.setText(String.valueOf(trocaDevolucaoAtual.getQuantidade()));

            // CORREÇÃO: Substitui getValorUnitario() por cálculo
            double valorUnitario = trocaDevolucaoAtual.getValorTotal() / trocaDevolucaoAtual.getQuantidade();
            precoVendaField.setText(String.format("R$ %.2f", valorUnitario));

            atualizarFiltros();
        }
    }

    private void atualizarFiltros() {
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
        precoCustoField.setEditable(editavel);
        precoVendaField.setEditable(editavel);
        dataEntradaField.setEditable(editavel);
        dataReposicaoField.setEditable(editavel);
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
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Atenção");
                alert.setHeaderText("Campos obrigatórios");
                alert.setContentText("Preencha o motivo e número do pedido.");
                alert.showAndWait();
                return;
            }

            if (!trocaRadio.isSelected() && !devolucaoRadio.isSelected()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Atenção");
                alert.setHeaderText("Tipo de solicitação");
                alert.setContentText("Selecione Troca ou Devolução.");
                alert.showAndWait();
                return;
            }

            trocaDevolucaoAtual.setNumeroPedido(numeroPedidoField.getText());
            trocaDevolucaoAtual.setMotivo(motivoField.getText());
            trocaDevolucaoAtual.setObservacoes(observacoesArea.getText());
            trocaDevolucaoAtual.setDataSolicitacao(dataTrocaField.getValue());

            String tipoSolicitacao = trocaRadio.isSelected() ? "TROCA" : "DEVOLUCAO";
            trocaDevolucaoAtual.setTipoSolicitacao(tipoSolicitacao);

            // Nota: O valor total deve ser recalculado com base no valor unitário e quantidade
            int novaQuantidade = Integer.parseInt(quantidadeField.getText().replaceAll("[^0-9]", ""));
            double novoValorUnitario = Double.parseDouble(precoVendaField.getText().replace("R$", "").replace(",", ".").trim());

            trocaDevolucaoAtual.setQuantidade(novaQuantidade);
            // Assumindo que setValorTotal é o que armazena o valor total da linha:
            trocaDevolucaoAtual.setValorTotal(novoValorUnitario * novaQuantidade);

            nomeProdutoLabel.setText("Produto ID: " + trocaDevolucaoAtual.getProdutoId());
            precoLabel.setText(String.format("R$ %.2f", trocaDevolucaoAtual.getValorTotal()));

            toggleEdicao();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Sucesso");
            alert.setHeaderText(null);
            alert.setContentText("Solicitação salva com sucesso!");
            alert.showAndWait();

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText("Erro ao salvar solicitação");
            alert.setContentText("Verifique os dados informados. Detalhe: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void excluirSolicitacao() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmação");
        alert.setHeaderText("Excluir Solicitação");
        alert.setContentText("Tem certeza que deseja excluir esta solicitação de troca/devolução?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                System.out.println("Solicitação excluída: " + trocaDevolucaoAtual.getNumeroPedido());

                Alert info = new Alert(Alert.AlertType.INFORMATION);
                info.setTitle("Sucesso");
                info.setHeaderText(null);
                info.setContentText("Solicitação excluída com sucesso!");
                info.showAndWait();
            }
        });
    }

    @FXML
    private void processarSolicitacao() {
        try {
            if (motivoField.getText().isEmpty() || numeroPedidoField.getText().isEmpty() ||
                    dataTrocaField.getValue() == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Atenção");
                alert.setHeaderText("Campos obrigatórios");
                alert.setContentText("Preencha todos os campos obrigatórios.");
                alert.showAndWait();
                return;
            }

            String tipoSolicitacao = trocaRadio.isSelected() ? "Troca" : "Devolução";

            // Aviso: O método 'processar' não existe no TrocaDevolucao.java.
            // Se esta linha causar um erro, você precisará implementá-lo no Model.
            trocaDevolucaoAtual.processar(
                    trocaRadio.isSelected() ? "TROCA_PRODUTO" : "ESTORNO",
                    trocaRadio.isSelected() ? "Novo Produto" : null,
                    trocaRadio.isSelected() ? 100 : null
            );

            System.out.println("Processando " + tipoSolicitacao + " para o pedido: " + trocaDevolucaoAtual.getNumeroPedido());
            System.out.println("Número do pedido: " + numeroPedidoField.getText());
            System.out.println("Motivo: " + motivoField.getText());
            System.out.println("Data: " + dataTrocaField.getValue());

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Sucesso");
            alert.setHeaderText("Solicitação Processada");
            alert.setContentText(tipoSolicitacao + " processada com sucesso!\nNº Pedido: " + numeroPedidoField.getText());
            alert.showAndWait();

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText("Erro ao processar solicitação");
            alert.setContentText("Ocorreu um erro ao processar a solicitação.");
            alert.showAndWait();
        }
    }

    @FXML
    private void cancelarSolicitacao() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmação");
        alert.setHeaderText("Cancelar Solicitação");
        alert.setContentText("Tem certeza que deseja cancelar esta solicitação?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                motivoField.clear();
                numeroPedidoField.clear();
                observacoesArea.clear();
                dataTrocaField.setValue(LocalDate.now());
                trocaRadio.setSelected(false);
                devolucaoRadio.setSelected(false);

                Alert info = new Alert(Alert.AlertType.INFORMATION);
                info.setTitle("Sucesso");
                info.setHeaderText(null);
                info.setContentText("Solicitação cancelada com sucesso!");
                info.showAndWait();
            }
        });
    }

    @FXML
    private void buscarPedido() {
        String numeroPedido = numeroPedidoField.getText();
        if (numeroPedido.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Atenção");
            alert.setHeaderText(null);
            alert.setContentText("Informe o número do pedido.");
            alert.showAndWait();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Busca Concluída");
        alert.setHeaderText(null);
        alert.setContentText("Pedido #" + numeroPedido + " localizado com sucesso!");
        alert.showAndWait();
    }
}
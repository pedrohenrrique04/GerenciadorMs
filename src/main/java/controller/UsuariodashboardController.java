package controller;

import Model.Usuariodashboard;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class UsuariodashboardController {

    @FXML
    private VBox listaUsuariosContainer;

    @FXML
    private TextField campoNome;

    @FXML
    private TextField campoSenha;

    @FXML
    private ComboBox<String> comboFuncoes;

    // LISTA AGORA DO TIPO CORRETO
    private ObservableList<Usuariodashboard> listaDeUsuarios =
            FXCollections.observableArrayList();

    @FXML
    public void initialize() {

        comboFuncoes.getItems().setAll(
                "Administrador",
                "Funcionario"
        );
        comboFuncoes.getSelectionModel().selectFirst();
    }

    // =====================================================
    // SALVAR
    // =====================================================

    @FXML
    private void handleSalvarNovoUsuario() {

        String nome = campoNome.getText();
        String senha = campoSenha.getText();
        String funcao = comboFuncoes.getValue();

        if (nome.isEmpty() || senha.isEmpty() || funcao == null) {
            alert("Preencha todos os campos!");
            return;
        }

        // CRIANDO O MODELO CORRETO
        Usuariodashboard user = new Usuariodashboard(nome, senha, funcao);
        listaDeUsuarios.add(0, user);

        renderizarLista();
        handleLimparFormulario();

        alert("Usuário salvo com sucesso!");
    }

    // =====================================================
    // LIMPAR
    // =====================================================

    @FXML
    private void handleLimparFormulario() {
        campoNome.clear();
        campoSenha.clear();
        comboFuncoes.getSelectionModel().selectFirst();
    }

    // =====================================================
    // RENDERIZAR LISTA
    // =====================================================

    private void renderizarLista() {
        listaUsuariosContainer.getChildren().clear();

        for (Usuariodashboard u : listaDeUsuarios) {
            VBox card = new VBox(5);
            card.setStyle(
                    "-fx-padding: 15;" +
                            "-fx-background-color: #F8FAFC;" +
                            "-fx-background-radius: 8;" +
                            "-fx-border-color: #CBD5E1;" +
                            "-fx-border-width: 1;"
            );

            Label lblNome = new Label(u.getNome());
            Label lblInfo = new Label(u.getSenha() + " • " + u.getTipo());

            card.getChildren().addAll(lblNome, lblInfo);

            VBox.setMargin(card, new Insets(5));
            listaUsuariosContainer.getChildren().add(card);
        }
    }

    // =====================================================
    // ALERTA
    // =====================================================

    private void alert(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.showAndWait();
    }
}

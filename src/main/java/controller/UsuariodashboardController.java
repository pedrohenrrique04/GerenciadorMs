package controller;

import Model.Usuariodashboard;
import javafx.fxml.FXML;
import javafx.scene.control.*; // Importa TextField, PasswordField, ComboBox, Alert, etc.
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
    private PasswordField campoSenha; // CORREÇÃO: Usar PasswordField para senhas

    @FXML
    private ComboBox<String> comboFuncoes;

    // Lista temporária (apenas na memória)
    private ObservableList<Usuariodashboard> listaDeUsuarios = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Inicializa o ComboBox
        comboFuncoes.getItems().setAll("Administrador", "Funcionario");
        comboFuncoes.getSelectionModel().selectFirst();

        // DICA: Aqui você chamaria o banco para carregar usuários existentes
        // carregarUsuariosDoBanco();
    }

    // =====================================================
    // SALVAR
    // =====================================================
    @FXML
    private void handleSalvarNovoUsuario() {
        String nome = campoNome.getText();
        String senha = campoSenha.getText();
        String funcao = comboFuncoes.getValue();

        // Validação
        if (nome.isEmpty() || senha.isEmpty() || funcao == null) {
            alert("Preencha todos os campos!");
            return;
        }

        // 1. Cria o objeto Modelo
        Usuariodashboard user = new Usuariodashboard(nome, senha, funcao);

        // ---------------------------------------------------------
        // ⚠ AQUI ENTRARIA A CONEXÃO COM O BANCO DE DADOS (DAO)
        // Exemplo:
        // UsuarioDAO dao = new UsuarioDAO();
        // dao.salvar(user);
        // ---------------------------------------------------------

        // 2. Adiciona na lista visual (para aparecer na tela agora)
        listaDeUsuarios.add(0, user);

        // 3. Atualiza a tela e limpa os campos
        renderizarLista();
        handleLimparFormulario();

        alert("Usuário salvo com sucesso! (Apenas na memória)");
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
    // RENDERIZAR LISTA (VISUAL)
    // =====================================================
    private void renderizarLista() {
        listaUsuariosContainer.getChildren().clear();

        for (Usuariodashboard u : listaDeUsuarios) {
            // Cria o cartão visual para cada usuário
            VBox card = new VBox(5);
            card.setStyle(
                    "-fx-padding: 15;" +
                            "-fx-background-color: #F8FAFC;" +
                            "-fx-background-radius: 8;" +
                            "-fx-border-color: #CBD5E1;" +
                            "-fx-border-width: 1;"
            );

            Label lblNome = new Label(u.getNome());
            lblNome.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

            // Dica: Não é seguro mostrar a senha na lista, mas mantive como você fez
            Label lblInfo = new Label(u.getTipo());
            lblInfo.setStyle("-fx-text-fill: #64748B;");

            card.getChildren().addAll(lblNome, lblInfo);

            VBox.setMargin(card, new Insets(5));
            listaUsuariosContainer.getChildren().add(card);
        }
    }

    private void alert(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.showAndWait();
    }
}
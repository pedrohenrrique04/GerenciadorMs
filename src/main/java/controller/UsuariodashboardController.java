package controller;

import Model.NivelAcesso;
import Model.Usuario; // 🚨 CORREÇÃO: Usar o nome correto da classe Model (Usuario)
import Dao.UsuarioDAO; // 🚨 IMPORTANTE: Importa a classe de persistência
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.List;

public class UsuariodashboardController {

    @FXML
    private VBox listaUsuariosContainer;

    @FXML
    private TextField campoNome;

    @FXML
    private PasswordField campoSenha;

    @FXML
    private ComboBox<String> comboFuncoes;

    // A lista agora reflete o estado do banco e é usada apenas para a visualização
    private ObservableList<Usuario> listaDeUsuarios = FXCollections.observableArrayList();

    // 🚨 INSTÂNCIA DO DAO: Injetamos a dependência
    private UsuarioDAO usuarioDAO = new UsuarioDAO();

    @FXML
    public void initialize() {
        // Inicializa o ComboBox
        comboFuncoes.getItems().setAll("Administrador", "Funcionario");
        comboFuncoes.getSelectionModel().selectFirst();

        // 🚨 CHAMA O BANCO: Carrega usuários existentes assim que a tela abre
        carregarUsuariosDoBanco();
    }

    // =====================================================
    // CARREGAR DADOS DO BANCO
    // =====================================================
    private void carregarUsuariosDoBanco() {
        System.out.println("Buscando usuários existentes no MySQL...");
        List<Usuario> usuarios = usuarioDAO.listarTodos();

        listaDeUsuarios.clear();
        // Adiciona todos os usuários buscados na lista visual
        listaDeUsuarios.addAll(usuarios);

        // Atualiza o VBox visualmente
        renderizarLista();
        System.out.println("Lista de usuários carregada. Total: " + usuarios.size());
    }

    // =====================================================
    // SALVAR (AGORA COM PERSISTÊNCIA NO BANCO)
    // =====================================================
    @FXML
    private void handleSalvarNovoUsuario() {
        String nome = campoNome.getText();
        String senha = campoSenha.getText();
        String funcaoTexto = comboFuncoes.getValue(); // "Administrador" ou "Funcionario"

        // Validação
        if (nome.isEmpty() || senha.isEmpty() || funcaoTexto == null) {
            alert("Preencha todos os campos!");
            return;
        }

        // 1. Converte o texto da função para o Enum
        NivelAcesso nivel = NivelAcesso.fromString(funcaoTexto);

        // 2. Cria o objeto Modelo
        Usuario novoUsuario = new Usuario(nome, senha, nivel);

        // 3. 🚨 PERSISTÊNCIA: Chama o DAO para salvar no MySQL
        if (usuarioDAO.salvar(novoUsuario)) {
            // 4. Atualiza a lista visual
            listaDeUsuarios.add(0, novoUsuario);
            renderizarLista();
            handleLimparFormulario();
            alert("Usuário '" + nome + "' salvo com sucesso no MySQL!");
        } else {
            alert("ERRO ao salvar o usuário no banco de dados. Verifique a conexão.");
        }
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

        for (Usuario u : listaDeUsuarios) {
            // Cria o cartão visual para cada usuário
            VBox card = new VBox(5);

            // Estilo do cartão
            String corBorda = u.getNivelAcesso() == NivelAcesso.ADMINISTRADOR ? "#EF4444" : "#CBD5E1";

            card.setStyle(
                    "-fx-padding: 15;" +
                            "-fx-background-color: #F8FAFC;" +
                            "-fx-background-radius: 8;" +
                            "-fx-border-color: " + corBorda + ";" +
                            "-fx-border-width: 2;"
            );

            Label lblNome = new Label(u.getNome());
            lblNome.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #1E293B;");

            Label lblInfo = new Label("Nível: " + u.getTipo());
            lblInfo.setStyle("-fx-text-fill: #64748B;");

            card.getChildren().addAll(lblNome, lblInfo);

            VBox.setMargin(card, new Insets(5, 0, 5, 0)); // Ajuste de margem
            listaUsuariosContainer.getChildren().add(card);
        }
    }

    private void alert(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.showAndWait();
    }
}
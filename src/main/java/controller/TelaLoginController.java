package controller;

import Dao.UsuarioDAO;
import Model.Usuario; // Seu modelo atualizado com NivelAcesso
import util.SessaoUsuario; // Gerenciador de sessão
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class TelaLoginController {

    @FXML
    private TextField txtUsuario;

    @FXML
    private PasswordField txtSenha;

    @FXML
    private Button btnLogin;

    private UsuarioDAO usuarioDAO = new UsuarioDAO();

    @FXML
    public void initialize() {
        // Vincula o evento de clique do botão ao método de autenticação
        btnLogin.setOnAction(e -> autenticarUsuario());
    }

    private void autenticarUsuario() {
        String nome = txtUsuario.getText();
        String senha = txtSenha.getText();

        if (nome.isEmpty() || senha.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Login", "Por favor, preencha todos os campos.");
            return;
        }

        // 1. Tenta buscar o usuário completo no banco
        Optional<Usuario> usuarioOptional = usuarioDAO.buscarPorNomeESenha(nome, senha);

        if (usuarioOptional.isPresent()) {
            Usuario usuarioLogado = usuarioOptional.get();

            // 2. 🚨 GESTÃO DE SESSÃO: Salva o objeto Usuário completo na sessão
            SessaoUsuario.setUsuarioLogado(usuarioLogado);

            System.out.println("Login realizado com sucesso! Usuário: " + usuarioLogado.getNome() +
                    " | Nível: " + usuarioLogado.getNivelAcesso());

            // 3. Troca de tela para o Dashboard
            trocarParaDashboard(btnLogin);
        } else {
            showAlert(Alert.AlertType.ERROR, "Erro de Login", "Usuário ou senha inválidos!");
        }
    }

    /**
     * Método auxiliar para trocar a tela principal.
     * @param sourceButton O botão que acionou a troca (usado para obter a janela atual).
     */
    private void trocarParaDashboard(Button sourceButton) {
        try {
            // Assume que o seu dashboard.fxml está na pasta /view/
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/dashboard-view.fxml"));
            Parent root = loader.load();

            // Opcional: obter o controller do dashboard se precisar passar dados (normalmente não precisa)
            // DashboardController dashboardController = loader.getController();

            Stage stage = (Stage) sourceButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("GerenciadorMS - Dashboard");
            stage.show();
        } catch (IOException e) {
            System.err.println("Erro ao carregar o dashboard: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erro de Interface", "Não foi possível carregar o dashboard.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // 🚨 REMOVIDO MÉTODO OBSOLETO: A autenticação agora retorna o objeto completo.
    // O método 'cadastrarUsuario' foi removido, pois esta tela não deve ter essa função.
}
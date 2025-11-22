package controller;

import Dao.UsuarioDAO;
import Model.Usuario;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

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
        btnLogin.setOnAction(e -> autenticarUsuario());
    }

    private void autenticarUsuario() {
        String nome = txtUsuario.getText();
        String senha = txtSenha.getText();

        if (autenticar(nome, senha)) {
            System.out.println("Login realizado com sucesso!");
            // aqui você pode trocar de tela
        } else {
            System.out.println("Usuário ou senha inválidos!");
        }
    }

    public boolean autenticar(String nome, String senha) {
        return usuarioDAO.validarLogin(nome, senha);
    }

    public void cadastrarUsuario(String nome, String senha) {
        Usuario usuario = new Usuario(nome, senha);
        usuarioDAO.cadastrarUsuario(usuario);
    }
}

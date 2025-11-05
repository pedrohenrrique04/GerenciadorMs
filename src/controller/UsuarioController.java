package controller;

import Dao.UsuarioDAO;
import Model.Usuario;

public class UsuarioController {
    private UsuarioDAO usuarioDAO = new UsuarioDAO();

    public boolean autenticar(String nome, String senha) {
        return usuarioDAO.validarLogin(nome, senha);
    }

    public void cadastrarUsuario(String nome, String senha) {
        Usuario usuario = new Usuario(nome, senha);
        usuarioDAO.cadastrarUsuario(usuario);
    }
}

package Test;

import Dao.UsuarioDAO;
import Model.Usuario;

public class TesteUsuarioDAO {
    public static void main(String[] args) {
        Usuario u = new Usuario("Pedro", "12345");

        new UsuarioDAO().cadastrarUsuario(u);
    }
}
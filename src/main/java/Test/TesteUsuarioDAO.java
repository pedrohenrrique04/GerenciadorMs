package Test;

import Dao.UsuarioDAO;
import Model.Usuario;

public class TesteUsuarioDAO {
    public static void main(String[] args) {
        Usuario u = new Usuario("verto", "123456");

        new UsuarioDAO().cadastrarUsuario(u);
    }
}
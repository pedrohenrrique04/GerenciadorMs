package Test;

import Model.Usuario;

public class Test {
    public static void main(String[] args) {
        Usuario u = new Usuario("Pedro", "0000000000");
        System.out.println("Nome: " + u.getNome());
        System.out.println("Senha: " + u.getSenha());
    }
}

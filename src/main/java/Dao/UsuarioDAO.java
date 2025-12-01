package Dao;

import Model.Usuario;
import util.Conexao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDAO {
    public void cadastrarUsuario(Usuario usuario){
        String sql = "insert into usuario(nome, senha) values(?,?)";
        PreparedStatement ps = null;

        try {
            ps = Conexao.getConn().prepareStatement(sql);
            ps.setString(1, usuario.getNome()); // 1 é o índice do primeiro '?'
            ps.setString(2, usuario.getSenha()); // 2 é o índice do segundo '?'
            ps.execute();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public boolean validarLogin(String nome, String senha) {
        String sql = "SELECT * FROM usuario WHERE nome = ? AND senha = ?";
        Connection conn = Conexao.getConn();

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, nome);
            ps.setString(2, senha);
            ResultSet rs = ps.executeQuery();

            return rs.next(); // se encontrou o usuário, retorna true
        } catch (SQLException e) {
            System.out.println("Erro ao validar login: " + e.getMessage());
            return false;
        }
    }
}

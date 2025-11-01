package Dao;

import Model.Usuario;
import util.Conexao;

import java.sql.PreparedStatement;
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
}

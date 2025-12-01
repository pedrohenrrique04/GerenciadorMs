package Dao;

import Model.Usuario;
import Model.NivelAcesso;
import util.Conexao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UsuarioDAO {

    // =====================================================
    // CADASTRAR/SALVAR NOVO USUÁRIO
    // 🚨 CORRIGIDO: O Controller chama este método.
    // =====================================================
    public boolean salvar(Usuario usuario) {
        // SQL para inserir, incluindo a coluna nivel_acesso
        String sql = "INSERT INTO usuario (nome, senha, nivel_acesso) VALUES (?, ?, ?)";

        try (Connection conn = Conexao.getConn();
             // Permite obter o ID gerado pelo banco
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, usuario.getNome());
            ps.setString(2, usuario.getSenha());
            ps.setString(3, usuario.getNivelAcesso().name());

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        usuario.setId(rs.getInt(1)); // Define o ID gerado no objeto
                    }
                }
                System.out.println("✅ Usuário '" + usuario.getNome() + "' salvo com sucesso no banco.");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Erro ao salvar novo usuário no banco de dados.");
            e.printStackTrace();
        }
        return false;
    }

    // =====================================================
    // LISTAR TODOS OS USUÁRIOS
    // 🚨 CORRIGIDO: O Controller chama este método.
    // =====================================================
    public List<Usuario> listarTodos() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT id, nome, senha, nivel_acesso FROM usuario ORDER BY nome ASC";

        try (Connection conn = Conexao.getConn();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                NivelAcesso nivel = NivelAcesso.valueOf(rs.getString("nivel_acesso"));

                Usuario usuario = new Usuario(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("senha"),
                        nivel
                );
                usuarios.add(usuario);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar usuários: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Erro ao converter Nível de Acesso do banco: " + e.getMessage());
        }
        return usuarios;
    }

    // =====================================================
    // BUSCAR POR LOGIN (Usado no TelaLoginController)
    // =====================================================
    public Optional<Usuario> buscarPorNomeESenha(String nome, String senha) {
        String sql = "SELECT id, nivel_acesso FROM usuario WHERE nome = ? AND senha = ?";

        try (Connection conn = Conexao.getConn();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nome);
            ps.setString(2, senha);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    NivelAcesso nivel = NivelAcesso.valueOf(rs.getString("nivel_acesso"));

                    Usuario usuarioLogado = new Usuario(
                            rs.getInt("id"),
                            nome,
                            senha,
                            nivel
                    );
                    return Optional.of(usuarioLogado);
                }
            }
        } catch (SQLException | IllegalArgumentException e) {
            System.err.println("Erro durante o processo de login: " + e.getMessage());
        }

        return Optional.empty();
    }

}
package Model;

// 🚨 IMPORTANTE: NivelAcesso deve estar no arquivo Model/NivelAcesso.java
import Model.NivelAcesso;

/**
 * Representa um usuário do sistema (Admin ou Funcionário).
 */
public class Usuario {
    private int id; // ID gerado pelo banco de dados
    private String nome;
    private String senha;
    private NivelAcesso nivelAcesso; // Campo crucial para permissão

    // CONSTRUTOR 1: Para NOVO USUÁRIO (sem ID)
    public Usuario(String nome, String senha, NivelAcesso nivelAcesso) {
        this.nome = nome;
        this.senha = senha;
        this.nivelAcesso = nivelAcesso;
    }

    // CONSTRUTOR 2: Para USUÁRIO EXISTENTE (com ID do banco)
    public Usuario(int id, String nome, String senha, NivelAcesso nivelAcesso) {
        this.id = id;
        this.nome = nome;
        this.senha = senha;
        this.nivelAcesso = nivelAcesso;
    }

    // --- Getters e Setters ---

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public NivelAcesso getNivelAcesso() { return nivelAcesso; }
    public void setNivelAcesso(NivelAcesso nivelAcesso) { this.nivelAcesso = nivelAcesso; }

    // --- Métodos Utilitários ---

    public String getTipo() {
        return this.nivelAcesso.toString();
    }

    // Método rápido para checar se o usuário é Administrador
    public boolean isAdmin() {
        return this.nivelAcesso == NivelAcesso.ADMINISTRADOR;
    }
}
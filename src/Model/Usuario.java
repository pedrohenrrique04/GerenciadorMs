package Model;

/*Essa classe representa um usuário do sistema , ou seja, é uma entidade (ou “modelo”) que reflete uma tabela usuariono banco de dados.*/

public class Usuario {
    private int id;
    private String nome;
    private String senha;

    public Usuario() {}

    public Usuario(String nome, String senha) {
        this.nome = nome;
        this.senha = senha;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}

package Model;

public class Usuariodashboard {
    private String nome;
    private String senha;
    private String tipo;

    public Usuariodashboard(String nome, String senha, String tipo) {
        this.nome = nome;
        this.senha = senha;
        this.tipo = tipo;
    }

    public String getNome() { return nome; }
    public String getSenha() { return senha; }
    public String getTipo() { return tipo; }

    public void setNome(String nome) { this.nome = nome; }
    public void setSenha(String senha) { this.senha = senha; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    @Override
    public String toString() {
        return nome + " (" + tipo + ")";
    }
}

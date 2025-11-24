package util;

import Model.Usuario;

/**
 * Classe estática (Singleton) para gerenciar o estado da sessão do usuário logado.
 * Permite que qualquer Controller acesse as informações do usuário atual.
 */
public final class SessaoUsuario {

    // Armazena o usuário logado. É estático para ser único no sistema.
    private static Usuario usuarioLogado;

    // Construtor privado para evitar que a classe seja instanciada.
    private SessaoUsuario() {
        // Construtor privado
    }

    /**
     * Define o usuário logado após a autenticação bem-sucedida.
     * Deve ser chamada pelo TelaLoginController.
     */
    public static void setUsuarioLogado(Usuario usuario) {
        SessaoUsuario.usuarioLogado = usuario;
    }

    /**
     * Retorna o objeto Usuário atualmente logado.
     */
    public static Usuario getUsuarioLogado() {
        return usuarioLogado;
    }

    /**
     * Verifica se há um usuário logado e se ele tem permissão de Administrador.
     * Esta é a função-chave para a regra de permissão.
     */
    public static boolean isAdmin() {
        return usuarioLogado != null && usuarioLogado.isAdmin();
    }

    /**
     * Limpa a sessão, usado principalmente ao fazer logout.
     */
    public static void limparSessao() {
        usuarioLogado = null;
    }
}
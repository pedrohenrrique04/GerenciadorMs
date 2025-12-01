package Model;

/**
 * Define os níveis de acesso (permissões) dos usuários no sistema.
 * É declarada em um arquivo separado pois é um enum público.
 */
public enum NivelAcesso {

    // Níveis de acesso definidos
    ADMINISTRADOR,
    FUNCIONARIO;

    /**
     * Método utilitário para converter uma String para o Enum NivelAcesso.
     * @param texto A string a ser convertida ("Administrador" ou "Funcionario").
     * @return O valor do Enum correspondente.
     * @throws IllegalArgumentException Se a string não corresponder a nenhum nível válido.
     */
    public static NivelAcesso fromString(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            throw new IllegalArgumentException("O texto do nível de acesso não pode ser nulo ou vazio.");
        }

        if (texto.equalsIgnoreCase(ADMINISTRADOR.name())) {
            return ADMINISTRADOR;
        }
        if (texto.equalsIgnoreCase(FUNCIONARIO.name())) {
            return FUNCIONARIO;
        }

        throw new IllegalArgumentException("Nível de acesso inválido: " + texto);
    }
}
package util;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {

    // Carrega as variáveis de ambiente do arquivo .env
    private static final Dotenv dotenv = Dotenv.load();

    private static final String DRIVER_CLASS = dotenv.get("DB_DRIVER");
    private static final String url = dotenv.get("DB_URL");
    private static final String user = dotenv.get("DB_USER");
    private static final String pass = dotenv.get("DB_PASS");

    static {
        // Bloco estático para garantir que o driver seja carregado
        try {
            if (DRIVER_CLASS != null && !DRIVER_CLASS.isEmpty()) {
                // Tenta carregar a classe do driver dinamicamente
                Class.forName(DRIVER_CLASS);
                System.out.println("✅ Driver JDBC carregado com sucesso: " + DRIVER_CLASS);
            } else {
                System.err.println("❌ Variável DB_DRIVER está ausente ou vazia no .env.");
            }
        } catch (ClassNotFoundException e) {
            // Se o driver não for encontrado (ex: falta o JAR do MySQL Connector)
            System.err.println("❌ Erro fatal: O Driver JDBC (" + DRIVER_CLASS + ") não foi encontrado.");
            throw new RuntimeException("Verifique o JAR do driver no seu classpath.", e);
        }
    }

    /**
     * Tenta estabelecer e retornar uma nova conexão com o banco de dados.
     * Método preferencial, usado em TrocaDevolucaoDAO.
     * @return Objeto Connection se a conexão for bem-sucedida, ou null em caso de falha.
     */
    public static Connection getConnection() {
        if (url == null || user == null || pass == null) {
            System.err.println("❌ Variáveis de ambiente incompletas (URL, USER, ou PASS). Verifique o arquivo .env.");
            return null;
        }
        try {
            // Tenta estabelecer a conexão usando as credenciais do .env
            return DriverManager.getConnection(url, user, pass);
        } catch (SQLException e) {
            System.err.println("❌ Erro ao conectar ao banco de dados. Verifique as credenciais e a URL.");
            System.err.println("Detalhe: " + e.getMessage());
            e.printStackTrace(); // Mantenha o stack trace para diagnóstico
            return null;
        }
    }

    /**
     * Método de compatibilidade. Chama getConnection().
     * Corrige o erro 'cannot find symbol method getConn()' em DAOs antigos.
     * @return Objeto Connection se a conexão for bem-sucedida, ou null em caso de falha.
     * @deprecated Use {@link #getConnection()} em vez disso.
     */
    @Deprecated
    public static Connection getConn() {
        return getConnection();
    }
}
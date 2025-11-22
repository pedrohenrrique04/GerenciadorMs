package com.trocadevolucao.dao;

import io.github.cdimascio.dotenv.Dotenv;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    // Carrega as variáveis do arquivo .env. Certifique-se de que a biblioteca dotenv-java está configurada.
    private static final Dotenv dotenv = Dotenv.load();

    private static final String URL = dotenv.get("DB_URL");
    private static final String USER = dotenv.get("DB_USER");
    private static final String PASSWORD = dotenv.get("DB_PASSWORD");

    /**
     * Retorna uma nova conexão com o banco de dados MySQL.
     * @return Objeto Connection
     */
    public static Connection getConnection() throws SQLException {
        if (URL == null || USER == null || PASSWORD == null) {
            // Este erro é vital para identificar problemas de configuração do .env
            throw new SQLException("Erro de Configuração: As variáveis de ambiente DB_URL, DB_USER ou DB_PASSWORD não foram carregadas do arquivo .env. Verifique o arquivo e a dependência dotenv-java.");
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            System.err.println("Driver JDBC do MySQL não encontrado. Verifique se o Connector/J está no classpath.");
            throw new SQLException("Driver JDBC não encontrado: " + e.getMessage());
        }
    }
}
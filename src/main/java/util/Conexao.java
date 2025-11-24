package util;



import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;

import java.sql.DriverManager;

import java.sql.SQLException;



public class Conexao {

    private static final Dotenv dotenv = Dotenv.load();



    private static final String DRIVER_CLASS = dotenv.get("DB_DRIVER");

    private static final String url = dotenv.get("DB_URL");

    private static final String user = dotenv.get("DB_USER");

    private static final String pass = dotenv.get("DB_PASS");



    static {

// Bloco estático para garantir que o driver seja carregado

        try {

            if (DRIVER_CLASS != null && !DRIVER_CLASS.isEmpty()) {

                Class.forName(DRIVER_CLASS);

                System.out.println("Driver JDBC carregado com sucesso: " + DRIVER_CLASS);

            } else {

                System.err.println(" Variável DB_DRIVER está ausente ou vazia no .env.");

            }

        } catch (ClassNotFoundException e) {

            System.err.println(" Erro fatal: O Driver JDBC (" + DRIVER_CLASS + ") não foi encontrado.");

            throw new RuntimeException("Verifique o JAR do driver MySQL no seu classpath.", e);

        }

    }



    public static Connection getConn() {

        if (url == null || user == null || pass == null) {

            System.err.println(" Variáveis de ambiente incompletas (URL, USER, ou PASS). Verifique o arquivo .env.");

            return null;

        }

        try {

            return DriverManager.getConnection(url, user, pass);

        } catch (SQLException e) {

            System.err.println(" Erro ao conectar ao banco de dados Railway. Verifique as credenciais e a URL.");

            System.err.println(" Detalhe: " + e.getMessage());

// Imprime o stack trace completo para diagnóstico no console

// e.printStackTrace();

            return null;

        }

    }

}
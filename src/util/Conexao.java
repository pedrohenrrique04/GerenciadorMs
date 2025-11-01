package util;

/*Essa classe serve para Abrir uma conexão com o MySQL,
*Retornar essa conexão para qualquer outra classe usar e Avisar se deu erro.
* */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {
    private static final String url = "jdbc:mysql://localhost:3306/meubanco";
    private static final String user = "root";
    private static final String pass = "1234";

    private static Connection conn;

    public static Connection getConn() {
        try {
            if (conn == null) {
                conn = DriverManager.getConnection(url, user, pass);
                return conn;
            } else {
                return conn;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    }

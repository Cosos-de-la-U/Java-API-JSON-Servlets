package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class Conexion {
    private static final String URL = "jdbc:postgresql://localhost:5432/semana04";
    private static final String USUARIO = "postgres";
    private static final String CLAVE = "password";
    public static Connection getConexion() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection(URL, USUARIO, CLAVE);
        } catch (ClassNotFoundException ex) {
            throw new SQLException("No se pudo cargar el driver de la base de datos");
        }
    }
}
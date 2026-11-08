import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/toweltrack_db?useSSL=false&serverTimezone=UTC";
    private static final String USUARIO = "root";
    private static final String SENHA = "";

    public static Connection conectar() {
        try {
            Connection conexao = DriverManager.getConnection(URL, USUARIO, SENHA);
            System.out.println("Verbindung zur Datenbank erfolgreich!");
            return conexao;
        } catch (SQLException e) {
            System.out.println("Fehler bei der Verbindung: " + e.getMessage());
            return null;
        }
    }
}
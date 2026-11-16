
package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL =
            "jdbc:mysql://localhost:3306/toweltrack_db?serverTimezone=UTC";

    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static Connection conectar() {

        try {

            Connection conexao =
                    DriverManager.getConnection(URL, USER, PASSWORD);

            System.out.println(
                    "Verbindung zur Datenbank erfolgreich!"
            );

            return conexao;

        } catch (SQLException e) {

            System.out.println(
                    "Fehler bei der Verbindung: " + e.getMessage()
            );

            return null;
        }
    }
}

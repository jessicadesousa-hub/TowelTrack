
import database.DatabaseConnection;

public class Main {

    public static void main(String[] args) {

        System.out.println("=== TOWELTRACK TEST ===");

        System.out.println(
                "Teste der Datenbankverbindung..."
        );

        DatabaseConnection.conectar();
    }
}

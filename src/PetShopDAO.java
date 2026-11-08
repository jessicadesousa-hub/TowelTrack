import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class PetShopDAO {

    // Neuen Pet Shop in der Datenbank speichern (mit Duplikat-Prüfung)
    public boolean insert(PetShop shop) {
        // Zuerst prüfen, ob bereits ein Shop mit demselben Namen existiert
        String checkSql = "SELECT COUNT(*) FROM pet_shops WHERE name = ?";
        String insertSql = "INSERT INTO pet_shops (name, strasse, hausnummer, plz, stadt, kontakt_email, wochenbedarf_basis, sicherheitsbestand) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conexao = DatabaseConnection.conectar()) {
            assert conexao != null;

            // 1. Duplikat prüfen
            try (PreparedStatement checkStmt = conexao.prepareStatement(checkSql)) {
                checkStmt.setString(1, shop.getName());
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        System.out.println("Duplikat-Warnung: Ein PetShop mit diesem Namen existiert bereits!");
                        return false; // Duplikat gefunden, nicht einfügen
                    }
                }
            }

            // 2. Einfügen, falls kein Duplikat
            try (PreparedStatement stmt = conexao.prepareStatement(insertSql)) {
                stmt.setString(1, shop.getName());
                stmt.setString(2, shop.getStrasse());
                stmt.setString(3, shop.getHausnummer());
                stmt.setString(4, shop.getPlz());
                stmt.setString(5, shop.getStadt());
                stmt.setString(6, shop.getKontaktEmail());
                stmt.setInt(7, shop.getWochenbedarfBasis());
                stmt.setInt(8, shop.getSicherheitsbestand());

                stmt.executeUpdate();
                System.out.println("PetShop erfolgreich in der Datenbank gespeichert!");
                return true;
            }

        } catch (SQLException e) {
            System.out.println("Fehler beim Speichern: " + e.getMessage());
            return false;
        }
    }

    // Alle Pet Shops aus der Datenbank abrufen
    public List<PetShop> listarTodos() {
        List<PetShop> lista = new ArrayList<>();
        String sql = "SELECT * FROM pet_shops";

        try (Connection conexao = DatabaseConnection.conectar()) {
            assert conexao != null;
            try (PreparedStatement stmt = conexao.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    PetShop shop = new PetShop(
                            rs.getString("name"),
                            rs.getString("strasse"),
                            rs.getString("hausnummer"),
                            rs.getString("plz"),
                            rs.getString("stadt"),
                            rs.getString("kontakt_email"),
                            rs.getInt("wochenbedarf_basis")
                    );

                    // Wichtig: Die ID aus der Datenbank explizit setzen, damit das Löschen funktioniert!
                    shop.setShopId(rs.getInt("shop_id"));

                    // Falls der Sicherheitsbestand explizit aus der DB geladen werden soll:
                    shop.setSicherheitsbestand(rs.getInt("sicherheitsbestand"));

                    lista.add(shop);
                }
            }
        } catch (SQLException e) {
            System.out.println("Fehler beim Abrufen der Daten: " + e.getMessage());
        }

        return lista;
    }

    // Pet Shop anhand der ID löschen
    public void delete(int id) {
        String sql = "DELETE FROM pet_shops WHERE shop_id = ?";
        try (Connection conexao = DatabaseConnection.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            System.out.println("PetShop erfolgreich gelöscht!");
        } catch (SQLException e) {
            System.out.println("Fehler beim Löschen: " + e.getMessage());
        }
    }
}
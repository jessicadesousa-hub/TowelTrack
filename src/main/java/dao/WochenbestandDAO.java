
package dao;

import database.DatabaseConnection;
import model.Wochenbestand;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class WochenbestandDAO {

    public boolean speichern(Wochenbestand bestand) {
        if (bereitsExist(bestand.getShopId(),bestand.getErfassungsdatum())) {
            return false;
        }
        String sql = """
                INSERT INTO wochenbestand
                (shop_id, erfassungsdatum, restbestand)
                VALUES (?, ?, ?)
                """;

        try (Connection conexao = DatabaseConnection.conectar()) {

            if (conexao == null) return false;

            try (PreparedStatement stmt =
                         conexao.prepareStatement(sql)) {

                stmt.setInt(1, bestand.getShopId());

                stmt.setDate(
                        2,
                        Date.valueOf(bestand.getErfassungsdatum())
                );

                stmt.setInt(3, bestand.getRestbestand());

                int resultado = stmt.executeUpdate();

                return resultado > 0;
            }

        } catch (SQLException e) {

            System.out.println(
                    "Fehler beim Speichern des Bestands: "
                            + e.getMessage()
            );

            return false;
        }
    }

    public Wochenbestand letzterBestand(int shopId) {

        String sql = """
                SELECT * FROM wochenbestand
                WHERE shop_id = ?
                ORDER BY erfassungsdatum DESC, bestand_id DESC
                LIMIT 1
                """;

        try (Connection conexao = DatabaseConnection.conectar()) {

            if (conexao == null) return null;

            try (PreparedStatement stmt =
                         conexao.prepareStatement(sql)) {

                stmt.setInt(1, shopId);

                try (ResultSet rs = stmt.executeQuery()) {

                    if (rs.next()) {

                        return ergebnisZuWochenbestand(rs);
                    }
                }
            }

        } catch (SQLException e) {

            System.out.println(
                    "Fehler beim Abrufen des Bestands: " + e.getMessage()
            );
        }

        return null;
    }

    public List<Wochenbestand> alleAuflisten(int shopId) {

        List<Wochenbestand> liste = new ArrayList<>();

        String sql = """
                SELECT * FROM wochenbestand
                WHERE shop_id = ?
                ORDER BY erfassungsdatum DESC
                """;

        try (Connection conexao = DatabaseConnection.conectar()) {

            if (conexao == null) return liste;

            try (PreparedStatement stmt =
                         conexao.prepareStatement(sql)) {

                stmt.setInt(1, shopId);

                try (ResultSet rs = stmt.executeQuery()) {

                    while (rs.next()) {
                        liste.add(ergebnisZuWochenbestand(rs));
                    }
                }
            }

        } catch (SQLException e) {

            System.out.println(
                    "Fehler beim Abrufen der Bestandshistorie: "
                            + e.getMessage()
            );
        }

        return liste;
    }

    private Wochenbestand ergebnisZuWochenbestand(ResultSet rs)
            throws SQLException {

        Wochenbestand bestand = new Wochenbestand(
                rs.getInt("shop_id"),
                rs.getDate("erfassungsdatum").toLocalDate(),
                rs.getInt("restbestand")
        );

        bestand.setBestandId(rs.getInt("bestand_id"));

        return bestand;
    }

    public boolean bereitsExist(int shopId, LocalDate data) {

        String sql = """
            SELECT COUNT(*)
            FROM wochenbestand
            WHERE shop_id = ?
            AND erfassungsdatum = ?
            """;

        try (Connection conexao = DatabaseConnection.conectar()) {

            if (conexao == null) return false;

            try (PreparedStatement stmt = conexao.prepareStatement(sql)) {

                stmt.setInt(1, shopId);
                stmt.setDate(2, Date.valueOf(String.valueOf(data)));

                try (ResultSet rs = stmt.executeQuery()) {

                    if (rs.next()) {
                        return rs.getInt(1) > 0;
                    }
                }

            }

        } catch (SQLException e) {

            System.out.println(
                    "Fehler bei der Bestandsprüfung: " + e.getMessage()
            );
        }

        return false;
    }

    public Wochenbestand letzterBestandBisDatum(
            int shopId,
            LocalDate datum
    ) {

        String sql = """
            SELECT *
            FROM wochenbestand
            WHERE shop_id = ?
            AND erfassungsdatum <= ?
            ORDER BY erfassungsdatum DESC, bestand_id DESC
            LIMIT 1
            """;

        try (Connection conexao = DatabaseConnection.conectar()) {

            if (conexao == null) return null;

            try (PreparedStatement stmt = conexao.prepareStatement(sql)) {

                stmt.setInt(1, shopId);
                stmt.setDate(2, Date.valueOf(datum));

                try (ResultSet rs = stmt.executeQuery()) {

                    if (rs.next()) {
                        return ergebnisZuWochenbestand(rs);
                    }
                }
            }

        } catch (SQLException e) {

            System.out.println(
                    "Fehler beim Suchen des Bestands: " + e.getMessage()
            );
        }

        return null;
    }
}

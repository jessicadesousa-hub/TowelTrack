
package dao;

import database.DatabaseConnection;
import model.Lieferung;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LieferungDAO {

    public boolean speichern(Lieferung lieferung) {

        String sql = """
                INSERT INTO lieferungen
                (shop_id, bestand_id, lieferdatum,
                 gelieferte_anzahl, ist_sonderlieferung)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection conexao = DatabaseConnection.conectar()) {

            if (conexao == null) return false;

            try (PreparedStatement stmt =
                         conexao.prepareStatement(sql)) {

                stmt.setInt(1, lieferung.getShopId());

                if (lieferung.getBestandId() == null) {
                    stmt.setNull(2, Types.INTEGER);
                } else {
                    stmt.setInt(2, lieferung.getBestandId());
                }

                stmt.setDate(
                        3,
                        Date.valueOf(lieferung.getLieferdatum())
                );

                stmt.setInt(4, lieferung.getGelieferteAnzahl());

                stmt.setBoolean(5, lieferung.isSonderlieferung());

                return stmt.executeUpdate() > 0;
            }

        } catch (SQLException e) {

            System.out.println(
                    "Fehler beim Speichern der Lieferung: "
                            + e.getMessage()
            );

            return false;
        }
    }

    public List<Lieferung> alleAuflisten(int shopId) {

        List<Lieferung> liste = new ArrayList<>();

        String sql = """
                SELECT * FROM lieferungen
                WHERE shop_id = ?
                ORDER BY lieferdatum DESC
                """;

        try (Connection conexao = DatabaseConnection.conectar()) {

            if (conexao == null) return liste;

            try (PreparedStatement stmt =
                         conexao.prepareStatement(sql)) {

                stmt.setInt(1, shopId);

                try (ResultSet rs = stmt.executeQuery()) {

                    while (rs.next()) {

                        int bestandId = rs.getInt("bestand_id");

                        Integer id = rs.wasNull() ? null : bestandId;

                        Lieferung lieferung = new Lieferung(
                                rs.getInt("shop_id"),
                                id,
                                rs.getDate("lieferdatum").toLocalDate(),
                                rs.getInt("gelieferte_anzahl"),
                                rs.getBoolean("ist_sonderlieferung")
                        );

                        lieferung.setLieferungId(
                                rs.getInt("lieferung_id")
                        );

                        liste.add(lieferung);
                    }
                }
            }

        } catch (SQLException e) {

            System.out.println(
                    "Fehler beim Abrufen der Lieferhistorie: "
                            + e.getMessage()
            );
        }

        return liste;
    }
}

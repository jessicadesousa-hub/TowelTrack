
package dao;

import database.DatabaseConnection;
import model.Bedarfshistorie;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BedarfshistorieDAO {

    public List<Bedarfshistorie> alleAuflisten(int shopId) {

        List<Bedarfshistorie> liste = new ArrayList<>();

        String sql = """
                SELECT * FROM bedarfshistorie
                WHERE shop_id = ?
                ORDER BY aenderungsdatum DESC
                """;

        try (Connection conexao = DatabaseConnection.conectar()) {

            if (conexao == null) return liste;

            try (PreparedStatement stmt =
                         conexao.prepareStatement(sql)) {

                stmt.setInt(1, shopId);

                try (ResultSet rs = stmt.executeQuery()) {

                    while (rs.next()) {

                        Bedarfshistorie eintrag = new Bedarfshistorie(
                                rs.getInt("shop_id"),
                                (Integer) rs.getObject("vorheriger_wochenbedarf"),
                                rs.getInt("neuer_wochenbedarf")
                        );

                        eintrag.setBedarfId(rs.getInt("bedarf_id"));

                        Timestamp timestamp =
                                rs.getTimestamp("aenderungsdatum");

                        if (timestamp != null) {
                            eintrag.setAenderungsdatum(
                                    timestamp.toLocalDateTime()
                            );
                        }

                        liste.add(eintrag);
                    }
                }
            }

        } catch (SQLException e) {

            System.out.println(
                    "Fehler beim Abrufen der Bedarfshistorie: "
                            + e.getMessage()
            );
        }

        return liste;
    }
}

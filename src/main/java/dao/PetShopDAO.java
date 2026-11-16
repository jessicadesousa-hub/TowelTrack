
package dao;

import database.DatabaseConnection;
import model.PetShop;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PetShopDAO {

    public boolean insert(PetShop shop) {

        String checkSql =
                "SELECT COUNT(*) FROM pet_shops WHERE name = ?";

        String insertSql = """
                INSERT INTO pet_shops
                (name, strasse, hausnummer, plz, stadt,
                 kontakt_email, wochenbedarf, sicherheitsprozent, aktiv)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        String historySql = """
                INSERT INTO bedarfshistorie
                (shop_id, vorheriger_wochenbedarf, neuer_wochenbedarf)
                VALUES (?, NULL, ?)
                """;

        try (Connection conexao = DatabaseConnection.conectar()) {

            if (conexao == null) return false;

            conexao.setAutoCommit(false);

            try {

                try (PreparedStatement check =
                             conexao.prepareStatement(checkSql)) {

                    check.setString(1, shop.getName());

                    try (ResultSet rs = check.executeQuery()) {

                        if (rs.next() && rs.getInt(1) > 0) {
                            conexao.rollback();
                            return false;
                        }
                    }
                }

                int shopId;

                try (PreparedStatement stmt =
                             conexao.prepareStatement(
                                     insertSql,
                                     Statement.RETURN_GENERATED_KEYS
                             )) {

                    preencherStatement(stmt, shop);

                    stmt.executeUpdate();

                    try (ResultSet rs = stmt.getGeneratedKeys()) {

                        if (!rs.next()) {
                            conexao.rollback();
                            return false;
                        }

                        shopId = rs.getInt(1);
                    }
                }

                try (PreparedStatement stmt =
                             conexao.prepareStatement(historySql)) {

                    stmt.setInt(1, shopId);
                    stmt.setInt(2, shop.getWochenbedarf());

                    stmt.executeUpdate();
                }

                conexao.commit();

                shop.setShopId(shopId);

                return true;

            } catch (SQLException e) {

                conexao.rollback();
                throw e;
            }

        } catch (SQLException e) {

            System.out.println("Fehler beim Speichern: " + e.getMessage());
            return false;
        }
    }

    public List<PetShop> alleAuflisten() {

        List<PetShop> liste = new ArrayList<>();

        String sql = "SELECT * FROM pet_shops";

        try (Connection conexao = DatabaseConnection.conectar()) {

            if (conexao == null) return liste;

            try (PreparedStatement stmt =
                         conexao.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    liste.add(ergebnisZuPetShop(rs));
                }
            }

        } catch (SQLException e) {

            System.out.println("Fehler beim Abrufen: " + e.getMessage());
        }

        return liste;
    }

    public PetShop idSuchen(int id) {

        String sql =
                "SELECT * FROM pet_shops WHERE shop_id = ?";

        try (Connection conexao = DatabaseConnection.conectar()) {

            if (conexao == null) return null;

            try (PreparedStatement stmt =
                         conexao.prepareStatement(sql)) {

                stmt.setInt(1, id);

                try (ResultSet rs = stmt.executeQuery()) {

                    if (rs.next()) {
                        return ergebnisZuPetShop(rs);
                    }
                }
            }

        } catch (SQLException e) {

            System.out.println("Fehler bei der Suche: " + e.getMessage());
        }

        return null;
    }

    public boolean aktualisieren(PetShop shop) {

        String selectSql =
                "SELECT wochenbedarf FROM pet_shops WHERE shop_id = ?";

        String updateSql = """
                UPDATE pet_shops
                SET name = ?, strasse = ?, hausnummer = ?,
                    plz = ?, stadt = ?, kontakt_email = ?,
                    wochenbedarf = ?, sicherheitsprozent = ?, aktiv = ?
                WHERE shop_id = ?
                """;

        String historySql = """
                INSERT INTO bedarfshistorie
                (shop_id, vorheriger_wochenbedarf, neuer_wochenbedarf)
                VALUES (?, ?, ?)
                """;

        try (Connection conexao = DatabaseConnection.conectar()) {

            if (conexao == null) return false;

            conexao.setAutoCommit(false);

            try {

                int alterBedarf;

                try (PreparedStatement stmt =
                             conexao.prepareStatement(selectSql)) {

                    stmt.setInt(1, shop.getShopId());

                    try (ResultSet rs = stmt.executeQuery()) {

                        if (!rs.next()) {
                            conexao.rollback();
                            return false;
                        }

                        alterBedarf = rs.getInt("wochenbedarf");
                    }
                }

                try (PreparedStatement stmt =
                             conexao.prepareStatement(updateSql)) {

                    preencherStatement(stmt, shop);

                    stmt.setInt(10, shop.getShopId());

                    stmt.executeUpdate();
                }

                if (alterBedarf != shop.getWochenbedarf()) {

                    try (PreparedStatement stmt =
                                 conexao.prepareStatement(historySql)) {

                        stmt.setInt(1, shop.getShopId());
                        stmt.setInt(2, alterBedarf);
                        stmt.setInt(3, shop.getWochenbedarf());

                        stmt.executeUpdate();
                    }
                }

                conexao.commit();

                return true;

            } catch (SQLException e) {

                conexao.rollback();
                throw e;
            }

        } catch (SQLException e) {

            System.out.println(
                    "Fehler beim Aktualisieren: " + e.getMessage()
            );

            return false;
        }
    }

    public boolean deaktivieren(int id) {

        String sql =
                "UPDATE pet_shops SET aktiv = FALSE WHERE shop_id = ?";

        try (Connection conexao = DatabaseConnection.conectar()) {

            if (conexao == null) return false;

            try (PreparedStatement stmt =
                         conexao.prepareStatement(sql)) {

                stmt.setInt(1, id);

                return stmt.executeUpdate() > 0;
            }

        } catch (SQLException e) {

            System.out.println("Fehler beim Deaktivieren: " + e.getMessage());
            return false;
        }
    }


    private PetShop ergebnisZuPetShop(ResultSet rs)
            throws SQLException {

        PetShop shop = new PetShop(
                rs.getString("name"),
                rs.getString("strasse"),
                rs.getString("hausnummer"),
                rs.getString("plz"),
                rs.getString("stadt"),
                rs.getString("kontakt_email"),
                rs.getInt("wochenbedarf")
        );

        shop.setShopId(rs.getInt("shop_id"));
        shop.setSicherheitsprozent(
                rs.getDouble("sicherheitsprozent")
        );
        shop.setAktiv(rs.getBoolean("aktiv"));

        return shop;
    }

    private void preencherStatement(
            PreparedStatement stmt,
            PetShop shop
    ) throws SQLException {

        stmt.setString(1, shop.getName());
        stmt.setString(2, shop.getStrasse());
        stmt.setString(3, shop.getHausnummer());
        stmt.setString(4, shop.getPlz());
        stmt.setString(5, shop.getStadt());
        stmt.setString(6, shop.getKontaktEmail());
        stmt.setInt(7, shop.getWochenbedarf());
        stmt.setDouble(8, shop.getSicherheitsprozent());
        stmt.setBoolean(9, shop.isAktiv());
    }
}


import dao.PetShopDAO;
import dao.BedarfshistorieDAO;
import dao.WochenbestandDAO;
import dao.LieferungDAO;

import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;

import model.PetShop;
import model.Wochenbestand;
import model.Lieferung;


public class Server {

    public static void main(String[] args) {

        Javalin app = Javalin.create(config -> {
            config.staticFiles.add("public", Location.EXTERNAL);
        }).start(7000);

        PetShopDAO petShopDAO = new PetShopDAO();
        BedarfshistorieDAO bedarfshistorieDAO =
                new BedarfshistorieDAO();
        WochenbestandDAO wochenbestandDAO =
                new WochenbestandDAO();
        LieferungDAO lieferungDAO =
                new LieferungDAO();


        // Startseite
        app.get("/", ctx -> ctx.redirect("/index.html"));


        // Alle Pet Shops auflisten
        app.get("/api/petshops", ctx ->
                ctx.json(petShopDAO.alleAuflisten())
        );


        // Neuen Pet Shop hinzufügen
        app.post("/api/petshops", ctx -> {

            PetShop neuerShop = ctx.bodyAsClass(PetShop.class);

            if (neuerShop.getWochenbedarf() < 0 ||
                    !Double.isFinite(neuerShop.getSicherheitsprozent()) ||
                    neuerShop.getSicherheitsprozent() < 0) {

                ctx.status(400).result(
                        "Negative oder ungültige Werte sind nicht erlaubt."
                );
                return;
            }

            if (neuerShop.getSicherheitsprozent() == 0) {
                neuerShop.setSicherheitsprozent(15.00);
            }

            neuerShop.setAktiv(true);

            boolean erfolgreich = petShopDAO.insert(neuerShop);

            if (erfolgreich) {

                ctx.status(201).result(
                        "Pet Shop erfolgreich erstellt!"
                );

            } else {

                ctx.status(400).result(
                        "Pet Shop konnte nicht gespeichert werden."
                );
            }
        });

        // Liefermenge berechnen
        app.get("/api/petshops/lieferung-berechnen", ctx -> {

            try {

                int wochenbedarf = Integer.parseInt(
                        ctx.queryParam("basis")
                );

                int restbestand = Integer.parseInt(
                        ctx.queryParam("restbestand")
                );

                double sicherheitsprozent = Double.parseDouble(
                        ctx.queryParam("sicherheitsprozent")
                );

                if (wochenbedarf < 0 ||
                        restbestand < 0 ||
                        !Double.isFinite(sicherheitsprozent) ||
                        sicherheitsprozent < 0) {

                    ctx.status(400).result("Ungültige Werte.");
                    return;
                }

                int bedarfMitSicherheit = (int) Math.round(
                        wochenbedarf *
                                (1 + sicherheitsprozent / 100)
                );

                int endgueltigeLieferung = Math.max(
                        0,
                        bedarfMitSicherheit - restbestand
                );

                ctx.json(new LieferungsErgebnis(
                        wochenbedarf,
                        restbestand,
                        sicherheitsprozent,
                        bedarfMitSicherheit,
                        endgueltigeLieferung
                ));

            } catch (NumberFormatException e) {

                ctx.status(400).result(
                        "Bitte gültige Zahlen angeben."
                );
            }
        });

        // Pet Shop anhand der ID suchen
        app.get("/api/petshops/{id}", ctx -> {

            try {

                int id = Integer.parseInt(ctx.pathParam("id"));

                PetShop shop = petShopDAO.idSuchen(id);

                if (shop == null) {
                    ctx.status(404).result("Pet Shop nicht gefunden.");
                    return;
                }

                ctx.json(shop);

            } catch (NumberFormatException e) {
                ctx.status(400).result("Ungültige Shop-ID.");
            }
        });


        // Pet Shop aktualisieren
        app.put("/api/petshops/{id}", ctx -> {

            try {

                int id = Integer.parseInt(ctx.pathParam("id"));

                PetShop shop = ctx.bodyAsClass(PetShop.class);

                if (shop.getWochenbedarf() < 0 ||
                        !Double.isFinite(shop.getSicherheitsprozent()) ||
                        shop.getSicherheitsprozent() < 0) {

                    ctx.status(400).result("Ungültige Werte.");
                    return;
                }

                shop.setShopId(id);

                boolean erfolgreich =
                        petShopDAO.aktualisieren(shop);

                if (erfolgreich) {
                    ctx.result("Pet Shop erfolgreich aktualisiert!");
                } else {
                    ctx.status(404).result("Pet Shop nicht gefunden.");
                }

            } catch (NumberFormatException e) {
                ctx.status(400).result("Ungültige Shop-ID.");
            }
        });


        // Pet Shop deaktivieren
        app.patch("/api/petshops/{id}/deaktivieren", ctx -> {

            try {

                int id = Integer.parseInt(ctx.pathParam("id"));

                boolean erfolgreich = petShopDAO.deaktivieren(id);

                if (erfolgreich) {
                    ctx.result("Pet Shop erfolgreich deaktiviert!");
                } else {
                    ctx.status(404).result("Pet Shop nicht gefunden.");
                }

            } catch (NumberFormatException e) {
                ctx.status(400).result("Ungültige Shop-ID.");
            }
        });





        // Wochenbestand speichern
        app.post("/api/petshops/{id}/wochenbestand", ctx -> {

            try {

                int shopId = Integer.parseInt(ctx.pathParam("id"));

                if (petShopDAO.idSuchen(shopId) == null) {
                    ctx.status(404).result("Pet Shop nicht gefunden.");
                    return;
                }

                Wochenbestand bestand =
                        ctx.bodyAsClass(Wochenbestand.class);

                if (bestand.getRestbestand() < 0 ||
                        bestand.getErfassungsdatum() == null) {

                    ctx.status(400).result("Ungültige Bestandsdaten.");
                    return;
                }

                bestand.setShopId(shopId);
                if (wochenbestandDAO.bereitsExist(shopId, bestand.getErfassungsdatum())) {
                    ctx.status(409).result(
                            "Für diesen Pet Shop existiert bereits ein Bestand für dieses Datum."
                    );
                    return;
                }

                boolean erfolgreich =
                        wochenbestandDAO.speichern(bestand);

                if (erfolgreich) {
                    ctx.status(201).result(
                            "Wochenbestand erfolgreich gespeichert!"
                    );
                } else {
                    ctx.status(400).result(
                            "Wochenbestand konnte nicht gespeichert werden."
                    );
                }

            } catch (NumberFormatException e) {
                ctx.status(400).result("Ungültige Shop-ID.");
            }
        });


        // Wochenbestand abrufen
        app.get("/api/petshops/{id}/wochenbestand", ctx -> {

            try {

                int shopId = Integer.parseInt(ctx.pathParam("id"));

                ctx.json(wochenbestandDAO.alleAuflisten(shopId));

            } catch (NumberFormatException e) {
                ctx.status(400).result("Ungültige Shop-ID.");
            }
        });


        // Bedarfshistorie abrufen
        app.get("/api/petshops/{id}/bedarfshistorie", ctx -> {

            try {

                int shopId = Integer.parseInt(ctx.pathParam("id"));

                ctx.json(
                        bedarfshistorieDAO.alleAuflisten(shopId)
                );

            } catch (NumberFormatException e) {
                ctx.status(400).result("Ungültige Shop-ID.");
            }
        });


        // Lieferung speichern
        app.post("/api/petshops/{id}/lieferungen", ctx -> {

            try {

                int shopId = Integer.parseInt(ctx.pathParam("id"));

                if (petShopDAO.idSuchen(shopId) == null) {
                    ctx.status(404).result("Pet Shop nicht gefunden.");
                    return;
                }

                Lieferung lieferung =
                        ctx.bodyAsClass(Lieferung.class);

                if (lieferung.getGelieferteAnzahl() < 0 ||
                        lieferung.getLieferdatum() == null) {

                    ctx.status(400).result("Ungültige Lieferdaten.");
                    return;
                }

                if (lieferung.getBestandId() != null) {

                    boolean bestandGefunden =
                            wochenbestandDAO.alleAuflisten(shopId)
                                    .stream()
                                    .anyMatch(b ->
                                            b.getBestandId() ==
                                                    lieferung.getBestandId()
                                    );

                    if (!bestandGefunden) {
                        ctx.status(400).result(
                                "Bestand gehört nicht zu diesem Pet Shop."
                        );
                        return;
                    }
                }

                lieferung.setShopId(shopId);

                boolean erfolgreich =
                        lieferungDAO.speichern(lieferung);

                if (erfolgreich) {
                    ctx.status(201).result(
                            "Lieferung erfolgreich gespeichert!"
                    );
                } else {
                    ctx.status(400).result(
                            "Lieferung konnte nicht gespeichert werden."
                    );
                }

            } catch (NumberFormatException e) {
                ctx.status(400).result("Ungültige Shop-ID.");
            }
        });


        // Lieferhistorie abrufen
        app.get("/api/petshops/{id}/lieferungen", ctx -> {

            try {

                int shopId = Integer.parseInt(ctx.pathParam("id"));

                ctx.json(lieferungDAO.alleAuflisten(shopId));

            } catch (NumberFormatException e) {
                ctx.status(400).result("Ungültige Shop-ID.");
            }
        });


        System.out.println(
                "TowelTrack Server läuft unter: http://localhost:7000"
        );
    }

    public static class LieferungsErgebnis {

        public int wochenbedarf;
        public int restbestand;
        public double sicherheitsprozent;
        public int bedarfMitSicherheit;
        public int endgueltigeLieferung;

        public LieferungsErgebnis(
                int wochenbedarf,
                int restbestand,
                double sicherheitsprozent,
                int bedarfMitSicherheit,
                int endgueltigeLieferung) {

            this.wochenbedarf = wochenbedarf;
            this.restbestand = restbestand;
            this.sicherheitsprozent = sicherheitsprozent;
            this.bedarfMitSicherheit = bedarfMitSicherheit;
            this.endgueltigeLieferung = endgueltigeLieferung;
        }
    }
}

import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;

public class Server {
    public static void main(String[] args) {
        // Javalin-Anwendung starten und statische Dateien (HTML, CSS, JS) konfigurieren
        Javalin app = Javalin.create(config -> {
            config.staticFiles.add("public", Location.EXTERNAL);
        }).start(7000);

        PetShopDAO dao = new PetShopDAO();

        // Alle Pet Shops auflisten
        app.get("/api/petshops", ctx -> ctx.json(dao.listarTodos()));

        // Neuen Pet Shop hinzufügen (POST) mit Duplikat-Prüfung
        app.post("/api/petshops", ctx -> {
            PetShop neuerShop = ctx.bodyAsClass(PetShop.class);

            // Automatische Berechnung des Sicherheitsbestands (+15%), falls nicht angegeben
            if (neuerShop.getSicherheitsbestand() == 0) {
                int sicherheitsbestand = (int) Math.round(neuerShop.getWochenbedarfBasis() * 1.15);
                neuerShop.setSicherheitsbestand(sicherheitsbestand);
            }

            boolean sucesso = dao.insert(neuerShop);

            if (sucesso) {
                ctx.status(201).result("Pet Shop erfolgreich erstellt!");
            } else {
                ctx.status(400).result("Fehler: Ein Pet Shop mit diesem Namen existiert bereits.");
            }
        });

        // Pet Shop anhand der ID löschen (DELETE)
        app.delete("/api/petshops/{id}", ctx -> {
            int id = Integer.parseInt(ctx.pathParam("id"));
            dao.delete(id);
            ctx.result("Pet Shop erfolgreich gelöscht!");
        });

        // Liefermenge basierend auf dem Restbestand neu berechnen
        app.get("/api/petshops/lieferung-berechnen", ctx -> {
            int wochenbedarfBasis = Integer.parseInt(ctx.queryParam("basis"));
            int restbestand = Integer.parseInt(ctx.queryParam("restbestand"));

            // Formel: (Basisbedarf - Restbestand) + 15% Puffer
            int benoetigt = Math.max(0, wochenbedarfBasis - restbestand);
            int endgültigeLieferung = (int) Math.round(benoetigt * 1.15);

            ctx.json(new LieferungsErgebnis(wochenbedarfBasis, restbestand, endgültigeLieferung));
        });

        System.out.println("TowelTrack Server läuft unter: http://localhost:7000");
    }
}

// Hilfsklasse für das Berechnungsergebnis der Lieferung
class LieferungsErgebnis {
    public int basisbedarf;
    public int restbestand;
    public int endgültigeLieferung;

    public LieferungsErgebnis(int basisbedarf, int restbestand, int endgültigeLieferung) {
        this.basisbedarf = basisbedarf;
        this.restbestand = restbestand;
        this.endgültigeLieferung = endgültigeLieferung;
    }
}
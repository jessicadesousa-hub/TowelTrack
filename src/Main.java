import java.util.List;

public class Main {
    public static void main(String[] args) {
        //Ein PetShop-Objekt erstellen
        PetShop meuShop = new PetShop(
                "Happy Paws Pet-Shop",
                "Hauptstraße",
                "42a",
                "10115",
                "Berlin",
                "kontakt@happypaws.de",
                100);

        //Test Konsole Ausgabe:
        System.out.println("=== TEST TOWELTRACK ===");
        System.out.println("Kunde: " + meuShop.getName());
        System.out.println("Adresse: " + meuShop.getStrasse() + " " + meuShop.getHausnummer() + ", " + meuShop.getPlz() + " " + meuShop.getStadt());
        System.out.println("Wochenbedarf (Basis): " + meuShop.getWochenbedarfBasis() + " Stk.");
        System.out.println("Berechneter Sicherheitsbestand (+15%): " + meuShop.getSicherheitsbestand() + " Stk.");

        //Test DB Conection
        System.out.println("Testando a conexao com o banco de dados...");
        DatabaseConnection.conectar();


    //Ein PetShop-Objekt mit atomisierter Adresse erstellen
    PetShop myShop = new PetShop(
            "PfotenParadies",
            "Berliner Straße",
            "12",
            "10115",
            "Berlin",
            "kontakt@pfotenparadies.de",
            120);

    //Das DAO aufrufen, um die Daten in der Datenbank zu speichern
    PetShopDAO dao = new PetShopDAO();
        dao.insert(meuShop);

        //Ein PetShop-Objekt mit atomisierter Adresse erstellen und in der Datenbank speichern
        PetShop neuerShop = new PetShop(
                "PfotenParadies",
                "Berliner Straße",
                "12",
                "10115",
                "Berlin",
                "kontakt@pfotenparadies.de",
                120);

        PetShopDAO dao2 = new PetShopDAO();
        dao2.insert(neuerShop);

        //Alle Pet-Shops aus der Datenbank abrufen und auf der Konsole ausgeben
        System.out.println("\n--- Alle Pet-Shops aus der Datenbank ---");
        List<PetShop> shops = dao2.listarTodos();

        for (PetShop shop : shops) {
            System.out.println("Name: " + shop.getName() + " | Stadt: " + shop.getStadt() + " | Bedarf: " + shop.getWochenbedarfBasis() + " Stk.");
        }
    }

}
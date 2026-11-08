public class PetShop {

    //Attribute
    private int shopId;
    private String name;
    private String strasse;
    private String hausnummer;
    private String plz;
    private String stadt;
    private String kontaktEmail;
    private int wochenbedarfBasis;
    private int sicherheitsbestand;

 //Konstruktoren
 public PetShop() {
 }

    public PetShop(String name, String strasse, String hausnummer, String plz, String stadt, String kontaktEmail, int wochenbedarfBasis) {
        this.name = name;
        this.strasse = strasse;
        this.hausnummer = hausnummer;
        this.plz = plz;
        this.stadt = stadt;
        this.kontaktEmail = kontaktEmail;
        this.wochenbedarfBasis = wochenbedarfBasis;
        this.sicherheitsbestand = berechneSicherheitsbestand(wochenbedarfBasis);
    }

    //Methoden:

    public int berechneSicherheitsbestand(int basis) {
        return (int) Math.round(basis * 1.15);
    }

    // Getters
    public int getShopId() {
        return shopId;
    }
    public String getName() {
        return name;
    }
    public String getStrasse() {
        return strasse;
    }
    public String getHausnummer() {
        return hausnummer;
    }
    public String getPlz() {
        return plz;
    }
    public String getStadt() {
        return stadt;
    }
    public String getKontaktEmail() {

        return kontaktEmail;
    }
    public int getWochenbedarfBasis() {

        return wochenbedarfBasis;
    }
    public int getSicherheitsbestand() {

        return sicherheitsbestand;
    }

    //Setters:
    public void setWochenbedarfBasis(int wochenbedarfBasis) {
        this.wochenbedarfBasis = wochenbedarfBasis;
    }
    public void setSicherheitsbestand(int sicherheitsbestand) {
        this.sicherheitsbestand = sicherheitsbestand;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }
}
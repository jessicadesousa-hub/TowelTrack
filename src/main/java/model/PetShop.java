
package model;

public class PetShop {

    private int shopId;
    private String name;
    private String strasse;
    private String hausnummer;
    private String plz;
    private String stadt;
    private String kontaktEmail;
    private int wochenbedarf;
    private double sicherheitsprozent;
    private boolean aktiv;

    public PetShop() {
    }

    public PetShop(String name, String strasse, String hausnummer,
                   String plz, String stadt, String kontaktEmail,
                   int wochenbedarf) {

        this.name = name;
        this.strasse = strasse;
        this.hausnummer = hausnummer;
        this.plz = plz;
        this.stadt = stadt;
        this.kontaktEmail = kontaktEmail;
        this.wochenbedarf = wochenbedarf;
        this.sicherheitsprozent = 15.00;
        this.aktiv = true;
    }

    public int berechneSicherheitsbestand() {

        return (int) Math.round(
                wochenbedarf * (1 + sicherheitsprozent / 100)
        );
    }

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

    public int getWochenbedarf() {
        return wochenbedarf;
    }

    public double getSicherheitsprozent() {
        return sicherheitsprozent;
    }

    public boolean isAktiv() {
        return aktiv;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStrasse(String strasse) {
        this.strasse = strasse;
    }

    public void setHausnummer(String hausnummer) {
        this.hausnummer = hausnummer;
    }

    public void setPlz(String plz) {
        this.plz = plz;
    }

    public void setStadt(String stadt) {
        this.stadt = stadt;
    }

    public void setKontaktEmail(String kontaktEmail) {
        this.kontaktEmail = kontaktEmail;
    }

    public void setWochenbedarf(int wochenbedarf) {
        this.wochenbedarf = wochenbedarf;
    }

    public void setSicherheitsprozent(double sicherheitsprozent) {
        this.sicherheitsprozent = sicherheitsprozent;
    }

    public void setAktiv(boolean aktiv) {
        this.aktiv = aktiv;
    }
}

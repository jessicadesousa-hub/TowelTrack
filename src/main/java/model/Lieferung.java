
package model;

import java.time.LocalDate;

public class Lieferung {

    private int lieferungId;
    private int shopId;
    private Integer bestandId;
    private LocalDate lieferdatum;
    private int gelieferteAnzahl;
    private boolean sonderlieferung;

    public Lieferung() {
    }

    public Lieferung(int shopId,
                     Integer bestandId,
                     LocalDate lieferdatum,
                     int gelieferteAnzahl,
                     boolean sonderlieferung) {

        this.shopId = shopId;
        this.bestandId = bestandId;
        this.lieferdatum = lieferdatum;
        this.gelieferteAnzahl = gelieferteAnzahl;
        this.sonderlieferung = sonderlieferung;
    }

    public int getLieferungId() {
        return lieferungId;
    }

    public int getShopId() {
        return shopId;
    }

    public Integer getBestandId() {
        return bestandId;
    }

    public LocalDate getLieferdatum() {
        return lieferdatum;
    }

    public int getGelieferteAnzahl() {
        return gelieferteAnzahl;
    }

    public boolean isSonderlieferung() {
        return sonderlieferung;
    }

    public void setLieferungId(int id) {
        this.lieferungId = id;
    }

    public void setShopId(int id) {
        this.shopId = id;
    }

    public void setBestandId(Integer id) {
        this.bestandId = id;
    }

    public void setLieferdatum(LocalDate datum) {
        this.lieferdatum = datum;
    }

    public void setGelieferteAnzahl(int anzahl) {
        this.gelieferteAnzahl = anzahl;
    }

    public void setSonderlieferung(boolean sonderlieferung) {
        this.sonderlieferung = sonderlieferung;
    }
}

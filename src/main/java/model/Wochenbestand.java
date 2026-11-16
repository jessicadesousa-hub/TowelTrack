
package model;

import java.time.LocalDate;

public class Wochenbestand {

    private int bestandId;
    private int shopId;
    private LocalDate erfassungsdatum;
    private int restbestand;

    public Wochenbestand() {
    }

    public Wochenbestand(int shopId,
                         LocalDate erfassungsdatum,
                         int restbestand) {

        this.shopId = shopId;
        this.erfassungsdatum = erfassungsdatum;
        this.restbestand = restbestand;
    }

    public int getBestandId() {
        return bestandId;
    }

    public int getShopId() {
        return shopId;
    }

    public LocalDate getErfassungsdatum() {
        return erfassungsdatum;
    }

    public int getRestbestand() {
        return restbestand;
    }

    public void setBestandId(int bestandId) {
        this.bestandId = bestandId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

    public void setErfassungsdatum(LocalDate datum) {
        this.erfassungsdatum = datum;
    }

    public void setRestbestand(int restbestand) {
        this.restbestand = restbestand;
    }
}

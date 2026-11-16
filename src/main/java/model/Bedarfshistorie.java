
package model;

import java.time.LocalDateTime;

public class Bedarfshistorie {

    private int bedarfId;
    private int shopId;
    private Integer vorherigerWochenbedarf;
    private int neuerWochenbedarf;
    private LocalDateTime aenderungsdatum;

    public Bedarfshistorie() {
    }

    public Bedarfshistorie(int shopId,
                           Integer vorherigerWochenbedarf,
                           int neuerWochenbedarf) {

        this.shopId = shopId;
        this.vorherigerWochenbedarf = vorherigerWochenbedarf;
        this.neuerWochenbedarf = neuerWochenbedarf;
    }

    public int getBedarfId() {
        return bedarfId;
    }

    public int getShopId() {
        return shopId;
    }

    public Integer getVorherigerWochenbedarf() {
        return vorherigerWochenbedarf;
    }

    public int getNeuerWochenbedarf() {
        return neuerWochenbedarf;
    }

    public LocalDateTime getAenderungsdatum() {
        return aenderungsdatum;
    }

    public void setBedarfId(int bedarfId) {
        this.bedarfId = bedarfId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

    public void setVorherigerWochenbedarf(Integer wert) {
        this.vorherigerWochenbedarf = wert;
    }

    public void setNeuerWochenbedarf(int wert) {
        this.neuerWochenbedarf = wert;
    }

    public void setAenderungsdatum(LocalDateTime datum) {
        this.aenderungsdatum = datum;
    }
}

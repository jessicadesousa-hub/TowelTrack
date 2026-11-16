
CREATE DATABASE toweltrack_db;

USE toweltrack_db;


CREATE TABLE pet_shops (
    shop_id INT NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    strasse VARCHAR(100) NOT NULL,
    hausnummer VARCHAR(10) NOT NULL,
    plz VARCHAR(10) NOT NULL,
    stadt VARCHAR(50) NOT NULL,
    kontakt_email VARCHAR(100) NOT NULL,
    wochenbedarf INT NOT NULL,
    sicherheitsprozent DECIMAL(5,2) NOT NULL DEFAULT 15.00,
    aktiv BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (shop_id)
);


CREATE TABLE bedarfshistorie (
    bedarf_id INT NOT NULL AUTO_INCREMENT,
    shop_id INT NOT NULL,
    vorheriger_wochenbedarf INT DEFAULT NULL,
    neuer_wochenbedarf INT NOT NULL,
    aenderungsdatum DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (bedarf_id),
    FOREIGN KEY (shop_id) REFERENCES pet_shops(shop_id)
);



CREATE TABLE wochenbestand (
    bestand_id INT NOT NULL AUTO_INCREMENT,
    shop_id INT NOT NULL,
    erfassungsdatum DATE NOT NULL,
    restbestand INT NOT NULL,
    PRIMARY KEY (bestand_id),
    FOREIGN KEY (shop_id) REFERENCES pet_shops(shop_id)
);


CREATE TABLE lieferungen (
    lieferung_id INT NOT NULL AUTO_INCREMENT,
    shop_id INT NOT NULL,
    bestand_id INT DEFAULT NULL,
    lieferdatum DATE NOT NULL,
    gelieferte_anzahl INT NOT NULL,
    ist_sonderlieferung BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (lieferung_id),
    FOREIGN KEY (shop_id) REFERENCES pet_shops(shop_id),
    FOREIGN KEY (bestand_id) REFERENCES wochenbestand(bestand_id)
);

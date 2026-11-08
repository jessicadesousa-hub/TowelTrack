-- Erstellung der Tabelle für Pet-Shops (Kunden)
CREATE TABLE pet_shops (
    shop_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    strasse VARCHAR(100) NOT NULL,
    hausnummer VARCHAR(10) NOT NULL,
    plz VARCHAR(10) NOT NULL,
    stadt VARCHAR(50) NOT NULL,
    kontakt_email VARCHAR(100) NOT NULL,
    wochenbedarf_basis INT NOT NULL,
    sicherheitsbestand INT NOT NULL
);

-- Erstellung der Tabelle für Lieferungen
CREATE TABLE lieferungen (
    lieferung_id INT AUTO_INCREMENT PRIMARY KEY,
    shop_id INT NOT NULL,
    lieferdatum DATE NOT NULL,
    gelieferte_anzahl INT NOT NULL,
    ist_sonderlieferung BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (shop_id) REFERENCES pet_shops(shop_id) ON DELETE CASCADE
);
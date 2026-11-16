
document.addEventListener("DOMContentLoaded", () => {

    // Pet Shops beim Öffnen der Seite laden
    carregarPetShops();


    // Neuen Pet Shop hinzufügen
    document.getElementById('form-add')
        .addEventListener('submit', async (e) => {

            e.preventDefault();

            const wochenbedarf = Number(
                document.getElementById('wochenbedarfBasis').value
            );

            if (!Number.isInteger(wochenbedarf) ||
                wochenbedarf < 0) {

                alert('Bitte geben Sie einen gültigen Wochenbedarf ein.');
                return;
            }

            const neuerShop = {

                name: document.getElementById('name').value.trim(),
                stadt: document.getElementById('stadt').value.trim(),
                plz: document.getElementById('plz').value.trim(),
                strasse: document.getElementById('strasse').value.trim(),
                hausnummer: document.getElementById('hausnummer').value.trim(),

                kontaktEmail: document.getElementById('kontaktEmail').value.trim(),

                wochenbedarf: wochenbedarf,
                sicherheitsprozent: 15.00,
                aktiv: true
            };

            try {

                const res = await fetch('/api/petshops', {

                    method: 'POST',

                    headers: {
                        'Content-Type': 'application/json'
                    },

                    body: JSON.stringify(neuerShop)
                });

                if (!res.ok) {
                    throw new Error(await res.text());
                }

                alert('Pet Shop erfolgreich erstellt!');

                document.getElementById('form-add').reset();

                await carregarPetShops();

            } catch (err) {

                console.error(err);

                alert(err.message);
            }
        });


    // Pet Shop auswählen
    document.getElementById('select-shop')
        .addEventListener('change', (e) => {

            const option = e.target.selectedOptions[0];

            document.getElementById('calc-base').value =
                option.getAttribute('data-basis') || '';

            document.getElementById('calc-sobras').value = 0;

            document.getElementById('calc-resultado').style.display =
                'none';
        });


    // Liefermenge berechnen
    document.getElementById('btn-calcular')
        .addEventListener('click', berechnenLieferung);


    // Wochenbestand speichern
    document.getElementById('form-bestand')
        .addEventListener('submit', speichernWochenbestand);


    // Lieferung speichern
    document.getElementById('form-lieferung')
        .addEventListener('submit', speichernLieferung);


    // Historien anzeigen
    document.getElementById('btn-bestand-historie')
        .addEventListener('click', () =>
            ladenHistorie('wochenbestand', 'bestand-historie')
        );

    document.getElementById('btn-liefer-historie')
        .addEventListener('click', () =>
            ladenHistorie('lieferungen', 'liefer-historie')
        );

    document.getElementById('btn-bedarf-historie')
        .addEventListener('click', () =>
            ladenHistorie('bedarfshistorie', 'bedarf-historie')
        );

});


// Alle Pet Shops laden
async function carregarPetShops() {

    const tbody = document.getElementById('tabela-corpo');

    const selectShop = document.getElementById('select-shop');

    try {

        const res = await fetch('/api/petshops');

        if (!res.ok) {
            throw new Error('Fehler beim Abrufen der Pet Shops.');
        }

        const data = await res.json();

        if (!Array.isArray(data)) {
            throw new Error('Ungültige Daten vom Server.');
        }

        tbody.innerHTML = '';

        selectShop.innerHTML =
            '<option value="">-- Shop wählen --</option>';

        const aktiveShops = data.filter(shop => shop.aktiv);

        if (aktiveShops.length === 0) {

            tbody.innerHTML = `
                <tr>
                    <td colspan="7" class="loading">
                        Keine aktiven Pet Shops registriert.
                    </td>
                </tr>
            `;

            document.getElementById('calc-base').value = '';

            document.getElementById('calc-resultado').style.display =
                'none';

            return;
        }

        aktiveShops.forEach(shop => {

            const tr = document.createElement('tr');

            const werte = [
                shop.name,
                `${shop.stadt} (${shop.plz})`,
                `${shop.strasse} ${shop.hausnummer}`,
                shop.kontaktEmail,
                shop.wochenbedarf,
                `${shop.sicherheitsprozent}%`
            ];

            werte.forEach(wert => {

                const td = document.createElement('td');

                td.textContent = wert ?? '';

                tr.appendChild(td);
            });


            // Pet Shop deaktivieren
            const tdAktion = document.createElement('td');

            const button = document.createElement('button');

            button.className = 'btn-danger';

            button.textContent = 'Deaktivieren';

            button.addEventListener('click', () =>
                deaktivierenShop(shop.shopId)
            );

            tdAktion.appendChild(button);

            tr.appendChild(tdAktion);

            tbody.appendChild(tr);


            // Dropdown
            const option = document.createElement('option');

            option.value = shop.shopId;

            option.textContent =
                `${shop.name} (${shop.stadt})`;

            option.setAttribute(
                'data-basis',
                shop.wochenbedarf
            );

            option.setAttribute(
                'data-sicherheit',
                shop.sicherheitsprozent ?? 15
            );

            selectShop.appendChild(option);
        });

    } catch (err) {

        console.error(err);

        tbody.innerHTML = `
            <tr>
                <td colspan="7" class="loading error-message">
                    Fehler beim Laden der Daten.
                </td>
            </tr>
        `;

        alert('Die Pet Shops konnten nicht geladen werden.');
    }
}


// Liefermenge berechnen
async function berechnenLieferung() {

    const selectShop = document.getElementById('select-shop');

    const option = selectShop.selectedOptions[0];

    if (!selectShop.value || !option) {

        alert('Bitte wählen Sie zuerst einen Pet Shop aus.');

        return;
    }

    const base = document.getElementById('calc-base').value;

    const restbestand =
        document.getElementById('calc-sobras').value;

    const sicherheitsprozent =
        option.getAttribute('data-sicherheit') || 15;

    if (base === '' || restbestand === '') {

        alert('Bitte geben Sie alle Werte ein.');

        return;
    }

    const wochenbedarf = Number(base);

    const rest = Number(restbestand);

    const prozent = Number(sicherheitsprozent);

    if (
        !Number.isInteger(wochenbedarf) ||
        !Number.isInteger(rest) ||
        !Number.isFinite(prozent) ||
        wochenbedarf < 0 ||
        rest < 0 ||
        prozent < 0
    ) {

        alert('Bitte geben Sie gültige Werte ein.');

        return;
    }

    try {

        const url =
            `/api/petshops/lieferung-berechnen?basis=${encodeURIComponent(wochenbedarf)}` +
            `&restbestand=${encodeURIComponent(rest)}` +
            `&sicherheitsprozent=${encodeURIComponent(prozent)}`;

        const res = await fetch(url);

        if (!res.ok) {
            throw new Error(await res.text());
        }

        const data = await res.json();

        const result = document.getElementById('calc-resultado');

        result.style.display = 'block';

        result.textContent =
            `Empfohlene Liefermenge: ${data.endgueltigeLieferung} Handtücher ` +
            `(Wochenbedarf: ${data.wochenbedarf}, ` +
            `Sicherheitsprozent: ${data.sicherheitsprozent}%, ` +
            `Bedarf inklusive Sicherheit: ${data.bedarfMitSicherheit}, ` +
            `Restbestand: ${data.restbestand})`;

        // Vorgeschlagene Liefermenge übernehmen
        document.getElementById('liefer-anzahl').value =
            data.endgueltigeLieferung;

    } catch (err) {

        console.error(err);

        alert(err.message || 'Fehler bei der Berechnung.');
    }
}


// Wochenbestand speichern
async function speichernWochenbestand(e) {

    e.preventDefault();

    const shopId = document.getElementById('select-shop').value;

    if (!shopId) {

        alert('Bitte wählen Sie zuerst einen Pet Shop aus.');

        return;
    }

    const datum = document.getElementById('bestand-datum').value;

    const restbestand = Number(
        document.getElementById('bestand-rest').value
    );

    if (!datum || !Number.isInteger(restbestand) ||
        restbestand < 0) {

        alert('Bitte geben Sie gültige Bestandsdaten ein.');

        return;
    }

    const bestand = {

        erfassungsdatum: datum,
        restbestand: restbestand
    };

    try {

        const res = await fetch(
            `/api/petshops/${encodeURIComponent(shopId)}/wochenbestand`,
            {
                method: 'POST',

                headers: {
                    'Content-Type': 'application/json'
                },

                body: JSON.stringify(bestand)
            }
        );

        if (!res.ok) {
            throw new Error(await res.text());
        }

        alert('Wochenbestand erfolgreich gespeichert!');

        document.getElementById('form-bestand').reset();

        // KORRIGIERTER FUNKTIONSNAME
        await ladenHistorie(
            'wochenbestand',
            'bestand-historie'
        );

    } catch (err) {

        console.error(err);

        alert(err.message);
    }
}


// Lieferung speichern
async function speichernLieferung(e) {

    e.preventDefault();

    const shopId = document.getElementById('select-shop').value;

    if (!shopId) {

        alert('Bitte wählen Sie zuerst einen Pet Shop aus.');

        return;
    }

    const datum = document.getElementById('liefer-datum').value;

    const anzahl = Number(
        document.getElementById('liefer-anzahl').value
    );

    const sonderlieferung =
        document.getElementById('sonderlieferung').checked;

    if (!datum || !Number.isInteger(anzahl) || anzahl < 0) {

        alert('Bitte geben Sie gültige Lieferdaten ein.');

        return;
    }

    const lieferung = {

        lieferdatum: datum,
        gelieferteAnzahl: anzahl,
        sonderlieferung: sonderlieferung
    };

    try {

        const res = await fetch(
            `/api/petshops/${encodeURIComponent(shopId)}/lieferungen`,
            {
                method: 'POST',

                headers: {
                    'Content-Type': 'application/json'
                },

                body: JSON.stringify(lieferung)
            }
        );

        if (!res.ok) {
            throw new Error(await res.text());
        }

        alert('Lieferung erfolgreich gespeichert!');

        document.getElementById('form-lieferung').reset();

        // KORRIGIERTER FUNKTIONSNAME
        await ladenHistorie(
            'lieferungen',
            'liefer-historie'
        );

    } catch (err) {

        console.error(err);

        alert(err.message);
    }
}


// Historie laden
async function ladenHistorie(endpoint, containerId) {

    const shopId = document.getElementById('select-shop').value;

    if (!shopId) {

        alert('Bitte wählen Sie zuerst einen Pet Shop aus.');

        return;
    }

    const container = document.getElementById(containerId);

    try {

        const res = await fetch(
            `/api/petshops/${encodeURIComponent(shopId)}/${endpoint}`
        );

        if (!res.ok) {
            throw new Error('Fehler beim Laden der Historie.');
        }

        const data = await res.json();

        container.innerHTML = '';

        if (data.length === 0) {

            container.textContent = 'Keine Einträge vorhanden.';

            return;
        }

        const table = document.createElement('table');

        const thead = document.createElement('thead');

        const tbody = document.createElement('tbody');

        let headers = [];

        let fields = [];

        if (endpoint === 'wochenbestand') {

            headers = ['Datum', 'Restbestand'];

            fields = ['erfassungsdatum', 'restbestand'];

        } else if (endpoint === 'lieferungen') {

            headers = [
                'Lieferdatum',
                'Gelieferte Anzahl',
                'Sonderlieferung'
            ];

            fields = [
                'lieferdatum',
                'gelieferteAnzahl',
                'sonderlieferung'
            ];

        } else {

            headers = [
                'Vorheriger Bedarf',
                'Neuer Bedarf',
                'Änderungsdatum'
            ];

            fields = [
                'vorherigerWochenbedarf',
                'neuerWochenbedarf',
                'aenderungsdatum'
            ];
        }

        const headerRow = document.createElement('tr');

        headers.forEach(header => {

            const th = document.createElement('th');

            th.textContent = header;

            headerRow.appendChild(th);
        });

        thead.appendChild(headerRow);

        data.forEach(eintrag => {

            const tr = document.createElement('tr');

            fields.forEach(field => {

                const td = document.createElement('td');

                td.textContent = eintrag[field] ?? '-';

                tr.appendChild(td);
            });

            tbody.appendChild(tr);
        });

        table.appendChild(thead);

        table.appendChild(tbody);

        container.appendChild(table);

    } catch (err) {

        console.error(err);

        container.textContent = err.message;
    }
}


// Pet Shop deaktivieren
async function deaktivierenShop(id) {

    if (!confirm('Möchten Sie diesen Pet Shop wirklich deaktivieren?')) {
        return;
    }

    try {

        const res = await fetch(
            `/api/petshops/${encodeURIComponent(id)}/deaktivieren`,
            {
                method: 'PATCH'
            }
        );

        if (!res.ok) {
            throw new Error(await res.text());
        }

        alert('Pet Shop erfolgreich deaktiviert!');

        await carregarPetShops();

    } catch (err) {

        console.error(err);

        alert(err.message);
    }
}

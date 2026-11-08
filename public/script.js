document.addEventListener("DOMContentLoaded", () => {
    carregarPetShops();

    // Submeter novo Pet Shop (POST)
    document.getElementById('form-add').addEventListener('submit', (e) => {
        e.preventDefault();

        const neuerShop = {
            name: document.getElementById('name').value,
            stadt: document.getElementById('stadt').value,
            plz: document.getElementById('plz').value,
            strasse: document.getElementById('strasse').value,
            hausnummer: document.getElementById('hausnummer').value,
            kontaktEmail: document.getElementById('kontaktEmail').value,
            wochenbedarfBasis: parseInt(document.getElementById('wochenbedarfBasis').value),
            sicherheitsbestand: 0 // O backend calcula automaticamente com +15%
        };

        fetch('/api/petshops', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(neuerShop)
                })
                .then(async res => {
                    if (res.ok) {
                        document.getElementById('form-add').reset();
                        carregarPetShops();
                    } else {
                        // Lê a mensagem de erro que veio do Java (ex: duplicado)
                        const erroTexto = await res.text();
                        alert(erroTexto || 'Fehler beim Speichern des Pet Shops.');
                    }
                })
                .catch(err => {
                    console.error('Netzwerkfehler:', err);
                    alert('Verbindung zum Server fehlgeschlagen.');
                });
            });

    // Selecionar um shop no dropdown para preencher o valor base automaticamente
    document.getElementById('select-shop').addEventListener('change', (e) => {
        const selectedOption = e.target.selectedOptions[0];
        const basis = selectedOption.getAttribute('data-basis');
        document.getElementById('calc-base').value = basis ? basis : '';
    });

    // Botão para calcular a entrega semanal com base nas sobras
    document.getElementById('btn-calcular').addEventListener('click', () => {
        const base = document.getElementById('calc-base').value;
        const restbestand = document.getElementById('calc-sobras').value;

        if (!base) {
            alert('Bitte wählen Sie einen Pet Shop aus oder geben Sie den Basisbedarf ein.');
            return;
        }

        fetch(`/api/petshops/lieferung-berechnen?basis=${base}&restbestand=${restbestand}`)
            .then(res => res.json())
            .then(data => {
                const resDiv = document.getElementById('calc-resultado');
                resDiv.style.display = 'block';
                resDiv.innerHTML = `Empfohlene Liefermenge für diese Woche (inkl. 15% Puffer): <strong>${data.endgültigeLieferung} Handtücher</strong> (Basis: ${data.basisbedarf}, Restbestand: ${data.restbestand})`;
            })
            .catch(err => {
                console.error(err);
                alert('Fehler bei der Berechnung.');
            });
    });
});

function carregarPetShops() {
    fetch('/api/petshops')
        .then(res => res.json())
        .then(data => {
            const tbody = document.getElementById('tabela-corpo');
            const selectShop = document.getElementById('select-shop');

            tbody.innerHTML = '';
            // Limpa o dropdown mantendo a primeira opção padrão
            selectShop.innerHTML = '<option value="">-- Shop wählen --</option>';

            if (data.length === 0) {
                tbody.innerHTML = '<tr><td colspan="7" class="loading">Keine Pet Shops registriert.</td></tr>';
                return;
            }

            data.forEach(shop => {
                // Preencher tabela
                const tr = document.createElement('tr');
                tr.innerHTML = `
                    <td><strong>${shop.name}</strong></td>
                    <td>${shop.stadt} (${shop.plz})</td>
                    <td>${shop.strasse} ${shop.hausnummer}</td>
                    <td>${shop.kontaktEmail}</td>
                    <td>${shop.wochenbedarfBasis}</td>
                    <td>${shop.sicherheitsbestand}</td>
                    <td><button class="btn-danger" onclick="removerShop(${shop.shopId})">Löschen</button></td>
                `;
                tbody.appendChild(tr);

                // Preencher o seletor (dropdown) da calculadora de entregas
                const option = document.createElement('option');
                option.value = shop.shopId;
                option.textContent = `${shop.name} (${shop.stadt})`;
                option.setAttribute('data-basis', shop.wochenbedarfBasis);
                selectShop.appendChild(option);
            });
        })
        .catch(err => {
            console.error(err);
            document.getElementById('tabela-corpo').innerHTML = '<tr><td colspan="7" class="loading" style="color: #e74c3c;">Fehler beim Laden der Daten.</td></tr>';
        });
}

function removerShop(id) {
    if (confirm('Möchten Sie diesen Pet Shop wirklich löschen?')) {
        fetch(`/api/petshops/${id}`, { method: 'DELETE' })
            .then(res => {
                if (res.ok) {
                    carregarPetShops();
                } else {
                    alert('Fehler beim Löschen.');
                }
            });
    }
}
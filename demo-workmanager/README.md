# WorkManager demo aplikacija

Ta mapa vsebuje preprosto demo Android aplikacijo, katere namen je prikaz
osnovne uporabe knjižnice **Android WorkManager** za izvajanje opravil v
ozadju.

## Opis delovanja

Ob kliku na gumb v aplikaciji se ustvari in razporedi `OneTimeWorkRequest`,
ki se izvede po kratkem zamiku. Opravilo se izvaja v ozadju z uporabo
razreda `CoroutineWorker`.

Ko se opravilo zaključi, aplikacija prikaže sistemsko obvestilo, s čimer je
razvidno, da je bilo opravilo uspešno izvedeno tudi brez neposredne
interakcije uporabnika z aplikacijo.

## Namen demo aplikacije

Namen te demo aplikacije je izključno predstavitev delovanja WorkManagerja
in njegove uporabe za zanesljivo izvajanje opravil v ozadju. Aplikacija ne
predstavlja polne produkcijske rešitve, temveč služi kot didaktičen primer
za razumevanje osnovnih konceptov.

Za primer realne uporabe WorkManagerja v produkcijskem okolju je v tem
repozitoriju dodan tudi primer iz aplikacije **AirPin**.

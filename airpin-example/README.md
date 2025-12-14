# AirPin – primer uporabe WorkManagerja

Ta mapa vsebuje poenostavljen primer uporabe Android WorkManagerja iz mojega
lastnega projekta ***AirPin***, razvitega v okviru predmeta PORA.

Aplikacija omogoča dodajanje in upravljanje osebnih lokacij (“pinov”), 
za katere uporabnik želi spremljati vremenske razmere. 
Vsaka dodana lokacija je prikazana na interaktivnem zemljevidu, 
AirPin pa v ozadju samodejno preverja spremembe vremena in uporabniku 
pošilja push obvestila ob pomembnih vremenskih dogodkih.

V aplikaciji AirPin se WorkManager uporablja za periodično pridobivanje
vremenskih podatkov iz OpenWeather API-ja in za prikaz obvestil, povezanih z
uporabniško izbranimi lokacijami.

Datoteka `WeatherWorker.kt` prikazuje implementacijo opravila v ozadju z uporabo
razreda `CoroutineWorker`, ki vključuje dostop do omrežja in pošiljanje
obvestil uporabniku.

## Razporejanje periodičnega opravila

V aplikaciji AirPin je opravilo razporejeno kot enolično periodično opravilo z
najmanjšim dovoljenim intervalom 15 minut, skladno z omejitvami sistema Android:

```kotlin
private fun scheduleWeatherWorker() {
    val request = PeriodicWorkRequestBuilder<WeatherWorker>(
        15, TimeUnit.MINUTES
    ).build()

    WorkManager.getInstance(requireContext())
        .enqueueUniquePeriodicWork(
            "WeatherCheckWork",
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
}

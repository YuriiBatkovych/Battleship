# Battleship
Battleship game 

### Parametry uruchomieniowe
Aplikacja obługuje następujące parametry:
* `-mode [server|client]` - wskazuje tryb działania (jako serwer: przyjmuje połączenie, jako klient: nawiązuje połączenie z serwerem)
* `-port N` - port, na którym aplikacja ma się komunikować.
* `-address X` - adres serwera (tylko w trybie client)
* `-map map-file` - ścieżka do pliku zawierającego mapę z rozmieszczeniem statków (format opisany w sekcji Mapa).

### Protokół komunikacji
* Komunikacja odbywa się z użyciem protokołu TCP, z kodowaniem UTF-8.

* Komendy i ich znaczenie:
  * _start_
    * komenda inicjująca rozgrywkę. 
    * Wysyła ją klient tylko raz, na początku.
    * Przykład: `start;A1\n`
  * _pudło_
    * odpowiedź wysyłana, gdy pod współrzędnymi otrzymanymi od drugiej strony nie znajduje się żaden okręt.
    * Przykład: `pudło;A1\n`
  * _trafiony_
    * odpowiedź wysyłana, gdy pod współrzędnymi otrzymanymi od drugiej strony znajduje się okręt, i nie jest to jego ostatni dotychczas nie trafiony segment.
    * Przykład: `trafiony;A1\n`
  * _trafiony zatopiony_
    * odpowiedź wysyłana, gdy pod współrzędnymi otrzymanymi od drugiej strony znajduje się okręt, i trafiono ostatni jeszcze nie trafiony segment tego okrętu.
    * Przykład: `trafiony zatopiony;A1\n`
  * _ostatni zatopiony_
    * odpowiedź wysyłana, gdy pod współrzędnymi otrzymanymi od drugiej strony znajduje się okręt, i trafiono ostatni jeszcze nie trafiony segment okrętu całej floty w tej grze.
    * Jest to ostatnia komenda w grze. Strona wysyłająca ją przegrywa.
    * Przy tej komendzie nie podaje się współrzędnych strzału (już nie ma kto strzelać!). 
    * Przykład: `ostatni zatopiony\n`
* Możliwe (choć strategicznie nierozsądne) jest wielokrotne strzelanie w to samo miejsce. Należy wtedy odpowiadać zgodnie z aktualnym stanem planszy:
  * `pudło` w razie pudła,
  * `trafiony` gdy okręt już był trafiony w to miejsce, ale nie jest jeszcze zatopiony,
  * `trafiony zatopiony` gdy okręt jest już zatopiony.
* Obsługa błędów:
  * W razie otrzymania niezrozumiałej komendy lub po 1 sekundzie oczekiwania należy ponownie wysłać swoją ostatnią wiadomość. 
  * Po 3 nieudanej próbie należy wyświelić komunikat `Błąd komunikacji` i zakończyć działanie aplikacji.

Przykład mapy przeciwnika z przegranej sesji:
```
..#..??.?.
#.????.#..
#....??...
..##....?.
?.....##..
??#??.....
..?......#
..##...#..
.##....#.#
.......#..
```

Przykład swojej mapy po grze (wygranej; nie wszytkie okręty zatopione):
```
~~@~~.~~~.
@..~.~.@.~
#.~#..~.~.
..##..~..~
..~.~.@@..
.#@~..~...
.~.~.~.~.@
~.##.~.#~~
.##~..~~~~
..~.~.~~~.

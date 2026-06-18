# ESP32 & Android Airsoft Domination System

Nowoczesny system elektronicznego punktu kontrolnego stworzony do rozgrywek **Airsoft (ASG)** oraz **Paintballa**. Projekt składa się z urządzenia bazowego (mikrokontroler ESP32 z fizycznymi przyciskami i sygnalizacją LED) oraz dedykowanej aplikacji mobilnej (Android), która pozwala na pełny monitoring i zarządzanie rozgrywką w czasie rzeczywistym.

---

## Jak to działa?

1. **Punkt Dominacji (ESP32)** umieszczany jest na polu gry.
2. Zawodnicy z dwóch drużyn (**Niebieskich** i **Czerwonych**) próbują przejąć bazę poprzez wciśnięcie swojego fizycznego przycisku.
3. Gdy punkt zostanie przejęty, mikrokontroler zaczyna odliczać czas dominacji dla danego zespołu, zapala odpowiednie diody LED i aktualizuje czas na wyświetlaczu TM1637.
4. **Sędzia / Organizator** za pomocą aplikacji mobilnej na telefonie podłączonym do sieci Wi-Fi może zdalnie konfigurować parametry gry, rozpoczynać i zatrzymywać mecz, śledzić czas dominacji w czasie rzeczywistym, a także symulować przejęcia.

---

## Główne Cechy Systemu

* **Fizyczne sterowanie i sygnalizacja (ESP32):**
  * Obsługa 2 fizycznych przycisków z programowym filtrowaniem drgań styków (*debounce*).
  * Wizualizacja stanu przy użyciu 10 jasnych diod LED (po 5 na drużynę).
  * Wyświetlacz TM1637 pokazujący pozostały czas rozgrywki.
  * Efekty świetlne: mruganie przy odliczaniu ostatnich 10 sekund oraz sygnalizacja zwycięstwa po zakończeniu gry.
* **Zdalne zarządzanie (Aplikacja Android):**
  * Zbudowana z użyciem najnowszych technologii zalecanych przez Google (**Jetpack Compose**).
  * Dynamiczny podgląd czasu w czasie rzeczywistym (automatyczne odświeżanie co 1 sekundę).
  * Wizualny pasek postępu pokazujący stosunek dominacji drużyn.
  * Zapisywanie historii ostatnich 10 rozegranych meczów na liście typu `LazyColumn`.
* **Architektura REST API:**
  * Mikrokontroler działa jako serwer HTTP udostępniający dane w formacie JSON (wykorzystano bibliotekę `ArduinoJson` oraz nagłówki CORS).
* **Środowisko testowe:**
  * Pełna integracja z symulatorem elektroniki **Wokwi** w VS Code oraz **emulatorem Android Studio** za pomocą dedykowanego serwera proxy w Pythonie.

---

## Struktura Projektu

Projekt podzielony jest na dwa główne katalogi robocze:
* **`VisualStudio/`** — Oprogramowanie wbudowane dla ESP32 (konfigurowane i budowane przez PlatformIO).
* **`AndroidStudio/`** — Kod źródłowy aplikacji mobilnej w języku Kotlin.
* **`relay_server.py`** — Skrypt pomocniczy w Pythonie, realizujący tunel sieciowy (TCP Proxy) pomiędzy emulatorem Androida a symulatorem Wokwi.

---

## Stos Technologiczny

* **Hardware / Firmware:** C++, ESP32 (ESP-IDF/Arduino Framework), PlatformIO, ArduinoJson, TM1637Display.
* **Software mobilny:** Kotlin, Jetpack Compose, Retrofit, KotlinX Serialization, Kotlin Coroutines, Jetpack Navigation, Android Architecture Components (MVVM).
* **Pomost sieciowy:** Python (Sockets, Threading).

---

## Szybki Start (Uruchomienie w symulatorze)

Aby przetestować pełen system na jednym komputerze bez fizycznej płytki ESP32:

1. **Uruchom symulator Wokwi:**
   * Otwórz katalog `VisualStudio/` w programie VS Code.
   * Uruchom symulator (F1 -> `Wokwi: Start Simulator`). Serwer wystartuje lokalnie na porcie `127.0.0.1:8180`.

2. **Włącz tunel pośredniczący (Relay Server):**
   * Uruchom skrypt w konsoli:
     ```bash
     python relay_server.py
     ```
   * Serwer proxy zacznie nasłuchiwać na porcie `8181` na adresie `0.0.0.0`.

3. **Uruchom aplikację na emulatorze Android:**
   * Otwórz katalog `AndroidStudio/` w Android Studio.
   * Skompiluj i włącz aplikację na dowolnym emulatorze.
   * Na ekranie startowym wpisz adres IP hosta wskazujący na tunel proxy: **`10.0.2.2:8181`**.
   * Kliknij **Sprawdź łączność** — po pomyślnym połączeniu ustaw czas gry i naciśnij **Rozpocznij grę**!

>  **Wersja fizyczna:** W przypadku wdrożenia na rzeczywisty sprzęt, ESP32 tworzy punkt dostępowy Wi-Fi (lub łączy się z istniejącym routerem). Wtedy aplikacja na telefonie łączy się bezpośrednio z rzeczywistym adresem IP mikrokontrolera (np. `192.168.4.1`), a uruchamianie skryptu `relay_server.py` nie jest wymagane.

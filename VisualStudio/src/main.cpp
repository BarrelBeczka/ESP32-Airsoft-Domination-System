#include <WiFi.h>
#include <WebServer.h>
#include "game_logic.h"
#include "led_control.h"
#include "display_control.h"
#include "api_handlers.h"

// Odpowiada za wyjścia i wejsćia i główną pętlę
// Konfiguracja WiFi (Station / Wokwi)
const char* WIFI_SSID     = "Wokwi-GUEST";
const char* WIFI_PASSWORD = "";

// Konfiguracja przycisków
#define BTN_BLUE_PIN   26    // GPIO26 - przycisk niebieski
#define BTN_RED_PIN    27    // GPIO27 - przycisk czerwony

// Debounce przycisków
unsigned long lastBtnBlue = 0;
unsigned long lastBtnRed  = 0;
const unsigned long DEBOUNCE_MS = 300;  // 300ms debounce

// Serwer HTTP na porcie 80
WebServer server(80);

// Prototypy funkcji
void handleButtons();
void updateLeds();

// Timer aktualizacji gry
unsigned long lastGameUpdate = 0;
const unsigned long GAME_UPDATE_INTERVAL = 500;  // Co 500ms


// SETUP - Jednorazowa konfiguracja
void setup() {
    // Port szeregowy (do debugowania)
    Serial.begin(115200);
    Serial.println();
    Serial.println(" PUNKT DOMINACJI AIRSOFT v1.0");

    // Konfiguracja przycisków (INPUT_PULLUP = domyślnie HIGH)
    pinMode(BTN_BLUE_PIN, INPUT_PULLUP);
    pinMode(BTN_RED_PIN,  INPUT_PULLUP);
    Serial.println("[HW] Przyciski skonfigurowane");

    // Inicjalizacja diod LED i wyświetlacza
    initLeds();
    setLedsOff();
    initDisplay();

    // Uruchomienie WiFi w trybie Station
    WiFi.mode(WIFI_STA);
    WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
    Serial.print("[WiFi] Lączenie z Wokwi-GUEST");
    while (WiFi.status() != WL_CONNECTED) {
        delay(500);
        Serial.print(".");
    }
    Serial.println();
    Serial.println("[WiFi] Połączono!");
    Serial.println("[WiFi] IP: " + WiFi.localIP().toString());

    // Rejestracja endpointów API
    registerApiHandlers();

    // Uruchomienie serwera HTTP
    server.begin();
    Serial.println("[HTTP] Serwer uruchomiony na porcie 80");

    // Reset stanu gry
    resetGame();

    // Sygnalizacja gotowości - krótkie mignięcie białym
    setAllLeds(255, 255, 255);
    delay(500);
    setLedsOff();

    Serial.println(" GOTOWY DO GRY!");
}

// Główna pętla programu
void loop() {
    // Obsługa zapytań HTTP od aplikacji
    server.handleClient();

    // Odczyt przycisków fizycznych (z debounce)
    handleButtons();

    // Aktualizacja logiki gry
    unsigned long now = millis();
    if (now - lastGameUpdate >= GAME_UPDATE_INTERVAL) {
        lastGameUpdate = now;
        updateGame();
        updateLeds();
        
        // Aktualizacja wyświetlacza
        if (game.gameActive) {
            showTimeOnDisplay(getTimeLeft());
        } else if (game.finished) {
            showEnd();
        } else {
            clearDisplay();
        }
    }
}

// Zmienne do stanów przycisków
int lastBlueState = HIGH;
int lastRedState = HIGH;

void handleButtons() {
    int currentBlueState = digitalRead(BTN_BLUE_PIN);
    int currentRedState = digitalRead(BTN_RED_PIN);

    // Wykrycie zmiany ze zwolnionego (HIGH) na wciśnięty (LOW)
    if (currentBlueState == LOW && lastBlueState == HIGH) {
        delay(30); 
        if (digitalRead(BTN_BLUE_PIN) == LOW && game.gameActive) {
            pressButton(TEAM_BLUE);
            Serial.println("[BTN] Przycisk NIEBIESKI zarejestrowany!");
        }
    }
    lastBlueState = currentBlueState;

    // Wykrycie zmiany ze zwolnionego (HIGH) na wciśnięty (LOW)
    if (currentRedState == LOW && lastRedState == HIGH) {
        delay(30); 
        if (digitalRead(BTN_RED_PIN) == LOW && game.gameActive) {
            pressButton(TEAM_RED);
            Serial.println("[BTN] Przycisk CZERWONY zarejestrowany!");
        }
    }
    lastRedState = currentRedState;
}

// Aktualizacja stanu LED-ów
void updateLeds() {
    if (!game.gameActive && !game.finished) {
        // Gra nieaktywna - diody wyłączone
        setLedsOff();
        return;
    }

    if (game.finished) {
        // Gra zakończona - mruganie kolorem zwycięzcy
        TeamOwner winner = (game.blueTime > game.redTime) ? TEAM_BLUE : (game.redTime > game.blueTime) ? TEAM_RED : TEAM_NONE;
        blinkVictory(winner);
        return;
    }

    // Gra aktywna
    int timeLeft = getTimeLeft();

    if (timeLeft <= 10 && game.currentOwner != TEAM_NONE) {
        // Ostatnie 10 sekund - szybkie miganie
        animateCountdown(timeLeft);
    } else {
        // Normalny stan - kolor drużyny
        setTeamColor(game.currentOwner);
    }
}

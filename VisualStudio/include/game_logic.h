#ifndef GAME_LOGIC_H
#define GAME_LOGIC_H

// Zarzadzanie czasem liczenie sekund
#include <Arduino.h>

// Maksymalna liczba meczy w historii
#define MAX_HISTORY 10

// Możliwe stany właściciela punktu
enum TeamOwner {
    TEAM_NONE,
    TEAM_BLUE,
    TEAM_RED
};

// Wynik jednego meczu w historii
struct MatchResult {
    int id;
    int duration;      // czas trwania meczu w sekundach
    int blueTime;      // łączny czas kontroli niebieskich (sekundy)
    int redTime;       // łączny czas kontroli czerwonych (sekundy)
    String winner;     // "blue", "red" lub "draw"
};

// Aktualny stan gry
struct GameState {
    bool gameActive;           // czy gra trwa
    bool finished;             // czy gra się właśnie zakończyła
    int totalDuration;         // łączny czas gry ustawiony na starcie
    unsigned long startTime;   // millis() przy starcie gry
    int blueTime;              // sekundy kontroli niebieskich
    int redTime;               // sekundy kontroli czerwonych
    TeamOwner currentOwner;    // kto aktualnie trzyma punkt
    unsigned long lastCapture; // millis() ostatniego przejęcia punktu
};

// Globalne zmienne gry
GameState game;
MatchResult history[MAX_HISTORY];
int historyCount = 0;
int nextMatchId = 1;

// Funkcje gry

// Resetowanie stanu gry
void resetGame() {
    game.gameActive = false;
    game.finished = false;
    game.totalDuration = 0;
    game.startTime = 0;
    game.blueTime = 0;
    game.redTime = 0;
    game.currentOwner = TEAM_NONE;
    game.lastCapture = 0;
}

// Rozpoczęcie nowej gry
void startGame(int durationSeconds) {
    resetGame();
    game.gameActive = true;
    game.totalDuration = durationSeconds;
    game.startTime = millis();
    game.lastCapture = millis();
    Serial.println("[GAME] Gra rozpoczęta! Czas: " + String(durationSeconds) + "s");
}

// Obliczenie ile sekund zostało
int getTimeLeft() {
    if (!game.gameActive) return 0;
    unsigned long elapsed = (millis() - game.startTime) / 1000;
    int left = game.totalDuration - (int)elapsed;
    return (left > 0) ? left : 0;
}

// Aktualizacja czasu kontroli dla aktualnego właściciela
void updateControlTime() {
    if (!game.gameActive || game.currentOwner == TEAM_NONE) return;

    unsigned long now = millis();
    int delta = (int)((now - game.lastCapture) / 1000);

    if (delta > 0) {
        if (game.currentOwner == TEAM_BLUE) {
            game.blueTime += delta;
        } else if (game.currentOwner == TEAM_RED) {
            game.redTime += delta;
        }
        game.lastCapture = now;
    }
}

// Wciśnięcie przycisku przez drużynę
TeamOwner pressButton(TeamOwner team) {
    if (!game.gameActive) return game.currentOwner;

    // Aktualizuj czas poprzedniego właściciela
    updateControlTime();

    // Zmień właściciela
    game.currentOwner = team;
    game.lastCapture = millis();

    String teamName = (team == TEAM_BLUE) ? "BLUE" : "RED";
    Serial.println("[GAME] Punkt przejęty przez: " + teamName);

    return game.currentOwner;
}

// Zapisanie wyniku do historii
void saveToHistory() {
    MatchResult result;
    result.id = nextMatchId++;
    result.duration = game.totalDuration;
    result.blueTime = game.blueTime;
    result.redTime = game.redTime;

    if (game.blueTime > game.redTime) {
        result.winner = "blue";
    } else if (game.redTime > game.blueTime) {
        result.winner = "red";
    } else {
        result.winner = "draw";
    }

    // Przesunięcie historii jeśli pełna
    if (historyCount >= MAX_HISTORY) {
        for (int i = 0; i < MAX_HISTORY - 1; i++) {
            history[i] = history[i + 1];
        }
        historyCount = MAX_HISTORY - 1;
    }

    history[historyCount] = result;
    historyCount++;

    Serial.println("[GAME] Mecz #" + String(result.id) + " zapisany. Zwycięzca: " + result.winner);
}

// Zakończenie gry
void stopGame() {
    if (!game.gameActive) return;
    updateControlTime();

    game.gameActive = false;
    game.finished = true;

    // Zapisz do historii
    saveToHistory();

    String winner = (game.blueTime > game.redTime) ? "BLUE" : (game.redTime > game.blueTime) ? "RED" : "DRAW";
    Serial.println("[GAME] Gra zakończona! Blue: " + String(game.blueTime) + "s, Red: " + String(game.redTime) + "s, Zwycięzca: " + winner);
}

// Główna aktualizacja gry
void updateGame() {
    if (!game.gameActive) return;

    // Sprawdź czy czas się skończył
    if (getTimeLeft() <= 0) {
        stopGame();
    }
}

// Konwersja TeamOwner na String
String teamToString(TeamOwner team) {
    switch (team) {
        case TEAM_BLUE: return "blue";
        case TEAM_RED:  return "red";
        default:        return "none";
    }
}

#endif

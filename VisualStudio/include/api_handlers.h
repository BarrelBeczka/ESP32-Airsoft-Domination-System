/*
 * ========================================
 * API HANDLERS - Obsługa zapytań HTTP
 * ========================================
 * Handlery REST API dla komunikacji
 * z aplikacją mobilną przez Retrofit.
 * Formaty odpowiedzi: JSON (ArduinoJson).
 * ========================================
 */

#ifndef API_HANDLERS_H
#define API_HANDLERS_H

#include <WebServer.h>
#include <ArduinoJson.h>
#include "game_logic.h"
#include "led_control.h"

// Referencja do serwera (zdefiniowanego w main.cpp)
extern WebServer server;

// ---- Nagłówki CORS (żeby przeglądarka mogła odpytywać API) ----
void setCorsHeaders() {
    server.sendHeader("Access-Control-Allow-Origin", "*");
    server.sendHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
    server.sendHeader("Access-Control-Allow-Headers", "Content-Type");
}

// ---- Handler: GET /status ----
// Ogólny status ESP (do sprawdzenia łączności)
void handleStatus() {
    setCorsHeaders();

    JsonDocument doc;
    doc["device"] = "AirsoftDominationPoint";
    doc["version"] = "1.0";
    doc["uptime"] = millis() / 1000;
    doc["gameActive"] = game.gameActive;
    doc["ip"] = WiFi.localIP().toString();

    String response;
    serializeJson(doc, response);
    server.send(200, "application/json", response);
}

// ---- Handler: GET /game/status ----
// Aktualny stan gry (odpytywany co sekundę przez apkę)
void handleGameStatus() {
    setCorsHeaders();

    // Aktualizuj czas kontroli przed wysłaniem
    updateControlTime();

    JsonDocument doc;
    doc["gameActive"] = game.gameActive;
    doc["finished"] = game.finished;
    doc["timeLeft"] = getTimeLeft();
    doc["totalDuration"] = game.totalDuration;
    doc["blueTime"] = game.blueTime;
    doc["redTime"] = game.redTime;
    doc["currentOwner"] = teamToString(game.currentOwner);

    // Jeśli gra się skończyła, dodaj info o zwycięzcy
    if (game.finished) {
        if (game.blueTime > game.redTime) {
            doc["winner"] = "blue";
        } else if (game.redTime > game.blueTime) {
            doc["winner"] = "red";
        } else {
            doc["winner"] = "draw";
        }
    }

    String response;
    serializeJson(doc, response);
    server.send(200, "application/json", response);
}

// ---- Handler: POST /game/start ----
// Rozpoczęcie nowej gry (parametr: duration w sekundach)
void handleGameStart() {
    setCorsHeaders();

    int duration = 600;  // Domyślnie 10 minut

    if (server.hasArg("duration")) {
        duration = server.arg("duration").toInt();
        if (duration <= 0 || duration > 3600) {
            server.send(400, "application/json",
                "{\"error\":\"Czas musi być między 1 a 3600 sekund\"}");
            return;
        }
    }

    // Rozpocznij grę
    startGame(duration);

    // Diody na neutralny kolor
    setLedsOff();

    // Odpowiedź
    JsonDocument doc;
    doc["status"] = "started";
    doc["duration"] = duration;
    doc["message"] = "Gra rozpoczęta! Czas: " + String(duration) + "s";

    String response;
    serializeJson(doc, response);
    server.send(200, "application/json", response);
}

// ---- Handler: POST /game/stop ----
// Ręczne zatrzymanie gry
void handleGameStop() {
    setCorsHeaders();

    if (!game.gameActive) {
        server.send(400, "application/json",
            "{\"error\":\"Żadna gra nie jest aktywna\"}");
        return;
    }

    stopGame();
    setLedsOff();

    JsonDocument doc;
    doc["status"] = "stopped";
    doc["blueTime"] = game.blueTime;
    doc["redTime"] = game.redTime;

    if (game.blueTime > game.redTime) {
        doc["winner"] = "blue";
    } else if (game.redTime > game.blueTime) {
        doc["winner"] = "red";
    } else {
        doc["winner"] = "draw";
    }

    String response;
    serializeJson(doc, response);
    server.send(200, "application/json", response);
}

// ---- Handler: POST /game/press ----
// Symulacja wciśnięcia przycisku z aplikacji
// (parametr: team = "blue" lub "red")
void handleGamePress() {
    setCorsHeaders();

    if (!game.gameActive) {
        server.send(400, "application/json",
            "{\"error\":\"Żadna gra nie jest aktywna. Najpierw rozpocznij grę.\"}");
        return;
    }

    String team = server.arg("team");

    if (team == "blue") {
        pressButton(TEAM_BLUE);
        animateCapture(TEAM_BLUE);
    } else if (team == "red") {
        pressButton(TEAM_RED);
        animateCapture(TEAM_RED);
    } else {
        server.send(400, "application/json",
            "{\"error\":\"Parametr 'team' musi być 'blue' lub 'red'\"}");
        return;
    }

    JsonDocument doc;
    doc["status"] = "captured";
    doc["currentOwner"] = team;
    doc["blueTime"] = game.blueTime;
    doc["redTime"] = game.redTime;
    doc["timeLeft"] = getTimeLeft();

    String response;
    serializeJson(doc, response);
    server.send(200, "application/json", response);
}

// ---- Handler: GET /history ----
// Lista zakończonych meczy (JSON array)
void handleHistory() {
    setCorsHeaders();

    JsonDocument doc;
    JsonArray arr = doc.to<JsonArray>();

    for (int i = 0; i < historyCount; i++) {
        JsonObject obj = arr.add<JsonObject>();
        obj["id"] = history[i].id;
        obj["duration"] = history[i].duration;
        obj["blueTime"] = history[i].blueTime;
        obj["redTime"] = history[i].redTime;
        obj["winner"] = history[i].winner;
    }

    String response;
    serializeJson(doc, response);
    server.send(200, "application/json", response);
}

// ---- Handler: OPTIONS (preflight CORS) ----
void handleOptions() {
    setCorsHeaders();
    server.send(204);
}

// ---- Rejestracja wszystkich endpointów ----
void registerApiHandlers() {
    server.on("/status",      HTTP_GET,  handleStatus);
    server.on("/game/status", HTTP_GET,  handleGameStatus);
    server.on("/game/start",  HTTP_POST, handleGameStart);
    server.on("/game/stop",   HTTP_POST, handleGameStop);
    server.on("/game/press",  HTTP_POST, handleGamePress);
    server.on("/history",     HTTP_GET,  handleHistory);

    // Obsługa preflight CORS requests
    server.on("/game/start",  HTTP_OPTIONS, handleOptions);
    server.on("/game/stop",   HTTP_OPTIONS, handleOptions);
    server.on("/game/press",  HTTP_OPTIONS, handleOptions);

    Serial.println("[API] Endpointy zarejestrowane");
}

#endif // API_HANDLERS_H

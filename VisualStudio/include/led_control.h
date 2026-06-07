#ifndef LED_CONTROL_H
#define LED_CONTROL_H

#include <Arduino.h>
#include "game_logic.h"

// Piny dla niebieskiej drużyny
const int BLUE_LEDS[] = {2, 4, 16, 17, 5};
// Piny dla czerwonej drużyny
const int RED_LEDS[] = {18, 19, 21, 22, 23};

const int NUM_LEDS_PER_TEAM = 5;

// Status mrugania na koniec gry
unsigned long lastBlinkTime = 0;
bool blinkState = false;
int blinkCount = 0;

void initLeds() {
    for (int i = 0; i < NUM_LEDS_PER_TEAM; i++) {
        pinMode(BLUE_LEDS[i], OUTPUT);
        pinMode(RED_LEDS[i], OUTPUT);
        digitalWrite(BLUE_LEDS[i], LOW);
        digitalWrite(RED_LEDS[i], LOW);
    }
}

void setLedsOff() {
    for (int i = 0; i < NUM_LEDS_PER_TEAM; i++) {
        digitalWrite(BLUE_LEDS[i], LOW);
        digitalWrite(RED_LEDS[i], LOW);
    }
    blinkCount = 0;
}

// Zapala 5 diod konkretnego zespołu (a pozostałe gasi)
void setTeamColor(TeamOwner team) {
    if (team == TEAM_BLUE) {
        for (int i = 0; i < NUM_LEDS_PER_TEAM; i++) {
            digitalWrite(BLUE_LEDS[i], HIGH);
            digitalWrite(RED_LEDS[i], LOW);
        }
    } else if (team == TEAM_RED) {
        for (int i = 0; i < NUM_LEDS_PER_TEAM; i++) {
            digitalWrite(BLUE_LEDS[i], LOW);
            digitalWrite(RED_LEDS[i], HIGH);
        }
    } else {
        setLedsOff();
    }
}

// Efekt mrugania na koniec - mrugają diody drużyny, która wygrała
void blinkVictory(TeamOwner winner) {
    if (!game.finished) {
        blinkCount = 0;
        return;
    }

    if (blinkCount >= 20) { // 10 cykli zapal-zgaś
        setLedsOff();
        return;
    }

    unsigned long now = millis();
    if (now - lastBlinkTime > 300) {  // Mrugnięcie co 300ms
        lastBlinkTime = now;
        blinkState = !blinkState;
        blinkCount++;

        if (winner == TEAM_BLUE) {
            for (int i = 0; i < NUM_LEDS_PER_TEAM; i++) {
                digitalWrite(BLUE_LEDS[i], blinkState ? HIGH : LOW);
                digitalWrite(RED_LEDS[i], LOW);
            }
        } else if (winner == TEAM_RED) {
            for (int i = 0; i < NUM_LEDS_PER_TEAM; i++) {
                digitalWrite(BLUE_LEDS[i], LOW);
                digitalWrite(RED_LEDS[i], blinkState ? HIGH : LOW);
            }
        } else {
            // Remis - mrugają wszystkie
            for (int i = 0; i < NUM_LEDS_PER_TEAM; i++) {
                digitalWrite(BLUE_LEDS[i], blinkState ? HIGH : LOW);
                digitalWrite(RED_LEDS[i], blinkState ? HIGH : LOW);
            }
        }
    }
}

// Podczas ostatnich 10 sekund (odliczanie) - bardzo szybkie mruganie paska aktualnej drużyny
void animateCountdown(int timeLeft) {
    unsigned long now = millis();
    if (now - lastBlinkTime > 100) {  // Szybkie mrugnięcie (100ms)
        lastBlinkTime = now;
        blinkState = !blinkState;

        if (game.currentOwner == TEAM_BLUE) {
            for (int i = 0; i < NUM_LEDS_PER_TEAM; i++) {
                digitalWrite(BLUE_LEDS[i], blinkState ? HIGH : LOW);
                digitalWrite(RED_LEDS[i], LOW);
            }
        } else if (game.currentOwner == TEAM_RED) {
            for (int i = 0; i < NUM_LEDS_PER_TEAM; i++) {
                digitalWrite(BLUE_LEDS[i], LOW);
                digitalWrite(RED_LEDS[i], blinkState ? HIGH : LOW);
            }
        }
    }
}

// Testowy, krótki błysk (zgodność wsteczna z main.cpp)
void setAllLeds(int r, int g, int b) {
    // Zapala po prostu wszystko by sprawdzić zasilanie
    for (int i = 0; i < NUM_LEDS_PER_TEAM; i++) {
        digitalWrite(BLUE_LEDS[i], HIGH);
        digitalWrite(RED_LEDS[i], HIGH);
    }
}

// Zgodność wsteczna z kodem przycisków w main.cpp, nie musi nic robić bo pętla główna i tak zaktualizuje ledy 
void animateCapture(TeamOwner newOwner) {
    // W starej wersji robił tu przejście z gradientem, na zwykłych diodach tego nie robimy.
}

#endif

#ifndef DISPLAY_CONTROL_H
#define DISPLAY_CONTROL_H

// Wyświetlacz
#include <TM1637Display.h>

// Piny dla komunikacji z TM1637
#define CLK_PIN 33
#define DIO_PIN 25

// Inicjalizacja biblioteki
TM1637Display display(CLK_PIN, DIO_PIN);

void initDisplay() {
    // Ustawienie maksymalnej jasności (0x0f)
    display.setBrightness(0x0f);
    display.clear();
}

void showTimeOnDisplay(int seconds) {
    if (seconds < 0) seconds = 0;
    // Wyświetla sekundy bez zer wiodących (false)
    display.showNumberDec(seconds, false);
}

void clearDisplay() {
    display.clear();
}

// Wyświetlenie End na koniec gry
void showEnd() {
    uint8_t data[] = {
        SEG_A | SEG_F | SEG_G | SEG_E | SEG_D,             // E
        SEG_C | SEG_E | SEG_G,                             // n
        SEG_B | SEG_C | SEG_D | SEG_E | SEG_G              // d
    };
    display.clear();
    display.setSegments(data, 3, 1);
}

#endif 

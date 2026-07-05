#include <BleMouse.h>
#include <BleKeyboard.h>

// ---------------- Joystick Pins ----------------
#define VRX 34
#define VRY 35
#define SW  15

BleMouse bleMouse("ESP32 Mouse Controller");
BleKeyboard bleKeyboard("ESP32 Keyboard Controller");

// ---------------- Settings ----------------
int deadzone = 400;          // joystick centre tolerance
int keyThreshold = 1200;     // how far before key press triggers

int mouseSensitivity = 12;   // cursor speed scaling

// ---------------- Setup ----------------
void setup() {
  Serial.begin(115200);

  pinMode(SW, INPUT_PULLUP);

  bleMouse.begin();
  bleKeyboard.begin();

  Serial.println("BLE Mouse + Keyboard started");
}

// ---------------- Loop ----------------
void loop() {

  if (bleMouse.isConnected()) {

    int x = analogRead(VRX);
    int y = analogRead(VRY);

    bool buttonPressed = (digitalRead(SW) == LOW);

    // Convert to centered values (-2048 to +2048 approx)
    int cx = x - 2048;
    int cy = y - 2048;

    // ---------------- MOUSE MOVEMENT ----------------
    int mx = 0;
    int my = 0;

    if (abs(cx) > deadzone) {
      mx = cx / mouseSensitivity;
    }

    if (abs(cy) > deadzone) {
      my = cy / mouseSensitivity;
    }

    bleMouse.move(mx, my);

    // ---------------- MOUSE CLICK ----------------
    if (buttonPressed) {
      bleMouse.press(MOUSE_LEFT);
    } else {
      bleMouse.release(MOUSE_LEFT);
    }

    // ---------------- KEYBOARD INPUT ----------------
    // Only trigger key when pushed far in direction

    if (cx > keyThreshold) {
      bleKeyboard.press('d');
    } else {
      bleKeyboard.release('d');
    }

    if (cx < -keyThreshold) {
      bleKeyboard.press('a');
    } else {
      bleKeyboard.release('a');
    }

    if (cy > keyThreshold) {
      bleKeyboard.press('s');
    } else {
      bleKeyboard.release('s');
    }

    if (cy < -keyThreshold) {
      bleKeyboard.press('w');
    } else {
      bleKeyboard.release('w');
    }

    // ---------------- Debug ----------------
    Serial.print("X:");
    Serial.print(cx);
    Serial.print(" Y:");
    Serial.print(cy);
    Serial.print(" Button:");
    Serial.println(buttonPressed);
  }

  delay(10);
}
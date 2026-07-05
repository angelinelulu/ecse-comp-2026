#include <HijelHID_BLEMouse.h>

HijelBLEMouse mouse; 

#define VRX 34
#define VRY 35
#define SW  15

BleMouse bleMouse("ESP32 Joystick Mouse");

int deadzone = 400;
int sensitivity = 10;

void setup() {
  Serial.begin(115200);

  pinMode(SW, INPUT_PULLUP);

  bleMouse.begin();

  Serial.println("BLE Mouse started");
}

void loop() {

  if (bleMouse.isConnected()) {

    int x = analogRead(VRX) - 2048;
    int y = analogRead(VRY) - 2048;

    bool button = digitalRead(SW) == LOW;

    // ---------------- DEADZONE ----------------
    if (abs(x) < deadzone) x = 0;
    if (abs(y) < deadzone) y = 0;

    // ---------------- MOUSE MOVE ----------------
    int mx = x / sensitivity;
    int my = y / sensitivity;

    bleMouse.move(mx, my);

    // ---------------- CLICK ----------------
    if (button) {
      bleMouse.press(MOUSE_LEFT);
    } else {
      bleMouse.release(MOUSE_LEFT);
    }

    Serial.print("X:");
    Serial.print(x);
    Serial.print(" Y:");
    Serial.print(y);
    Serial.print(" Click:");
    Serial.println(button);
  }

  delay(10);
}
#include <HijelHID_BLEKeyboard.h>

// =====================================================
// Bluetooth keyboard
// =====================================================

HijelHID_BLEKeyboard keyboard(
  "PUFF DADDY Controller 2",
  "UoA Team",
  100
);

// =====================================================
// ESP32-S3 pin assignments
// =====================================================

// Joystick
const int JOY_X  = 1;    // GPIO1, VRX
const int JOY_Y  = 2;    // GPIO2, VRY
const int JOY_SW = 38;   // GPIO38, Confirm / Start

// D-pad
const int DPAD_RIGHT = 39;  // F, Attack
const int DPAD_DOWN  = 40;  // G, Special movement
const int DPAD_UP    = 41;  // J, Mute
const int DPAD_LEFT  = 42;  // Escape, Pause / Settings

// =====================================================
// Joystick settings
// =====================================================

int joystickCenterX = 2048;
int joystickCenterY = 2048;

// Joystick must move this far away from the calibrated
// centre before a direction is detected.
const int JOYSTICK_DEADZONE = 700;

// Average several ADC readings to reduce noise.
const int ADC_SAMPLES = 8;

// Change either value to true if that axis is reversed.
const bool INVERT_JOYSTICK_X = false;
const bool INVERT_JOYSTICK_Y = false;

// =====================================================
// BLE keyboard state
// =====================================================

struct KeyState {
  uint8_t keyCode;
  bool held;
};

// Joystick movement: WASD
// Joystick movement: flipped WASD mapping
KeyState moveUpKey    = {KEY_S, false};
KeyState moveDownKey  = {KEY_W, false};
KeyState moveLeftKey  = {KEY_D, false};
KeyState moveRightKey = {KEY_A, false};

// Joystick SW: Enter
KeyState confirmKey = {KEY_RETURN, false};

// D-pad
KeyState attackKey          = {KEY_F, false};
KeyState specialMovementKey = {KEY_G, false};
KeyState muteKey            = {KEY_J, false};
KeyState pauseKey           = {KEY_ESCAPE, false};

// =====================================================
// Helper functions
// =====================================================

// Digital buttons are wired:
//
// GPIO → switch → GND
//
// INPUT_PULLUP means:
// released = HIGH
// pressed  = LOW
bool isPressed(int pin) {
  return digitalRead(pin) == LOW;
}

// Dummy reading plus averaging improves ADC stability.
int readJoystickAverage(int pin) {
  analogRead(pin);
  delayMicroseconds(50);

  long total = 0;

  for (int i = 0; i < ADC_SAMPLES; i++) {
    total += analogRead(pin);
    delayMicroseconds(10);
  }

  return total / ADC_SAMPLES;
}

// Send a key only when the physical input changes state.
void updateKey(KeyState &key, bool shouldBeHeld) {
  if (!keyboard.isPaired()) {
    return;
  }

  if (shouldBeHeld && !key.held) {
    keyboard.press(key.keyCode);
    key.held = true;
  }
  else if (!shouldBeHeld && key.held) {
    keyboard.release(key.keyCode);
    key.held = false;
  }
}

void resetKeyStates() {
  moveUpKey.held = false;
  moveDownKey.held = false;
  moveLeftKey.held = false;
  moveRightKey.held = false;

  confirmKey.held = false;

  attackKey.held = false;
  specialMovementKey.held = false;
  muteKey.held = false;
  pauseKey.held = false;
}

// =====================================================
// Joystick calibration
// =====================================================

void calibrateJoystick() {
  Serial.println("Do not touch the joystick.");
  Serial.println("Calibrating...");
  delay(1500);

  long xTotal = 0;
  long yTotal = 0;

  const int calibrationSamples = 100;

  for (int i = 0; i < calibrationSamples; i++) {
    xTotal += readJoystickAverage(JOY_X);
    yTotal += readJoystickAverage(JOY_Y);
    delay(5);
  }

  joystickCenterX = xTotal / calibrationSamples;
  joystickCenterY = yTotal / calibrationSamples;

  Serial.println("Calibration complete.");

  Serial.print("Joystick centre X: ");
  Serial.println(joystickCenterX);

  Serial.print("Joystick centre Y: ");
  Serial.println(joystickCenterY);
}

// =====================================================
// Setup
// =====================================================

void setup() {
  Serial.begin(115200);
  delay(1000);

  Serial.println();
  Serial.println("Starting PUFF DADDY Controller 2...");

  // Joystick click
  pinMode(JOY_SW, INPUT_PULLUP);

  // D-pad
  pinMode(DPAD_RIGHT, INPUT_PULLUP);
  pinMode(DPAD_DOWN, INPUT_PULLUP);
  pinMode(DPAD_UP, INPUT_PULLUP);
  pinMode(DPAD_LEFT, INPUT_PULLUP);

  // ESP32-S3 ADC range: 0 to 4095
  analogReadResolution(12);

  calibrateJoystick();

  keyboard.begin();

  Serial.println();
  Serial.println("Bluetooth started.");
  Serial.println("Pair with: PUFF DADDY Controller 2");
}

// =====================================================
// Main loop
// =====================================================

void loop() {
  // ---------------------------------------------------
  // Read joystick
  // ---------------------------------------------------

  int joystickX = readJoystickAverage(JOY_X);
  int joystickY = readJoystickAverage(JOY_Y);

  int differenceX =
    joystickX - joystickCenterX;

  int differenceY =
    joystickY - joystickCenterY;

  if (INVERT_JOYSTICK_X) {
    differenceX = -differenceX;
  }

  if (INVERT_JOYSTICK_Y) {
    differenceY = -differenceY;
  }

  bool joystickLeft =
    differenceX < -JOYSTICK_DEADZONE;

  bool joystickRight =
    differenceX > JOYSTICK_DEADZONE;

  bool joystickUp =
    differenceY < -JOYSTICK_DEADZONE;

  bool joystickDown =
    differenceY > JOYSTICK_DEADZONE;

  // Safety: do not send opposite directions together.
  if (joystickLeft && joystickRight) {
    joystickLeft = false;
    joystickRight = false;
  }

  if (joystickUp && joystickDown) {
    joystickUp = false;
    joystickDown = false;
  }

  // ---------------------------------------------------
  // Read joystick click and D-pad
  // ---------------------------------------------------

  bool confirmPressed =
    isPressed(JOY_SW);

  bool attackPressed =
    isPressed(DPAD_RIGHT);

  bool specialMovementPressed =
    isPressed(DPAD_DOWN);

  bool mutePressed =
    isPressed(DPAD_UP);

  bool pausePressed =
    isPressed(DPAD_LEFT);

  // ---------------------------------------------------
  // Send Bluetooth keyboard input
  // ---------------------------------------------------

  static bool previouslyPaired = false;
  bool paired = keyboard.isPaired();

  if (paired) {
    // Joystick: WASD
    updateKey(moveUpKey, joystickUp);
    updateKey(moveDownKey, joystickDown);
    updateKey(moveLeftKey, joystickLeft);
    updateKey(moveRightKey, joystickRight);

    // Joystick SW: Enter
    updateKey(confirmKey, confirmPressed);

    // D-pad
    updateKey(attackKey, attackPressed);
    updateKey(
      specialMovementKey,
      specialMovementPressed
    );
    updateKey(muteKey, mutePressed);
    updateKey(pauseKey, pausePressed);
  }
  else if (previouslyPaired) {
    keyboard.releaseAll();
    resetKeyStates();
  }

  previouslyPaired = paired;

  // ---------------------------------------------------
  // Serial debugging every 300 ms
  // ---------------------------------------------------

  static unsigned long previousPrintTime = 0;

  if (millis() - previousPrintTime >= 300) {
    previousPrintTime = millis();

    Serial.print("Bluetooth: ");
    Serial.print(paired ? "Paired" : "Waiting");

    Serial.print(" | Joystick X=");
    Serial.print(joystickX);

    Serial.print(" Y=");
    Serial.print(joystickY);

    Serial.print(" | Difference X=");
    Serial.print(differenceX);

    Serial.print(" Y=");
    Serial.print(differenceY);

    Serial.print(" | Active: ");

    if (joystickUp) {
      Serial.print("W ");
    }

    if (joystickDown) {
      Serial.print("S ");
    }

    if (joystickLeft) {
      Serial.print("A ");
    }

    if (joystickRight) {
      Serial.print("D ");
    }

    if (confirmPressed) {
      Serial.print("ENTER ");
    }

    if (attackPressed) {
      Serial.print("F ");
    }

    if (specialMovementPressed) {
      Serial.print("G ");
    }

    if (mutePressed) {
      Serial.print("J ");
    }

    if (pausePressed) {
      Serial.print("ESCAPE ");
    }

    Serial.print("Joystick centre X: ");
    Serial.println(joystickCenterX);

    Serial.print("Joystick centre Y: ");
    Serial.println(joystickCenterY);

    Serial.println();
  }

  // Check inputs approximately 50 times per second.
  delay(20);
}
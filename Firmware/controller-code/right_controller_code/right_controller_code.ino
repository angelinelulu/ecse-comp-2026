#include <HijelHID_BLEKeyboard.h>

// =====================================================
// Bluetooth keyboard
// =====================================================

HijelHID_BLEKeyboard keyboard(
  "PUFF DADDY Controller 1",
  "UoA Team",
  100
  
);

// =====================================================
// FireBeetle 2 ESP32-E pin assignments
// =====================================================

// Right joystick
const int JOY_X  = 34;  // A2 / GPIO34
const int JOY_Y  = 35;  // A3 / GPIO35
const int JOY_SW = 12;  // D13 / GPIO12

// DPAD2
const int DPAD_J         = 25;  // D2 / GPIO25
const int DPAD_K         = 26;  // D3 / GPIO26
const int DPAD_L         = 14;  // D6 / GPIO14
const int DPAD_SEMICOLON = 0;   // D5 / GPIO0

// =====================================================
// Joystick settings
// =====================================================

int joystickCenterX = 2048;
int joystickCenterY = 2048;

const int JOYSTICK_DEADZONE = 700;
const int ADC_SAMPLES = 8;

// Change these if either joystick direction is reversed.
//const bool INVERT_JOYSTICK_X = true;
//const bool INVERT_JOYSTICK_Y = false;

// =====================================================
// Key state structure
// =====================================================

struct KeyState {
  uint8_t keyCode;
  bool held;
};

// =====================================================
// Keyboard assignments
// =====================================================

// Right joystick: arrow keys
KeyState joystickUpKey = {
  KEY_UP,
  false
};

KeyState joystickDownKey = {
  KEY_DOWN,
  false
};

KeyState joystickLeftKey = {
  KEY_LEFT,
  false
};

KeyState joystickRightKey = {
  KEY_RIGHT,
  false
};

// DPAD2: J, K, L, ;
KeyState dpadJKey = {
  KEY_J,
  false
};

KeyState dpadKKey = {
  KEY_K,
  false
};

KeyState dpadLKey = {
  KEY_L,
  false
};

KeyState dpadSemicolonKey = {
  KEY_SEMICOLON,
  false
};

// Joystick switch -> ENTER
KeyState enterKey = {
  0x28,   // HID ENTER / RETURN
  false
};


// =====================================================
// Button helper
// =====================================================

// Button wiring:
//
// GPIO ---- button ---- GND
//
// INPUT_PULLUP:
// released = HIGH
// pressed  = LOW

bool isPressed(int pin) {
  return digitalRead(pin) == LOW;
}

// =====================================================
// Joystick ADC helper
// =====================================================

int readJoystickAverage(int pin) {
  // Dummy read to allow the ADC channel to settle.
  analogRead(pin);
  delayMicroseconds(50);

  long total = 0;

  for (int i = 0; i < ADC_SAMPLES; i++) {
    total += analogRead(pin);
    delayMicroseconds(10);
  }

  return total / ADC_SAMPLES;
}

// =====================================================
// Bluetooth key helper
// =====================================================

void updateKey(KeyState &key, bool shouldBeHeld) {
  if (!keyboard.isConnected()) {
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

// Reset the program's stored key states after Bluetooth
// disconnects.
void resetKeyStates() {
  joystickUpKey.held = false;
  joystickDownKey.held = false;
  joystickLeftKey.held = false;
  joystickRightKey.held = false;
  

  dpadJKey.held = false;
  dpadKKey.held = false;
  dpadLKey.held = false;
  dpadSemicolonKey.held = false;

  enterKey.held = false;
}

// =====================================================
// Joystick calibration
// =====================================================

void calibrateJoystick() {
  Serial.println();
  Serial.println("Do not touch the joystick.");
  Serial.println("Calibrating joystick...");

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

  // ---------------------------------------------------
  // Configure digital inputs
  // ---------------------------------------------------

  pinMode(JOY_SW, INPUT_PULLUP);

  pinMode(DPAD_J, INPUT_PULLUP);
  pinMode(DPAD_K, INPUT_PULLUP);
  pinMode(DPAD_L, INPUT_PULLUP);
  pinMode(DPAD_SEMICOLON, INPUT_PULLUP);

  // ---------------------------------------------------
  // Configure joystick ADC
  // ---------------------------------------------------

  analogReadResolution(12);

  calibrateJoystick();

  // ---------------------------------------------------
  // Start Bluetooth
  // ---------------------------------------------------

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

  int differenceX = joystickX - joystickCenterX;
  int differenceY = joystickY - joystickCenterY;

// Physical joystick direction detection.
//
// Your joystick produces a HIGHER X value when moved left
// and a LOWER X value when moved right.

bool joystickLeft =
  differenceX > JOYSTICK_DEADZONE;

bool joystickRight =
  differenceX < -JOYSTICK_DEADZONE;

// Your joystick produces a HIGHER Y value when moved up
// and a LOWER Y value when moved down.

bool joystickUp =
  differenceY > JOYSTICK_DEADZONE;

bool joystickDown =
  differenceY < -JOYSTICK_DEADZONE;

  // Prevent opposite directions from being active together.
  if (joystickLeft && joystickRight) {
    joystickLeft = false;
    joystickRight = false;
  }

  if (joystickUp && joystickDown) {
    joystickUp = false;
    joystickDown = false;
  }

  // ---------------------------------------------------
  // Read DPAD2
  // ---------------------------------------------------

  bool dpadJPressed =
    isPressed(DPAD_J);

  bool dpadKPressed =
    isPressed(DPAD_K);

  bool dpadLPressed =
    isPressed(DPAD_L);

  bool dpadSemicolonPressed =
    isPressed(DPAD_SEMICOLON);
  
  bool enterPressed =
    isPressed(JOY_SW);

  // ---------------------------------------------------
  // Bluetooth keyboard output
  // ---------------------------------------------------

  static bool previouslyPaired = false;

  bool paired = keyboard.isConnected();

  if (paired) {
    // Right joystick: arrow keys
    updateKey(joystickUpKey, joystickUp);
    updateKey(joystickDownKey, joystickDown);
    updateKey(joystickLeftKey, joystickLeft);
    updateKey(joystickRightKey, joystickRight);

    // DPAD2
    updateKey(dpadJKey, dpadJPressed);
    updateKey(dpadKKey, dpadKPressed);
    updateKey(dpadLKey, dpadLPressed);
    updateKey(dpadSemicolonKey, dpadSemicolonPressed);

    updateKey(enterKey, enterPressed);
  }
  else if (previouslyPaired) {
    keyboard.releaseAll();
    resetKeyStates();
  }

  previouslyPaired = paired;

  // ---------------------------------------------------
  // Serial debugging
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
      Serial.print("UP ");
    }

    if (joystickDown) {
      Serial.print("DOWN ");
    }

    if (joystickLeft) {
      Serial.print("LEFT ");
    }

    if (joystickRight) {
      Serial.print("RIGHT ");
    }

    if (dpadJPressed) {
      Serial.print("J ");
    }

    if (dpadKPressed) {
      Serial.print("K ");
    }

    if (dpadLPressed) {
      Serial.print("L ");
    }

    if (dpadSemicolonPressed) {
      Serial.print("; ");
    }

    if (enterPressed) {
      Serial.print("ENTER ");
    }

    Serial.println();
  }

  // Check the inputs approximately 50 times per second.
  delay(20);
}
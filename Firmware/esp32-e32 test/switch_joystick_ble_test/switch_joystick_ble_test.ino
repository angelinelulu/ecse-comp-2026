#include <HijelHID_BLEMouse.h>

// -------------------------------
// FireBeetle ESP32-E Pin Mapping
// -------------------------------
#define JOY_X_PIN   34
#define JOY_Y_PIN   35
#define JOY_SW_PIN  17

#define DPAD_UP_PIN     25
#define DPAD_DOWN_PIN   26
#define DPAD_LEFT_PIN   14
#define DPAD_RIGHT_PIN  13

HijelBLEMouse mouse("FireBeetle Mouse", "ECSE");

// -------------------------------
// Settings
// -------------------------------
const int DEADZONE = 350;
const int STEP = 20;      // pixels per update

int joyXCenter = 2048;
int joyYCenter = 2048;

// --------------------------------------------------

int averageAnalogRead(int pin)
{
    long total = 0;

    for (int i = 0; i < 50; i++)
    {
        total += analogRead(pin);
        delay(5);
    }

    return total / 50;
}

void calibrateJoystick()
{
    Serial.println("Keep joystick centred...");

    delay(1000);

    joyXCenter = averageAnalogRead(JOY_X_PIN);
    joyYCenter = averageAnalogRead(JOY_Y_PIN);

    Serial.print("X Centre = ");
    Serial.println(joyXCenter);

    Serial.print("Y Centre = ");
    Serial.println(joyYCenter);
}

// --------------------------------------------------

void setup()
{
    Serial.begin(115200);

    pinMode(JOY_SW_PIN, INPUT_PULLUP);

    pinMode(DPAD_UP_PIN, INPUT_PULLUP);
    pinMode(DPAD_DOWN_PIN, INPUT_PULLUP);
    pinMode(DPAD_LEFT_PIN, INPUT_PULLUP);
    pinMode(DPAD_RIGHT_PIN, INPUT_PULLUP);

    calibrateJoystick();

    mouse.setBatteryLevel(100);
    mouse.begin();

    Serial.println("Waiting for Bluetooth pairing...");
}

// --------------------------------------------------

void loop()
{
    if (!mouse.isPaired())
    {
        delay(100);
        return;
    }

    int x = analogRead(JOY_X_PIN) - joyXCenter;
    int y = analogRead(JOY_Y_PIN) - joyYCenter;

    int dx = 0;
    int dy = 0;

    // -------------------------------
    // Mouse movement
    // -------------------------------

    if (x > DEADZONE)
        dx = STEP;

    if (x < -DEADZONE)
        dx = -STEP;

    if (y > DEADZONE)
        dy = STEP;

    if (y < -DEADZONE)
        dy = -STEP;

    if (dx != 0 || dy != 0)
    {
        // Smooth movement over 20 ms
        mouse.moveTo(dx, dy, 20);
    }

    // -------------------------------
    // Buttons
    // -------------------------------

    if (digitalRead(JOY_SW_PIN) == LOW)
    {
        Serial.println("Joystick Button");
        mouse.click(MouseButton::Left);
        delay(200);
    }

    if (digitalRead(DPAD_UP_PIN) == LOW)
    {
        Serial.println("UP");
        mouse.moveTo(0, -60, 50);
        delay(100);
    }

    if (digitalRead(DPAD_DOWN_PIN) == LOW)
    {
        Serial.println("DOWN");
        mouse.moveTo(0, 60, 50);
        delay(100);
    }

    if (digitalRead(DPAD_LEFT_PIN) == LOW)
    {
        Serial.println("LEFT");
        mouse.click(MouseButton::Back);
        delay(200);
    }

    if (digitalRead(DPAD_RIGHT_PIN) == LOW)
    {
        Serial.println("RIGHT");
        mouse.click(MouseButton::Forward);
        delay(200);
    }

    delay(10);
}
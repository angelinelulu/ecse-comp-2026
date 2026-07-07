#include <WiFi.h>
#include <WiFiClient.h>
#include <WiFiAP.h>

// -------------------------------
// Wi-Fi Configuration
// -------------------------------
const char *ssid = "esp32";
const char *password = "123456789";

WiFiServer server(80);

// -------------------------------
// Joystick Pin Configuration
// FireBeetle ESP32-E:
// A2 = GPIO34
// A3 = GPIO35
// A4 = GPIO15
// -------------------------------
#define ANALOG_X_PIN 34
#define ANALOG_Y_PIN 35
#define ANALOG_BUTTON_PIN 15

// ESP32 ADC range is usually 0 to 4095
#define ADC_MIN 0
#define ADC_MAX 4095

// Mapped joystick range
#define JOYSTICK_MIN 0
#define JOYSTICK_MAX 255
#define JOYSTICK_CENTER 128

// Deadzone around center
#define DEADZONE 20

struct ButtonState {
  bool pressed = false;
};

struct JoystickState {
  int rawX = 0;
  int rawY = 0;

  int mappedX = 0;
  int mappedY = 0;

  int correctedX = 0;
  int correctedY = 0;

  String xDirection = "CENTER";
  String yDirection = "CENTER";

  ButtonState button;
};

JoystickState joystick;

// -------------------------------
// Helper Functions
// -------------------------------

int readAnalogAxisLevel(int pin) {
  int rawValue = analogRead(pin);
  return map(rawValue, ADC_MIN, ADC_MAX, JOYSTICK_MIN, JOYSTICK_MAX);
}

bool isAnalogButtonPressed(int pin) {
  // Joystick SW is connected to GND when pressed
  return digitalRead(pin) == LOW;
}

String getAxisDirection(int correctedValue, String negativeName, String positiveName) {
  if (correctedValue > DEADZONE) {
    return positiveName;
  }
  else if (correctedValue < -DEADZONE) {
    return negativeName;
  }
  else {
    return "CENTER";
  }
}

void updateJoystickState() {
  joystick.rawX = analogRead(ANALOG_X_PIN);
  joystick.rawY = analogRead(ANALOG_Y_PIN);

  joystick.mappedX = map(joystick.rawX, ADC_MIN, ADC_MAX, JOYSTICK_MIN, JOYSTICK_MAX);
  joystick.mappedY = map(joystick.rawY, ADC_MIN, ADC_MAX, JOYSTICK_MIN, JOYSTICK_MAX);

  joystick.correctedX = joystick.mappedX - JOYSTICK_CENTER;
  joystick.correctedY = joystick.mappedY - JOYSTICK_CENTER;

  joystick.xDirection = getAxisDirection(joystick.correctedX, "LEFT", "RIGHT");
  joystick.yDirection = getAxisDirection(joystick.correctedY, "UP", "DOWN");

  joystick.button.pressed = isAnalogButtonPressed(ANALOG_BUTTON_PIN);
}

void printJoystickState() {
  Serial.print("Raw X: ");
  Serial.print(joystick.rawX);
  Serial.print(" | Raw Y: ");
  Serial.println(joystick.rawY);

  Serial.print("Mapped X: ");
  Serial.print(joystick.mappedX);
  Serial.print(" | Mapped Y: ");
  Serial.println(joystick.mappedY);

  Serial.print("Corrected X: ");
  Serial.print(joystick.correctedX);
  Serial.print(" | Corrected Y: ");
  Serial.println(joystick.correctedY);

  Serial.print("X Direction: ");
  Serial.print(joystick.xDirection);
  Serial.print(" | Y Direction: ");
  Serial.println(joystick.yDirection);

  if (joystick.button.pressed) {
    Serial.println("Button: PRESSED");
  }
  else {
    Serial.println("Button: NOT PRESSED");
  }

  Serial.println("-------------------------");
}

void sendWebPage(WiFiClient client) {
  client.println("HTTP/1.1 200 OK");
  client.println("Content-type:text/html");
  client.println("Connection: close");
  client.println();

  client.println("<!DOCTYPE html>");
  client.println("<html>");

  client.println("<head>");
  client.println("<meta http-equiv='refresh' content='1'>");
  client.println("<title>ESP32 Joystick Monitor</title>");
  client.println("</head>");

  client.println("<body>");
  client.println("<h1>ESP32 Joystick Monitor</h1>");

  client.println("<h2>Analog Values</h2>");

  client.print("<p>Raw X: ");
  client.print(joystick.rawX);
  client.println("</p>");

  client.print("<p>Raw Y: ");
  client.print(joystick.rawY);
  client.println("</p>");

  client.print("<p>Mapped X: ");
  client.print(joystick.mappedX);
  client.println(" / 255</p>");

  client.print("<p>Mapped Y: ");
  client.print(joystick.mappedY);
  client.println(" / 255</p>");

  client.print("<p>Corrected X: ");
  client.print(joystick.correctedX);
  client.println("</p>");

  client.print("<p>Corrected Y: ");
  client.print(joystick.correctedY);
  client.println("</p>");

  client.println("<h2>Direction</h2>");

  client.print("<p>X Direction: <b>");
  client.print(joystick.xDirection);
  client.println("</b></p>");

  client.print("<p>Y Direction: <b>");
  client.print(joystick.yDirection);
  client.println("</b></p>");

  client.println("<h2>Joystick Button</h2>");

  if (joystick.button.pressed) {
    client.println("<h3 style='color:green;'>Button Status: PRESSED</h3>");
  }
  else {
    client.println("<h3 style='color:red;'>Button Status: NOT PRESSED</h3>");
  }

  client.println("<hr>");
  client.println("<p>VRX: A2 / GPIO34</p>");
  client.println("<p>VRY: A3 / GPIO35</p>");
  client.println("<p>SW: A4 / GPIO15</p>");
  client.println("<p>Page refreshes every 1 second.</p>");

  client.println("</body>");
  client.println("</html>");
}

// -------------------------------
// Setup
// -------------------------------

void setup() {
  Serial.begin(115200);
  Serial.println();

  pinMode(ANALOG_BUTTON_PIN, INPUT_PULLUP);

  Serial.println("Configuring Access Point...");
  WiFi.softAP(ssid, password);

  IPAddress myIP = WiFi.softAPIP();

  Serial.print("AP IP Address: ");
  Serial.println(myIP);

  server.begin();

  Serial.println("Server Started");
}

// -------------------------------
// Loop
// -------------------------------

void loop() {
  updateJoystickState();
  printJoystickState();

  WiFiClient client = server.available();

  if (client) {
    Serial.println("New Web Client Connected.");

    String currentLine = "";

    while (client.connected()) {
      if (client.available()) {
        char c = client.read();
        Serial.write(c);

        if (c == '\n') {
          if (currentLine.length() == 0) {
            sendWebPage(client);
            break;
          }
          else {
            currentLine = "";
          }
        }
        else if (c != '\r') {
          currentLine += c;
        }
      }
    }

    client.stop();
    Serial.println("Web Client Disconnected.");
  }

  delay(200);
}
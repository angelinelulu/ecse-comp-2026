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
// FireBeetle ESP32-E Pin Mapping
// -------------------------------


// Joystick pins
// A2 = GPIO34
// A3 = GPIO35
// D10 = GPIO17
#define JOY_X_PIN   34
#define JOY_Y_PIN   35
#define JOY_SW_PIN  17


// DPAD1 pins
// D2 = GPIO25
// D3 = GPIO26
// D6 = GPIO14
// D7 = GPIO13
#define DPAD_UP_PIN     25
#define DPAD_DOWN_PIN   26
#define DPAD_LEFT_PIN   14
#define DPAD_RIGHT_PIN  13


// -------------------------------
// Joystick Settings
// -------------------------------
const int DEADZONE = 350;


int joyXCenter = 2048;
int joyYCenter = 2048;


int rawX = 0;
int rawY = 0;
int correctedX = 0;
int correctedY = 0;


String xDirection = "CENTER";
String yDirection = "CENTER";


// -------------------------------
// Helper Functions
// -------------------------------


bool isPressed(int pin) {
  return digitalRead(pin) == LOW;
}


String pressedText(bool pressed) {
  return pressed ? "PRESSED" : "not pressed";
}


String getXDirection(int value) {
  if (value > DEADZONE) {
    return "RIGHT";
  }
  else if (value < -DEADZONE) {
    return "LEFT";
  }
  else {
    return "CENTER";
  }
}


String getYDirection(int value) {
  if (value > DEADZONE) {
    return "DOWN";
  }
  else if (value < -DEADZONE) {
    return "UP";
  }
  else {
    return "CENTER";
  }
}


int averageAnalogRead(int pin) {
  long total = 0;


  for (int i = 0; i < 50; i++) {
    total += analogRead(pin);
    delay(5);
  }


  return total / 50;
}


void calibrateJoystick() {
  Serial.println("Calibrating joystick...");
  Serial.println("Keep joystick centred.");
  delay(1000);


  joyXCenter = averageAnalogRead(JOY_X_PIN);
  joyYCenter = averageAnalogRead(JOY_Y_PIN);


  Serial.println("Calibration complete.");
  Serial.print("Joystick X center: ");
  Serial.println(joyXCenter);
  Serial.print("Joystick Y center: ");
  Serial.println(joyYCenter);
  Serial.println("--------------------------------");
}


void updateJoystick() {
  rawX = analogRead(JOY_X_PIN);
  rawY = analogRead(JOY_Y_PIN);


  correctedX = rawX - joyXCenter;
  correctedY = rawY - joyYCenter;


  xDirection = getXDirection(correctedX);
  yDirection = getYDirection(correctedY);
}


void printSerialStatus() {
  Serial.println("========== ESP32-E JOYSTICK + DPAD1 TEST ==========");


  Serial.print("Joystick | Raw X: ");
  Serial.print(rawX);
  Serial.print(" Raw Y: ");
  Serial.print(rawY);


  Serial.print(" | Corrected X: ");
  Serial.print(correctedX);
  Serial.print(" Corrected Y: ");
  Serial.print(correctedY);


  Serial.print(" | Direction: ");
  Serial.print(xDirection);
  Serial.print(" / ");
  Serial.println(yDirection);


  Serial.print("Joystick SW A4/GPIO15: ");
  Serial.println(pressedText(isPressed(JOY_SW_PIN)));


  Serial.println("--- DPAD1 ---");
  Serial.print("UP D2/GPIO25: ");
  Serial.print(pressedText(isPressed(DPAD_UP_PIN)));
  Serial.print(" | DOWN D3/GPIO26: ");
  Serial.print(pressedText(isPressed(DPAD_DOWN_PIN)));
  Serial.print(" | LEFT D6/GPIO14: ");
  Serial.print(pressedText(isPressed(DPAD_LEFT_PIN)));
  Serial.print(" | RIGHT D7/GPIO13: ");
  Serial.println(pressedText(isPressed(DPAD_RIGHT_PIN)));


  Serial.println("===================================================");
  Serial.println();
}


void printButtonHTML(WiFiClient &client, String name, int pin) {
  bool pressed = isPressed(pin);


  client.print("<p>");
  client.print(name);
  client.print(": ");


  if (pressed) {
    client.print("<b style='color:green;'>PRESSED</b>");
  } else {
    client.print("<span style='color:red;'>not pressed</span>");
  }


  client.println("</p>");
}


void sendWebPage(WiFiClient &client) {
  updateJoystick();


  client.println("HTTP/1.1 200 OK");
  client.println("Content-type:text/html");
  client.println();


  client.println("<!DOCTYPE html>");
  client.println("<html>");


  client.println("<head>");
  client.println("<meta http-equiv='refresh' content='1'>");
  client.println("<title>ESP32-E Joystick + DPAD1 Test</title>");
  client.println("</head>");


  client.println("<body>");
  client.println("<h1>ESP32-E Joystick + DPAD1 Test</h1>");
  client.println("<p>Page refreshes every 1 second.</p>");


  client.println("<h2>Joystick</h2>");


  client.print("<p>Raw X: ");
  client.print(rawX);
  client.print(" | Raw Y: ");
  client.print(rawY);
  client.println("</p>");


  client.print("<p>Corrected X: ");
  client.print(correctedX);
  client.print(" | Corrected Y: ");
  client.print(correctedY);
  client.println("</p>");


  client.print("<p>Direction: <b>");
  client.print(xDirection);
  client.print(" / ");
  client.print(yDirection);
  client.println("</b></p>");


  printButtonHTML(client, "Joystick SW A4/GPIO15", JOY_SW_PIN);


  client.println("<h2>DPAD1</h2>");


  printButtonHTML(client, "UP D2/GPIO25", DPAD_UP_PIN);
  printButtonHTML(client, "DOWN D3/GPIO26", DPAD_DOWN_PIN);
  printButtonHTML(client, "LEFT D6/GPIO14", DPAD_LEFT_PIN);
  printButtonHTML(client, "RIGHT D7/GPIO13", DPAD_RIGHT_PIN);


  client.println("<hr>");
  client.println("<p>Joystick VRX: A2 / GPIO34</p>");
  client.println("<p>Joystick VRY: A3 / GPIO35</p>");
  client.println("<p>Joystick SW: A4 / GPIO15</p>");


  client.println("</body>");
  client.println("</html>");
}


// -------------------------------
// Setup
// -------------------------------
void setup() {
  Serial.begin(115200);
  Serial.println();


  // Joystick button
  pinMode(JOY_SW_PIN, INPUT_PULLUP);


  // DPAD1 buttons
  pinMode(DPAD_UP_PIN, INPUT_PULLUP);
  pinMode(DPAD_DOWN_PIN, INPUT_PULLUP);
  pinMode(DPAD_LEFT_PIN, INPUT_PULLUP);
  pinMode(DPAD_RIGHT_PIN, INPUT_PULLUP);


  calibrateJoystick();


  Serial.println("Configuring access point...");


  // Keep WiFi setup same as your working code
  WiFi.softAP(ssid, password);


  IPAddress myIP = WiFi.softAPIP();


  Serial.print("AP IP address: ");
  Serial.println(myIP);


  server.begin();


  Serial.println("Server started");
}


// -------------------------------
// Loop
// -------------------------------
void loop() {
  updateJoystick();
  printSerialStatus();


  WiFiClient client = server.available();


  if (client) {
    Serial.println("New Client.");


    String currentLine = "";


    while (client.connected()) {
      if (client.available()) {
        char c = client.read();


        Serial.write(c);


        if (c == '\n') {
          if (currentLine.length() == 0) {
            sendWebPage(client);
            break;
          } else {
            currentLine = "";
          }
        } else if (c != '\r') {
          currentLine += c;
        }
      }
    }


    client.stop();
    Serial.println("Client Disconnected.");
  }


  delay(500);
}

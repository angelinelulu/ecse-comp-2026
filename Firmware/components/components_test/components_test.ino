#include <WiFi.h>
#include <WiFiClient.h>
#include <WiFiAP.h>

// --- Wi-Fi Constants ---
const char *ssid = "esp32";
const char *password = "123456789";
WiFiServer server(80);

// --- Button & LED Configuration ---
const int NUM_BUTTONS = 4; 
// Reassigned to safe FireBeetle 2 pins (Avoiding GPIO 2 [onboard LED] and GPIO 3 [RX])
const int BUTTON_PINS[NUM_BUTTONS] = {4, 12, 13, 14};
const int LED_PINS[NUM_BUTTONS]    = {25, 26, 27, 9};
int BUTTON_STATES[NUM_BUTTONS]     = {0, 0, 0, 0}; 

// --- Joystick Configuration ---
#define ANALOG_X_PIN A2
#define ANALOG_Y_PIN A3
#define ANALOG_BUTTON_PIN A4
     
#define ANALOG_X_CORRECTION 128
#define ANALOG_Y_CORRECTION 128
     
struct button {
     byte pressed = 0;
};
     
struct analog {
     short x, y;
     button button;
};

// Timing variable to handle non-blocking printing for the joystick
unsigned long lastJoystickPrint = 0;
const unsigned long joystickInterval = 200; 

// Forward declarations
byte readAnalogAxisLevel(int pin);
bool isAnalogButtonPressed(int pin);

void setup() {
  Serial.begin(115200);
  Serial.println();
  
  // Initialize Web Server Built-in LED
  pinMode(LED_BUILTIN, OUTPUT); 

  // Initialize Digital Buttons & LEDs
  for (int i = 0; i < NUM_BUTTONS; i++) { 
    pinMode(BUTTON_PINS[i], INPUT_PULLUP);
    pinMode(LED_PINS[i], OUTPUT); 
  }

  // Initialize Joystick Button
  pinMode(ANALOG_BUTTON_PIN, INPUT_PULLUP);

  // Configure Access Point
  Serial.println("Configuring access point...");
  WiFi.softAP(ssid, password);
  IPAddress myIP = WiFi.softAPIP();
  Serial.print("AP IP address: ");
  Serial.println(myIP);
  
  server.begin();
  Serial.println("Server started");
}

void loop() {
  // ----------------------------------------------------
  // 1. DIGITAL BUTTONS & LEDS LOGIC
  // ----------------------------------------------------
  for (int i = 0; i < NUM_BUTTONS; i++) { 
    BUTTON_STATES[i] = digitalRead(BUTTON_PINS[i]);

    // INPUT_PULLUP means LOW = Pressed
    if (BUTTON_STATES[i] == LOW) {
      digitalWrite(LED_PINS[i], HIGH); // Turn LED on
      Serial.print("Button : "); 
      Serial.print(i); 
      Serial.println(" has been pressed");
    } else {
      digitalWrite(LED_PINS[i], LOW);  // Turn LED off
    }
  }

  // ----------------------------------------------------
  // 2. JOYSTICK LOGIC (Non-blocking timing)
  // ----------------------------------------------------
  if (millis() - lastJoystickPrint >= joystickInterval) {
    lastJoystickPrint = millis();
    
    analog analogData;
    analogData.x = readAnalogAxisLevel(ANALOG_X_PIN) - ANALOG_X_CORRECTION;
    analogData.y = readAnalogAxisLevel(ANALOG_Y_PIN) - ANALOG_Y_CORRECTION;
    analogData.button.pressed = isAnalogButtonPressed(ANALOG_BUTTON_PIN);
     
    Serial.print("X:"); Serial.print(analogData.x);
    Serial.print(" | Y:"); Serial.println(analogData.y);
     
    if (analogData.button.pressed) {
      Serial.println("Joystick Button: Pressed");
    }
  }

  // ----------------------------------------------------
  // 3. WIFI WEB SERVER LOGIC
  // ----------------------------------------------------
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
              client.println("HTTP/1.1 200 OK");
              client.println("Content-type:text/html");
              client.println("Connection: close");
              client.println();
              client.print("Click <a href=\"/H\">here</a> to turn ON the built-in LED.<br>");
              client.print("Click <a href=\"/L\">here</a> to turn OFF the built-in LED.<br>");
              client.println();
              break;
            } else {   
              currentLine = "";
            }
          } else if (c != '\r') {  
            currentLine += c;     
          }
          if (currentLine.endsWith("GET /H")) {
            digitalWrite(LED_BUILTIN, HIGH);               
          }
          if (currentLine.endsWith("GET /L")) {
            digitalWrite(LED_BUILTIN, LOW);                
          }
        }
    }
    client.stop();     
    Serial.println("Web Client Disconnected.");
  }
}

// Map function altered to match ESP32 12-bit range (0 - 4095)
byte readAnalogAxisLevel(int pin) {
     return map(analogRead(pin), 0, 4095, 0, 255);
}
     
bool isAnalogButtonPressed(int pin) {
     return digitalRead(pin) == LOW; 
}
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
// GPIO Configuration
// -------------------------------
// D12 on the FireBeetle ESP32-E = GPIO12
const int LED_PIN = 12;

// Variable to store the current state
bool isExternalLedOn = false;

void setup() {

  Serial.begin(115200);
  Serial.println();

  // Configure GPIO12 as an INPUT only
  pinMode(LED_PIN, INPUT);

  // Configure WiFi Access Point
  Serial.println("Configuring Access Point...");
  WiFi.softAP(ssid, password);

  IPAddress myIP = WiFi.softAPIP();

  Serial.print("AP IP Address: ");
  Serial.println(myIP);

  server.begin();

  Serial.println("Server Started");
}

void loop() {

  // ---------------------------------
  // Read the external signal
  // ---------------------------------
  isExternalLedOn = (digitalRead(LED_PIN) == HIGH);

  // Print current state
  if (isExternalLedOn) {
    Serial.println("System Log: External LED Signal is HIGH.");
  }
  else {
    Serial.println("System Log: External LED Signal is LOW.");
  }

  delay(500);

  // ---------------------------------
  // Web Server
  // ---------------------------------
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

            client.println("<!DOCTYPE html>");
            client.println("<html>");

            client.println("<head>");
            client.println("<meta http-equiv='refresh' content='1'>");
            client.println("<title>ESP32 LED Monitor</title>");
            client.println("</head>");

            client.println("<body>");
            client.println("<h1>ESP32 External LED Monitor</h1>");

            if (isExternalLedOn) {

              client.println("<h2 style='color:green;'>");
              client.println("LED Status: ON");
              client.println("</h2>");

            }
            else {

              client.println("<h2 style='color:red;'>");
              client.println("LED Status: OFF");
              client.println("</h2>");

            }

            client.println("<p>GPIO Pin: D12 (GPIO12)</p>");
            client.println("<p>Refreshing every second...</p>");

            client.println("</body>");
            client.println("</html>");

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
}
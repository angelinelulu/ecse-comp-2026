/*WiFiAccessPoint.ino creates a wifi hotspot and provides a web service
  Steps:
  1. Connect to the wifi "yourAp"
  2. Access https://192.168.4.1/H to turn on the LED, or access https://192.168.4.1/L to turn off the LED*/

#include <WiFi.h>
#include <WiFiClient.h>
#include <WiFiAP.h>

// Set your wifi name and password
const char *ssid = "esp32";
const char *password = "123456789";

WiFiServer server(80);

void setup() {
  pinMode(LED_BUILTIN, OUTPUT); //Set the LED pin as output
  Serial.begin(115200);
  Serial.begin(115200);
  Serial.println();
  Serial.println("Configuring access point...");

  // Configure wifi and get IP address
  WiFi.softAP(ssid, password);
  IPAddress myIP = WiFi.softAPIP();
  Serial.print("AP IP address: ");
  Serial.println(myIP);
  server.begin();
  Serial.println("Server started");
}

void loop() {
  WiFiClient client = server.available();   // Listen to the server

  if (client) {                             // If there is message from the server 
    Serial.println("New Client.");           // Print the message on the serial port
    String currentLine = "";                // Create a String to save the incoming data from the client
    while (client.connected()) {           
        char c = client.read();          
        Serial.write(c);                    
        if (c == '\n') {                  
          if (currentLine.length() == 0) {
            client.println("HTTP/1.1 200 OK");
            client.println("Content-type:text/html");
            client.println();
            client.print("Click <a href=\"/H\">here</a> to turn ON the LED.<br>");
            client.print("Click <a href=\"/L\">here</a> to turn OFF the LED.<br>");
            client.println();
            break;
          } else {   
            currentLine = "";
          }
        } else if (c != '\r') {  
          currentLine += c;     
        }
        if (currentLine.endsWith("GET /H")) {
          digitalWrite(LED_BUILTIN, HIGH);               // GET /H turns on the LED
        }
        if (currentLine.endsWith("GET /L")) {
          digitalWrite(LED_BUILTIN, LOW);                // GET /L turns off the LED
      }
    }
    client.stop();     
    Serial.println("Client Disconnected.");
  }
}

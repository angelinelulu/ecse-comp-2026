#include <BLEDevice.h>
#include <BLEServer.h>
#include <BLEUtils.h>
#include <BLE2902.h>
#define SERVICE_UUID             "DFCD0001-36E1-4688-B7F5-EA07361B26A8"
#define CHARACTERISTIC1_UUID     "DFCD000A-36E1-4688-B7F5-EA07361B26A8"
bool deviceConnected = false;
BLEServer *pServer;
BLEService *pService;
BLECharacteristic* pCharacteristic;

class MyServerCallbacks: public BLEServerCallbacks {
    void onConnect(BLEServer* pServer) {
      deviceConnected = true;
    };

    void onDisconnect(BLEServer* pServer) {
      deviceConnected = false;
    }
};
class MyCallbacks: public BLECharacteristicCallbacks {
    void onWrite(BLECharacteristic *pCharacteristic) {
      String value = pCharacteristic->getValue();

      if (value.length() > 0) {
        Serial.println("*********");
        Serial.print("New value: ");
        for (int i = 0; i < value.length(); i++){
          Serial.print(value[i]);
        }
        Serial.println();
        Serial.println("*********");
        pCharacteristic->notify();
      }
    }
};
void setupBLE()
{
  BLEDevice::init("DFRobot_ESP32");   // Create BLE device
  pServer = BLEDevice::createServer();   // Create BLE server
  pServer->setCallbacks(new MyServerCallbacks());   // Set the server's callback function
  pService = pServer->createService(SERVICE_UUID); // Create BLE service
  
  pCharacteristic = pService->createCharacteristic(
                                                   CHARACTERISTIC1_UUID,
                                                   BLECharacteristic::PROPERTY_READ   |
                                                   BLECharacteristic::PROPERTY_NOTIFY |
                                                   BLECharacteristic::PROPERTY_WRITE); 
                                                   
  pCharacteristic->setCallbacks(new MyCallbacks());    // Set the callback function for the characteristic
  pCharacteristic->addDescriptor(new BLE2902());
  
  // Set initial value
  pCharacteristic->setValue("Hello DFRobot");
  
  pService->start();
  
  // --- Updated Advertising Settings ---
  BLEAdvertising *pAdvertising = BLEDevice::getAdvertising();
  pAdvertising->addServiceUUID(SERVICE_UUID);
  pAdvertising->setScanResponse(true);
  
  // These parameters help devices like iOS/Android connect without timing out
  pAdvertising->setMinPreferred(0x06);  // functions that help with iPhone connections issue
  pAdvertising->setMinPreferred(0x12);
  
  BLEDevice::startAdvertising();
  Serial.println("BLE Advertising Started...");
}
void setup() {
  Serial.begin(115200);
  setupBLE();
}

void loop() {
   delay(3000);
}

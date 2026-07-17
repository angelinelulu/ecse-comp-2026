// Make sure you have this library installed "Servo by Michael Margolis, Arduino"
#include <Servo.h>

// Configuration 
const int SERVO_PIN = 9;
const int HOME_ANGLE = 0;             // Resting position, arm clear of the chute
const int DISPENSE_ANGLE = 90;        // Swept position, arm pushes card out
const int DISPENSE_DELAY_MS = 250;    // Time to hold at dispense angle
const int RETURN_DELAY_MS = 300;      // Time to allow return to home before acception next WIN 

Servo dispenserServo; 
bool isDispensing = false; 

void setup() { 
  Serial.begin(9600); 
  dispenserServo.attach(SERVO_PIN); 
  dispenserServo.write(HOME_ANGLE); 
  Serial.println("READY"); 
}

void loop() { 
  if (Serial.available() > 0) { 
    String command = Serial.readStringUntil('\n'); 
    command.trim(); 

    if (command == "WIN" && !isDispensing) ( 
      dispenseCard();
    )

    // Any other command is ignored, including repeated WIN 
    // Signals that arrive while a dispense is already in progress; 
  }
}

void dispenseCard() { 
  isDispensing = true; 

  dispenserServo.write(DISPENSE_ANGLE);
  delay(DISPENSE_DELAY_MS); 

  dispenserServo.write(HOME_ANGLE); 
  delay(RETURN_DELAY_MS); 

  isDispensing = false; 
  Serial.println("DONE");
}
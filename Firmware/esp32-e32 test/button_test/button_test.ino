// constants won't change. They're used here to set pin numbers:
const int NUM_BUTTONS = 4; 
// Pins for buttons A, B, X, Y
const int BUTTON_PINS[NUM_BUTTONS] = {2, 3, 4, 5};
// Pins for each button's corresponding LED
const int LED_PINS[NUM_BUTTONS] = {10, 11, 12, 13};
// Holds the current reading for each button 
int BUTTON_STATES[NUM_BUTTONS] = {0, 0, 0, 0}; 

void setup() {
  for (int i = 0; i < NUM_BUTTONS; i++) { 
      // initialize the pushbutton pin as an input:
    pinMode(BUTTON_PINS[i], INPUT_PULLUP);
    // initialize the LED pin as an output: 
    pinMode(LED_PINS[i], OUTPUT); 
  }
}
void loop() {
  // Read the state of the pushbutton value:
  for (int i = 0; i < NUM_BUTTONS; i++) { 
    BUTTON_STATES[i] = digitalRead(BUTTON_PINS[i]);

    // Check if the pushbutton is pressed. If it is, the buttonState is HIGH:
    if (BUTTON_STATES[i] == HIGH) {
      // turn LED on:
      digitalWrite(LED_PINS[i], HIGH);
      Serial.println("Button : "); 
      Serial.println(i); 
      Serial.println(" has been pressed");
    } else {
      // turn LED off:
      digitalWrite(LED_PINS[i], LOW);
    }
  }
}
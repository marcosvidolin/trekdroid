const int trigPin = 2;
const int echoPin = 4;

void setup() {
  Serial.begin(9600);
}

void loop() {
  long duration, inches, cm;
  
  pinMode(trigPin, OUTPUT);
  digitalWrite(trigPin, LOW);
  delayMicroseconds(2);
  digitalWrite(trigPin, HIGH);
  delayMicroseconds(1000);
  digitalWrite(trigPin, LOW);
  delayMicroseconds(10);
  
  pinMode(echoPin, INPUT);
  duration = pulseIn(echoPin, HIGH);
  
  delay(150);
  
  Serial.print(duration);
  Serial.print("duration");
  Serial.println("");
  

}

long microsecondsToCentimeters(long microseconds) {
  return microseconds / 29 / 2;
}

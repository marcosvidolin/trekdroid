#include <HMC.h>

void setupMagnetometro() {
  // The HMC5843 needs 5ms before it will communicate
  delay(5);
  HMC.init();
}

/**
 * Obtem os graus de acordo com o eixo x e y do magnetometro.
 */
float getDegrees(int x, int y) {
  // Calculate heading when the magnetometer is level, then correct for signs of axis.
  float heading = atan2(y, x);
   
  // Correct for when signs are reversed.
  if(heading < 0)
    heading += 2 * PI;
   
  // Convert radians to degrees for readability.
  return heading * 180 / M_PI; 
}


void setup() {
  Serial.begin(9600);
  setupMagnetometro();
}

void loop() {
  
  int x,y,z;
  // There will be new values every 100ms
  delay(100);
  HMC.getValues(&x,&y,&z);
  
  Serial.println(getDegrees(x, y));
  
  /*Serial.print("x:");
  Serial.print(x);
  Serial.print(" y:");
  Serial.print(y);
  Serial.print(" z:");
  Serial.println(z);*/
}

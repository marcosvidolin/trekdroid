/**
 * Teste sensor LV-MaxSonar -EZ3.
 *
 * @author Marcos Vidolin
 * @sinze 11/05/2013
 */

const unsigned int SONAR_PIN = 4; 

/**
 * Configura o sonar.
 */
void setupSonar() {
  pinMode(SONAR_PIN, INPUT);
}

void setup() {
  setupSonar();
  Serial.begin(9600);
}

/**
 * Converte pulso (microssegundos) em centimetros.
 *
 * Existem 29 microssegundos em 1 cm, entao divide-se o valor do pulso por 29.
 * Com isso obtem-se a distancia total (centimetros) percorridos apartir do 
 * disparo do pulso ate atingir um objeto e volta. Entao divide-se a distancia
 * total por 2 para obter a distancia entre sensor e o objeto.
 */
unsigned long sonarPulsoParaCentimetros(unsigned long pulso) {
  return pulso / 29 / 2;
}

/**
 * Checa se um objeto foi detectado pelo sonar.
 */
boolean isObjetoDetectadoViaSonar() {
  long pulse;
  delay(250);
  
  //Used to read in the pulse that is being sent by the MaxSonar device.
  //Pulse Width representation with a scale factor of 147 uS per Inch.
  pulse = pulseIn(SONAR_PIN, HIGH); //147uS per inch 
  if (sonarPulsoParaCentimetros(pulse) > 0)
    return true;
 
  return false;
}

void loop() {
  Serial.print(sonarPulsoParaCentimetros(pulseIn(SONAR_PIN, HIGH))); 
  Serial.print("cm"); 
  Serial.println();
  Serial.println(isObjetoDetectadoViaSonar());
}
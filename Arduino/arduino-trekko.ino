/**
 * Programa usado no Arduino UNO para controle do Robo de Trekking.
 * @author Marcos Vidolin
 * @since 04/05/2013
 */

#include <Servo.h>


const unsigned int MOTOR_ESQUERDO_PIN = 2;
const unsigned int MOTOR_DIREITO_PIN = 3;
const unsigned int SONAR_PIN = 4;


const unsigned int VELOCIDADE_MIN = 1;
const unsigned int VELOCIDADE_MAX = 2;

const unsigned int ANGULO_MOTOR_PARADO = 95;

const unsigned int ANGULO_MIN_FRENTE = 110;
const unsigned int ANGULO_MAX_FRENTE = 120;

const unsigned int ANGULO_MIN_RE = 80;
const unsigned int ANGULO_MAX_RE = 70;


Servo motorEsq;
Servo motorDir;

/**
 * Para os motores do Robo.
 */
void pararMotores() {
  motorEsq.write(ANGULO_MOTOR_PARADO);
  motorDir.write(ANGULO_MOTOR_PARADO);
}

/**
 * Configura os motores do Robo.
 */
void setupMotores() {
  motorEsq.attach(MOTOR_ESQUERDO_PIN);
  motorDir.attach(MOTOR_DIREITO_PIN);
  pararMotores();
}

/**
 * Configura o sonar.
 */
void setupSonar() {
  pinMode(SONAR_PIN, INPUT);
}

/**
 * Obtem o angulo dos motores de acordo com a 
 * velocidade passada por parametro.
 *
 * @return unsigned int
 */
unsigned int getAnguloMotoresFrente(unsigned int velocidade) {
  if (VELOCIDADE_MIN) {
    return ANGULO_MIN_FRENTE;
  }
  return ANGULO_MAX_FRENTE;
}

/**
 * Obtem o angulo dos motores de acordo com a 
 * velocidade passada por parametro.
 *
 * @return unsigned int
 */
unsigned int getAnguloMotoresRe(unsigned int velocidade) {
  if (VELOCIDADE_MIN) {
    return ANGULO_MIN_RE;
  }
  return ANGULO_MAX_RE;
}

/**
 * Move o Robo para frente.
 */
void moverParaFrente(unsigned int velocidade) {
  unsigned int angulo = getAnguloMotoresFrente(velocidade);
  motorEsq.write(angulo);
  motorDir.write(angulo);
}

/**
 * Move o Robo para frente.
 */
void moverParaTraz(unsigned int velocidade) {
  unsigned int angulo = getAnguloMotoresRe(velocidade);
  motorEsq.write(angulo);
  motorDir.write(angulo);
}

/**
 * Move o Robo para Direita. Lado esquerdo para frente
 * e lado direito para traz.
 */
void moverParaDireita(unsigned int velocidade) {
  motorEsq.write(getAnguloMotoresFrente(velocidade));
  motorDir.write(getAnguloMotoresRe(velocidade));
}

/**
 * Move o Robo para Esquerda. Lado esquerdo para frente
 * e lado direito para traz.
 */
void moverParaEsquerda(unsigned int velocidade) {
  motorEsq.write(getAnguloMotoresRe(velocidade));
  motorDir.write(getAnguloMotoresFrente(velocidade));
}

/**
 * Rotaciona o robo 360 graus.
 */
void rotacionar360Graus() {
  moverParaDireita(VELOCIDADE_MIN);
}

/**
 * Converte pulso (microssegundos) em centimetros.
 *
 * Existem 29 microssegundos em 1 cm, entao divide-se o valor do pulso por 29.
 * Com isso obtem-se a distancia total (centimetros) percorridos apartir do 
 * disparo do pulso ate atingir um objeto e volta. Entao divide-se a distancia
 * total por 2 para obter a distancia entre sensor e o objeto.
 *
 * @return 
 */
unsigned long sonarPulsoParaCentimetros(unsigned long pulso) {
  return pulso / 29 / 2;
}

/**
 * Checa se um objeto foi detectado pelo sonar.
 *
 * @return boolean indicando se um objeto foi detectado pelo sonar
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

/**
 * Realiza busca pelo cone. Rotaciona no eixo (360 graus)
 * ate encontrar o alvo ou ate completar os 360 graus.
 *
 * @return boolean indicando se o alvo foi encontrado
 */
boolean rotacionarAteDetectarCone() {
  rotacionar360Graus();
  
  // rotaciona ate encontrar o alvo, ou ate terminar os 360 graus
  while (true) {

    if (isObjetoDetectadoViaSonar()) {
      pararMotores();
      return true;
    }
      
    // TODO: if Robo ja rotacionou completamente.
    // pararMotores();
    // return false;
  }

  return false;
}

/**
 * Obtem a distancia em centimetros entre o Robo e o clone.
 */
unsigned int getDistanciaDoCone() {
  int pulse = pulseIn(SONAR_PIN, HIGH);
  return sonarPulsoParaCentimetros(pulse);
}

/**
 * Se aproxima do cone.
 */
boolean andarParaCone() {
  while (true) {
    // proximo o bastante
    if (getDistanciaDoCone() <= 35)
      return true;

    // perdeu o cone do sonar
    if (getDistanciaDoCone() == 0)
      return false;
  }
}

boolean encontrarCone() {
  if (rotacionarAteDetectarCone()) {
    if (andarParaCone()) {
      // chegou no cone
      // TODO: checar tablado branco?
      return true;
    } else {
      // perdeu o cone
      encontrarCone();
    }
  } else {
    // destino atingido via coordenadas e rotacionou,
    // mas nao encontrou o cone. Protocolo alternatino.
  }
}

/**
 * Configuracao geral do Robo.
 */
void setup() {
  setupMotores();
  setupSonar();
}

/**
 * Loop principal do Robo.
 */
void loop() {
  encontrarCone();
}
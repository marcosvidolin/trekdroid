/**
 * Programa usado no Arduino UNO para controle do Robo de Trekking.
 * @author Marcos Vidolin
 * @since 04/05/2013
 */


#include <Servo.h>


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
  motorEsq.attach(2);
  motorDir.attach(3);
  pararMotores();
}

/**
 * Obtem o angulo dos motores de acordo com a 
 * velocidade passada por parametro.
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
 * Configuracao geral do Robo.
 */
void setup() {
  setupMotores();
}

/**
 * Loop principal do Robo.
 */
void loop() {
  rotacionar360Graus();
}
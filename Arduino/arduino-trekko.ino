/**
 * Programa usado no Arduino UNO para controle do Robo de Trekking.
 * @author Marcos Vidolin
 * @since 04/05/2013
 */

#include <Servo.h>
#include <SPI.h>
#include <Ethernet.h>


//
// Constantes referentes aos pinos utilizados no Arduino.
const unsigned int SONAR_PIN          = 2;
const unsigned int MOTOR_ESQUERDO_PIN = 5;
const unsigned int MOTOR_DIREITO_PIN  = 6;

//
// Constantes referentes aos motores.
const unsigned int VELOCIDADE_MIN = 1;
const unsigned int VELOCIDADE_MAX = 2;
const unsigned int ANGULO_MOTOR_PARADO = 95;
const unsigned int ANGULO_MIN_FRENTE = 110;
const unsigned int ANGULO_MAX_FRENTE = 120;
const unsigned int ANGULO_MIN_RE = 80;
const unsigned int ANGULO_MAX_RE = 70;

//
// Constantes referentes ao sonar (distancia do cone).
const unsigned int DISTANCIA_CONE_ENCONTRADO_CM = 20;
const unsigned int DISTANCIA_CONE_FORA_RADAR_CM = 100;

//
// Constantes referentes aos commandos recebidos do Android.
const unsigned char COMANDO_PARAR_MOTORES  = '0';
const unsigned char COMANDO_ANDAR_FRENTE   = '1';
const unsigned char COMANDO_GIRAR_ESQUESDA = '2';
const unsigned char COMANDO_GIRAR_DIREITA  = '3';
const unsigned char COMANDO_LOCALIZAR_CONE = '4';

//
// Variaveis globais do sistema.
Servo motorEsq;
Servo motorDir;

EthernetServer server = EthernetServer(80);

boolean isConeEncontrado = false;


/**
 * Configura a placa Ethernet.
 */
void setupEthernet() {
  // start the Ethernet connection and the server:
  byte ip[] = {192, 168, 1, 199};
  byte gateway[] = {192, 168, 1, 1}; 
  byte subnet[] = {255, 255, 255, 0};
  byte mac[] = {0xDE, 0xAD, 0xBE, 0xEF, 0xFE, 0xED};
  Ethernet.begin(mac, ip, gateway, subnet);
  server.begin();
}

/**
 * Para os motores do Robo.
 */
void pararMotores() {
  motorEsq.write(ANGULO_MOTOR_PARADO);
  motorDir.write(ANGULO_MOTOR_PARADO);
  delay(15);
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
  moverParaDireita(VELOCIDADE_MAX);
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
  //if (sonarPulsoParaCentimetros(pulse) > 0)
  if (sonarPulsoParaCentimetros(pulse) > 0 && sonarPulsoParaCentimetros(pulse) < DISTANCIA_CONE_FORA_RADAR_CM)
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
  
  // caso ja esteja alinhado com o cone
  if (isObjetoDetectadoViaSonar()) {
    pararMotores();
    return true;
  }
  
  // rotaciona ate encontrar o alvo, ou ate terminar os 360 graus
  rotacionar360Graus();
  while (true) {
    // cone detectado
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
 *
 * @return unsigned int
 */
unsigned int getDistanciaDoCone() {
  int pulse = pulseIn(SONAR_PIN, HIGH);
  return sonarPulsoParaCentimetros(pulse);
}

/**
 * Anda continuamente em direcao ao cone. Retorna true, caso esteja
 * em uma distancia de 20 cm do cone (perto o bastante). Retorna falso
 * caso o cone saia do radar do sonar.
 *
 * @return boolean
 */
boolean moverDirecaoCone() {
  
  // caso ja esteja proximo o suficiente do cone
  if (getDistanciaDoCone() <= DISTANCIA_CONE_ENCONTRADO_CM) {
    pararMotores();
    return true;
  }
  
  moverParaFrente(VELOCIDADE_MAX);
  while (true) {
    // proximo o bastante
    if (getDistanciaDoCone() <= DISTANCIA_CONE_ENCONTRADO_CM) {
      pararMotores();
      return true;
    }

    // perdeu o cone do sonar
    if (getDistanciaDoCone() >= DISTANCIA_CONE_FORA_RADAR_CM) {
      pararMotores();
      return false;
    }
  }
}

/**
 * Processo de busca de cone (Rotacionar/Andar para frente).
 * Retorna true se o cone foi encontrado e PODE SER SINALIZADO.
 * Retorna false caso ja tenha refeito o processo por algumas vezes
 * e o clone nao foi localizado.
 *
 * @return boolean
 */
boolean encontrarCone() {
  if (rotacionarAteDetectarCone()) {
    if (moverDirecaoCone()) {
      // chegou no cone
      // TODO: checar tablado branco?
      return true;
    } else {
      // perdeu o cone
      encontrarCone();
    }
  } else {
    // Nao encontrou o cone. Protocolo alternatino.
    // Volta a consultar GPS?
  }
}

/**
 * Executa os comandos recebidos do Android.
 */
void executarComando(unsigned char command) {
  switch(command) {
    case COMANDO_PARAR_MOTORES:
      pararMotores(); 
      break;
    case COMANDO_ANDAR_FRENTE:
      moverParaFrente(VELOCIDADE_MAX);
      break;
    case COMANDO_GIRAR_ESQUESDA:
      moverParaEsquerda(VELOCIDADE_MAX);
      break;
    case COMANDO_GIRAR_DIREITA:
      moverParaDireita(VELOCIDADE_MAX);
      break;
    case COMANDO_LOCALIZAR_CONE:
      encontrarCone();
      break;
  }
}

/**
 * Inicia o servidor HTTP do Robo.
 */
void initHttpServer() {
  // listen for incoming clients
  EthernetClient client = server.available();
  unsigned int characters = 0;
  if (client) {
    while (client.connected()) {
      if (client.available()) {

        char c = client.read();
        ++characters;
               
        // 123456789
        // GET /1        
        if (characters == 6) {
          Serial.println(c);
          executarComando(c);

          // send a standard http response header
          client.println("HTTP/1.1 200 OK");
          client.println("Content-Type: text/html");
          client.println();
          break;
        }
        
      }
    }
    // give the web browser time to receive the data
    delay(1);
    // close the connection:
    client.stop();
  }
}

/**
 * Configuracao geral do Robo.
 */
void setup() {
  setupEthernet();
  setupMotores();
  setupSonar();
  Serial.begin(9600);
}

/**
 * Loop principal do Robo.
 */
void loop() {
 initHttpServer();
}




/*void loop() {
  if (!isConeEncontrado) {
    encontrarCone();
    isConeEncontrado = true;
  }
}*/
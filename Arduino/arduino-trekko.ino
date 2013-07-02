/**
 * Programa usado no Arduino UNO para controle do Robo de Trekking.
 * @author Marcos Vidolin
 * @since 04/05/2013
 */

#include <Servo.h>
#include <SPI.h>
#include <Ethernet.h>
#include <HMC.h>

//
// Constantes de direcao
const unsigned int DIREITA  = 1;
const unsigned int ESQUERDA = 2;

//
// Constantes referentes aos pinos utilizados no Arduino.
// O magnetometro faz uso dos pinos A4 e A5, para SDA e SCL respectivamente.
const unsigned int PIN_SONAR           = 2;
const unsigned int PIN_MOTOR_DIREITO   = 5;
const unsigned int PIN_MOTOR_ESQUERDO  = 6;
const unsigned int PIN_SINALIZADOR_LED = 7;

//
// Constantes referentes aos motores.
const char VELOCIDADE_MIN = '1';
const char VELOCIDADE_MED = '2';
const char VELOCIDADE_MAX = '3';

const unsigned int ANGULO_MOTOR_PARADO = 95;

const unsigned int ANGULO_MIN_FRENTE = 110;
const unsigned int ANGULO_MED_FRENTE = 115;
const unsigned int ANGULO_MAX_FRENTE = 120;

const unsigned int ANGULO_MIN_RE = 80;
const unsigned int ANGULO_MED_RE = 70;
const unsigned int ANGULO_MAX_RE = 60;

//
// Constantes referentes ao sonar (distancia do cone).
const unsigned long DISTANCIA_CONE_ENCONTRADO_CM = 25;
const unsigned long DISTANCIA_CONE_FORA_RADAR_CM = 400;

//
// Constantes referentes aos quadrantes.
const unsigned int QUADRANTE_1_INDICE = 1;
const unsigned int QUADRANTE_2_INDICE = 2;
const unsigned int QUADRANTE_3_INDICE = 3;
const unsigned int QUADRANTE_4_INDICE = 4;

const unsigned int QUADRANTE_1_GRAU_INICIO = 0;
const unsigned int QUADRANTE_2_GRAU_INICIO = 90;
const unsigned int QUADRANTE_3_GRAU_INICIO = 180;
const unsigned int QUADRANTE_4_GRAU_INICIO = 270;

//
// Variaveis globais do sistema.
Servo motorDir;
Servo motorEsq;

EthernetServer server = EthernetServer(80);


/**
 * Para os motores do Robo.
 */
void pararMotores() {
  motorDir.write(ANGULO_MOTOR_PARADO);
  motorEsq.write(ANGULO_MOTOR_PARADO);
  delay(15);
}

/**
 * Configura os motores do Robo.
 */
void setupMotores() {
  motorDir.attach(PIN_MOTOR_DIREITO);
  motorEsq.attach(PIN_MOTOR_ESQUERDO);
  pararMotores();
}

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
 * Configura o sonar.
 */
void setupSonar() {
  pinMode(PIN_SONAR, INPUT);
}

/**
 * Configura o magnetometro.
 * Faz uso dos pinos A4 e A5, para SDA e SCL respectivamente.
 */
void setupMagnetometro() {
  // The HMC5843 needs 5ms before it will communicate
  delay(5);
  HMC.init();
}

/**
 * Configura o LED sinalizador.
 */
void setupSinalizadorLED() {
  pinMode(PIN_SINALIZADOR_LED, OUTPUT);
}

/**
 * Funcao executada apenas uma vez na inicializacao.
 * Aguarda (delay) em segundos antes de iniciar o trekking.
 * 
 * @param sec segundos para ser aguardado para inicializar o trekking
 */
boolean isStarted = false;
void aguardePrimeiraVez(int sec) {
  if (!isStarted) {
    delay(sec * 1000);
    isStarted = true;
  }
}

/**
 * Convert a String to float value.
 * 
 * @param text
 *      - the String
 *
 * @return float value
 *
 */
float stringToFloat(String text) {
  char carray[text.length() + 1];
  text.toCharArray(carray, sizeof(carray));
  return atof(carray);
}

/**
 * Obtem o angulo dos motores de acordo com a 
 * velocidade passada por parametro.
 *
 * @return int
 */
int getAnguloMotoresFrente(char velocidade) {
  if (VELOCIDADE_MIN == velocidade)
    return ANGULO_MIN_FRENTE;
  if (VELOCIDADE_MED == velocidade)
    return ANGULO_MED_FRENTE;
    
  return ANGULO_MAX_FRENTE;
}

/**
 * Obtem o angulo dos motores de acordo com a 
 * velocidade passada por parametro.
 *
 * @return unsigned int
 */
unsigned int getAnguloMotoresRe(char velocidade) {
  if (VELOCIDADE_MIN == velocidade)
    return ANGULO_MIN_RE;
  if (VELOCIDADE_MED == velocidade)
    return ANGULO_MED_RE;
    
  return ANGULO_MAX_RE;
}

/**
 * Move o Robo para frente.
 */
void moverParaFrente(char velocidade) {
  int angulo = getAnguloMotoresFrente(velocidade);
  motorDir.write(angulo);
  motorEsq.write(angulo);
}

/**
 * Move o Robo para frente.
 */
void moverParaTraz(char velocidade) {
  unsigned int angulo = getAnguloMotoresRe(velocidade);
  motorDir.write(angulo);
  motorEsq.write(angulo);
}

/**
 * Move o Robo para Direita. Lado esquerdo para frente
 * e lado direito para traz.
 */
void moverParaEsquerda(char velocidade) {
  motorDir.write(getAnguloMotoresFrente(velocidade));
  motorEsq.write(getAnguloMotoresRe(velocidade));
}

/**
 * Move o Robo para Esquerda. Lado esquerdo para frente
 * e lado direito para traz.
 */
void moverParaDireita(char velocidade) {
  motorDir.write(getAnguloMotoresRe(velocidade));
  motorEsq.write(getAnguloMotoresFrente(velocidade));
}

/**
 * Rotaciona o robo 360 graus.
 */
void rotacionar360Graus() {
    // velocidade especifica, rotaciona para a direita
    motorDir.write(112);
    motorEsq.write(78);
}

/**
 * Converte pulso (microssegundos) em centimetros.
 *
 * Existem 29 microssegundos em 1 cm, entao divide-se o valor do pulso por 29.
 * Com isso obtem-se a distancia total (centimetros) percorridos apartir do 
 * disparo do pulso ate atingir um objeto e volta. Entao, divide-se a distancia
 * total por 2 para obter a distancia entre sensor e o objeto.
 *
 * @return long
 */
long sonarPulsoParaCentimetros(long pulso) {
  return pulso / 29 / 2;
}

/**
 * Checa se um objeto foi detectado pelo sonar.
 *
 * @return boolean indicando se um objeto foi detectado pelo sonar
 */
boolean isObjetoDetectadoViaSonar() {
  long pulse;
  //delay(250);
  delay(150);
  
  //Used to read in the pulse that is being sent by the MaxSonar device.
  //Pulse Width representation with a scale factor of 147 uS per Inch.
  pulse = pulseIn(PIN_SONAR, HIGH); //147uS per inch 
  long distancia_cm = sonarPulsoParaCentimetros(pulse);
  if (distancia_cm > 0 && distancia_cm < DISTANCIA_CONE_FORA_RADAR_CM)
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
 * @return int
 */
int getDistanciaDoCone() {
  int pulse = pulseIn(PIN_SONAR, HIGH);
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
  
  moverParaFrente(VELOCIDADE_MED);
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
 * Obtem a posicao do Robo em graus (0-360) em relacao ao norte.
 *
 * @return float
 */
float obterGrausCorrente() {
  int x,y,z;

  // There will be new values every 100ms
  delay(100);
  HMC.getValues(&x,&y,&z);

  // Calculate heading when the magnetometer is level, then correct for signs of axis.
  float heading = atan2(y, x);
   
  // Correct for when signs are reversed.
  if(heading < 0)
    heading += 2 * PI;
   
  // Convert radians to degrees for readability.
  return heading * 180 / M_PI;
}

/**
 * Obtem a diferenca entre os graus informados por parametro,
 *
 * @param g1 float
 * @param g2 float
 * @return float
 */
float obterDiferencaEntreGraus(float g1, float g2) {
  float diff = g1 - g2;
  if (diff < 0)
    return diff * -1;
  return diff;
}

/**
 * Rotaciona o Robo para uma determinada direcao de forma continua,
 * ate que o Robo atinja o grau informado.
 *
 * @param graus - grau destino
 * @param direcao - direcao no qual o Robo ira rotacionar para atingir o grau destino
 */
void rotacionarPara(float graus, unsigned int direcao) {
  unsigned int grausTolerancia = 3;
  
  pararMotores();
  if (direcao == DIREITA)
    moverParaDireita(VELOCIDADE_MIN);
  else
    moverParaEsquerda(VELOCIDADE_MIN);

  while(true) {
    if (obterDiferencaEntreGraus(graus, obterGrausCorrente()) <= grausTolerancia) {
      pararMotores();
      Serial.println("Robo alinhado!!");
      break;
    }
  }
}

/**
 * Obtem o quadrante de um determinado grau. O retorno é equivalente aos
 * indeces definidos nas constantes.
 * 
 * @param graus - graus
 * @return index do quadrante
 */
unsigned int getQuadrante(float graus) {
  if (graus >= QUADRANTE_1_GRAU_INICIO && graus < QUADRANTE_2_GRAU_INICIO)
    return QUADRANTE_1_INDICE;

  if (graus >= QUADRANTE_2_GRAU_INICIO && graus < QUADRANTE_3_GRAU_INICIO)
    return QUADRANTE_2_INDICE;

  if (graus >= QUADRANTE_3_GRAU_INICIO && graus < QUADRANTE_4_GRAU_INICIO)
    return QUADRANTE_3_INDICE;

  return QUADRANTE_4_INDICE;
}

/**
 * Obtem a direcao/sentido (DIREITA ou ESQUERDA) mais curto de um grau de origem 
 * para um grau de destino.
 *
 * @param gOrigem - grau de origem
 * @param gDestino - grau de destino
 * @return unsigned int - direcao ou sentido mais curto para o destino
 */
unsigned int getDirecaoEntreQuadrantes(float gOrigem, float gDestino) {

  int quadOrigem = getQuadrante(gOrigem);
  int quadDestino = getQuadrante(gDestino);

  // se origem e destino estiverem no mesmo quadrante
  if (quadOrigem == quadDestino) {
    if ((gOrigem - gDestino) > 0)
      return ESQUERDA;
    else 
      return DIREITA;
  }

  if ((quadOrigem - quadDestino) < 0)
      return DIREITA;

  return ESQUERDA;
}

/**
 * Roraciona o Robo para o grau destino, escolhendo o direcao/sentido 
 * mais curto.
 */
void rotacionarParaCaminhoMaisCurto(float graus) {
  pararMotores();
  rotacionarPara(graus, getDirecaoEntreQuadrantes(obterGrausCorrente(), graus));
}

/**
 * Metodo usado para indicar quando um alvo e encontrado. 
 */
void sinalizarAlvoEncontrado() {
  pararMotores();
  digitalWrite(PIN_SINALIZADOR_LED, HIGH);
  delay(3000);
}

/**
 * Obtem a distancia da String passada na requisicao HTTP.
 * 
 * @param valor - String no formato 111;222 sendo que 111 corresponde
 * a distancia e 222 ao grau
 * @return float
 */
float obterDistanciaDoRequest(String valor) {
  int semicolun = valor.indexOf(';');
  return stringToFloat(valor.substring(0, semicolun));
}

/**
 * Obtem o grau da String passada na requisicao HTTP.
 * 
 * @param valor - String no formato 111;222 sendo que 111 corresponde
 * a distancia e 222 ao grau
 * @return float
 */
float obterGrausDoRequest(String valor) {
  int semicolun = valor.indexOf(';');
  return stringToFloat(valor.substring(semicolun + 1));
}

/**
 * Funcao para logar os valores obtidos do request.
 */
void logger(float distancia, float graus, String desc) {
  Serial.println("");
  Serial.print("Distancia: ");
  Serial.print(distancia);
  Serial.print(" Graus: ");
  Serial.print(graus);
  Serial.print(" Desc: ");
  Serial.print(desc);
}

/**
 * Realiza o operacao de trekking dado uma distancia e os graus para
 * o destino.
 *
 * @param distancia - distancia a percorrer para chegar ao destino
 * @param graus - o grau de alinhamento do destino
 * @return char - caractere '1' indicando cone encontrado ('1')
 *                 ou '0' cone nao encontrado
 */
char trekking(float distancia, float graus) {

  // inicia busca do cone usando o Sonar
  if (distancia <= 1) {
    while(true) {
      logger(distancia, graus, "buscando o cone via sonar...");
      if (encontrarCone()) {
        pararMotores();
        sinalizarAlvoEncontrado();
        logger(distancia, graus, "cone encontrado!!!");
        return '1';
      }
    }
  }
  
  // alinha em direcao ao cone
  if (obterDiferencaEntreGraus(graus, obterGrausCorrente()) >= 15) {
    rotacionarParaCaminhoMaisCurto(graus);
    logger(distancia, graus, "alinhando robo em direcao cone...");
    return '0';
  }

  // despois de alinhado anda em direcao ao cone
  if (distancia > 1) {
    if (distancia > 2)
      moverParaFrente(VELOCIDADE_MAX);
    else
      moverParaFrente(VELOCIDADE_MED);
    logger(distancia, graus, "andando em direcao cone...");
    return '0';
  }

  return '0';
}

/**
 * Inicia o servidor HTTP do Robo.
 */
void initHttpValueListener() {
  // listen for incoming clients
  EthernetClient client = server.available();
  int characters = 0;
  String valor = "";
  if (client) {
    while (client.connected()) {
      if (client.available()) {

        char c = client.read();
        ++characters;

        // 123456789012
        // GET /999;999
        if (characters >= 6 && characters < 13) {
          valor += c;
        } else if (characters >= 13) {

          aguardePrimeiraVez(5);
          // Serial.println(valor);
          char resp = trekking(obterDistanciaDoRequest(valor), obterGrausDoRequest(valor));

          // send a standard http response header
          client.println("HTTP/1.1 200 OK");
          client.println("Content-Type: text/html");
          client.println();
          client.println(resp);

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
  setupMagnetometro();
  setupSinalizadorLED();
  Serial.begin(9600);
}

/**
 * Loop principal do Robo.
 */
void loop() {
  initHttpValueListener();
}

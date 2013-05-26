#include <SPI.h>
#include <Ethernet.h>

const unsigned int SONAR_PIN = 2; 

// Enter a MAC address and IP address for your controller below.
// The IP address will be dependent on your local network:
byte mac[] = { 0xDE, 0xAD, 0xBE, 0xEF, 0xFE, 0xED };
IPAddress ip(192,168,1, 177);

// Initialize the Ethernet server library
// with the IP address and port you want to use 
// (port 80 is default for HTTP):
EthernetServer server(80);

/**
 * Configura o sonar.
 */
void setupSonar() {
  setupSonar();
  pinMode(SONAR_PIN, INPUT);
}

void setup() {
  // start the Ethernet connection and the server:
  Ethernet.begin(mac, ip);
  server.begin();
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

void loop() {
  // listen for incoming clients
  EthernetClient client = server.available();
  if (client) {
    // an http request ends with a blank line
    boolean currentLineIsBlank = true;
    while (client.connected()) {
      if (client.available()) {
        char c = client.read();
        // if you've gotten to the end of the line (received a newline
        // character) and the line is blank, the http request has ended,
        // so you can send a reply
        if (c == '\n' && currentLineIsBlank) {
          // send a standard http response header
          client.println("HTTP/1.1 200 OK");
          client.println("Content-Type: text/html");
          client.println();

          // output the value of each analog input pin
          for (int analogChannel = 0; analogChannel < 6; analogChannel++) {
            client.print(sonarPulsoParaCentimetros(pulseIn(SONAR_PIN, HIGH)));
            client.println("<br />");
          }
          break;
        }
        if (c == '\n') {
          // you're starting a new line
          currentLineIsBlank = true;
        } 
        else if (c != '\r') {
          // you've gotten a character on the current line
          currentLineIsBlank = false;
        }
      }
    }
    // give the web browser time to receive the data
    delay(1);
    // close the connection:
    client.stop();
  }
}

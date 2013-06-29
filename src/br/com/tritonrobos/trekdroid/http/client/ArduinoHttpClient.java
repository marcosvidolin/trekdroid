package br.com.tritonrobos.trekdroid.http.client;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.util.Log;
import br.com.tritonrobos.trekdroid.model.ComandoArduino;
import br.com.tritonrobos.trekdroid.model.ComandoArduino.Comando;
import br.com.tritonrobos.trekdroid.model.ComandoArduino.Velocidade;
import br.com.tritonrobos.trekdroid.util.CoordenadaUtil;

/**
 * Cliente HTTP do Arduino, responsavel por se comunicar com o Robo atraves de
 * comandos.
 * 
 * @author vidolin
 * @sinze 25/05/2013
 */
public class ArduinoHttpClient {

	private final static String LOG_TAG = "ArduinoHttpClient";

	/**
	 * Endereco do servidor (Arduino).
	 */
	private final static String ARDUINO_SERVER_URL = "http://192.168.1.199/";

	/**
	 * Obtem o endereco com o comando a ser passado para o Robo.
	 * 
	 * @param command
	 *            {@link String}
	 * @return {@link String}
	 */
	private static String getEndereco(final String command) {
		return ARDUINO_SERVER_URL + command;
	}

	/**
	 * Envia comandos para o Arduino via HTTP GEt.
	 * 
	 * @param command
	 *            {@link String}
	 * @return {@link String} response
	 */
	private String sendCommand(final String command) {
		String responseString = null;
		HttpClient client = new DefaultHttpClient();
		HttpGet method = new HttpGet(getEndereco(command));
		try {
			HttpResponse response = client.execute(method);
			HttpEntity entity = response.getEntity();
			responseString = EntityUtils.toString(entity);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return responseString;
	}

	/**
	 * Envia um comando para que os motores do robo sejam parados.
	 * 
	 * @return {@link String} resposta
	 */
	public String pararMotores() {
		ComandoArduino comando = new ComandoArduino.Builder().comando(
				Comando.PARAR_MOTORES).build();
		Log.i(LOG_TAG, "pararMotores()");
		return this.sendCommand(comando.getComandoComoTexto());
	}

	/**
	 * Envia um comando ao Robo para realizar movimento para frente.
	 * 
	 * @param velocidade
	 *            {@link Velocidade}
	 * @return {@link String} resposta
	 */
	public String moverParaFrente(final Velocidade velocidade) {
		ComandoArduino comando = new ComandoArduino.Builder()
				.comando(Comando.ANDAR_FRENTE).velocidade(velocidade).build();
		Log.i(LOG_TAG, "moverParaFrente()");
		return this.sendCommand(comando.getComandoComoTexto());
	}

	/**
	 * Envia um comando ao Robo para realizar movimento para traz.
	 * 
	 * @param velocidade
	 *            {@link Velocidade}
	 * @return {@link String} resposta
	 */
	public String moverParaTraz(final Velocidade velocidade) {
		ComandoArduino comando = new ComandoArduino.Builder()
				.comando(Comando.ANDAR_TRAZ).velocidade(velocidade).build();
		Log.i(LOG_TAG, "moverParaTraz()");
		return this.sendCommand(comando.getComandoComoTexto());
	}

	/**
	 * Envia um comando para o Robo rotacionar para a direita na velocidade
	 * informada.
	 * 
	 * @param velocidade
	 *            {@link Velocidade}
	 * @return {@link String} resposta
	 */
	public String rotacionarParaDireita(final Velocidade velocidade) {
		ComandoArduino comando = new ComandoArduino.Builder()
				.comando(Comando.GIRAR_DIREITA).velocidade(velocidade).build();
		Log.i(LOG_TAG, "moverParaDireita()");
		return this.sendCommand(comando.getComandoComoTexto());
	}

	/**
	 * Envia um comando para o Robo rotacionar para a esquerda na velocidade
	 * informada.
	 * 
	 * @param velocidade
	 *            {@link Velocidade}
	 * @return {@link String} resposta
	 */
	public String rotacionarParaEsquerda(final Velocidade velocidade) {
		ComandoArduino comando = new ComandoArduino.Builder()
				.comando(Comando.GIRAR_ESQUESDA).velocidade(velocidade).build();
		Log.i(LOG_TAG, "moverParaEsquerda()");
		return this.sendCommand(comando.getComandoComoTexto());
	}

	/**
	 * Obtem a grau corrente referente a posição do Robo em relação ao norte.
	 * 
	 * @return {@link String} resposta
	 */
	public Double obterGrauCorrente() {
		ComandoArduino comando = new ComandoArduino.Builder().comando(
				Comando.OBTER_GRAUS).build();
		Log.i(LOG_TAG, "obterGrauCorrente()");
		return Double.valueOf(this.sendCommand(comando.getComandoComoTexto()));
	}

	/**
	 * Envia um comando para o Arduino localizar o cone. Retorna true caso tenha
	 * encontrado o cone.
	 * 
	 * @return boolean
	 */
	public boolean localizarCone() {
		ComandoArduino comando = new ComandoArduino.Builder().comando(
				Comando.LOCALIZAR_CONE).build();
		this.sendCommand(comando.getComandoComoTexto());

		Log.i(LOG_TAG, "localizarCone()");

		// TODO: tratar retorno da requisicao para saber se o cone foi
		// encontrado
		return true;
	}

	/**
	 * Envia um comando para o Rovo se alinhar de acordo com o grau informado
	 * (formatado).
	 * 
	 * @param graus
	 *            {@link String}
	 * @return {@link String} resposta
	 */
	public String rotacionarPara(final String graus) {
		ComandoArduino comando = new ComandoArduino.Builder()
				.comando(Comando.ROTACIONAR_PARA).valor(graus).build();
		Log.i(LOG_TAG, "rotacionarPara()");
		return this.sendCommand(comando.getComandoComoTexto());
	}

	/**
	 * Envia um comando para o Rovo se alinhar de acordo com o grau informado.
	 * 
	 * @param graus
	 *            {@link Double}
	 * @return {@link String} resposta
	 */
	public String rotacionarPara(final Double graus) {
		return this.rotacionarPara(CoordenadaUtil.getRolamentoFormatado(graus));
	}

}

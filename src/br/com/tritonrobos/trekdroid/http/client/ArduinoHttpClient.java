package br.com.tritonrobos.trekdroid.http.client;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

/**
 * 
 * @author vidolin
 * @sinze 25/05/2013
 */
public class ArduinoHttpClient {

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
	public static String sendCommand(final String command) {
		String responseString = null;
		try {
			HttpGet get = new HttpGet(getEndereco(command));
			HttpClient client = new DefaultHttpClient();
			HttpResponse response = client.execute(get);
			HttpEntity entity = response.getEntity();
			responseString = EntityUtils.toString(entity);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return responseString;
	}

}

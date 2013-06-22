package br.com.tritonrobos.trekdroid.service;

import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;
import br.com.tritonrobos.trekdroid.http.client.ArduinoHttpClient;
import br.com.tritonrobos.trekdroid.model.ComandoArduino.Velocidade;
import br.com.tritonrobos.trekdroid.model.Coordenada;

/**
 * 
 * @author Marcos Vidolin
 * @since 06/06/2013
 * 
 */
public class TrekkingService extends Service implements Runnable {

	private Coordenada coordenadaCorrente = new Coordenada();

	ArduinoHttpClient robo = new ArduinoHttpClient();

	private Intent intent;

	/**
	 * Envia o comando para o arduino.
	 */
	/*
	 * private void converterButtonAction() { new AsyncTask<String, Void,
	 * String>() {
	 * 
	 * @Override protected String doInBackground(String... params) { return
	 * ArduinoHttpClient.sendCommand("1"); }
	 * 
	 * @Override protected void onPostExecute(String result) {
	 * System.out.println(result); }
	 * 
	 * @Override protected void onPreExecute() {
	 * 
	 * }
	 * 
	 * }.execute(new String[] {}); }
	 */

	/**
	 * Método que faz a leitura de fato dos valores recebidos do GPS.
	 */
	private void acionarGPS() {

		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		LocationListener lListener = new LocationListener() {
			public void onLocationChanged(Location location) {
				coordenadaCorrente.setLocation(location);
			}

			public void onStatusChanged(String provider, int status,
					Bundle extras) {
			}

			public void onProviderEnabled(String provider) {
			}

			public void onProviderDisabled(String provider) {
			}
		};
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
				0, lListener);
	}

	/**
	 * Obtem uma lista dos destinos (metas) que o Robo deve atingir.
	 * 
	 * @return {@link List}
	 */
	private List<Coordenada> getDestinos() {
		List<Coordenada> destinos = new ArrayList<Coordenada>();

		destinos.add(new Coordenada(this.intent.getDoubleArrayExtra("d1")[0],
				this.intent.getDoubleArrayExtra("d1")[1]));

		destinos.add(new Coordenada(this.intent.getDoubleArrayExtra("d2")[0],
				this.intent.getDoubleArrayExtra("d2")[1]));

		destinos.add(new Coordenada(this.intent.getDoubleArrayExtra("d3")[0],
				this.intent.getDoubleArrayExtra("d3")[1]));

		return destinos;
	}

	/**
	 * Alinha o Robo em direcao ao destino informado.
	 * 
	 * @param destino
	 *            {@link Coordenada}
	 */
	private void alinharRobo(final Coordenada destino) {
		Double rolamento = this.coordenadaCorrente.rolamentoPara(destino);
		robo.rotacionarPara(rolamento);
	}

	/**
	 * Movimenta o Robo em direcao ao destino. O Robo se movimento com
	 * velocidade maxima, caso esteja em 3m ou menos de distancia do destino,
	 * reduz a velocidade para a velocidade minima.
	 * 
	 * @param destino
	 *            {@link Coordenada}
	 */
	private void moverDirecaoCone(final Coordenada destino) {

		if (this.coordenadaCorrente.rolamentoPara(destino) >= 90)
			this.alinharRobo(destino);

		if (this.coordenadaCorrente.distanciaPara(destino) > 3)
			this.robo.moverParaFrente(Velocidade.MAXIMA);
	}

	/**
	 * Inicia a execucao do trekking.
	 */
	public void run() {
		List<Coordenada> destinos = this.getDestinos();

		// percorre todos os destinos
		for (int i = 0; i < destinos.size(); i++) {
			Coordenada destino = destinos.get(i);
			boolean isConeEncontrado = false;

			// logica para encontrar cone
			this.alinharRobo(destino);
			while (!isConeEncontrado) {
				if (this.coordenadaCorrente.isNotNull()) {
					this.moverDirecaoCone(destino);
					if (this.robo.localizarCone())
						destinos.remove(i);
				}
			}

		}
		Toast.makeText(this, "Fora do laco", Toast.LENGTH_LONG).show();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// return Service.START_NOT_STICKY;

		// We want this service to continue running until it is explicitly
		// stopped, so return sticky.
		Toast.makeText(this, "onStartCommand", Toast.LENGTH_LONG).show();
		// ArduinoHttpClient.sendCommand("1");
		acionarGPS();
		this.intent = intent;
		this.run();
		// return Service.START_STICKY;
		return Service.START_NOT_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "onBind", Toast.LENGTH_LONG).show();
		return null;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		Toast.makeText(this, "onStart", Toast.LENGTH_LONG).show();
	}

	@Override
	public void onDestroy() {
		Toast.makeText(this, "onDestroy", Toast.LENGTH_LONG).show();
	}

}

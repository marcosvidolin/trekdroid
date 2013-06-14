package br.com.tritonrobos.trekdroid.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;
import br.com.tritonrobos.trekdroid.http.client.ArduinoHttpClient;
import br.com.tritonrobos.trekdroid.model.Coordinate;

/**
 * 
 * @author Marcos Vidolin
 * @since 06/06/2013
 * 
 */
public class TrekkingService extends Service implements Runnable {

	private Coordinate coordenadaCorrente = new Coordinate();
	
	/**
	 * Envia o comando para o arduino.
	 */
	/*private void converterButtonAction() {
		new AsyncTask<String, Void, String>() {
			@Override
			protected String doInBackground(String... params) {
				return ArduinoHttpClient.sendCommand("1");
			}

			@Override
			protected void onPostExecute(String result) {
				System.out.println(result);
			}

			@Override
			protected void onPreExecute() {

			}

		}.execute(new String[] {});
	}*/

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
	 * 
	 */
	public void run() {

	}	
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// return Service.START_NOT_STICKY;

		// We want this service to continue running until it is explicitly
		// stopped, so return sticky.
		Toast.makeText(this, "onStartCommand", Toast.LENGTH_LONG).show();
		ArduinoHttpClient.sendCommand("1");
		acionarGPS();
		return Service.START_STICKY;
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

package br.com.tritonrobos.trekdroid;

import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import br.com.tritonrobos.trekdroid.http.client.ArduinoHttpClient;
import br.com.tritonrobos.trekdroid.model.Coordenada;

/**
 * Activity principal para controle da tela de captura de coordenadas.
 * 
 * @author vidolin
 * @since 25/04/2013
 */
public class MainActivity extends Activity implements LocationListener {

	private static final String CONE_ENCONTRADO = "1";

	private EditText edLatitude;
	private EditText edLongitude;
	private EditText edCoord1;
	private EditText edCoord2;
	private EditText edCoord3;

	private Button btSalvarLocalizacao;
	private Button btIniciarTrekking;

	private List<Coordenada> destinos = new ArrayList<Coordenada>();

	private Coordenada coordenadaCapturada;
	private Coordenada coordenadaCorrente = new Coordenada();

	private boolean isTrekkingIniciado = false;

	@TargetApi(9)
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);

		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				SensorManager.SENSOR_DELAY_FASTEST, 0, this);

		setupElements();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	/**
	 * Atualiza a coordenada corrente com os valores de latitude e longitude dos
	 * campos da tela.
	 */
	private Coordenada getCoordenadaCapturada() {
		coordenadaCapturada = new Coordenada();
		coordenadaCapturada.setLatitude(Double.valueOf(edLatitude.getText()
				.toString()));
		coordenadaCapturada.setLongitude(Double.valueOf(edLongitude.getText()
				.toString()));
		return coordenadaCapturada;
	}

	/**
	 * Método usado para importar os elementos da classe R.
	 */
	private void setupElements() {

		edLatitude = (EditText) findViewById(R.id.edLatitude);
		edLongitude = (EditText) findViewById(R.id.edLongitude);
		edCoord1 = (EditText) findViewById(R.id.edCoord1);
		edCoord2 = (EditText) findViewById(R.id.edCoord2);
		edCoord3 = (EditText) findViewById(R.id.edCoord3);

		btSalvarLocalizacao = (Button) findViewById(R.id.btSalvarLocalizacao);
		btSalvarLocalizacao.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				addLocalizacaoDestino(getCoordenadaCapturada());
			}
		});

		btIniciarTrekking = (Button) findViewById(R.id.btIniciarTrekking);
		btIniciarTrekking.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				isTrekkingIniciado = true;
			}
		});
	}

	/**
	 * Adiciona uma coordenada na lista de coordenadas a serem localizadas pelo
	 * Robo.
	 * 
	 * @param destino
	 *            {@link Coordenada}
	 */
	private void addLocalizacaoDestino(final Coordenada destino) {
		this.destinos.add(destino);
		this.refreshCamposDestino();
	}

	/**
	 * Popula os campos de destino (sequencialmente) com as coordenadas passadas
	 * por parametro.
	 * 
	 * @param destino
	 *            the {@link Coordenada}
	 */
	private void refreshCamposDestino() {
		if (this.destinos.isEmpty())
			return;

		Coordenada coord = getCoordenadaCapturada();

		String str = coord.getLatitude().toString() + ", "
				+ coord.getLongitude().toString();

		// atualiza o primeiro campo
		if (this.destinos.size() == 1) {
			edCoord1.setText(str);
			return;
		}

		// atualiza o segundo campo
		if (this.destinos.size() == 2) {
			edCoord2.setText(str);
			return;
		}

		// atualiza o terceiro campo
		if (this.destinos.size() == 3) {
			edCoord3.setText(str);
			return;
		}
	}

	/**
	 * Atualiza os campos de Latitude/Longitude da coordenada capturada.
	 * 
	 * @param destino
	 *            {@link Coordenada} de distino capturada
	 */
	private void updateCamposCoordenadaCopturada(final Coordenada destino) {
		edLatitude.setText(destino.getLatitude().toString());
		edLongitude.setText(destino.getLongitude().toString());
	}

	/**
	 * 
	 */
	public void onLocationChanged(final Location location) {
		this.coordenadaCapturada = new Coordenada(location);
		this.coordenadaCorrente = new Coordenada(location);
		this.updateCamposCoordenadaCopturada(coordenadaCapturada);

		if (isTrekkingIniciado && !destinos.isEmpty()) {
			Coordenada destino = this.destinos.get(0);
			Double distancia = this.coordenadaCorrente.distanciaPara(destino);
			Double graus = this.coordenadaCorrente.rolamentoPara(destino);

			String resp = ArduinoHttpClient.sendValues(distancia, graus);
			if (CONE_ENCONTRADO.equals(resp.trim()))
				this.destinos.remove(0);
		}
	}

	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
	}

	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
	}

}
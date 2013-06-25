package br.com.tritonrobos.trekdroid;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import br.com.tritonrobos.trekdroid.model.Coordenada;

/**
 * Activity principal para controle da tela de captura de coordenadas.
 * 
 * @author vidolin
 * @since 25/04/2013
 */
public class MainActivity extends Activity {

	private EditText edLatitude;
	private EditText edLongitude;
	private EditText edCoord1;
	private EditText edCoord2;
	private EditText edCoord3;

	private Button btAtivarGPS;
	private Button btSalvarLocalizacao;
	private Button btIniciarTrekking;

	private List<Coordenada> destinos = new ArrayList<Coordenada>();

	private Coordenada coordenadaCapturada;
	private Coordenada coordenadaCorrente = new Coordenada();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
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

		btAtivarGPS = (Button) findViewById(R.id.btAtivarGPS);
		btAtivarGPS.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				acionarGPS();
			}
		});

		btSalvarLocalizacao = (Button) findViewById(R.id.btSalvarLocalizacao);
		btSalvarLocalizacao.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				addLocalizacaoDestino(getCoordenadaCapturada());
			}
		});

		btIniciarTrekking = (Button) findViewById(R.id.btIniciarTrekking);
		btIniciarTrekking.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent("TREKKING_SERVICE");
				i.putExtra("d1", destinos.get(0).toArry());
				i.putExtra("d2", destinos.get(1).toArry());
				i.putExtra("d3", destinos.get(2).toArry());
				i.putExtra("cu", coordenadaCorrente.toArry());
				startService(i);
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
	 * Método que faz a leitura de fato dos valores recebidos do GPS.
	 */
	private void acionarGPS() {

		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		LocationListener lListener = new LocationListener() {
			public void onLocationChanged(Location location) {
				coordenadaCapturada = new Coordenada(location);
				coordenadaCorrente.setLocation(location);
				updateCamposCoordenadaCopturada(coordenadaCapturada);
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
	 * Atualiza os campos de Latitude/Longitude da coordenada capturada.
	 * 
	 * @param destino
	 *            {@link Coordenada} de distino capturada
	 */
	private void updateCamposCoordenadaCopturada(final Coordenada destino) {
		edLatitude.setText(destino.getLatitude().toString());
		edLongitude.setText(destino.getLongitude().toString());
	}

}
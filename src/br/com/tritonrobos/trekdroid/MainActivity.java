package br.com.tritonrobos.trekdroid;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import br.com.tritonrobos.trekdroid.model.Coordinate;

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

	private Button btLocalizar;
	private Button btSalvar;
	private Button btIniciarTrekking;

	private List<Coordinate> destinos = new ArrayList<Coordinate>();
	private Coordinate coordenadaCorrente;

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
	private Coordinate getCoordenadaCorrente() {
		coordenadaCorrente = new Coordinate(Double.valueOf(edLatitude.getText()
				.toString()), Double.valueOf(edLongitude.getText().toString()));
		return coordenadaCorrente;
	}

	/**
	 * Método usado para importar os elementos da classe R.
	 */
	public void setupElements() {
		edLatitude = (EditText) findViewById(R.id.edLatitude);
		edLongitude = (EditText) findViewById(R.id.edLongitude);
		edCoord1 = (EditText) findViewById(R.id.edCoord1);
		edCoord2 = (EditText) findViewById(R.id.edCoord2);
		edCoord3 = (EditText) findViewById(R.id.edCoord3);

		btLocalizar = (Button) findViewById(R.id.btLocalizar);
		btLocalizar.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				startGPS();
			}
		});

		btSalvar = (Button) findViewById(R.id.btSalvar);
		btSalvar.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				addDestino(getCoordenadaCorrente());
			}
		});

		btIniciarTrekking = (Button) findViewById(R.id.btIniciarTrekking);
		btIniciarTrekking.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				// TODO: iniciar trekking
			}
		});
	}

	/**
	 * Adiciona uma coordenada na lista de coordenadas a serem localizadas pelo
	 * Robo.
	 * 
	 * @param destino
	 *            {@link Coordinate}
	 */
	private void addDestino(final Coordinate destino) {
		this.destinos.add(destino);
		this.refreshCamposDestino();
	}

	/**
	 * Popula os campos de destino (sequencialmente) com as coordenadas passadas
	 * por parametro.
	 * 
	 * @param destino
	 *            the {@link Coordinate}
	 */
	private void refreshCamposDestino() {
		if (this.destinos.isEmpty())
			return;

		Coordinate coord = getCoordenadaCorrente();

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
	public void startGPS() {

		LocationManager lManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		LocationListener lListener = new LocationListener() {
			public void onLocationChanged(Location locat) {
				coordenadaCorrente = new Coordinate(locat);
				updateInputFieldsView(coordenadaCorrente);
			}

			public void onStatusChanged(String provider, int status,
					Bundle extras) {
			}

			public void onProviderEnabled(String provider) {
			}

			public void onProviderDisabled(String provider) {
			}
		};
		lManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
				lListener);
	}

	// Coordenada origem = new Coordenada(-20.67230, -47.07650);
	// Coordenada destino = new Coordenada(18.5971945, -5.4213818);

	private Location getLocationTest() {
		Location location = new Location("");
		location.setLatitude(-20.67194);
		location.setLongitude(-47.07648);
		return location;
	}

	/**
	 * Atualiza os campos editaveis de Latitude/Longitude.
	 * 
	 * @param destino
	 *            {@link Coordinate} de distino capturada
	 */
	public void updateInputFieldsView(final Coordinate destino) {

		Double latPoint = destino.getLatitude();
		Double lngPoint = destino.getLongitude();

		edLatitude.setText(latPoint.toString());
		edLongitude.setText(lngPoint.toString());
	}
}

package br.com.tritonrobos.trekdroid;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import br.com.tritonrobos.trekdroid.model.Coordinate;

public class MainActivity extends Activity {

	private EditText edLatitude;
	private EditText edLongitude;
	private Button btLocalizar;
	private List<Location> locations = new ArrayList<Location>();

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

	// Método usado para importar os elementos da classe R
	public void setupElements() {
		edLatitude = (EditText) findViewById(R.id.edLatitude);
		edLongitude = (EditText) findViewById(R.id.edLongitude);

		btLocalizar = (Button) findViewById(R.id.btLocalizar);
		btLocalizar.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				startGPS();
			}
		});
	}

	// Método que faz a leitura de fato dos valores recebidos do GPS
	public void startGPS() {
		LocationManager lManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		LocationListener lListener = new LocationListener() {
			public void onLocationChanged(Location locat) {
				updateInputFieldsView(locat);
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
	public void updateInputFieldsView(final Location destino) {

		Double latPoint = destino.getLatitude();
		Double lngPoint = destino.getLongitude();

		edLatitude.setText(latPoint.toString());
		edLongitude.setText(lngPoint.toString());

		float distancia = destino.distanceTo(getLocationTest());
		Log.v("GPS", distancia + " m");

		float bearing = destino.bearingTo(getLocationTest());
		Log.v("GPS", bearing + " bearing");
	}
}

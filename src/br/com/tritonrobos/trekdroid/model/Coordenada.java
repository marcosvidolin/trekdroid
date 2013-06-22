package br.com.tritonrobos.trekdroid.model;

import android.location.Location;
import br.com.tritonrobos.trekdroid.util.CoordinateUtil;

/**
 * Objeto que representa coordenadas geograficas (Latitude/Longitude).
 * 
 * @author vidolin
 * @since 26/04/2013
 * 
 */
public class Coordenada {

	private Double latitude;
	private Double longitude;
	private Location location;

	/**
	 * Construtor padrao.
	 */
	public Coordenada() {
		super();
	}

	/**
	 * Construtor com parametros.
	 * 
	 * @param latitude
	 *            the latitude to set
	 * @param longitude
	 *            the longitude to set
	 */
	public Coordenada(final Double latitude, final Double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}

	/**
	 * Construtor com parametro.
	 * 
	 * @param location
	 *            the {@link Location}
	 */
	public Coordenada(final Location location) {
		this(location.getLatitude(), location.getLongitude());
	}

	/**
	 * Remove os tres ultimos digitos da coordenada informada.
	 * 
	 * @param coordinate
	 *            the coordinate to round
	 * @return Double
	 */
	private Double roundCoordinate(final Double coordinate) {
		String str = coordinate.toString();
		int diff = str.length() - 9;
		if (diff > 0)
			str = str.substring(0, str.length() - diff);
		return Double.valueOf(str);
	}

	/**
	 * @return the latitude
	 */
	public Double getLatitude() {
		return this.roundCoordinate(latitude);
	}

	/**
	 * @param latitude
	 *            the latitude to set
	 */
	public void setLatitude(final Double latitude) {
		this.latitude = latitude;
	}

	/**
	 * @return the longitude
	 */
	public Double getLongitude() {
		return this.roundCoordinate(longitude);
	}

	/**
	 * @param longitude
	 *            the longitude to set
	 */
	public void setLongitude(final Double longitude) {
		this.longitude = longitude;
	}

	/**
	 * @param location
	 *            the location to set
	 */
	public void setLocation(final Location location) {
		this.setLatitude(location.getLatitude());
		this.setLongitude(location.getLongitude());
		this.location = location;
	}

	/**
	 * Obtem a distancia em metros entre a coordenada corrente e a coordenada
	 * informada por parametro.
	 * 
	 * @param destino
	 *            {@link Coordenada}
	 * @return {@link Double}
	 */
	public Double distanciaPara(final Coordenada destino) {
		return CoordinateUtil.getDistancia(this, destino);
	}

	/**
	 * Obtem o rolamento em graus para trassar o caminho mais curto entre a
	 * coordenada corrente e a coordenada passada por parametro.
	 * 
	 * @param destino
	 *            {@link Coordenada}
	 * 
	 * @return {@link Double}
	 */
	public Double rolamentoPara(final Coordenada destino) {
		return CoordinateUtil.getRolamento(this, destino);
	}

	/**
	 * Retorna true caso o objeto tenha seus principais atributos preenchidos.
	 * False caso contrario.
	 * 
	 * @return boolean
	 */
	public boolean isNotNull() {
		if (this.latitude == null || this.longitude == null)
			return false;
		return true;
	}

	/**
	 * Obtem a representacao de um objeto {@link Coordenada} no formato de um
	 * array de double, contendo as coordenadas de latitude e longitude.
	 * 
	 * @return double[]{latitude, longitude}
	 */
	public double[] toArry() {
		double[] a = { this.latitude, this.longitude };
		return a;
	}

	/**
	 * Object.toString() Metod.
	 */
	@Override
	public String toString() {
		return "Coordinate [latitude=" + this.latitude + ", longitude="
				+ this.longitude + "]";
	}

}
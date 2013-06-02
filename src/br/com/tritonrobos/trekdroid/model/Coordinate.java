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
public class Coordinate {

	private Double latitude;
	private Double longitude;
	private Location location;

	/**
	 * Construtor padrao.
	 */
	public Coordinate() {
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
	public Coordinate(final Double latitude, final Double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}

	/**
	 * Construtor com parametro.
	 * 
	 * @param location
	 *            the {@link Location}
	 */
	public Coordinate(final Location location) {
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
	 *            {@link Coordinate}
	 * @return {@link Double}
	 */
	public Double distanciaPara(final Coordinate destino) {
		return CoordinateUtil.getDistancia(this, destino);
	}

	/**
	 * Obtem o rolamento em graus para trassar o caminho mais curto entre a
	 * coordenada corrente e a coordenada passada por parametro.
	 * 
	 * @param destino
	 *            {@link Coordinate}
	 * 
	 * @return {@link Double}
	 */
	public Double rolamentoPara(final Coordinate destino) {
		return CoordinateUtil.getRolamento(this, destino);
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
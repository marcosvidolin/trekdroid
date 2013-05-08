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
	 * @return the latitude
	 */
	public Double getLatitude() {
		return latitude;
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
		return longitude;
	}

	/**
	 * @param longitude
	 *            the longitude to set
	 */
	public void setLongitude(final Double longitude) {
		this.longitude = longitude;
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

}
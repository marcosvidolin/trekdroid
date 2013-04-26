package br.com.tritonrobos.trekdroid.model;

/**
 * Objeto que representa coordenadas geograficas (Latitude/Longitude).
 * 
 * @author vidolin
 * @since 26/04/2013
 * 
 */
public class Coordinate {

	private double latitude;
	private double longitude;

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
	public Coordinate(final double latitude, final double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}

	/**
	 * @return the latitude
	 */
	public double getLatitude() {
		return latitude;
	}

	/**
	 * @param latitude
	 *            the latitude to set
	 */
	public void setLatitude(final double latitude) {
		this.latitude = latitude;
	}

	/**
	 * @return the longitude
	 */
	public double getLongitude() {
		return longitude;
	}

	/**
	 * @param longitude
	 *            the longitude to set
	 */
	public void setLongitude(final double longitude) {
		this.longitude = longitude;
	}

}
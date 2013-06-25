package br.com.tritonrobos.trekdroid.util;

import br.com.tritonrobos.trekdroid.model.Coordenada;

/**
 * Classe utilitaria para realizar calculos diversos sobre coordenadas
 * geograficas.
 * 
 * @author vidolin
 * @since 25/04/2013
 * 
 */
public class CoordenadaUtil {

	// R is earth’s radius (mean radius = 6,371km)
	private static final double R = 6371; // 6.372,795477598

	/**
	 * Obtem a diferenca entre a Latitude das Coordinates de origem e destino.
	 * 
	 * @param origem
	 *            Coordinate de origem
	 * @param destino
	 *            Coordinate de destino
	 * @return double
	 */
	private static double getDiffLatitude(final Coordenada origem,
			final Coordenada destino) {
		return Math.toRadians(destino.getLatitude())
				- Math.toRadians(origem.getLatitude());
	}

	/**
	 * Obtem a diferenca entre a Longitude das Coordinates de origem e destino.
	 * 
	 * @param origem
	 *            Coordinate de origem
	 * @param destino
	 *            Coordinate de destino
	 * @return double
	 */
	private static double getDiffLongitude(final Coordenada origem,
			final Coordenada destino) {
		return Math.toRadians(destino.getLongitude())
				- Math.toRadians(origem.getLongitude());
	}

	/**
	 * Obtem a distancia em metros entre duas {@link Coordenada}s.
	 * 
	 * @param origem
	 *            Coordinate origem
	 * @param destino
	 *            Coordinate destino
	 * @return double
	 */
	public static double getDistancia(final Coordenada origem,
			final Coordenada destino) {

		double dLat = CoordenadaUtil.getDiffLatitude(origem, destino);
		double dLon = CoordenadaUtil.getDiffLongitude(origem, destino);

		double lat1 = Math.toRadians(origem.getLatitude());
		double lat2 = Math.toRadians(destino.getLatitude());

		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.sin(dLon / 2)
				* Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);

		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

		return (R * c) * 1000;
	}

	/**
	 * Obtem a rolamento (bearing) para trassar uma reta entre dois pontos
	 * formando o caminho mais curto entre eles.
	 * 
	 * @param origem
	 *            Coordinate origem
	 * @param destino
	 *            Coordinate destino
	 * @return double
	 */
	public static double getRolamento(final Coordenada origem,
			final Coordenada destino) {

		double dPhi = Math.log(Math.tan(Math.toRadians(destino.getLatitude())
				/ 2 + Math.PI / 4)
				/ Math.tan(Math.toRadians(origem.getLatitude()) / 2 + Math.PI
						/ 4));

		double dLon = CoordenadaUtil.getDiffLongitude(origem, destino);

		// if dLon over 180° take shorter rhumb across anti-meridian:
		if (Math.abs(dLon) > Math.PI) {
			dLon = dLon > 0 ? -(2 * Math.PI - dLon) : (2 * Math.PI + dLon);
		}

		double bearing = Math.toDegrees(Math.atan2(dLon, dPhi));

		if (bearing < 0)
			bearing = 360 + bearing;

		return bearing;
	}

	/**
	 * Converte o rolamento dem graus em uma String formatada para 3 digitos.
	 * Removendo as casas decimais e incluindo zero na frente caso seja um valor
	 * menor que tres digitos.
	 * 
	 * @param rolamento
	 *            {@link Double}
	 * 
	 * @return {@link String}
	 */
	public static String getRolamentoFormatado(final Double rolamento) {
		String rol = rolamento.toString();
		rol = rol.substring(0, rol.indexOf("."));

		if (rol.length() < 2)
			return "00" + rol;
		if (rol.length() == 2)
			return "0" + rol;

		return rol;
	}

}
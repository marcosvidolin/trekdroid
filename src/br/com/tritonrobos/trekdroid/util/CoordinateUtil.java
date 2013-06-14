package br.com.tritonrobos.trekdroid.util;

import br.com.tritonrobos.trekdroid.model.Coordinate;

/**
 * Classe utilitaria para realizar calculos diversos sobre coordenadas
 * geograficas.
 * 
 * @author vidolin
 * @since 25/04/2013
 * 
 */
public class CoordinateUtil {

	// R is earth’s radius (mean radius = 6,371km)
	private static final double R = 6371; // 6.372,795477598

	// fazer enum recebendo o valor de graus para ser retornado
	enum Direcao {
		DIREITA, ESQUERDA;
	}

	/**
	 * Obtem a diferenca entre a Latitude das Coordinates de origem e destino.
	 * 
	 * @param origem
	 *            Coordinate de origem
	 * @param destino
	 *            Coordinate de destino
	 * @return double
	 */
	private static double getDiffLatitude(final Coordinate origem,
			final Coordinate destino) {
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
	private static double getDiffLongitude(final Coordinate origem,
			final Coordinate destino) {
		return Math.toRadians(destino.getLongitude())
				- Math.toRadians(origem.getLongitude());
	}

	/**
	 * Obtem a distancia em metros entre duas {@link Coordinate}s.
	 * 
	 * @param origem
	 *            Coordinate origem
	 * @param destino
	 *            Coordinate destino
	 * @return double
	 */
	public static double getDistancia(final Coordinate origem,
			final Coordinate destino) {

		double dLat = CoordinateUtil.getDiffLatitude(origem, destino);
		double dLon = CoordinateUtil.getDiffLongitude(origem, destino);

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
	public static double getRolamento(final Coordinate origem,
			final Coordinate destino) {

		double dPhi = Math.log(Math.tan(Math.toRadians(destino.getLatitude())
				/ 2 + Math.PI / 4)
				/ Math.tan(Math.toRadians(origem.getLatitude()) / 2 + Math.PI
						/ 4));

		double dLon = CoordinateUtil.getDiffLongitude(origem, destino);

		// if dLon over 180° take shorter rhumb across anti-meridian:
		if (Math.abs(dLon) > Math.PI) {
			dLon = dLon > 0 ? -(2 * Math.PI - dLon) : (2 * Math.PI + dLon);
		}

		return Math.toDegrees(Math.atan2(dLon, dPhi));
	}

	/**
	 * Obtem a diferenca entre os graus de origem e destino rotacionando para a
	 * esquerda.
	 * 
	 * @param grausOrigem
	 *            valor de 0 a 360
	 * @param grausDestino
	 *            valor de 0 a 360
	 * @return double graus entre 0 a 360 a ser rotacionado para a esquerda
	 */
	private static double getGrausEsquesda(final double grausOrigem,
			final double grausDestino) {
		return grausDestino - grausOrigem;
	}

	/**
	 * Obtem a diferenca entre os graus de origem e destino rotacionando para a
	 * direita.
	 * 
	 * @param grausOrigem
	 *            valor de 0 a 360
	 * @param grausDestino
	 *            valor de 0 a 360
	 * @return double graus entre 0 a 360 a ser rotacionado para a direita
	 */
	private static double getGrausDireita(final double grausOrigem,
			final double grausDestino) {
		return grausOrigem - grausDestino;
	}

	/**
	 * Obtem um enum do tipo Direcao, contendo a direcao (direita/esquerda) e
	 * quantos graus deve rotacionar para a direcao dada. Obs.: 0 ou 360
	 * continuar reto, 180 tanto faz o lado.
	 * 
	 * @param grausOrigem
	 *            valor de 0 a 360
	 * @param grausDestino
	 *            valor de 0 a 360
	 * @return
	 */
	public static Direcao getRotacao(final double grausOrigem,
			final double grausDestino) {
		double esq = CoordinateUtil.getGrausEsquesda(grausOrigem, grausDestino);
		double dir = CoordinateUtil.getGrausDireita(grausOrigem, grausDestino);

		/*if (removeSinal(esq) > 180 && removeSinal(dir) > 180) {
			if (esq > dir) {
				return "Rotacionar " + (360 - esq) + " para a esquerda.";
			}
			return "Rotacionar " + (360 - dir) + " para a direita.";
		}

		if (esq < 0) {
			return "Rotacionar " + removeSinal(esq) + " para a esquerda.";
		}

		return "Rotacionar " + removeSinal(dir) + " para a direita.";*/
		return null;
	}

	/**
	 * usar a funcao MOD para remover o sinal e excluir esse metodo do programa.
	 * 
	 * @param valor
	 * @return
	 */
	private static double removeSinal(final double valor) {
		return valor < 0 ? valor * -1 : valor;
	}

}
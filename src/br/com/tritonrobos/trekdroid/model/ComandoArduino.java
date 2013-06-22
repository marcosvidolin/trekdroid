package br.com.tritonrobos.trekdroid.model;

/**
 * 
 * @author vidolin
 * 
 */
public class ComandoArduino {

	/**
	 * Enum de comando do arduino.
	 */
	public enum Comando {
		PARAR_MOTORES("0"), ANDAR_FRENTE("1"), ANDAR_TRAZ("2"), GIRAR_DIREITA(
				"3"), GIRAR_ESQUESDA("4"), LOCALIZAR_CONE("5"), OBTER_GRAUS("6"), ROTACIONAR_PARA(
				"7");

		private final String valor;

		Comando(final String valor) {
			this.valor = valor;
		}

		public String toString() {
			return valor;
		}
	}

	/**
	 * Enum para velocidade.
	 */
	public enum Velocidade {
		MINIMA("1"), MEDIA("2"), MAXIMA("3");

		private final String valor;

		Velocidade(final String valor) {
			this.valor = valor;
		}

		public String toString() {
			return valor;
		}
	}

	/**
	 * Enum de direcao.
	 */
	public enum Direcao {
		DIREITA("D"), ESQUERDA("E");

		private final String valor;

		Direcao(final String valor) {
			this.valor = valor;
		}

		public String toString() {
			return valor;
		}
	}

	private final Comando comando;
	private final String valor;
	private final Velocidade velocidade;

	/**
	 * Classe builder.
	 */
	public static class Builder {

		private Comando comando = Comando.PARAR_MOTORES;
		private String valor = "000";
		private Velocidade velocidade = Velocidade.MINIMA;

		public Builder comando(final Comando comando) {
			this.comando = comando;
			return this;
		}

		public Builder valor(final String valor) {
			this.valor = valor;
			return this;
		}

		public Builder velocidade(final Velocidade velocidade) {
			this.velocidade = velocidade;
			return this;
		}

		public ComandoArduino build() {
			return new ComandoArduino(this);
		}
	}

	/**
	 * Construtor privado para o builder.
	 * 
	 * @param builder
	 */
	private ComandoArduino(final Builder builder) {
		this.comando = builder.comando;
		this.valor = builder.valor;
		this.velocidade = builder.velocidade;
	}

	/**
	 * Preenche a string com zero a esquerda.
	 * 
	 * @param valor
	 *            valor do comando
	 * @return String
	 */
	private String preencherComZero(final String valor) {
		if (valor == null || valor.trim().equals(""))
			return "000";

		if (valor.length() == 1)
			return "00" + valor;

		if (valor.length() == 2)
			return "0" + valor;

		return valor;
	}

	/**
	 * Obtem o comando formatado para ser enviada ao Arduino.
	 * 
	 * @return {@link String}
	 */
	public String getComandoComoTexto() {
		return this.comando + this.preencherComZero(valor) + velocidade;
	}

	/**
	 * toString.
	 */
	@Override
	public String toString() {
		return "ComandoArduino [comando=" + comando + ", valor=" + valor
				+ ", velocidade=" + velocidade + "]";
	}

}

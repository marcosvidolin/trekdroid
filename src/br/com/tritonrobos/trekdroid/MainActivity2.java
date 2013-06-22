package br.com.tritonrobos.trekdroid;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import br.com.tritonrobos.trekdroid.http.client.ArduinoHttpClient;
import br.com.tritonrobos.trekdroid.model.ComandoArduino.Velocidade;

/**
 * Activity principal para controle da tela de captura de coordenadas.
 * 
 * @author vidolin
 * @since 25/04/2013
 */
public class MainActivity2 extends Activity {

	private Button btParar;
	private Button btFrente;
	private Button btRe;
	private Button btDireita;
	private Button btEsquerda;

	private ArduinoHttpClient arduino = new ArduinoHttpClient();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main2);
		this.setupElements();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	/**
	 * Método usado para importar os elementos da classe R.
	 */
	private void setupElements() {

		this.btParar = (Button) findViewById(R.id.btParar);
		this.btParar.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Runnable runnable = new Runnable() {
					public void run() {
						arduino.pararMotores();
					}
				};
				new Thread(runnable).start();
			}
		});

		this.btFrente = (Button) findViewById(R.id.btFrente);
		this.btFrente.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Runnable runnable = new Runnable() {
					public void run() {
						arduino.moverParaFrente(Velocidade.MINIMA);
					}
				};
				new Thread(runnable).start();
			}
		});

		this.btRe = (Button) findViewById(R.id.btRe);
		this.btRe.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Runnable runnable = new Runnable() {
					public void run() {
						arduino.moverParaTraz(Velocidade.MINIMA);
					}
				};
				new Thread(runnable).start();
			}
		});

		this.btDireita = (Button) findViewById(R.id.btDireita);
		this.btDireita.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Runnable runnable = new Runnable() {
					public void run() {
						arduino.rotacionarParaDireita(Velocidade.MINIMA);
					}
				};
				new Thread(runnable).start();
			}
		});

		this.btEsquerda = (Button) findViewById(R.id.btEsquerda);
		this.btEsquerda.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Runnable runnable = new Runnable() {
					public void run() {
						arduino.rotacionarParaEsquerda(Velocidade.MINIMA);
					}
				};
				new Thread(runnable).start();
			}
		});
	}

}
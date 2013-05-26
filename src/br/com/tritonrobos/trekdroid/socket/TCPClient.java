package br.com.tritonrobos.trekdroid.socket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import android.util.Log;

/**
 * Cliente TCP para troca de mensagem com o Arduino via Socket.
 * 
 * @author vidolin
 * @since 21/05/2013
 */
public class TCPClient {

	private String serverMessage;
	public static final String ARDUINO_SERVER_IP = "192.168.1.199";

	public static final int ARDUINO_SERVER_PORT = 80;
	private OnMessageReceived mMessageListener = null;
	private boolean mRun = false;

	PrintWriter out;
	BufferedReader in;

	/**
	 * Constructor of the class. OnMessagedReceived listens for the messages
	 * received from server
	 */
	public TCPClient(OnMessageReceived listener) {
		mMessageListener = listener;
	}

	/**
	 * Sends the message entered by client to the server
	 * 
	 * @param message
	 *            text entered by client
	 */
	public void sendMessage(String message) {
		if (out != null && !out.checkError()) {
			out.println(message);
			out.flush();
		}
	}

	public void stopClient() {
		mRun = false;
	}

	public void run() {
		mRun = true;

		try {
			InetAddress serverAddr = InetAddress.getByName(ARDUINO_SERVER_IP);

			Log.e("TCP Client", "C: Connecting...");
			Socket socket = new Socket(serverAddr, ARDUINO_SERVER_PORT);

			try {
				// send the message to the server
				out = new PrintWriter(new BufferedWriter(
						new OutputStreamWriter(socket.getOutputStream())), true);

				Log.e("TCP Client", "C: Sent.");
				Log.e("TCP Client", "C: Done.");

				// receive the message which the server sends back
				in = new BufferedReader(new InputStreamReader(
						socket.getInputStream()));

				// in this while the client listens for the messages sent by the
				// server
				while (mRun) {
					serverMessage = in.readLine();

					if (serverMessage != null && mMessageListener != null) {
						// call the method messageReceived from MyActivity class
						mMessageListener.messageReceived(serverMessage);
					}
					serverMessage = null;
				}

				Log.e("RESPONSE FROM SERVER", "S: Received Message: '"
						+ serverMessage + "'");

			} catch (Exception e) {
				Log.e("TCP", "S: Error", e);
			} finally {
				// the socket must be closed. It is not possible to reconnect to
				// this socket
				// after it is closed, which means a new socket instance has to
				// be created.
				socket.close();
			}
		} catch (Exception e) {
			Log.e("TCP", "C: Error", e);
		}
	}

	// Declare the interface. The method messageReceived(String message) will
	// must be implemented in the MyActivity
	// class at on asynckTask doInBackground
	public interface OnMessageReceived {
		public void messageReceived(String message);
	}
}
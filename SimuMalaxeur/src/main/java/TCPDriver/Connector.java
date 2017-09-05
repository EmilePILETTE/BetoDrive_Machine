package TCPDriver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class Connector extends Thread {

	static org.jdom2.Document document;
	private String Statut = "Disconnected";
	private String ip, port;
	private Emission t2;
	private Reception t3;

	/**
	 * @return the Statut
	 */
	public String getStatut() {
		return Statut;
	}

	/**
	 * @param Statut the Statut to set
	 */
	public void setStatut(String Statut) {
		this.Statut = Statut;
	}

	/**
	 * @return the ip
	 */
	public String getIp() {
		return ip;
	}

	/**
	 * @param ip the ip to set
	 */
	public void setIp(String ip) {
		this.ip = ip;
	}

	/**
	 * @return the port
	 */
	public String getPort() {
		return port;
	}

	/**
	 * @param port the port to set
	 */
	public void setPort(String port) {
		this.port = port;
	}

	public enum StatutList {
		Started,
		Init,
		Connected,
		Disconnected,
		Error;
	}

	@Override
	public void run() {
		//read TCP conf from an xml file
		Socket socket = null;
		System.out.println("socket : " + getIp() + ":" + getPort());
		this.setStatut("Started");
		// TODO Auto-generated method stub
		while (true) {
			try {
				InetAddress serveur = InetAddress.getByName(getIp());
				socket = new Socket(serveur, Integer.parseInt(getPort()));
				InputStreamReader ReadStream = new InputStreamReader(socket.getInputStream());
				PrintWriter dataout = new PrintWriter(socket.getOutputStream());
				BufferedReader datain = new BufferedReader(ReadStream);
				// Thread lecture/ecriture lanc�s
				t2 = new Emission(dataout);
				t2.start();
				t3 = new Reception(datain);
				t3.start();
				this.setStatut("Connected");
				System.out.println(this.getStatut());
				while (true) {
					try {
						this.sleep(5000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block

					}
				}
			} catch (IOException | NumberFormatException e) {
				this.setStatut("Disonnected");
				System.out.println(this.getStatut());
				try {
					socket.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					System.out.println("Can't close socket");
				}
			}
		}
	}
}

package TCPDriver;

import DataModel.TCPDataMultiplexer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Server {

	//On initialise des valeurs par défaut
	private int port = 50000;
	private String host = "127.0.0.1";
	private ServerSocket server = null;
	private boolean isRunning = true;
	private TCPDataMultiplexer InOutBuffer;
	private PrintWriter writer = null;
	private BufferedReader reader = null;
	private InputStreamReader ReadStream = null;
	private int multiplexerLineNumber;
	private String clientName = "";
	private Emission t2;
	private Reception t3;

	public Server() {
		try {
			server = new ServerSocket(port, 100, InetAddress.getByName(host));
		} catch (UnknownHostException e) {
		} catch (IOException e) {
		}
	}

	public Server(String pHost, int pPort, TCPDataMultiplexer InOutBuff) {
		host = pHost;
		port = pPort;
		InOutBuffer = InOutBuff;
		try {
			server = new ServerSocket(port, 100, InetAddress.getByName(host));
		} catch (UnknownHostException e) {
		} catch (IOException e) {
		}
	}

	//On lance notre serveur
	public void open() {

		//Toujours dans un thread à part vu qu'il est dans une boucle infinie
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				while (isRunning == true) {
					try {
						//On attend une connexion d'un client
						Socket client = server.accept();

						//Une fois reçue, on la traite dans un thread séparé
						/*
						 * System.out.println("Connexion cliente reçue.");
						 * Thread t = new Thread(new ClientProcessor(client,
						 * InOutBuffer));
						 * t.start();
						 */
						System.err.println("Lancement du traitement de la connexion cliente");

						boolean closeConnexion = false;
						//tant que la connexion est active, on traite les demandes
						//Ici, nous n'utilisons pas les mêmes objets que précédemment
						//Je vous expliquerai pourquoi ensuite
						InputStreamReader ReadStream = new InputStreamReader(client.getInputStream());
						PrintWriter writer = new PrintWriter(client.getOutputStream());
						BufferedReader reader = new BufferedReader(ReadStream);
						/*
						 *
						 */
						String debug = "";
						/*
						 * debug = "Init Tab size : InBool : " +
						 * InOutBuffer.getInBool().size()
						 * + " InBuff : " + InOutBuffer.getInBuff().size()
						 * + " OutBool : " + InOutBuffer.getOutBool().size()
						 * + " OutBuff : " + InOutBuffer.getOutBuff().size();
						 * System.err.println(debug);
						 */
						//On attend la demande du client
						clientName = "";
						char[] buffer = new char[50];
						reader.read(buffer);
						for (char c : buffer) {
							if (c == ';') {
								break;
							}
							clientName += c;
						}
						buffer = null;
						InOutBuffer.addName(clientName);
						//On affiche quelques infos, pour le débuggage
						debug = "";
						debug = "Thread : " + Thread.currentThread().getName() + ". ";
						debug += "\t -> Commande reçue : " + clientName;
						System.err.println(debug);
						//On affiche quelques infos, pour le débuggage
						/*
						 * debug = "";
						 * debug = "Client procesor Tab size : InBool : " +
						 * InOutBuffer.getInBool().size()
						 * + " InBuff : " + InOutBuffer.getInBuff().size()
						 * + " OutBool : " + InOutBuffer.getOutBool().size()
						 * + " OutBuff : " + InOutBuffer.getOutBuff().size();
						 * System.err.println(debug);
						 */
						multiplexerLineNumber = InOutBuffer.getName().indexOf(clientName);
						System.out.println("numéro du pipe : " + multiplexerLineNumber);

						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block

						}
						Thread t1 = new Thread(new Emission(writer, InOutBuffer, multiplexerLineNumber));
						t1.start();
						Thread t2 = new Thread(new Reception(reader, InOutBuffer, multiplexerLineNumber));
						t2.start();

					} catch (SocketException e) {
						System.err.println("LA CONNEXION A ETE INTERROMPUE ! ");
						server.isClosed();
					} catch (IOException e) {
						System.err.println("Err ; fermeture connexion");
						server.isClosed();
					}
				}

				try {
					server.close();
				} catch (IOException e) {
					server = null;
				}
			}
		}
		);

		t.start();
	}

	public void close() {
		isRunning = false;
	}
}

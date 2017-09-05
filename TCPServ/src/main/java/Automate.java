
import DataModel.TCPDataMultiplexer;
import TCPDriver.Server;
import java.net.ServerSocket;

public class Automate {

	static org.jdom2.Document document;
	static Thread serverThread;
	//static TCPDataMultiplexer InOutBuffer = new TCPDataMultiplexer();
	private ServerSocket server;
	static TCPDataMultiplexer InOutBuffer;
	static private int poidsEau = 8;
	static boolean poidsEauAtteind = false;
	static private int poidsAdjuvants = 3;
	static boolean poidsAdjuvantsAtteind = false;
	static boolean finEauAdjuvants = false;
	static private int poidsCiment = 7;
	static boolean poidsCimentAtteind = false;
	static boolean finCiment = false;
	static private int poidsSable = 12;
	static boolean poidsSableAtteind = false;
	static boolean positionSkip = false;
	static boolean finSable = false;
	static int malaxeurStep = 0;
	static private int ampMalaxeur = 0;
	static boolean finMalaxeur = false;
	static private String capteurRead = "";
	static private int numMalaxeur = -1;
	static private int numBaculeCiment = -1;
	static private int numBasculeEau = -1;
	static private int numSkipTremies = -1;
	static boolean buffersFree = true;

	public static void sendCmd(int canal, String cmd) {
		InOutBuffer.getOutBuff().set(canal, (cmd).toCharArray());
		InOutBuffer.getOutBool().set(canal, true);
		while (InOutBuffer.getOutBool().get(canal)) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block

			}
		}
	}

	public static void readCmd(int canal) {
		capteurRead = new String();
		for (char c : InOutBuffer.getInBuff().get(canal)) {
			capteurRead += c;
			if (c == ';') {
				break;
			}
		}
		InOutBuffer.getInBool().set(canal, false);
	}

	public static void main(String[] args) {

		InOutBuffer = new TCPDataMultiplexer();
		String host = "127.0.0.1";
		int port = 50000;
		int amps = 0;
		String debug = "";

		Server ts = new Server(host, port, InOutBuffer);
		ts.open();
		System.out.println("Serveur initialisé.");
		while (InOutBuffer.getName().size() < 4) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block

			}
		}
		for (int i = 0; i < InOutBuffer.getName().size(); i++) {
			InOutBuffer.getOutBuff().set(i, ("led:g;").toCharArray());
			InOutBuffer.getOutBool().set(i, true);
		}
		debug = "Automate Tab :\n";
		for (int i = 0; i < InOutBuffer.getName().size(); i++) {
			debug += "Name : \t" + InOutBuffer.getName().get(i)
					+ " InBool : " + InOutBuffer.getInBool().get(i)
					+ " OutBool : " + InOutBuffer.getOutBool().get(i) + "\n";
		}
		System.err.print(debug);
		for (int i = 0; i < InOutBuffer.getName().size(); i++) {
			while (InOutBuffer.getOutBool().get(i)) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block

				}
			}
		}
		for (int i = 0; i < InOutBuffer.getName().size(); i++) {
			if (InOutBuffer.getName().get(i).matches("Malaxeur")) {
				numMalaxeur = i;
			}
			if (InOutBuffer.getName().get(i).matches("BasculeCiment")) {
				numBaculeCiment = i;
			}
			if (InOutBuffer.getName().get(i).matches("BasculeEau")) {
				numBasculeEau = i;
			}
			if (InOutBuffer.getName().get(i).matches("SkipTremies")) {
				numSkipTremies = i;
			}
		}
		System.out.println("Num Malaxeur : " + numMalaxeur + "Num Ciment : " + numBaculeCiment + "Num Eau : " + numBasculeEau + "Num Skip : " + numSkipTremies);
		if (numBaculeCiment != -1) {
			sendCmd(numBaculeCiment, "cim:on;");
		}
		if (numBasculeEau != -1) {
			sendCmd(numBasculeEau, "eau:on;");
		}
		if (numSkipTremies != -1) {
			sendCmd(numSkipTremies, "door:on;");
		}
		while (true) {
			try {
				if (numBaculeCiment != -1 && InOutBuffer.getName().get(numBaculeCiment).matches("BasculeCiment") && InOutBuffer.getInBool().get(numBaculeCiment)) {
					readCmd(numBaculeCiment);
					System.out.print("Poids de la bascule à ciment :");
					System.out.println(Integer.parseInt(capteurRead.split(";")[0].split(":")[1]));
					poidsCimentAtteind = (Integer.parseInt(capteurRead.split(";")[0].split(":")[1]) >= poidsCiment);
					if (!finCiment && poidsCimentAtteind) {
						sendCmd(numBaculeCiment, "cim:off;");
					}
					if (!finCiment && poidsCimentAtteind) {
						sendCmd(numBaculeCiment, "door:on;");
						finCiment = true;
					}
					if (finCiment && !poidsCimentAtteind) {
						sendCmd(numBaculeCiment, "door:off;");
						malaxeurStep++;
					}
				}
				/*
				 * Gestion Machine eau et adjuvants
				 */
				if (numBasculeEau != -1 && InOutBuffer.getName().get(numBasculeEau).matches("BasculeEau") && InOutBuffer.getInBool().get(numBasculeEau)) {
					readCmd(numBasculeEau);
					System.out.print("Poids de la bascule à eau et adjuvants : ");
					System.out.println(Integer.parseInt(capteurRead.split(";")[0].split(":")[1]));
					if (Integer.parseInt(capteurRead.split(";")[0].split(":")[1]) >= poidsEau + poidsAdjuvants) {
						poidsAdjuvantsAtteind = true;
					} else {
						if (Integer.parseInt(capteurRead.split(";")[0].split(":")[1]) >= poidsEau) {
							poidsEauAtteind = true;
						} else {
							poidsEauAtteind = false;
							poidsAdjuvantsAtteind = false;
						}
					}
					if (!finEauAdjuvants && poidsEauAtteind && !poidsAdjuvantsAtteind) {
						sendCmd(numBasculeEau, "eau:off;");
						sendCmd(numBasculeEau, "adj:on;");
					}
					if (!finEauAdjuvants && poidsAdjuvantsAtteind) {
						sendCmd(numBasculeEau, "adj:off;");
					}
					if (!finEauAdjuvants && poidsAdjuvantsAtteind && poidsEauAtteind) {
						sendCmd(numBasculeEau, "door:on;");
						finEauAdjuvants = true;
					}
					if (finEauAdjuvants && !poidsEauAtteind && !poidsAdjuvantsAtteind) {
						sendCmd(numBasculeEau, "door:off;");
						malaxeurStep++;
					}
				}
				/*
				 * Gestion Skip et trémies
				 */
				if (numSkipTremies != -1 && InOutBuffer.getName().get(numSkipTremies).matches("SkipTremies") && InOutBuffer.getInBool().get(numSkipTremies)) {
					readCmd(numSkipTremies);
					if (capteurRead.split(";")[0].split(":")[0].matches("Poids")) {
						System.out.print("Poids du sable et des granulats : ");
						System.out.println(Integer.parseInt(capteurRead.split(";")[0].split(":")[1]));
						if (Integer.parseInt(capteurRead.split(";")[0].split(":")[1]) >= poidsSable) {
							poidsSableAtteind = true;
						}
					}
					if (capteurRead.split(";")[0].split(":")[0].matches("Position")) {
						System.out.print("Position du skip : ");
						System.out.println(capteurRead.split(";")[0].split(":")[1]);
						positionSkip = capteurRead.split(";")[0].split(":")[1].matches("top");
					}
					if (poidsSableAtteind) {
						sendCmd(numSkipTremies, "door:off;");
					}
					if (!finSable && !positionSkip && poidsSableAtteind) {
						sendCmd(numSkipTremies, "up:on;");
					}
					if (positionSkip) {
						sendCmd(numSkipTremies, "up:off;");
						poidsSableAtteind = false;
						sendCmd(numSkipTremies, "res:on;");
						finSable = true;
					}

					if (!positionSkip && finSable) {
						sendCmd(numSkipTremies, "res:off;");
						malaxeurStep++;
					}
					if (capteurRead.split(";")[0].split(":")[0].matches("Poids")) {
						if (Integer.parseInt(capteurRead.split(";")[0].split(":")[1]) == 0) {
							sendCmd(numSkipTremies, "res:off;");
						}
					}
				}
				if (numMalaxeur != -1) {
					if (finMalaxeur) {
						sendCmd(numMalaxeur, "door:off;");
					}
					if (malaxeurStep == 1) {
						sendCmd(numMalaxeur, "amp:50;");
					}
					if (malaxeurStep == 2) {
						sendCmd(numMalaxeur, "amp:100;");
					}
					if (malaxeurStep == 3) {
						sendCmd(numMalaxeur, "amp:150;");
						malaxeurStep = 0;
					}
					if (finCiment && finEauAdjuvants && finSable && !finMalaxeur) {
						sendCmd(numMalaxeur, "door:on;");
						finMalaxeur = true;
					}
				}
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block

			}
		}
	}
}

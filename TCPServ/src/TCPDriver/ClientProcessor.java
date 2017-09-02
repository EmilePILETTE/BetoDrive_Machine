package TCPDriver;

import DataModel.TCPDataMultiplexer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

public class ClientProcessor extends Thread {

    private Socket sock;
    private PrintWriter writer = null;
    private BufferedReader reader = null;
    private InputStreamReader ReadStream = null;
    private TCPDataMultiplexer InOutBuffer;
    private int multiplexerLineNumber;
    private String clientName;
    private Emission t2;
    private Reception t3;

    public ClientProcessor(Socket pSock, TCPDataMultiplexer InOutBuff) {
        sock = pSock;
        clientName = new String();
        InOutBuffer = InOutBuff;
    }

    //Le traitement lancé dans un thread séparé
    public void run() {
        System.err.println("Lancement du traitement de la connexion cliente");

        boolean closeConnexion = false;
        //tant que la connexion est active, on traite les demandes
        //Ici, nous n'utilisons pas les mêmes objets que précédemment
        //Je vous expliquerai pourquoi ensuite
        try {
            InputStreamReader ReadStream = new InputStreamReader(sock.getInputStream());
            PrintWriter writer = new PrintWriter(sock.getOutputStream());
            BufferedReader reader = new BufferedReader(ReadStream);
            /**/
            String debug = "";
            /*
            debug = "Init Tab size : InBool : " + InOutBuffer.getInBool().size()
                    + " InBuff : " + InOutBuffer.getInBuff().size()
                    + " OutBool : " + InOutBuffer.getOutBool().size()
                    + " OutBuff : " + InOutBuffer.getOutBuff().size();
            System.err.println(debug);*/
            //On attend la demande du client         
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
            debug = "";
            debug = "Client procesor Tab size : InBool : " + InOutBuffer.getInBool().size()
                    + " InBuff : " + InOutBuffer.getInBuff().size()
                    + " OutBool : " + InOutBuffer.getOutBool().size()
                    + " OutBuff : " + InOutBuffer.getOutBuff().size();
            System.err.println(debug);
            */
            multiplexerLineNumber = InOutBuffer.getName().indexOf(clientName);
            System.out.println("numéro du pipe : " + multiplexerLineNumber);
        } catch (SocketException e) {
            System.err.println("LA CONNEXION A ETE INTERROMPUE ! ");
            sock.isClosed();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Err ; fermeture connexion");
            sock.isClosed();
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        t2 = new Emission(writer, InOutBuffer, multiplexerLineNumber);
        t2.start();
        t3 = new Reception(reader, InOutBuffer, multiplexerLineNumber);
        t3.start();

        while (!sock.isClosed()) {
            try {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                if (closeConnexion) {
                    System.err.println("COMMANDE CLOSE DETECTEE ! ");
                    writer = null;
                    reader = null;
                    sock.close();
                    break;
                }
            } catch (SocketException e) {
                System.err.println("LA CONNEXION A ETE INTERROMPUE ! ");
                sock.isClosed();
                closeConnexion = true;
                break;
            } catch (IOException e) {
                e.printStackTrace();
                closeConnexion = true;
            }
        }
    }
}

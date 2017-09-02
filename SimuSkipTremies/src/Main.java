
import View.Vue;
import DataModel.TCPDataBufferIn;
import DataModel.TCPDataBufferOut;
import TCPDriver.Connector;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JLabel;

import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

public class Main {

    static org.jdom2.Document document;
    static TCPDataBufferIn TCPdataIn = new TCPDataBufferIn();
    static TCPDataBufferOut TCPdataOut = new TCPDataBufferOut();
    static String name;
    static Vue Vue;
    static ArrayList<String[]> ListElement = new ArrayList();

    /**
     * @param args
     */
    public static void main(String[] args) {
        Connector t1 = new Connector();
        Element racine, pannelElements;
        SAXBuilder sxb = new SAXBuilder();

        t1.setStatut("Init");
        try {
            document = sxb.build(new File("conf.xml"));
        } catch (Exception e) {
            System.out.println("Error in xml file");
        }
        racine = document.getRootElement();
        t1.setIp(racine.getChild("ip").getValue());
        t1.setPort(racine.getChild("port").getValue());
        pannelElements = racine.getChild("panelElement");
        name = racine.getChild("Name").getText();
        for (Element pane : pannelElements.getChildren()) {
            ListElement.add(new String[]{pane.getChild("name").getValue(),
                pane.getChild("value").getValue(),
                pane.getChild("unit").getValue(),
                pane.getChild("commande").getValue()});
        }
        Vue = new Vue(name);
        Vue.setStatut(t1.getStatut());
        Vue.RedOn();
        for (String[] n : ListElement) {
            Vue.putHashMapValue(n[0], n[1] + n[2]);
        }
        t1.start();
        while (!t1.getStatut().matches("Connected")) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.err.println("Send name : " + name);
        TCPdataOut.setBuffer((name + ";").toCharArray());
        TCPdataOut.setDataRdy(true);
        while (true) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (TCPdataIn.isDataRdy()) {
                System.out.println("Main get data : " + TCPdataIn.getData());
                for (String DataRcvE : TCPdataIn.getData().split("/")) {
                    // TRAITEMENT DES COMMANDES LED
                    if (DataRcvE.split(":")[0].matches("led")) {
                        if (DataRcvE.split(":")[1].matches("r")) {
                            ((Vue) Vue).RedOn();
                        }
                        if (DataRcvE.split(":")[1].matches("g")) {
                            ((Vue) Vue).GreenOn();
                        }
                    } else {
                        // On parcourt tous les éléments pour vérifier si on à reçu une commande qui correspond à l'un d'eux
                        for (String[] n : ListElement) {
                            if (DataRcvE.split(":")[0].matches(n[3])) {
                                n[1] = DataRcvE.split(":")[1];
                                Vue.editHashMapValue(n[0], n[1]);
                            }
                        }
                    }
                }
                TCPdataIn.setData("");
                TCPdataIn.setDataRdy(false);

            } else {                //Envoi des données de sorties qui sont identifiées avec la commande out
                for (String[] n : ListElement) {
                    if (n[3].matches("out") && !TCPdataOut.isDataRdy()) {
                        TCPdataOut.setBuffer((n[0] + ":" + n[1] + ";").toCharArray());
                        TCPdataOut.setDataRdy(true);
                        while (TCPdataOut.isDataRdy()) {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    }
                }
                // Si le trémie est en bas avec la porte ouverte, le poids augmente
                if (ListElement.get(4)[1].matches("down") && ListElement.get(2)[1].matches("on")) {
                    ListElement.get(3)[1] = String.valueOf(Integer.parseInt(ListElement.get(3)[1]) + 1);
                    Vue.editHashMapValue(ListElement.get(3)[0], ListElement.get(3)[1]);
                }
                // Si le trémie arrive en haut alors le contenu devient vide
                if (ListElement.get(4)[1].matches("top")) {
                    ListElement.get(3)[1] = String.valueOf(0);
                    Vue.editHashMapValue(ListElement.get(3)[0], ListElement.get(3)[1]);
                }
                // Si le trémie reçoit la commande monté et ne reçoit plus la commande reset alors il monte
                if (ListElement.get(0)[1].matches("on") && ListElement.get(1)[1].matches("off") && !ListElement.get(4)[1].matches("top")) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    ListElement.get(4)[1] = "top";
                    Vue.editHashMapValue(ListElement.get(4)[0], ListElement.get(4)[1]);
                }
                // Si le trémie reçoit la commande reset et ne reçoit plus la commande monté alors il déscend
                if (ListElement.get(1)[1].matches("on") && ListElement.get(0)[1].matches("off") && !ListElement.get(4)[1].matches("down")) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    ListElement.get(4)[1] = "down";
                    Vue.editHashMapValue(ListElement.get(4)[0], ListElement.get(4)[1]);
                }
                Vue.setStatut(t1.getStatut());
            }
        }
    }
}

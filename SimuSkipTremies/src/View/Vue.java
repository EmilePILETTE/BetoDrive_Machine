package View;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.util.HashMap;

public class Vue extends JFrame {

    private LedPannel LedPane;
    private JLabel labelStatut;
    private JPanel pan;
    private HashMap<JLabel, JLabel> LabelMap = new HashMap();

    public Vue(String title) {

        //Instanciation d'un objet JPanel
        pan = new JPanel();
        //D�finition de sa couleur de fond
        pan.setBackground(Color.LIGHT_GRAY);
        pan.setLayout(new GridLayout(0, 2));
        labelStatut = new JLabel("STATUT : Init");
        pan.add(labelStatut);
        LedPane = new LedPannel();
        pan.add(LedPane);
        //D�finit un titre pour notre fen�tre
        this.setTitle(title);
        //D�finit sa taille : 200 pixels de large et 100 pixels de haut
        this.setSize(300, 200);
        //Nous demandons maintenant � notre objet de se positionner au centre
        this.setLocationRelativeTo(null);
        //On pr�vient notre JFrame que notre JPanel sera son content pane
        this.setContentPane(pan);
        // On active le kill de l'application à la fermeture de la fenètre
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // On vérouille la taille de la fenètre
        this.setResizable(false);
        // On rend la fenètre visible
        this.setVisible(true);
    }

    public void RedOn() {
        this.LedPane.setRedLed(true);
        this.LedPane.setGreenLed(false);
        this.LedPane.repaint();
    }

    public void GreenOn() {
        this.LedPane.setRedLed(false);
        this.LedPane.setGreenLed(true);
        this.LedPane.repaint();
    }

    public void putHashMapValue(String key, String value) {
        JLabel Jkey = new JLabel(key);
        JLabel Jvalue = new JLabel(value);
        this.LabelMap.put(Jkey, Jvalue);
        this.getContentPane().add(Jkey);
        this.getContentPane().add(Jvalue);
    }

    public void rmHashMapValue(String key, String value) {
        JLabel Jkey = new JLabel(key);
        JLabel Jvalue = new JLabel(value);
        this.LabelMap.remove(Jkey, Jvalue);
        this.getContentPane().remove(Jkey);
        this.getContentPane().remove(Jvalue);
    }

    public void editHashMapValue(String key, String value) {
        for (HashMap.Entry<JLabel, JLabel> entry : LabelMap.entrySet()) {
            if (entry.getKey().getText() == key) {
                entry.getKey().setText(key);
                entry.getValue().setText(value);
            }
        }
    }

    public void setStatut(String Statut) {
        this.labelStatut.setText("STATUT: " + Statut);
    }
}

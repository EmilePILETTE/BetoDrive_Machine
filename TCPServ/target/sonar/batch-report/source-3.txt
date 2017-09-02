/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DataModel;

import java.util.ArrayList;

/**
 *
 * @author emile.pilette
 */
public class TCPDataMultiplexer {

    static private ArrayList<String> Name;
    static private ArrayList<Boolean> InBool;
    static private ArrayList<char[]> InBuff;
    static private ArrayList<Boolean> OutBool;
    static private ArrayList<char[]> OutBuff;
    private int bufflen;

    public TCPDataMultiplexer() {
        this.Name = new ArrayList();
        this.InBool = new ArrayList();
        this.InBuff = new ArrayList();
        this.OutBool = new ArrayList();
        this.OutBuff = new ArrayList();
        this.bufflen = 50;
    }

    public void addName(String threadName) {
        this.getName().add(threadName);
        this.getInBool().add(false);
        this.getInBuff().add(new char[bufflen]);
        this.getOutBool().add(false);
        this.getOutBuff().add(new char[bufflen]);
    }

    /**
     * @return the Name
     */
    public ArrayList<String> getName() {
        return Name;
    }

    /**
     * @return the InBool
     */
    public ArrayList<Boolean> getInBool() {
        return InBool;
    }

    /**
     * @return the InBuff
     */
    public ArrayList<char[]> getInBuff() {
        return InBuff;
    }

    /**
     * @return the OutBool
     */
    public ArrayList<Boolean> getOutBool() {
        return OutBool;
    }

    /**
     * @return the OutBuff
     */
    public ArrayList<char[]> getOutBuff() {
        return OutBuff;
    }

}

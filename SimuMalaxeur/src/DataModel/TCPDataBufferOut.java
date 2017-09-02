package DataModel;

import java.io.IOException;
import java.io.Serializable;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author emile.pilette
 */
public class TCPDataBufferOut implements Serializable {

    private int bufflen;
    static private boolean DataRdy;
    static private char[] buffer;

    public TCPDataBufferOut() {
        this.bufflen = 50;
        this.buffer = new char[bufflen];
        this.DataRdy = false;
    }

    /**
     * @return the bufflen
     */
    public int getBufflen() {
        return bufflen;
    }

    /**
     * @param bufflen the bufflen to set
     */
    public void setBufflen(int bufflen) {
        this.bufflen = bufflen;
    }

    /**
     * @return the buffer
     */
    public char[] getBuffer() {
        return buffer;
    }

    /**
     * @param buffer the buffer to set
     */
    public void setBuffer(char[] buffer) {
        this.buffer = buffer;
    }

    /**
     * @return the DataRdy
     */
    public boolean isDataRdy() {
        return DataRdy;
    }

    /**
     * @param DataRdy the DataRdy to set
     */
    public void setDataRdy(boolean DataRdy) {
        this.DataRdy = DataRdy;
    }
}

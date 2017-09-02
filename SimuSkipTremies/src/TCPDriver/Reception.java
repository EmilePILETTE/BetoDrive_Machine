package TCPDriver;

import DataModel.TCPDataBufferIn;
import java.io.BufferedReader;
import java.io.IOException;

public class Reception extends Thread {

    private BufferedReader in;
    private TCPDataBufferIn databuff;

    public Reception(BufferedReader in) {
        this.in = in;
        this.databuff = new TCPDataBufferIn();
    }

    public void run() {
        while (true) {
            try {
                if (!databuff.isDataRdy()) {
                    in.read(databuff.getBuffer());
                    databuff.setBufftodataString();
                    System.out.println(databuff.getData());
                } else {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @return the data
     */
    public TCPDataBufferIn getDataBuffer() {
        return databuff;
    }

    /**
     * @param data the data to set
     */
    public void setDataBuffer(TCPDataBufferIn databuff) {
        this.databuff = databuff;
    }
}

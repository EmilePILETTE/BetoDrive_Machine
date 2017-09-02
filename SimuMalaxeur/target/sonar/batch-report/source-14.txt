package TCPDriver;

import DataModel.TCPDataBufferOut;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class Emission extends Thread {

    private PrintWriter out;
    private TCPDataBufferOut databuff;

    public Emission(PrintWriter out) {
        this.out = out;
        this.databuff = new TCPDataBufferOut();
    }

    public void run() {
        while (true) {
            if (databuff.isDataRdy()) {
                System.out.println("Emission " + databuff.getBuffer().toString());
                out.print(databuff.getBuffer());
                out.flush();
                databuff.setDataRdy(false);
                databuff.setBuffer(null);
            } else {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }
}

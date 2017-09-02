package TCPDriver;

import DataModel.TCPDataMultiplexer;
import java.io.PrintWriter;

public class Emission extends Thread {

    private PrintWriter out;
    private TCPDataMultiplexer InOutBuffer;
    private int pipeNumber;

    public Emission(PrintWriter out, TCPDataMultiplexer InOutBuff, int pipeNum) {
        this.out = out;
        this.InOutBuffer = InOutBuff;
        pipeNumber = pipeNum;
        /*
        String debug = "";
        debug = "Send Tab size : InBool : " + InOutBuffer.getInBool().size()
                + " InBuff : " + InOutBuffer.getInBuff().size()
                + " OutBool : " + InOutBuffer.getOutBool().size()
                + " OutBuff : " + InOutBuffer.getOutBuff().size();
        System.err.println(debug);
         */
    }

    public void run() {
        while (true) {
            if (InOutBuffer.getOutBool().get(pipeNumber)) {
                System.out.println("Pipe nb " + pipeNumber + " is sending");
                /*
                System.err.println("état buffer : " + InOutBuffer.getOutBool().get(pipeNumber));
                System.err.println("buffer : " + InOutBuffer.getOutBuff().get(pipeNumber).toString());
                for (char c : InOutBuffer.getOutBuff().get(pipeNumber)) {
                    System.err.print(c);
                    if(c == ';'){
                        break;
                    }
                }
                System.err.println();*/
                out.print(InOutBuffer.getOutBuff().get(pipeNumber));
                out.flush();
                InOutBuffer.getOutBool().set(pipeNumber, false);
                InOutBuffer.getOutBuff().set(pipeNumber, null);
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

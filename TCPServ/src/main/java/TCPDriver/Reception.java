package TCPDriver;

import DataModel.TCPDataMultiplexer;
import java.io.BufferedReader;
import java.io.IOException;

public class Reception extends Thread {

	private BufferedReader in;
	private TCPDataMultiplexer InOutBuffer;
	private int pipeNumber;

	public Reception(BufferedReader in, TCPDataMultiplexer InOutBuff, int pipeNum) {
		this.in = in;
		this.InOutBuffer = InOutBuff;
		this.pipeNumber = pipeNum;
	}

	@Override
	public void run() {
		while (true) {
			try {
				if (!InOutBuffer.getInBool().get(pipeNumber)) {
					System.out.println("Pipe nb " + pipeNumber + " is reading");
					in.read(InOutBuffer.getInBuff().get(pipeNumber));
					InOutBuffer.getInBool().set(pipeNumber, true);
				} else {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block

					}
				}
			} catch (IOException e) {
			}
		}
	}
}

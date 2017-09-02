package View;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JPanel;

public class LedPannel extends JPanel {

	private boolean RedLed = false;
	private boolean GreenLed = false;

	@Override
	public void paintComponent(Graphics g) {
		//Vous verrez cette phrase chaque fois que la m�thode sera invoqu�e
		this.setBackground(Color.LIGHT_GRAY);
		int size = 0;
		if (this.getWidth() / 2 >= this.getHeight()) {
			size = this.getHeight();
		} else {
			size = this.getWidth() / 2;
		}
		if (GreenLed == true) {
			g.setColor(new Color(0, 255, 0));
		} else {
			g.setColor(new Color(0, 100, 0));
		}
		g.fillOval(this.getWidth() / 2, 0, size, size);
		if (RedLed == true) {
			g.setColor(new Color(255, 0, 0));
		} else {
			g.setColor(new Color(100, 0, 0));
		}
		g.fillOval(0, 0, size, size);
	}

	public boolean getGreenLed() {
		return GreenLed;
	}

	public void setGreenLed(boolean GreenLed) {
		this.GreenLed = GreenLed;
	}

	public boolean getRedLed() {
		return RedLed;
	}

	public void setRedLed(boolean RedLed) {
		this.RedLed = RedLed;
	}
}

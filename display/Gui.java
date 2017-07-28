package display;

import display.Gfx;
import display.Window;

/**
 * Incharge of initializing and running the project.
 * @author Ryan Paulitschke
 */
public class Gui {

	public static void main(String[] args) throws InterruptedException {
		Gfx.preload();

		Window window = new Window();
		javax.swing.SwingUtilities.invokeLater(window);
	
	}
}

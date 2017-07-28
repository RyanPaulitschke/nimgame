package display;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Handles importing all the images that need to be drawn on screen.
 * @author Ryan Paulitschke
 */
public class Gfx {
	public static Icon button = null;
	//Button image, background, selection box, and GUI highlights
	public static BufferedImage butt, bg, selected, info;
	//Piles
	public static BufferedImage[] dots = new BufferedImage[11];
	
	/*
	 * Pre-loads images so they can be used later
	 */
	public static void preload() {
		//Try to load images
		try {
			butt = ImageIO.read(new File("gfx/button.png"));
			button = new ImageIcon(butt,"Submit");
			bg = ImageIO.read(new File("gfx/bg.png"));
			selected = ImageIO.read(new File("gfx/select.png"));
			info = ImageIO.read(new File("gfx/info.png"));
			
			dots[0] = ImageIO.read(new File("gfx/nim0.png"));
			dots[1] = ImageIO.read(new File("gfx/nim1.png"));
			dots[2] = ImageIO.read(new File("gfx/nim2.png"));
			dots[3] = ImageIO.read(new File("gfx/nim3.png"));
			dots[4] = ImageIO.read(new File("gfx/nim4.png"));
			dots[5] = ImageIO.read(new File("gfx/nim5.png"));
			dots[6] = ImageIO.read(new File("gfx/nim6.png"));
			dots[7] = ImageIO.read(new File("gfx/nim7.png"));
			dots[8] = ImageIO.read(new File("gfx/nim8.png"));
			dots[9] = ImageIO.read(new File("gfx/nim9.png"));
			dots[10] = ImageIO.read(new File("gfx/nim10.png"));
			
			System.out.println("Graphics were successfully preloaded");

		} catch (Exception e) { 
			//Let us know if the images failed to load.
			e.printStackTrace();
			System.out.println("Preloading graphics has failed");
		}
	}

}

package display;


import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Screen is used to draw text & images
 * and to display Drop down menus, selection boxes,
 * and anything else that needs to be visible on the screen
 * @author Ryan Paulitschke
 */
@SuppressWarnings("serial")
public class Screen extends JPanel {

	/*
	 * Image Object
	 * stores position x,y
	 * and it's image img
	 */
	public class iObj {
		public BufferedImage img;
		public int xx;
		public int yy;
		public int type;
		//Whether or not the image should be resized
		public boolean scale=false;
	}

	/*
	 * Text Object
	 * stores position x,y
	 * and it's Text txt
	 */
	public class tObj {
		public String txt;
		public int xx;
		public int yy;
	}
	
	public List<iObj> details = new ArrayList<>();
	public LinkedList<tObj> msgs = new LinkedList<>();

	/**
	 * Draws the image to the x,y spot. Note coordinates are top-left
	 * @param x x position of image
	 * @param y y position of image
	 * @param img img to be drawn
	 * @param id group id to assign images by (doesn't need to be unique)
	 * @return the image object to be drawn
	 */
	public iObj draw(int x, int y, BufferedImage img, int id) {
		iObj det = new iObj();
		det.xx = x;
		det.yy = y;
		det.img = img;
		det.type = id;
		details.add(det);
		this.repaint();
		return det;
	}
	
	
	/**
	 * Draws text to the x,y spot. Note coordinates are top-left 
	 * @param x x position of text
	 * @param y y position of text
	 * @param txt text to be drawn
	 * @return the text object to be drawn
	 */
	public tObj drawText(int x, int y, String txt) {
		tObj det = new tObj();
		det.xx = x;
		det.yy = y;
		det.txt = txt;
		msgs.add(det);
		this.repaint();
		return det;
	}
	

	/**
	 * Draws image centered at x,y
	 * @param x x position of image
	 * @param y y position of image
	 * @param img img to be drawn
	 * @param id group id to assign images by (doesn't need to be unique)
	 */
	public void drawCenter(int x, int y, BufferedImage img, int id) {
		draw(x - img.getWidth() / 2, y - img.getHeight() / 2, img, id);
	}


	/**
	 * Draws text at position x,y centered horizontally
	 * @param x x position of text
	 * @param y y position of text
	 * @param txt text to be drawn
	 */
	public void drawTextCenter(int x, int y, String txt) {
		drawText(x - txt.length() / 2, y, txt);
	}

	/**
	 * Creates a Drop Down Menu a x,y
	 * @param args is the options to choose from separated by a comma
	 * @return returns the drop down menu
	 */
	public JComboBox<String> createDD(int x, int y, String... args) {
		ArrayList<String> choices = new ArrayList<>();

		for (String ele : args) {
			choices.add(ele);
		}

		JComboBox<String> cOptions = new JComboBox<String>(new Vector<String>(choices));
		cOptions.setBounds(x, y-16, 128, 24);

		return cOptions;
	}
	
	/**
	 * Creates a Drop Down Menu at x,y
	 * @param arr is the list of things
	 * @return the DD menu
	 */
	public <T> JComboBox<String> createDD(int x, int y, List<T> arr) {
		ArrayList<String> choices = new ArrayList<String>();

		for (T ele : arr) {
			choices.add(ele.toString());
		}

		JComboBox<String> cOptions = new JComboBox<String>(new Vector<String>(choices));
		cOptions.setBounds(x, y-16, 128, 24);

		return cOptions;
	}
	
	/**
	 * Creates a text box at x,y 
	 * @param str  The initial text in the box
	 * @param size	The width of the text box
	 * @return the text box 
	 */
	public JTextField createTextBox(int x, int y, int size, String str) {

		JTextField txtBox = new JTextField(str, size);
		txtBox.setForeground(Color.black);
		txtBox.setSelectedTextColor(Color.black);
		txtBox.setSelectionColor(Color.orange);
		txtBox.setBackground(new Color(140,175,196));
		txtBox.setBounds(x, y-16, 96, 24);

		return txtBox;
	}
	
	/**
	 * Creates a button at x,y
	 * @param x x position
	 * @param y y position
	 * @param txt the text to be displayed on the button
	 * @return the button
	 */
	public JButton createButton(int x, int y, String txt){
		JButton but = new JButton(txt,Gfx.button);
		but.setForeground(Color.white);
		but.setFont(new Font("default", Font.BOLD, 16));
		but.setHorizontalTextPosition(JButton.CENTER);
		but.setVerticalTextPosition(JButton.CENTER);
		but.setBounds(x, y,130, 40);
		return but;
	}
		
	/**
	 * In charge of drawing to the screen.
	 */
	public void paintComponent(Graphics g) throws ConcurrentModificationException {
		super.paintComponent(g);
		
		//Draws image objects
		for (iObj det : details) {
			if (!det.scale)
			g.drawImage(det.img, det.xx, det.yy, null);
			else
			g.drawImage(det.img, det.xx, det.yy,2000,2000, null);
		}

		//draws text objects
		for (tObj det : msgs) {
			g.setColor(Color.white);
			g.setFont(new Font("default", Font.BOLD, 16));
			FontMetrics metric = g.getFontMetrics(g.getFont());
			int line=0;
			for (@SuppressWarnings("unused") String str : det.txt.split("\n"))
				line+=1;
			int yyy=det.yy;
			  for (String str : det.txt.split("\n"))
		            g.drawString(str, det.xx, (yyy += 18)-18*line - metric.getAscent()- metric.getDescent() - metric.getLeading());
		}
	}
	

	/**
	 * Creates a popup message
	 * @param title of message window
	 * @param msg to be read
	 */
	public void createMsgBox(String title, String msg){
		JOptionPane.showMessageDialog(null, msg, title, JOptionPane.INFORMATION_MESSAGE);
	}
	
	
	/**
	 * removes objects with given type(group id) value
	 * @param type
	 */
	public void remove(int type){
		details.removeIf(obj -> (obj.type==type));
	}
	

	/**
	 * Removes all images & text from the screen
	 */
	public void clear(){
		msgs.clear();
		details.clear();
	}

	/**
	 * Removes all text from the screen
	 */
	public void clearMsgs(){
		msgs.clear();
	}
	
	/**
	 * refreshes screen
	 */
	public void refresh() {
		this.repaint();
	}
}

package display;

import display.Gfx;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import nimGraph.NimGraph;
import nimGraph.NimMove;
import display.Screen.iObj;

/**
 * Window handles the game window that all GUI elements are inside of.
 * @author Ryan Paulitschke
 */
public class Window implements Runnable {

	private Screen screen = new Screen(); // Screen to draw stuff on
	private JFrame frame = new JFrame();
	
	private int selected = -1; //pile currently selected (piles 1-10: [0,9]; -1 = none)
	private int initial_value = -1; //Initial pile value before being taken from (for undoing turn)
	private int turn = 0; //[0: player 1, 1: player 2, 3: Human, 4: PC]
	private int num_piles = 0; //Number of piles
	
	private Boolean game_over = false; //Is the game over?
	
	private String last_move = ""; //last move made
	
	private NimMove m;

	@Override
	public void run() {
		// Window Properties
		createWindow(270, 600, frame, screen);
		initSetup();
	}

	/**
	 * Creates a window of the specified width and height
	 * 
	 * @param width width of window
	 * @param height height of window
	 * @param window window to be modified
	 * @param screen screen to be attached to window
	 */
	private void createWindow(int width, int height, JFrame window, Screen screen) {

		window.setTitle("NimGame - B/Ryan");
		window.setBackground(new Color(30, 30, 30));
		screen.draw(0, 0, Gfx.bg, 77).scale = true;
		window.setSize(new Dimension(width, height));
		screen.setLayout(null); // Enable Absolute Layout
		window.add(screen);
		window.setVisible(true);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	/**
	 * Updates the pile gfx
	 * @param piles array of pile amounts
	 * @param id group id of pile image objects
	 */
	private void updatePiles(int[] piles,int id){
		screen.remove(id);
		
		for (int i=0;i<num_piles;i++){
			screen.drawText((int) (48+220*Math.floor(i*0.5)), 50+250*(i%2),"# "+(i+1));
			screen.draw((int) (16+220*Math.floor(i*0.5)), 0+250*(i%2), Gfx.dots[piles[i]], 4);
		}
		screen.revalidate();
		screen.refresh();
		
	}
	
	//Creates the Game Setup GUI window
	private void initSetup() {
		JTextField[] pile = new JTextField[10];
		
		int yOffset = 40;
		int xOffset = 140;
		
		//Create pile text boxes
		for (int i=0;i<10;i++){
			screen.drawText(8, 56+yOffset*i, "Size of pile "+(i+1)+"?: ");
			pile[i] = screen.createTextBox(xOffset, 32+yOffset*i, 200, "10");
			screen.add(pile[i]);
		}

		//Game type dropdown
		screen.drawText(8, 64+yOffset*10, "Game Type?: ");
		JComboBox<String> game_type = screen.createDD(xOffset-20, 11*yOffset, "2 Player","Human vs PC");
		screen.add(game_type);
		
		//Who goes first
		screen.drawText(8, 64+yOffset*11, "Who goes first?: ");
		JComboBox<String> first_player = screen.createDD(xOffset-20, 12*yOffset, "Player 1/Human","Player 2/PC");
		screen.add(first_player);
		
		//Submit button
		JButton but = screen.createButton(60, 32+yOffset*12, "Submit");
		screen.add(but);
		
		//Amounts in the piles
		int[] pVal = new int[10];

		but.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				//Game Setup
				try {
					for (int i=0;i<10;i++){
					pVal[i] = java.lang.Integer.valueOf(pile[i].getText());	
					}
				} catch (Exception ee) {
					System.out.println("Invalid Size entered, defaulted to: 2-2-1");
				}

				for (int i=0;i<10;i++){
				if (pVal[i] < 0){
					pVal[i]=0;
					
					System.out.println("Invalid Pile "+(i+1)+", sizes must be between [0-10]");
				}
				if (pVal[i] > 10){
					pVal[i]=10;
					
					System.out.println("Invalid Pile "+(i+1)+", sizes must be between [0-10]");
				}
				}
				
				//Number of piles
				num_piles = 10;
				for (int i=0;i<10;i++){
					if (pVal[i] <= 0){
						num_piles = i;
						break;
					}
				}
				
				//makes sure unused piles are set to 0
				for (int i=num_piles;i<10;i++)
					pVal[i] = 0;
				
				//Scale window size based on # of piles
				int window_width = (int) (16+220*Math.ceil(num_piles*0.5));
				
				//Minimum window size
				if (window_width<456)
					window_width=456;
					
				frame.setSize(window_width, 660);
				screen.clear(); // removes text & images
				screen.removeAll(); // removes TextBoxes/DropDowns & Background
				
				//Pile buttons
				JButton[] pbutton = new JButton[num_piles];
				screen.draw(0, 0, Gfx.bg, 77).scale = true;
				
				//Create pile buttons
				for (int i=0;i<10;i++){
					if (num_piles<=i)
						break;
					
					screen.drawText((int) (48+220*Math.floor(i*0.5)), 50+250*(i%2),"# "+(i+1));
					
					//Take buttons
					pbutton[i] = screen.createButton((int) (48+220*Math.floor(i*0.5)), 160+250*(i%2), "Take");
					screen.add(pbutton[i]);
					
					//Take from piles
					int index = i;
					pbutton[i].addMouseListener(new MouseAdapter() {
						public void mouseReleased(MouseEvent e) {
							if(pVal[index]>0 && game_over == false){
							if (selected == index || selected == -1){
								if (turn!=3){ //Can't take on PC's turn.
								//UNDO Value
								if (initial_value==-1)
									initial_value = pVal[index];
								
								//Pile editing
								selected = index;
								screen.remove(5);
								screen.draw((int) (16+220*Math.floor(index*0.5)), 0+250*(index%2), Gfx.selected,5);
								pVal[index]-=1;
								updatePiles(pVal,4);
								}}
							}
						}
					});
					
				}
				
				//Draw piles
				updatePiles(pVal,4);
				
				//Draw Move info
				screen.draw(0,480,Gfx.info,2);
				screen.drawText(16, 560, "Last Move: "+last_move);
				
				JButton but_undo = screen.createButton(16, 560, "Undo");
				screen.add(but_undo);
				
				JButton but_end = screen.createButton(298, 560, "End Turn");
				screen.add(but_end);
				
				JButton but_restart = screen.createButton(158, 560, "Restart");
				screen.add(but_restart);
				
				//GAME TYPE
				switch (game_type.getSelectedIndex()){
				//Player 1 vs Player 2
				case 0:
					if (first_player.getSelectedIndex()==0)
						turn=0;
					else
						turn=1;
					
					screen.drawText(16, 520, "Turn: "+pTurn());
					
					System.out.println("Loaded Player vs Player");
					
					but_end.addMouseListener(new MouseAdapter() {
						public void mouseReleased(MouseEvent e) {
							//If a turn was made
							if (selected!= -1){
								last_move = "Pile "+(selected+1)+"; "+(initial_value-pVal[selected])+" Taken";
								screen.clearMsgs();
								
								//Swap turns [0-1]pl1/pl2
								if (turn==0)
									turn=1;
								else 
									turn = 0;
								
								//Check if someone won
								if (checkGameOver(pVal)){
									System.out.println("GAME OVER: "+pTurn()+" is the WINNER!");
									screen.createMsgBox("NimGame",pTurn()+" is the WINNER!\nYou may restart or continue");
									game_over = true;
								}
									
								selected = -1;
								screen.remove(5);
								initial_value=-1;
								updatePiles(pVal,4);
								
								screen.drawText(16, 520, "Turn: "+pTurn());
								screen.drawText(16, 560, "Last Move: "+last_move);
							
							}
						}
					});
					break;
				
				//Human vs PC
				case 1:
					System.out.println("Calculating possible moves...Please Wait");
					NimGraph g = new NimGraph(10,10);
					System.out.println("Loaded Human vs PC");
					if (first_player.getSelectedIndex()==0){
						turn=2;
						screen.drawText(16, 520, "Turn: "+pTurn());
					}
					else{
						turn=3;
						
						screen.drawText(16, 520, "Turn: "+pTurn());
						
						//PC's Move
						m = g.getMove(pVal);
						
						int choice =0;
						
						for (int i=0;i<pVal.length;i++){
							if (pVal[i]==m.getCurrentAmount())
								choice = i;
						}
						
						last_move = "Pile "+(choice+1)+"; "+(m.getSubtract())+" Taken";
						screen.clearMsgs();
						
						pVal[choice]-=m.getSubtract();
						updatePiles(pVal,4);
						
						turn = 2;
						
						//Check if someone won
						if (checkGameOver(pVal)){
							System.out.println("GAME OVER: "+pTurn()+" is the WINNER!");
							screen.createMsgBox("NimGame",pTurn()+" is the WINNER!\nYou may restart or continue");
							game_over = true;
						}
						
						screen.drawText(16, 520, "Turn: "+pTurn());
						screen.drawText(16, 560, "Last Move: "+last_move);
					}
					
					but_end.addMouseListener(new MouseAdapter() {
						public void mouseReleased(MouseEvent e) {
							
							//Swap turns [2-3]Human/AI
							if (turn == 2){
							//If a turn was made
							if (selected!= -1){
								last_move = "Pile "+(selected+1)+"; "+(initial_value-pVal[selected])+" Taken";
								screen.clearMsgs();
								
								selected = -1;
								screen.remove(5);
								initial_value=-1;
								updatePiles(pVal,4);
								
								screen.drawText(16, 520, "Turn: "+pTurn());
								screen.drawText(16, 560, "Last Move: "+last_move);
								
								turn = 3; //PC's turn now
								
								//Check if someone won
								if (checkGameOver(pVal)){
									System.out.println("GAME OVER: "+pTurn()+" is the WINNER!");
									screen.createMsgBox("NimGame",pTurn()+" is the WINNER!\nYou may restart or continue");
									game_over = true;
								}else{
								
								if (game_over==false){
								//PC's Move
								m = g.getMove(pVal);
								
								int choice =0;
								
								for (int i=0;i<pVal.length;i++){
									if (pVal[i]==m.getCurrentAmount())
										choice = i;
								}
								
								last_move = "Pile "+(choice+1)+"; "+(m.getSubtract())+" Taken";
								screen.clearMsgs();
								
								pVal[choice]-=m.getSubtract();
								updatePiles(pVal,4);
								
								turn = 2;
								
								//Check if someone won
								if (checkGameOver(pVal)){
									System.out.println("GAME OVER: "+pTurn()+" is the WINNER!");
									screen.createMsgBox("NimGame",pTurn()+" is the WINNER!\nYou may restart or continue");
									game_over = true;
								}
								
								screen.drawText(16, 520, "Turn: "+pTurn());
								screen.drawText(16, 560, "Last Move: "+last_move);
								}
								
								}
							}
						}}
					});
					break;
				}
				
				but_undo.addMouseListener(new MouseAdapter() {
					public void mouseReleased(MouseEvent e) {
						//undo only if a move was made
						if (selected!= -1){
						pVal[selected]=initial_value;
						selected = -1;
						screen.remove(5);
						initial_value=-1;
						updatePiles(pVal,4);
						}
					}
				});
				
				but_restart.addMouseListener(new MouseAdapter() {
					public void mouseReleased(MouseEvent e) {
						selected = -1;
						initial_value = -1;
						last_move = "";
						game_over = false;
						
						frame.setSize(270, 600);
						screen.clear();
						screen.removeAll();
						screen.draw(0, 0, Gfx.bg, 77).scale = true;
						initSetup();
					}
				});
				
				
				screen.revalidate();
				screen.refresh();
			}
		});

	}
	
	/**
	 * Returns true if game was ended by the previous move
	 * @param piles array of the size of the piles
	 * @return true if game was won
	 */
	private boolean checkGameOver(int[] piles){
		boolean success = true;
		
		//all piles are empty
		for (int i=0; i<piles.length;i++){
			if (piles[i]!=0)
				success=false;
		}
		if (success)
			return success;
		
		//three piles of 2 objects Rest empty
		int twos=0;
		int zeros=0;
		for (int i=0; i<piles.length;i++){
			if (piles[i]==2)
				twos+=1;
			
			if (piles[i]==0)
				zeros+=1;
		}
		
		if (twos==3 && zeros == (piles.length-3))
			return true;
		
		//three piles: 1,2,3 objs Rest empty
		int ones=0;
		int threes=0;
		
		for (int i=0; i<piles.length;i++){
			if (piles[i]==1)
				ones+=1;
			
			if (piles[i]==3)
				threes+=1;
		}
		
		if (ones==1 && twos==1 && threes==1 && zeros == (piles.length-3))
			return true;
		
		//two piles of 1 obj and two piles of 2 objs Rest empty
		if (ones==2 && twos==2 && zeros == (piles.length-4))
			return true;
		
		return false;
		
	}
	
	//Returns who's turn it is
	private String pTurn(){
	String pturn = "";
	
	switch (turn){
	case 0:
		pturn = "Player 1";
		break;
	case 1:
		pturn = "Player 2";
		break;
	case 2:
		pturn = "Human";
		break;
	case 3:
		pturn = "PC";
		break;
		
	}
	
	return pturn;
}
	

}
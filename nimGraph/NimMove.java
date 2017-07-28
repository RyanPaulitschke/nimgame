package nimGraph;

/**
 * 
 * @author Bryan Storie
 *
 */
public class NimMove {

	private int currentAmount = 0;
	private int subtract = 0;
	//if given this state you can win
	private boolean givenWin = false;
	
	public NimMove(int p, int a, boolean g){
		this.currentAmount = p;
		this.subtract = a;
		this.givenWin = g;
	}
	
	public void setCurrentAmount(int i){
		this.currentAmount = i;
	}
	
	public void setSubtract(int i){
		this.subtract = i;
	}
	
	public void setGivenWin(boolean b){
		this.givenWin = b;
	}
	
	/**
	 * @return The initial amount in the pile that should be removed
	 */
	public int getCurrentAmount(){
		return this.currentAmount;
	}
	
	/**
	 * @return The amount to remove
	 */
	public int getSubtract(){
		return this.subtract;
	}
	
	/**
	 * @return Whether or not a player can guarantee a win given this state
	 */
	public boolean getGivenWin(){
		return this.givenWin;
	}
}

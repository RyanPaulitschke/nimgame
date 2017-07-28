package nimGraph;

import java.util.Arrays;
import java.util.HashMap;
/**
 * 
 * @author Bryan Storie
 *
 */
public class NimGraph {
	
	//String representing state -> Move to make
	private HashMap<String,NimMove> stateToMove = new HashMap<String,NimMove>();
	private int numPiles;
	private int maxItems;
	private int[] L1,L2,L3;

	public NimGraph(int numPiles, int maxItems){
		this.numPiles = numPiles;
		this.maxItems = maxItems;
		generateMappings();
	}
	
	//assumes the given state is sorted in ascending order
	public NimMove getMove(int[] state){		
		return stateToMove.get(arrToKey(state));
	}
	
	/**
	 * Fills the stateToMove HashMap
	 */
	private void generateMappings(){
		generateLossStates();
		
		int[] vert1 = new int[numPiles];
		int[] moveVert = new int[numPiles];
		boolean added = false;
		int curPile;
		int curAmount;
						
		//add the all zeros state		
		stateToMove.put(arrToKey(vert1), new NimMove(0,0,true));
		
		//iterate across all possible states in ascending order
		while(vert1[0] < maxItems){
			incrementVertex(vert1);
			added = false;
			
			//check if vert1 is one of the preset loss conditions
			if(isLoss(vert1)){
				stateToMove.put(arrToKey(vert1), new NimMove(0,0, true));
			}
			else{			
			
				curPile = 0;
				curAmount = 0;
				moveVert = Arrays.copyOf(vert1, vert1.length);
				//move curPile to leftmost pile with something in it
				while(moveVert[curPile]==0){
					curPile++;
				}
				//need to iterate across all possible moves from vert1 until one is found that is a win
				//while we haven't filled givenWin and sentWin
				while(moveVert[moveVert.length-1]>0){
					
					//apply next move to moveVert
					if(curAmount < vert1[curPile]){
						curAmount++;
						moveVert[curPile]--;
					}
					//start on the next pile and reset the one we were looking at
					else if(curPile < moveVert.length-1){
						moveVert[curPile] = vert1[curPile];
						curPile++;
						moveVert[curPile]--;
						curAmount = 1;
					}
					
					//if a possible move leads to a given loss state then this state is given win
					if(!stateToMove.get(arrToKey(moveVert)).getGivenWin()){
						stateToMove.put(arrToKey(vert1), new NimMove(vert1[curPile], curAmount, true));
						added = true;
						break;
					}
				}	
				
				//if we reach this point without adding vert1 then it is given loss
				if(!added){
					//find first possible move and make it
					curPile = 0;
					while(vert1[curPile]==0){
						curPile++;
					}				
					stateToMove.put(arrToKey(vert1), new NimMove(moveVert[curPile], 1 ,false));
				}
			}			
		}
	}
	
	/**
	 * Generates the loss states with the correct number of piles and max items for the NimGraph.  If either of those parameters makes a loss state
	 * impossible then it is left null.
	 */
	private void generateLossStates(){
		
		L1 = null;
		L2 = null;
		L3 = null;	
		
		if(numPiles >= 3 && maxItems >= 2){
			L1 = new int[numPiles];
			L1[L1.length-3] = 2;
			L1[L1.length-2] = 2;
			L1[L1.length-1] = 2;
			if(maxItems >= 3){
				L2 = new int[numPiles];
				L2[L2.length-3] = 1;
				L2[L2.length-2] = 2;
				L2[L2.length-1] = 3;
			}
			if(numPiles >= 4){
				L3 = new int[numPiles];
				L3[L3.length-4] = 1;
				L3[L3.length-3] = 1;
				L3[L3.length-2] = 2;
				L3[L3.length-1] = 2;
			}
		}
	}
	
	/**
	 * Checks if the given state matches any of the loss states (other than all zeroes).
	 * @param state
	 * @return
	 */
	private boolean isLoss(int[] state){
		return Arrays.equals(L1, state) || Arrays.equals(L2, state) || Arrays.equals(L3, state);
	}
	
	/**
	 * Converts the given array to a string with one character per pile (using higher number base if necessary)(eg. 10 = a)
	 * @param arr
	 * @return
	 */
	private String arrToKey(int[] arr){
		
		//sort array here?
		
		int[] copy = copySort(arr);
		
		String ret = "";
		for(int i:copy){
			ret += Integer.toString(i, maxItems+1);
		}
		
		return ret;
	}
	
	/**
	 * Returns a sorted copy of the given array.
	 * @param a
	 * @return
	 */
	private int[] copySort(int[] a){
		
		int[] arr = Arrays.copyOf(a, a.length);
		
		for(int i = 1; i < arr.length; i++){
			int temp = arr[i];
			int j;
			for(j = i - 1; j>= 0 && temp < arr[j]; j--){
				arr[j+1] = arr[j];
			}
			arr[j+1] = temp;
		}
		
		return arr;
	}
	
	/**
	 * Increments the given array ensuring that all values are >= to the value to their left.  This ensures that all combinations are
	 * iterated through without any repeats.
	 * @param vert
	 */
	private void incrementVertex(int[] vert){
		int index = vert.length-1;
		//check if first pile can still be incremented
		if(vert[index]<maxItems){
			vert[index]++;
			return;
		}
		//if it can't then we perform extra steps
		else{
			//find the leftmost pile that hasn't hit max yet
			while(index >= 0 && vert[index]>=maxItems){
				index--;
			}
			//all piles are maxed so the vertex can't be incremented
			if(index == -1){
				return;
			}
			//increment the pile then set all piles to the right of it to the same value
			else{
				vert[index]++;
				int temp = vert[index];
				index++;
				while(index < vert.length){
					vert[index] = temp;
					index++;
				}
			}
		}		
	}
}

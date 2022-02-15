package supervisor.games;

import java.io.File;
import java.util.ArrayList;

import util.MethodNotImplementedException;

/**
 * A basic class to represent the agent's environment. In this 
 * simple implementation, we only have access to the environment's 
 * dimensions (if applicable) and the labels of the current state 
 * of the environment. Thus, this class is made to accommodate the 
 * simplest case of the Normative Supervisor operating on an agent 
 * in a labelled MDP.
 * 
 * Should not be modified.
 * 
 * @author emery
 *
 */

public class Environment {
	protected int gridX;
	protected int gridY;
	ArrayList<String> pos_labels = new ArrayList<String>();
	ArrayList<String> neg_labels = new ArrayList<String>();
	ArrayList<String> all_labels = new ArrayList<String>();
	
	public Environment() {
	}
	
	public Environment(int x, int y, ArrayList<String> all_labs) {
		gridX = x;
		gridY = y;
		all_labels = all_labs;
	}
	
	public Environment(File gridfile) throws MethodNotImplementedException {
		throw new MethodNotImplementedException("Not configured for a gridfile!");
	}
	
	public int[] getBoardDimensions() {
		int [] dim = {gridX, gridY};
		return dim;
	}
	
	public void setLabels(ArrayList<String> lab) {
		pos_labels = lab;
		for(String l : all_labels) {
			if(!pos_labels.contains(l)) {
				neg_labels.add(l);
			}
		}
	}
	
	public ArrayList<String> getPosLabels(){
		return pos_labels;
	}
	
	public ArrayList<String> getNegLabels(){
		return neg_labels;
	}
	
}

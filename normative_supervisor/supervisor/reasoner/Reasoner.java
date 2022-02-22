package supervisor.reasoner;

import java.util.ArrayList;
import java.util.Hashtable;

import supervisor.games.Game;
import util.ProjectUtils;

/**
 * Abstract base class for new reasoners. Do not modify.
 * 
 * @author emery
 *
 */

public abstract class Reasoner {
	ProjectUtils util = new ProjectUtils();
	Game game;
	Hashtable<String, Integer> hierarchy;
	
	public Reasoner(Game g) {
		game = g; 
	}
	
	
	/*
	 * Method to update the GameState theory. Should clear the GameState theory,
	 * call getter methods from the associated translator, and then add the 
	 * translated facts/rules to the object representing the GameState theory.
	 */
	public abstract void update();
	
	
	/*
	 * Method to run whatever reasoning engine is being utilized by the reasoner on
	 * the GameState theory. All the relevant results should be stored e.g. as in 
	 * the conclusions Map in the DDPL reasoners.
	*/
	public abstract void reason();
	
	
	/*
	 * Method to parse the conclusions yielded by reason(), and interpret the results
	 * wrt which actions are permissible and which are not. Should return an ArrayList 
	 * of Strings representing action labels.
	 */
	public abstract ArrayList<String> findCompliantActions();
	
	
	/*
	 * A method that, given an action label, determines whether or not the action is 
	 * compliant, given the conclusions from reason(). Returns true/false.
	 */
	public abstract boolean checkActionCompliance(String action);
	
	
	/*
	 * The method that is called if findCompliantActions() returns an empty ArrayList.
	 * It may or may not require the reasoning engine to be run (at least) once more.
	 * This method returns the labels of actions that are determined to be minimally 
	 * non-compliant (e.g., they break the least number of rules). The way this method 
	 * has been implemented thus far involves an evaluateAction() method that returns 
	 * scores for any actions input.
	 */
	public abstract ArrayList<String> findNCActions();
	
	
	/*
	 * A method to print the GameState theory.
	 */
	public abstract void printTheory();
    	

}

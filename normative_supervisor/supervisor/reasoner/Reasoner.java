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
	
	public abstract void update();
	
	public abstract void reason();
	
	public abstract ArrayList<String> findCompliantActions();
	
	public abstract boolean checkActionCompliance(String action);
	
	public abstract ArrayList<String> findNCActions();
	
	public abstract void printTheory();
    	

}

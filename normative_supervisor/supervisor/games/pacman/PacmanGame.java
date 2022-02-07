package supervisor.games.pacman;

import java.util.ArrayList;
import java.util.Arrays;

import supervisor.games.Game;
import supervisor.games.GameObject;
import supervisor.games.pacman.PacmanEnvironment;
import supervisor.normsys.NormBase;
import supervisor.reasoner.DDPLReasoner;
import supervisor.reasoner.DDPLReasoner2;

/**
 * Subclass of Game, adds methods specific to the Pac-Man game/Game Objects/Environment.
 * 
 * @author emery
 *
 */


public class PacmanGame extends Game{
	ArrayList<GameObject> characters = new ArrayList<GameObject>();
	boolean violated_blue;
	boolean violated_orange;
	static ArrayList<String> acts = new ArrayList<String>(Arrays.asList("North", "South", "East", "West", "Stop"));
	
	public PacmanGame(PacmanEnvironment env, NormBase gt, String rt) {
		super(env, gt, rt, acts);
		characters.add(env.getPacman());
		characters.addAll(env.getGhosts());
		violated_blue = false;
		violated_orange = false;
	}
	
	@Override
	public void init() {
		if(reasonerType.equals("DDPL")) {
			translator = new PacmanToDDPL(normBase);
			reasoner = new DDPLReasoner(this);
			translator.init(environment, actions);
		}
		else if(reasonerType.equals("DDPL2")) {
			translator = new PacmanToDDPL2(normBase);
			reasoner = new DDPLReasoner2(this, true);
			translator.init(environment, actions);
		}
		
		
	}
	
	public ArrayList<String> getActions() {
	    	return actions;
	    }    
	    
	public ArrayList<GameObject> getCharacters() {
	    	return characters;
	    }
	
	public boolean hasBlueViolated() {
			return violated_blue;
		}
		
	public void recBlueViolation() {
			violated_blue = true;
		}
		
    public boolean hasOrangeViolated() {
			return violated_orange;
		}
		
	public void recOrangeViolation() {
			violated_orange = true;
		}
		
	public void clViolations() {
			violated_blue = false;
			violated_orange = false;
		}



}

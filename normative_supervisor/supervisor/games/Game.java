package supervisor.games;

import java.util.ArrayList;
import java.util.Arrays;

import supervisor.normsys.DDPLTranslator;
import supervisor.normsys.DDPLTranslator2;
import supervisor.normsys.NormBase;
import supervisor.normsys.Translator;
import supervisor.reasoner.DDPLReasoner;
import supervisor.reasoner.DDPLReasoner2;
import supervisor.reasoner.Reasoner;
import util.ProjectUtils;

/**
 * The Game class that links the environment, the norm base, the translator, and the reasoner.
 * 
 * The init() - and nothing else! - method must be modified if a new reasoner is implemented.
 * 
 * @author emery
 *
 */

public class Game {
	protected String reasonerType;
	protected String gameType;
	ProjectUtils util = new ProjectUtils();
	protected ArrayList<String> actions;
	ArrayList<String> possibleActions = new ArrayList<String>();
	protected NormBase normBase;
	protected Reasoner reasoner;
	protected Environment environment;
	protected Translator translator;
	
	
	public Game(Environment env, NormBase nb, String rt, ArrayList<String> acts) {
		reasonerType = rt;
		normBase = nb;
		environment = env;
		actions = acts;
	}
	
	public void init() {
		if(reasonerType.equals("DDPL")) {
			translator = new DDPLTranslator(normBase);
			reasoner = new DDPLReasoner(this);
			translator.init(environment, actions);
		}
		else if(reasonerType.equals("DDPL2")) {
			translator = new DDPLTranslator2(normBase);
			reasoner = new DDPLReasoner2(this, false);
			translator.init(environment, actions);
		}
		//add new reasoners here
		
	}
	
	public void update(Environment env, ArrayList<String> possible) {
		environment = env;
		setPossibleActions(possible);
		translator.update(env, possible, this);
		reasoner.update();
	}
	
	
    public ArrayList<String> findCompliantActions() {
    	ArrayList<String> compl = new ArrayList<String>();
    	compl.addAll(reasoner.findCompliantActions());
    	return compl;
    }  
    
    public void reason() {
    	reasoner.reason();
    }
    
    public ArrayList<String> getActions(){
    	return actions;
    }
    
    public ArrayList<String> getPossibleActions(){
    	return possibleActions;
    }
    
    public void setPossibleActions(ArrayList<String> pa){
    	possibleActions = pa;
    }

	public ArrayList<String> findBestNCActions() {
    	return reasoner.findNCActions();
	}
	
	public boolean checkAction(String action) {
		return reasoner.checkActionCompliance(action);
	}
	
	public NormBase getNormBase() {
		return normBase;
	}
	
	public Environment getEnvironment() {
		return environment;
	}
	
	public String getGameType() {
		return gameType;
	}
	
	public Translator getTranslator() {
		return translator;
	}
	
	public void printStateTheory() {
		reasoner.printTheory();
	}


}

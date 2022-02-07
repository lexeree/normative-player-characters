package supervisor.normsys;

import java.util.ArrayList;

import supervisor.games.Environment;
import supervisor.games.Game;

/**
 * Abstract class for translators. Contains the bare minimum.
 * 
 * @author emery
 *
 */

public abstract class Translator {
	protected NormBase normBase;

	public Translator(NormBase nb) {
		normBase = nb;
	}
	
	public abstract void init(Environment env, ArrayList<String> actions);
	public abstract void update(Environment env, ArrayList<String> possible, Game game);
	public abstract void generateActions(ArrayList<String> actions);


}

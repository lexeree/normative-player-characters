package supervisor.games.pacman;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import supervisor.games.Game;
import supervisor.games.pacman.PacmanEnvironment;
import supervisor.games.pacman.PacmanGame;
import supervisor.normsys.NormBase;
import supervisor.server.NormativeSupervisor;

/**
 * Custom Supervisor for the Pac-Man game.
 * 
 * @author emery
 *
 */

public class PacmanSupervisor extends NormativeSupervisor {

		public PacmanSupervisor(String json) {
			super(json);
			
		}
		
		@Override
		public void update(String json) {		
			current = json;
			JSONObject q = new JSONObject(json);
			state = q.getJSONObject("game");
			type = q.getString("request");
			possible.clear();
			if(type.equals("FILTER")) {
				JSONArray p = q.getJSONArray("possible");
				for(int i = 0; i < p.length(); i++) {
		    		possible.add(p.getString(i));
		    	}
			}
			else if(type.equals("EVALUATION")) {
				eval = q.getString("action");
				possible.add(q.getString("action"));
			}
			int d = q.getInt("id");
			if(d != id) {
				id = d;
				game = createGame();
				game.init();
			}
			game.update(parseGame(), possible);
			if(state.getInt("blue_eaten") > 0) {
				((PacmanGame)game).recBlueViolation();
			}
			if(state.getInt("orange_eaten") > 0) {
				((PacmanGame)game).recOrangeViolation();
			}
		}
		
	@Override
	public PacmanEnvironment parseGame() {
	    	ArrayList<PacmanGameObject> walls = new ArrayList<PacmanGameObject>();
	    	ArrayList<PacmanGameObject> food = new ArrayList<PacmanGameObject>();
	    	PacmanGameObject pacman = new PacmanGameObject();
	    	PacmanGameObject bGhost = new PacmanGameObject();
	    	PacmanGameObject gGhost = new PacmanGameObject();
	    	PacmanGameObject oGhost = new PacmanGameObject();
	    	ArrayList<PacmanGameObject> characters = new ArrayList<PacmanGameObject>();
	    	JSONArray layout = state.getJSONArray("layout");
	    	for(int i=0; i < layout.length(); i++) {
	    		try {
	    		if (layout.getJSONObject(i).getString("type").contains("p")) {
	    			pacman.setType("p");
	    			pacman.setCoordX(layout.getJSONObject(i).getJSONObject("position").getFloat("x"));
	    			pacman.setCoordY(layout.getJSONObject(i).getJSONObject("position").getFloat("y"));
	    			characters.add(pacman);
	    		}
	    		else if (layout.getJSONObject(i).getString("type").contains("b")) {
	    			bGhost.setType("b");
	    			bGhost.setCoordX(layout.getJSONObject(i).getJSONObject("position").getFloat("x"));
	    			bGhost.setCoordY(layout.getJSONObject(i).getJSONObject("position").getFloat("y"));
	    			if(layout.getJSONObject(i).getString("type").contains("sc")) {
	    				bGhost.scared();
	    			}
	    			characters.add(bGhost);
	    		}
	    		else if (layout.getJSONObject(i).getString("type").contains("g")) {
	    			gGhost.setType("g");
	    			gGhost.setCoordX(layout.getJSONObject(i).getJSONObject("position").getFloat("x"));
	    			gGhost.setCoordY(layout.getJSONObject(i).getJSONObject("position").getFloat("y"));
	    			if(layout.getJSONObject(i).getString("type").contains("sc")) {
	    				gGhost.scared();
	    			}
	    			characters.add(gGhost);
	    		}
	    		else if (layout.getJSONObject(i).getString("type").contains("o")) {
	    			oGhost.setType("o");
	    			oGhost.setCoordX(layout.getJSONObject(i).getJSONObject("position").getFloat("x"));
	    			oGhost.setCoordY(layout.getJSONObject(i).getJSONObject("position").getFloat("y"));
	    			if(layout.getJSONObject(i).getString("type").contains("sc")) {
	    				oGhost.scared();
	    			}
	    			characters.add(oGhost);
	    		}
	    		}
	    		catch(NullPointerException e) {
	    			e.printStackTrace();
	    		}
	    	}
	    	PacmanEnvironment gb = new PacmanEnvironment(Math.round(state.getJSONObject("dimension").getInt("x")), 
	    			Math.round(state.getJSONObject("dimension").getInt("y")), 
	    			walls, food, characters);
	    	return gb;
	    }
	
	@Override 
	public Game createGame() {
    	Game game;
    	NormBase nb = util.defaultPacmanNormBase(normBaseType);
    	game = new PacmanGame(parseGame(), nb, reasonType);
    	return game;
    }


}

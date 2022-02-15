package supervisor.server;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
//import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

import supervisor.games.Game;
import supervisor.normsys.NormBase;
import supervisor.games.Environment;
import util.ProjectUtils;

/**
 * The Normative Supervisor class initiates, updates, and fetches output for the module.
 * 
 * Should not be modified unless a bug is found; should not be extended unless absolutely necessary.
 * 
 * @author emery
 *
 */

public class NormativeSupervisor {
	protected Game game;
	protected int id;
	protected String type;
	protected String current;
	protected String reasonType;
	protected String normBaseType;
	protected String eval;
	protected ArrayList<String> possible;
	protected ArrayList<String> labels;
	protected ProjectUtils util = new ProjectUtils();
	protected String name;
	protected JSONObject state;
	
	

	public NormativeSupervisor(String json) {
		current = json;
		JSONObject q = new JSONObject(json);
		possible = new ArrayList<String>();
		labels = new ArrayList<String>();
		if(q.has("game")) {
			state = q.getJSONObject("game");
		}
		eval = "";
		try {
		    name = q.getString("name");
		} catch(NullPointerException e){
			e.printStackTrace();
		}
    	reasonType = q.getString("reasoner");
    	normBaseType = q.getString("norms");
    	id = q.getInt("id");
    	game = createGame();
    	game.init();
	}
	
	public void update(String json) {
		current = json;
		JSONObject q = new JSONObject(json);
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
		labels.clear();
		JSONArray l = q.getJSONArray("labels");
		for(int i = 0; i < l.length(); i++) {
    		labels.add(l.getString(i));
    	}
		game.update(parseGame(), possible);
	}
	
	
	public JSONObject fullfillRequest() {
		JSONObject response = new JSONObject();
		
		if(type.equals("FILTER")) {
            game.reason();
            ArrayList<String> actions = game.findCompliantActions();
		        boolean compl = true;
		        if (actions.isEmpty()) {
		            System.out.println("No compliant actions. Locating maximally compliant action...");
		            actions = game.findBestNCActions();
		            printViolationFiles(possible, actions);
		            compl = false;
		          }
		        response = createFilterResponse(actions, compl);
		        actions.clear();
		}
		else if(type.equals("EVALUATION")) {
			game.reason();
			boolean compl = game.checkAction(eval);
			response = createEvalResponse(compl);
		}
		return response;
	}
	
	
	public JSONObject createFilterResponse(List<String> actions, boolean compl){
		JSONObject response = new JSONObject();
		response.put("response", "RECOMMENDATION");
		JSONArray acts = new JSONArray(actions);
		response.put("actions", acts);
		response.put("compliant", compl);
    	return response;
    }
	
	
	public JSONObject createEvalResponse(boolean compl){
		JSONObject response = new JSONObject();
		response.put("response", "EVALUATION");
		response.put("compliant", compl);
    	return response;
    }
    
    
    public void printViolationFiles(ArrayList<String> possible, ArrayList<String> non) {
    	DateFormat df = new SimpleDateFormat("yyMMddHHmmss");
		Date date = new Date();
		String datestr = df.format(date);
    	PrintWriter pw;
		try {
			pw = new PrintWriter("violation_"+datestr+".txt");
			pw.println("Game #: "+Integer.toString(id));
			pw.println("----- VIOLATION -----");
			pw.println("possible actions: "+possible.toString());
			pw.println("no compliant actions found");
			pw.println("minimally non-compliant: "+non.toString());
			pw.println("----- CONTEXT -----");
			pw.println(current);
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
    }
		
	
	public Environment parseGame() {
		Environment env = new Environment(0,0, util.getAllLabels(name));
		env.setLabels(labels);
		return env;
	}

	
	public Game createGame() {
		ArrayList<String> acts = util.getActionList(name);
		NormBase nb = util.defaultNormBase(name, normBaseType);
    	Game gm = new Game(parseGame(), nb, reasonType, acts);
    	return gm;
    }
	
	public Game getGame() {
		return game;
	}

}

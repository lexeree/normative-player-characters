package supervisor.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;
import org.json.JSONObject;

import supervisor.games.pacman.PacmanSupervisor;
import util.ProjectUtils;

/**
 * The Normative Module Server is the interface through which the Normative 
 * Supervisor is connected to a game. 
 * 
 * Will need to be modified if a new custom Supervisor is implemented 
 * (let's try not to do that). 
 * 
 * @author emery
 *
 */

public class NormativeModuleServer {
	
    int port;
    private ServerSocket server;
    ProjectUtils util = new ProjectUtils();
    NormativeSupervisor filter;
    static Logger log = Logger.getLogger(NormativeSupervisor.class.getName());

    public NormativeModuleServer(int port) {
    	
        try {
        	this.port = port;
            server = new ServerSocket(this.port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
    public void run(String init) {
        boolean running = true;
        try {
        	JSONObject q = new JSONObject(init);
        	String n = q.getString("name");
        	//This is where you would add a new supervisor for a new game.
        	if(n.equals("pacman")) {
        		filter = new PacmanSupervisor(init);
        	}
        	else {
        	    filter = new NormativeSupervisor(init);
        	}
            System.out.println("Waiting for connection to port "+this.port);
            Socket socket = server.accept();
            System.out.println("Connection established. Waiting for queries...");
            OutputStream os = socket.getOutputStream();
            InputStream is = socket.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            while(running) {
            	String msg = br.readLine();
            	running = fillRequest(msg, os);
            }
            System.out.println("Terminating Normative Module Server...");
            br.close();
            os.close();
            is.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
    public boolean fillRequest(String msg, OutputStream os) {
    	PrintWriter out = new PrintWriter(os, true);
    	JSONObject q = new JSONObject(msg);
    	if(q.getString("request").contains("TERMINATION")) {
    		return false;
    	}
    	filter.update(msg);
    	JSONObject response = filter.fullfillRequest();
    	String json = response.toString();
		out.println(json);
	    out.flush();
    	return true;
    }
    
}


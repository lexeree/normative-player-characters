package supervisor;

import supervisor.server.NormativeModuleServer;
import util.ProjectUtils;

/**
 * To initiate the Normative Module Server with the default starting 
 * state. Two arguments are needed: the name of the game being modeled, 
 * and the name of the norm base.
 * 
 * To run, we need 4 arguments: the name of the game, the name of the 
 * norm base, the name of the reasoner, and the port to connect to.
 * 
 * Unless a bug is found, please do not alter this class.
 * 
 * @author emery
 *
 */


public class run {

	public static void main(String[] args)  {
		ProjectUtils util = new ProjectUtils();
		String game = args[0];
		String nb = args[1];
		String reasoner = args[2];
		int port = Integer.valueOf(args[3]);
		String json = util.defaultState(game, nb, reasoner);
		NormativeModuleServer server = new NormativeModuleServer(port);
	    server.run(json);
	}

}

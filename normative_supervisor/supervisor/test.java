package supervisor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.JSONObject;
import supervisor.games.pacman.PacmanSupervisor;
import supervisor.server.NormativeSupervisor;

/**
 * A class to facilitate the evaluation of a single state.
 * 
 * Should be changed with the introduction of a new Supervisor subclass.
 * 
 * @author emery
 *
 */

public class test {

	public static void main(String[] args) throws IOException  {
		String path = args[0];
		String msg = new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
		JSONObject q = new JSONObject(msg);
		NormativeSupervisor nf;
		if(q.getString("name").equals("pacman")) {
			nf = new PacmanSupervisor(msg);
		}
		else {
			nf = new NormativeSupervisor(msg);
		}
		nf.update(msg);
		nf.getGame().printStateTheory();
		JSONObject response = nf.fullfillRequest();
		System.out.println(response.toString());

	}

}

package synthesis;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import spindle.core.dom.RuleException;
import supervisor.normsys.NormBase;
import util.ProjectUtils;

public class synthesize {

	public static void main(String[] args) throws IOException, RuleException {
		ProjectUtils util = new ProjectUtils();
        String path = args[0];
        String text = new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
        JSONObject obj = new JSONObject(text);
		String name = obj.getString("normsys");
		JSONArray ap = obj.getJSONArray("AP");
		ArrayList<String> aps = new ArrayList<String>();
		for (Object a : ap) {
			String p = (String) a;
			aps.add(p);
		}
		JSONArray trans = obj.getJSONArray("transitions");
		ArrayList<Transition> tr = new ArrayList<Transition>();
		for (Object a : trans) {
			JSONObject act = (JSONObject) a;
			Transition t = new Transition(act.getString("action"), act.getString("initial"), act.getString("next"));
			tr.add(t);
		}
		NormBase nb = util.defaultNormSys("merchant", name); 
		Synthesizer synth = new Synthesizer(nb, aps, tr);
		synth.findBadStates();
		synth.printBadStates();

	}

}

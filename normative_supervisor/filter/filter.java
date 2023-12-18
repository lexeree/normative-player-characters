package filter;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import spindle.core.dom.RuleException;
import supervisor.normsys.NormBase;
import util.ProjectUtils;

public class filter {

	public static void main(String[] args) {
		int port = Integer.parseInt(args[0]);
		try {
            ServerSocket server = new ServerSocket(port);
            System.out.println("Waiting for connection to port "+port);
            Socket socket = server.accept();
            System.out.println("Connection established. Waiting for queries...");
            OutputStream os = socket.getOutputStream();
            InputStream is = socket.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String msg = br.readLine();
            PrintWriter out = new PrintWriter(os, true);
            JSONObject q = new JSONObject(msg);
            String norm = q.getString("norms");
            String game = q.getString("name");
            JSONArray labs = q.getJSONArray("labels");
            ArrayList<String> labels = new ArrayList<String>();
            for(int i=0; i<labs.length(); i++) {
            	labels.add(labs.getString(i));
            }
            JSONArray acts = q.getJSONArray("actions");
            ArrayList<String> actions = new ArrayList<String>();
            for(int i=0; i<acts.length(); i++) {
            	actions.add(acts.getString(i));
            }
            ProjectUtils util = new ProjectUtils();
    		NormBase nb = util.defaultNormBase(game, norm);
    		
    		NormativeFilter nf = new NormativeFilter(nb, labels, actions);
    		try{
    			nf.constructFilter();
    		} catch(RuleException e) {
    			e.printStackTrace();
    		}
    		JSONArray response = nf.formatFilter();
    		out.println(response);
    	    out.flush();
            System.out.println("Terminating Normative Module Server...");
            br.close();
            os.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

	}

}

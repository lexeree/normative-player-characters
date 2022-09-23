package synthesis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import spindle.core.dom.Conclusion;
import spindle.core.dom.ConclusionType;
import spindle.core.dom.Literal;
import spindle.core.dom.Mode;
import spindle.core.dom.Rule;
import spindle.core.dom.RuleException;
import spindle.core.dom.RuleType;
import spindle.core.dom.Theory;
import supervisor.games.Environment;
import supervisor.normsys.*;
import supervisor.reasoner.DDPLReasonerCore;
import util.ProjectUtils;

public class Synthesizer {
	Mode obl = new Mode("O", false);
	Mode perm = new Mode("P", false);
	ProjectUtils util = new ProjectUtils();
	NormBase NS;
	ArrayList<String> AP;
	ArrayList<Transition> transitions;
	DDPLReasonerCore reasoner = new DDPLReasonerCore();
	Environment env;
	DDPLTranslator2 translator;
	ArrayList<ArrayList<String>> badStates = new ArrayList<ArrayList<String>>();
	HashMap<ArrayList<String>, String> mandatory = new HashMap<ArrayList<String>, String>();
	HashMap<ArrayList<String>, String> prohibited = new HashMap<ArrayList<String>, String>();
	
	
	public Synthesizer(NormBase nb, ArrayList<String> ap, ArrayList<Transition> trans) {
		NS = nb;
		translator = new DDPLTranslator2(NS);
		AP = ap;
		transitions = trans;
		env = new Environment();
		env.configureLabels(AP);
		ArrayList<String> acts = new ArrayList<String>();
		for (Transition t : transitions) {
			acts.add(t.getAction());
		}
	}
	
	
	public ArrayList<ArrayList<String>> getSubsets(){
		int allMasks = 1 << AP.size();
	    ArrayList<ArrayList<String>> output = new ArrayList<ArrayList<String>>();
		for(int i=0;i<allMasks;i++) {
	        ArrayList<String> sub = new ArrayList<String>();
	        for(int j=0;j<AP.size();j++) {
	            if((i & (1 << j)) > 0) {
	            	boolean doubled = false;
	            	for(String s : sub) {
	            		if(s.substring(0,s.indexOf("_")).equals("at") || s.substring(0,s.indexOf("_")).equals("north") ||
	            				s.substring(0,s.indexOf("_")).equals("south") || s.substring(0,s.indexOf("_")).equals("east") ||
	            				s.substring(0,s.indexOf("_")).equals("west")) {
	            	    if(s.substring(0,s.indexOf("_")).equals(AP.get(j).substring(0,AP.get(j).indexOf("_")))){
	            	    	doubled = true;
	            	    } }
	            	}
	            	if(!doubled) {
	            		sub.add(AP.get(j));
	            	}
	            }
	        }
	        output.add(sub);
	    }
		return output;
	}
	
	public void updateReasoner(ArrayList<String> set) {
		env.clear();
		translator.clear();
		env.setLabels(set);
		translator.synth_update(env);
		reasoner.getTheory().clear();
		ArrayList<Rule> facts = translator.getFacts();
		ArrayList<Rule> rules = translator.getRules();
		ArrayList<Rule> norms = translator.getNorms();
		ArrayList<Rule> all = new ArrayList<Rule>();
		all.addAll(rules);
		all.addAll(norms);
		reasoner.addFacts(facts);
		reasoner.addRules(all);
		reasoner.addHierarchy(translator.getHierarchy());
	}
	
	public boolean checkCompliance() {
		Theory copy = reasoner.getTheory().clone();
		Map<Literal, Map<ConclusionType, Conclusion>> conclusions = reasoner.drawConclusions()
				.entrySet().stream()
				.filter(p -> util.checkForLit(copy.getAllLiteralsInRules(), p.getKey().getName()))
				.collect(Collectors.toMap(map -> map.getKey(), map -> map.getValue()));
		Collection<Map<ConclusionType, Conclusion>> concl = conclusions.values();
		concl.removeIf(p -> !p.keySet().contains(ConclusionType.DEFEASIBLY_PROVABLE));
		Map<Literal, Map<ConclusionType, Conclusion>> obls = conclusions
				.entrySet().stream()
				.filter(p -> p.getKey().getMode().equals(obl))
				.filter(p -> concl.contains(p.getValue()))
				.collect(Collectors.toMap(map -> map.getKey(), map -> map.getValue()));
		Map<Literal, Map<ConclusionType, Conclusion>> bes = conclusions
				.entrySet().stream()
				.filter(p -> !p.getKey().getMode().equals(obl) )
				.filter(p -> concl.contains(p.getValue()))
				.collect(Collectors.toMap(map -> map.getKey(), map -> map.getValue()));
		obls.keySet().forEach(p -> p.removeMode());
		return bes.keySet().containsAll(obls.keySet());
	}
	
	public void findBadStates() throws RuleException {
		ArrayList<ArrayList<String>> subsets = getSubsets();
		for (ArrayList<String> gamma : subsets) {
			updateReasoner(gamma);
			if(!checkCompliance()) {
				badStates.add(gamma);
			}
			for(String a : util.getActionList("merchant")) {
				Literal act = new Literal(a);
				Rule fact = new Rule(a, RuleType.DEFEASIBLE);
				fact.addHeadLiteral(act);
				ArrayList<Rule> f = new ArrayList<Rule>();
				f.add(fact);
				updateReasoner(gamma);
				reasoner.addFacts(f);
				//MAKE SURE INIT AND NEXT ARE COMPATIBLE WITH GAMMA
				if(checkCompliance()) {
					if (badStates.contains(gamma)){
						badStates.remove(gamma);
						for(Transition t : transitions) {
							if(t.getAction().equals(a)) {
								mandatory.put(gamma, t.getTransition());
							}
						}
					}
				}
				else {
					if (!badStates.contains(gamma)){
						for(Transition t : transitions) {
							if(t.getAction().equals(a)) {
								prohibited.put(gamma, t.getTransition());
							}
						}
					}
				}
			}
		}
	}
	
	
	
	public void printBadStates() {
		System.out.println("Bad states: " + badStates.toString());
		System.out.println("Mandatory acts: "+mandatory.toString());
		System.out.println("Prohibited acts: "+prohibited.toString());
	}
	

}

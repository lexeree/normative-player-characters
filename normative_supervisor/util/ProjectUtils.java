package util;

import util.ObjectNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import spindle.core.dom.Literal;
import spindle.core.dom.Rule;
import spindle.core.dom.Theory;
import spindle.core.dom.TheoryException;
import supervisor.games.merchant.MerchantNormBase;
import supervisor.games.pacman.PacmanNormBase;
import supervisor.normsys.Modality;
import supervisor.normsys.NormBase;
import supervisor.normsys.RegulativeNorm;
import supervisor.normsys.Term;

/**
 * Helpful methods + configuration methods.
 * 
 * If implementing a new reasoner, feel free to create any helper functions you need
 * here. 
 * 
 * If implementing a new game, make sure to modify:
 * - getActionList  
 * - defaultNormBase
 * 
 * @author emery
 *
 */

public class ProjectUtils {
	
	public ProjectUtils() { }
	
	public boolean litCalled(Literal lit, String name) {
		return lit.getName().equals(name);
	}
	
	
	public Literal getLit(List<Literal> lits, String name) throws ObjectNotFoundException {
		for(Literal lit : lits) {
			if(litCalled(lit, name)) {
				return lit;
			}
		}
		throw new ObjectNotFoundException("No matching literal!");
	}
	
	public boolean checkForLit(Set<Literal> lits, String name){
		boolean check = false;
		for(Literal lit : lits) {
			if(litCalled(lit, name)) {
				check = true;
			}
		}
		return check;
	}
	
	
	public Rule getMatchedRule(List<Rule> rules, String label) throws ObjectNotFoundException {
		for(Rule rule : rules) {
			if(rule.getLabel().equals(label)) {
				return rule;
			}
		}
		throw new ObjectNotFoundException("No matching literal!");
	}
	
	
	public void addRulesToTheory(List<Rule> rules, Theory theory) throws TheoryException {
		Set<String> remove = Collections.emptySet();
		Map<String, List<Rule>> map = Collections.emptyMap();
		theory.updateTheory(rules, remove, map);
	}
	
	
	public ArrayList<String> getRuleLabels(Collection<Rule> list) {
		ArrayList<String> labels = new ArrayList<String>();
		for(Rule r : list) {
			labels.add(r.getLabel());
		}
		return labels;
	}
	
	
	public Set<Literal> getFactLits(List<Rule> list) {
		Set<Literal> lits = new HashSet<Literal>();
		for(Rule r : list) {
			lits.addAll(r.getHeadLiterals());
		}
		return lits;
	}
	
	public Set<String> getLitNames(Collection<Literal> list) {
		Set<String> lits = new HashSet<String>();
		for(Literal l : list) {
			lits.add(l.getName());
		}
		return lits;
	}
	
	
	
	public boolean containsSome(String test, List<String> liststr) {
		for(String str : liststr) {
			if(test.contains(str)) {
				return true;
			}
		}
		return false;
	}
	
	public String defaultState(String game, String nb, String reasoner) {
		String str = "";
		if(game.equals("pacman")) {
			str = "{\"name\": \"pacman\", \"norms\": \""+nb+"\", \"reasoner\": \""+reasoner+"\", \"id\": 0, " +
				      "\"game\": {\"blue_eaten\": 0, \"orange_eaten\": 0, \"layout\": "+
					"[{\"position\": {\"y\": 1.0, \"x\": 9.0}, \"type\": \"p\"}, "+
					"{\"position\": {\"y\": 5.0, \"x\": 8.0}, \"type\": \"b\"}, "+
					"{\"position\": {\"y\": 5.0, \"x\": 11.0}, \"type\": \"o\"}],"+
					"\"dimension\": {\"y\": 11.0, \"x\": 20.0}}}";
		}
		else if(game.equals("merchant")) {
			str = "{\"name\": \"merchant\", \"norms\": \""+nb+"\", \"reasoner\": \""+reasoner+
					"\", \"id\": 0, " +"\"labels\": []}";
		}
		return str;
	}
	
	public ArrayList<String> getDirs(){
		ArrayList<String> dirs = new ArrayList<String>(Arrays.asList("North", "South", "East", "West"));
		return dirs;
	}
	
	public ArrayList<String> getAllLabels(String game){
		ArrayList<String> all = new ArrayList<String>();
		if(game.equals("merchant")) {
			ArrayList<String> cells = new ArrayList<String>(Arrays.asList("wood", "ore", "tree", "rock", "danger"));
			for(String c : cells) {
				all.add("at_"+c);
			}
			for(String dir : getDirs()) {
				for(String c : cells) {
					all.add(dir+"_"+c);
				}
			}
			all.add("has_wood");
			all.add("has_ore");
			all.add("attacked");
		}
		return all;
	}
	
	public NormBase defaultNormBase(String game, String type) {
		NormBase nb;
		if(game.equals("merchant")) {
			nb = new MerchantNormBase(type);
			if (type.equals("pacifist")) {
				((MerchantNormBase) nb).generatePacifist();
			}
			else if (type.equals("pacifist-weak")) {
				((MerchantNormBase) nb).weakPacifist();
			}
			else if (type.equals("green1")) {
				((MerchantNormBase) nb).generateGreen1();
			}
			else if (type.equals("green2")) {
				((MerchantNormBase) nb).generateGreen2();
			}
		}
		else {
			nb = new NormBase(type);
		}
		
		
		return nb;
	}
	
	public NormBase defaultNormSys(String game, String type) {
		NormBase nb;
		if(game.equals("merchant")) {
			nb = new MerchantNormBase(type);
			if (type.equals("pacifist")) {
				((MerchantNormBase) nb).directPacifist();
			}
			else if (type.equals("pacifist-weak")) {
				((MerchantNormBase) nb).weakPacifist();
			}
			else if (type.equals("green1")) {
				((MerchantNormBase) nb).generateGreen1();
			}
			else if (type.equals("green2")) {
				((MerchantNormBase) nb).generateGreen2();
			}
		}
		else {
			nb = new NormBase(type);
		}
		
		
		return nb;
	}
	
	
	public NormBase defaultPacmanNormBase(String type) {
		PacmanNormBase nb = new PacmanNormBase(type);
		ArrayList<Float[]> danger = new ArrayList<Float[]>();
		danger.add(new Float[] {(float) 8.0, (float) 5.0});
		danger.add(new Float[] {(float) 11.0, (float) 5.0});
		try {
		if(type.equals("broken")) {
			Term east = new Term("East", false, false, true);
			east.setMode(Modality.PROHIBITION);
			ArrayList<Term> context = new ArrayList<Term>();
			RegulativeNorm broke;
		    broke = new RegulativeNorm("-moveEast", context, east);
			nb.addRegulativeNorm(broke);
		}
		else if(type.equals("busy")) {
			nb.generateMoveAction();
		}
        else if(type.equals("hungry")) {
			nb.eatFood();
		}
        else if(type.equals("vegan")) {
        	nb.vegan("bGhost");
        	nb.vegan("oGhost");
        }
        else if(type.equals("unfair-vegan")) {
        	nb.vegan("bGhost");
        	nb.vegan("oGhost");
        	nb.prefVegan("bGhost");
        }
        else if(type.equals("safe-vegan")) {
        	nb.vegan("bGhost");
        	nb.vegan("oGhost");
        	nb.danger(danger, "bGhost");
        	nb.danger(danger, "oGhost");
        }
        else if(type.equals("safe-small")) {
        	danger.add(new Float[] {(float) 5.0, (float) 3.0});
        	nb.vegan("bGhost");
        	nb.danger(danger, "bGhost");
        }
        else if(type.equals("vegetarian")) {
        	nb.vegan("bGhost");
        }
        else if(type.equals("safe-vegetarian")) {
        	danger.add(new Float[] {(float) 1.0, (float) 9.0});
    		danger.add(new Float[] {(float) 18.0, (float) 1.0});
        	nb.vegan("bGhost");
        	nb.danger(danger, "bGhost");
        }
        else if(type.equals("hungry-vegetarian")) {
        	nb.vegan("bGhost");
        	nb.vegan("oGhost");
        	nb.eat("oGhost");
        }
        else if(type.equals("give-up-vegan")) {
        	nb.vegan("bGhost");
        	nb.vegan("oGhost");
        	nb.giveUp("bGhost", "violated");
        	nb.giveUp("oGhost", "violated");
        }
        else if(type.equals("switch-vegan")) {
        	nb.vegan("bGhost");
        	nb.vegan("oGhost");
        	nb.giveUp("bGhost", "violated(blue)");
        	nb.giveUp("oGhost", "violated(orange)");
        }
        else if(type.equals("passive-vegan")) {
        	nb.vegan("bGhost");
        	nb.vegan("oGhost");
        	nb.passive("bGhost");
        	nb.passive("oGhost");
        }
        else if(type.equals("cautious-vegan")) {
        	danger.add(new Float[] {(float) 1.0, (float) 9.0});
    		danger.add(new Float[] {(float) 18.0, (float) 1.0});
    		nb.superDanger(danger);
        }
        else if(type.equals("over-cautious")) {
        	for(int i=7; i<11; i++) {
        		for(int j=1; j<5; j++) {
        		    danger.add(new Float[] {(float) j, (float) i});
        	    }
            }
        	for(int i=15; i<19; i++) {
        		for(int j=1; j<4; j++) {
        		    danger.add(new Float[] {(float) i, (float) j});
        	    }
            }
        	nb.superDanger(danger);
          }
        else if(type.equals("benevolent")) {
        	nb.benevolent();
        }
        else if(type.equals("partial-benevolent")) {
        	nb.benevolent();
        	nb.deleteNorm("oGhost(ghost)");
        }
        else if(type.equals("permit-benevolent")) {
        	nb.benevolent();
        	nb.permit("bGhost");
        }
        else if(type.equals("passive-benevolent")) {
        	nb.benevolent();
        	nb.passive("bGhost");
        	nb.passive("oGhost");
        }
        } catch (Exception e) {
			e.printStackTrace();
		}
		
		return nb;
	}
	
	public ArrayList<String> getActionList(String name){
		ArrayList<String> acts;
		if(name.equals("pacman")) {
			acts = new ArrayList<String>(Arrays.asList("North", "South", "East", "West", "Stop"));
		}
		else if(name.equals("merchant")) {
			acts = new ArrayList<String>(Arrays.asList("North", "South", "East", "West", "Extract", "Pickup", "Unload", "Fight"));
		}
		else {
			acts = new ArrayList<String>();
		}
		return acts;
	}


}

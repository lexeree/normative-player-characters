package supervisor.normsys;

import java.util.ArrayList;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import supervisor.games.pacman.PacmanGameObject;


/**
 * Class defining a Term object; which can be (negated) literals or unary/binary predicates. 
 * It is also required to indicate whether the term is an state property type (default), 
 * or an action type.
 * 
 * @author emery
 *
 */

public class Term {
	ArrayList<Modality> modes;
	boolean ispred;
	String label;
	String description;
	Predicate<PacmanGameObject> unaryPredicate;
	BiPredicate<PacmanGameObject, PacmanGameObject> binaryPredicate;
	int level;
	boolean isaction;
	boolean negated;
	String base;
	String satelite;

	
	public Term(String lab,  boolean neg, boolean pred, boolean action) {
		modes = new ArrayList<Modality>();
		ispred = pred;
		label = lab;
		description = "";
		isaction = action;
		negated = neg;
	}
	
	
	public boolean isNegated() {
		return negated;
	}
	
	
	public boolean isPredicate() {
		return ispred;
	}
	
	
	public void setBaseObject(String bs) {
		if(ispred) {
			base = bs;
		}
	}
	
	public void setSateliteObject(String st) {
		if(ispred) {
			satelite = st;
		}
	}
	
	
	public String getBaseObject() {
		return base;
	}
	
	public String getSateliteObject() {
		return satelite;
	}
	
	
	public boolean isAction() {
		return isaction;
	}

	
	public boolean evaluateBinary(PacmanGameObject obj1, PacmanGameObject obj2) {
		return binaryPredicate.test(obj1, obj2);
	}
	
	public boolean evaluateUnary(PacmanGameObject obj) {
		return unaryPredicate.test(obj);
	}
	
	
	public ArrayList<Modality> getModes(){
		return modes;
	}
	
	
	public String getDescription(){
		return description;
	}
	
	
	public String getLabel(){
		return label;
	}
	
	
	public void setMode(Modality mode){
		modes.add(mode);
	}
	
	
	public void setDescription(String s){
		description = s;
	}
	
	
	public void setBinaryPredicate(BiPredicate<PacmanGameObject, PacmanGameObject> func) {
		binaryPredicate = func;
	}
	
	
	public void setUnaryPredicate(Predicate<PacmanGameObject> func) {
		unaryPredicate = func;
	}
	
	public void negate() {
		negated = !negated;
		//label = "-"+label;
	}
	
	
	public Term copy() {
		return new Term(this.label, this.negated, this.ispred, this.isaction);
	}

}

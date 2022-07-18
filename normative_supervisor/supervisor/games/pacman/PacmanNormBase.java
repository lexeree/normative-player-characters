package supervisor.games.pacman;

import java.util.ArrayList;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import supervisor.games.pacman.PacmanGameObject;
import supervisor.normsys.ConstitutiveNorm;
import supervisor.normsys.ExceptionNorm;
import supervisor.normsys.Modality;
import supervisor.normsys.NormBase;
import supervisor.normsys.PriorityNorm;
import supervisor.normsys.RegulativeNorm;
import supervisor.normsys.Term;

/**
 * Generation methods for the norm bases for the Pac-Man game.
 * 
 * @author emery
 *
 */


public class PacmanNormBase extends NormBase{
	ArrayList<ConstitutiveNorm> actionConstitutive;
	ArrayList<ConstitutiveNorm> stateConstitutive;
	ArrayList<RegulativeNorm> regulative;
	ArrayList<ExceptionNorm> exception;
	ArrayList<PriorityNorm> priority;
	String name;

	public PacmanNormBase(String n) {
		super(n);
	}
	
	
	public void generateMoveAction() {
		ArrayList<Term> context = new ArrayList<Term>();
		ArrayList<Term> countsAs = new ArrayList<Term>();
		Term as = new Term("Stop", true, false, true);
		Term move = new Term("Move", false, false, true);
		countsAs.add(move);
		ConstitutiveNorm cons = new ConstitutiveNorm("strategy:move", context, countsAs, as);
		addActionConstitutiveNorm(cons);
		
		Term obl = move.copy();
		obl.setMode(Modality.OBLIGATION);
		try {
			RegulativeNorm bored = new RegulativeNorm("move", context, obl);
			addRegulativeNorm(bored);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	public ArrayList<Term> generateAdjacencyTerms(boolean range, String base, String satelite){
		ArrayList<Term> terms = new ArrayList<Term>();
		String rng = "";
		if(range) {
			rng = "range:";
		}
		
		Term neat = new Term(rng+"beside-North("+base+","+satelite+")", false, true, false);
		BiPredicate<PacmanGameObject, PacmanGameObject> nbeside = new BiPredicate<PacmanGameObject, PacmanGameObject>() {
		    @Override
		    public boolean test(PacmanGameObject base, PacmanGameObject satelite) {
		    	if(range) {
		    		float dir = Math.abs(base.getCoordX() - satelite.getCoordX()) + Math.abs(satelite.getCoordY() - base.getCoordY());
		    		return dir < 2.5 && base.getCoordY() < satelite.getCoordY();
		    	}
		    	else {
		    		return base.getCoordX() == satelite.getCoordX() && base.getCoordY() == satelite.getCoordY() - 1.0;	
		    	}
		    }
		};
		neat.setBinaryPredicate(nbeside);
		neat.setBaseObject(base);
		neat.setSateliteObject(satelite);
		terms.add(neat);
		
		Term seat = new Term(rng+"beside-South("+base+","+satelite+")", false, true, false);
		BiPredicate<PacmanGameObject, PacmanGameObject> sbeside = new BiPredicate<PacmanGameObject, PacmanGameObject>() {
		    @Override
		    public boolean test(PacmanGameObject base, PacmanGameObject satelite) {
		    	if(range) {
		    		float dir = Math.abs(base.getCoordX() - satelite.getCoordX()) + Math.abs(satelite.getCoordY() - base.getCoordY());
		    		return dir < 2.5 && base.getCoordY() > satelite.getCoordY();
		    	}
		    	else {
		    		return base.getCoordX() == satelite.getCoordX() && base.getCoordY() == satelite.getCoordY() + 1.0;	
		    	}
		    }
		};
		seat.setBinaryPredicate(sbeside);
		seat.setBaseObject(base);
		seat.setSateliteObject(satelite);
		terms.add(seat);
		
		Term eeat = new Term(rng+"beside-East("+base+","+satelite+")", false, true, false);
		BiPredicate<PacmanGameObject, PacmanGameObject> ebeside = new BiPredicate<PacmanGameObject, PacmanGameObject>() {
		    @Override
		    public boolean test(PacmanGameObject base, PacmanGameObject satelite) {
		    	if(range) {
		    		float dir = Math.abs(base.getCoordX() - satelite.getCoordX()) + Math.abs(satelite.getCoordY() - base.getCoordY());
		    		return dir < 2.5 && base.getCoordX() < satelite.getCoordX();
		    	}
		    	else {
		    		return base.getCoordY() == satelite.getCoordY() && base.getCoordX() == satelite.getCoordX() - 1.0;	
		    	}
		    }
		};
		eeat.setBinaryPredicate(ebeside);
		eeat.setBaseObject(base);
		eeat.setSateliteObject(satelite);
		terms.add(eeat);
		
		Term weat = new Term(rng+"beside-West("+base+","+satelite+")", false, true, false);
		BiPredicate<PacmanGameObject, PacmanGameObject> wbeside = new BiPredicate<PacmanGameObject, PacmanGameObject>() {
		    @Override
		    public boolean test(PacmanGameObject base, PacmanGameObject satelite) {
		    	if(range) {
		    		float dir = Math.abs(base.getCoordX() - satelite.getCoordX()) + Math.abs(satelite.getCoordY() - base.getCoordY());
		    		return dir < 2.5 && base.getCoordX() > satelite.getCoordX();
		    	}
		    	else {
		    		return base.getCoordY() == satelite.getCoordY() && base.getCoordX() == satelite.getCoordX() + 1.0;	
		    	}
		    }
		};
		weat.setBinaryPredicate(wbeside);
		weat.setBaseObject(base);
		weat.setSateliteObject(satelite);
		terms.add(weat);
		
		if(range) {
			Term imm = new Term(rng+"immediate-Stop("+base+","+satelite+")", false, true, false);
			BiPredicate<PacmanGameObject, PacmanGameObject> immside = new BiPredicate<PacmanGameObject, PacmanGameObject>() {
			    @Override
			    public boolean test(PacmanGameObject base, PacmanGameObject satelite) {
			    	float dir = Math.abs(base.getCoordX() - satelite.getCoordX()) + Math.abs(satelite.getCoordY() - base.getCoordY());
		    		return dir < 1.5;	
			    }
			};
			imm.setBinaryPredicate(immside);
			imm.setBaseObject(base);
			imm.setSateliteObject(satelite);
			terms.add(imm);
		}
		
		return terms;
	}
	
	
	
	public Term generateAdjacencyRules(boolean range, String base, String satelite) {
		ArrayList<Term> terms = generateAdjacencyTerms(range, base, satelite);
		
		Term beside = new Term("range:beside("+base+","+satelite+")", false, true, false);
		BiPredicate<PacmanGameObject, PacmanGameObject> b = new BiPredicate<PacmanGameObject, PacmanGameObject>() {
		    @Override
		    public boolean test(PacmanGameObject base, PacmanGameObject satelite) {
		    	boolean tst = false;
		    	for(Term t : terms) {
		    		tst = tst || t.evaluateBinary(base, satelite);
		    	}
		        return tst;
		    }
		};
		beside.setBinaryPredicate(b);
		beside.setBaseObject(base);
		beside.setSateliteObject(satelite);
		
		for(Term term : terms) {
			ArrayList<Term> context = new ArrayList<Term>();
			ArrayList<Term> countsAs = new ArrayList<Term>(); 
			countsAs.add(term);
			ConstitutiveNorm norm = new ConstitutiveNorm("range:"+term.getLabel().substring(term.getLabel().indexOf("-") + 1, 
					term.getLabel().indexOf("("))+">beside("+base+","+satelite+")", context, countsAs, (Term) beside);
			addStateConstitutiveNorm(norm);
		}
		return beside;
	}
	
	
	
	public void eatFood() {
		ArrayList<Term> beside = generateAdjacencyTerms(false, "pacman", "food");
		ArrayList<Term> context = new ArrayList<Term>();
		ArrayList<Term> countsAs = new ArrayList<Term>();
		try {
			Term eat = new Term("eat(pacman,food)", false, false, true);
			eat.setMode(Modality.OBLIGATION);
			eat.setBaseObject("pacman");
			eat.setSateliteObject("food");
			countsAs.add(eat);
			
			RegulativeNorm eatstuff = new RegulativeNorm("eat(pacman,food)", context, eat);
			addRegulativeNorm(eatstuff);
			
			for(Term b : beside) {
				context = new ArrayList<Term>();
				context.add(b);
				Term term = new Term(b.getLabel().substring(b.getLabel().indexOf("-") + 1, b.getLabel().indexOf("(")), 
						false, false, true);
				term.setMode(Modality.OBLIGATION);
				ConstitutiveNorm strategy = new ConstitutiveNorm("strategy:eat"+term.getLabel()+"(pacman,food)", 
								context, countsAs, term);
						addActionConstitutiveNorm(strategy);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void vegan(String satelite) {
		ArrayList<Term> beside = generateAdjacencyTerms(true, "pacman", satelite);
		ArrayList<Term> context = new ArrayList<Term>();
		Term scgh = new Term("scared("+satelite+")", false, true, false);
		Predicate<PacmanGameObject> scared = new Predicate<PacmanGameObject>() {
			@Override
			public boolean test(PacmanGameObject base) {
			    return base.isScared();
			}
		};
		scgh.setBaseObject(satelite);
		scgh.setUnaryPredicate(scared);
		try {
			Term eat = new Term("eat(pacman,"+satelite+")", false, false, true);
			eat.setBaseObject("pacman");
			eat.setSateliteObject(satelite);
			Term f_eat = eat.copy();
			f_eat.setMode(Modality.PROHIBITION);
			RegulativeNorm eatstuff  = new RegulativeNorm("-eat(pacman,"+satelite+")", context, f_eat);
			addRegulativeNorm(eatstuff);
			
			for(Term b : beside) {
				ArrayList<Term> countsAs = new ArrayList<Term>();
				context = new ArrayList<Term>();
				context.add(b);
				context.add(scgh);
				Term term = new Term(b.getLabel().substring(b.getLabel().indexOf("-") + 1, b.getLabel().indexOf("(")), 
						false, false, true);
				countsAs.add(term);
				ConstitutiveNorm strategy = new ConstitutiveNorm("strategy:eat"+term.getLabel()+"(pacman,"+satelite+")", 
								context, countsAs, eat);
				addActionConstitutiveNorm(strategy);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
	
	public void prefVegan(String satelite) {
		ArrayList<Term> beside = generateAdjacencyTerms(true, "pacman", satelite);
		ArrayList<Term> context = new ArrayList<Term>();
		try {
			Term eat = new Term("eat(pacman,"+satelite+")", false, false, true);
			eat.setBaseObject("pacman");
			eat.setSateliteObject(satelite);
			Term f_eat = eat.copy();
			f_eat.setMode(Modality.PROHIBITION);
			RegulativeNorm eatstuff  = new RegulativeNorm("priority(pacman,"+satelite+")", context, f_eat);
			addRegulativeNorm(eatstuff);
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
	public void eat(String satelite) {
		Term beside = generateAdjacencyRules(true, "pacman", satelite);
		ArrayList<Term> context = new ArrayList<Term>();
		context.add(beside);
		Term scgh = new Term("scared("+satelite+")", false, true, false);
		Predicate<PacmanGameObject> scared = new Predicate<PacmanGameObject>() {
			@Override
			public boolean test(PacmanGameObject base) {
			    return base.isScared();
			}
		};
		scgh.setBaseObject(satelite);
		scgh.setUnaryPredicate(scared);
		context.add(scgh);
		try {
			Term eat = new Term("eat(pacman,"+satelite+")", false, false, true);
			eat.setBaseObject("pacman");
			eat.setSateliteObject(satelite);
			Term f_eat = eat.copy();
			f_eat.setMode(Modality.OBLIGATION);
			RegulativeNorm eatstuff  = new RegulativeNorm("eat(pacman,"+satelite+")", context, f_eat);
			addRegulativeNorm(eatstuff);
			PriorityNorm p = new PriorityNorm("priority", "-eat(pacman,"+satelite+")", "eat(pacman,"+satelite+")");
			addPriorityNorm(p);
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
	
	public void danger(ArrayList<Float[]> zone, String satelite) {
		ArrayList<Term> beside1 = generateAdjacencyTerms(false, "pacman", "danger");
		for(Term term : beside1) {
			Predicate<PacmanGameObject> dZone = new Predicate<PacmanGameObject>() {
				@Override
				public boolean test(PacmanGameObject base) {
					boolean bool = false;
					for(Float[] coord : zone) {
						PacmanGameObject obj = new PacmanGameObject("",  coord[0], coord[1]);
						boolean b = term.evaluateBinary(base, obj);
						bool = bool || b;
					}
				    return bool;
				}
			};
			term.setUnaryPredicate(dZone);
		}
		Term beside2 = generateAdjacencyRules(true, "pacman", satelite);
		try {
			Term ent = new Term("enter(pacman,danger)", false, false, true);
			ent.setBaseObject("pacman");
			Term f_ent = ent.copy();
			f_ent.setMode(Modality.PROHIBITION);
			ArrayList<Term> context = new ArrayList<Term>();
			RegulativeNorm enter = new RegulativeNorm("-enter(pacman,danger)", context, f_ent);
			addRegulativeNorm(enter);
			
			for(Term b : beside1) {
				context = new ArrayList<Term>();
				ArrayList<Term> countsAs = new ArrayList<Term>();
				context.add(b);
				context.add(beside2);
				Term term = new Term(b.getLabel().substring(b.getLabel().indexOf("-") + 1, b.getLabel().indexOf("(")), 
						false, false, true);
				countsAs.add(term);
				ConstitutiveNorm strategy = new ConstitutiveNorm("strategy:enter"+term.getLabel()+"(pacman,danger)("+satelite+")", 
								context, countsAs, ent);
				addActionConstitutiveNorm(strategy);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
	
	public void giveUp(String satelite, String condition) {
		ArrayList<Term> context = new ArrayList<Term>();
		Term cond = new Term(condition, false, false, false);
		context.add(cond);
		try {
			Term eat = new Term("eat(pacman,"+satelite+")", false, false, true);
			eat.setBaseObject("pacman");
			eat.setSateliteObject(satelite);
			Term f_eat = eat.copy();
			f_eat.setMode(Modality.PERMISSION);
			ExceptionNorm eatstuff  = new ExceptionNorm("eat(pacman,"+satelite+")", context, f_eat);
			addExceptionNorm(eatstuff);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void passive(String satelite) {
		try {
			ArrayList<Term> context = new ArrayList<Term>();
			Term eat = new Term("eat(pacman,"+satelite+")", false, false, true);
			eat.setBaseObject("pacman");
			eat.setSateliteObject(satelite);
			//eat.setMode(Modality.OBLIGATION);
			context.add(eat);
			Term term = new Term("Stop", false, false, true);
			RegulativeNorm move  = new RegulativeNorm("passive:"+satelite, context, term);
			addRegulativeNorm(move);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void superDanger(ArrayList<Float[]> zone) {
		ArrayList<Term> beside1 = generateAdjacencyTerms(false, "pacman", "danger");
		for(Term term : beside1) {
			Predicate<PacmanGameObject> dZone = new Predicate<PacmanGameObject>() {
				@Override
				public boolean test(PacmanGameObject base) {
					boolean bool = false;
					for(Float[] coord : zone) {
						PacmanGameObject obj = new PacmanGameObject("",  coord[0], coord[1]);
						boolean b = term.evaluateBinary(base, obj);
						bool = bool || b;
					}
				    return bool;
				}
			};
			term.setUnaryPredicate(dZone);
		}
		try {
			Term ent = new Term("enter(pacman,danger)", false, false, true);
			ent.setBaseObject("pacman");
			Term f_ent = ent.copy();
			f_ent.setMode(Modality.PROHIBITION);
			ArrayList<Term> context = new ArrayList<Term>();
			RegulativeNorm enter = new RegulativeNorm("-enter(pacman,danger)", context, f_ent);
			addRegulativeNorm(enter);
			
			for(Term b : beside1) {
				context = new ArrayList<Term>();
				ArrayList<Term> countsAs = new ArrayList<Term>();
				context.add(b);
				Term term = new Term(b.getLabel().substring(b.getLabel().indexOf("-") + 1, b.getLabel().indexOf("(")), 
						false, false, true);
				countsAs.add(term);
				ConstitutiveNorm strategy = new ConstitutiveNorm("strategy:enter"+term.getLabel()+"(pacman,danger)", 
								context, countsAs, ent);
				addActionConstitutiveNorm(strategy);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
	
	public void benevolent() {
		ArrayList<Term> beside1 = generateAdjacencyTerms(true, "pacman", "bGhost");
		ArrayList<Term> beside2 = generateAdjacencyTerms(true, "pacman", "oGhost");
		ArrayList<Term> context = new ArrayList<Term>();
		Term scgh1 = new Term("scared(bGhost)", false, true, false);
		Term scgh2 = new Term("scared(oGhost)", false, true, false);
		Predicate<PacmanGameObject> scared = new Predicate<PacmanGameObject>() {
			@Override
			public boolean test(PacmanGameObject base) {
			    return base.isScared();
			}
		};
		scgh1.setBaseObject("bGhost");
		scgh1.setUnaryPredicate(scared);
		scgh2.setBaseObject("oGhost");
		scgh2.setUnaryPredicate(scared);
		try {
			Term bene = new Term("benevolent", false, false, true);
			Term nbene = new Term("benevolent", true, false, true);
			Term eat = new Term("eat(pacman,ghost)", false, false, true);
			eat.setBaseObject("pacman");
			eat.setSateliteObject("ghost");
			Term eat1 = new Term("eat(pacman,bGhost)", false, false, true);
			eat1.setBaseObject("pacman");
			eat1.setSateliteObject("bGhost");
			Term eat2 = new Term("eat(pacman,oGhost)", false, false, true);
			eat2.setBaseObject("pacman");
			eat2.setSateliteObject("oGhost");
			
			Term o_bene = bene.copy();
			o_bene.setMode(Modality.OBLIGATION);
			RegulativeNorm benevolence = new RegulativeNorm("benevolence", context, o_bene);
			addRegulativeNorm(benevolence);
			
			ArrayList<Term> countsAs = new ArrayList<Term>();
			countsAs.add(eat);
			ConstitutiveNorm ghost = new ConstitutiveNorm("-benevolence(ghost)", context, countsAs, nbene);
			addActionConstitutiveNorm(ghost);
			
			ArrayList<Term> countsAs1 = new ArrayList<Term>();
			countsAs1.add(eat1);
			ArrayList<Term> countsAs2 = new ArrayList<Term>();
			countsAs2.add(eat2);
			ConstitutiveNorm ghost1 = new ConstitutiveNorm("bGhost(ghost)", context, countsAs1, eat);
			ConstitutiveNorm ghost2 = new ConstitutiveNorm("oGhost(ghost)", context, countsAs2, eat);
			addActionConstitutiveNorm(ghost1);
			addActionConstitutiveNorm(ghost2);
			
			for(Term b : beside1) {
				countsAs1 = new ArrayList<Term>();
				context = new ArrayList<Term>();
				context.add(b);
				context.add(scgh1);
				Term term = new Term(b.getLabel().substring(b.getLabel().indexOf("-") + 1, b.getLabel().indexOf("(")), 
						false, false, true);
				countsAs1.add(term);
				ConstitutiveNorm strategy1 = new ConstitutiveNorm("strategy:eat"+term.getLabel()+"(pacman,bGhost)", 
								context, countsAs1, eat1);
				addActionConstitutiveNorm(strategy1);
			}
			
			for(Term b : beside2) {
				countsAs2 = new ArrayList<Term>();
				context = new ArrayList<Term>();
				context.add(b);
				context.add(scgh2);
				Term term = new Term(b.getLabel().substring(b.getLabel().indexOf("-") + 1, b.getLabel().indexOf("(")), 
						false, false, true);
				countsAs2.add(term);
				ConstitutiveNorm strategy = new ConstitutiveNorm("strategy:eat"+term.getLabel()+"(pacman,oGhost)", 
								context, countsAs2, eat2);
				addActionConstitutiveNorm(strategy);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
	
	public void permit(String satelite) {
		ArrayList<Term> context = new ArrayList<Term>();
		try {
			Term eat = new Term("eat(pacman,"+satelite+")", false, false, true);
			eat.setBaseObject("pacman");
			eat.setSateliteObject(satelite);
			Term f_eat = eat.copy();
			f_eat.setMode(Modality.PERMISSION);
			ExceptionNorm eatstuff  = new ExceptionNorm("eat(pacman,"+satelite+")", context, f_eat);
			addExceptionNorm(eatstuff);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void partial(String satelite) {
		ArrayList<Term> context = new ArrayList<Term>();
		try {
			Term bene = new Term("benevolent", false, false, true);
			Term eat = new Term("eat(pacman,"+satelite+")", false, false, true);
			eat.setBaseObject("pacman");
			eat.setSateliteObject("satelite");
			ArrayList<Term> countsAs = new ArrayList<Term>();
			countsAs.add(eat);
			ConstitutiveNorm ghost = new ConstitutiveNorm("benevolence("+satelite+")", context, countsAs, bene);
			addActionConstitutiveNorm(ghost);
			
			PriorityNorm p = new PriorityNorm("p", "pos:benevolence(ghost)", "pos:benevolence(oGhost)" );
			addPriorityNorm(p);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	

}

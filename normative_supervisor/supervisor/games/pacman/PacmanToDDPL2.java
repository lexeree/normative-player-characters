package supervisor.games.pacman;

import java.util.ArrayList;

import spindle.core.dom.Literal;
import spindle.core.dom.Rule;
import spindle.core.dom.RuleException;
import spindle.core.dom.RuleType;
import supervisor.normsys.ConstitutiveNorm;
import supervisor.normsys.NormBase;
import supervisor.normsys.Term;

/**
 * PacmanToDDPL modified in order to accommodate parsing DDPLReasoner2. 
 * Just needed to modify the action constitutive rules method.
 * Not really necessary, just generates a smaller theory.
 * 
 * @author emery
 *
 */

public class PacmanToDDPL2 extends PacmanToDDPL {

	public PacmanToDDPL2(NormBase nb) {
		super(nb);
	}
	
	
    @Override
	public void generateActionConstitutiveRules(PacmanEnvironment board) {
		try {
			ArrayList<ConstitutiveNorm> actNorms = normBase.getActionConstitutiveNorms();
			for(ConstitutiveNorm n : actNorms) {
				Rule rule = new Rule("pos:"+n.getName(), RuleType.STRICT);
				for(Term term : n.getContext()) {
					if(term.isPredicate()) {
						try {
							unaryPredicateToFact(term, board.getObject(term.getBaseObject()));
						} catch(NullPointerException e) {}
						try {
							binaryPredicateToConstitutive(term, board.getObject(term.getBaseObject()), board.getObject(term.getSateliteObject()));
						} catch(NullPointerException e) {}
						
					}
					Literal lit = termToLit(term, false);
					rule.addBodyLiteral(lit);
				}
				//positive
				for(Term term : n.getLowerTerms()) {
					Literal lit1 = termToLit(term, false);
					rule.addBodyLiteral(lit1);
				}
				Literal head1 = termToLit(n.getHigherTerm(), false);	
				rule.addHeadLiteral(head1);
				rules.add(rule);
				strategies.add(rule);
			}
		}
		catch (RuleException e) {
			e.printStackTrace();
		}
	}
	
		



}

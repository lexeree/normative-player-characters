package supervisor.games.merchant;

import java.util.ArrayList;
import java.util.Arrays;

import supervisor.normsys.ConstitutiveNorm;
import supervisor.normsys.ExceptionNorm;
import supervisor.normsys.Modality;
import supervisor.normsys.NormBase;
import supervisor.normsys.RegulativeNorm;
import supervisor.normsys.Term;

/**
 * Norm bases for the Merchant environment. Additional norm base generation methods can be added.
 * 
 * @author emery
 *
 */

public class MerchantNormBase extends NormBase {
	ArrayList<String> dirs = new ArrayList<String>(Arrays.asList("North", "South", "East", "West"));

	public MerchantNormBase(String n) {
		super(n);
	}
	
	
	public void generatePacifist() {
		ArrayList<Term> ctxt1 = new ArrayList<Term>();
		ArrayList<Term> ctxt2 = new ArrayList<Term>();
		ArrayList<Term> ctxt3 = new ArrayList<Term>();
		ArrayList<Term> ctxt4;
		Term danger = new Term("at_danger", false, false, false);
		Term pdanger = danger.copy();
		pdanger.setMode(Modality.PROHIBITION);
		Term attack = new Term("attacked", false, false, false);
		Term unload = new Term("Unload", false, false, true);
		Term negotiate = new Term("negotiate", false, false, true);
		Term donegotiate = negotiate.copy();
		donegotiate.setMode(Modality.OBLIGATION);
		RegulativeNorm fdanger = new RegulativeNorm("F(danger)", ctxt1, pdanger);
		addRegulativeNorm(fdanger);
		ctxt2.add(danger);
		ctxt2.add(attack);
		RegulativeNorm negot = new RegulativeNorm("O(negotiate)", ctxt2, donegotiate);
		addRegulativeNorm(negot);
		ctxt3.add(attack);
		ArrayList<Term> ctas = new ArrayList<Term>();
		ctas.add(unload);
		ConstitutiveNorm tonegotiate = new ConstitutiveNorm("C(unload,negotiate)", ctxt3, ctas, negotiate);
		addActionConstitutiveNorm(tonegotiate);
		for(String dir : dirs) {
			Term ddr = new Term(dir+"_danger", false, false, false);
			Term act = new Term(dir, false, false, true);
			ArrayList<Term> actn = new ArrayList<Term>();
			actn.add(act);
			ctxt4 = new ArrayList<Term>();
			ctxt4.add(ddr);
			ConstitutiveNorm move = new ConstitutiveNorm("move("+dir+")", ctxt4, actn, danger);
			addActionConstitutiveNorm(move);
		}
	}
	
	
	public void generateGreen() {
		ArrayList<Term> ctxt1 = new ArrayList<Term>();
		ArrayList<Term> ctas1 = new ArrayList<Term>();
		ArrayList<Term> ctxt2 = new ArrayList<Term>();
		ArrayList<Term> ctxt3 = new ArrayList<Term>();
		ArrayList<Term> ctxt4 = new ArrayList<Term>();
		ArrayList<Term> ctxt5 = new ArrayList<Term>();
		ArrayList<Term> ctas2 = new ArrayList<Term>();
		ArrayList<Term> ctas3 = new ArrayList<Term>();
		Term env = new Term("environment", false, false, true);
		Term doenv = env.copy();
		doenv.setMode(Modality.OBLIGATION);
		Term nenv = env.copy();
		nenv.negate();
		RegulativeNorm envfr = new RegulativeNorm("O(environment)", ctxt1, doenv);
		addRegulativeNorm(envfr);
		Term deforest = new Term("deforest", false, false, true);
		ctas1.add(deforest);
		ConstitutiveNorm defnenv = new ConstitutiveNorm("C(deforest, -env)", ctxt1, ctas1, nenv);
		addActionConstitutiveNorm(defnenv);
		Term tree = new Term("at_tree", false, false, false);
		Term wood = new Term("at_wood", false, false, false);
		ctxt2.add(tree);
		ctxt3.add(wood);
		Term extract = new Term("Extract", false, false, true);
		Term pickup = new Term("Pickup", false, false, true);
		ctas2.add(extract);
		ctas3.add(pickup);
		ConstitutiveNorm extractdef = new ConstitutiveNorm("C(extract,deforest)", ctxt2, ctas2, deforest);
		addActionConstitutiveNorm(extractdef);
		ConstitutiveNorm pickupdef = new ConstitutiveNorm("C(pickup,deforest)", ctxt3, ctas3, deforest);
		addActionConstitutiveNorm(pickupdef);
	    Term has = new Term("has_wood", true, false, false);
	    ctxt4.add(has);
	    Term ppickup = pickup.copy();
	    ppickup.setMode(Modality.PERMISSION);
	    ctxt5.add(ppickup);
	    Term pextract = extract.copy();
	    pextract.setMode(Modality.PERMISSION);
		try {
			 ExceptionNorm nowood = new ExceptionNorm("P(pickup)", ctxt4, ppickup);
			 addExceptionNorm(nowood);
			 ExceptionNorm depper = new ExceptionNorm("P(extract)", ctxt5, pextract);
			 addExceptionNorm(depper);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

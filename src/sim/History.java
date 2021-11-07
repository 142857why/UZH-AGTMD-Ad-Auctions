package sim;

import java.util.ArrayList;
/**
 * This class stores information about the auctions of previous rounds
 */

public class History{
	
	//<agentId> in descending order (slot 1, slot 2 etc)
	private ArrayList<ArrayList<Integer>> slotOccupants;
	
	//<bidAmount> ordered by agentId (agent1, agent2 etc)
	private ArrayList<ArrayList<Integer>> bids;
	
	//<amount> in descending order (slot 1, slot 2 etc)
	private ArrayList<ArrayList<Integer>> perClickPayments;
	
	//<amount> in descending order (slot 1, slot 2 etc)
	private ArrayList<ArrayList<Integer>> slotPayments;
	
	//<amount> ordered by agentId
	private ArrayList<ArrayList<Integer>> utilities;
	
	//<clicks> in descending order (slot 1, slot 2 etc)
	private ArrayList<ArrayList<Integer>> slotClicks;
	
	public History(){
		this.slotOccupants = new ArrayList<ArrayList<Integer>>();
		this.bids = new ArrayList<ArrayList<Integer>>();
		this.perClickPayments= new ArrayList<ArrayList<Integer>>();
		this.slotPayments = new ArrayList<ArrayList<Integer>>();
		this.utilities = new ArrayList<ArrayList<Integer>>();
		this.slotClicks= new ArrayList<ArrayList<Integer>>();
	}
	
	/**
	 * Copy constructor. Returns a references to a copy of the argument hist
	 * @param hist
	 */
	public History(History hist) {
		ArrayList<ArrayList<Integer>> slotOccupants = new ArrayList<ArrayList<Integer>>();
		ArrayList<ArrayList<Integer>> bids = new ArrayList<ArrayList<Integer>>();
		ArrayList<ArrayList<Integer>> perClickPayments= new ArrayList<ArrayList<Integer>>();
		ArrayList<ArrayList<Integer>> slotPayments = new ArrayList<ArrayList<Integer>>();
		ArrayList<ArrayList<Integer>> utilities = new ArrayList<ArrayList<Integer>>();
		ArrayList<ArrayList<Integer>> slotClicks= new ArrayList<ArrayList<Integer>>();
		
		
		ArrayList<ArrayList<Integer>> slotOccupantsHist = hist.getSlotOccupants();
		ArrayList<ArrayList<Integer>> bidsHist = hist.getBids();
		ArrayList<ArrayList<Integer>> perClickPaymentsHist= hist.getPerClickPayments();
		ArrayList<ArrayList<Integer>> slotPaymentsHist = hist.getSlotPayments();
		ArrayList<ArrayList<Integer>> utilitiesHist = hist.getUtilities();
		ArrayList<ArrayList<Integer>> slotClicksHist = hist.getSlotClicks();
		
		//make clones of all lists
		int t;
		
		for (t = 0; t<slotOccupantsHist.size(); t++){
			slotOccupants.add((ArrayList<Integer>) slotOccupantsHist.get(t).clone());
		}
		
		for (t = 0; t<bidsHist.size(); t++){
			bids.add((ArrayList<Integer>) bidsHist.get(t).clone());
		}
		
		for (t = 0; t<perClickPaymentsHist.size(); t++){
			perClickPayments.add((ArrayList<Integer>) perClickPaymentsHist.get(t).clone());
		}
		
		for (t = 0; t<slotPaymentsHist.size(); t++){
			slotPayments.add((ArrayList<Integer>) slotPaymentsHist.get(t).clone());
		}
		
		for (t = 0; t<utilitiesHist.size(); t++){
			utilities.add((ArrayList<Integer>) utilitiesHist.get(t).clone());
		}
		
		for (t = 0; t<slotClicksHist.size(); t++){
			slotClicks.add((ArrayList<Integer>) slotClicksHist.get(t).clone());
		}
		
		this.slotOccupants = slotOccupants;
		this.bids = bids;
		this.perClickPayments = perClickPayments;
		this.slotPayments = slotPayments;
		this.utilities = utilities;
		this.slotClicks = slotClicks;
	}

	public ArrayList<Integer> getSlotOccupants(int t) {
		return slotOccupants.get(t);
	}

	public void setSlotOccupants(ArrayList<Integer> slotOccupants, int t) {
		this.slotOccupants.add(t,slotOccupants);
	}

	public ArrayList<Integer> getBids(int t) {
		return bids.get(t);
	}

	public void setBids(ArrayList<Integer> bids,int t) {
		this.bids.add(t,bids);
	}

	public ArrayList<Integer> getPerClickPayments(int t) {
		return perClickPayments.get(t);
	}

	public void setPerClickPayments(ArrayList<Integer> perClickPayments, int t) {
		this.perClickPayments.add(t,perClickPayments);
	}

	public ArrayList<Integer> getSlotPayments(int t) {
		return slotPayments.get(t);
	}

	public void setSlotPayments(ArrayList<Integer> slotPayments, int t) {
		this.slotPayments.add(t,slotPayments);
	}

	public ArrayList<Integer> getUtilities(int t) {
		return utilities.get(t);
	}

	public void setUtilities(ArrayList<Integer> utilities, int t) {
		this.utilities.add(t,utilities);
	}

	public ArrayList<Integer> getSlotClicks(int t) {
		return slotClicks.get(t);
	}

	public ArrayList<ArrayList<Integer>> getSlotOccupants() {
		return slotOccupants;
	}

	public ArrayList<ArrayList<Integer>> getBids() {
		return bids;
	}

	public ArrayList<ArrayList<Integer>> getPerClickPayments() {
		return perClickPayments;
	}

	public ArrayList<ArrayList<Integer>> getSlotPayments() {
		return slotPayments;
	}

	public ArrayList<ArrayList<Integer>> getUtilities() {
		return utilities;
	}

	public ArrayList<ArrayList<Integer>> getSlotClicks() {
		return slotClicks;
	}

	public void setSlotClicks(ArrayList<Integer> slotClicks, int t) {
		this.slotClicks.add(t,slotClicks);
	}
	
	// length of the history.
	public int size() {
		return slotClicks.size();
	}
}

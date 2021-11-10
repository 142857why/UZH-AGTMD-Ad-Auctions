package sim;

import java.util.ArrayList;
import java.util.Collections;

/**
 * This class implements the general VCG slot auction.
 * We assume bidder effects to be all 1, i.e. they don't matter.
 *
 */
public class VCG {


	/**
	 * This function returns two lists allocation and payments.
	 * allocation is a list of agents that win the slots (in the order: slot1, slot2, ...)
	 * payments is a list of per click payments that have to be made for the slots (in the order: slot1, slot2, ...)
	 * @param slotClicks, the clicks per slot
	 * @param bids, the agent's bids
	 * @param reserve, the reserve price
	 * @return a pair of lists for the allocation and the payments for each slot: (list_agents, list_payments)
	 */
	public static ArrayList<ArrayList<Integer>> computeOutcome(ArrayList<Integer> slotClicks, ArrayList<Integer> bids, int reserve) {
		
		ArrayList<ArrayList<Integer>> allocation = new ArrayList<ArrayList<Integer>>();
		ArrayList<Integer> occupants = new ArrayList<Integer>();
		ArrayList<Integer> perClickPayments = new ArrayList<Integer>();

		//make a copy of the bids 
		ArrayList<Pair> bidsCopy = new ArrayList<Pair>();
		for (int i =0;i<bids.size();i++){
			Pair p = new Pair(i, bids.get(i));
			bidsCopy.add(p);
		}

		//shuffle (for random tie breaking) and sort
		Collections.shuffle(bidsCopy, AuctionSimulator.getRandom());
		Collections.sort(bidsCopy, Collections.reverseOrder());

		int numSlots = slotClicks.size();
		occupants = computeAllocation(numSlots, bidsCopy,reserve);
		perClickPayments = computePayments(slotClicks, bidsCopy, reserve);

		allocation.add(occupants);
		allocation.add(perClickPayments);

		return allocation;
	}

	/**
	 * This function computes the allocation of the slots. The agents with the highest bids are allocated (tie breaking is random).
	 * @param numSlots, the number of available slots
	 * @param bids, pairs (agent id, bid amount), must be ordered according to the bid amount (i.e. biggest amount first).
	 * @param reserve, the reserve price
	 * @return a list of agent ids (in order: slot1, slot2, ...)
	 */
	public static ArrayList<Integer> computeAllocation(int numSlots, ArrayList<Pair> bids, int reserve){
		ArrayList<Integer> occupants = new ArrayList<Integer>();

		if (bids.size() == 0){
			return occupants;
		}
		ArrayList<Pair> validBids = new ArrayList<Pair>();
		for (int i = 0; i<bids.size(); i++){
			if(bids.get(i).getSecond() >= reserve)
				validBids.add(bids.get(i));
		}

		//the top bidders occupy the slots
		int alloc = Math.min(validBids.size(), numSlots);
		for (int i = 0; i<alloc; i++){
			occupants.add(validBids.get(i).getFirst());
		}
		return occupants;
	}

	/**
	 * This function computes the vcg per click payments
	 * @param slotClicks, a list of number of slot clicks (in order: slot1, slot2, ...)
	 * @param bids, pairs (agent id, bid amount), must be ordered according to the bid amount (i.e. biggest amount first)
	 * @param reserve, the reserve price
	 * @return a list of payments (in order: slot1, slot2, ...)
	 */
	public static ArrayList<Integer> computePayments(ArrayList<Integer> slotClicks, ArrayList<Pair> bids, int reserve){
		ArrayList<Integer> perClickPayments = new ArrayList<Integer>();

		int numSlots = slotClicks.size();
		int firstUnallocatedBid;
		
		int numValid = 0;
		for (int i = 0; i<bids.size(); i++){
			if (bids.get(i).getSecond()>=reserve){
				numValid++;
			}
		}
		
		firstUnallocatedBid = numValid > numSlots ? bids.get(numSlots).getSecond() : reserve;
		
		int alloc = Math.min(numValid, numSlots);
		for (int i = 0; i < alloc; i++){
			int totalPayment = 0;
			/*TODO implement the vcg payment rule, i.e. you need to implement equation (9.13) from the lectures notes
			 * (i.e. totalPayment = t_{vcg,i}(b))
			 */

			// (pos(k-1) - pos(k)) Qk * bk
			for (int k = i + 1; k < alloc + 1; ++k) {
				int b_k = (k == alloc) ?  firstUnallocatedBid : bids.get(k).getSecond();
				int pos_k_minus_1 = slotClicks.get(k-1);
				int pos_k = (k == alloc) ? 0 : slotClicks.get(k);

				totalPayment += (pos_k_minus_1 - pos_k) * b_k;
			}

			perClickPayments.add((int)Math.round(totalPayment/(double)slotClicks.get(i)));

		}
		return perClickPayments;
	}

	/**
	 * This function computes the range of bids that would result in winning the slot,
	 * give that the agents submit bids. This is the same function as in the class GSP
	 * @param slot, the target slot considered
	 * @param reserve, the reserve price
	 * @param bids, the agents' bids
	 * @return a pair (minBid, maxBid) that defines the range of valid bids for ending up in the slot
	 */
	public static Pair bidRangeForSlot(int slot, int reserve, ArrayList<Integer>bids) {
		//make a copy of the bids above the reserve price. 
		ArrayList<Integer> bidsCopy = new ArrayList<Integer>();
		for (int i =0;i<bids.size();i++){
			if(bids.get(i) >= reserve){
				bidsCopy.add(bids.get(i));
			}
		}

		Collections.sort(bidsCopy, Collections.reverseOrder());

		int numBidders = bidsCopy.size();
		int minBid, maxBid;

		if (slot >= numBidders ){
			minBid = reserve;
			if (numBidders > 0){
				maxBid = bidsCopy.get(bidsCopy.size()-1);
			}
			else {
				maxBid = slot == 0 ? 2*minBid : reserve;
			}
		}

		else {
			minBid = bidsCopy.get(slot);
			maxBid = slot == 0 ? 2*minBid : bidsCopy.get(slot-1);
		}

		return new Pair(minBid,maxBid);
	}
}

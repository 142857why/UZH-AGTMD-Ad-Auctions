package sim;

import java.util.ArrayList;

/**
 * This class implements the balanced bidding agent
 * Rename the class by substituting "TeamABC" by your team's initials,
 * e.g. BBAgentMRTK
 */
public class BBAgentRZHOU extends Agent{

	public BBAgentRZHOU(int id, int value, int budget){
		super(id,value,budget);
	}
	
	@Override
	public int initialBid(int reserve) {
		return getValue()/2;
	}

	@Override
	public int bid(int t, History history, int reserve) {
		ArrayList<Integer> targetInfo = targetSlot(t, history, reserve);
		
		int targetSlot = targetInfo.get(0); // targetSlot = i*
		int bid = 0;
		
		/*TODO compute the bid according to the balanced bidding strategy:
		 * a) find the clickrates for the target and the next higher slot and
		 * b) solve the balanced bidding equation q_i* (w_i-p_i*) = q_(i*-1)(w_i-b_i)
		 * 
		 * hint 1: use history.getSlotClicks(t-1)
		 * hint 2: as described in the assignment, clicks_(targetSlot-1) = 2*clicks_(target_slot) if targetSlot == 0
		 */
		ArrayList<Integer> lastTimeNumClicks = history.getSlotClicks(t - 1); //q_k
		int numSlots = lastTimeNumClicks.size();
		// p_i* payment to (perhaps Google) to get position i*
		// q_i* the quality on position i*， = c(i*, t-1)
		// w_i : value
		// b_i : bid (that's what we return)
		int q_i_star = lastTimeNumClicks.get(targetSlot);
		int q_i_star_minus1 = (targetSlot == 0) ? 2 * q_i_star : lastTimeNumClicks.get(targetSlot - 1);
		ArrayList<Pair> bids = slotInfo(t, history, reserve);
//		ArrayList<Integer> payment = GSP.computePayments(numSlots, bids, reserve);
		int w_i = this.getValue();
		int p_i_star = bids.get(targetSlot).getFirst();
		double exactBid = (double)w_i - (q_i_star * (w_i - p_i_star) / (double)q_i_star_minus1);
		bid = (int) Math.floor(exactBid);

//		bid = this.getValue() - q_i_star * (this.getValue() - bids.get(targetSlot).getFirst()) / q_i_star_minus1;
		// ONLY FOR DEBUG
//		System.out.println("Let's print" + this.getId() + " at time t-1");
//		for (Integer i : lastTimeNumClicks) {
//			System.out.print(i + " ");
//		}
//		System.out.println();

		// DEBUG ends
		return Math.min(bid, this.getValue());
	}
	
	/**
	 * This function returns a list of Pairs (minBid,maxBid) for each slot (i.e. slot1,slot2, ...).
	 * (minBid,maxBid)_i defines the miminum and maximum bid to win slot i, given that all other
	 * agents bid the same amount as in the last round
	 * @param t, the time step
	 * @param history, the object in which the information of all previous rounds is stored
	 * @param reserve, the reserve price
	 * @return list of Pairs (minBid,maxBid)_i for all slots i
	 */

	public ArrayList<Pair> slotInfo(int t, History history, int reserve){
		ArrayList<Pair> info = new ArrayList<Pair>();
		
		//get bids from last round
		ArrayList<Integer> lastBids = (ArrayList<Integer>)history.getBids(t-1).clone();

		lastBids.remove(getId());
		// lastBids : 其他bidder上一时刻的bid

		//get clicks from last rounds
		int numSlots = history.getSlotClicks(t-1).size();

		for (int slot = 0; slot < numSlots; slot++){
			info.add(GSP.bidRangeForSlot(slot, reserve, lastBids));
		}

//		System.out.println("printing " + this.getId() + "'s slot info");
//		//ONLY FOR DEBUG
//
//		for (int i = 0; i < info.size(); ++i) {
//			System.out.println(info.get(i).getFirst() + "  " +  info.get(i).getSecond());
//		}
//		System.out.println();
//		//DEBUG ENDS
		return info;
	}
	
	/**
	 * The function computes the expected utilities for all slots, assuming that all other
	 * agents bid the same amount as in the last round
	 * @param t, the time step (of the current round, not last round)
	 * @param history, the object in which the information of all previous rounds is stored
	 * @param reserve, the reserve price
	 * @return a list of integers (eu1, eu2, ...), in which eu_i is the agent's expected utility of winning slot i 
	 */
	public ArrayList<Integer> expectedUtilities(int t, History history, int reserve){
		ArrayList<Integer> expectedUtils = new ArrayList<Integer>();
		
		/* TODO compute the expected utilities for each available slot, assuming that the other bidders
		 * keep their bids constants from the previous round
		 * 
		 * hint: use history.getSlotClicks(t-1), and slotInfo(t, history, reserve)
		 */
		ArrayList<Integer> q = history.getSlotClicks(t-1);
		int numSlots = q.size();
		ArrayList<Pair> bids = slotInfo(t, history, reserve);
//		ArrayList<Integer> payment = GSP.computePayments(numSlots, bids, reserve);

		for (int k = 0; k < numSlots; ++k) {
			expectedUtils.add(q.get(k) * (this.getValue() - bids.get(k).getFirst()));
		}

		return expectedUtils;
	}
	
	/**
	 * This function computes the slot with the highest expected utility and returns
	 * this slotId together with (minBid,maxBid)_i
	 * @param t, the time step
	 * @param history, the object in which the information of all previous rounds is stored
	 * @param reserve, the reserve price
	 * @return a triple (slot,minBid,maxBid), where slot is the target slot, minBid and maxBid
	 * are the minimum and maximum bid to win this slot assuming all other bid the same amount as in the last round
	 */
	public ArrayList<Integer> targetSlot(int t, History history, int reserve){
		ArrayList<Integer> targetSlot = new ArrayList<Integer>();
		ArrayList<Integer> expectedUtils = expectedUtilities(t, history, reserve);
		int target = 0;
		for (int i = 1; i<expectedUtils.size(); i++){
			if(expectedUtils.get(target) < expectedUtils.get(i)){
				target = i;
			}
		}
		targetSlot.add(target);
		
		ArrayList<Pair> info = slotInfo(t, history, reserve);
		targetSlot.add(info.get(target).getFirst());
		targetSlot.add(info.get(target).getSecond());
		return targetSlot;
	}

}

package sim;

import java.util.ArrayList;

/**
 * Implement your team competition agent here
 * Rename the class by substituting "ABC" by your team's initials,
 * e.g. BudgetMRTK
 *
 */

public class BudgetRZHOU extends Agent{

	public BudgetRZHOU(int id, int value, int budget) {
		super(id, value, budget);
	}

	@Override
	public int initialBid(int reserve) {
		// TODO: find a strategy for the initial bid
		return this.getValue();
	}

	@Override
	public int bid(int t, History history, int reserve) {
		// TODO: find a common bid strategy. An idea might be that first be a BB, ...?

		int numSlots = history.getSlotClicks(t-1).size();

		int retBid = reserve + getValue() / (numSlots);
		return retBid;
	}

}

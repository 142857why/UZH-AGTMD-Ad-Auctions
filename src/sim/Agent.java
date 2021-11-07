package sim;

/**
 * This is an abstract class defining a bidding agent.
 * All bidding agents have to extend this basic class.
 *
 */
public abstract class Agent {

	private final int id;
	private int value;
	private final int totalBudget;
	private int budgetLeft;
	
	public Agent(int id, int value, int budget) {
		this.id = id;
		this.value = value;
		this.totalBudget = budget;
		this.budgetLeft = budget;
	}
	
	/**
	 * This is the bid function for the first round
	 * @param reserve, the reserve price
	 * @return the amount the agent is willing to bid
	 */
	public abstract int initialBid(int reserve);
	
	/**
	 * This is the bid function for all subsequent rounds
	 * @param t, the time step
	 * @param history, the object in which the information of all previous rounds is stored
	 * @param reserve, the reserve price
	 * @return the amount the agent is willing to bid
	 */
	public abstract int bid(int t, History history, int reserve);

	public int getId() {
		return id;
	}


	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public int getTotalBudget() {
		return totalBudget;
	}

	public int getBudgetLeft() {
		return budgetLeft;
	}
	
	public void setBudgetLeft(int budget) {
		this.budgetLeft = budget;
	}
}

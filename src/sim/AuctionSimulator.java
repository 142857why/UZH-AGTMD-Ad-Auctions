package sim;
/**
 * 
 * This is the main class of the auction simulator.
 * It sets the parameters and runs the auctions in the main() function.
 */

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;


public class AuctionSimulator {
	private static String mechanism;
	private static int numRounds = 48;
	private static Integer seed = null;

	private static int reserve = 0;
	private static int totalBudget = 150000;
	private static double dropOff = 0.7;

	private static int minVal = 30;
	private static int maxVal = 180;

	private static int maxPerms = 120;
	private static int numIters = 1;

	private static ArrayList<Integer> totalSpent;
	private static ArrayList<Agent> bidders;
	private static ArrayList<String> agentNames;
	private static ArrayList<Integer> totalUtility;
	private static ArrayList<Integer> totalRevenue;

	private static History history;
	
	private static final boolean DEBUG = true;
	private static boolean logLevel = !DEBUG;

	// our RNG. Always use this one instead of new Random() or
	// Collection.shuffle(list).
	// The user can set a seed in the param file and we need to respect that.
	private static Random random;

	/**
	 * this function simulates one slot auction
	 * 
	 * @param t, the time step
	 * @param clicksOfTopSlot, the number of clicks the top slot gets
	 */
	public static void simulateRound(int t, int clicksOfTopSlot) {


		ArrayList<Integer> currentBids = new ArrayList<Integer>();
		History histCopy = new History(history);

		//the number of bidders above reserve
		int activeBidders = 0;

		//collect bids
		for (int i = 0; i<bidders.size(); i++) {
			Agent a = bidders.get(i);
			int bid;

			//the bid for the first round
			if (t == 0) {
				bid = a.initialBid(reserve);			
			}
			//bid for all other rounds
			else{

				//there is still budget left
				if (totalSpent.get(i) < totalBudget) {
					bid = a.bid(t, histCopy, reserve);
				}
				//no budget left ==> reduce bid to zero
				else {
					bid = 0;
				}				
			}
			currentBids.add(bid);
			if (bid >= reserve) {
				activeBidders++;
			}
		}


		//number of slots = number of active bidders -1
		int numSlots = Math.max(activeBidders - 1, 1);

		//compute slot clicks
		ArrayList<Integer> slotClicks = new ArrayList<Integer>();
		for (int i = 0; i < numSlots; i++) {
			slotClicks.add( (int) (clicksOfTopSlot * Math.pow(dropOff, i) ));
		}

		//compute allocation of mechanism: (occupant, per click payment) for (slot1, slot2, ...)
		ArrayList<ArrayList<Integer>> allocation = null;
		if (mechanism.equals("gsp")) {
			allocation = GSP.computeOutcome(slotClicks,currentBids,reserve);
		}
		else if (mechanism.equals("vcg")) {
			allocation = VCG.computeOutcome(slotClicks,currentBids,reserve);
		}
		else if (mechanism.equals("switch")) {
			if (t < numRounds/2){
				allocation = GSP.computeOutcome(slotClicks,currentBids,reserve);
			}
			else {
				allocation = VCG.computeOutcome(slotClicks,currentBids,reserve);
			}
		}

		ArrayList<Integer> slotOccupants = allocation.get(0);
		ArrayList<Integer> perClickPayments = allocation.get(1);

		//update history information for this round
		history.setBids(currentBids,t);
		history.setPerClickPayments(perClickPayments,t);
		history.setSlotClicks(slotClicks,t);
		history.setSlotOccupants(slotOccupants,t);
		
		//compute total payment per slot
		ArrayList<Integer> slotPayments = new ArrayList<Integer>();
		for (int i = 0; i<slotClicks.size(); i++){
			if(perClickPayments.size()>i){
				int p = slotClicks.get(i) * perClickPayments.get(i);
				slotPayments.add(p);
			}
			else{
				slotPayments.add(0);
			}	
		}
		history.setSlotPayments(slotPayments,t);

		//compute the agents' utilities and total spendings
		ArrayList<Integer> utilities = new ArrayList<Integer>();
		for (int i = 0; i<bidders.size(); i++) {
			int slotIndex = slotOccupants.indexOf(i);
			//is not an occupant
			if (slotIndex == -1){
				utilities.add(0);
			}
			//is an occupant
			else {
				int u = slotClicks.get(slotIndex) * (bidders.get(i).getValue() - perClickPayments.get(slotIndex));
				utilities.add(u);
				totalSpent.set(i, totalSpent.get(i)+slotPayments.get(slotIndex));
			}
		}
		history.setUtilities(utilities,t);
		

		
		//display detailed information if needed
		if (logLevel == DEBUG){
			System.out.println("========================================");
			System.out.println(String.format("Round %d", t));
			System.out.println("========================================");
			System.out.println(String.format("numSlots %d", numSlots));
			System.out.print("Bids: ");
			for (int i = 0; i<currentBids.size(); i++){
				System.out.print(currentBids.get(i)+" ");
			}
			System.out.println();
			
			System.out.print("Slot occupants: ");
			for (int i = 0; i<slotOccupants.size(); i++){
				System.out.print(slotOccupants.get(i)+" ");
			}
			System.out.println();
			
			System.out.print("Slot clicks: ");
			for (int i = 0; i<slotClicks.size(); i++){
				System.out.print(slotClicks.get(i)+" ");
			}
			System.out.println();
			
			System.out.print("Per click payments: ");
			for (int i = 0; i<perClickPayments.size(); i++){
				System.out.print(perClickPayments.get(i)+" ");
			}
			System.out.println();
			
			System.out.print("Slot payments: ");
			for (int i = 0; i<slotPayments.size(); i++){
				System.out.print(slotPayments.get(i)+" ");
			}
			System.out.println();
			
			System.out.print("Values: ");
			for (int i = 0; i<bidders.size(); i++){
				System.out.print(bidders.get(i).getValue()+" ");
			}
			System.out.println();
			
			System.out.print("Utilities: ");
			for (int i = 0; i<utilities.size(); i++){
				System.out.print(utilities.get(i)+" ");
			}
			System.out.println();
			
			System.out.print("Total spent: ");
			for (int i = 0; i<totalSpent.size(); i++){
				System.out.print(totalSpent.get(i)+" ");
			}
			System.out.println();	
		}
	}


	/**
	 * This function simulates a number of slot auctions over a whole day
	 * @param rounds: the number of individual auctions to be run
	 */
	public static void simulateAuction(int rounds) {
		int clicksOfTopSlot;
		for (int t = 0; t < rounds; t++) {
			clicksOfTopSlot = (int) (Math.round(35*Math.cos(Math.PI*t/24) + 40));
			simulateRound(t,clicksOfTopSlot);
		}
	}

	/**
	 * This function computes the total utility of each agents for the whole day
	 * utility = #clicks*(value-perClickPayment)
	 * 
	 * @param utilities, a list of utilities for all agents for every time step
	 * @param numBidders, the number of bidders
	 * @return a list of total utility per agent (agent1, agent2, ...)
	 */
	public static ArrayList<Integer> computeUtilitiesOfAuction(ArrayList<ArrayList<Integer>> utilities, int numBidders){
		ArrayList<Integer> u = new ArrayList<Integer>();
		for (int t = 0; t<utilities.size(); t++){
			for (int i = 0; i<numBidders; i++){
				if (t == 0){
					u.add(utilities.get(t).get(i));
				}
				else{
					u.set(i, u.get(i) + utilities.get(t).get(i));
				}
			}
		}
		return u;
	}

	/**
	 * This function computes the total revenue of a slot auction over a day
	 * 
	 * @param slotPayments the payments made for the slots (i.e. slot1, slot2, ...)
	 * @return the total revenue over a whole day
	 */
	public static int computeRevenueOfAuction(ArrayList<ArrayList<Integer>> slotPayments){
		int revenue = 0;
		for (int t = 0; t<slotPayments.size(); t++){
			int numSlots = slotPayments.get(t).size();
			for (int i = 0; i<numSlots; i++){
				revenue += slotPayments.get(t).get(i);
			}
		}
		return revenue;
	}
	
	/**
	 * This function creates the agents dynamically from the agent names defined
	 * in the list "agents"
	 * 
	 * @param agents a list of class names
	 * @param values a list of values per click
	 * @param budget the total budget over the day
	 * @return a list of agents, according to the specified names
	 */
	public static ArrayList<Agent> generateBiddingAgents(ArrayList<String> agents, ArrayList<Integer>values, int budget) {
		ArrayList<Agent> biddingAgents = new ArrayList<Agent>();
		for (int i = 0; i<agents.size(); i++){
			String name = agents.get(i);
			try {
				String cl = "sim."+name;
				Constructor c = Class.forName(cl).getConstructors()[0];
				Agent a = (Agent)c.newInstance(i,values.get(i),budget);
				biddingAgents.add(a);
			}
			catch (Exception e){
				//System.err.println("Couldn't find class "+name+". Please check the name.");
				e.printStackTrace();
			}
		}
		return biddingAgents;
	}

	

	public static void setMechanism(String mechanism) {
		AuctionSimulator.mechanism = mechanism;
	}

	public static void setNumRounds(int numRounds) {
		AuctionSimulator.numRounds = numRounds;
	}

	public static void setReserve(int reserve) {
		AuctionSimulator.reserve = reserve;
	}

	public static void setTotalBudget(int totalBudget) {
		AuctionSimulator.totalBudget = totalBudget;
	}

	public static void setDropOff(double dropOff) {
		AuctionSimulator.dropOff = dropOff;
	}

	public static void setMinVal(int minVal) {
		AuctionSimulator.minVal = minVal;
	}

	public static void setMaxVal(int maxVal) {
		AuctionSimulator.maxVal = maxVal;
	}

	public static void setMaxPerms(int maxPerms) {
		AuctionSimulator.maxPerms = maxPerms;
	}

	public static void setNumIters(int numIters) {
		AuctionSimulator.numIters = numIters;
	}

	public static void setSeed(int seed){
		AuctionSimulator.seed = new Integer(seed);
	}

	public static void setAgentNames(ArrayList<String> names){
		AuctionSimulator.agentNames = names;
	}

	public static String getMechanism() {
		return mechanism;
	}

	public static int getNumRounds() {
		return numRounds;
	}

	public static Integer getSeed() {
		return seed;
	}
	public static Random getRandom() {
		return random;
	}

	public static int getReserve() {
		return reserve;
	}

	public static int getTotalBudget() {
		return totalBudget;
	}

	public static double getDropOff() {
		return dropOff;
	}

	public static int getMinVal() {
		return minVal;
	}

	public static int getMaxVal() {
		return maxVal;
	}

	public static int getMaxPerms() {
		return maxPerms;
	}

	public static int getNumIters() {
		return numIters;
	}

	public static ArrayList<Integer> getTotalSpent() {
		return totalSpent;
	}

	public static ArrayList<Agent> getBidders() {
		return bidders;
	}

	public static ArrayList<String> getAgentNames() {
		return agentNames;
	}

	public static ArrayList<Integer> getTotalUtility() {
		return totalUtility;
	}

	public static ArrayList<Integer> getTotalRevenue() {
		return totalRevenue;
	}

	public static History getHistory() {
		return history;
	}
	
	public static void setLoglevel(String log){
		if (log.toLowerCase().equals("default")){
			logLevel = !DEBUG;
		}
		else if (log.toLowerCase().equals("debug")){
			logLevel = DEBUG;
		}
	}
	
	
	/**
	 * This is the main method for the simulation
	 * It runs numIters iterations days of slot auctions.
	 * For every iteration, there are numPerms permutation of the per-click value assigned to the agents thatare simulated.
	 * If all factorial(#agents) are run, then all value assignments get used exactly once.
	 * 
	 * @param args string array of parameters as quoted pairs (i.e. "seed 5")
	 */
	public static void main(String[] args) {

		//profile time
		double startTime = System.nanoTime();
		
		/* Set the parameters. unless the parameter is mentioned in the text
		 * file, or passed on the commandline, the default is set.
		 * 
		 * Note that commandline arguments override those in the parameters file.
		 */
		String file = "src/sim/parameters.txt";
		ParseInput.setParameters(file, args);

		//initialize the pseudo-random number generator
		random = new Random();
		if (seed != null){
			random = new Random(seed);
		}

		totalSpent = new ArrayList<Integer>();
		
		final int numBidders = agentNames.size();
//		System.out.println("Number of bidders bidders= " + numBidders);
		//number of permutations
		int numPerms = (int) Math.min(maxPerms,Util.factorial(numBidders));
//		System.out.println("Number of permutations = " + numPerms);

		//initialize revenue and utility
		totalRevenue = new ArrayList<Integer>();
		totalUtility = new ArrayList<Integer>();

		//the main iteration
		for (int i = 0; i<numIters; i++){
			if(logLevel == DEBUG){
				System.out.println();
				System.out.println("========================================");
				System.out.println("Iteration " + i);
				System.out.println("========================================");
				System.out.println();
			}
			ArrayList<Integer> perms = new ArrayList<Integer>();
			
			//initialize the agents			
			ArrayList<Integer> values = new ArrayList<Integer>();
			for (int j = 0; j <numBidders; j++){
				int v;
				if (maxVal != minVal){
					v = random.nextInt(maxVal-minVal) + minVal;
				}
				else{
					v = maxVal;
				}
				values.add(v);
			}
			bidders = generateBiddingAgents(agentNames,values,totalBudget);
			for (int l = 0; l<numPerms;l++){
				perms.add(l);
			}
			for (int p = 0; p < numPerms; p++){
				if(logLevel == DEBUG){
					System.out.println();
					System.out.println("========================================");
					System.out.println("Permutation "+p);
					System.out.println("========================================");
					System.out.println();
				}
				ArrayList<Integer> permValues = (ArrayList<Integer>) values.clone();
				
				int perm = random.nextInt(perms.size());
				
				permValues = Util.permute(perms.get(perm), values);
				perms.remove(perm);

				//start new auction
				history = new History();
				totalSpent.clear();

				//permute the bidder's values
				for (int b = 0; b<numBidders; b++){
					totalSpent.add(0);
					bidders.get(b).setValue(permValues.get(b));
				}
				simulateAuction(numRounds);


				//update statistics (agent utilities, auction revenue)
				ArrayList<Integer> u = computeUtilitiesOfAuction(history.getUtilities(), numBidders);
				
				for (int b = 0; b<bidders.size(); b++){
					
					if (i == 0 && p == 0){
						totalUtility.add((int)(u.get(b)/(double)numPerms/(double)numIters));
					}
					else{
						totalUtility.set(b, totalUtility.get(b)+(int)(u.get(b)/(double)numPerms/(double)numIters));
					}
				}
				int rev = computeRevenueOfAuction(history.getSlotPayments());
				
				totalRevenue.add(rev);

			}
		}

		
		double endTime = System.nanoTime();

		//display results
		double m = Util.mean(totalRevenue);
		double stdDev = Util.stdDeviation(totalRevenue);

		ArrayList<Integer> utilityCopy = (ArrayList<Integer>)totalUtility.clone();
		Collections.sort(utilityCopy,Collections.reverseOrder());
		
		System.out.println("========================================");
		System.out.println("Simulation Results");
		System.out.println("========================================");
		

		System.out.println("Avg. Revenue \t Standard Deviation");
		System.out.println(String.format("%.3f \t %.3f \n", m, stdDev));

		
		System.out.println("Iter \t Revenue");
		
		for (int i = 0; i<numIters; i++){
			System.out.println(String.format("%d \t %d", i, totalRevenue.get(i)));
		}

		System.out.println("\nAgent \t Type \t \t Avg. Utility");
		
		for (int i = 0; i<totalUtility.size(); i++){
			System.out.println(String.format("%d \t %s \t  %d", i, agentNames.get(i),totalUtility.get(i)));
		}
		
		System.out.println();
		System.out.println("Elapsed time: "+ (endTime-startTime)/Math.pow(10, 9) + " s");

	}


}

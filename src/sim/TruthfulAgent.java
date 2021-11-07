package sim;
/**
 * This agent always bids its value truthfully (i.e. bid = value)
 *
 */
public class TruthfulAgent extends Agent{
	
	public TruthfulAgent(int id, int value, int budget){
		super(id,value,budget);
	}
	

	@Override
	public int bid(int t, History history, int reserve) {
		return this.getValue();
	}


	@Override
	public int initialBid(int reserve) {
		return this.getValue();
	}

}

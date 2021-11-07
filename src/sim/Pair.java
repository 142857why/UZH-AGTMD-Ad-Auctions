package sim;

/**
 * This class implements a simple integer pair (first, second), which is sortable.
 * 
 * NOTE: When comparing, we ignore the first component!
 * So the first component is like an additional annotation and the second is
 * for the ordering.
 * (we sometimes also abuse this for things like (min bid, max bid))
 *
 */
public class Pair implements Comparable<Pair>{
	private int first;
	private int second;
	
	public Pair(int ID, int bid) {
		this.first = ID;
		this.second = bid;
	}

	public int getFirst() {
		return first;
	}

	public void setFirst(int first) {
		this.first = first;
	}

	public int getSecond() {
		return second;
	}

	public void setSecond(int second) {
		this.second = second;
	}

	@Override
	public int compareTo(Pair p) throws ClassCastException{
		if (!(p instanceof Pair)){
			throw new ClassCastException("Argument must be instonce of Pair");
		}
		return this.getSecond() - p.getSecond();
		
	}

}

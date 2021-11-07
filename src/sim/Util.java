package sim;

import java.math.BigInteger;
import java.util.ArrayList;

public class Util {
	
	/**
	 * 
	 * @param i, an arbitrary integer >= 0
	 * @return the factorial of i
	 * 
	 * source:
	 * http://stackoverflow.com/questions/3905658/factorial-method-doesnt-work-well
	 */
	public static BigInteger bigFactorial(int i) {
	    BigInteger n = BigInteger.valueOf(i);
	    while (--i > 0)
	        n = n.multiply(BigInteger.valueOf(i));
	    return n;
	}
	/**
	 * 
	 * @param i, an arbitrary integer >=0
	 * @return the factorial of i
	 * this function produces an overflow if i>12,
	 * so it returns MAX_INT (usually 2^31-1)
	 */
	public static long factorial (int i){
		if (i < 13){
			long n = i;
			while (--i > 0){
				n *= i;
			}
			return n;
		}
		else {
			return Integer.MAX_VALUE;
		}
	}
	
	/**
	 * @param n, integer >= 0, that specifies how many pairs are swapped
	 * @param list, a list of integers
	 * @return a permutation of list
	 */
	public static ArrayList<Integer> permute(int n, ArrayList<Integer> list){
		ArrayList<Integer> copy = (ArrayList<Integer>)list.clone();

		//swap the elements (i,i+1) modulo list.size()
		for (int i = 0; i<n; i++){
			int first = i%copy.size();
			int second = (i+1)%copy.size();
			
			int temp = copy.get(first);
			copy.set(first, copy.get(second));
			copy.set(second, temp);
		}
		return copy;
	}
	
	/**
	 * 
	 * @param list, a list of integer values
	 * @return the mean of the list values
	 */
	public static double mean(ArrayList<Integer> list) {
		double mean = 0;
		if (list.size() < 1) return -1;
		
		for (Integer i : list){
			mean += i;
		}
		return mean/(double)list.size();
	}
	
	/**
	 * 
	 * @param list, a list of integer values
	 * @return the standard deviation of the list values
	 */
	public static double stdDeviation(ArrayList<Integer> list){
		double stdDev = 0;
		
		if (list.size() < 1) return -1;
		if (list.size() == 1) return 0;
		
		double mean = mean(list);
		
		for (Integer i : list){
			stdDev += (i - mean)*(i - mean);
		}
		stdDev = Math.sqrt(stdDev/(list.size()-1));
		return stdDev;
	}
	
	
}

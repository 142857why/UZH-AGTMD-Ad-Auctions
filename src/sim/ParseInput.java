package sim;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * This class reads through the parameter file and sets the values accordingly.
 *
 */
public class ParseInput {

	private static final String loglevel = "loglevel";
	private static final String mechanism = "mechanism";
	private static final String numrounds = "numrounds";
	private static final String reserve = "reserve";
	private static final String totalbudget = "totalbudget";
	private static final String dropoff = "dropoff";
	private static final String minval = "minval";
	private static final String maxperms = "maxperms";
	private static final String maxval = "maxval";
	private static final String numiters = "numiters";
	private static final String seed = "seed";
	private static final String agents = "agents";

	
	public static void setParameters(String fileName, String[] args){
		
		try {
			BufferedReader b = new BufferedReader(new FileReader(fileName));
			String line;
			while( (line = b.readLine()) != null){
				//do nothing, line is a comment or empty
				if (line.contains("#") || line.length() < 1){
					continue;
				}
				
				setParameter(line);
			}
			b.close();
		}
		catch (FileNotFoundException e) {
			if (args.length == 0) {
				System.out.println("Couldn't read the parameter file and no arguments given.");
			}
		} 
		catch(IOException ioe){
			ioe.printStackTrace();
		}
		
		for (int i = 0; i < args.length; i++){
			setParameter(args[i]);
		}
	}

	private static void setParameter(String parameter){
		String[] s = parameter.split(" ");
		
		// Switching on a String requires JDK 1.7 or later.
		switch (s[0]) {
		case loglevel:
			AuctionSimulator.setLoglevel(s[1]);
			return;
		case mechanism:
			switch (s[1].toLowerCase()) {
				case "gsp":
					AuctionSimulator.setMechanism("gsp");
					break;
				case "vcg":
					AuctionSimulator.setMechanism("vcg");
					break;
				case "switch":
					AuctionSimulator.setMechanism("switch");
					break;
				default:
					System.err.println("Invalid auction mechanism.");
					System.exit(1);
			}
			return;
		case agents:
			ArrayList<String> agents = new ArrayList<String>();
			for (int i = 1; i< s.length-1; i=i+2){
				String name = s[i];
				Integer k = null;
				try{
					k = new Integer(s[i+1]);
				}catch(NumberFormatException e){
					System.err.println("Agent numbers must be integers.");
					System.exit(1);
				}
				for (int n=0; n<k; n++){
					agents.add(name);
				}
			}
			AuctionSimulator.setAgentNames(agents);
			return;
		case dropoff:
			try {
				Double d = new Double(s[1]);
				AuctionSimulator.setDropOff(d);
			}
			catch (NumberFormatException e){
				System.err.println("dropoff must be a double.");
				System.exit(1);
			}
			return;
		}
		
		/* All the other parameters require an int argument, this way we have to
		 * convert it only once.
		 */
		
		Integer intArg = null;
		try {
			intArg = new Integer(s[1]);
		}
		catch (NumberFormatException e){
			System.err.println(String.format("%s must have an integer argument.", s[0]));
			System.exit(1);
		}
		
		switch (s[0]) {
		case numrounds:
			AuctionSimulator.setNumRounds(intArg);
			return;
		case reserve:
			AuctionSimulator.setReserve(intArg);
			return;
		case totalbudget:
			AuctionSimulator.setTotalBudget(intArg);
			return;
		case minval:
			AuctionSimulator.setMinVal(intArg);
			return;
		case maxval:
			AuctionSimulator.setMaxVal(intArg);
			return;
		case maxperms:
			AuctionSimulator.setMaxPerms(intArg);
			return;
		case numiters:
			AuctionSimulator.setNumIters(intArg);
			return;
		case seed:
			AuctionSimulator.setSeed(intArg);
			return;
		}

	}

}

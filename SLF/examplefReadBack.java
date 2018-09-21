// Example of how to use the lineRead package to read the
// data file.
// The file consists of a number of lines. Each line is defined by
// a number of points. The points are x and y co-ordinates.
// There are a variable number of points including 1 point for
// which a fit is not possible.
// This file provides a working example of reading the file.
// There is no requirement that your programme should follow this
// template.

package SLF;

import java.util.Date;
import uk.ac.brunel.ee.lineRead;
import uk.ac.brunel.ee.UnreadException;
import uk.ac.brunel.ee.RereadException;

public class examplefReadBack {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		double x,y;
		
		Date start = new Date();
		
// Open the file and initialise
		lineRead reader = new lineRead("data.dat");

// Loop over all the lines in the data set		
		while (reader.nextLine()) {
			boolean np=true;
// Loop over all the points associated with the current line
			while (np) {
			    try {
				    np = reader.nextPoint();
			    } catch (UnreadException UE) {
				    System.out.println(UE);
				    System.exit(0);
			    }
// If there is another point read it.
			    if (np){
				    try {
					   x = reader.getX();
					   y = reader.getY();
//
//      >> do the fitting here
//
				    } catch (RereadException RE) {
					    System.out.println(RE);
					    System.exit(0);
				    }
			    }
			}
		}
// Sort out the summary of the run
	    Date end = new Date();
	    long begin=start.getTime();
	    long fin = end.getTime();
	    System.out.println("run time is "+ (fin-begin)+" milliseconds");
	    
	}
}

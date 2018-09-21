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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ee5616_2018.Line;
import ee5616_2018.Point;
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

		List<Line> lines = new ArrayList<>();
		
// Loop over all the lines in the data set		
		while (reader.nextLine()) {
			boolean np=true;
			Line line = new Line();
			lines.add(line);
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
					   
					   Point p = new Point(x, y);
					   line.add(p);
					   
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
	    
	    
	    System.out.println("Lines: "+lines.size());
	    int valid = 0;
	    int validLength = 0;
	    int invalidLength = 0;
	    for (Line line : lines) {
	    	if (line.isValid()) {
	    		try {
		    		System.out.println("  - line length="+line.length()+" slope="+line.slope()+", intersect="+line.intercept());
		    		valid += 1;
		    		validLength += line.length();
	    		} catch (Throwable t) {
	    			System.err.println("Unexpected invalid line");
	    			t.printStackTrace();
	    		}
	    	} else {
	    		invalidLength += line.length();
	    	}
	    }
	    
	    System.out.println("Valid lines: "+valid);
	    System.out.println("Invalid lines: "+(lines.size() - valid));
	    System.out.println("Average valid line length: "+((double)validLength / (double)valid));
	    System.out.println("Average invalid line length: "+((double)invalidLength / (double)(lines.size() - valid)));
	}
}

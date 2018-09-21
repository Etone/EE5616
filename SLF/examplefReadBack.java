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

	public static void main(String[] args) {
		List<Line> lines = readLines("data.dat");
	    System.out.println("Total lines read: "+lines.size());
	    
	    int validLines					= 0;
	    int pointLengthOfValidLines 	= 0;
	    int pointLengthOfInvalidLines 	= 0;
	    int pointLengthOfAllLines 		= 0;
	    
	    double totalSlope		= 0;
	    double totalIntercept 	= 0;
	    
	    for (Line line : lines) {
	    	if (line.isValid()) {
	    		try {
		    		validLines += 1;
		    		pointLengthOfValidLines += line.length();
		    		totalSlope += line.slope();
		    		totalIntercept += line.intercept();
	    		} catch (Throwable t) {
	    			System.err.println("Unexpected invalid line");
	    			t.printStackTrace();
	    			System.exit(0);
	    		}
	    	} else {
	    		pointLengthOfInvalidLines += line.length();
	    	}
	    	pointLengthOfAllLines += line.length();
	    }

	    int invalidLines = lines.size() - validLines;
	    double avgPointLengthOfValidLines 	= (double)pointLengthOfValidLines 	/ (double)validLines;
	    double avgPointLengthOfInvalidLines = (double)pointLengthOfInvalidLines / (double)invalidLines;
	    double avgPointLengthOfAllLines 	= (double)pointLengthOfAllLines 	/ (double)lines.size();
	    
	    System.out.println("Valid lines: "	+ validLines);
	    System.out.println("Invalid lines: "+ invalidLines);
	    System.out.println("Average valid line length: "	+ avgPointLengthOfValidLines);
	    System.out.println("Average invalid line length: "	+ avgPointLengthOfInvalidLines);
	    System.out.println("Average line length: "			+ avgPointLengthOfAllLines);
	    System.out.println();
	    
	    double avgSlope 		= totalSlope	/(double)validLines;
	    double avgInterception	= totalIntercept/(double)validLines;
	    
	    System.out.println("Average slope="+avgSlope+" intercept="+avgInterception);
	    
	    double slopeVarianz2     = 0;
	    double interceptVarianz2 = 0;
	    
	    for (Line line : lines) {
	    	if (line.isValid()) {
	    		try {
		    		double deltaSlope = line.slope() - avgSlope;
		    		slopeVarianz2    += deltaSlope * deltaSlope;
		    		
		    		double deltaIntercept = line.intercept() - avgInterception;
		    		interceptVarianz2    += deltaIntercept * deltaIntercept;
		    		
	    		} catch (Throwable t) {
	    			System.err.println("Unexpected invalid line");
	    			t.printStackTrace();
	    		}
	    	}
	    }
	    
	    slopeVarianz2 		= slopeVarianz2 	/ (double)(validLines-1);
	    interceptVarianz2 	= interceptVarianz2 / (double)(validLines-1);
	    
	    double stdAbwSlope			= Math.sqrt(slopeVarianz2);
	    double stdAbwInterception	= Math.sqrt(interceptVarianz2);
	    
	    System.out.println("Standardabweichung: slope="+stdAbwSlope+", intercept="+stdAbwInterception);
	}
	
	private static List<Line> readLines(String file) {
		Date start = new Date();
		
		lineRead reader = new lineRead(file);
		List<Line> lines = new ArrayList<>();
				
		while (reader.nextLine()) {
			try {
				Line line = new Line();
				while (reader.nextPoint()) {
					line.add(new Point(reader.getX(), reader.getY()));
				}
				lines.add(line);
			} catch (UnreadException|RereadException e) {
				e.printStackTrace();
			    System.exit(0);
			}
		}

	    Date end = new Date();
	    System.out.println("Load time was "+ (end.getTime()-start.getTime())+" milliseconds");
	    return lines;
	}
}

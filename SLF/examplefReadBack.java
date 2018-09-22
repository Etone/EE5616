package SLF;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import ee5616_2018.Line;
import ee5616_2018.Point;
import uk.ac.brunel.ee.lineRead;
import uk.ac.brunel.ee.UnreadException;
import uk.ac.brunel.ee.RereadException;

public class examplefReadBack {

	public static void main(String[] args) {
		System.out.println("  #### Loading data ");
		System.out.println();
		TotalLoadTimings loadTimings = readLines("data.dat");

		System.out.println();
		System.out.println();
		System.out.println("  #### Calculating load statistics");
		System.out.println();
		
		List<Line> lines = loadTimings.lineTimings
				.values()
				.stream()
				.flatMap(v -> v.stream())
				.map(v -> v.line)
				.collect(Collectors.toList());
				
	    System.out.println("Total lines read: "+lines.size());
	    System.out.println("Load times per point number:");
	    System.out.println();
	    System.out.println(" NoP   NoL nextPoint[ms]     getX[ms]     getY[ms]    total[ms]");
	    loadTimings.lineTimings.entrySet().stream().forEach(entry -> {
	    	System.out.printf(Locale.ENGLISH, " %3d,  %3d,     %7.3f,     %7.3f,     %7.3f,     %7.3f%n",
	    			entry.getKey(),
	    			entry.getValue().size(),
	    			entry.getValue().stream().mapToDouble(a -> a.timeNextPoint).average().getAsDouble()/(double)entry.getKey(),
	    			entry.getValue().stream().mapToDouble(a -> a.timeGetX).average().getAsDouble()/(double)entry.getKey(),
	    			entry.getValue().stream().mapToDouble(a -> a.timeGetY).average().getAsDouble()/(double)entry.getKey(),
	    			entry.getValue().stream().mapToDouble(a -> a.timeTotal).average().getAsDouble()/(double)entry.getKey()
			);
	    });
	    System.out.println();
	    System.out.println(" : NoP=Number of Points, NoL=Number of Lines");
	    System.out.println();
	    
	    double timeTotalAllLines = loadTimings.lineTimings.values().stream().flatMap(v -> v.stream()).mapToDouble(v -> v.timeTotal).sum();
	    double timeTotalCount = loadTimings.lineTimings.values().stream().flatMap(v -> v.stream()).mapToDouble(v -> v.timeTotal).count();
	    System.out.printf(Locale.ENGLISH, "Avg nextLine call: %4.3fms%n", (loadTimings.timeNextLine/(double)timeTotalCount));
	    System.out.printf(Locale.ENGLISH, "Avg load time per line: %4.3fms%n", (timeTotalAllLines/(double)timeTotalCount));
	    

		System.out.println();
		System.out.println();
		System.out.println("  #### Looking into the loaded values");
		System.out.println();
	    
	    int validLines					= 0;
	    int pointLengthOfValidLines 	= 0;
	    int pointLengthOfInvalidLines 	= 0;
	    int pointLengthOfAllLines 		= 0;
	    
	    double totalSlope		= 0;
	    double totalIntercept 	= 0;
	    
	    TreeMap<Integer, List<Long[]>> isValidTimes = new TreeMap<>();
	    
	    for (Line line : lines) {
	    	try {
	    		long beforeSlope = System.nanoTime();
	    		line.slope();
	    		long beforeIntercept = System.nanoTime();
	    		line.intercept();
	    		long beforeValid = System.nanoTime();

		    	
		    	isValidTimes.computeIfAbsent(line.length(), k -> new ArrayList<>()).add(new Long[]{
		    			beforeIntercept-beforeSlope,
		    			beforeValid-beforeIntercept,
		    			beforeValid-beforeSlope
    			});
	    	} catch (Throwable t) {
    			// ignore
    		}

	    	try {
		    	if (line.isValid()) {
		    		validLines += 1;
		    		pointLengthOfValidLines += line.length();
		    		totalSlope += line.slope();
		    		totalIntercept += line.intercept();
		    	} else {
		    		pointLengthOfInvalidLines += line.length();
		    	}
	    	} catch (Throwable t) {
    			// should never happen!
	    		t.printStackTrace();
	    		System.exit(0);
    		}
	    	pointLengthOfAllLines += line.length();
	    }

	    System.out.println();
	    System.out.println(" NoP   NoL     slope[ms]  delta in[ms]     total[ms]    summed[ms]");
	    isValidTimes.entrySet().stream().forEach(entry -> {
	    	System.out.printf(Locale.ENGLISH, " %3d,  %3d,     %7.6f,     %7.6f,     %7.6f,     %7.6f%n",
	    			entry.getKey(),
	    			entry.getValue().size(),
	    			entry.getValue().stream().mapToDouble(a -> a[0]).average().getAsDouble()/1000000.0,
	    			entry.getValue().stream().mapToDouble(a -> a[1]).average().getAsDouble()/1000000.0,
	    			entry.getValue().stream().mapToDouble(a -> a[2]).average().getAsDouble()/1000000.0,
	    			entry.getValue().stream().mapToDouble(a -> a[0]).sum()/1000000.0
			);
	    });
	    System.out.println();
	    System.out.println("                                                        ----------");
	    System.out.printf(
	    		Locale.ENGLISH,
	    		"                                                         %8.6f%n",
	    		isValidTimes.values().stream().flatMap(s -> s.stream()).mapToDouble(a -> a[0]).sum()/1000000.0
		);
	    System.out.println(" : NoP=Number of Points, NoL=Number of Lines");
	    System.out.println();
	    

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
	    
	    System.out.println("Std-deviation: slope="+stdAbwSlope+", intercept="+stdAbwInterception);
	}
	
	private static TotalLoadTimings readLines(String file) {
		TotalLoadTimings timings = new TotalLoadTimings();
		long start = System.currentTimeMillis();
		
		lineRead reader = new lineRead(file);
		
		boolean hasNextLine = true;
				
		while (hasNextLine) {
			long beforeNextLine = System.currentTimeMillis();
			hasNextLine = reader.nextLine();
			timings.timeNextLine += System.currentTimeMillis() - beforeNextLine;
			
			if (hasNextLine) {
				try {
					long lineStart = System.currentTimeMillis();
					LineLoadTimings lineTimings = new LineLoadTimings();
					lineTimings.line = new Line();

					boolean hasNextPoint = true;
					
					while (hasNextPoint) {
						long beforeNextPoint = System.currentTimeMillis();
						hasNextPoint = reader.nextPoint();
						lineTimings.timeNextPoint += System.currentTimeMillis() - beforeNextPoint;
						
						if (hasNextPoint) {
							long beforeX = System.currentTimeMillis();
							double x = reader.getX();
							long beforeY = System.currentTimeMillis();
							double y = reader.getY();
							long afterY = System.currentTimeMillis();
							lineTimings.timeGetX += beforeY - beforeX;
							lineTimings.timeGetY += afterY - beforeY;
							lineTimings.line.add(new Point(x, y));
						}
					}
					
					lineTimings.timeTotal = System.currentTimeMillis() - lineStart;
					timings
						.lineTimings.computeIfAbsent(lineTimings.line.length(), k -> new ArrayList<>())
						.add(lineTimings);
					
				} catch (UnreadException|RereadException e) {
					e.printStackTrace();
				    System.exit(0);
				}
			}
		}

	    long end = System.currentTimeMillis();
	    timings.timeTotal = end - start;
	    System.out.println("Load time was "+ timings.timeTotal +" milliseconds");
	    return timings;
	}
	
	public static class LineLoadTimings {
		private long timeTotal;
		private long timeGetX;
		private long timeGetY;
		private long timeNextPoint;
		private Line line;
	}
	
	public static class TotalLoadTimings {
		private long timeTotal;
		private long timeNextLine;
		private Map<Integer, List<LineLoadTimings>> lineTimings = new TreeMap<>();
	}
}

package ee5616_2018;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import ee5616_2018.Line;
import ee5616_2018.Line.RegressionFailedException;
import ee5616_2018.Point;
import uk.ac.brunel.ee.lineRead;
import uk.ac.brunel.ee.UnreadException;
import uk.ac.brunel.ee.RereadException;

public class Main {

	private static final String DATA_SHORT_PATH = "C:\\Workspace\\TAE\\EE5616\\data_short.dat";
	private static final String DATA_LONG_PATH = "C:\\Workspace\\TAE\\EE5616\\data_long.dat";
	
	private static final double ONE_MILLION = 1000000;
	
	public static void main(String[] args) throws UnreadException, RereadException, RegressionFailedException {
		
		List<Line> lines = readLines(DATA_LONG_PATH);
		
		TimeStatistics.calculateUncachedSlopeAndIntercept(lines);
		LineStatistics.calculateStats(lines);
		TimeStatistics.calculateDurationCalcAndRead();
		
		
		printFormattedOutput(formatOutput());
	}
	
	private static List<Line> readLines(String file) throws UnreadException, RereadException {
		
		TimeStatistics.timeStampStartRead = System.currentTimeMillis();
		lineRead reader = new lineRead(file);
		List<Line> lines = new ArrayList<>();
				
		while (reader.nextLine()) {
			Line line = new Line();
			while (reader.nextPoint()) {
				
				//time GetX and GetY
				long timeBeforeGetX = System.nanoTime();
				double readerX = reader.getX();
				long timeBeforeGetY = System.nanoTime();
				double readerY = reader.getY();
				long timeAfterGetY = System.nanoTime();
				
				long[] timeXY= TimeStatistics.timeGetXGetY.computeIfAbsent(line, (k) -> new long[] {0,0});
				timeXY[0] += timeBeforeGetY - timeBeforeGetX;
				timeXY[1] += timeAfterGetY - timeBeforeGetY;

				line.add(new Point(readerX, readerY));
			}
			lines.add(line);
			TimeStatistics.timestampedInputRead.put(System.currentTimeMillis(), line);
		}
		
		TimeStatistics.calculateAvgSlopeForLineLength();
		TimeStatistics.calculateAvgGetXAndGetYForLineLength();

		TimeStatistics.timeStampStopRead = System.currentTimeMillis();
	    return lines;
	}
	
	private static void printFormattedOutput(String output) {
		System.out.println(output);
	}
	
	private static String formatOutput() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("------------------------------" + System.lineSeparator());
		sb.append("       Time Statistics        " + System.lineSeparator());
		sb.append("------------------------------" + System.lineSeparator());
		sb.append(System.lineSeparator());
		sb.append(buildDurationOutput());
		sb.append(System.lineSeparator());
		sb.append("Slope Timing %n");
		sb.append(buildSlopeAndInterceptTimingOutput());
		sb.append(System.lineSeparator());
		sb.append("Intercept Timing %n");
		sb.append("GetX GetY Timings %n");
		sb.append(buildGetXGetYOutput());
		sb.append(System.lineSeparator());	
		
		sb.append("------------------------------" + System.lineSeparator());
		sb.append("       Line Statistics        " + System.lineSeparator());
		sb.append("------------------------------" + System.lineSeparator());
		sb.append(System.lineSeparator());
		
		//Output Data
		sb.append(buildLineOutputString());
		sb.append(System.lineSeparator());
		
		sb.append(buildAvgNumberOfPointsOutput());
		sb.append(System.lineSeparator());
		sb.append(System.lineSeparator());
		
		sb.append(buildSlopeOutput());
		sb.append(System.lineSeparator());
		
		sb.append(buildInterceptOutput());
		
		
		return sb.toString();
	}
	
	private static String buildGetXGetYOutput() {
		StringBuilder sb = new StringBuilder();
		
		TimeStatistics.timeAvgGetXGetY.forEach((key, value) -> {
			sb.append(String.format("%3d points       %11.3fms        %11.3fms %n",
					key, value[0] / ONE_MILLION, value[1] / ONE_MILLION));
		});
		
		return sb.toString();
	}

	private static String buildSlopeAndInterceptTimingOutput() {
		StringBuilder sb = new StringBuilder();
		
		TimeStatistics.timeSlopeLineLength.forEach((key, value) -> {
			sb.append(String.format("%3d points       %11.3fms %n", key, value / ONE_MILLION));
		});
		
		return sb.toString();
	}

	private static String buildDurationOutput() {
		StringBuilder sb = new StringBuilder();
		
		sb.append(String.format("Reading data took %d ms", TimeStatistics.durationRead));
		sb.append(System.lineSeparator());
		sb.append(String.format("Calculating statistics took %d ms", TimeStatistics.durationCalc));
		sb.append(System.lineSeparator());
		sb.append(System.lineSeparator());
		
		return sb.toString();
	}

	private static String buildLineOutputString() {
		return String.format("Total number of Lines: %d (%d valid | %d invalid)", LineStatistics.numberOfTotalLines, 
				LineStatistics.numberOfValidLines, LineStatistics.numberOfInvalidLines);
	}
	
	private static Object buildInterceptOutput() {
		return String.format("Average intercept: %f | Standard deviation: %f", LineStatistics.avgIntercept, LineStatistics.stdDevIntercept);
	}

	private static Object buildSlopeOutput() {
		return String.format("Average slope: %f | Standard deviation: %f", LineStatistics.avgSlope, LineStatistics.stdDevSlope);
	}

	private static Object buildAvgNumberOfPointsOutput() {
		StringBuilder sb = new StringBuilder();
		
		sb.append(String.format("Average number of Points per Line (including invalid Lines): %f", LineStatistics.avgPointsPerLineTotal));
		sb.append(System.lineSeparator());
		sb.append(String.format("Average number of Points per Line (excluding invalid Lines): %f", LineStatistics.avgPointsPerLineValid));
		
		return sb.toString();
		
	}

	private static class LineStatistics {
		
		private static int numberOfValidLines = 0;
		private static int numberOfInvalidLines = 0;
		private static int numberOfTotalLines = 0;
		private static int numberOfPointsValid = 0;
		private static int numberOfPointsInvalid = 0;
		private static int numberOfPointsTotal = 0;
		
		private static double avgPointsPerLineValid = 0;
		private static double avgPointsPerLineTotal = 0;


		private static double avgSlope = 0;
		private static double totalSlope = 0;
		private static double stdDevSlope = 0;
		private static double varianceSlope = 0;
		
		private static double avgIntercept = 0;
		private static double totalIntercept = 0;
		private static double stdDevIntercept = 0;
		private static double varianceIntercept = 0;
		
		private static List<Line> lines;
		
		public static void calculateStats(List<Line> lines) throws RegressionFailedException {
			TimeStatistics.timeStampStartCalc = System.currentTimeMillis();
			LineStatistics.lines = lines;
			
			for(Line line : lines) {
				if(line.isValid()) {
					handleValidLineStats(line);
				} else {
					handleInvalidLineStats(line);
				}
			}
			numberOfTotalLines = numberOfInvalidLines + numberOfValidLines;
			numberOfPointsTotal = numberOfPointsInvalid + numberOfPointsValid;
			
			calculateAverages();
			calculateVarianceAndStdDev();
			TimeStatistics.timeStampStopCalc = System.currentTimeMillis();
		}

		private static void handleValidLineStats(Line validLine) throws RegressionFailedException {
			numberOfValidLines++;
			
			numberOfPointsValid += validLine.length();
			
			totalIntercept += validLine.intercept();
			totalSlope += validLine.slope();
		}
		
		private static void handleInvalidLineStats(Line invalidLine) {
			numberOfInvalidLines++;
			numberOfPointsInvalid += invalidLine.length();
		}
		
		private static void calculateAverages() {
			avgPointsPerLineValid = (double) numberOfPointsValid/ (double) numberOfValidLines;
			avgPointsPerLineTotal = (double) numberOfPointsTotal / (double) numberOfTotalLines;
			
			avgSlope = totalSlope / (double) numberOfValidLines;
			avgIntercept = totalIntercept / (double) numberOfValidLines;
		}
		
		private static void calculateVarianceAndStdDev() throws RegressionFailedException {
			for(Line line : lines) {
				if(line.isValid()) {
					varianceSlope += ((line.slope() - avgSlope)*(line.slope() - avgSlope))/(double) numberOfValidLines;
					varianceIntercept += ((line.intercept() - avgIntercept)*(line.intercept() - avgIntercept))/(double) numberOfValidLines;
				}
			}
			stdDevSlope = Math.sqrt(varianceSlope);
			stdDevIntercept = Math.sqrt(varianceIntercept);
		}
	}
	
	private static class TimeStatistics {
		private static long timeStampStartRead;
		private static long timeStampStopRead;
		private static long durationRead;
		
		
		private static Map<Long, Line> timestampedInputRead = new HashMap<>();
		private static Map<Line, Long> timeForSlope = new HashMap<>();
		private static Map<Line, Long> timeForIntercept = new HashMap<>();
		private static Map<Integer, Double> timeSlopeLineLength = new TreeMap<>();
		
		private static Map<Line, long[]> timeGetXGetY = new HashMap<>();
		private static Map<Integer, double[]> timeAvgGetXGetY = new TreeMap<>();
		
		
		private static long timeStampStartCalc;
		private static long timeStampStopCalc;
		private static long durationCalc;
		
		public static void calculateDurationCalcAndRead() {
			durationRead = timeStampStopRead - timeStampStartRead;
			durationCalc = timeStampStopCalc - timeStampStartCalc;
		}

		public static void calculateAvgGetXAndGetYForLineLength() {
			Set<Integer> lengths = timeGetXGetY.keySet().stream().map(l -> l.length()).collect(Collectors.toSet());
			
			timeAvgGetXGetY.putAll(lengths.stream().map(length -> {
				double avg0 = timeGetXGetY.entrySet().stream()
						.filter(entry -> length == entry.getKey().length())
						.mapToDouble(e -> e.getValue()[0])
						.average()
						.getAsDouble();
				
				double avg1 = timeGetXGetY.entrySet().stream()
						.filter(entry -> length == entry.getKey().length())
						.mapToDouble(e -> e.getValue()[1])
						.average()
						.getAsDouble();
				return new AbstractMap.SimpleEntry<>(length, new double[] {avg0, avg1});
			}).collect(Collectors.toMap(Entry::getKey, Entry::getValue)));
		}

		public static void calculateUncachedSlopeAndIntercept(List<Line> lines) {
			for (Line line : lines) {
				try {
					long beforeSlope = System.nanoTime();
					line.slope();
					long beforeIntercept = System.nanoTime();
					line.intercept();
					long afterIntercept = System.nanoTime();
					
					timeForSlope.put(line, beforeIntercept - beforeSlope);
					timeForIntercept.put(line, afterIntercept - beforeIntercept);
				} catch (RegressionFailedException e) {
					//invalid Line, not useful for our analysis
				}
			}
			calculateAvgSlopeForLineLength();
		}

		//NANI
		private static void calculateAvgSlopeForLineLength() {
			Set<Integer> lengths = timeForSlope.keySet().stream().map(l -> l.length()).collect(Collectors.toSet());
			timeSlopeLineLength.putAll(lengths.stream().map(length -> {
				double avg = timeForSlope.entrySet().stream()
						.filter(entry -> length == entry.getKey().length())
						.mapToDouble(e -> e.getValue())
						.average()
						.getAsDouble();
				return new AbstractMap.SimpleEntry<>(length, avg);
			}).collect(Collectors.toMap(Entry::getKey, Entry::getValue)));
		}
	}
}

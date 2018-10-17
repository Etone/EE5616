package ee5616_2018;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Supplier;

import ee5616_2018.Line.RegressionFailedException;
import ex3.DurationTimer;
import ex3.LineClassStatistic;
import ex3.LineStatistics;
import ex3.Tuple;
import uk.ac.brunel.ee.RereadException;
import uk.ac.brunel.ee.UnreadException;
import uk.ac.brunel.ee.lineRead;

public class AnalysisRunner {

	//Paths to dat files
	private static final String DATA_SHORT = "C:\\Workspace\\TAE\\EE5616\\data_short.dat";
	private static final String DATA_LONG = "C:\\Workspace\\TAE\\EE5616\\data_long.dat";
	
	//lines to analyse
	private static List<Line> lines = new ArrayList<>();
	
	//timestamp start reading and end reading for display purpose
	private static long startRead = 0l;
	private static long stopRead = 0l;
	
	//Map classes to statistics for this class
	private static Map<Integer, LineClassStatistic> mapClass2TimeStatistics = new TreeMap<>();
	
	public static void main(String[] args) throws RegressionFailedException, UnreadException, RereadException {	
		
		long startExecution = System.currentTimeMillis();
		startRead = System.currentTimeMillis();
		readLineFromFile(DATA_SHORT);
		stopRead = System.currentTimeMillis();
		
		//Uncached slope and intercept calc
		measureSlopeAndIntercept();
		
		
		//Initialize lines in LineStatistics by reference
		LineStatistics.initLines(lines);
		LineStatistics.calcMetrics();
		mapClass2TimeStatistics.forEach((lineClass, lineClassStatistics) -> lineClassStatistics.calcAvgs());
		
		long startPrint = System.currentTimeMillis();
		printFormatted(format());
		long endPrint = System.currentTimeMillis();
		
		System.out.println(String.format("Printing Results took %d miliseconds", endPrint - startPrint));
		
		long stopExecution = System.currentTimeMillis();
		System.out.println(String.format("Execution took %d seconds", (stopExecution-startExecution) / 1000));
	}
	
	private static void measureSlopeAndIntercept() {
		for (Line line : lines) {
			LineClassStatistic lcs = mapClass2TimeStatistics.get(line.length());
			Tuple<Double, Long> timeSlope = DurationTimer.measureDurationForCallInNs(()-> {
				try {
					return line.slope();
				} catch (RegressionFailedException e) {
					//Happens when line is not valid, as we cannot check without caching slope and intercept
					return Double.NaN;
				}
			});
			
			Tuple<Double, Long> timeIntercept = DurationTimer.measureDurationForCallInNs(()-> {
				try {
					return line.intercept();
				} catch (RegressionFailedException e) {
					//Happens when line is not valid, as we cannot check without caching slope and intercept
					return Double.NaN;
				}
			});
			
			lcs.timingsSlopeUnchached.add(timeSlope.getDuration());
			lcs.timingsInterceptUncached.add(timeIntercept.getDuration());
			
		}
	}

	private static void printFormatted(String output) {
		System.out.println(output);
	}

	private static String format() {
		StringBuilder sb = new StringBuilder();
		
		//Timing Section NYI
		sb.append("------------------------------------------------------------------" + System.lineSeparator());
		sb.append("                              TIMING                              " + System.lineSeparator());
		sb.append("------------------------------------------------------------------" + System.lineSeparator());
		
		sb.append(System.lineSeparator());
		
		sb.append(String.format("Time needed to read file: %ds %n %n", (stopRead - startRead) / 1000));
		
		sb.append("Timings per points in line (avg)" + System.lineSeparator());
		sb.append("________________________________" + System.lineSeparator());
		sb.append(System.lineSeparator());
		sb.append(String.format("%6s %21s %21s %21s %21s %31s %n", "Class*",
				"getX (ms)", "getY (ms)",
				"slope (ms)", "intercept (ms)",
				"Loadtimes for lines(ms)**"));
		for (int i : mapClass2TimeStatistics.keySet()) {
			LineClassStatistic lcs = mapClass2TimeStatistics.get(i);
			sb.append(String.format("%5d; %20.7f; %20.7f; %20.7f; %20.7f; %30.2f; %n",
					i, lcs.getXAvg, lcs.getYAvg, lcs.slopeUnchachedAvg / (double) 1000000, lcs.interceptUnchachedAvg/ (double) 1000000, lcs.loadTimeLineAvg));
		}
		sb.append(System.lineSeparator());
		sb.append(" *  each class represents lines with n points (f.e. Class 2 means all lines with length=2)" + System.lineSeparator());
		sb.append("**  including time for measuring times for getX and getY"+ System.lineSeparator());


		sb.append(System.lineSeparator());
		//Metrics Section
		sb.append("------------------------------------------------------------------" + System.lineSeparator());
		sb.append("                              METRICS                             " + System.lineSeparator());
		sb.append("------------------------------------------------------------------" + System.lineSeparator());
		sb.append(String.format("Total number of lines: %d ( %d valid | %d invalid ) %n",
				LineStatistics.numberInvalidLines + LineStatistics.numberValidLines,
				LineStatistics.numberValidLines, LineStatistics.numberInvalidLines));
		sb.append(String.format("Average number of points (valid) line %.2f %n", LineStatistics.avgNumberPointsPerLine ));
		sb.append(String.format("%20s %10f %30s %10f %n",
				"Average slope:", LineStatistics.avgSlope,
				"standard deviation slope:", LineStatistics.stdDevSlope));
		sb.append(String.format("%20s %10f %30s %10f %n",
				"Average intercept:", LineStatistics.avgIntercept,
				"standard deviation intercept:", LineStatistics.stdDevIntercept));
		
		return sb.toString();
	}
	
	private static void readLineFromFile(String path) throws UnreadException, RereadException {
		lineRead reader = new lineRead(path);
		
		List<Long> bufferGetXDurations = new ArrayList<>();
		List<Long> bufferGetYDurations = new ArrayList<>();

		while (reader.nextLine()) {
			long startTimeReadLine = System.currentTimeMillis();
			Line line = new Line();
			while (reader.nextPoint()) {
				Tuple<Double, Long> getX =  DurationTimer.measureDurationForCallInMs(() -> {
					try {
						return reader.getX();
					} catch (RereadException e) {
						return Double.NaN;
					}
				});
				
				Tuple<Double, Long> getY =  DurationTimer.measureDurationForCallInMs(() -> {
					try {
						return reader.getY();
					} catch (RereadException e) {
						return Double.NaN;
					}
				});
				
				//Buffer time measurement for getX and getY until end of line (unknown length of line until then)
				bufferGetXDurations.add(getX.getDuration());
				bufferGetYDurations.add(getY.getDuration());
				
				line.add(new Point(getX.getResult(), getY.getResult()));
			}

			long stopTimeReadLine = System.currentTimeMillis();
			//Store results from time measurement GetX and GetY
			storeTimeMeasurementsGetXGetY(line.length(), bufferGetXDurations, bufferGetYDurations);
			
			//calc duration for Line and add to Class Stats
			storeTimeMeasurementReadLine(line.length(), stopTimeReadLine-startTimeReadLine);
			
			//reset buffers
			bufferGetXDurations.clear();
			bufferGetYDurations.clear();
			
			lines.add(line);
		}
	}
	
	private static void storeTimeMeasurementReadLine(int length, long durationReadLine) {
		//dont have to check if key is there as it is checked when storing getX and getY which is called first
		//Normally not smart, but for this it will do
		mapClass2TimeStatistics.get(length).timingsLoadingLine.add(durationReadLine);
	}

	private static void storeTimeMeasurementsGetXGetY(int lineClass, List<Long> bufferGetXDurations,
			List<Long> bufferGetYDurations) {

		if (mapClass2TimeStatistics.containsKey(lineClass)) {
			mapClass2TimeStatistics.get(lineClass).timingsGetX.addAll(bufferGetXDurations);
			mapClass2TimeStatistics.get(lineClass).timingsGetY.addAll(bufferGetYDurations);
		} else {
			LineClassStatistic lcs = new LineClassStatistic();
			lcs.timingsGetX.addAll(bufferGetXDurations);
			lcs.timingsGetY.addAll(bufferGetYDurations);
			
			mapClass2TimeStatistics.put(lineClass, lcs);
		}
		
	}
}

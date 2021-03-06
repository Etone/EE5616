package ex3;

import java.util.List;

import ee5616_2018.Line;
import ee5616_2018.Line.RegressionFailedException;

/**
	 * Class that holds all data which can only be calculated from all lines
	 * @author csa
	 *
	 */
	public class LineStatistics {
		//Everything public as in LineClassStatistics, this is only for easier access
		public static int numberValidLines = 0;
		public static int numberInvalidLines = 0;
		
		public static double numberPoints = 0.0;
		public static double avgNumberPointsPerLine = Double.NaN;
		
		public static double totalSlope = 0.0;
		public static double avgSlope = Double.NaN;
		public static double stdDevSlope = Double.NaN;
		public static double varianceSlope = 0.0;
		
		public static double totalIntercept = 0.0;
		public static double avgIntercept = Double.NaN;
		public static double stdDevIntercept = Double.NaN;
		public static double varianceIntercept = 0.0;
		
		private static List<Line> lines = null;
		
		public static void initLines(List<Line> newLines) {
			lines = newLines;
		}
		
		public static void calcMetrics() throws RegressionFailedException {
			
			//Check if lines is initialized, else return with no calculation
			if(lines == null) {
				return;
			}
			
			//1. number of Valid Lines and Invalid Lines
			//1a. count all points
			//1b. For Valid Lines calc slope
			calcNumberVaildInvalidLines();
			//2. Avg Number of Points per Line
			calcAvgNumberPointsPerLine();
			//3. Avg for slope() and intercept()
			calcAvgSlopeIntercept();
			//5. calc Variance slope and intercept (avg needed for this)
			calcVarianceSlopeIntercept();
			//6. calc std-dev slope and intercept
			calcStdDevSlopeIntercept();
		}

		private static void calcStdDevSlopeIntercept() {
			stdDevIntercept = Math.sqrt(varianceIntercept);
			stdDevSlope = Math.sqrt(varianceSlope);
		}

		private static void calcVarianceSlopeIntercept() throws RegressionFailedException {
			for (Line line : lines) {
				//if not valid, slope and intercept not calculable, skip this line
				if (!line.isValid()) continue;
				
				//Variance = ((current - avg)^2) / count items
				varianceIntercept += ((line.intercept() - avgIntercept) * (line.intercept() - avgIntercept))/ (double) numberValidLines;
				varianceSlope += ((line.slope() - avgSlope) * (line.slope() - avgSlope))/ (double) numberValidLines;
			}
		}


		private static void calcAvgSlopeIntercept() {
			avgSlope = (double) totalSlope / (double) numberValidLines;
			avgIntercept = (double) totalIntercept / (double) numberValidLines;
		}

		private static void calcAvgNumberPointsPerLine() {
			//cast to double to get correct result, else it always would cut off floating points
			avgNumberPointsPerLine = (double) numberPoints / (double) numberValidLines;
		}

		private static void calcNumberVaildInvalidLines() throws RegressionFailedException {
			for (Line l : lines) {
				if (l.isValid()) {
					numberPoints += l.length();
					numberValidLines++;
					totalIntercept += l.intercept();
					totalSlope += l.slope();
				} else {
					numberInvalidLines++;
				}
			}
		}	
}
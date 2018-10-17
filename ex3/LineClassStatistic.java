package ex3;

import java.util.ArrayList;
import java.util.List;

/**
	 * Class that holds all data for a single LineClass (f.e. 3 point long lines)
	 * @author csa
	 *
	 */
	public class LineClassStatistic {
		//Everything is kept public for more readable AnalysisRunner impl. This is normally a bad idea but not mission critical this time
		public List<Long> timingsGetX = new ArrayList<>();
		public List<Long> timingsGetY = new ArrayList<>();
		public List<Long> timingsSlopeUnchached = new ArrayList<>();
		public List<Long> timingsInterceptUncached = new ArrayList<>();
		public List<Long> timingsLoadingLine = new ArrayList<>();
		
		public double getXAvg = Double.NaN;
		public double getYAvg = Double.NaN;
		public double loadTimeLineAvg = Double.NaN;

		public double slopeUnchachedAvg = Double.NaN;
		public double interceptUnchachedAvg = Double.NaN;
		
		public void calcAvgs() {
			getXAvg = calcAvgFromList(timingsGetX);
			getYAvg = calcAvgFromList(timingsGetY);
			slopeUnchachedAvg = calcAvgFromList(timingsSlopeUnchached);
			interceptUnchachedAvg = calcAvgFromList(timingsInterceptUncached);
			loadTimeLineAvg = calcAvgFromList(timingsLoadingLine);
		}
		
		private double calcAvgFromList(List<Long> list) {
			return list.stream().mapToLong(a -> a).average().orElseGet(() -> Double.NaN);
		}
	}

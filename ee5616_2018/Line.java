package ee5616_2018;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Line {
	
	private final List<Point> points = new ArrayList<>();
	private double intercept = Double.NaN;
	private double slope = Double.NaN;

	public Line() {
		
	}
	
	public Line(Point[] points) {
		this.points.addAll(Arrays.asList(points));
	}
	
	public void add(Point point) {
		this.points.add(point);
		this.intercept = Double.NaN;
		this.slope = Double.NaN;
	}
	
	public int length() {
		return this.points.size();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		
		if (obj instanceof Line) {
			Line other = (Line)obj;
			if (other.length() != this.length()) {
				return false;
			}
			
			for (Point p : this.points) {
				if (!other.points.contains(p)) {
					return false;
				}
			}
			
			for (Point p : other.points) {
				if (!this.points.contains(p)) {
					return false;
				}
			}

			return true;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		int code = this.points.size();
		for (Point p : this.points) {
			if (p != null) {
				code += p.hashCode();
			}
		}
		return code;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("(");
		for (int i = 0; i < this.points.size(); ++i) {
			if (i > 0) {
				builder.append(' ');
			}
			builder.append(String.valueOf(this.points.get(i)));
			if (i+1 < this.points.size()) {
				builder.append(',');
				builder.append(System.lineSeparator());
			}
		}
		builder.append(')');
		return builder.toString();
	}
	
	public boolean isValid() {
		try {
			this.ensureEnoughPointsForRegressionCalculation();
			this.slope();
			this.intercept();
			return true;
		} catch (RegressionFailedException e) {
			return false;
		}
	}
	
	public double slope() throws RegressionFailedException {
		if (Double.isNaN(this.slope)) {
			this.ensureEnoughPointsForRegressionCalculation();
			
			double xy = calcXY();
			double x = calcX();
			double y = calcY();
			double x2 = calcX2();

			this.slope = (xy - (x * y)) / (x2 - (x*x));
		}
		return this.slope;
	}
	
	public double intercept() throws RegressionFailedException {
		if (Double.isNaN(this.intercept)) {
			this.ensureEnoughPointsForRegressionCalculation();
			
			double y = calcY();
			double a = slope();
			double x = calcX();
			
			this.intercept = y - (a * x);
		}
		return this.intercept;
	}
	
	private double calcX() throws RegressionFailedException {
		double result = 0;
		for (Point p : this.points) {
			result += this.getX(p);
		}
		return result / (double)this.points.size();
	}
	
	private void ensureEnoughPointsForRegressionCalculation() throws RegressionFailedException {
		if (this.points.size() < 2) {
			throw new RegressionFailedException();
		}
	}
	
	private double calcY() throws RegressionFailedException {
		double result = 0;
		for (Point p : this.points) {
			result += this.getY(p);
		}
		return result / (double)this.points.size();
	}
	
	private double calcX2() throws RegressionFailedException {
		double result = 0;
		for (Point p : this.points) {
			result += (this.getX(p) * this.getX(p));
		}
		return result / (double)this.points.size();
	}
	
	private double calcXY() throws RegressionFailedException {
		double result = 0;
		for (Point p : this.points) {
			result += (this.getX(p) * this.getY(p));
		}
		return result / (double)this.points.size();
		
	}
	
	private double getX(Point p) throws RegressionFailedException {
		return getValue(p != null ? p.getX() : null);
	}
	
	private double getY(Point p) throws RegressionFailedException {
		return getValue(p != null ? p.getY() : null);
	}
	
	private double getValue(Double v) throws RegressionFailedException {
		if (v == null || !Double.isFinite(v)) {
			throw new RegressionFailedException();
		}
		return v;
	}
	
	
	
	public class RegressionFailedException extends Exception {
		private static final long serialVersionUID = 1L;
	}
	
}

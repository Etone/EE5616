package ee5616_2018;

import java.util.Locale;

public class Point {

	private double x;
	private double y;
	
	public Point() {
		x = 0.0;
		y = 0.0;
	}
	
	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Used to get the distance to origin
	 * 
	 * @return double representing the distance of Point to origin
	 */
	public double norm() {
		return Math.sqrt((Math.pow(x, 2)) + Math.pow(y, 2));
	}
	
	/**
	 * Rotates the Point by angle theta.
	 * 
	 * @param theta the angle to return around
	 * @throws AngleOutOfRangeException thrown when -180.0 < theta < 180.0 
	 */
	public void rotate(double theta) throws AngleOutOfRangeException {
		if(theta < -180.0 || theta > 180.0) {
			throw new AngleOutOfRangeException("Angle must be between -180 and 180 degree");
		}
		
		double radTheta = Math.toRadians(theta);
		
		double tempX = x * Math.cos(radTheta) - y * Math.sin(radTheta);
		double tempY = y * Math.cos(radTheta) + x * Math.sin(radTheta);
		
		x = tempX;
		y = tempY;
	}
	
	public void displace(Point p) {
		x = x + p.x;
		y = y + p.y;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(x);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(y);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		//Objects are same instance, faster comparison
		if (this == obj)
			return true;
		
		Point other = (Point) obj;
		if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
			return false;
		if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return String.format(Locale.ENGLISH, "( %+.4E, %+.4E )", x, y);
	}
	
	/*
	 * Exceptions used in this class
	 */
	public class AngleOutOfRangeException extends Exception{
		private static final long serialVersionUID = -3726276637567215315L;
		
		public AngleOutOfRangeException(String message) {
			super(message);
		}
	}
}

package ee5616_2018;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import ee5616_2018.Line.RegressionFailedException;

class LineTest {
	
	public static final double ACCURACY = 0.0000000000001;
	
	Point[] points3ordered = new Point[] {new Point(0,0), new Point(0,1), new Point(0,2)};
	Point[] points3scrambled = new Point[] {new Point(0,2), new Point(0,0), new Point(0,1)};
	Point[] points3 = new Point[] {new Point(1,0), new Point(0,1), new Point(1,2)};
	Point[] points45degree = new Point[] {new Point(1,1), new Point(2,2), new Point(3,3)};
	
	
	/*
	 * CTOR
	 */
	@Test
	void testEmptyLineDefaultCtor() {
		Line l = new Line();
		
		assertEquals(0, l.length());
	}
	
	@Test
	void testEmptyLineCtorWithEmptyArray() {
		Line l = new Line(new Point[0]);
		
		assertEquals(0, l.length());
	}
	
	/*
	 * METHOD ADD
	 */
	@Test
	void testAddAdditionalPointToLine() {
		Line l = new Line();
		l.add(new Point(0,1));
		
		assertEquals(1, l.length());
	}
	
	@Test
	void testAddNullToLineShouldNotAppend() {
		Line l = new Line();
		l.add(null);
		
		assertEquals(0,l.length());
	}
	
	/*
	 * METHOD LENGTH
	 */
	@Test
	void testLengthReturnsCorrectLengthNotEmpty() {
		Line l1 = new Line(points3);
		
		assertEquals(3, l1.length());
	}
	
	/*
	 * METHOD EQUALS
	 */
	@Test
	void testLinesNotEqualWithDifferentPoints() {
		Line l1 = new Line(points3ordered);
		Line l2 = new Line(points3);
		
		assertNotEquals(l1, l2);
	}
	
	@Test
	void testLineEqualsDifferentOrderPoints() {
		Line l1 = new Line(points3ordered);
		Line l2 = new Line(points3scrambled);
		
		assertEquals(l1, l2);
	}
	
	@Test
	void testEqualsSamePointTwiceInLine() {
		Point p1 = new Point();
		Point p2 = new Point();
		
		Point p3 = new Point(0,1);
		
		Line l1 = new Line(new Point[] {p1,p2});
		Line l2 = new Line(new Point[] {p1, p3});
		
		assertNotEquals(l1, l2);
	}
	
	@Test
	void testObjectEqualsItself() {
		Line l1 = new Line();
		
		assertEquals(l1, l1);
	}
	
	@Test
	void testLinesWithDifferentLenghtDontEqual() {
		Line l1 = new Line();
		Line l2 = new Line(points3);
		
		assertNotEquals(l1, l2);
	}
	
	@Test
	void testLineDowsNotEqualNull() {
		Line l1 = new Line();
		
		assertNotEquals(l1, null);
	}
	
	/*
	 * METHOD HASHCODE
	 */
	@Test
	void testTwoLinesHaveSameHashCodeDifferentOrder() {
		Line l1 = new Line(points3ordered);
		Line l2 = new Line(points3scrambled);
		
		assertEquals(l1.hashCode(), l2.hashCode());
	}
	
	@Test
	void testTwoLinesHaveSameHashCodeSameOrder() {
		Line l1 = new Line(points3);
		Line l2 = new Line(points3);
		
		assertEquals(l1.hashCode(), l2.hashCode());
	}
	
	/*
	 * METHOD toString
	 */
	@Test
	void testToStringEmptyLine() {
		Line l1 = new Line();
		
		assertEquals("()", l1.toString());
	}
	
	@Test
	void testToStringOnePoint(){
		Line l1 = new Line();
		l1.add(new Point(0,1));
		String wantedOutput = "(( +0.0000E+00, +1.0000E+00 ))";
		
		assertEquals(wantedOutput, l1.toString());
	}
	
	@Test
	void testToStringWithThreePointsInLine() {
		Line l1 = new Line(points3);
		String wantedOutput = String.format(
				"(%s," + System.lineSeparator() 
				+ " %s," + System.lineSeparator()
				+ " %s)", points3[0], points3[1], points3[2]);
		
		assertEquals(wantedOutput, l1.toString());
	}
	
	/*
	 * METHOD isValid
	 */
	@Test
	void testIsInvalidWhenZeroPointsAreStored() {
		Line l1 = new Line();
		
		assertFalse(l1.isValid());
	}
	
	@Test
	void testIsInvalidWhenOnePointIsStored() {
		Line l1 = new Line();
		l1.add(new Point());
		
		assertFalse(l1.isValid());
	}
	
	@Test
	void testIsInvalidWhenSlopeOrInterceptCanNotBeCalculated() {
		Line l1 = new Line(points3ordered);
		
		assertFalse(l1.isValid());
	}
	
	@Test
	void testLineIsValid() {
		Line l1 = new Line(points45degree);
		
		assertTrue(l1.isValid());
	}
	
	/*
	 * METHOD slope
	 */
	@Test
	void testReturnsCorrectSlopeForLine() throws RegressionFailedException{
		Line l1 = new Line(points45degree);
		
		assertEquals(1.0, l1.slope());
	}
	
	@Test
	void testReturnsCorrectSlopeForSixPoints() throws RegressionFailedException {
		Line l1 = new Line();
		
		l1.add(new Point(1,0));		
		l1.add(new Point(3,0));
		l1.add(new Point(5,0));
		l1.add(new Point(0,1));
		l1.add(new Point(0,3));
		l1.add(new Point(0,5));
		
		assertEquals(-0.627906976744186, l1.slope(), ACCURACY);
	}
	
	@Test
	void testThrowsExceptionWhenSlopeNotCalculable() {
		Line l1 = new Line(points3ordered);
		
		assertThrows(RegressionFailedException.class, () -> l1.slope());
	}
	
	@Test
	void testAddPointsOtherSlope() throws RegressionFailedException {
		Line l1 = new Line(points45degree);
		
		assertEquals(1.0, l1.slope());
		
		l1.add(new Point(0,1));
		l1.add(new Point(0,2));
		l1.add(new Point(0,3));
		
		assertNotEquals(1.0, l1.slope());
	}
	
	/*
	 * METHOD intercept
	 */
	@Test
	void testInterceptReturnsCorrectValue() throws RegressionFailedException {
		Point p1 = new Point(0,1);
		Point p2 = new Point(1,2);
		Line l1 = new Line(new Point[] {p1,p2});
		
		assertEquals(1.0, l1.intercept());
	}
	
	@Test
	void testInterceptReturnsCorrectValueForLargerLines() throws RegressionFailedException {
		Line l1 = new Line();
		
		l1.add(new Point(1,0));		
		l1.add(new Point(3,0));
		l1.add(new Point(5,0));
		l1.add(new Point(0,1));
		l1.add(new Point(0,3));
		l1.add(new Point(0,5));
		
		assertEquals(2.441860465116279, l1.intercept(), ACCURACY);
	}
	
	@Test
	void testInterceptThrowsExceptionWhenNotCalculable() {
		Line l1 = new Line();

		l1.add(new Point(0,1));
		l1.add(new Point(0,3));
		l1.add(new Point(0,5));

		assertThrows(RegressionFailedException.class, () -> l1.intercept());
	}
	
	@Test
	void testInterceptWithNaNforValidLine() {
		Line l1 = new Line();
		
		l1.add(new Point(Double.NaN, Double.NaN));
		l1.add(new Point());
		
		assertThrows(RegressionFailedException.class, ()-> l1.intercept());
	}
}

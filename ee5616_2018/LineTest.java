package ee5616_2018;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import ee5616_2018.Line.RegressionFailedException;

class LineTest {
	
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
	void testThrowsExceptionWhenSlopeNotCalculatable() {
		Line l1 = new Line(points3ordered);
		
		assertThrows(RegressionFailedException.class, () -> l1.slope());
	}
	
	@Test
	void testAddPointOtherSlope() throws RegressionFailedException {
		Line l1 = new Line(points45degree);
		
		assertEquals(1.0, l1.slope());
		
		l1.add(new Point(0,1));
		l1.add(new Point(0,2));
		l1.add(new Point(0,3));
		
		assertNotEquals(1.0, l1.slope());
	}
}

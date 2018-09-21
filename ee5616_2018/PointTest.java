package ee5616_2018;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import ee5616_2018.Point.AngleOutOfRangeException;

class PointTest {

	@Test
	void testDefaultConstructor() {
		Point p = new Point();
		assertEquals(0, p.getX());
		assertEquals(0, p.getY());
	}
	
	@Test
	void testConstructorWithXY() {
		double x = 128.31;
		double y = -63.23;
		Point p = new Point(x, y);
		assertEquals(x, p.getX());
		assertEquals(y, p.getY());
	}
	
	@Test
	void testXAccessorForMax() {
		testXAccessor(Double.MAX_VALUE);
		testYAccessor(Double.MAX_VALUE);
	}
	
	@Test
	void testXAccessorForZero() {
		testXAccessor(0);
		testYAccessor(0);
	}

	@Test
	void testAccessorForMin() {
		testXAccessor(Double.MIN_VALUE);
		testYAccessor(Double.MIN_VALUE);
	}
	
	@Test
	void testAccessorForMaxNeg() {
		testXAccessor(-(Double.MAX_VALUE-1));
		testYAccessor(-(Double.MAX_VALUE-1));
	}
	
	@Test
	void testAccessorForNaN() {
		testXAccessor(Double.NaN);
		testYAccessor(Double.NaN);
	}

	@Test
	void testAccessorForPosIninite() {
		testXAccessor(Double.POSITIVE_INFINITY);
		testYAccessor(Double.POSITIVE_INFINITY);
	}

	@Test
	void testAccessorForNegIninite() {
		testXAccessor(Double.NEGATIVE_INFINITY);
		testYAccessor(Double.NEGATIVE_INFINITY);
	}
	
	void testXAccessor(double x) {
		Point p = new Point();
		p.setX(x);
		if (Double.isNaN(x)) {
			assertTrue(Double.isNaN(p.getX()));
		} else {
			assertEquals(x, p.getX());			
		}
	}
	
	void testYAccessor(double y) {
		Point p = new Point();
		p.setY(y);
		if (Double.isNaN(y)) {
			assertTrue(Double.isNaN(p.getY()));
		} else {
			assertEquals(y, p.getY());			
		}
	}
	
	@Test
	void testEqualsForEqualPoints() {
		double x = 125.12;
		double y = 899.13;
		Point p1 = new Point(x, y);
		Point p2 = new Point(x, y);
		assertEquals(p1, p2);
	}
	
	@Test
	void testEqualsForEqualPointsNaN() {
		Point p1 = new Point(Double.NaN, Double.NaN);
		Point p2 = new Point(Double.NaN, Double.NaN);
		assertEquals(p1, p2);
	}
	
	@Test
	void testNotEqualsForDifferentPoints() {
		double x = 125.12;
		double y = 899.13;
		Point p1 = new Point(x, y);
		Point p2 = new Point(y, x);
		assertNotEquals(p1, p2);
	}
	
	@Test
	void testEqualsForSamePoint() {
		double x = 125.12;
		double y = 899.13;
		Point p1 = new Point(x, y);
		assertEquals(p1, p1);
	}
	
	@Test
	void testHashCode() {
		double x = 142.92;
		double y = -91.52;
		Point p1 = new Point(x, y);
		Point p2 = new Point(x, y);
		assertEquals(p1, p2);
		assertEquals(p1.hashCode(), p1.hashCode());
	}
	
	@Test
	void testHashCodeNaN() {
		Point p1 = new Point(Double.NaN, Double.NaN);
		Point p2 = new Point(Double.NaN, Double.NaN);
		assertEquals(p1, p2);
		assertEquals(p1.hashCode(), p1.hashCode());
	}
	
	@Test
	void testToStringZero() {
		assertEquals("( +0.0000E+00, +0.0000E+00 )", new Point());
	}
	
	@Test
	void testToStringNaN() {
		assertEquals("( +0.0000E+00, +0.0000E+00 )", new Point(Double.NaN, Double.NaN));
	}
	
	@Test
	void testToStringNeg() {
		assertEquals(
				"( -1.0000E+03, +2.0000E+05 )",
				new Point(1000, 200000)
		);
	}
	
	@Test
	void testNorm0_0() {
		assertEquals(0, new Point(0, 0).norm());
	}
	
	@Test
	void testNorm0_1() {
		assertEquals(1, new Point(0, 1).norm());
	}
	
	@Test
	void testNorm1_0_neg() {
		assertEquals(1, new Point(-1, 0).norm());
	}
	
	@Test
	void testNorm2_3() {
		assertEquals(Math.sqrt(13), new Point(2, 3).norm());
	}
	
	@Test
	void testRotateZeroOrigin() throws AngleOutOfRangeException {
		Point p = new Point(0, 0);
		p.rotate(0);
		assertEquals(0, p.getX());
		assertEquals(0, p.getY());
	}
	
	@Test
	void testRotateZero() throws AngleOutOfRangeException {
		Point p = new Point(1, 0);
		p.rotate(0);
		assertEquals(1, p.getX());
		assertEquals(0, p.getY());
	}
	
	@Test
	void testRotatePlus90() throws AngleOutOfRangeException {
		Point p = new Point(1, 0);
		p.rotate(90);
		assertEquals(0, p.getX());
		assertEquals(-1, p.getY());
	}
	
	@Test
	void testRotatePlus180() throws AngleOutOfRangeException {
		Point p = new Point(1, 0);
		p.rotate(180);
		assertEquals(-1, p.getX());
		assertEquals(0, p.getY());
	}
	
	@Test
	void testRotateNeg90() throws AngleOutOfRangeException {
		Point p = new Point(1, 0);
		p.rotate(-90);
		assertEquals(0, p.getX());
		assertEquals(1, p.getY());
	}
	
	@Test
	void testRotateNeg180() throws AngleOutOfRangeException {
		Point p = new Point(1, 0);
		p.rotate(-180);
		assertEquals(-1, p.getX());
		assertEquals(0, p.getY());
	}
	
	@Test
	void testRotatePos180Fail() {
		try {
			new Point(0, 0).rotate(180 + Double.MIN_VALUE);
			fail("Missing exception");
		} catch (AngleOutOfRangeException e) {
			
		}
	}
	
	@Test
	void testRotateNeg180Fail() {
		try {
			new Point(0, 0).rotate(-180 - Double.MIN_VALUE);
			fail("Missing exception");
		} catch (AngleOutOfRangeException e) {
			
		}
	}
	
	@Test
	void testDisplaceNonZero() {
		double x_base = 3.141;
		double y_base = 82.23;
		double x_displace = 237.1;
		double y_displace = -1.28;
		Point p = new Point(x_base, y_base);
		p.displace(new Point(x_displace, y_displace));
		assertEquals(x_base + x_displace, p.getX());
		assertEquals(y_base + y_displace, p.getY());
	}

	@Test
	void testDisplacZero() {
		double x_base = 3.141;
		double y_base = 82.23;
		Point p = new Point(x_base, y_base);
		p.displace(new Point());
		assertEquals(x_base, p.getX());
		assertEquals(y_base, p.getY());
	}

	@Test
	void testDisplacNaN() {
		double x_base = 3.141;
		double y_base = 82.23;
		Point p = new Point(x_base, y_base);
		p.displace(new Point(Double.NaN, Double.NaN));
		assertTrue(Double.isNaN(p.getX()));
		assertTrue(Double.isNaN(p.getY()));
	}

	@Test
	void testDisplacInfinite() {
		double x_base = 3.141;
		double y_base = 82.23;
		Point p = new Point(x_base, y_base);
		p.displace(new Point(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY));
		assertTrue(Double.isInfinite(p.getX()));
		assertTrue(p.getX() > 0);
		assertTrue(Double.isInfinite(p.getY()));
		assertTrue(p.getY() < 0);
	}

}

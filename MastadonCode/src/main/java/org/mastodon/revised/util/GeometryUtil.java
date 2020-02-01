package org.mastodon.revised.util;

public class GeometryUtil
{
	/**
	 * Computes the distance of a point {@code A0 (x0, y0)} to a segment defined
	 * by two points {@code A1 (x1, y1)} and {@code A2 (x2, y2)}. If the
	 * projection of {@code A0} on the segment does not lie between {@code A1}
	 * and {@code A2}, this method returns the distance to the closest segment
	 * end-point.
	 *
	 * @param x0
	 *            x coordinate of point A0.
	 * @param y0
	 *            y coordinate of point A0.
	 * @param x1
	 *            x coordinate of segment extremity A1.
	 * @param y1
	 *            y coordinate of segment extremity A1.
	 * @param x2
	 *            x coordinate of segment extremity A2.
	 * @param y2
	 *            y coordinate of segment extremity A2.
	 * @return the distance from the point to the line segment.
	 */
	public static final double segmentDist( final double x0, final double y0, final double x1, final double y1, final double x2, final double y2 )
	{
		return Math.sqrt( squSegmentDist( x0, y0, x1, y1, x2, y2 ) );
	}

	/**
	 * Computes the squared distance of a point {@code A0 (x0, y0)} to a segment
	 * defined by two points {@code A1 (x1, y1)} and {@code A2 (x2, y2)}. If the
	 * projection of {@code A0} on the segment does not lie between {@code A1}
	 * and {@code A2}, this method returns the distance to the closest segment
	 * end-point.
	 *
	 * @param x0
	 *            x coordinate of point A0.
	 * @param y0
	 *            y coordinate of point A0.
	 * @param x1
	 *            x coordinate of segment extremity A1.
	 * @param y1
	 *            y coordinate of segment extremity A1.
	 * @param x2
	 *            x coordinate of segment extremity A2.
	 * @param y2
	 *            y coordinate of segment extremity A2.
	 * @return the squared distance from the point to the line segment.
	 */
	public static final double squSegmentDist( final double x0, final double y0, final double x1, final double y1, final double x2, final double y2 )
	{
		final double l12sq = ( x2 - x1 ) * ( x2 - x1 ) + ( y2 - y1 ) * ( y2 - y1 );

		final double x = ( ( x0 - x1 ) * ( x2 - x1 ) + ( y0 - y1 ) * ( y2 - y1 ) ) / l12sq;
		if ( x < 0 ) { return ( x0 - x1 ) * ( x0 - x1 ) + ( y0 - y1 ) * ( y0 - y1 ); }
		if ( x > 1 ) { return ( x0 - x2 ) * ( x0 - x2 ) + ( y0 - y2 ) * ( y0 - y2 ); }

		final double c = ( y2 - y1 ) * x0 - ( x2 - x1 ) * y0 + x2 * y1 - y2 * x1;
		return ( c * c ) / l12sq;
	}

	private GeometryUtil()
	{}

}

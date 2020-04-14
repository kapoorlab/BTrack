package budDetector;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import net.imglib2.AbstractEuclideanSpace;
import net.imglib2.RealLocalizable;

public class BCellobject extends AbstractEuclideanSpace implements RealLocalizable, Comparable<BCellobject> {
	
	private final ConcurrentHashMap< String, Double > features = new ConcurrentHashMap< String, Double >();
	public static AtomicInteger IDcounter = new AtomicInteger( -1 );
	
	// Bud the cell is inside
	public final ArrayList<Budobject> mybuds;
	// Growth points of the bud
	public final ArrayList<Budpointobject> mybudpoints;
	// Location of the cell
	public double[] Location;
	// Distance from center of cell to nearest bud growth point
	public double closestGrowthPoint;
	// Distance from center of cell to nearest bud point
	public double closestBudPoint;
	
	// Other non important cell properties
	public double cellArea;
	public double TotalIntensity;
	public double AverageIntensity;
	
	
	
	public BCellobject(final ArrayList<Budobject> mybuds, final ArrayList<Budpointobject> mybudpoints, final double[] Location, final double closestGrowthPoint, final double closestBudPoint,
			final double cellArea, final double TotalIntenity, final double AverageIntensity) {
		
		
		
		super(3);
		this.mybuds = mybuds;
		this.mybudpoints = mybudpoints;
		this.Location = Location;
		this.closestGrowthPoint = closestGrowthPoint;
		this.closestBudPoint = closestBudPoint;
		this.cellArea = cellArea;
		this.TotalIntensity = TotalIntenity;
		this.AverageIntensity = AverageIntensity;
		
	}




	
	public final Double getFeature(final String feature) {
		
		return features.get(feature);
	}
	public final void putFeature( final String feature, final Double value )
	{
		features.put( feature, value );
	}
	
	
	@Override
	public int compareTo(BCellobject o) {
		return hashCode() - o.hashCode();
	}

	@Override
	public void localize(float[] position) {
		int n = position.length;
		for (int d = 0; d < n; ++d)
			position[d] = getFloatPosition(d);
		
	}

	@Override
	public void localize(double[] position) {
		int n = position.length;
		for (int d = 0; d < n; ++d)
			position[d] = getFloatPosition(d);
		
	}

	@Override
	public float getFloatPosition(int d) {
		return (float) Location[d];
	}

	@Override
	public double getDoublePosition(int d) {
		return Location[d];
	}
	
	/**
	 * Returns the difference between the location of two clouds, this operation
	 * returns ( <code>A.diffTo(B) = - B.diffTo(A)</code>)
	 *
	 * @param target
	 *            the Cloud to compare to.
	 * @param int
	 *            n n = 0 for X- coordinate, n = 1 for Y- coordinate
	 * @return the difference in co-ordinate specified.
	 */
	public double diffTo(final BCellobject target, int n) {

		final double thisBloblocation = Location[n];
		final double targetBloblocation = target.Location[n];
		return thisBloblocation - targetBloblocation;
	}
	
	/**
	 * Returns the squared distance between two clouds.
	 *
	 * @param target
	 *            the Cloud to compare to.
	 *
	 * @return the distance to the current cloud to target cloud specified.
	 */

	public double squareDistanceTo(BCellobject target) {
		// Returns squared distance between the source Blob and the target Blob.

		final double[] sourceLocation = Location;
		final double[] targetLocation = target.Location;

		double distance = 0;

		for (int d = 0; d < sourceLocation.length; ++d) {

			distance += (sourceLocation[d] - targetLocation[d]) * (sourceLocation[d] - targetLocation[d]);
		}

		return distance;
	}
	public double DistanceTo(BCellobject target, final double alpha, final double beta) {
		// Returns squared distance between the source Blob and the target Blob.

		final double[] sourceLocation = Location;
		final double[] targetLocation = target.Location;

		double distance = 1.0E-5;

		for (int d = 0; d < sourceLocation.length; ++d) {

			distance += (sourceLocation[d] - targetLocation[d]) * (sourceLocation[d] - targetLocation[d]);
		}

			return distance;
	}


}

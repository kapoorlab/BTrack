package budDetector;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import net.imglib2.AbstractEuclideanSpace;
import net.imglib2.RealLocalizable;

public class Budpointobject extends AbstractEuclideanSpace implements RealLocalizable, Comparable<Budpointobject> {
	
	
	public final double[] Location;
	public final double velocity;
	public final  int t;
	private final int ID;
	private String name;
	private final ConcurrentHashMap< String, Double > features = new ConcurrentHashMap< String, Double >();
	public static AtomicInteger IDcounter = new AtomicInteger( -1 );
	
	public Budpointobject(final double[] Location, final double velocity, final int t ) {
		
		
		
		super(3);
		this.Location = Location;
		this.velocity = velocity;
		this.t = t;
		
		this.ID = IDcounter.incrementAndGet();
		this.name = "ID" + ID;
		putFeature(TIME,  (double)t);
		putFeature(XPOSITION, Location[0]);
		putFeature(YPOSITION, Location[1]);
		putFeature(Velocity, velocity);
	}

	
	public static final String XPOSITION = "XPOSITION";
	public static final String YPOSITION = "YPOSITION";
	public static final String Velocity = "Velocity";
	public static final String TIME = "TIME";
	
	
	public void setName(final String name) {
		
		this.name = name;
	}
	
	public int ID() {
		
		return ID;
	}
	
	public final Double getFeature(final String feature) {
		
		return features.get(feature);
	}
	public final void putFeature( final String feature, final Double value )
	{
		features.put( feature, value );
	}
	
	
	@Override
	public int compareTo(Budpointobject o) {
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
	public double diffTo(final Budpointobject target, int n) {

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

	public double squareDistanceTo(Budpointobject target) {
		// Returns squared distance between the source Blob and the target Blob.

		final double[] sourceLocation = Location;
		final double[] targetLocation = target.Location;

		double distance = 0;

		for (int d = 0; d < sourceLocation.length; ++d) {

			distance += (sourceLocation[d] - targetLocation[d]) * (sourceLocation[d] - targetLocation[d]);
		}

		return distance;
	}
	public double DistanceTo(Budpointobject target, final double alpha, final double beta) {
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

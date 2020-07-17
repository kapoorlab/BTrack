package greenDetector;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import Buddy.plugin.trackmate.util.AlphanumComparator;
import budDetector.BCellobject;
import greenDetector.Greenobject;
import net.imglib2.AbstractEuclideanSpace;
import net.imglib2.RealLocalizable;
import tracker.BUDDYDimension;
import tracker.GREENDimension;

public class Greenobject extends AbstractEuclideanSpace implements RealLocalizable, Comparable<Greenobject> {
	
	
	public final RealLocalizable Greencenter;
	
	public final double Greenarea;
	
	public final double Greenperimeter;
	
	public  int time;
	
	public double Radius;
	
	private String name;
	
	public final int ID;
	
	private final ConcurrentHashMap< String, Double > features = new ConcurrentHashMap< String, Double >();
	
	public Greenobject(final RealLocalizable Greencenter, final double Greenarea, final double Greenperimeter, final double Radius, final int ID, final int time) {
		
		super(3);
		
		this.Greencenter = Greencenter;
		
		this.Greenarea = Greenarea;
		
		this.Radius = Math.sqrt(Greenarea);
		
		this.Greenperimeter = Greenperimeter;
		
		this.time = time;
		
		this.ID = ID;
	}



	public static final String XPOSITION = "XPOSITION";
	public static final String YPOSITION = "XPOSITION";
	public static final String ZPOSITION = "ZPOSITION";
	public static final String RADIUS = "RADIUS";
	public static final String TIME = "TIME";
	
	

	/** The name of the spot X position feature. */
	public static final String POSITION_X = "POSITION_X";

	/** The name of the spot Y position feature. */
	public static final String POSITION_Y = "POSITION_Y";
	
	/** The name of the spot Y position feature. */
	public static final String POSITION_Z = "POSITION_Z";
	
	/** The name of the spot Y position feature. */
	public static final String Velocity = "Velocity";
	
	/** The name of the spot Y position feature. */
	public static final String Size = "Size";
	
	/** The name of the spot T position feature. */
	public static final String POSITION_T = "POSITION_T";


	/** The position features. */
	public final static String[] POSITION_FEATURES = new String[] { POSITION_X, POSITION_Y , POSITION_Z};
	static int numfeatures = 6;
	public final static Collection<String> FEATURES = new ArrayList<>(numfeatures);

	/** The 7 privileged spot feature names. */
	public final static Map<String, String> FEATURE_NAMES = new HashMap<>(numfeatures);

	/** The 7 privileged spot feature short names. */
	public final static Map<String, String> FEATURE_SHORT_NAMES = new HashMap<>(numfeatures);

	/** The 7 privileged spot feature dimensions. */
	public final static Map<String, GREENDimension> FEATURE_GREENDIMENSIONS = new HashMap<>(numfeatures);

	/** The 7 privileged spot feature isInt flags. */
	public final static Map<String, Boolean> IS_INT = new HashMap<>(numfeatures);

	static {
		FEATURES.add(POSITION_X);
		FEATURES.add(POSITION_Y);
		FEATURES.add(POSITION_Z);
		FEATURES.add(Velocity);
		FEATURES.add(Size);
		FEATURES.add(POSITION_T);

		FEATURE_NAMES.put(POSITION_X, "X");
		FEATURE_NAMES.put(POSITION_Y, "Y");
		FEATURE_NAMES.put(POSITION_Z, "Z");
		FEATURE_NAMES.put(Velocity, "V");
		FEATURE_NAMES.put(Size, "R");
		FEATURE_NAMES.put(POSITION_T, "T");

		FEATURE_SHORT_NAMES.put(POSITION_X, "X");
		FEATURE_SHORT_NAMES.put(POSITION_Y, "Y");
		FEATURE_SHORT_NAMES.put(POSITION_Z, "Z");
		FEATURE_SHORT_NAMES.put(Velocity, "V");
		FEATURE_SHORT_NAMES.put(Size, "R");
		FEATURE_SHORT_NAMES.put(POSITION_T, "T");

		FEATURE_GREENDIMENSIONS.put(POSITION_X, GREENDimension.POSITION);
		FEATURE_GREENDIMENSIONS.put(POSITION_Y, GREENDimension.POSITION);
		FEATURE_GREENDIMENSIONS.put(POSITION_Z, GREENDimension.POSITION);
		FEATURE_GREENDIMENSIONS.put(Velocity, GREENDimension.POSITION);
		FEATURE_GREENDIMENSIONS.put(Size, GREENDimension.POSITION);
		FEATURE_GREENDIMENSIONS.put(POSITION_T, GREENDimension.TIME);

		IS_INT.put(POSITION_X, Boolean.FALSE);
		IS_INT.put(POSITION_Y, Boolean.FALSE);
		IS_INT.put(POSITION_Z, Boolean.FALSE);
		IS_INT.put(Velocity, Boolean.FALSE);
		IS_INT.put(POSITION_T, Boolean.FALSE);
		IS_INT.put(Size, Boolean.FALSE);
	}

	
	
	public void setName(final String name) {
		
		this.name = name;
	}
	
	public String getName()
	{
		return this.name;
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
	public double diffTo(final Greenobject target, int n) {

		final double thisBloblocation = getDoublePosition(n);
		final double targetBloblocation = target.getDoublePosition(n);
		return thisBloblocation - targetBloblocation;
	}
	
	public double diffTo( final Greenobject s, final String feature )
	{
		final double f1 = features.get( feature ).doubleValue();
		final double f2 = s.getFeature( feature ).doubleValue();
		return f1 - f2;
	}
	
	public double normalizeDiffTo( final Greenobject s, final String feature )
	{
		final double a = features.get( feature ).doubleValue();
		final double b = s.getFeature( feature ).doubleValue();
		if ( a == -b )
			return 0d;
		
		return Math.abs( a - b ) / ( ( a + b ) / 2 );
	}
	public final static Comparator< Greenobject > featureComparator( final String feature )
	{
		final Comparator< Greenobject > comparator = new Comparator< Greenobject >()
		{
			@Override
			public int compare( final Greenobject o1, final Greenobject o2 )
			{
				final double diff = o2.diffTo( o1, feature );
				if ( diff == 0 )
					return 0;
				else if ( diff < 0 )
					return 1;
				else
					return -1;
			}
		};
		return comparator;
	}
	/** A comparator used to sort spots by ascending time frame. */
	public final static Comparator<Greenobject> frameComparator = featureComparator( POSITION_T );
	
	@Override
	public int compareTo(Greenobject o) {
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
		return (float) Greencenter.getFloatPosition(d);
	}

	@Override
	public double getDoublePosition(int d) {
		return Greencenter.getDoublePosition(d);
	}
	
	/**
	 * Returns the squared distance between two clouds.
	 *
	 * @param target
	 *            the Cloud to compare to.
	 *
	 * @return the distance to the current cloud to target cloud specified.
	 */

	public double squareDistanceTo(Greenobject target) {
		// Returns squared distance between the source Blob and the target Blob.

		final double[] sourceLocation = new double[] {Greencenter.getDoublePosition(0),Greencenter.getDoublePosition(1) };
		final double[] targetLocation = new double[] {target.getDoublePosition(0), target.getDoublePosition(1)};

		double distance = 0;

		for (int d = 0; d < sourceLocation.length; ++d) {

			distance += (sourceLocation[d] - targetLocation[d]) * (sourceLocation[d] - targetLocation[d]);
		}

		return distance;
	}
	public double DistanceTo(Greenobject target, final double alpha, final double beta) {
		// Returns squared distance between the source Blob and the target Blob.

		final double[] sourceLocation = new double[] {Greencenter.getDoublePosition(0),Greencenter.getDoublePosition(1) };
		final double[] targetLocation = new double[] {target.getDoublePosition(0), target.getDoublePosition(1)};

		double distance = 1.0E-5;

		for (int d = 0; d < sourceLocation.length; ++d) {

			distance += (sourceLocation[d] - targetLocation[d]) * (sourceLocation[d] - targetLocation[d]);
		}

			return distance;
	}

	public Map< String, Double > getFeatures()
	{
		return features;
	}

	
	/**
	 * A comparator used to sort spots by name. The comparison uses numerical
	 * natural sorting, So that "Spot_4" comes before "Spot_122".
	 */
	public final static Comparator< Greenobject > nameComparator = new Comparator< Greenobject >()
	{
		private final AlphanumComparator comparator = AlphanumComparator.instance;

		@Override
		public int compare( final Greenobject o1, final Greenobject o2 )
		{
			return comparator.compare( o1.getName(), o2.getName() );
		}
	};
}

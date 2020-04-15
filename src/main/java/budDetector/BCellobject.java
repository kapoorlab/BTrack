package budDetector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import fiji.plugin.trackmate.BCellobjectCollection;
import fiji.plugin.trackmate.Dimension;
import fiji.plugin.trackmate.Spot;
import fiji.plugin.trackmate.util.AlphanumComparator;
import net.imglib2.AbstractEuclideanSpace;
import net.imglib2.RealLocalizable;

public class BCellobject extends AbstractEuclideanSpace implements RealLocalizable, Comparable<BCellobject> {
	
	private final ConcurrentHashMap< String, Double > features = new ConcurrentHashMap< String, Double >();
	public static AtomicInteger IDcounter = new AtomicInteger( -1 );
	
	// Bud the cell is inside
	public final Budobject mybud;
	// Growth points of the bud
	public final ArrayList<Budpointobject> mybudpoints;
	
	public final Cellobject currentcell;
	// Location of the cell
	public double[] Location;
	// Distance from center of cell to nearest bud growth point
	public double closestGrowthPoint;
	// Distance from center of cell to nearest bud point
	public double closestBudPoint;
	
	public int time;
	private final int ID;
	
	/** A user-supplied name for this spot. */
	private String name;
	
	
	public BCellobject( final Cellobject currentcell,  final int time) {
		
		
		
		super(3);
		this.ID = IDcounter.incrementAndGet();
		putFeature( POSITION_X, Double.valueOf( currentcell.Location.getDoublePosition(0) ) );
		putFeature( POSITION_Y, Double.valueOf( currentcell.Location.getDoublePosition(1) ) );
		putFeature( RADIUS, Double.valueOf( currentcell.size ) );
		putFeature( POSITION_T, Double.valueOf( time) );
		this.mybud = null;
		this.mybudpoints = null;
		this.currentcell = currentcell;
		this.closestGrowthPoint = 0;
		this.closestBudPoint = 0;
		this.time = time;
		this.name = "ID" + ID;
		
	}
	
	public BCellobject(final Budobject mybud, final ArrayList<Budpointobject> mybudpoints, final Cellobject currentcell, final double closestGrowthPoint, final double closestBudPoint, final int time) {
		
		
		
		super(3);
		this.ID = IDcounter.incrementAndGet();
		putFeature( POSITION_X, Double.valueOf( currentcell.Location.getDoublePosition(0) ) );
		putFeature( POSITION_Y, Double.valueOf( currentcell.Location.getDoublePosition(1) ) );
		putFeature( RADIUS, Double.valueOf( currentcell.size ) );
		putFeature( POSITION_T, Double.valueOf( time) );
		this.mybud = mybud;
		this.mybudpoints = mybudpoints;
		this.currentcell = currentcell;
		this.closestGrowthPoint = closestGrowthPoint;
		this.closestBudPoint = closestBudPoint;
		this.time = time;
		this.name = "ID" + ID;
		
	}

	/** The name of the spot X position feature. */
	public static final String POSITION_X = "POSITION_X";

	/** The name of the spot Y position feature. */
	public static final String POSITION_Y = "POSITION_Y";

	/** The name of the spot Radius feature. */
	public static final String RADIUS = "RADIUS";

	/** The name of the spot T position feature. */
	public static final String POSITION_T = "POSITION_T";

	/** The position features. */
	public final static String[] POSITION_FEATURES = new String[] { POSITION_X, POSITION_Y};

	static int totalfeatures = 4;
	public final static Collection< String > FEATURES = new ArrayList< >( totalfeatures );

	/** The 7 privileged spot feature names. */
	public final static Map< String, String > FEATURE_NAMES = new HashMap< >( totalfeatures );

	/** The 7 privileged spot feature short names. */
	public final static Map< String, String > FEATURE_SHORT_NAMES = new HashMap< >( totalfeatures );

	/** The 7 privileged spot feature dimensions. */
	public final static Map< String, Dimension > FEATURE_DIMENSIONS = new HashMap< >( totalfeatures );

	/** The 7 privileged spot feature isInt flags. */
	public final static Map< String, Boolean > IS_INT = new HashMap< >( totalfeatures );
	/**
	 * @return the name for this Spot.
	 */
	public String getName()
	{
		return this.name;
	}

	/**
	 * Set the name of this Spot.
	 * 
	 * @param name
	 *            the name to use.
	 */
	public void setName( final String name )
	{
		this.name = name;
	}
	static
	{
		FEATURES.add( POSITION_X );
		FEATURES.add( POSITION_Y );
		FEATURES.add( POSITION_T );
		FEATURES.add( RADIUS );
		
		
		FEATURE_NAMES.put( POSITION_X, "X" );
		FEATURE_NAMES.put( POSITION_Y, "Y" );
		FEATURE_NAMES.put( POSITION_T, "T" );
		FEATURE_NAMES.put( RADIUS, "Radius" );
		
		FEATURE_SHORT_NAMES.put( POSITION_X, "X" );
		FEATURE_SHORT_NAMES.put( POSITION_Y, "Y" );
		FEATURE_SHORT_NAMES.put( POSITION_T, "T" );
		FEATURE_SHORT_NAMES.put( RADIUS, "R" );
		
		FEATURE_DIMENSIONS.put( POSITION_X, Dimension.POSITION );
		FEATURE_DIMENSIONS.put( POSITION_Y, Dimension.POSITION );
		FEATURE_DIMENSIONS.put( POSITION_T, Dimension.TIME );
		FEATURE_DIMENSIONS.put( RADIUS, Dimension.LENGTH );
		
		IS_INT.put( POSITION_X, Boolean.FALSE );
		IS_INT.put( POSITION_Y, Boolean.FALSE );
		IS_INT.put( POSITION_T, Boolean.FALSE );
		IS_INT.put( RADIUS, Boolean.FALSE );
	}

	public final Double getFeature(final String feature) {
		
		return features.get(feature);
	}
	public final void putFeature( final String feature, final Double value )
	{
		features.put( feature, value );
	}
	

	public int ID()
	{
		return ID;
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
	 * Returns the difference of the feature value for this spot with the one of
	 * the specified spot. By construction, this operation is anti-symmetric (
	 * <code>A.diffTo(B) = - B.diffTo(A)</code>).
	 * <p>
	 * Will generate a {@link NullPointerException} if one of the spots does not
	 * store the named feature.
	 *
	 * @param s
	 *            the spot to compare to.
	 * @param feature
	 *            the name of the feature to use for calculation.
	 * @return the difference in feature value.
	 */
	public double diffTo( final BCellobject s, final String feature )
	{
		final double f1 = features.get( feature ).doubleValue();
		final double f2 = s.getFeature( feature ).doubleValue();
		return f1 - f2;
	}
	
	public double normalizeDiffTo( final BCellobject s, final String feature )
	{
		final double a = features.get( feature ).doubleValue();
		final double b = s.getFeature( feature ).doubleValue();
		if ( a == -b )
			return 0d;
		
		return Math.abs( a - b ) / ( ( a + b ) / 2 );
	}
	public final static Comparator< BCellobject > featureComparator( final String feature )
	{
		final Comparator< BCellobject > comparator = new Comparator< BCellobject >()
		{
			@Override
			public int compare( final BCellobject o1, final BCellobject o2 )
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
	public final static Comparator<BCellobject> frameComparator = featureComparator( POSITION_T );
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

	/**
	 * A comparator used to sort spots by name. The comparison uses numerical
	 * natural sorting, So that "Spot_4" comes before "Spot_122".
	 */
	public final static Comparator< BCellobject > nameComparator = new Comparator< BCellobject >()
	{
		private final AlphanumComparator comparator = AlphanumComparator.instance;

		@Override
		public int compare( final BCellobject o1, final BCellobject o2 )
		{
			return comparator.compare( o1.getName(), o2.getName() );
		}
	};


	/**
	 * Exposes the storage map of features for this spot. Altering the returned
	 * map will alter the spot.
	 *
	 * @return a map of {@link String}s to {@link Double}s.
	 */
	public Map< String, Double > getFeatures()
	{
		return features;
	}

}

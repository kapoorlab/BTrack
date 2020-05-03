package tracker;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.jgrapht.graph.DefaultWeightedEdge;

import budDetector.Budpointobject;

public class BUDDYFeatureModel {

	/*
	 * FIELDS
	 */

	private final Collection< String > trackFeatures = new LinkedHashSet< >();

	private final Map< String, String > trackFeatureNames = new HashMap< >();

	private final Map< String, String > trackFeatureShortNames = new HashMap< >();

	private final Map< String, BUDDYDimension > trackFeatureBUDDYDimensions = new HashMap< >();

	private final Map< String, Boolean > trackFeatureIsInt = new HashMap< >();

	/**
	 * Feature storage. We use a Map of Map as a 2D Map. The list maps each
	 * track to its feature map. The feature map maps each feature to the double
	 * value for the specified feature.
	 */
	Map< Integer, Map< String, Double >> trackFeatureValues = new ConcurrentHashMap<>();

	/**
	 * Feature storage for edges.
	 */
	private final ConcurrentHashMap< DefaultWeightedEdge, ConcurrentHashMap< String, Double >> edgeFeatureValues = new ConcurrentHashMap<>();

	private final Collection< String > edgeFeatures = new LinkedHashSet< >();

	private final Map< String, String > edgeFeatureNames = new HashMap< >();

	private final Map< String, String > edgeFeatureShortNames = new HashMap< >();

	private final Map< String, BUDDYDimension > edgeFeatureBUDDYDimensions = new HashMap< >();

	private final Map< String, Boolean > edgeFeatureIsInt = new HashMap< >();

	private final Collection< String > BudpointobjectFeatures = new LinkedHashSet< >();

	private final Map< String, String > BudpointobjectFeatureNames = new HashMap< >();

	private final Map< String, String > BudpointobjectFeatureShortNames = new HashMap< >();

	private final Map< String, BUDDYDimension > BudpointobjectFeatureBUDDYDimensions = new HashMap< >();

	private final Map< String, Boolean > BudpointobjectFeatureIsInt = new HashMap< >();

	private final BUDDYModel model;



	/*
	 * CONSTRUCTOR
	 */

	/**
	 * Instantiates a new feature model. The basic Budpointobject features (POSITON_*,
	 * RADIUS, FRAME, QUALITY) are declared. Edge and track feature declarations
	 * are left blank.
	 *
	 * @param model
	 *            the parent {@link Model}.
	 */
	protected BUDDYFeatureModel( final BUDDYModel model )
	{
		this.model = model;
		// Adds the base Budpointobject features
		declareBudpointobjectFeatures( Budpointobject.FEATURES, Budpointobject.FEATURE_NAMES, Budpointobject.FEATURE_SHORT_NAMES, Budpointobject.FEATURE_BUDDYDIMENSIONS, Budpointobject.IS_INT );
	}

	/*
	 * METHODS
	 */

	/**
	 * Returns a new double array with all the values for the specified track
	 * feature.
	 *
	 * @param trackFeature
	 *            the track feature to parse. Throw an
	 *            {@link IllegalArgumentException} if the feature is unknown.
	 * @param visibleOnly
	 *            if <code>true</code>, will only include visible tracks, all
	 *            the tracks otherwise.
	 * @return a new <code>double[]</code>, one element per track.
	 */
	public double[] getTrackFeatureValues( final String trackFeature )
	{
		if ( !trackFeatures.contains( trackFeature ) ) { throw new IllegalArgumentException( "Unknown track feature: " + trackFeature ); }
		final Set< Integer > keys = model.getTrackModel().trackIDs( false );
		final double[] val = new double[ keys.size() ];
		int index = 0;
		for ( final Integer trackID : keys )
		{
			final Double tf = getTrackFeature( trackID, trackFeature );
			if ( null == tf )
				continue;
			val[ index++ ] = tf.doubleValue();
		}
		return val;
	}

	/**
	 * Returns a new double array with all the values for the specified edge
	 * feature.
	 *
	 * @param edgeFeature
	 *            the track feature to parse. Throw an
	 *            {@link IllegalArgumentException} if the feature is unknown.
	 * @param visibleOnly
	 *            if <code>true</code>, will only include edges in visible
	 *            tracks, in all the tracks otherwise.
	 * @return a new <code>double[]</code>, one element per edge.
	 */
	public double[] getEdgeFeatureValues( final String edgeFeature, final boolean visibleOnly )
	{
		if ( !edgeFeatures.contains( edgeFeature ) ) { throw new IllegalArgumentException( "Unknown edge feature: " + edgeFeature ); }
		final Set< Integer > keys = model.getTrackModel().trackIDs( visibleOnly );
		int nvals = 0;
		for ( final Integer trackID : keys )
		{
			nvals += model.getTrackModel().trackEdges( trackID ).size();
		}

		final double[] val = new double[ nvals ];
		int index = 0;
		for ( final Integer trackID : keys )
		{
			for ( final DefaultWeightedEdge edge : model.getTrackModel().trackEdges( trackID ) )
			{
				final Double ef = getEdgeFeature( edge, edgeFeature );
				if ( null == ef )
					continue;
				val[ index++ ] = ef.doubleValue();
			}
		}
		return val;
	}

	/*
	 * EDGE FEATURES
	 */

	/**
	 * Stores a numerical feature for an edge of this model.
	 * <p>
	 * Note that no checks are made to ensures that the edge exists in the
	 * {@link TrackModel}, and that the feature is declared in this
	 * {@link FeatureModel}.
	 *
	 * @param edge
	 *            the edge whose features to update.
	 * @param feature
	 *            the feature.
	 * @param value
	 *            the feature value
	 */
	public synchronized void putEdgeFeature( final DefaultWeightedEdge edge, final String feature, final Double value )
	{
		ConcurrentHashMap< String, Double > map = edgeFeatureValues.get( edge );
		if ( null == map )
		{
			map = new ConcurrentHashMap< >();
			edgeFeatureValues.put( edge, map );
		}
		map.put( feature, value );
	}

	public Double getEdgeFeature( final DefaultWeightedEdge edge, final String featureName )
	{
		final ConcurrentHashMap< String, Double > map = edgeFeatureValues.get( edge );
		if ( null == map ) { return null; }
		return map.get( featureName );
	}

	/**
	 * Returns edge features as declared in this model.
	 *
	 * @return the edge features.
	 */
	public Collection< String > getEdgeFeatures()
	{
		return edgeFeatures;
	}

	/**
	 * Declares edge features, by specifying their name, short name and
	 * BUDDYDimension. An {@link IllegalArgumentException} will be thrown if any of
	 * the map misses a feature.
	 *
	 * @param features
	 *            the list of edge features to register.
	 * @param featureNames
	 *            the name of these features.
	 * @param featureShortNames
	 *            the short name of these features.
	 * @param featureBUDDYDimensions
	 *            the BUDDYDimension of these features.
	 * @param isIntFeature
	 *            whether some of these features are made of <code>int</code>s (
	 *            <code>true</code>) or <code>double</code>s (<code>false</code>
	 *            ).
	 */
	public void declareEdgeFeatures( final Collection< String > features, final Map< String, String > featureNames, final Map< String, String > featureShortNames, final Map< String, BUDDYDimension > featureBUDDYDimensions, final Map< String, Boolean > isIntFeature )
	{
		edgeFeatures.addAll( features );
		for ( final String feature : features )
		{
			final String name = featureNames.get( feature );
			if ( null == name ) { throw new IllegalArgumentException( "Feature " + feature + " misses a name." ); }
			edgeFeatureNames.put( feature, name );

			final String shortName = featureShortNames.get( feature );
			if ( null == shortName ) { throw new IllegalArgumentException( "Feature " + feature + " misses a short name." ); }
			edgeFeatureShortNames.put( feature, shortName );

			final BUDDYDimension BUDDYDimension = featureBUDDYDimensions.get( feature );
			if ( null == BUDDYDimension ) { throw new IllegalArgumentException( "Feature " + feature + " misses a BUDDYDimension." ); }
			edgeFeatureBUDDYDimensions.put( feature, BUDDYDimension );

			final Boolean isInt = isIntFeature.get( feature );
			if ( null == isInt ) { throw new IllegalArgumentException( "Feature " + feature + " misses the isInt flag." ); }
			edgeFeatureIsInt.put( feature, isInt );
		}
	}

	/**
	 * Returns the name mapping of the edge features that are dealt with in this
	 * model.
	 *
	 * @return the map of edge feature names.
	 */
	public Map< String, String > getEdgeFeatureNames()
	{
		return edgeFeatureNames;
	}

	/**
	 * Returns the short name mapping of the edge features that are dealt with
	 * in this model.
	 *
	 * @return the map of edge short names.
	 */
	public Map< String, String > getEdgeFeatureShortNames()
	{
		return edgeFeatureShortNames;
	}

	/**
	 * Returns the BUDDYDimension mapping of the edge features that are dealt with in
	 * this model.
	 *
	 * @return the map of edge feature BUDDYDimensions.
	 */
	public Map< String, BUDDYDimension > getEdgeFeatureBUDDYDimensions()
	{
		return edgeFeatureBUDDYDimensions;
	}

	/**
	 * Returns the map that states whether the target feature is integer values
	 * (<code>true</code>) or double valued (<code>false</code>).
	 *
	 * @return the map of isInt flag.
	 */
	public Map< String, Boolean > getEdgeFeatureIsInt()
	{
		return edgeFeatureIsInt;
	}

	/*
	 * TRACK FEATURES
	 */

	/**
	 * Returns the track features that are dealt with in this model.
	 * 
	 * @return the collection of track features managed in this model.
	 */
	public Collection< String > getTrackFeatures()
	{
		return trackFeatures;
	}

	/**
	 * Declares track features, by specifying their names, short name and
	 * BUDDYDimension. An {@link IllegalArgumentException} will be thrown if any of
	 * the map misses a feature.
	 *
	 * @param features
	 *            the list of track feature to register.
	 * @param featureNames
	 *            the name of these features.
	 * @param featureShortNames
	 *            the short name of these features.
	 * @param featureBUDDYDimensions
	 *            the BUDDYDimension of these features.
	 * @param isIntFeature
	 *            whether some of these features are made of <code>int</code>s (
	 *            <code>true</code>) or <code>double</code>s (<code>false</code>
	 *            ).
	 */
	public void declareTrackFeatures( final Collection< String > features, final Map< String, String > featureNames, final Map< String, String > featureShortNames, final Map< String, BUDDYDimension > featureBUDDYDimensions, final Map< String, Boolean > isIntFeature )
	{
		trackFeatures.addAll( features );
		for ( final String feature : features )
		{

			final String name = featureNames.get( feature );
			if ( null == name ) { throw new IllegalArgumentException( "Feature " + feature + " misses a name." ); }
			trackFeatureNames.put( feature, name );

			final String shortName = featureShortNames.get( feature );
			if ( null == shortName ) { throw new IllegalArgumentException( "Feature " + feature + " misses a short name." ); }
			trackFeatureShortNames.put( feature, shortName );

			final BUDDYDimension BUDDYDimension = featureBUDDYDimensions.get( feature );
			if ( null == BUDDYDimension ) { throw new IllegalArgumentException( "Feature " + feature + " misses a BUDDYDimension." ); }
			trackFeatureBUDDYDimensions.put( feature, BUDDYDimension );

			final Boolean isInt = isIntFeature.get( feature );
			if ( null == isInt ) { throw new IllegalArgumentException( "Feature " + feature + " misses the isInt flag." ); }
			trackFeatureIsInt.put( feature, isInt );
		}
	}

	/**
	 * Returns the name mapping of the track features that are dealt with in
	 * this model.
	 * 
	 * @return the feature name.
	 */
	public Map< String, String > getTrackFeatureNames()
	{
		return trackFeatureNames;
	}

	/**
	 * Returns the short name mapping of the track features that are dealt with
	 * in this model.
	 *
	 * @return the feature short name.
	 */
	public Map< String, String > getTrackFeatureShortNames()
	{
		return trackFeatureShortNames;
	}

	/**
	 * Returns the BUDDYDimension mapping of the track features that are dealt with
	 * in this model.
	 * 
	 * @return the feature BUDDYDimension.
	 */
	public Map< String, BUDDYDimension > getTrackFeatureBUDDYDimensions()
	{
		return trackFeatureBUDDYDimensions;
	}

	/**
	 * Returns the map that states whether the target feature is integer values
	 * (<code>true</code>) or double valued (<code>false</code>).
	 *
	 * @return the map of isInt flag.
	 */
	public Map< String, Boolean > getTrackFeatureIsInt()
	{
		return trackFeatureIsInt;
	}

	/**
	 * Stores a track numerical feature.
	 * <p>
	 * Note that no checks are made to ensures that the track ID exists in the
	 * {@link TrackModel}, and that the feature is declared in this
	 * {@link FeatureModel}.
	 *
	 * @param trackID
	 *            the ID of the track. It must be an existing track ID.
	 * @param feature
	 *            the feature.
	 * @param value
	 *            the feature value.
	 */
	public synchronized void putTrackFeature( final Integer trackID, final String feature, final Double value )
	{
		Map< String, Double > trackFeatureMap = trackFeatureValues.get( trackID );
		if ( null == trackFeatureMap )
		{
			trackFeatureMap = new HashMap< >( trackFeatures.size() );
			trackFeatureValues.put( trackID, trackFeatureMap );
		}
		trackFeatureMap.put( feature, value );
	}

	/**
	 * Returns the numerical value of the specified track feature for the
	 * specified track.
	 *
	 * @param trackID
	 *            the track ID to quest.
	 * @param feature
	 *            the desired feature.
	 * @return the value of the specified feature.
	 */
	public Double getTrackFeature( final Integer trackID, final String feature )
	{
		final Map< String, Double > valueMap = trackFeatureValues.get( trackID );
		return valueMap.get( feature );
	}

	/**
	 * Returns the map of all track features declared for all tracks of the
	 * model.
	 *
	 * @return a new mapping of feature vs its numerical values.
	 */
	public Map< String, double[] > getTrackFeatureValues()
	{
		final Map< String, double[] > featureValues = new HashMap< >();
		Double val;
		final int nTracks = model.getTrackModel().nTracks( false );
		for ( final String feature : trackFeatures )
		{
			// Make a double array to comply to JFreeChart histograms
			boolean noDataFlag = true;
			final double[] values = new double[ nTracks ];
			int index = 0;
			for ( final Integer trackID : model.getTrackModel().trackIDs( false ) )
			{
				val = getTrackFeature( trackID, feature );
				if ( null == val )
				{
					continue;
				}
				values[ index++ ] = val;
				noDataFlag = false;
			}

			if ( noDataFlag )
			{
				featureValues.put( feature, new double[ 0 ] ); // Empty array to
				// signal no
				// data
			}
			else
			{
				featureValues.put( feature, values );
			}
		}
		return featureValues;
	}

	/*
	 * Budpointobject FEATURES the Budpointobject features are stored in the Budpointobject object themselves,
	 * but we declare them here.
	 */

	/**
	 * Declares Budpointobject features, by specifying their names, short name and
	 * BUDDYDimension. An {@link IllegalArgumentException} will be thrown if any of
	 * the map misses a feature.
	 *
	 * @param features
	 *            the list of Budpointobject feature to register.
	 * @param featureNames
	 *            the name of these features.
	 * @param featureShortNames
	 *            the short name of these features.
	 * @param featureBUDDYDimensions
	 *            the BUDDYDimension of these features.
	 * @param isIntFeature
	 *            whether some of these features are made of <code>int</code>s (
	 *            <code>true</code>) or <code>double</code>s (<code>false</code>
	 *            ).
	 */
	public void declareBudpointobjectFeatures( final Collection< String > features, final Map< String, String > featureNames, final Map< String, String > featureShortNames, final Map< String, BUDDYDimension > featureBUDDYDimensions, final Map< String, Boolean > isIntFeature )
	{
		BudpointobjectFeatures.addAll( features );
		for ( final String feature : features )
		{

			final String name = featureNames.get( feature );
			if ( null == name ) { throw new IllegalArgumentException( "Feature " + feature + " misses a name." ); }
			BudpointobjectFeatureNames.put( feature, name );

			final String shortName = featureShortNames.get( feature );
			if ( null == shortName ) { throw new IllegalArgumentException( "Feature " + feature + " misses a short name." ); }
			BudpointobjectFeatureShortNames.put( feature, shortName );

			final BUDDYDimension BUDDYDimension = featureBUDDYDimensions.get( feature );
			if ( null == BUDDYDimension ) { throw new IllegalArgumentException( "Feature " + feature + " misses a BUDDYDimension." ); }
			BudpointobjectFeatureBUDDYDimensions.put( feature, BUDDYDimension );

			final Boolean isInt = isIntFeature.get( feature );
			if ( null == isInt ) { throw new IllegalArgumentException( "Feature " + feature + " misses the isInt flag." ); }
			BudpointobjectFeatureIsInt.put( feature, isInt );

		}
	}

	/**
	 * Returns Budpointobject features as declared in this model.
	 *
	 * @return the Budpointobject features.
	 */
	public Collection< String > getBudpointobjectFeatures()
	{
		return BudpointobjectFeatures;
	}

	/**
	 * Returns the name mapping of the Budpointobject features that are dealt with in this
	 * model.
	 *
	 * @return the map of Budpointobject feature names.
	 */
	public Map< String, String > getBudpointobjectFeatureNames()
	{
		return BudpointobjectFeatureNames;
	}

	/**
	 * Returns the short name mapping of the Budpointobject features that are dealt with
	 * in this model.
	 *
	 * @return the map of Budpointobject short names.
	 */
	public Map< String, String > getBudpointobjectFeatureShortNames()
	{
		return BudpointobjectFeatureShortNames;
	}

	/**
	 * Returns the BUDDYDimension mapping of the Budpointobject features that are dealt with in
	 * this model.
	 *
	 * @return the map of Budpointobject feature BUDDYDimensions.
	 */
	public Map< String, BUDDYDimension > getBudpointobjectFeatureBUDDYDimensions()
	{
		return BudpointobjectFeatureBUDDYDimensions;
	}

	/**
	 * Returns the map that states whether the target feature is integer values
	 * (<code>true</code>) or double valued (<code>false</code>).
	 *
	 * @return the map of isInt flag.
	 */
	public Map< String, Boolean > getBudpointobjectFeatureIsInt()
	{
		return BudpointobjectFeatureIsInt;
	}

	@Override
	public String toString()
	{
		final StringBuilder str = new StringBuilder();

		// Budpointobjects
		str.append( "Budpointobject features declared:\n" );
		appendFeatureDeclarations( str, BudpointobjectFeatures, BudpointobjectFeatureNames, BudpointobjectFeatureShortNames, BudpointobjectFeatureBUDDYDimensions, BudpointobjectFeatureIsInt );
		str.append( '\n' );

		// Edges
		str.append( "Edge features declared:\n" );
		appendFeatureDeclarations( str, edgeFeatures, edgeFeatureNames, edgeFeatureShortNames, edgeFeatureBUDDYDimensions, edgeFeatureIsInt );
		str.append( '\n' );

		// Track
		str.append( "Track features declared:\n" );
		appendFeatureDeclarations( str, trackFeatures, trackFeatureNames, trackFeatureShortNames, trackFeatureBUDDYDimensions, trackFeatureIsInt );
		str.append( '\n' );

		return str.toString();
	}

	/**
	 * Echoes the full content of this {@link FeatureModel}.
	 * 
	 * @return a String representation of the full content of this model.
	 */
	public String echo()
	{
		final StringBuilder str = new StringBuilder();

		// Budpointobjects
		str.append( "Budpointobject features:\n" );
		str.append( " - Declared:\n" );
		appendFeatureDeclarations( str, BudpointobjectFeatures, BudpointobjectFeatureNames, BudpointobjectFeatureShortNames, BudpointobjectFeatureBUDDYDimensions, BudpointobjectFeatureIsInt );
		str.append( '\n' );

		// Edges
		str.append( "Edge features:\n" );
		str.append( " - Declared:\n" );
		appendFeatureDeclarations( str, edgeFeatures, edgeFeatureNames, edgeFeatureShortNames, edgeFeatureBUDDYDimensions, edgeFeatureIsInt );
		str.append( '\n' );
		str.append( " - Values:\n" );
		appendFeatureValues( str, edgeFeatureValues );

		// Track
		str.append( "Track features:\n" );
		str.append( " - Declared:\n" );
		appendFeatureDeclarations( str, trackFeatures, trackFeatureNames, trackFeatureShortNames, trackFeatureBUDDYDimensions, trackFeatureIsInt );
		str.append( '\n' );
		str.append( " - Values:\n" );
		appendFeatureValues( str, trackFeatureValues );

		return str.toString();
	}

	/*
	 * STATIC UTILS
	 */

	private static final < K > void appendFeatureValues( final StringBuilder str, final Map< K, ? extends Map< String, Double >> values )
	{
		for ( final K key : values.keySet() )
		{
			final String header = "   - " + key.toString() + ":\n";
			str.append( header );
			final Map< String, Double > map = values.get( key );
			for ( final String feature : map.keySet() )
			{
				str.append( "     - " + feature + " = " + map.get( feature ) + '\n' );
			}
		}
	}

	private static final void appendFeatureDeclarations( final StringBuilder str, final Collection< String > features, final Map< String, String > featureNames, final Map< String, String > featureShortNames, final Map< String, BUDDYDimension > featureBUDDYDimensions, final Map< String, Boolean > isIntFeature )
	{
		for ( final String feature : features )
		{
			str.append( "   - " + feature + ": " + featureNames.get( feature ) + ", '" + featureShortNames.get( feature ) + "' (" + featureBUDDYDimensions.get( feature ) + ")" );
			if ( isIntFeature.get( feature ).booleanValue() )
			{
				str.append( " - integer valued.\n" );
			}
			else
			{
				str.append( " - double valued.\n" );
			}
		}
	}
	
	
}

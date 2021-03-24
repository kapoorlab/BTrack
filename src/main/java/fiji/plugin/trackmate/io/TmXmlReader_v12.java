package Buddy.plugin.trackmate.io;

import static Buddy.plugin.trackmate.io.IOUtils.readBooleanAttribute;
import static Buddy.plugin.trackmate.io.IOUtils.readDoubleAttribute;
import static Buddy.plugin.trackmate.io.IOUtils.readFloatAttribute;
import static Buddy.plugin.trackmate.io.IOUtils.readIntAttribute;
import static Buddy.plugin.trackmate.io.TmXmlKeys.FILTER_ABOVE_ATTRIBUTE_NAME;
import static Buddy.plugin.trackmate.io.TmXmlKeys.FILTER_ELEMENT_KEY;
import static Buddy.plugin.trackmate.io.TmXmlKeys.FILTER_FEATURE_ATTRIBUTE_NAME;
import static Buddy.plugin.trackmate.io.TmXmlKeys.FILTER_VALUE_ATTRIBUTE_NAME;
import static Buddy.plugin.trackmate.io.TmXmlKeys.TRACK_FILTER_COLLECTION_ELEMENT_KEY;
import static Buddy.plugin.trackmate.io.TmXmlKeys_v12.FILTERED_BCellobject_COLLECTION_ELEMENT_KEY_v12;
import static Buddy.plugin.trackmate.io.TmXmlKeys_v12.FILTERED_BCellobject_ELEMENT_KEY_v12;
import static Buddy.plugin.trackmate.io.TmXmlKeys_v12.FILTERED_TRACK_ELEMENT_KEY_v12;
import static Buddy.plugin.trackmate.io.TmXmlKeys_v12.FILTER_ABOVE_ATTRIBUTE_NAME_v12;
import static Buddy.plugin.trackmate.io.TmXmlKeys_v12.FILTER_ELEMENT_KEY_v12;
import static Buddy.plugin.trackmate.io.TmXmlKeys_v12.FILTER_FEATURE_ATTRIBUTE_NAME_v12;
import static Buddy.plugin.trackmate.io.TmXmlKeys_v12.FILTER_VALUE_ATTRIBUTE_NAME_v12;
import static Buddy.plugin.trackmate.io.TmXmlKeys_v12.FRAME_ATTRIBUTE_NAME_v12;
import static Buddy.plugin.trackmate.io.TmXmlKeys_v12.IMAGE_ELEMENT_KEY_v12;
import static Buddy.plugin.trackmate.io.TmXmlKeys_v12.IMAGE_FILENAME_v12_ATTRIBUTE_NAME_v12;
import static Buddy.plugin.trackmate.io.TmXmlKeys_v12.IMAGE_FOLDER_ATTRIBUTE_NAME_v12;
import static Buddy.plugin.trackmate.io.TmXmlKeys_v12.IMAGE_HEIGHT_ATTRIBUTE_NAME_v12;
import static Buddy.plugin.trackmate.io.TmXmlKeys_v12.IMAGE_NFRAMES_ATTRIBUTE_NAME_v12;
import static Buddy.plugin.trackmate.io.TmXmlKeys_v12.IMAGE_NSLICES_ATTRIBUTE_NAME_v12;
import static Buddy.plugin.trackmate.io.TmXmlKeys_v12.IMAGE_PIXEL_HEIGHT_ATTRIBUTE_NAME_v12;
import static Buddy.plugin.trackmate.io.TmXmlKeys_v12.IMAGE_PIXEL_WIDTH_ATTRIBUTE_NAME_v12;
import static Buddy.plugin.trackmate.io.TmXmlKeys_v12.IMAGE_SPATIAL_UNITS_ATTRIBUTE_NAME_v12;
import static Buddy.plugin.trackmate.io.TmXmlKeys_v12.IMAGE_TIME_INTERVAL_ATTRIBUTE_NAME_v12;
import static Buddy.plugin.trackmate.io.TmXmlKeys_v12.IMAGE_TIME_UNITS_ATTRIBUTE_NAME_v12;
import static Buddy.plugin.trackmate.io.TmXmlKeys_v12.IMAGE_VOXEL_DEPTH_ATTRIBUTE_NAME_v12;
import static Buddy.plugin.trackmate.io.TmXmlKeys_v12.IMAGE_WIDTH_ATTRIBUTE_NAME_v12;
import static Buddy.plugin.trackmate.io.TmXmlKeys_v12.INITIAL_BCellobject_FILTER_ELEMENT_KEY_v12;
import static Buddy.plugin.trackmate.io.TmXmlKeys_v12.SEGMENTER_CLASS_ATTRIBUTE_NAME_v12;
import static Buddy.plugin.trackmate.io.TmXmlKeys_v12.SEGMENTER_SETTINGS_CLASS_ATTRIBUTE_NAME_v12;
import static Buddy.plugin.trackmate.io.TmXmlKeys_v12.SEGMENTER_SETTINGS_ELEMENT_KEY_v12;
import static Buddy.plugin.trackmate.io.TmXmlKeys_v12.SETTINGS_ELEMENT_KEY_v12;
import static Buddy.plugin.trackmate.io.TmXmlKeys_v12.SETTINGS_SEGMENTATION_CHANNEL_ATTRIBUTE_NAME_v12;
import static Buddy.plugin.trackmate.io.TmXmlKeys_v12.SETTINGS_TEND_ATTRIBUTE_NAME_v12;
import static Buddy.plugin.trackmate.io.TmXmlKeys_v12.SETTINGS_TSTART_ATTRIBUTE_NAME_v12;
import static Buddy.plugin.trackmate.io.TmXmlKeys_v12.SETTINGS_XEND_ATTRIBUTE_NAME_v12;
import static Buddy.plugin.trackmate.io.TmXmlKeys_v12.SETTINGS_XSTART_ATTRIBUTE_NAME_v12;
import static Buddy.plugin.trackmate.io.TmXmlKeys_v12.SETTINGS_YEND_ATTRIBUTE_NAME_v12;
import static Buddy.plugin.trackmate.io.TmXmlKeys_v12.SETTINGS_YSTART_ATTRIBUTE_NAME_v12;
import static Buddy.plugin.trackmate.io.TmXmlKeys_v12.SETTINGS_ZEND_ATTRIBUTE_NAME_v12;
import static Buddy.plugin.trackmate.io.TmXmlKeys_v12.SETTINGS_ZSTART_ATTRIBUTE_NAME_v12;
import static Buddy.plugin.trackmate.io.TmXmlKeys_v12.BCellobject_COLLECTION_ELEMENT_KEY_v12;
import static Buddy.plugin.trackmate.io.TmXmlKeys_v12.BCellobject_ELEMENT_KEY_v12;
import static Buddy.plugin.trackmate.io.TmXmlKeys_v12.BCellobject_FILTER_COLLECTION_ELEMENT_KEY_v12;
import static Buddy.plugin.trackmate.io.TmXmlKeys_v12.BCellobject_FRAME_COLLECTION_ELEMENT_KEY_v12;
import static Buddy.plugin.trackmate.io.TmXmlKeys_v12.BCellobject_ID_ATTRIBUTE_NAME_v12;
import static Buddy.plugin.trackmate.io.TmXmlKeys_v12.BCellobject_ID_ELEMENT_KEY_v12;
import static Buddy.plugin.trackmate.io.TmXmlKeys_v12.BCellobject_NAME_v12_ATTRIBUTE_NAME_v12;
import static Buddy.plugin.trackmate.io.TmXmlKeys_v12.TRACKER_CLASS_ATTRIBUTE_NAME_v12;
import static Buddy.plugin.trackmate.io.TmXmlKeys_v12.TRACKER_SETTINGS_CLASS_ATTRIBUTE_NAME_v12;
import static Buddy.plugin.trackmate.io.TmXmlKeys_v12.TRACKER_SETTINGS_ELEMENT_KEY_v12;
import static Buddy.plugin.trackmate.io.TmXmlKeys_v12.TRACK_COLLECTION_ELEMENT_KEY_v12;
import static Buddy.plugin.trackmate.io.TmXmlKeys_v12.TRACK_EDGE_ELEMENT_KEY_v12;
import static Buddy.plugin.trackmate.io.TmXmlKeys_v12.TRACK_EDGE_SOURCE_ATTRIBUTE_NAME_v12;
import static Buddy.plugin.trackmate.io.TmXmlKeys_v12.TRACK_EDGE_TARGET_ATTRIBUTE_NAME_v12;
import static Buddy.plugin.trackmate.io.TmXmlKeys_v12.TRACK_EDGE_WEIGHT_ATTRIBUTE_NAME_v12;
import static Buddy.plugin.trackmate.io.TmXmlKeys_v12.TRACK_ELEMENT_KEY_v12;
import static Buddy.plugin.trackmate.io.TmXmlKeys_v12.TRACK_ID_ATTRIBUTE_NAME_v12;
import static Buddy.plugin.trackmate.io.TmXmlKeys_v12.TRACK_ID_ELEMENT_KEY_v12;
import static Buddy.plugin.trackmate.tracking.TrackerKeys.KEY_ALLOW_GAP_CLOSING;
import static Buddy.plugin.trackmate.tracking.TrackerKeys.KEY_ALLOW_TRACK_MERGING;
import static Buddy.plugin.trackmate.tracking.TrackerKeys.KEY_ALLOW_TRACK_SPLITTING;
import static Buddy.plugin.trackmate.tracking.TrackerKeys.KEY_ALTERNATIVE_LINKING_COST_FACTOR;
import static Buddy.plugin.trackmate.tracking.TrackerKeys.KEY_BLOCKING_VALUE;
import static Buddy.plugin.trackmate.tracking.TrackerKeys.KEY_CUTOFF_PERCENTILE;
import static Buddy.plugin.trackmate.tracking.TrackerKeys.KEY_GAP_CLOSING_FEATURE_PENALTIES;
import static Buddy.plugin.trackmate.tracking.TrackerKeys.KEY_GAP_CLOSING_MAX_DISTANCE;
import static Buddy.plugin.trackmate.tracking.TrackerKeys.KEY_GAP_CLOSING_MAX_FRAME_GAP;
import static Buddy.plugin.trackmate.tracking.TrackerKeys.KEY_LINKING_FEATURE_PENALTIES;
import static Buddy.plugin.trackmate.tracking.TrackerKeys.KEY_LINKING_MAX_DISTANCE;
import static Buddy.plugin.trackmate.tracking.TrackerKeys.KEY_MERGING_FEATURE_PENALTIES;
import static Buddy.plugin.trackmate.tracking.TrackerKeys.KEY_MERGING_MAX_DISTANCE;
import static Buddy.plugin.trackmate.tracking.TrackerKeys.KEY_SPLITTING_FEATURE_PENALTIES;
import static Buddy.plugin.trackmate.tracking.TrackerKeys.KEY_SPLITTING_MAX_DISTANCE;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.jdom2.Attribute;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import Buddy.plugin.trackmate.Dimension;
import Buddy.plugin.trackmate.FeatureModel;
import Buddy.plugin.trackmate.Logger;
import Buddy.plugin.trackmate.Model;
import Buddy.plugin.trackmate.SelectionModel;
import Buddy.plugin.trackmate.Settings;
import Buddy.plugin.trackmate.BCellobjectCollection;
import Buddy.plugin.trackmate.features.FeatureFilter;
import Buddy.plugin.trackmate.features.edges.EdgeAnalyzer;
import Buddy.plugin.trackmate.features.BCellobject.BCellobjectAnalyzerFactory;
import Buddy.plugin.trackmate.features.track.TrackAnalyzer;
import Buddy.plugin.trackmate.features.track.TrackDurationAnalyzer;
import Buddy.plugin.trackmate.gui.descriptors.ConfigureViewsDescriptor;
import Buddy.plugin.trackmate.providers.EdgeAnalyzerProvider;
import Buddy.plugin.trackmate.providers.BCellobjectAnalyzerProvider;
import Buddy.plugin.trackmate.providers.TrackAnalyzerProvider;
import Buddy.plugin.trackmate.providers.TrackerProvider;
import Buddy.plugin.trackmate.providers.ViewProvider;
import Buddy.plugin.trackmate.tracking.BCellobjectTracker;
import Buddy.plugin.trackmate.tracking.BCellobjectTrackerFactory;
import Buddy.plugin.trackmate.tracking.kdtree.NearestNeighborTrackerFactory;
import Buddy.plugin.trackmate.tracking.oldlap.FastLAPTrackerFactory;
import Buddy.plugin.trackmate.tracking.oldlap.SimpleFastLAPTrackerFactory;
import Buddy.plugin.trackmate.visualization.TrackMateModelView;
import Buddy.plugin.trackmate.visualization.hyperstack.HyperStackDisplayer;
import fiji.util.NumberParser;
import budDetector.BCellobject;
import ij.IJ;
import ij.ImagePlus;
import pluginTools.InteractiveBud;

/**
 * A compatibility xml loader than can load TrackMate xml file saved for version
 * prior to 2.0. In the code, we keep the previous vocable of "segmenter"... The
 * code here is extremely pedestrian; we deal with all particular cases
 * explicitly, and convert on the fly to v2 classes.
 *
 * @author Jean-Yves Tinevez - 2012
 */
public class TmXmlReader_v12 extends TmXmlReader
{

	/*
	 * XML KEY_v12S FOR V 1.2
	 */

	private static final String TRACKER_SETTINGS_ALLOW_EVENT_ATTNAME_v12 = "allowed";

	// Alternative costs & blocking
	private static final String TRACKER_SETTINGS_ALTERNATE_COST_FACTOR_ATTNAME_v12 = "alternatecostfactor";

	private static final String TRACKER_SETTINGS_CUTOFF_PERCENTILE_ATTNAME_v12 = "cutoffpercentile";

	private static final String TRACKER_SETTINGS_BLOCKING_VALUE_ATTNAME_v12 = "blockingvalue";

	// Cutoff elements
	private static final String TRACKER_SETTINGS_TIME_CUTOFF_ELEMENT = "TimeCutoff";

	private static final String TRACKER_SETTINGS_TIME_CUTOFF_ATTNAME_v12 = "value";

	private static final String TRACKER_SETTINGS_DISTANCE_CUTOFF_ELEMENT = "DistanceCutoff";

	private static final String TRACKER_SETTINGS_DISTANCE_CUTOFF_ATTNAME_v12 = "value";

	private static final String TRACKER_SETTINGS_FEATURE_ELEMENT = "FeatureCondition";

	private static final String TRACKER_SETTINGS_LINKING_ELEMENT = "LinkingCondition";

	private static final String TRACKER_SETTINGS_GAP_CLOSING_ELEMENT = "GapClosingCondition";

	private static final String TRACKER_SETTINGS_MERGING_ELEMENT = "MergingCondition";

	private static final String TRACKER_SETTINGS_SPLITTING_ELEMENT = "SplittingCondition";

	// Nearest meighbor tracker
	private static final String MAX_LINKING_DISTANCE_ATTRIBUTE = "maxdistance";

	// Forgotten features
	private static final ArrayList< String > F_FEATURES = new ArrayList< >( 9 );

	private static final HashMap< String, String > F_FEATURE_NAMES = new HashMap< >( 9 );

	private static final HashMap< String, String > F_FEATURE_SHORT_NAMES = new HashMap< >( 9 );

	private static final HashMap< String, Dimension > F_FEATURE_DIMENSIONS = new HashMap< >( 9 );

	private static final HashMap< String, Boolean > F_ISINT = new HashMap< >( 9 );

	private static final String VARIANCE = "VARIANCE";

	private static final String KURTOSIS = "KURTOSIS";

	private static final String SKEWNESS = "SKEWNESS";
	static
	{
		F_FEATURES.add( VARIANCE );
		F_FEATURES.add( KURTOSIS );
		F_FEATURES.add( SKEWNESS );
		F_FEATURE_NAMES.put( VARIANCE, "Variance" );
		F_FEATURE_NAMES.put( KURTOSIS, "Kurtosis" );
		F_FEATURE_NAMES.put( SKEWNESS, "Skewness" );
		F_FEATURE_SHORT_NAMES.put( VARIANCE, "Var." );
		F_FEATURE_SHORT_NAMES.put( KURTOSIS, "Kurtosis" );
		F_FEATURE_SHORT_NAMES.put( SKEWNESS, "Skewness" );
		F_FEATURE_DIMENSIONS.put( VARIANCE, Dimension.INTENSITY_SQUARED );
		F_FEATURE_DIMENSIONS.put( KURTOSIS, Dimension.NONE );
		F_FEATURE_DIMENSIONS.put( SKEWNESS, Dimension.NONE );
		F_ISINT.put( VARIANCE, Boolean.FALSE );
		F_ISINT.put( KURTOSIS, Boolean.FALSE );
		F_ISINT.put( SKEWNESS, Boolean.FALSE );
	}

	/** Stores error messages when reading parameters. */
	private String errorMessage;

	/*
	 * CONSTRUCTORS
	 */

	public TmXmlReader_v12( final File file )
	{
		super( file );
	}

	/*
	 * PUBLIC METHODS
	 */

	@Override
	public void readSettings( final Settings settings,  final TrackerProvider trackerProvider, 
			final EdgeAnalyzerProvider edgeAnalyzerProvider, final TrackAnalyzerProvider trackAnalyzerProvider )
	{

		// Settings
		getBaseSettings( settings );
		
		getTrackerSettings( settings, trackerProvider );
		settings.imp = getImage();

		// BCellobject Filters
		final List< FeatureFilter > BCellobjectFilters = getBCellobjectFeatureFilters();
		final FeatureFilter initialFilter = getInitialFilter();
		settings.initialBCellobjectFilterValue = initialFilter.value;
		settings.setBCellobjectFilters( BCellobjectFilters );

		// Track Filters
		final List< FeatureFilter > trackFilters = getTrackFeatureFilters();
		settings.setTrackFilters( trackFilters );

		// Feature analyzers. By default, we add them all.

		settings.clearBCellobjectAnalyzerFactories();


		settings.clearEdgeAnalyzers();
		final List< String > edgeAnalyzerKeys = edgeAnalyzerProvider.getKeys();
		for ( final String key : edgeAnalyzerKeys )
		{
			final EdgeAnalyzer edgeAnalyzer = edgeAnalyzerProvider.getFactory( key );
			settings.addEdgeAnalyzer( edgeAnalyzer );
		}

		settings.clearTrackAnalyzers();
		final List< String > trackAnalyzerKeys = trackAnalyzerProvider.getKeys();
		for ( final String key : trackAnalyzerKeys )
		{
			final TrackAnalyzer trackAnalyzer = trackAnalyzerProvider.getFactory( key );
			settings.addTrackAnalyzer( trackAnalyzer );
		}
	}

	@Override
	public String getGUIState()
	{
		return ConfigureViewsDescriptor.KEY;
	}

	@Override
	public Collection< TrackMateModelView > getViews( final InteractiveBud parent, final ViewProvider provider, final Model model, final Settings settings, final SelectionModel selectionModel )
	{
		final Collection< TrackMateModelView > views = new ArrayList< >( 1 );
		views.add( provider.getFactory( HyperStackDisplayer.KEY ).create( parent, model, settings, selectionModel ) );
		return views;
	}

	@Override
	public Model getModel()
	{
		final Model model = new Model();

		// BCellobjects
		final BCellobjectCollection allBCellobjects = getAllBCellobjects();
		final Map< Integer, Set< Integer > > filteredIDs = getFilteredBCellobjectsIDs();
		if ( null != filteredIDs )
		{
			for ( final Integer frame : filteredIDs.keySet() )
			{
				for ( final Integer ID : filteredIDs.get( frame ) )
				{
					cache.get( ID ).putFeature( BCellobjectCollection.VISIBLITY, BCellobjectCollection.ONE );
				}
			}
		}
		model.setBCellobjects( allBCellobjects, false );

		// Tracks
		readTracks( model );

		// Physical units
		final Element infoEl = root.getChild( IMAGE_ELEMENT_KEY_v12 );
		if ( null != infoEl )
		{
			final String spaceUnits = infoEl.getAttributeValue( IMAGE_SPATIAL_UNITS_ATTRIBUTE_NAME_v12 );
			final String timeUnits = infoEl.getAttributeValue( IMAGE_TIME_UNITS_ATTRIBUTE_NAME_v12 );
			model.setPhysicalUnits( spaceUnits, timeUnits );
		}

		// Features
		declareDefaultFeatures( model.getFeatureModel() );

		return model;
	}

	/*
	 * PRIVATE METHODS
	 */

	/**
	 * We must initialize the model with feature declarations that match the
	 * feature we retrieved from the file.
	 */
	private void declareDefaultFeatures( final FeatureModel fm )
	{
		// BCellobjects:
		fm.declareBCellobjectFeatures( BCellobject.FEATURES, BCellobject.FEATURE_NAMES, BCellobject.FEATURE_SHORT_NAMES, BCellobject.FEATURE_DIMENSIONS, BCellobject.IS_INT );

		fm.declareBCellobjectFeatures( F_FEATURES, F_FEATURE_NAMES, F_FEATURE_SHORT_NAMES, F_FEATURE_DIMENSIONS, F_ISINT );

		// Edges: no edge features in v1.2

		// Tracks:
		fm.declareTrackFeatures( TrackDurationAnalyzer.FEATURES, TrackDurationAnalyzer.FEATURE_NAMES,
				TrackDurationAnalyzer.FEATURE_SHORT_NAMES, TrackDurationAnalyzer.FEATURE_DIMENSIONS, TrackDurationAnalyzer.IS_INT );
	}

	/**
	 * Load the tracks, the track features and the ID of the visible tracks into
	 * the model modified by this reader.
	 *
	 * @param model
	 *            the model to feed.
	 */
	private void readTracks( final Model model )
	{

		final Element allTracksElement = root.getChild( TRACK_COLLECTION_ELEMENT_KEY_v12 );
		if ( null == allTracksElement ) { return; }

		if ( null == cache )
		{
			getAllBCellobjects(); // build the cache if it's not there
		}

		final SimpleWeightedGraph< BCellobject, DefaultWeightedEdge > graph = new SimpleWeightedGraph< >( DefaultWeightedEdge.class );

		// Load tracks
		final List< Element > trackElements = allTracksElement.getChildren( TRACK_ELEMENT_KEY_v12 );

		final Map< Integer, Set< BCellobject > > trackBCellobjects = new HashMap< >( trackElements.size() );
		final Map< Integer, Set< DefaultWeightedEdge > > trackEdges = new HashMap< >( trackElements.size() );
		final Map< Integer, String > trackNames = new HashMap< >( trackElements.size() );

		for ( final Element trackElement : trackElements )
		{

			// Get track ID as it is saved on disk
			final int trackID = readIntAttribute( trackElement, TRACK_ID_ATTRIBUTE_NAME_v12, logger );

			// Iterate over edges
			final List< Element > edgeElements = trackElement.getChildren( TRACK_EDGE_ELEMENT_KEY_v12 );

			final Set< BCellobject > BCellobjects = new HashSet< >( edgeElements.size() );
			final Set< DefaultWeightedEdge > edges = new HashSet< >( edgeElements.size() );

			for ( final Element edgeElement : edgeElements )
			{

				// Get source and target ID for this edge
				final int sourceID = readIntAttribute( edgeElement, TRACK_EDGE_SOURCE_ATTRIBUTE_NAME_v12, logger );
				final int targetID = readIntAttribute( edgeElement, TRACK_EDGE_TARGET_ATTRIBUTE_NAME_v12, logger );

				// Get matching BCellobjects from the cache
				final BCellobject sourceBCellobject = cache.get( sourceID );
				final BCellobject targetBCellobject = cache.get( targetID );

				// Get weight
				double weight = 0;
				if ( null != edgeElement.getAttribute( TRACK_EDGE_WEIGHT_ATTRIBUTE_NAME_v12 ) )
				{
					weight = readDoubleAttribute( edgeElement, TRACK_EDGE_WEIGHT_ATTRIBUTE_NAME_v12, logger );
				}

				// Error check
				if ( null == sourceBCellobject )
				{
					logger.error( "Unknown BCellobject ID: " + sourceID );
					continue;
				}
				if ( null == targetBCellobject )
				{
					logger.error( "Unknown BCellobject ID: " + targetID );
					continue;
				}

				if ( sourceBCellobject.equals( targetBCellobject ) )
				{
					logger.error( "Bad link for track " + trackID + ". Source = Target with ID: " + sourceID );
					continue;
				}

				// Add BCellobjects to graph and build edge
				graph.addVertex( sourceBCellobject );
				graph.addVertex( targetBCellobject );
				final DefaultWeightedEdge edge = graph.addEdge( sourceBCellobject, targetBCellobject );

				if ( edge == null )
				{
					logger.error( "Bad edge found for track " + trackID );
					continue;
				}
				graph.setEdgeWeight( edge, weight );

				// Add to current track sets
				BCellobjects.add( sourceBCellobject );
				BCellobjects.add( targetBCellobject );
				edges.add( edge );

			} // Finished parsing over the edges of the track

			// Store one of the BCellobject in the saved trackID key map
			trackBCellobjects.put( trackID, BCellobjects );
			trackEdges.put( trackID, edges );
			trackNames.put( trackID, "Track_" + trackID ); // Default name
		}

		final Map< Integer, Boolean > trackVisibility = new HashMap< >( trackElements.size() );
		final Set< Integer > savedFilteredTrackIDs = readFilteredTrackIDs();
		for ( final Integer id : savedFilteredTrackIDs )
		{
			trackVisibility.put( id, Boolean.TRUE );
		}
		final Set< Integer > ids = new HashSet< >( trackBCellobjects.keySet() );
		ids.removeAll( savedFilteredTrackIDs );
		for ( final Integer id : ids )
		{
			trackVisibility.put( id, Boolean.FALSE );
		}

		/*
		 * Pass all of this to the model
		 */
		model.getTrackModel().from( graph, trackBCellobjects, trackEdges, trackVisibility, trackNames );

		/*
		 * We do the same thing for the track features.
		 */
		final FeatureModel fm = model.getFeatureModel();
		final Map< Integer, Map< String, Double > > savedFeatureMap = readTrackFeatures();
		for ( final Integer savedKey : savedFeatureMap.keySet() )
		{

			final Map< String, Double > savedFeatures = savedFeatureMap.get( savedKey );
			for ( final String feature : savedFeatures.keySet() )
			{
				fm.putTrackFeature( savedKey, feature, savedFeatures.get( feature ) );
			}

		}
	}

	/**
	 * @return the list of {@link FeatureFilter} for tracks stored in this file.
	 *         Return <code>null</code> if the track feature filters data cannot
	 *         be found in the file.
	 */
	private List< FeatureFilter > getTrackFeatureFilters()
	{
		final List< FeatureFilter > featureThresholds = new ArrayList< >();
		final Element ftCollectionEl = root.getChild( TRACK_FILTER_COLLECTION_ELEMENT_KEY );
		if ( null == ftCollectionEl ) { return null; }
		final List< Element > ftEls = ftCollectionEl.getChildren( FILTER_ELEMENT_KEY );
		for ( final Element ftEl : ftEls )
		{
			final String feature = ftEl.getAttributeValue( FILTER_FEATURE_ATTRIBUTE_NAME );
			final Double value = readDoubleAttribute( ftEl, FILTER_VALUE_ATTRIBUTE_NAME, logger );
			final boolean isAbove = readBooleanAttribute( ftEl, FILTER_ABOVE_ATTRIBUTE_NAME, logger );
			final FeatureFilter ft = new FeatureFilter( feature, value, isAbove );
			featureThresholds.add( ft );
		}
		return featureThresholds;
	}

	/**
	 * Return the initial threshold on quality stored in this file. Return
	 * <code>null</code> if the initial threshold data cannot be found in the
	 * file.
	 */
	private FeatureFilter getInitialFilter()
	{
		final Element itEl = root.getChild( INITIAL_BCellobject_FILTER_ELEMENT_KEY_v12 );
		if ( null == itEl ) { return null; }
		final String feature = itEl.getAttributeValue( FILTER_FEATURE_ATTRIBUTE_NAME_v12 );
		final double value = readFloatAttribute( itEl, FILTER_VALUE_ATTRIBUTE_NAME_v12, logger );
		final boolean isAbove = readBooleanAttribute( itEl, FILTER_ABOVE_ATTRIBUTE_NAME_v12, logger );
		final FeatureFilter ft = new FeatureFilter( feature, value, isAbove );
		return ft;
	}

	/**
	 * Return the list of {@link FeatureFilter} for BCellobjects stored in this file.
	 * Return <code>null</code> if the BCellobject feature filters data cannot be found
	 * in the file.
	 */
	private List< FeatureFilter > getBCellobjectFeatureFilters()
	{
		final List< FeatureFilter > featureThresholds = new ArrayList< >();
		final Element ftCollectionEl = root.getChild( BCellobject_FILTER_COLLECTION_ELEMENT_KEY_v12 );
		if ( null == ftCollectionEl ) { return null; }
		final List< Element > ftEls = ftCollectionEl.getChildren( FILTER_ELEMENT_KEY_v12 );
		for ( final Element ftEl : ftEls )
		{
			final String feature = ftEl.getAttributeValue( FILTER_FEATURE_ATTRIBUTE_NAME_v12 );
			final double value = readFloatAttribute( ftEl, FILTER_VALUE_ATTRIBUTE_NAME_v12, logger );
			final boolean isAbove = readBooleanAttribute( ftEl, FILTER_ABOVE_ATTRIBUTE_NAME_v12, logger );
			final FeatureFilter ft = new FeatureFilter( feature, value, isAbove );
			featureThresholds.add( ft );
		}
		return featureThresholds;
	}

	/**
	 * @return a map of the saved track features, as they appear in the file
	 */
	private Map< Integer, Map< String, Double > > readTrackFeatures()
	{

		final HashMap< Integer, Map< String, Double > > featureMap = new HashMap< >();

		final Element allTracksElement = root.getChild( TRACK_COLLECTION_ELEMENT_KEY_v12 );
		if ( null == allTracksElement ) { return null; }

		// Load tracks
		final List< Element > trackElements = allTracksElement.getChildren( TRACK_ELEMENT_KEY_v12 );
		for ( final Element trackElement : trackElements )
		{

			int trackID = -1;
			try
			{
				trackID = trackElement.getAttribute( TRACK_ID_ATTRIBUTE_NAME_v12 ).getIntValue();
			}
			catch ( final DataConversionException e1 )
			{
				logger.error( "Found a track with invalid trackID for " + trackElement + ". Skipping.\n" );
				continue;
			}

			final HashMap< String, Double > trackMap = new HashMap< >();

			final List< Attribute > attributes = trackElement.getAttributes();
			for ( final Attribute attribute : attributes )
			{

				final String attName = attribute.getName();
				if ( attName.equals( TRACK_ID_ATTRIBUTE_NAME_v12 ) )
				{ // Skip trackID attribute
					continue;
				}

				Double attVal = Double.NaN;
				try
				{
					attVal = attribute.getDoubleValue();
				}
				catch ( final DataConversionException e )
				{
					logger.error( "Track " + trackID + ": Cannot read the feature " + attName + " value. Skipping.\n" );
					continue;
				}

				trackMap.put( attName, attVal );

			}

			featureMap.put( trackID, trackMap );
		}

		return featureMap;

	}

	private void getBaseSettings( final Settings settings )
	{
		// Basic settings
		final Element settingsEl = root.getChild( SETTINGS_ELEMENT_KEY_v12 );
		if ( null != settingsEl )
		{
			settings.xstart = readIntAttribute( settingsEl, SETTINGS_XSTART_ATTRIBUTE_NAME_v12, logger, 1 );
			settings.xend = readIntAttribute( settingsEl, SETTINGS_XEND_ATTRIBUTE_NAME_v12, logger, 512 );
			settings.ystart = readIntAttribute( settingsEl, SETTINGS_YSTART_ATTRIBUTE_NAME_v12, logger, 1 );
			settings.yend = readIntAttribute( settingsEl, SETTINGS_YEND_ATTRIBUTE_NAME_v12, logger, 512 );
			settings.zstart = readIntAttribute( settingsEl, SETTINGS_ZSTART_ATTRIBUTE_NAME_v12, logger, 1 );
			settings.zend = readIntAttribute( settingsEl, SETTINGS_ZEND_ATTRIBUTE_NAME_v12, logger, 10 );
			settings.tstart = readIntAttribute( settingsEl, SETTINGS_TSTART_ATTRIBUTE_NAME_v12, logger, 1 );
			settings.tend = readIntAttribute( settingsEl, SETTINGS_TEND_ATTRIBUTE_NAME_v12, logger, 10 );
		}
		// Image info settings
		final Element infoEl = root.getChild( IMAGE_ELEMENT_KEY_v12 );
		if ( null != infoEl )
		{
			settings.dx = readFloatAttribute( infoEl, IMAGE_PIXEL_WIDTH_ATTRIBUTE_NAME_v12, logger );
			settings.dy = readFloatAttribute( infoEl, IMAGE_PIXEL_HEIGHT_ATTRIBUTE_NAME_v12, logger );
			settings.dz = readFloatAttribute( infoEl, IMAGE_VOXEL_DEPTH_ATTRIBUTE_NAME_v12, logger );
			settings.dt = readFloatAttribute( infoEl, IMAGE_TIME_INTERVAL_ATTRIBUTE_NAME_v12, logger );
			settings.width = readIntAttribute( infoEl, IMAGE_WIDTH_ATTRIBUTE_NAME_v12, logger, 512 );
			settings.height = readIntAttribute( infoEl, IMAGE_HEIGHT_ATTRIBUTE_NAME_v12, logger, 512 );
			settings.nslices = readIntAttribute( infoEl, IMAGE_NSLICES_ATTRIBUTE_NAME_v12, logger, 1 );
			settings.nframes = readIntAttribute( infoEl, IMAGE_NFRAMES_ATTRIBUTE_NAME_v12, logger, 1 );
			settings.imageFileName = infoEl.getAttributeValue( IMAGE_FILENAME_v12_ATTRIBUTE_NAME_v12 );
			settings.imageFolder = infoEl.getAttributeValue( IMAGE_FOLDER_ATTRIBUTE_NAME_v12 );
		}
	}

	
	/**
	 * Update the given {@link Settings} object with the TrackerSettings
	 * and {@link BCellobjectTracker} fields named {@link Settings#trackerSettings} and
	 * Settings#tracker read within the XML file this reader is
	 * initialized with.
	 * <p>
	 * If the tracker settings XML element is not present in the file, the
	 * {@link Settings} object is not updated. If the tracker settings or the
	 * tracker info can be read, but cannot be understood (most likely because
	 * the class the XML refers to is unknown) then a default object is
	 * substituted.
	 *
	 * @param settings
	 *            the base {@link Settings} object to update.
	 */
	private void getTrackerSettings( final Settings settings, final TrackerProvider provider )
	{
		final Element element = root.getChild( TRACKER_SETTINGS_ELEMENT_KEY_v12 );
		if ( null == element ) { return; }

		// Deal with tracker
		String trackerKey;
		final String trackerClassName = element.getAttributeValue( TRACKER_CLASS_ATTRIBUTE_NAME_v12 );

		if ( null == trackerClassName )
		{
			logger.error( "\nTracker class is not present.\n" );
			logger.error( "Substituting default.\n" );
			trackerKey = SimpleFastLAPTrackerFactory.THIS2_TRACKER_KEY;

		}
		else
		{

			if ( trackerClassName.equals( "Buddy.plugin.trackmate.tracking.SimpleFastLAPTracker" ) || trackerClassName.equals( "Buddy.plugin.trackmate.tracking.SimpleLAPTracker" ) )
			{
				// convert to simple fast version
				trackerKey = SimpleFastLAPTrackerFactory.THIS2_TRACKER_KEY;
			}
			else if ( trackerClassName.equals( "Buddy.plugin.trackmate.tracking.FastLAPTracker" ) || trackerClassName.equals( "Buddy.plugin.trackmate.tracking.LAPTracker" ) )
			{
				// convert to fast version
				trackerKey = FastLAPTrackerFactory.THIS_TRACKER_KEY;
			}
			else if ( trackerClassName.equals( "Buddy.plugin.trackmate.tracking.kdtree.NearestNeighborTracker" ) )
			{
				trackerKey = NearestNeighborTrackerFactory.TRACKER_KEY;
			}
			else
			{
				logger.error( "\nUnknown tracker: " + trackerClassName + ".\n" );
				logger.error( "Substituting default.\n" );
				trackerKey = SimpleFastLAPTrackerFactory.THIS2_TRACKER_KEY;
			}
		}
		BCellobjectTrackerFactory factory = provider.getFactory( trackerKey );
		if ( null == factory )
		{
			logger.error( "\nUnknown tracker: " + trackerClassName + ".\n" );
			logger.error( "Substituting default tracker.\n" );
			factory = new SimpleFastLAPTrackerFactory();
		}
		settings.trackerFactory = factory;

		// Deal with tracker settings
		{
			Map< String, Object > ts = new HashMap< >();

			final String trackerSettingsClassName = element.getAttributeValue( TRACKER_SETTINGS_CLASS_ATTRIBUTE_NAME_v12 );

			if ( null == trackerSettingsClassName )
			{

				logger.error( "\nTracker settings class is not present.\n" );
				logger.error( "Substituting default one.\n" );
				ts = factory.getDefaultSettings();

			}
			else
			{

				// All LAP trackers
				if ( trackerSettingsClassName.equals( "Buddy.plugin.trackmate.tracking.LAPTrackerSettings" ) )
				{

					if ( trackerKey.equals( SimpleFastLAPTrackerFactory.THIS2_TRACKER_KEY ) || trackerKey.equals( FastLAPTrackerFactory.THIS_TRACKER_KEY ) )
					{

						/*
						 * Read
						 */

						final double alternativeObjectLinkingCostFactor = readDoubleAttribute( element, TRACKER_SETTINGS_ALTERNATE_COST_FACTOR_ATTNAME_v12, Logger.VOID_LOGGER );
						final double cutoffPercentile = readDoubleAttribute( element, TRACKER_SETTINGS_CUTOFF_PERCENTILE_ATTNAME_v12, Logger.VOID_LOGGER );
						final double blockingValue = readDoubleAttribute( element, TRACKER_SETTINGS_BLOCKING_VALUE_ATTNAME_v12, Logger.VOID_LOGGER );
						// Linking
						final Element linkingElement = element.getChild( TRACKER_SETTINGS_LINKING_ELEMENT );
						final double linkingDistanceCutOff = readDistanceCutoffAttribute( linkingElement );
						final Map< String, Double > linkingFeaturePenalties = readTrackerFeatureMap( linkingElement );
						// Gap-closing
						final Element gapClosingElement = element.getChild( TRACKER_SETTINGS_GAP_CLOSING_ELEMENT );
						final boolean allowGapClosing = readBooleanAttribute( gapClosingElement, TRACKER_SETTINGS_ALLOW_EVENT_ATTNAME_v12, Logger.VOID_LOGGER );
						final double gapClosingDistanceCutoff = readDistanceCutoffAttribute( gapClosingElement );
						final double gapClosingTimeCutoff = readTimeCutoffAttribute( gapClosingElement );
						final Map< String, Double > gapClosingFeaturePenalties = readTrackerFeatureMap( gapClosingElement );
						// Splitting
						final Element splittingElement = element.getChild( TRACKER_SETTINGS_SPLITTING_ELEMENT );
						final boolean allowSplitting = readBooleanAttribute( splittingElement, TRACKER_SETTINGS_ALLOW_EVENT_ATTNAME_v12, Logger.VOID_LOGGER );
						final double splittingDistanceCutoff = readDistanceCutoffAttribute( splittingElement );
						@SuppressWarnings( "unused" )
						final double splittingTimeCutoff = readTimeCutoffAttribute( splittingElement ); // IGNORED
						final Map< String, Double > splittingFeaturePenalties = readTrackerFeatureMap( splittingElement );
						// Merging
						final Element mergingElement = element.getChild( TRACKER_SETTINGS_MERGING_ELEMENT );
						final boolean allowMerging = readBooleanAttribute( mergingElement, TRACKER_SETTINGS_ALLOW_EVENT_ATTNAME_v12, Logger.VOID_LOGGER );
						final double mergingDistanceCutoff = readDistanceCutoffAttribute( mergingElement );
						@SuppressWarnings( "unused" )
						final double mergingTimeCutoff = readTimeCutoffAttribute( mergingElement ); // IGNORED
						final Map< String, Double > mergingFeaturePenalties = readTrackerFeatureMap( mergingElement );

						/*
						 * Store
						 */

						ts.put( KEY_ALTERNATIVE_LINKING_COST_FACTOR, alternativeObjectLinkingCostFactor );
						ts.put( KEY_CUTOFF_PERCENTILE, cutoffPercentile );
						ts.put( KEY_BLOCKING_VALUE, blockingValue );
						// Linking
						ts.put( KEY_LINKING_MAX_DISTANCE, linkingDistanceCutOff );
						ts.put( KEY_LINKING_FEATURE_PENALTIES, linkingFeaturePenalties );
						// Gap-closing
						ts.put( KEY_ALLOW_GAP_CLOSING, allowGapClosing );
						ts.put( KEY_GAP_CLOSING_MAX_DISTANCE, gapClosingDistanceCutoff );
						ts.put( KEY_GAP_CLOSING_MAX_FRAME_GAP, ( int ) ( gapClosingTimeCutoff / settings.dt ) ); // CONVERTED
						ts.put( KEY_GAP_CLOSING_FEATURE_PENALTIES, gapClosingFeaturePenalties );
						// Splitting
						ts.put( KEY_ALLOW_TRACK_SPLITTING, allowSplitting );
						ts.put( KEY_SPLITTING_MAX_DISTANCE, splittingDistanceCutoff );
						ts.put( KEY_SPLITTING_FEATURE_PENALTIES, splittingFeaturePenalties );
						// the rest is IGNORED
						// Merging
						ts.put( KEY_ALLOW_TRACK_MERGING, allowMerging );
						ts.put( KEY_MERGING_MAX_DISTANCE, mergingDistanceCutoff );
						ts.put( KEY_MERGING_FEATURE_PENALTIES, mergingFeaturePenalties );
						// the rest is ignored

					}
					else
					{

						// They do not match. We DO NOT give priority to what
						// has been saved. That way we always
						// have something that works (when invoking the process
						// methods of the trackmate).

						logger.error( "\nTracker settings class (" + trackerSettingsClassName + ") does not match tracker requirements (" +
								ts.getClass().getName() + "),\n" );
						logger.error( "substituting default values.\n" );
					}

				}
				else if ( trackerSettingsClassName.equals( "Buddy.plugin.trackmate.tracking.kdtree.NearestNeighborTrackerSettings" ) )
				{

					if ( trackerKey.equals( NearestNeighborTrackerFactory.TRACKER_KEY ) )
					{

						// The saved class matched, we can updated the settings
						// created above with the file content
						final double maxDist = readDoubleAttribute( element, MAX_LINKING_DISTANCE_ATTRIBUTE, Logger.VOID_LOGGER );
						ts.put( KEY_LINKING_MAX_DISTANCE, maxDist );

					}
					else
					{

						// They do not match. We DO NOT give priority to what
						// has been saved. That way we always
						// have something that works (when invoking the process
						// methods of the trackmate).

						logger.error( "\nTracker settings class (" + trackerSettingsClassName + ") does not match tracker requirements (" +
								ts.getClass().getName() + "),\n" );
						logger.error( "substituting default values.\n" );
					}

				}
				else
				{

					logger.error( "\nTracker settings class (" + trackerSettingsClassName + ") is unknown.\n" );
					logger.error( "Substituting default one.\n" );

				}
			}
			settings.trackerSettings = ts;
		}
	}

	/**
	 * Return the list of all BCellobjects stored in this file.
	 *
	 * @return a {@link BCellobjectCollection}. Return <code>null</code> if the BCellobject
	 *         section is not present in the file.
	 */
	private BCellobjectCollection getAllBCellobjects()
	{
		final Element BCellobjectCollection = root.getChild( BCellobject_COLLECTION_ELEMENT_KEY_v12 );
		if ( null == BCellobjectCollection ) { return null; }

		// Retrieve children elements for each frame
		final List< Element > frameContent = BCellobjectCollection.getChildren( BCellobject_FRAME_COLLECTION_ELEMENT_KEY_v12 );

		// Determine total number of BCellobjects
		int nBCellobjects = 0;
		for ( final Element currentFrameContent : frameContent )
		{
			nBCellobjects += currentFrameContent.getChildren( BCellobject_ELEMENT_KEY_v12 ).size();
		}

		// Instantiate cache
		cache = new ConcurrentHashMap< >( nBCellobjects );

		int currentFrame = 0;
		ArrayList< BCellobject > BCellobjectList;
		final BCellobjectCollection allBCellobjects = new BCellobjectCollection();

		for ( final Element currentFrameContent : frameContent )
		{

			currentFrame = readIntAttribute( currentFrameContent, FRAME_ATTRIBUTE_NAME_v12, logger );
			final List< Element > BCellobjectContent = currentFrameContent.getChildren( BCellobject_ELEMENT_KEY_v12 );
			BCellobjectList = new ArrayList< >( BCellobjectContent.size() );
			

			allBCellobjects.put( currentFrame, BCellobjectList );
		}
		return allBCellobjects;
	}

	/**
	 * Return the filtered BCellobjects stored in this file, taken from the list of all
	 * BCellobjects, given in argument.
	 * <p>
	 * The {@link BCellobject} objects in this list will be the same that of the main
	 * list given in argument. If a BCellobject ID referenced in the file is in the
	 * selection but not in the list given in argument, it is simply ignored,
	 * and not added to the selection list. That way, it is certain that all
	 * BCellobjects belonging to the selection list also belong to the global list.
	 *
	 * @return a {@link BCellobjectCollection}. Each BCellobject of this collection belongs
	 *         also to the given collection. Return <code>null</code> if the
	 *         BCellobject selection section does is not present in the file.
	 */
	private Map< Integer, Set< Integer > > getFilteredBCellobjectsIDs()
	{
		final Element selectedBCellobjectCollection = root.getChild( FILTERED_BCellobject_ELEMENT_KEY_v12 );
		if ( null == selectedBCellobjectCollection ) { return null; }

		final List< Element > frameContent = selectedBCellobjectCollection.getChildren( FILTERED_BCellobject_COLLECTION_ELEMENT_KEY_v12 );
		final Map< Integer, Set< Integer > > visibleIDs = new HashMap< >( frameContent.size() );

		for ( final Element currentFrameContent : frameContent )
		{
			final int currentFrame = readIntAttribute( currentFrameContent, FRAME_ATTRIBUTE_NAME_v12, logger );
			final List< Element > BCellobjectContent = currentFrameContent.getChildren( BCellobject_ID_ELEMENT_KEY_v12 );
			final HashSet< Integer > IDs = new HashSet< >( BCellobjectContent.size() );
			// Loop over all BCellobject element
			for ( final Element BCellobjectEl : BCellobjectContent )
			{
				// Find corresponding BCellobject in cache
				final int ID = readIntAttribute( BCellobjectEl, BCellobject_ID_ATTRIBUTE_NAME_v12, logger );
				IDs.add( ID );
			}

			visibleIDs.put( currentFrame, IDs );
		}
		return visibleIDs;
	}

	/**
	 * Read and return the list of track indices that define the filtered track
	 * collection.
	 */
	private Set< Integer > readFilteredTrackIDs()
	{
		final Element filteredTracksElement = root.getChild( FILTERED_TRACK_ELEMENT_KEY_v12 );
		if ( null == filteredTracksElement ) { return null; }

		// Work because the track splitting from the graph is deterministic
		final List< Element > elements = filteredTracksElement.getChildren( TRACK_ID_ELEMENT_KEY_v12 );
		final HashSet< Integer > filteredTrackIndices = new HashSet< >( elements.size() );
		for ( final Element indexElement : elements )
		{
			final int trackID = readIntAttribute( indexElement, TRACK_ID_ATTRIBUTE_NAME_v12, logger );
			filteredTrackIndices.add( trackID );
		}
		return filteredTrackIndices;
	}

	private ImagePlus getImage()
	{
		final Element imageInfoElement = root.getChild( IMAGE_ELEMENT_KEY_v12 );
		if ( null == imageInfoElement ) { return null; }
		final String filename = imageInfoElement.getAttributeValue( IMAGE_FILENAME_v12_ATTRIBUTE_NAME_v12 );
		String folder = imageInfoElement.getAttributeValue( IMAGE_FOLDER_ATTRIBUTE_NAME_v12 );
		if ( null == filename || filename.isEmpty() ) { return null; }
		if ( null == folder || folder.isEmpty() )
		{
			folder = file.getParent(); // it is a relative path, then
		}
		File imageFile = new File( folder, filename );
		if ( !imageFile.exists() || !imageFile.canRead() )
		{
			// Could not find it to the absolute path. Then we look for the same
			// path of the xml file
			logger.log( "Could not find the image in " + folder + ". Looking in xml file location...\n" );
			folder = file.getParent();
			imageFile = new File( folder, filename );
			if ( !imageFile.exists() || !imageFile.canRead() ) { return null; }
		}
		return IJ.openImage( imageFile.getAbsolutePath() );
	}

	
	private boolean readDouble( final Element element, final String attName, final Map< String, Object > settings, final String mapKey )
	{
		final String str = element.getAttributeValue( attName );
		if ( null == str )
		{
			errorMessage = "Attribute " + attName + " could not be found in XML element.";
			return false;
		}
		try
		{
			final double val = NumberParser.parseDouble( str );
			settings.put( mapKey, val );
		}
		catch ( final NumberFormatException nfe )
		{
			errorMessage = "Could not read " + attName + " attribute as a double value. Got " + str + ".";
			return false;
		}
		return true;
	}

	private boolean readInteger( final Element element, final String attName, final Map< String, Object > settings, final String mapKey )
	{
		final String str = element.getAttributeValue( attName );
		if ( null == str )
		{
			errorMessage = "Attribute " + attName + " could not be found in XML element.";
			return false;
		}
		try
		{
			final int val = NumberParser.parseInteger( str );
			settings.put( mapKey, val );
		}
		catch ( final NumberFormatException nfe )
		{
			errorMessage = "Could not read " + attName + " attribute as an integer value. Got " + str + ".";
			return false;
		}
		return true;
	}

	private boolean readBoolean( final Element element, final String attName, final Map< String, Object > settings, final String mapKey )
	{
		final String str = element.getAttributeValue( attName );
		if ( null == str )
		{
			errorMessage = "Attribute " + attName + " could not be found in XML element.";
			return false;
		}
		try
		{
			final boolean val = Boolean.parseBoolean( str );
			settings.put( mapKey, val );
		}
		catch ( final NumberFormatException nfe )
		{
			errorMessage = "Could not read " + attName + " attribute as an boolean value. Got " + str + ".";
			return false;
		}
		return true;
	}

	private static final double readDistanceCutoffAttribute( final Element element )
	{
		double val = 0;
		try
		{
			val = element.getChild( TRACKER_SETTINGS_DISTANCE_CUTOFF_ELEMENT )
					.getAttribute( TRACKER_SETTINGS_DISTANCE_CUTOFF_ATTNAME_v12 ).getDoubleValue();
		}
		catch ( final DataConversionException e )
		{}
		return val;
	}

	private static final double readTimeCutoffAttribute( final Element element )
	{
		double val = 0;
		try
		{
			val = element.getChild( TRACKER_SETTINGS_TIME_CUTOFF_ELEMENT )
					.getAttribute( TRACKER_SETTINGS_TIME_CUTOFF_ATTNAME_v12 ).getDoubleValue();
		}
		catch ( final DataConversionException e )
		{}
		return val;
	}

	/**
	 * Look for all the sub-elements of <code>element</code> with the name
	 * TRACKER_SETTINGS_FEATURE_ELEMENT, fetch the feature attributes from them,
	 * and returns them in a map.
	 */
	private static final Map< String, Double > readTrackerFeatureMap( final Element element )
	{
		final Map< String, Double > map = new HashMap< >();
		final List< Element > featurelinkingElements = element.getChildren( TRACKER_SETTINGS_FEATURE_ELEMENT );
		for ( final Element el : featurelinkingElements )
		{
			final List< Attribute > atts = el.getAttributes();
			for ( final Attribute att : atts )
			{
				final String feature = att.getName();
				Double cutoff;
				try
				{
					cutoff = att.getDoubleValue();
				}
				catch ( final DataConversionException e )
				{
					cutoff = 0d;
				}
				map.put( feature, cutoff );
			}
		}
		return map;
	}

}

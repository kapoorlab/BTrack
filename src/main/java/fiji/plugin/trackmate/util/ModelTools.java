package Buddy.plugin.trackmate.util;

import static Buddy.plugin.trackmate.tracking.TrackerKeys.KEY_LINKING_MAX_DISTANCE;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import Buddy.plugin.trackmate.BCellobjectCollection;
import Buddy.plugin.trackmate.FeatureModel;
import Buddy.plugin.trackmate.Model;
import Buddy.plugin.trackmate.SelectionModel;
import Buddy.plugin.trackmate.tracking.kdtree.NearestNeighborTracker;
import budDetector.BCellobject;

/**
 * A collection of static utilities made to ease the manipulation of a TrackMate
 * {@link Model} and {@link SelectionModel}.
 * 
 * @author Jean-Yves Tinevez - 2013 - 2014
 * 
 */
public class ModelTools
{
	private ModelTools()
	{}

	/**
	 * Sets the content of the specified selection model to be the whole tracks
	 * the selected BCellobjects belong to. Other selected edges are removed from the
	 * selection.
	 * 
	 * @param selectionModel
	 *            the {@link SelectionModel} that will be updated by this call.
	 */
	public static void selectTrack( final SelectionModel selectionModel )
	{
		selectionModel.clearEdgeSelection();
		selectionModel.selectTrack( selectionModel.getBCellobjectSelection(), Collections.< DefaultWeightedEdge >emptyList(), 0 );
	}

	/**
	 * Sets the content of the specified selection model to be the whole tracks
	 * the selected BCellobjects belong to, but searched for only forward in time
	 * (downward). Other selected edges are removed from the selection.
	 * 
	 * @param selectionModel
	 *            the {@link SelectionModel} that will be updated by this call.
	 */
	public static void selectTrackDownward( final SelectionModel selectionModel )
	{
		selectionModel.clearEdgeSelection();
		selectionModel.selectTrack( selectionModel.getBCellobjectSelection(), Collections.< DefaultWeightedEdge >emptyList(), -1 );
	}

	/**
	 * Sets the content of the specified selection model to be the whole tracks
	 * the selected BCellobjects belong to, but searched for only backward in time
	 * (backward). Other selected edges are removed from the selection.
	 * 
	 * @param selectionModel
	 *            the {@link SelectionModel} that will be updated by this call.
	 */
	public static void selectTrackUpward( final SelectionModel selectionModel )
	{
		selectionModel.clearEdgeSelection();
		selectionModel.selectTrack( selectionModel.getBCellobjectSelection(), Collections.< DefaultWeightedEdge >emptyList(), 1 );
	}

	/**
	 * Links all the BCellobjects in the selection, in time-forward order.
	 * 
	 * @param model
	 *            the model to modify.
	 * @param selectionModel
	 *            the selection that contains the BCellobjects to link.
	 */
	public static void linkBCellobjects( final Model model, final SelectionModel selectionModel )
	{

		/*
		 * Configure tracker
		 */

		final BCellobjectCollection BCellobjects = BCellobjectCollection.fromCollection( selectionModel.getBCellobjectSelection() );
		final Map< String, Object > settings = new HashMap<>( 1 );
		settings.put( KEY_LINKING_MAX_DISTANCE, Double.POSITIVE_INFINITY );
		final NearestNeighborTracker tracker = new NearestNeighborTracker( BCellobjects, settings );
		tracker.setNumThreads( 1 );

		/*
		 * Execute tracking
		 */

		if ( !tracker.checkInput() || !tracker.process() )
		{
			System.err.println( "Problem while computing BCellobject links: " + tracker.getErrorMessage() );
			return;
		}
		final SimpleWeightedGraph< BCellobject, DefaultWeightedEdge > graph = tracker.getResult();

		/*
		 * Copy found links in source model
		 */

		model.beginUpdate();
		try
		{
			for ( final DefaultWeightedEdge edge : graph.edgeSet() )
			{
				final BCellobject source = graph.getEdgeSource( edge );
				final BCellobject target = graph.getEdgeTarget( edge );
				model.addEdge( source, target, graph.getEdgeWeight( edge ) );
			}
		}
		finally
		{
			model.endUpdate();
		}
	}

	/**
	 * A comparator used to sort edges by ascending feature values.
	 * 
	 * @param feature
	 *            the feature to use for comparison. It is the caller
	 *            responsibility to ensure that all edges have the target
	 *            feature declared in the specified {@link FeatureModel}.
	 * @param fm
	 *            the {@link FeatureModel} to read feature values from.
	 * @return a new {@link Comparator}.
	 */
	public final static Comparator< DefaultWeightedEdge > featureEdgeComparator( final String feature, final FeatureModel fm )
	{
		final Comparator< DefaultWeightedEdge > comparator = new Comparator< DefaultWeightedEdge >()
		{
			@Override
			public int compare( final DefaultWeightedEdge e1, final DefaultWeightedEdge e2 )
			{
				final double t1 = fm.getEdgeFeature( e1, feature ).doubleValue();
				final double t2 = fm.getEdgeFeature( e2, feature ).doubleValue();

				if ( t1 < t2 ) { return -1; }
				if ( t1 > t2 ) { return 1; }
				return 0;
			}
		};
		return comparator;
	}

	/**
	 * A comparator used to sort tracks by ascending feature values.
	 * 
	 * @param feature
	 *            the feature to use for comparison. It is the caller
	 *            responsibility to ensure that all tracks have the target
	 *            feature declared in the specified {@link FeatureModel}.
	 * @param fm
	 *            the {@link FeatureModel} to read feature values from.
	 * @return a new {@link Comparator}.
	 */
	public final static Comparator< Integer > featureTrackComparator( final String feature, final FeatureModel fm )
	{
		final Comparator< Integer > comparator = new Comparator< Integer >()
		{
			@Override
			public int compare( final Integer e1, final Integer e2 )
			{
				final double t1 = fm.getTrackFeature( e1, feature ).doubleValue();
				final double t2 = fm.getTrackFeature( e2, feature ).doubleValue();

				if ( t1 < t2 ) { return -1; }
				if ( t1 > t2 ) { return 1; }
				return 0;
			}
		};
		return comparator;
	}

}

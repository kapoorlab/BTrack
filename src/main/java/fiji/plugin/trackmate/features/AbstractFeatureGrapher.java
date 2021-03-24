package Buddy.plugin.trackmate.features;

import static Buddy.plugin.trackmate.visualization.trackscheme.TrackScheme.TRACK_SCHEME_ICON;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import org.jfree.chart.renderer.InterpolatePaintScale;
import org.jgrapht.graph.DefaultWeightedEdge;

import Buddy.plugin.trackmate.Model;
import Buddy.plugin.trackmate.TrackMateOptionUtils;
import Buddy.plugin.trackmate.TrackModel;
import Buddy.plugin.trackmate.util.ExportableChartPanel;
import budDetector.BCellobject;

public abstract class AbstractFeatureGrapher
{

	protected static final Shape DEFAULT_SHAPE = new Ellipse2D.Double( -3, -3, 6, 6 );

	protected final InterpolatePaintScale paints = TrackMateOptionUtils.getOptions().getPaintScale();

	protected final String xFeature;

	protected final Set< String > yFeatures;

	protected final Model model;

	public AbstractFeatureGrapher( final String xFeature, final Set< String > yFeatures, final Model model )
	{
		this.xFeature = xFeature;
		this.yFeatures = yFeatures;
		this.model = model;
	}

	/**
	 * Draw and render the graph.
	 */
	public abstract void render();

	/*
	 * UTILS
	 */

	/**
	 * Render and display a frame containing all the char panels, grouped by
	 * dimension
	 */
	protected final void renderCharts( final List< ExportableChartPanel > chartPanels )
	{
		// The Panel
		final JPanel panel = new JPanel();
		final BoxLayout panelLayout = new BoxLayout( panel, BoxLayout.Y_AXIS );
		panel.setLayout( panelLayout );
		for ( final ExportableChartPanel chartPanel : chartPanels )
		{
			panel.add( chartPanel );
			panel.add( Box.createVerticalStrut( 5 ) );
		}

		// Scroll pane
		final JScrollPane scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy( ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER );
		scrollPane.setViewportView( panel );

		// The frame
		final JFrame frame = new JFrame();
		frame.setTitle( "Feature plot for Track scheme" );
		frame.setIconImage( TRACK_SCHEME_ICON.getImage() );
		frame.getContentPane().add( scrollPane );
		frame.validate();
		frame.setSize( new java.awt.Dimension( 520, 320 ) );
		frame.setVisible( true );
	}

	/**
	 * @return the unique mapped values in the given map, for the collection of
	 *         keys given.
	 */
	protected final < K, V > Set< V > getUniqueValues( final Iterable< K > keys, final Map< K, V > map )
	{
		final HashSet< V > mapping = new HashSet<>();
		for ( final K key : keys )
			mapping.add( map.get( key ) );

		return mapping;
	}

	/**
	 * @return the collection of keys amongst the given ones, that point to the
	 *         target value in the given map.
	 * @param targetValue
	 *            the common value to search
	 * @param keys
	 *            the keys to inspect
	 * @param map
	 *            the map to search in
	 */
	protected final < K, V > List< K > getCommonKeys( final V targetValue, final Iterable< K > keys, final Map< K, V > map )
	{
		final ArrayList< K > foundKeys = new ArrayList<>();
		for ( final K key : keys )
		{
			if ( map.get( key ).equals( targetValue ) )
				foundKeys.add( key );

		}
		return foundKeys;
	}

	/**
	 * @return a suitable plot title built from the given target features
	 */

	protected final String buildPlotTitle( final Iterable< String > lYFeatures, final Map< String, String > featureNames )
	{
		final StringBuilder sb = new StringBuilder( "Plot of " );
		final Iterator< String > it = lYFeatures.iterator();
		sb.append( featureNames.get( it.next() ) );
		while ( it.hasNext() )
		{
			sb.append( ", " );
			sb.append( featureNames.get( it.next() ) );
		}
		sb.append( " vs " );
		sb.append( featureNames.get( xFeature ) );
		sb.append( "." );
		return sb.toString();
	}

	/**
	 * @return the list of links that have their source and target in the given
	 *         BCellobject list.
	 */
	protected final List< DefaultWeightedEdge > getInsideEdges( final Collection< BCellobject > BCellobjects )
	{
		final int nBCellobjects = BCellobjects.size();
		final ArrayList< DefaultWeightedEdge > edges = new ArrayList<>( nBCellobjects );
		final TrackModel trackModel = model.getTrackModel();
		for ( final DefaultWeightedEdge edge : trackModel.edgeSet() )
		{
			final BCellobject source = trackModel.getEdgeSource( edge );
			final BCellobject target = trackModel.getEdgeTarget( edge );
			if ( BCellobjects.contains( source ) && BCellobjects.contains( target ) )
				edges.add( edge );

		}
		return edges;
	}

}

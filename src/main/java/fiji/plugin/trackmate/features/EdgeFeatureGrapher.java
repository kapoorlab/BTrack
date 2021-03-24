package Buddy.plugin.trackmate.features;

import static Buddy.plugin.trackmate.gui.TrackMateWizard.FONT;
import static Buddy.plugin.trackmate.gui.TrackMateWizard.SMALL_FONT;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jgrapht.graph.DefaultWeightedEdge;

import Buddy.plugin.trackmate.Dimension;
import Buddy.plugin.trackmate.FeatureModel;
import Buddy.plugin.trackmate.Model;
import Buddy.plugin.trackmate.util.ExportableChartPanel;
import Buddy.plugin.trackmate.util.TMUtils;
import Buddy.plugin.trackmate.util.XYEdgeRenderer;
import Buddy.plugin.trackmate.util.XYEdgeSeries;
import Buddy.plugin.trackmate.util.XYEdgeSeriesCollection;

public class EdgeFeatureGrapher extends AbstractFeatureGrapher
{

	private final List< DefaultWeightedEdge > edges;

	private final Dimension xDimension;

	private final Map< String, Dimension > yDimensions;

	private final Map< String, String > featureNames;

	public EdgeFeatureGrapher( final String xFeature, final Set< String > yFeatures, final List< DefaultWeightedEdge > edges, final Model model )
	{
		super( xFeature, yFeatures, model );
		this.edges = edges;
		this.xDimension = model.getFeatureModel().getEdgeFeatureDimensions().get( xFeature );
		this.yDimensions = model.getFeatureModel().getEdgeFeatureDimensions();
		this.featureNames = model.getFeatureModel().getEdgeFeatureNames();
	}

	@Override
	public void render()
	{

		// Check x units
		final String xdim = TMUtils.getUnitsFor( xDimension, model.getSpaceUnits(), model.getTimeUnits() );
		if ( null == xdim )
		{ // not a number feature
			return;
		}

		// X label
		final String xAxisLabel = xFeature + " (" + xdim + ")";

		// Find how many different dimensions
		final Set< Dimension > dimensions = getUniqueValues( yFeatures, yDimensions );

		// Generate one panel per different dimension
		final ArrayList< ExportableChartPanel > chartPanels = new ArrayList<>( dimensions.size() );
		for ( final Dimension dimension : dimensions )
		{

			// Y label
			final String yAxisLabel = TMUtils.getUnitsFor( dimension, model.getSpaceUnits(), model.getTimeUnits() );

			// Check y units
			if ( null == yAxisLabel )
			{ // not a number feature
				continue;
			}

			// Collect suitable feature for this dimension
			final List< String > featuresThisDimension = getCommonKeys( dimension, yFeatures, yDimensions );

			// Title
			final String title = buildPlotTitle( featuresThisDimension, featureNames );

			// Data-set for points (easy)
			final XYSeriesCollection pointDataset = buildEdgeDataSet( featuresThisDimension, edges );

			// Point renderer
			final XYLineAndShapeRenderer pointRenderer = new XYLineAndShapeRenderer();

			// Edge renderer
			final XYEdgeRenderer edgeRenderer = new XYEdgeRenderer();

			// Data-set for edges
			final XYEdgeSeriesCollection edgeDataset = buildConnectionDataSet( featuresThisDimension, edges );

			// The chart
			final JFreeChart chart = ChartFactory.createXYLineChart( title, xAxisLabel, yAxisLabel, pointDataset, PlotOrientation.VERTICAL, true, true, false );
			chart.getTitle().setFont( FONT );
			chart.getLegend().setItemFont( SMALL_FONT );

			// The plot
			final XYPlot plot = chart.getXYPlot();
			plot.setDataset( 1, edgeDataset );
			plot.setRenderer( 1, edgeRenderer );
			plot.setRenderer( 0, pointRenderer );
			plot.getRangeAxis().setLabelFont( FONT );
			plot.getRangeAxis().setTickLabelFont( SMALL_FONT );
			plot.getDomainAxis().setLabelFont( FONT );
			plot.getDomainAxis().setTickLabelFont( SMALL_FONT );

			// Paint
			pointRenderer.setUseOutlinePaint( true );
			final int nseries = edgeDataset.getSeriesCount();
			for ( int i = 0; i < nseries; i++ )
			{
				pointRenderer.setSeriesOutlinePaint( i, Color.black );
				pointRenderer.setSeriesLinesVisible( i, false );
				pointRenderer.setSeriesShape( i, DEFAULT_SHAPE, false );
				pointRenderer.setSeriesPaint( i, paints.getPaint( ( double ) i / nseries ), false );
				edgeRenderer.setSeriesPaint( i, paints.getPaint( ( double ) i / nseries ), false );
			}

			// The panel
			final ExportableChartPanel chartPanel = new ExportableChartPanel( chart );
			chartPanel.setPreferredSize( new java.awt.Dimension( 500, 270 ) );
			chartPanels.add( chartPanel );
		}

		renderCharts( chartPanels );
	}

	private XYEdgeSeriesCollection buildConnectionDataSet( final List< String > targetYFeatures, final List< DefaultWeightedEdge > lEdges )
	{
		final XYEdgeSeriesCollection edgeDataset = new XYEdgeSeriesCollection();
		// First create series per y features. At this stage, we assume that
		// they are all numeric
		for ( final String yFeature : targetYFeatures )
		{
			final XYEdgeSeries edgeSeries = new XYEdgeSeries( featureNames.get( yFeature ) );
			edgeDataset.addSeries( edgeSeries );
		}

		// Build dataset. We look for edges that have a spot in common, one for
		// the target one for the source
		final FeatureModel fm = model.getFeatureModel();
		for ( final DefaultWeightedEdge edge0 : lEdges )
		{
			for ( final DefaultWeightedEdge edge1 : lEdges )
			{

				if ( model.getTrackModel().getEdgeSource( edge0 ).equals( model.getTrackModel().getEdgeTarget( edge1 ) ) )
				{
					for ( final String yFeature : targetYFeatures )
					{
						final XYEdgeSeries edgeSeries = edgeDataset.getSeries( featureNames.get( yFeature ) );
						final Number x0 = fm.getEdgeFeature( edge0, xFeature );
						final Number y0 = fm.getEdgeFeature( edge0, yFeature );
						final Number x1 = fm.getEdgeFeature( edge1, xFeature );
						final Number y1 = fm.getEdgeFeature( edge1, yFeature );
						
						// Some feature values might be null.
						if (null == x0 || null == y0 || null == x1 || null == y1)
							continue;
						
						edgeSeries.addEdge( x0.doubleValue(), y0.doubleValue(), x1.doubleValue(), y1.doubleValue() );
					}
				}
			}
		}
		return edgeDataset;
	}

	/**
	 * @return a new dataset that contains the values, specified from the given
	 *         feature, and extracted from all the given edges.
	 */
	private XYSeriesCollection buildEdgeDataSet( final Iterable< String > targetYFeatures, final Iterable< DefaultWeightedEdge > lEdges )
	{
		final XYSeriesCollection dataset = new XYSeriesCollection();
		final FeatureModel fm = model.getFeatureModel();
		for ( final String feature : targetYFeatures )
		{
			final XYSeries series = new XYSeries( featureNames.get( feature ) );
			for ( final DefaultWeightedEdge edge : lEdges )
			{
				final Number x = fm.getEdgeFeature( edge, xFeature );
				final Number y = fm.getEdgeFeature( edge, feature );
				if ( null == x || null == y )
				{
					continue;
				}
				series.add( x.doubleValue(), y.doubleValue() );
			}
			dataset.addSeries( series );
		}
		return dataset;
	}
}

package fiji.plugin.trackmate.gui;

import java.util.List;

import fiji.plugin.trackmate.Settings;
import fiji.plugin.trackmate.TrackMate;
import fiji.plugin.trackmate.features.edges.EdgeAnalyzer;
import fiji.plugin.trackmate.features.spot.BCellobjectAnalyzerFactory;
import fiji.plugin.trackmate.features.track.TrackAnalyzer;
import fiji.plugin.trackmate.gui.descriptors.WizardPanelDescriptor;
import pluginTools.InteractiveBud;

public class ManualTrackingGUIController extends TrackMateGUIController
{
	final InteractiveBud parent;
	public ManualTrackingGUIController( final InteractiveBud parent,final TrackMate trackmate )
	{
		
		super( parent, trackmate );
		this.parent = parent;
	}

	@Override
	protected WizardPanelDescriptor getFirstDescriptor()
	{
		return configureViewsDescriptor;
	}

	@Override
	protected WizardPanelDescriptor previousDescriptor( final WizardPanelDescriptor currentDescriptor )
	{
		if ( currentDescriptor == configureViewsDescriptor )
			return null;

		return super.previousDescriptor( currentDescriptor );
	}

	@Override
	protected void createProviders()
	{
		super.createProviders();

		trackmate.getModel().setLogger( logger );

		/*
		 * Immediately declare feature analyzers to settings object
		 */

		final Settings settings = trackmate.getSettings();

		settings.clearBCellobjectAnalyzerFactories();
		final List< String > BCellobjectAnalyzerKeys = BCellobjectAnalyzerProvider.getKeys();
		for ( final String key : BCellobjectAnalyzerKeys )
		{
			final BCellobjectAnalyzerFactory< ? > BCellobjectFeatureAnalyzer = BCellobjectAnalyzerProvider.getFactory( key );
			settings.addBCellobjectAnalyzerFactory( BCellobjectFeatureAnalyzer );
		}

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

		trackmate.getModel().getLogger().log( settings.toStringFeatureAnalyzersInfo() );

		/*
		 * Immediately declare features to model.
		 */

		trackmate.computeBCellobjectFeatures( false );
		trackmate.computeEdgeFeatures( false );
		trackmate.computeTrackFeatures( false );
	}

}

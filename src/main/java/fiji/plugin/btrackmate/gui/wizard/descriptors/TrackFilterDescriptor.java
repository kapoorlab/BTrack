package fiji.plugin.btrackmate.gui.wizard.descriptors;

import java.awt.Container;
import java.util.List;

import javax.swing.JLabel;

import fiji.plugin.btrackmate.Logger;
import fiji.plugin.btrackmate.Model;
import fiji.plugin.btrackmate.TrackMate;
import fiji.plugin.btrackmate.features.FeatureFilter;
import fiji.plugin.btrackmate.features.track.TrackBranchingAnalyzer;
import fiji.plugin.btrackmate.gui.components.FeatureDisplaySelector;
import fiji.plugin.btrackmate.gui.components.FilterGuiPanel;
import fiji.plugin.btrackmate.gui.displaysettings.DisplaySettings.TrackMateObject;
import fiji.plugin.btrackmate.gui.wizard.WizardPanelDescriptor;
import fiji.plugin.btrackmate.util.EverythingDisablerAndReenabler;

public class TrackFilterDescriptor extends WizardPanelDescriptor
{

	private static final String KEY = "TrackFilter";

	private final TrackMate btrackmate;

	public TrackFilterDescriptor(
			final TrackMate btrackmate,
			final List< FeatureFilter > filters,
			final FeatureDisplaySelector featureSelector )
	{
		super( KEY );
		this.btrackmate = btrackmate;
		final FilterGuiPanel component = new FilterGuiPanel(
				btrackmate.getModel(),
				btrackmate.getSettings(),
				TrackMateObject.TRACKS,
				filters,
				TrackBranchingAnalyzer.NUMBER_SPOTS,
				featureSelector );

		component.addChangeListener( e -> filterTracks() );
		this.targetPanel = component;
	}

	private void filterTracks()
	{
		final FilterGuiPanel component = ( FilterGuiPanel ) targetPanel;
		btrackmate.getSettings().setTrackFilters( component.getFeatureFilters() );
		btrackmate.execTrackFiltering( false );
	}

	@Override
	public Runnable getForwardRunnable()
	{
		return new Runnable()
		{
			@Override
			public void run()
			{
				final EverythingDisablerAndReenabler disabler = new EverythingDisablerAndReenabler( ( Container ) targetPanel, new Class[] { JLabel.class } );
				disabler.disable();
				try
				{
					final Model model = btrackmate.getModel();
					final Logger logger = model.getLogger();

					/*
					 * Show and log to progress bar in the filter GUI panel.
					 */

					final FilterGuiPanel panel = ( FilterGuiPanel ) targetPanel;
					panel.showProgressBar( true );

					/*
					 * We have some tracks so we need to compute spot features
					 * will we render them.
					 */
					logger.log( "\n" );
					// Calculate features
					final long start = System.currentTimeMillis();
					final Logger oldLogger = btrackmate.getModel().getLogger();
					btrackmate.getModel().setLogger( panel.getLogger() );
					btrackmate.computeEdgeFeatures( true );
					btrackmate.computeTrackFeatures( true );
					final long end = System.currentTimeMillis();
					btrackmate.getModel().setLogger( oldLogger );
					if ( btrackmate.isCanceled() )
						logger.log( "Spot feature calculation canceled.\nSome spots will have missing feature values.\n" );
					logger.log( String.format( "Calculating features done in %.1f s.\n", ( end - start ) / 1e3f ) );
					panel.showProgressBar( false );

					// Refresh component.
					panel.refreshValues();
					filterTracks();
				}
				finally
				{
					disabler.reenable();
				}
			}
		};
	}

	@Override
	public void displayingPanel()
	{
		filterTracks();
	}

	@Override
	public void aboutToHidePanel()
	{
		final Logger logger = btrackmate.getModel().getLogger();
		logger.log( "\nPerforming track filtering on the following features:\n", Logger.BLUE_COLOR );
		final Model model = btrackmate.getModel();
		final FilterGuiPanel component = ( FilterGuiPanel ) targetPanel;
		final List< FeatureFilter > featureFilters = component.getFeatureFilters();
		btrackmate.getSettings().setTrackFilters( featureFilters );
		btrackmate.execTrackFiltering( false );

		final int ntotal = model.getTrackModel().nTracks( false );
		if ( featureFilters == null || featureFilters.isEmpty() )
		{
			logger.log( "No feature threshold set, kept the " + ntotal + " tracks.\n" );
		}
		else
		{
			for ( final FeatureFilter ft : featureFilters )
			{
				String str = "  - on " + btrackmate.getModel().getFeatureModel().getTrackFeatureNames().get( ft.feature );
				if ( ft.isAbove )
					str += " above ";
				else
					str += " below ";
				str += String.format( "%.1f", ft.value );
				str += '\n';
				logger.log( str );
			}
			final int nselected = model.getTrackModel().nTracks( true );
			logger.log( "Kept " + nselected + " spots out of " + ntotal + ".\n" );
		}
	}
}

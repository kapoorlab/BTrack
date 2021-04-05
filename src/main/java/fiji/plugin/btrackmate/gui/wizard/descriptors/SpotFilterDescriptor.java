package fiji.plugin.btrackmate.gui.wizard.descriptors;

import java.awt.Container;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JLabel;

import org.scijava.Cancelable;

import fiji.plugin.btrackmate.Logger;
import fiji.plugin.btrackmate.Model;
import fiji.plugin.btrackmate.Settings;
import fiji.plugin.btrackmate.Spot;
import fiji.plugin.btrackmate.TrackMate;
import fiji.plugin.btrackmate.detection.DetectionUtils;
import fiji.plugin.btrackmate.features.FeatureFilter;
import fiji.plugin.btrackmate.features.spot.SpotMorphologyAnalyzerFactory;
import fiji.plugin.btrackmate.gui.components.FeatureDisplaySelector;
import fiji.plugin.btrackmate.gui.components.FilterGuiPanel;
import fiji.plugin.btrackmate.gui.displaysettings.DisplaySettings.TrackMateObject;
import fiji.plugin.btrackmate.gui.wizard.WizardPanelDescriptor;
import fiji.plugin.btrackmate.providers.SpotMorphologyAnalyzerProvider;
import fiji.plugin.btrackmate.util.EverythingDisablerAndReenabler;

public class SpotFilterDescriptor extends WizardPanelDescriptor
{

	private static final String KEY = "SpotFilter";

	private final TrackMate btrackmate;

	public SpotFilterDescriptor(
			final TrackMate btrackmate,
			final List< FeatureFilter > filters,
			final FeatureDisplaySelector featureSelector )
	{
		super( KEY );
		this.btrackmate = btrackmate;
		final FilterGuiPanel component = new FilterGuiPanel(
				btrackmate.getModel(),
				btrackmate.getSettings(),
				TrackMateObject.SPOTS,
				filters,
				Spot.QUALITY,
				featureSelector );

		component.addChangeListener( e -> filterSpots() );
		this.targetPanel = component;
	}

	private void filterSpots()
	{
		final FilterGuiPanel component = ( FilterGuiPanel ) targetPanel;
		btrackmate.getSettings().setSpotFilters( component.getFeatureFilters() );
		btrackmate.execSpotFiltering( false );
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
					final String str = "Initial thresholding with a quality threshold above "
							+ String.format( "%.1f", btrackmate.getSettings().initialSpotFilterValue )
							+ " ...\n";
					logger.log( str, Logger.BLUE_COLOR );
					final int ntotal = model.getSpots().getNSpots( false );
					btrackmate.execInitialSpotFiltering();
					final int nselected = model.getSpots().getNSpots( false );
					logger.log( String.format( "Retained %d spots out of %d.\n", nselected, ntotal ) );

					/*
					 * Should we add morphology feature analyzers?
					 */

					if ( btrackmate.getSettings().detectorFactory != null
							&& btrackmate.getSettings().detectorFactory.has2Dsegmentation()
							&& DetectionUtils.is2D( btrackmate.getSettings().imp ) )
					{
						logger.log( "\nAdding morphology analyzers...\n", Logger.BLUE_COLOR );
						final Settings settings = btrackmate.getSettings();
						final SpotMorphologyAnalyzerProvider spotMorphologyAnalyzerProvider = new SpotMorphologyAnalyzerProvider( settings.imp.getNChannels() );
						@SuppressWarnings( "rawtypes" )
						final List< SpotMorphologyAnalyzerFactory > factories = spotMorphologyAnalyzerProvider
								.getKeys()
								.stream()
								.map( key -> spotMorphologyAnalyzerProvider.getFactory( key ) )
								.collect( Collectors.toList() );
						factories.forEach( settings::addSpotAnalyzerFactory );
						final StringBuilder strb = new StringBuilder();
						Settings.prettyPrintFeatureAnalyzer( factories, strb );
						logger.log( strb.toString() );
					}

					/*
					 * Show and log to progress bar in the filter GUI panel.
					 */

					final FilterGuiPanel panel = ( FilterGuiPanel ) targetPanel;
					panel.showProgressBar( true );

					/*
					 * We have some spots so we need to compute spot features
					 * will we render them.
					 */
					logger.log( "\nCalculating spot features...\n", Logger.BLUE_COLOR );
					// Calculate features
					final long start = System.currentTimeMillis();

					final Logger oldLogger = btrackmate.getModel().getLogger();
					btrackmate.getModel().setLogger( panel.getLogger() );
					btrackmate.computeSpotFeatures( true );
					final long end = System.currentTimeMillis();
					btrackmate.getModel().setLogger( oldLogger );
					if ( btrackmate.isCanceled() )
						logger.log( "Spot feature calculation canceled.\nSome spots will have missing feature values.\n" );
					logger.log( String.format( "Calculating features done in %.1f s.\n", ( end - start ) / 1e3f ) );
					panel.showProgressBar( false );

					// Refresh component.
					panel.refreshValues();
					filterSpots();
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
		final FilterGuiPanel component = ( FilterGuiPanel ) targetPanel;
		btrackmate.getSettings().setSpotFilters( component.getFeatureFilters() );
		btrackmate.execSpotFiltering( false );
	}

	@Override
	public void aboutToHidePanel()
	{
		final Logger logger = btrackmate.getModel().getLogger();
		logger.log( "\nPerforming spot filtering on the following features:\n", Logger.BLUE_COLOR );
		final Model model = btrackmate.getModel();
		final FilterGuiPanel component = ( FilterGuiPanel ) targetPanel;
		final List< FeatureFilter > featureFilters = component.getFeatureFilters();
		btrackmate.getSettings().setSpotFilters( featureFilters );
		btrackmate.execSpotFiltering( false );

		final int ntotal = model.getSpots().getNSpots( false );
		if ( featureFilters == null || featureFilters.isEmpty() )
		{
			logger.log( "No feature threshold set, kept the " + ntotal + " spots.\n" );
		}
		else
		{
			for ( final FeatureFilter ft : featureFilters )
			{
				String str = "  - on " + btrackmate.getModel().getFeatureModel().getSpotFeatureNames().get( ft.feature );
				if ( ft.isAbove )
					str += " above ";
				else
					str += " below ";
				str += String.format( "%.1f", ft.value );
				str += '\n';
				logger.log( str );
			}
			final int nselected = model.getSpots().getNSpots( true );
			logger.log( "Kept " + nselected + " spots out of " + ntotal + ".\n" );
		}
	}

	@Override
	public Cancelable getCancelable()
	{
		return btrackmate;
	}
}

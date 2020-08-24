package Buddy.plugin.trackmate.gui.descriptors;

import Buddy.plugin.trackmate.Logger;
import Buddy.plugin.trackmate.Model;
import Buddy.plugin.trackmate.SelectionModel;
import Buddy.plugin.trackmate.TrackMate;
import Buddy.plugin.trackmate.gui.TrackMateGUIController;
import Buddy.plugin.trackmate.gui.TrackMateGUIModel;
import Buddy.plugin.trackmate.gui.panels.ListChooserPanel;
import Buddy.plugin.trackmate.providers.ViewProvider;
import Buddy.plugin.trackmate.visualization.TrackMateModelView;
import Buddy.plugin.trackmate.visualization.ViewFactory;
import Buddy.plugin.trackmate.visualization.hyperstack.HyperStackDisplayer;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;

public class ViewChoiceDescriptor implements WizardPanelDescriptor
{

	private static final String KEY = "ChooseView";

	private final ListChooserPanel component;

	private final ViewProvider viewProvider;

	private final TrackMateGUIModel guimodel;

	private final TrackMateGUIController controller;

	public ViewChoiceDescriptor( final ViewProvider viewProvider, final TrackMateGUIModel guimodel, final TrackMateGUIController controller )
	{
		this.viewProvider = viewProvider;
		this.guimodel = guimodel;
		this.controller = controller;
		// Only views that are set to be visible in the menu.
		final List< String > visibleKeys = viewProvider.getVisibleViews();
		final List< String > viewerNames = new ArrayList< >( visibleKeys.size() );
		final List< String > infoTexts = new ArrayList< >( visibleKeys.size() );
		for ( final String key : visibleKeys )
		{
			infoTexts.add( viewProvider.getFactory( key ).getInfoText() );
			viewerNames.add( viewProvider.getFactory( key ).getName() );
		}
		this.component = new ListChooserPanel( viewerNames, infoTexts, "view" );
	}

	/*
	 * METHODS
	 */

	@Override
	public Component getComponent()
	{
		return component;
	}

	@Override
	public void aboutToDisplayPanel()
	{}

	@Override
	public void displayingPanel()
	{
		final String oldText = controller.getGUI().getNextButton().getText();
		final Icon oldIcon = controller.getGUI().getNextButton().getIcon();
		controller.getGUI().getNextButton().setText( "Please wait..." );
		controller.getGUI().getNextButton().setIcon( null );
		new Thread( "TrackMate BCellobject feature calculation thread." )
		{
			@Override
			public void run()
			{
				final TrackMate trackmate = controller.getPlugin();
				final Model model = trackmate.getModel();
				final Logger logger = model.getLogger();
				final String str = "Initial thresholding with a quality threshold above " + String.format( "%.1f", trackmate.getSettings().initialBCellobjectFilterValue ) + " ...\n";
				logger.log( str, Logger.BLUE_COLOR );
				final int ntotal = model.getBCellobjects().getNBCellobjects(  );
				trackmate.execInitialBCellobjectFiltering();
				final int nselected = model.getBCellobjects().getNBCellobjects(  );
				logger.log( String.format( "Retained %d BCellobjects out of %d.\n", nselected, ntotal ) );

				/*
				 * We have some BCellobjects so we need to compute BCellobject features will
				 * we render them.
				 */
				logger.log( "Calculating BCellobject features...\n", Logger.BLUE_COLOR );
				// Calculate features
				final long start = System.currentTimeMillis();
				trackmate.computeBCellobjectFeatures( true );
				final long end = System.currentTimeMillis();
				logger.log( String.format( "Calculating features done in %.1f s.\n", ( end - start ) / 1e3f ), Logger.BLUE_COLOR );
				controller.getGUI().getNextButton().setText( oldText );
				controller.getGUI().getNextButton().setIcon( oldIcon );
				controller.getGUI().setNextButtonEnabled( true );
			}
		}.start();
	}

	@Override
	public void aboutToHidePanel()
	{
		final int index = component.getChoice();
		final TrackMate trackmate = controller.getPlugin();
		final SelectionModel selectionModel = controller.getSelectionModel();
		new Thread( "TrackMate view rendering thread" )
		{
			@Override
			public void run()
			{
				final String viewName = viewProvider.getVisibleViews().get( index );

				// The HyperStack view is already used in the GUI, no need to
				// re-instantiate it.
				if ( viewName.equals( HyperStackDisplayer.KEY ) ) { return; }

				final ViewFactory factory = viewProvider.getFactory( viewName );
				final TrackMateModelView view = factory.create( trackmate.getParent(), trackmate.getModel(), trackmate.getSettings(), selectionModel );
				for ( final String settingKey : guimodel.getDisplaySettings().keySet() )
				{
					view.setDisplaySettings( settingKey, guimodel.getDisplaySettings().get( settingKey ) );
				}
				guimodel.addView( view );
				view.render();
			}
		}.start();
	}

	@Override
	public void comingBackToPanel()
	{}

	@Override
	public String getKey()
	{
		return KEY;
	}
}

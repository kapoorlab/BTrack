package Buddy.plugin.trackmate.gui.descriptors;

import Buddy.plugin.trackmate.Logger;
import Buddy.plugin.trackmate.Model;
import Buddy.plugin.trackmate.Settings;
import Buddy.plugin.trackmate.TrackMate;
import Buddy.plugin.trackmate.features.FeatureFilter;
import Buddy.plugin.trackmate.gui.TrackMateGUIController;
import Buddy.plugin.trackmate.gui.panels.components.ColorByFeatureGUIPanel.Category;
import Buddy.plugin.trackmate.gui.panels.components.FilterGuiPanel;
import Buddy.plugin.trackmate.visualization.FeatureColorGenerator;
import budDetector.BCellobject;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class BCellobjectFilterDescriptor implements WizardPanelDescriptor
{

	private final ArrayList< ActionListener > actionListeners = new ArrayList< >();

	private final ArrayList< ChangeListener > changeListeners = new ArrayList< >();

	private static final String KEY = "BCellobjectFilter";

	private FilterGuiPanel component;

	private final TrackMate trackmate;

	private final FeatureColorGenerator< BCellobject > BCellobjectColorGenerator;

	private final TrackMateGUIController controller;

	public BCellobjectFilterDescriptor( final TrackMate trackmate, final FeatureColorGenerator< BCellobject > BCellobjectColorGenerator, final TrackMateGUIController controller )
	{
		this.trackmate = trackmate;
		this.BCellobjectColorGenerator = BCellobjectColorGenerator;
		this.controller = controller;
	}

	@Override
	public FilterGuiPanel getComponent()
	{
		return component;
	}

	@Override
	public void aboutToDisplayPanel()
	{
		component = new FilterGuiPanel( trackmate.getModel(), Arrays.asList( new Category[] { Category.BCellobjectS, Category.DEFAULT } ) );
		component.refreshDisplayedFeatureValues();
		final Settings settings = trackmate.getSettings();
		component.setFilters( settings.getBCellobjectFilters() );
		component.setColorFeature( BCellobjectColorGenerator.getFeature() );
		component.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed( final ActionEvent event )
			{
				fireAction( event );
			}
		} );
		component.addChangeListener( new ChangeListener()
		{
			@Override
			public void stateChanged( final ChangeEvent event )
			{
				fireThresholdChanged( event );
			}
		} );
		controller.getGUI().setNextButtonEnabled( true );
	}

	@Override
	public void displayingPanel()
	{
		if ( null == component )
		{
			// This happens when we load data: the component gets initialized
			// only in another method
			aboutToDisplayPanel();
		}
		trackmate.getSettings().setBCellobjectFilters( component.getFeatureFilters() );
		trackmate.execBCellobjectFiltering( false );
	}

	@Override
	public void aboutToHidePanel()
	{
		final Logger logger = trackmate.getModel().getLogger();
		logger.log( "Performing BCellobject filtering on the following features:\n", Logger.BLUE_COLOR );
		final Model model = trackmate.getModel();
		final List< FeatureFilter > featureFilters = component.getFeatureFilters();
		trackmate.getSettings().setBCellobjectFilters( featureFilters );
		trackmate.execBCellobjectFiltering( false );

		final int ntotal = model.getBCellobjects().getNBCellobjects(  );
		if ( featureFilters == null || featureFilters.isEmpty() )
		{
			logger.log( "No feature threshold set, kept the " + ntotal + " BCellobjects.\n" );
		}
		else
		{
			for ( final FeatureFilter ft : featureFilters )
			{
				String str = "  - on " + trackmate.getModel().getFeatureModel().getBCellobjectFeatureNames().get( ft.feature );
				if ( ft.isAbove )
					str += " above ";
				else
					str += " below ";
				str += String.format( "%.1f", ft.value );
				str += '\n';
				logger.log( str );
			}
			final int nselected = model.getBCellobjects().getNBCellobjects(  );
			logger.log( "Kept " + nselected + " BCellobjects out of " + ntotal + ".\n" );
		}
	}

	@Override
	public void comingBackToPanel()
	{}

	@Override
	public String getKey()
	{
		return KEY;
	}

	/**
	 * Adds an {@link ActionListener} to this panel. These listeners will be
	 * notified when a button is pushed or when the feature to color is changed.
	 */
	public void addActionListener( final ActionListener listener )
	{
		actionListeners.add( listener );
	}

	/**
	 * Removes an ActionListener from this panel.
	 *
	 * @return true if the listener was in the ActionListener collection of this
	 *         instance.
	 */
	public boolean removeActionListener( final ActionListener listener )
	{
		return actionListeners.remove( listener );
	}

	public Collection< ActionListener > getActionListeners()
	{
		return actionListeners;
	}

	/**
	 * Forwards the given {@link ActionEvent} to all the {@link ActionListener}
	 * of this panel.
	 */
	private void fireAction( final ActionEvent e )
	{
		for ( final ActionListener l : actionListeners )
			l.actionPerformed( e );
	}

	/**
	 * Add an {@link ChangeListener} to this panel. The {@link ChangeListener}
	 * will be notified when a change happens to the thresholds displayed by
	 * this panel, whether due to the slider being move, the auto-threshold
	 * button being pressed, or the combo-box selection being changed.
	 */
	public void addChangeListener( final ChangeListener listener )
	{
		changeListeners.add( listener );
	}

	/**
	 * Remove a ChangeListener from this panel.
	 *
	 * @return true if the listener was in listener collection of this instance.
	 */
	public boolean removeChangeListener( final ChangeListener listener )
	{
		return changeListeners.remove( listener );
	}

	public Collection< ChangeListener > getChangeListeners()
	{
		return changeListeners;
	}

	private void fireThresholdChanged( final ChangeEvent e )
	{
		for ( final ChangeListener cl : changeListeners )
		{
			cl.stateChanged( e );
		}
	}
}

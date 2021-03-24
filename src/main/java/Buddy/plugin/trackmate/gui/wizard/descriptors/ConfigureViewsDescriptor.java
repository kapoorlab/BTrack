package Buddy.plugin.trackmate.gui.wizard.descriptors;

import javax.swing.Action;

import Buddy.plugin.trackmate.gui.components.ConfigureViewsPanel;
import Buddy.plugin.trackmate.gui.components.FeatureDisplaySelector;
import Buddy.plugin.trackmate.gui.displaysettings.DisplaySettings;
import Buddy.plugin.trackmate.gui.wizard.WizardPanelDescriptor;

public class ConfigureViewsDescriptor extends WizardPanelDescriptor
{

	public static final String KEY = "ConfigureViews";

	public ConfigureViewsDescriptor(
			final DisplaySettings ds,
			final FeatureDisplaySelector featureSelector,
			final Action launchTrackSchemeAction,
			final Action showTrackTablesAction,
			final Action showSpotTableAction,
			final String spaceUnits )
	{
		super( KEY );
		this.targetPanel = new ConfigureViewsPanel(
				ds, 
				featureSelector, 
				spaceUnits,
				launchTrackSchemeAction,
				showTrackTablesAction,
				showSpotTableAction );
	}
}

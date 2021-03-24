package Buddy.plugin.trackmate.gui.wizard.descriptors;

import java.io.File;

import Buddy.plugin.trackmate.gui.components.LogPanel;
import Buddy.plugin.trackmate.gui.wizard.WizardPanelDescriptor;

/**
 * An abstract class made for describing panels that generate a dialog, like
 * save and load panels.
 *
 * @author Jean-Yves Tinevez
 *
 */
public abstract class SomeDialogDescriptor extends WizardPanelDescriptor
{

	/**
	 * File that governs saving and loading. We make it a static field so that
	 * loading and sharing events always point to a single file location by
	 * default.
	 */
	public static File file;

	public SomeDialogDescriptor( final String panelIdentifier, final LogPanel logPanel )
	{
		super( panelIdentifier );
		this.targetPanel = logPanel;
	}
}

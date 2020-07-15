package Buddy.plugin.trackmate.gui.descriptors;

import Buddy.plugin.trackmate.gui.LogPanel;
import pluginTools.InteractiveBud;
import pluginTools.InteractiveGreen;

import java.awt.Component;
import java.io.File;

/**
 * An abstract class made for describing panels that generate a dialog, like
 * save and load panels.
 *
 * @author Jean-Yves Tinevez
 *
 */
public abstract class GreenSomeDialogDescriptor implements GreenWizardPanelDescriptor {

	/**
	 * File that governs saving and loading. We make it a static field so that
	 * loading and sharing events always point to a single file location by default.
	 */
	public static File file;

	protected final LogPanel logPanel;

	public GreenSomeDialogDescriptor(final LogPanel logPanel) {
		this.logPanel = logPanel;
	}

	@Override
	public Component getComponent() {
		return logPanel;
	}

	@Override
	public void aboutToDisplayPanel() {
	}

	@Override
	public abstract void displayingPanel(InteractiveGreen parent);
	

	@Override
	public void aboutToHidePanel() {
	}

	@Override
	public void comingBackToPanel() {
	}
}

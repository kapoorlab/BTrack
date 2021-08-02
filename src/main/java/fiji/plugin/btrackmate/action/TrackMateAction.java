package fiji.plugin.btrackmate.action;

import java.awt.Frame;

import fiji.plugin.btrackmate.Logger;
import fiji.plugin.btrackmate.SelectionModel;
import fiji.plugin.btrackmate.TrackMate;
import fiji.plugin.btrackmate.gui.displaysettings.DisplaySettings;

/**
 * This interface describe a track mate action, that can be run on a
 * {@link TrackMate} object to change its content or properties.
 *
 * @author Jean-Yves Tinevez, 2011-2013 revised in 2021
 */
public interface TrackMateAction {

	/**
	 * Executes this action within an application specified by the parameters.
	 *
	 * @param btrackmate      the {@link TrackMate} instance to use to execute the
	 *                        action.
	 * @param selectionModel  the {@link SelectionModel} currently used in the
	 *                        application,
	 * @param displaySettings the {@link DisplaySettings} used to render the views
	 *                        in the application.
	 * @param parent          the user-interface parent window.
	 */
	public void execute(TrackMate btrackmate, SelectionModel selectionModel, DisplaySettings displaySettings,
			Frame parent);

	/**
	 * Sets the logger that will receive logs when this action is executed.
	 */
	public void setLogger(Logger logger);
}

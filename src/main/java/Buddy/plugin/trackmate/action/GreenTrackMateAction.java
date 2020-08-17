package Buddy.plugin.trackmate.action;

import Buddy.plugin.trackmate.GreenTrackMate;
import Buddy.plugin.trackmate.Logger;

/**
 * This interface describe a track mate action, that can be run on a
 * {@link TrackMate} object to change its content or properties.
 *
 * @author Jean-Yves Tinevez &lt;jeanyves.tinevez@gmail.com&gt; 2011-2013
 */
public interface GreenTrackMateAction {

	/**
	 * Executes this action using the specified trackmate instance.
	 *
	 * @param trackmate
	 *            the {@link TrackMate} instance to use to execute the action.
	 */
	public void execute(GreenTrackMate trackmate);

	/**
	 * Sets the logger that will receive logs when this action is executed.
	 */
	public void setLogger(Logger logger);
}

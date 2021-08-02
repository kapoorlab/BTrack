package fiji.plugin.btrackmate.visualization;

import fiji.plugin.btrackmate.Model;
import fiji.plugin.btrackmate.SelectionModel;
import fiji.plugin.btrackmate.Spot;
import fiji.plugin.btrackmate.gui.displaysettings.DisplaySettings;
import ij.ImagePlus;

public interface TrackMateModelView {

	/*
	 * INTERFACE METHODS
	 */

	/**
	 * Initializes this displayer and render it according to its concrete
	 * implementation.
	 */
	public void render();

	/**
	 * Refreshes the displayer display with current model. If the underlying model
	 * was modified, or the display settings were changed, calling this method
	 * should be enough to update the display with changes.
	 *
	 * @see #setDisplaySettings(String, Object)
	 */
	public void refresh();

	/**
	 * Removes any overlay (for spots or tracks) from this displayer.
	 */
	public void clear();

	/**
	 * Centers the view on the given spot.
	 */
	public void centerViewOn(final Spot spot);

	/**
	 * Returns the model displayed in this view.
	 */
	public Model getModel();

	/**
	 * Returns the unique key that identifies this view.
	 * <p>
	 * Careful: this key <b>must</b> be the same that for the {@link ViewFactory}
	 * that can instantiates this view. This is to facilitate saving/loading.
	 *
	 * @return the key, as a String.
	 */
	public String getKey();

	public void resetDisplaySettings(DisplaySettings displaySettings);

	public void resetSelectionModel(SelectionModel selectionModel);

	public void resetModel(Model model);

}

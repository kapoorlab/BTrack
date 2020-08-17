package Buddy.plugin.trackmate.gui.descriptors;

import Buddy.plugin.trackmate.GreenModel;
import Buddy.plugin.trackmate.GreenTrackMate;
import Buddy.plugin.trackmate.Logger;
import Buddy.plugin.trackmate.features.FeatureFilter;
import Buddy.plugin.trackmate.features.track.TrackIndexAnalyzer;
import Buddy.plugin.trackmate.gui.GreenTrackMateGUIController;
import Buddy.plugin.trackmate.gui.panels.components.GreenColorByFeatureGUIPanel.Category;
import Buddy.plugin.trackmate.gui.panels.components.GreenFilterGuiPanel;
import Buddy.plugin.trackmate.visualization.GreenPerTrackFeatureColorGenerator;
import pluginTools.InteractiveGreen;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class GreenTrackFilterDescriptor implements GreenWizardPanelDescriptor {

	private final ArrayList<ChangeListener> changeListeners = new ArrayList<>();

	private final ArrayList<ActionListener> actionListeners = new ArrayList<>();

	private static final String KEY = "FilterTracks";

	private GreenFilterGuiPanel component;

	private final GreenTrackMate trackmate;

	private final GreenPerTrackFeatureColorGenerator trackColorGenerator;

	private final GreenTrackMateGUIController controller;

	public GreenTrackFilterDescriptor(final GreenTrackMate trackmate2, final GreenPerTrackFeatureColorGenerator trackColorGenerator2,
			final GreenTrackMateGUIController controller) {
		this.trackmate = trackmate2;
		this.trackColorGenerator = trackColorGenerator2;
		this.controller = controller;
	}

	@Override
	public GreenFilterGuiPanel getComponent() {
		return component;
	}

	@Override
	public void aboutToDisplayPanel() {
		component = new GreenFilterGuiPanel(trackmate.getGreenModel(),
				Arrays.asList(new Category[] { Category.TRACKS, Category.DEFAULT }));
		component.setColorFeature(TrackIndexAnalyzer.TRACK_INDEX);
		component.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent event) {
				fireAction(event);
			}
		});
		component.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(final ChangeEvent event) {
				fireThresholdChanged(event);
			}
		});
		component.setColorFeature(trackColorGenerator.getFeature());
	}

	@Override
	public void displayingPanel(InteractiveGreen parent) {
		if (null == component) {
			// Happens when loading at this stage.
			aboutToDisplayPanel();
		} else {
			component.setColorFeature(trackColorGenerator.getFeature());
		}
		controller.getGUI().setNextButtonEnabled(true);
	}

	@Override
	public void aboutToHidePanel() {
		final Logger logger = trackmate.getGreenModel().getLogger();
		logger.log("Performing track filtering on the following features:\n", Logger.BLUE_COLOR);
		final List<FeatureFilter> featureFilters = component.getFeatureFilters();
		final GreenModel model = trackmate.getGreenModel();
		trackmate.getGreenSettings().setTrackFilters(featureFilters);
		trackmate.execTrackFiltering(true);

		if (featureFilters == null || featureFilters.isEmpty()) {
			logger.log("No feature threshold set, kept the " + model.getTrackModel().nTracks(false) + " tracks.\n");
		} else {
			for (final FeatureFilter ft : featureFilters) {
				String str = "  - on " + model.getFeatureModel().getTrackFeatureNames().get(ft.feature);
				if (ft.isAbove)
					str += " above ";
				else
					str += " below ";
				str += String.format("%.1f", ft.value);
				str += '\n';
				logger.log(str);
			}
			logger.log("Kept " + model.getTrackModel().nTracks(true) + " tracks out of "
					+ model.getTrackModel().nTracks(false) + ".\n");
		}
		trackmate.computeEdgeFeatures(true);
	}

	@Override
	public void comingBackToPanel() {
	}

	@Override
	public String getKey() {
		return KEY;
	}

	/**
	 * Adds an {@link ActionListener} to this panel. These listeners will be
	 * notified when a button is pushed or when the feature to color is changed.
	 */
	public void addActionListener(final ActionListener listener) {
		actionListeners.add(listener);
	}

	/**
	 * Removes an ActionListener from this panel.
	 *
	 * @return true if the listener was in the ActionListener collection of this
	 *         instance.
	 */
	public boolean removeActionListener(final ActionListener listener) {
		return actionListeners.remove(listener);
	}

	public Collection<ActionListener> getActionListeners() {
		return actionListeners;
	}

	/**
	 * Forwards the given {@link ActionEvent} to all the {@link ActionListener} of
	 * this panel.
	 */
	private void fireAction(final ActionEvent e) {
		for (final ActionListener l : actionListeners)
			l.actionPerformed(e);
	}

	/**
	 * Add an {@link ChangeListener} to this panel. The {@link ChangeListener} will
	 * be notified when a change happens to the thresholds displayed by this panel,
	 * whether due to the slider being move, the auto-threshold button being
	 * pressed, or the combo-box selection being changed.
	 */
	public void addChangeListener(final ChangeListener listener) {
		changeListeners.add(listener);
	}

	/**
	 * Remove a ChangeListener from this panel.
	 *
	 * @return true if the listener was in listener collection of this instance.
	 */
	public boolean removeChangeListener(final ChangeListener listener) {
		return changeListeners.remove(listener);
	}

	public Collection<ChangeListener> getChangeListeners() {
		return changeListeners;
	}

	private void fireThresholdChanged(final ChangeEvent e) {
		for (final ChangeListener cl : changeListeners) {
			cl.stateChanged(e);
		}
	}
}

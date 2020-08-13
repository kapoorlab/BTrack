package Buddy.plugin.trackmate.gui.descriptors;

import Buddy.plugin.trackmate.GreenSelectionModel;
import Buddy.plugin.trackmate.Logger;
import Buddy.plugin.trackmate.Model;
import Buddy.plugin.trackmate.SelectionModel;
import Buddy.plugin.trackmate.TrackMate;
import Buddy.plugin.trackmate.gui.GreenTrackMateGUIController;
import Buddy.plugin.trackmate.gui.GreenTrackMateGUIModel;
import Buddy.plugin.trackmate.gui.TrackMateGUIController;
import Buddy.plugin.trackmate.gui.TrackMateGUIModel;
import Buddy.plugin.trackmate.gui.panels.ListChooserPanel;
import Buddy.plugin.trackmate.providers.GreenViewProvider;
import Buddy.plugin.trackmate.providers.ViewProvider;
import Buddy.plugin.trackmate.visualization.GreenTrackMateModelView;
import Buddy.plugin.trackmate.visualization.GreenViewFactory;
import Buddy.plugin.trackmate.visualization.TrackMateModelView;
import Buddy.plugin.trackmate.visualization.ViewFactory;
import Buddy.plugin.trackmate.visualization.hyperstack.HyperStackDisplayer;
import pluginTools.InteractiveBud;
import pluginTools.InteractiveGreen;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;

public class GreenViewChoiceDescriptor implements GreenWizardPanelDescriptor {

	private static final String KEY = "ChooseView";

	private final ListChooserPanel component;

	private final GreenViewProvider viewProvider;

	private final GreenTrackMateGUIModel guimodel;

	private final GreenTrackMateGUIController controller;

	public GreenViewChoiceDescriptor(final GreenViewProvider viewProvider, final GreenTrackMateGUIModel guimodel,
			final GreenTrackMateGUIController controller) {
		this.viewProvider = viewProvider;
		this.guimodel = guimodel;
		this.controller = controller;
		// Only views that are set to be visible in the menu.
		final List<String> visibleKeys = viewProvider.getVisibleViews();
		final List<String> viewerNames = new ArrayList<>(visibleKeys.size());
		final List<String> infoTexts = new ArrayList<>(visibleKeys.size());
		for (final String key : visibleKeys) {
			infoTexts.add(viewProvider.getFactory(key).getInfoText());
			viewerNames.add(viewProvider.getFactory(key).getName());
		}
		this.component = new ListChooserPanel(viewerNames, infoTexts, "view");
	}

	/*
	 * METHODS
	 */

	@Override
	public Component getComponent() {
		return component;
	}

	@Override
	public void aboutToDisplayPanel() {
	}

	@Override
	public void displayingPanel(InteractiveGreen parent) {
		final String oldText = controller.getGUI().getNextButton().getText();
		final Icon oldIcon = controller.getGUI().getNextButton().getIcon();
		controller.getGUI().getNextButton().setText("Please wait...");
		controller.getGUI().getNextButton().setIcon(null);
		new Thread("TrackMate spot feature calculation thread.") {
			@Override
			public void run() {
				final TrackMate trackmate = controller.getPlugin();
				final Model model = trackmate.getModel();
				final Logger logger = model.getLogger();
				final int ntotal = model.getBCellobjects().getNBCellobjects();
				final int nselected = model.getBCellobjects().getNBCellobjects();
				logger.log(String.format("Retained %d spots out of %d.\n", nselected, ntotal));

				/*
				 * We have some spots so we need to compute spot features will we render them.
				 */
				logger.log("Calculating spot features...\n", Logger.BLUE_COLOR);
				// Calculate features
				final long start = System.currentTimeMillis();
				trackmate.computeBCellobjectFeatures(true);
				final long end = System.currentTimeMillis();
				logger.log(String.format("Calculating features done in %.1f s.\n", (end - start) / 1e3f),
						Logger.BLUE_COLOR);
				controller.getGUI().getNextButton().setText(oldText);
				controller.getGUI().getNextButton().setIcon(oldIcon);
				controller.getGUI().setNextButtonEnabled(true);
			}
		}.start();
	}

	@Override
	public void aboutToHidePanel() {
		final int index = component.getChoice();
		final TrackMate trackmate = controller.getPlugin();
		final GreenSelectionModel selectionModel = controller.getSelectionModel();
		new Thread("TrackMate view rendering thread") {
			@Override
			public void run() {
				final String viewName = viewProvider.getVisibleViews().get(index);

				// The HyperStack view is already used in the GUI, no need to
				// re-instantiate it.
				if (viewName.equals(HyperStackDisplayer.KEY)) {
					return;
				}

				final GreenViewFactory factory = viewProvider.getFactory(viewName);
				final GreenTrackMateModelView view = factory.create(trackmate.getGreenModel(), trackmate.getGreenSettings(),
						selectionModel);
				for (final String settingKey : guimodel.getDisplaySettings().keySet()) {
					view.setDisplaySettings(settingKey, guimodel.getDisplaySettings().get(settingKey));
				}
				guimodel.addView(view);
				view.render();
			}
		}.start();
	}

	@Override
	public void comingBackToPanel() {
	}

	@Override
	public String getKey() {
		return KEY;
	}
}

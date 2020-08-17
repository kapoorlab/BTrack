package Buddy.plugin.trackmate.gui.descriptors;

import Buddy.plugin.trackmate.GreenTrackMate;
import Buddy.plugin.trackmate.Logger;
import Buddy.plugin.trackmate.TrackMate;
import Buddy.plugin.trackmate.gui.GreenTrackMateGUIController;
import Buddy.plugin.trackmate.gui.LogPanel;
import Buddy.plugin.trackmate.gui.TrackMateGUIController;
import Buddy.plugin.trackmate.tracking.GreenobjectTrackerFactory;
import pluginTools.InteractiveBud;
import pluginTools.InteractiveGreen;

import javax.swing.SwingUtilities;

public class GreenTrackingDescriptor implements GreenWizardPanelDescriptor {

	private static final String KEY = "Tracking";

	private final LogPanel logPanel;

	private final GreenTrackMate trackmate;

	private final GreenTrackMateGUIController controller;

	public GreenTrackingDescriptor(final GreenTrackMateGUIController controller) {
		this.controller = controller;
		this.trackmate = controller.getPlugin();
		this.logPanel = controller.getGUI().getLogPanel();
	}

	@Override
	public LogPanel getComponent() {
		return logPanel;
	}

	@Override
	public void aboutToDisplayPanel() {
	}

	@Override
	public void displayingPanel(InteractiveGreen parent) {
		final Logger logger = trackmate.getGreenModel().getLogger();
		final GreenobjectTrackerFactory trackerFactory = trackmate.getGreenSettings().trackerFactory;
		logger.log("Starting tracking using " + trackerFactory.getName() + "\n", Logger.BLUE_COLOR);
		logger.log("with settings:\n");
		logger.log(trackerFactory.toString(trackmate.getGreenSettings().trackerSettings));
		controller.disableButtonsAndStoreState();

		new Thread("TrackMate tracking thread") {
			@Override
			public void run() {
				final long start = System.currentTimeMillis();
				final boolean trackingOK = trackmate.execTracking();
				final long end = System.currentTimeMillis();
				if (trackingOK) {
					logger.log("Found " + trackmate.getGreenModel().getTrackModel().nTracks(false) + " tracks.\n");
					logger.log(String.format("Tracking done in %.1f s.\n", (end - start) / 1e3f), Logger.BLUE_COLOR);
				} else {
					logger.error(trackmate.getErrorMessage() + '\n');
				}
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						controller.restoreButtonsState();
					}
				});
			}

		}.start();
	}

	@Override
	public void aboutToHidePanel() {
		final Thread trackFeatureCalculationThread = new Thread("TrackMate track feature calculation thread") {
			@Override
			public void run() {
				trackmate.computeTrackFeatures(true);
			}
		};
		trackFeatureCalculationThread.start();
		try {
			trackFeatureCalculationThread.join();
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void comingBackToPanel() {
	}

	@Override
	public String getKey() {
		return KEY;
	}
}

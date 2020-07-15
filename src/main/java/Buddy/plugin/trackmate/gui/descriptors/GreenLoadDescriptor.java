package Buddy.plugin.trackmate.gui.descriptors;

import Buddy.plugin.trackmate.GreenModel;
import Buddy.plugin.trackmate.GreenSettings;
import Buddy.plugin.trackmate.Logger;
import Buddy.plugin.trackmate.Model;
import Buddy.plugin.trackmate.Settings;
import Buddy.plugin.trackmate.TrackMate;
import Buddy.plugin.trackmate.gui.GreenTrackMateGUIController;
import Buddy.plugin.trackmate.gui.GuiUtils;
import Buddy.plugin.trackmate.gui.TrackMateGUIController;
import Buddy.plugin.trackmate.io.GreenTmXmlReader;
import Buddy.plugin.trackmate.io.IOUtils;
import Buddy.plugin.trackmate.providers.BCellobjectAnalyzerProvider;
import Buddy.plugin.trackmate.providers.EdgeAnalyzerProvider;
import Buddy.plugin.trackmate.providers.GreenEdgeAnalyzerProvider;
import Buddy.plugin.trackmate.providers.GreenTrackAnalyzerProvider;
import Buddy.plugin.trackmate.providers.GreenViewProvider;
import Buddy.plugin.trackmate.providers.GreenobjectAnalyzerProvider;
import Buddy.plugin.trackmate.providers.TrackAnalyzerProvider;
import Buddy.plugin.trackmate.providers.TrackerProvider;
import Buddy.plugin.trackmate.providers.ViewProvider;
import Buddy.plugin.trackmate.util.TMUtils;
import Buddy.plugin.trackmate.util.Version;
import Buddy.plugin.trackmate.visualization.TrackMateModelView;
import Buddy.plugin.trackmate.visualization.hyperstack.GreenHyperStackDisplayer;
import Buddy.plugin.trackmate.visualization.hyperstack.HyperStackDisplayer;
import Buddy.plugin.trackmate.visualization.trackscheme.BCellobjectImageUpdater;
import Buddy.plugin.trackmate.visualization.trackscheme.GreenobjectImageUpdater;
import Buddy.plugin.trackmate.visualization.trackscheme.TrackScheme;
import pluginTools.InteractiveBud;
import pluginTools.InteractiveGreen;

import java.io.File;
import java.util.Collection;
import java.util.Map;

public class GreenLoadDescriptor extends GreenSomeDialogDescriptor {

	protected InteractiveGreen parent;
	
	private static final String KEY = "Loading";

	private final TrackMate trackmate;

	private final GreenTrackMateGUIController controller;
	

	public GreenLoadDescriptor(final InteractiveGreen parent, final GreenTrackMateGUIController controller) {
		super(controller.getGUI().getLogPanel());
		this.parent = parent;
		this.controller = controller;
		this.trackmate = controller.getPlugin();
	}
	


	@Override
	public void displayingPanel(InteractiveGreen parent) {

		if (null == file) {
			try {
				final File folder = new File(trackmate.getSettings().imp.getOriginalFileInfo().directory);
				file = new File(
						folder.getPath() + File.separator + trackmate.getSettings().imp.getShortTitle() + ".xml");
			} catch (final NullPointerException npe) {
				final File folder = new File(System.getProperty("user.dir")).getParentFile().getParentFile();
				file = new File(folder.getPath() + File.separator + "TrackMateData.xml");
			}
		}

		final Logger logger = logPanel.getLogger();
		final File tmpFile = IOUtils.askForFileForLoading(file, "Load a TrackMate XML file", controller.getGUI(),
				logger);
		if (null == tmpFile) {
			return;
		}
		file = tmpFile;

		// Read the file content
		GreenTmXmlReader reader = new GreenTmXmlReader(file);
		final Version version = new Version(reader.getVersion());
	
		if (!reader.isReadingOk()) {
			logger.error(reader.getErrorMessage());
			logger.error("Aborting.\n"); // If I cannot even open the xml
											// file, it is not worth going on.
			return;
		}

		// Log
		final String logText = reader.getLog() + '\n';
		// Model
		final GreenModel model = reader.getModel();
		// Settings -> empty for now.
		final GreenSettings settings = new GreenSettings();

		// With this we can create a new controller from the provided one:
		final TrackMate lTrackmate = new TrackMate(parent, settings);
		final GreenTrackMateGUIController newcontroller = controller.createOn(parent, lTrackmate);

		// We feed then the reader with the providers taken from the NEW
		// controller.
		final TrackerProvider trackerProvider = newcontroller.getTrackerProvider();
		final GreenobjectAnalyzerProvider spotAnalyzerProvider = newcontroller.getGreenobjectAnalyzerProvider();
		final GreenEdgeAnalyzerProvider edgeAnalyzerProvider = newcontroller.getEdgeAnalyzerProvider();
		final GreenTrackAnalyzerProvider trackAnalyzerProvider = newcontroller.getTrackAnalyzerProvider();

		// GUI position
		GuiUtils.positionWindow(newcontroller.getGUI(), settings.imp.getWindow());

		// GUI state
		final String guiState = reader.getGUIState();

		// Views
		final GreenViewProvider viewProvider = newcontroller.getViewProvider();
		final Collection<TrackMateModelView> views = reader.getViews(viewProvider, model, settings,
				newcontroller.getSelectionModel());
		for (final TrackMateModelView view : views) {
			if (view instanceof TrackScheme) {
				final TrackScheme trackscheme = (TrackScheme) view;
				trackscheme.setGreenobjectImageUpdater(new GreenobjectImageUpdater(settings));
			}
		}

		if (!reader.isReadingOk()) {
			final Logger newlogger = newcontroller.getGUI().getLogger();
			newlogger.error("Some errors occured while reading file:\n");
			newlogger.error(reader.getErrorMessage());
		}
		newcontroller.setGUIStateString(guiState);

		// Setup and render views
		if (views.isEmpty()) { // at least one view.
			views.add(new GreenHyperStackDisplayer(model, newcontroller.getSelectionModel(), settings.imp));
		}
		final Map<String, Object> displaySettings = newcontroller.getGuimodel().getDisplaySettings();
		for (final TrackMateModelView view : views) {
			for (final String key : displaySettings.keySet()) {
				newcontroller.getGuimodel().addView(view);
				view.setDisplaySettings(key, displaySettings.get(key));
			}
			view.render();
		}

		// Close the old one
		controller.quit();

		// Text
		newcontroller.getGUI().getLogPanel().setTextContent(logText);
		model.getLogger().log("File loaded on " + TMUtils.getCurrentTimeString() + '\n', Logger.BLUE_COLOR);
	}
	
	
	


	@Override
	public String getKey() {
		return KEY;
	}

}

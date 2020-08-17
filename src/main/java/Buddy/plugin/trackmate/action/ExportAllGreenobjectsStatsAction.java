package Buddy.plugin.trackmate.action;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.swing.ImageIcon;

import org.scijava.plugin.Plugin;

import Buddy.plugin.trackmate.FeatureModel;
import Buddy.plugin.trackmate.GreenFeatureModel;
import Buddy.plugin.trackmate.GreenModel;
import Buddy.plugin.trackmate.GreenSelectionModel;
import Buddy.plugin.trackmate.Model;
import Buddy.plugin.trackmate.SelectionModel;
import Buddy.plugin.trackmate.TrackMate;
import Buddy.plugin.trackmate.gui.GreenTrackMateGUIController;
import Buddy.plugin.trackmate.gui.GreenTrackMateWizard;
import Buddy.plugin.trackmate.gui.TrackMateGUIController;
import Buddy.plugin.trackmate.gui.TrackMateWizard;
import greenDetector.Greenobject;
import ij.WindowManager;
import ij.measure.ResultsTable;
import ij.text.TextPanel;
import ij.text.TextWindow;

public class ExportAllGreenobjectsStatsAction extends AbstractTMAction {

	public static final ImageIcon ICON = new ImageIcon(GreenTrackMateWizard.class.getResource("images/calculator.png"));

	public static final String NAME = "Export all Greenobjects statistics";

	public static final String KEY = "EXPORT_ALL_GreenobjectS_STATS";

	public static final String INFO_TEXT = "<html>"
			+ "Compute and export the statistics of all Greenobjects to ImageJ results table."
			+ "The numerical features of all visible Greenobjects are exported, "
			+ "regardless of whether they are in a track or not." + "</html>";

	private static final String ID_COLUMN = "ID";

	private ResultsTable GreenobjectTable;

	private final GreenSelectionModel selectionModel;

	private final static String Greenobject_TABLE_NAME = "All Greenobjects statistics";

	public ExportAllGreenobjectsStatsAction(final GreenSelectionModel selectionModel) {
		this.selectionModel = selectionModel;
	}

	@Override
	public void execute(final TrackMate trackmate) {
		logger.log("Exporting all Greenobjects statistics.\n");

		// Model
		final GreenModel model = trackmate.getGreenModel();
		final GreenFeatureModel fm = model.getFeatureModel();

		// Export Greenobjects
		final Collection<String> GreenobjectFeatures = trackmate.getGreenModel().getFeatureModel().getGreenobjectFeatures();

		// Create table
		this.GreenobjectTable = new ResultsTable();

		final Iterable<Greenobject> iterable = model.getGreenobjects().iterable(true);
		for (final Greenobject Greenobject : iterable) {
			GreenobjectTable.incrementCounter();
			GreenobjectTable.addLabel(Greenobject.getName());
			GreenobjectTable.addValue(ID_COLUMN, "" + Greenobject.ID());

			// Check if it is in a track.
			final Integer trackID = model.getTrackModel().trackIDOf(Greenobject);
			if (null != trackID)
				GreenobjectTable.addValue("TRACK_ID", "" + trackID.intValue());
			else
				GreenobjectTable.addValue("TRACK_ID", "None");

			for (final String feature : GreenobjectFeatures) {
				final Double val = Greenobject.getFeature(feature);
				if (null == val) {
					GreenobjectTable.addValue(feature, "None");
				} else {
					if (fm.getGreenobjectFeatureIsInt().get(feature).booleanValue()) {
						GreenobjectTable.addValue(feature, "" + val.intValue());
					} else {
						GreenobjectTable.addValue(feature, val.doubleValue());
					}
				}
			}
		}
		logger.log(" Done.\n");

		// Show tables
		GreenobjectTable.show(Greenobject_TABLE_NAME);

		// Hack to make the results tables in sync with selection model.
		if (null != selectionModel) {

			/*
			 * Greenobject table listener.
			 */

			final TextWindow GreenobjectTableWindow = (TextWindow) WindowManager.getWindow(Greenobject_TABLE_NAME);
			final TextPanel GreenobjectTableTextPanel = GreenobjectTableWindow.getTextPanel();
			GreenobjectTableTextPanel.addMouseListener(new MouseAdapter() {

				@Override
				public void mouseReleased(final MouseEvent e) {
					final int selStart = GreenobjectTableTextPanel.getSelectionStart();
					final int selEnd = GreenobjectTableTextPanel.getSelectionEnd();
					if (selStart < 0 || selEnd < 0)
						return;

					final int minLine = Math.min(selStart, selEnd);
					final int maxLine = Math.max(selStart, selEnd);
					final Set<Greenobject> Greenobjects = new HashSet<>();
					for (int row = minLine; row <= maxLine; row++) {
						final int GreenobjectID = Integer.parseInt(GreenobjectTable.getStringValue(ID_COLUMN, row));
						final Greenobject Greenobject = model.getGreenobjects().search(GreenobjectID);
						if (null != Greenobject)
							Greenobjects.add(Greenobject);
					}
					selectionModel.clearSelection();
					selectionModel.addGreenobjectToSelection(Greenobjects);
				}
			});
		}
	}

	/**
	 * Returns the results table containing the Greenobject statistics, or
	 * <code>null</code> if the {@link #execute(TrackMate)} method has not been
	 * called.
	 *
	 * @return the results table containing the Greenobject statistics.
	 */
	public ResultsTable getGreenobjectTable() {
		return GreenobjectTable;
	}

	@Plugin(type = GreenTrackMateActionFactory.class)
	public static class Factory implements GreenTrackMateActionFactory {

		@Override
		public String getInfoText() {
			return INFO_TEXT;
		}

		@Override
		public String getKey() {
			return KEY;
		}

		@Override
		public TrackMateAction create(final GreenTrackMateGUIController controller) {
			return new ExportAllGreenobjectsStatsAction(controller.getSelectionModel());
		}

		@Override
		public String getName() {
			return NAME;
		}

		@Override
		public ImageIcon getIcon() {
			// TODO Auto-generated method stub
			return null;
		}
	}

}

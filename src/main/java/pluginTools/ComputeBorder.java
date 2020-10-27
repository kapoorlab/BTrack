package pluginTools;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import budDetector.Budobject;
import displayBud.DisplayListOverlay;
import ij.gui.OvalRoi;
import net.imglib2.RealLocalizable;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import utility.GetNearest;

public class ComputeBorder extends SwingWorker<Void, Void> {

	final InteractiveBud parent;
	final JProgressBar jpb;

	public ComputeBorder(final InteractiveBud parent, final JProgressBar jpb) {

		this.parent = parent;
		this.jpb = jpb;

	}

	public Void doInBackground() throws Exception {

		String uniqueID = Integer.toString(parent.thirdDimension);
		if (parent.AllBudpoints.get(uniqueID) != null) {

			ArrayList<Budobject> Currentbud = parent.AllBuds.get(uniqueID);

			for (Budobject thisbud : Currentbud) {

				RealLocalizable centerpoint = thisbud.Budcenter;

				List<RealLocalizable> truths = thisbud.linelist;

				List<RealLocalizable> skeletonEndPoints = thisbud.dynamiclinelist;

				DisplayListOverlay.ArrowDisplay(parent,
						new ValuePair<RealLocalizable, List<RealLocalizable>>(centerpoint, truths), skeletonEndPoints,
						uniqueID);

				if (parent.SegYelloworiginalimg != null) {

					GetNearest.getAllInteriorCells(parent, parent.CurrentViewInt, parent.CurrentViewYellowInt);

				}

			}

		}

		else {

			BoundaryTrack newtrack = new BoundaryTrack(parent, jpb);
			newtrack.ShowBoundary();

		}

		return null;

	}

	@Override
	protected void done() {

		parent.jpb.setIndeterminate(false);
		if (parent.jpb != null)
			utility.BudProgressBar.SetProgressBar(parent.jpb, 100,
					"Bud endpoints computed for all buds present at timepoint " + parent.thirdDimension);

	}

}

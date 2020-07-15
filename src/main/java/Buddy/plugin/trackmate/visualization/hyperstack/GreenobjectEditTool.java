package Buddy.plugin.trackmate.visualization.hyperstack;

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.ComponentEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

import javax.swing.SwingUtilities;

import org.jgrapht.graph.DefaultWeightedEdge;

import Buddy.plugin.trackmate.GreenModel;
import Buddy.plugin.trackmate.GreenobjectCollection;
import Buddy.plugin.trackmate.Logger;
import Buddy.plugin.trackmate.Model;
import Buddy.plugin.trackmate.SelectionModel;
import Buddy.plugin.trackmate.util.TMUtils;
import fiji.tool.AbstractTool;
import fiji.tool.ToolWithOptions;
import greenDetector.Greenobject;
import ij.IJ;
import ij.ImagePlus;
import ij.gui.FreehandRoi;
import ij.gui.ImageCanvas;
import ij.gui.Toolbar;

public class GreenobjectEditTool extends AbstractTool
		implements MouseMotionListener, MouseListener, MouseWheelListener, KeyListener, ToolWithOptions {

	private static final boolean DEBUG = false;

	private static final double COARSE_STEP = 2;

	private static final double FINE_STEP = 0.2f;

	private static final String TOOL_NAME = "Greenobject edit tool";

	private static final String TOOL_ICON = "CeacD70Cd8bD80" + "D71Cc69D81CfefD91" + "CdbcD72Cb9bD82"
			+ "Cd9bD73Cc8aD83CfefD93" + "CdddD54CbaaD64Cb69D74Cb59D84Cb9aD94CdddDa4"
			+ "CfefD25Cd9bD35Cb8aD45CaaaD55CcccD65CfdeL7585CdccD95CaaaDa5Cb8aDb5Cd7aDc5CfceDd5"
			+ "CfeeD26Cc69D36Cc8aD46CdacDb6Cb59Dc6CecdDd6" + "Cb9aD37CdcdD47CeeeDb7Ca89Dc7"
			+ "CfefD28Cc7aD38Cd9cD48CecdDb8Cb79Dc8CfdeDd8" + "CcabD29Cb59D39Cb69D49CedeD59CeacDb9Cc59Dc9CebdDd9"
			+ "CfdeD0aCc7aD1aCb8aD2aCedeD3aCcbcD4aCb7aD5aCe9cD6aCeeeDbaCa89DcaCfefDda"
			+ "CebdD0bCc59D1bCebdD2bCfefD4bCc7aL5b6bCeceDbbCb79DcbCfdeDdb"
			+ "CfeeD0cCa89D1cCfefD2cCcabL5c6cCc9bDbcCc59DccCdabDdc"
			+ "CedeD0dCb79D1dCedeD2dCc9bL5d6dCecdD9dCc8aDadCb9aDbdCdbcDcdCb8aDddCd8bDedCfceDfd"
			+ "CebdD0eCc59D1eCebdD2eCfeeD4eCc7aD5eCc6aD6eCfeeD7eCd9bD9eCc59DaeCfdeDbeCebdDdeCc59DeeCeacDfe"
			+ "CfefD0fCdbcD1fCdddD4fCdcdL5f6fCdddD7fCfdeD9fCdbdDafCebdDefCfefDff";

	/**
	 * Fall back default radius when the settings does not give a default radius to
	 * use.
	 */
	private static final double FALL_BACK_RADIUS = 5;

	/** The singleton instance. */
	private static GreenobjectEditTool instance;

	/** Stores the edited Greenobject in each {@link ImagePlus}. */
	private final HashMap<ImagePlus, Greenobject> editedGreenobjects = new HashMap<>();

	/** Stores the view possible attached to each {@link ImagePlus}. */
	HashMap<ImagePlus, GreenHyperStackDisplayer> displayers = new HashMap<>();

	/** Stores the config panel attached to each {@link ImagePlus}. */
	// private final HashMap< ImagePlus, FloatingDisplayConfigFrame > configFrames =
	// new HashMap<>();

	/** The radius of the previously edited Greenobject. */
	private Double previousRadius = null;

	private Greenobject quickEditedGreenobject;

	/** Flag for the auto-linking mode. */
	private boolean autolinkingmode = false;

	GreenobjectEditToolParams params = new GreenobjectEditToolParams();

	private Logger logger = Logger.IJTOOLBAR_LOGGER;

	private GreenobjectEditToolConfigPanel configPanel;

	/**
	 * The last {@link ImagePlus} on which an action happened.
	 */
	ImagePlus imp;

	private FreehandRoi roiedit;

	/*
	 * CONSTRUCTOR
	 */

	/**
	 * Singleton
	 */
	private GreenobjectEditTool() {
	}

	/**
	 * Return the singleton instance for this tool. If it was not previously
	 * instantiated, this calls instantiates it.
	 */
	public static GreenobjectEditTool getInstance() {
		if (null == instance) {
			instance = new GreenobjectEditTool();
			if (DEBUG)
				System.out.println("[GreenobjectEditTool] Instantiating: " + instance);
		}
		if (DEBUG)
			System.out.println("[GreenobjectEditTool] Returning instance: " + instance);
		return instance;
	}

	/**
	 * Return true if the tool is currently present in ImageJ toolbar.
	 */
	public static boolean isLaunched() {
		final Toolbar toolbar = Toolbar.getInstance();
		if (null != toolbar && toolbar.getToolId(TOOL_NAME) >= 0)
			return true;
		return false;
	}

	/*
	 * METHODS
	 */

	@Override
	public String getToolName() {
		return TOOL_NAME;
	}

	@Override
	public String getToolIcon() {
		return TOOL_ICON;
	}

	/**
	 * Overridden so that we can keep track of the last ImagePlus actions are taken
	 * on. Very much like ImageJ.
	 */
	@Override
	public ImagePlus getImagePlus(final ComponentEvent e) {
		imp = super.getImagePlus(e);
		return imp;
	}

	/**
	 * Register the given {@link HyperStackDisplayer}. If this method id not called,
	 * the tool will not respond.
	 */
	public void register(final ImagePlus lImp, final GreenHyperStackDisplayer displayer) {
		if (DEBUG)
			System.out.println("[GreenobjectEditTool] Currently registered: " + displayers);

		if (displayers.containsKey(lImp)) {
			unregisterTool(lImp);
			if (DEBUG)
				System.out.println("[GreenobjectEditTool] De-registering " + lImp + " as tool listener.");
		}

		displayers.put(lImp, displayer);
		if (DEBUG) {
			System.out.println("[GreenobjectEditTool] Registering " + lImp + " and " + displayer + "."
					+ " Currently registered: " + displayers);
		}
	}

	/*
	 * MOUSE AND MOUSE MOTION
	 */

	@Override
	public void mouseClicked(final MouseEvent e) {
		final ImagePlus lImp = getImagePlus(e);
		final GreenHyperStackDisplayer displayer = displayers.get(lImp);
		if (DEBUG) {
			System.out.println("[GreenobjectEditTool] @mouseClicked");
			System.out.println("[GreenobjectEditTool] Got " + lImp + " as ImagePlus");
			System.out.println("[GreenobjectEditTool] Matching displayer: " + displayer);

			for (final MouseListener ml : lImp.getCanvas().getMouseListeners()) {
				System.out.println("[GreenobjectEditTool] mouse listener: " + ml);
			}

		}

		if (null == displayer)
			return;

		final int frame = displayer.imp.getFrame() - 1;
		final GreenModel model = displayer.getModel();
		Greenobject editedGreenobject = editedGreenobjects.get(lImp);

		final SelectionModel selectionModel = displayer.getSelectionModel();

		// Check desired behavior
		switch (e.getClickCount()) {

		case 1: {
			// Change selection
			// only if we are not currently editing.
			if (null != editedGreenobject) {
				return;
			}
			// If no target, we clear selection

		}

		case 2: {
			// Edit Greenobject

			if (null == editedGreenobject) {
				// No Greenobject is currently edited, we pick one to edit
				Double radius;

				// Edit Greenobject
				if (DEBUG)
					System.out.println("[GreenobjectEditTool] mouseClicked: Set " + editedGreenobject
							+ " as editing Greenobject for this imp.");

			} else {
				// We leave editing mode
				if (DEBUG)
					System.out.println("[GreenobjectEditTool] mouseClicked: Got " + editedGreenobject
							+ " as editing Greenobject for this imp, leaving editing mode.");

				// A hack: we update the current z and t of the edited Greenobject to
				// the current one,
				// because it is not updated otherwise: there is no way to
				// listen to slice change
				final double calibration[] = TMUtils.getSpatialCalibration(lImp);
				final Double initFrame = editedGreenobject.getFeature(Greenobject.POSITION_T);
				// Move it in Z
				editedGreenobject.putFeature(Greenobject.POSITION_T, frame * lImp.getCalibration().frameInterval);
				editedGreenobject.putFeature(Greenobject.POSITION_T, Double.valueOf(frame));

				model.beginUpdate();
				try {
					if (initFrame == null) {
						// Means that the Greenobject was created
						model.addGreenobjectTo(editedGreenobject, frame);
					} else if (initFrame != frame) {
						// Move it to the new frame
						model.moveGreenobjectFrom(editedGreenobject, initFrame.intValue(), frame);
					} else {
						// The Greenobjects pre-existed and was not moved across frames
						model.updateFeatures(editedGreenobject);
					}
					logger.log("Finished editing Greenobject " + editedGreenobject + ".\n");

				} finally {
					model.endUpdate();
				}

				/*
				 * If we are in auto-link mode, we create an edge with Greenobject in selection,
				 * if there is just one and if it is in a previous frame
				 */
				if (autolinkingmode) {
					final Set<Greenobject> GreenobjectSelection = selectionModel.getGreenobjectSelection();
					if (GreenobjectSelection.size() == 1) {
						final Greenobject source = GreenobjectSelection.iterator().next();
						if (editedGreenobject.diffTo(source, Greenobject.POSITION_T) > 0) {
							model.beginUpdate();
							try {
								model.addEdge(source, editedGreenobject, -1);
								logger.log("Created a link between " + source + " and " + editedGreenobject + ".\n");
							} finally {
								model.endUpdate();
							}
						}
					}
				}

				// Set selection
				selectionModel.clearGreenobjectSelection();
				selectionModel.addGreenobjectToSelection(editedGreenobject);

				// Forget edited Greenobject, but remember its radius
				previousRadius = editedGreenobject.getFeature(Greenobject.RADIUS);
				editedGreenobject = null;
				displayer.GreenobjectOverlay.editingGreenobject = null;
			}
			break;
		}
		}
		editedGreenobjects.put(lImp, editedGreenobject);
	}

	@Override
	public void mousePressed(final MouseEvent e) {
	}

	@Override
	public void mouseReleased(final MouseEvent e) {
		if (null != roiedit) {
			new Thread("GreenobjectEditTool roiedit processing") {
				@Override
				public void run() {
					roiedit.mouseReleased(e);
					final ImagePlus lImp = getImagePlus(e);
					final HyperStackDisplayer displayer = displayers.get(lImp);
					final int frame = displayer.imp.getFrame() - 1;
					final Model model = displayer.getModel();
					final SelectionModel selectionModel = displayer.getSelectionModel();

					final Iterator<Greenobject> it;
					it = model.getGreenobjects().iterator(frame);

					final Collection<Greenobject> added = new ArrayList<>();
					final double calibration[] = TMUtils.getSpatialCalibration(lImp);

					while (it.hasNext()) {
						final Greenobject Greenobject = it.next();
						final double x = Greenobject.getFeature(Greenobject.POSITION_X);
						final double y = Greenobject.getFeature(Greenobject.POSITION_Y);
						// In pixel units
						final int xp = (int) (x / calibration[0] + 0.5f);
						final int yp = (int) (y / calibration[1] + 0.5f);

						if (null != roiedit && roiedit.contains(xp, yp)) {
							added.add(Greenobject);
						}
					}

					if (!added.isEmpty()) {
						selectionModel.addGreenobjectToSelection(added);
						if (added.size() == 1)
							logger.log("Added one Greenobject to selection.\n");
						else
							logger.log("Added " + added.size() + " Greenobjects to selection.\n");
					}
					roiedit = null;
				}
			}.start();
		}
	}

	@Override
	public void mouseEntered(final MouseEvent e) {
	}

	@Override
	public void mouseExited(final MouseEvent e) {
	}

	@Override
	public void mouseDragged(final MouseEvent e) {
		final ImagePlus lImp = getImagePlus(e);
		final double[] calibration = TMUtils.getSpatialCalibration(lImp);
		final HyperStackDisplayer displayer = displayers.get(lImp);
		if (null == displayer)
			return;
		final Greenobject editedGreenobject = editedGreenobjects.get(lImp);
		if (null != editedGreenobject) {

			final Point mouseLocation = e.getPoint();
			final ImageCanvas canvas = getImageCanvas(e);
			final double x = (-0.5 + canvas.offScreenXD(mouseLocation.x)) * calibration[0];
			final double y = (-0.5 + canvas.offScreenYD(mouseLocation.y)) * calibration[1];
			final double z = (lImp.getSlice() - 1) * calibration[2];
			editedGreenobject.putFeature(Greenobject.POSITION_X, x);
			editedGreenobject.putFeature(Greenobject.POSITION_Y, y);
			displayer.imp.updateAndDraw();
			updateStatusBar(editedGreenobject, lImp.getCalibration().getUnits());
		} else {
			if (null == roiedit) {
				if (!IJ.spaceBarDown()) {
					roiedit = new FreehandRoi(e.getX(), e.getY(), lImp) {
						private static final long serialVersionUID = 1L;

						@Override
						protected void handleMouseUp(final int screenX, final int screenY) {
							type = FREEROI;
							super.handleMouseUp(screenX, screenY);
						}
					};
					lImp.setRoi(roiedit);
				}
			} else {
				roiedit.mouseDragged(e);
			}
		}
	}

	@Override
	public void mouseMoved(final MouseEvent e) {
		if (quickEditedGreenobject == null)
			return;
		final ImagePlus lImp = getImagePlus(e);
		final double[] calibration = TMUtils.getSpatialCalibration(lImp);
		final HyperStackDisplayer displayer = displayers.get(lImp);
		if (null == displayer)
			return;
		final Greenobject editedGreenobject = editedGreenobjects.get(lImp);
		if (null != editedGreenobject)
			return;

		final Point mouseLocation = e.getPoint();
		final ImageCanvas canvas = getImageCanvas(e);
		final double x = (-0.5 + canvas.offScreenXD(mouseLocation.x)) * calibration[0];
		final double y = (-0.5 + canvas.offScreenYD(mouseLocation.y)) * calibration[1];
		final double z = (lImp.getSlice() - 1) * calibration[2];

		quickEditedGreenobject.putFeature(Greenobject.POSITION_X, x);
		quickEditedGreenobject.putFeature(Greenobject.POSITION_Y, y);
		displayer.imp.updateAndDraw();

	}

	/*
	 * MOUSEWHEEL
	 */

	@Override
	public void mouseWheelMoved(final MouseWheelEvent e) {
		final ImagePlus lImp = getImagePlus(e);
		final HyperStackDisplayer displayer = displayers.get(lImp);
		if (null == displayer)
			return;
		final Greenobject editedGreenobject = editedGreenobjects.get(lImp);
		if (null == editedGreenobject || !e.isAltDown())
			return;
		double radius = editedGreenobject.getFeature(Greenobject.RADIUS);
		final double dx = lImp.getCalibration().pixelWidth;
		if (e.isShiftDown())
			radius += e.getWheelRotation() * dx * COARSE_STEP;
		else
			radius += e.getWheelRotation() * dx * FINE_STEP;

		if (radius < dx) {
			e.consume();
			return;
		}

		editedGreenobject.putFeature(Greenobject.RADIUS, radius);
		displayer.imp.updateAndDraw();
		e.consume();
		updateStatusBar(editedGreenobject, lImp.getCalibration().getUnits());
	}

	/*
	 * KEYLISTENER
	 */

	@Override
	public void keyTyped(final KeyEvent e) {
	}

	@Override
	public void keyPressed(final KeyEvent e) {

		if (DEBUG)
			System.out.println("[GreenobjectEditTool] keyPressed: " + e.getKeyChar());

		final ImagePlus lImp = getImagePlus(e);
		if (lImp == null)
			return;
		final HyperStackDisplayer displayer = displayers.get(lImp);
		if (null == displayer)
			return;

		final Model model = displayer.getModel();
		final SelectionModel selectionModel = displayer.getSelectionModel();
		Greenobject editedGreenobject = editedGreenobjects.get(lImp);
		final ImageCanvas canvas = getImageCanvas(e);

		final int keycode = e.getKeyCode();

		switch (keycode) {

		// case KeyEvent.VK_R:
		// {
		// FloatingDisplayConfigFrame configFrame = configFrames.get( imp );
		// if ( null == configFrame )
		// {
		// final String title = displayer.getImp().getShortTitle();
		// configFrame = new FloatingDisplayConfigFrame( model, displayer, title );
		// configFrame.setDefaultCloseOperation( JFrame.HIDE_ON_CLOSE );
		// configFrame.setLocationRelativeTo( displayer.getImp().getWindow() );
		// configFrames.put( imp, configFrame );
		// }
		//
		// configFrame.setVisible( !configFrame.isVisible() );
		// break;
		// }

		// Delete currently edited Greenobject
		case KeyEvent.VK_DELETE: {
			if (null == editedGreenobject) {
				final ArrayList<Greenobject> GreenobjectSelection = new ArrayList<>(
						selectionModel.getGreenobjectSelection());
				final ArrayList<DefaultWeightedEdge> edgeSelection = new ArrayList<>(selectionModel.getEdgeSelection());
				model.beginUpdate();
				try {
					selectionModel.clearSelection();
					for (final DefaultWeightedEdge edge : edgeSelection) {
						model.removeEdge(edge);
						logger.log("Removed edge " + edge + ".\n");
					}
					for (final Greenobject Greenobject : GreenobjectSelection) {
						model.removeGreenobject(Greenobject);
						logger.log("Removed Greenobject " + Greenobject + ".\n");
					}
				} finally {
					model.endUpdate();
				}

			} else {
				model.beginUpdate();
				try {
					model.removeGreenobject(editedGreenobject);
					logger.log("Removed " + editedGreenobject + ".\n");
				} finally {
					model.endUpdate();
				}
				editedGreenobject = null;
				editedGreenobjects.put(lImp, null);
			}
			lImp.updateAndDraw();
			e.consume();
			break;
		}

		// Quick add Greenobject at mouse
		case KeyEvent.VK_A: {

			if (null == editedGreenobject) {

				// Create and drop a new Greenobject
				double radius;
				if (null != previousRadius) {
					radius = previousRadius;
				} else {
					radius = FALL_BACK_RADIUS;
				}

				final double dt = lImp.getCalibration().frameInterval;
				final int frame = displayer.imp.getFrame() - 1;

				model.beginUpdate();

				model.endUpdate();

				/*
				 * If we are in auto-link mode, we create an edge with Greenobject in selection,
				 * if there is just one and if it is in a previous frame
				 */

				lImp.updateAndDraw();
				e.consume();

			}

			break;
		}

		// Quick delete Greenobject under mouse
		case KeyEvent.VK_D: {

			if (null == editedGreenobject) {

				final int frame = displayer.imp.getFrame() - 1;

				model.beginUpdate();

				lImp.updateAndDraw();

			} else {

			}
			e.consume();
			break;
		}

		// Quick move Greenobject under the mouse
		case KeyEvent.VK_SPACE: {

			if (null == quickEditedGreenobject) {
				final int frame = displayer.imp.getFrame() - 1;
				if (null == quickEditedGreenobject) {
					return;
				}
			}
			e.consume();
			break;

		}

		// Quick change Greenobject radius
		case KeyEvent.VK_Q:
		case KeyEvent.VK_E: {

			e.consume();
			if (null == editedGreenobject) {

				final int frame = displayer.imp.getFrame() - 1;

				int factor;
				if (e.getKeyCode() == KeyEvent.VK_Q) {
					factor = -1;
				} else {
					factor = 1;
				}
				final double dx = lImp.getCalibration().pixelWidth;

				model.beginUpdate();

				lImp.updateAndDraw();
			}

			break;
		}

		// Copy Greenobjects from previous frame
		case KeyEvent.VK_V: {
			if (e.isShiftDown()) {

				final int currentFrame = lImp.getFrame() - 1;
				if (currentFrame > 0) {

					final GreenobjectCollection Greenobjects = model.getGreenobjects();
					if (Greenobjects.getNGreenobjects(currentFrame - 1) == 0) {
						e.consume();
						break;
					}
					final HashSet<Greenobject> copiedGreenobjects = new HashSet<>(
							Greenobjects.getNGreenobjects(currentFrame - 1));
					final HashSet<String> featuresKey = new HashSet<>(
							Greenobjects.iterator(currentFrame - 1).next().getFeatures().keySet());
					featuresKey.remove(Greenobject.POSITION_T); // Deal with time
					// separately
					double dt = lImp.getCalibration().frameInterval;
					if (dt == 0) {
						dt = 1;
					}

					for (final Iterator<Greenobject> it = Greenobjects.iterator(currentFrame - 1); it.hasNext();) {
						final Greenobject Greenobject = it.next();
						final Greenobject newGreenobject = Greenobject;
						// Deal with features
						Double val;
						for (final String key : featuresKey) {
							val = Greenobject.getFeature(key);
							if (val == null) {
								continue;
							}
							newGreenobject.putFeature(key, val);
						}
						newGreenobject.putFeature(Greenobject.POSITION_T,
								Greenobject.getFeature(Greenobject.POSITION_T) + dt);
						copiedGreenobjects.add(newGreenobject);
					}

					model.beginUpdate();
					try {
						// Remove old ones
						final HashSet<Greenobject> toRemove = new HashSet<>();
						for (final Iterator<Greenobject> it = Greenobjects.iterator(currentFrame); it.hasNext();) {
							toRemove.add(it.next());
						}
						for (final Greenobject Greenobject : toRemove) {
							model.removeGreenobject(Greenobject);
						}

						// Add new ones
						for (final Greenobject Greenobject : copiedGreenobjects) {
							model.addGreenobjectTo(Greenobject, currentFrame);
						}
					} finally {
						model.endUpdate();
						lImp.updateAndDraw();
						logger.log("Removed Greenobjects of frame " + currentFrame + ".\n");
						logger.log("Copied Greenobjects of frame " + (currentFrame - 1) + " to frame " + currentFrame
								+ ".\n");
					}
				}

				e.consume();
			}
			break;
		}

		case KeyEvent.VK_L: {

			if (e.isShiftDown()) {
				/*
				 * Toggle auto-linking mode
				 */
				autolinkingmode = !autolinkingmode;
				logger.log("Toggled auto-linking mode " + (autolinkingmode ? "on.\n" : "off.\n"));

			} else {
				/*
				 * Toggle a link between two Greenobjects.
				 */
				final Set<Greenobject> selectedGreenobjects = selectionModel.getGreenobjectSelection();
				if (selectedGreenobjects.size() == 2) {
					final Iterator<Greenobject> it = selectedGreenobjects.iterator();
					final Greenobject source = it.next();
					final Greenobject target = it.next();

					if (model.getTrackModel().containsEdge(source, target)) {
						/*
						 * Remove it
						 */
						model.beginUpdate();
						try {
							model.removeEdge(source, target);
							logger.log("Removed edge between " + source + " and " + target + ".\n");
						} finally {
							model.endUpdate();
						}

					} else {
						/*
						 * Create a new link
						 */
						final int ts = source.getFeature(Greenobject.POSITION_T).intValue();
						final int tt = target.getFeature(Greenobject.POSITION_T).intValue();

						if (tt != ts) {
							model.beginUpdate();
							try {
								model.addEdge(source, target, -1);
								logger.log("Created an edge between " + source + " and " + target + ".\n");
							} finally {
								model.endUpdate();
							}
							/*
							 * To emulate a kind of automatic linking, we put the last Greenobject to the
							 * selection, so several Greenobjects can be tracked in a row without having to
							 * de-select one
							 */
							Greenobject single;
							if (tt > ts) {
								single = target;
							} else {
								single = source;
							}
							selectionModel.clearGreenobjectSelection();
							selectionModel.addGreenobjectToSelection(single);

						} else {
							logger.error(
									"Cannot create an edge between two Greenobjects belonging to the same frame.\n");
						}
					}

				} else {
					logger.error("Expected selection to contain 2 Greenobjects, found " + selectedGreenobjects.size()
							+ ".\n");
				}

			}
			e.consume();
			break;

		}

		case KeyEvent.VK_G:
		case KeyEvent.VK_F: {
			// Stepwise time browsing.
			final int currentT = lImp.getT() - 1;
			final int prevStep = (currentT / params.stepwiseTimeBrowsing) * params.stepwiseTimeBrowsing;
			int tp;
			if (keycode == KeyEvent.VK_G) {
				tp = prevStep + params.stepwiseTimeBrowsing;
			} else {
				if (currentT == prevStep) {
					tp = currentT - params.stepwiseTimeBrowsing;
				} else {
					tp = prevStep;
				}
			}
			lImp.setT(tp + 1);

			e.consume();
			break;
		}

		case KeyEvent.VK_W: {
			e.consume(); // consume it: we do not want IJ to close the window
			break;
		}

		}

	}

	@Override
	public void keyReleased(final KeyEvent e) {
		if (DEBUG)
			System.out.println("[GreenobjectEditTool] keyReleased: " + e.getKeyChar());

		switch (e.getKeyCode()) {
		case KeyEvent.VK_SPACE: {
			if (null == quickEditedGreenobject)
				return;
			final ImagePlus lImp = getImagePlus(e);
			if (lImp == null)
				return;
			final HyperStackDisplayer displayer = displayers.get(lImp);
			if (null == displayer)
				return;
			final Model model = displayer.getModel();
			model.beginUpdate();
			try {
				model.updateFeatures(quickEditedGreenobject);
			} finally {
				model.endUpdate();
			}
			quickEditedGreenobject = null;
			break;
		}
		}

	}

	/*
	 * PRIVATE METHODS
	 */

	private void updateStatusBar(final Greenobject Greenobject, final String units) {
		if (null == Greenobject)
			return;
		String statusString = "";
		if (null == Greenobject.getName() || Greenobject.getName().equals("")) {
			statusString = String.format(Locale.US, "Greenobject ID%d, x = %.1f, y = %.1f, z = %.1f, r = %.1f %s",
					Greenobject.ID(), Greenobject.getFeature(Greenobject.POSITION_X),
					Greenobject.getFeature(Greenobject.POSITION_Y), Greenobject.getFeature(Greenobject.RADIUS), units);
		} else {
			statusString = String.format(Locale.US, "Greenobject %s, x = %.1f, y = %.1f, z = %.1f, r = %.1f %s",
					Greenobject.getName(), Greenobject.getFeature(Greenobject.POSITION_X),
					Greenobject.getFeature(Greenobject.POSITION_Y), Greenobject.getFeature(Greenobject.RADIUS), units);
		}
		IJ.showStatus(statusString);
	}

	@Override
	public void showOptionDialog() {
		if (null == configPanel) {
			configPanel = new GreenobjectEditToolConfigPanel(this);
			configPanel.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(final WindowEvent e) {
					logger = Logger.IJTOOLBAR_LOGGER;
				}
			});
		}
		configPanel.setLocation(toolbar.getLocationOnScreen());
		configPanel.setVisible(true);
		logger = configPanel.getLogger();
	}

	/*
	 * INNER CLASSES
	 */

	static class GreenobjectEditToolParams {

		/*
		 * Semi-auto tracking parameters
		 */
		/**
		 * The fraction of the initial quality above which we keep new Greenobjects. The
		 * highest, the more intolerant.
		 */
		double qualityThreshold = 0.5;

		/**
		 * How close must be the new Greenobject found to be accepted, in radius units.
		 */
		double distanceTolerance = 2d;

		/**
		 * We process at most nFrames. Make it 0 or negative to have no bounds.
		 */
		int nFrames = 10;

		/**
		 * By how many frames to jymp when we do step-wide time browsing.
		 */
		int stepwiseTimeBrowsing = 5;

		@Override
		public String toString() {
			return super.toString() + ": " + "QualityThreshold = " + qualityThreshold + ", DistanceTolerance = "
					+ distanceTolerance + ", nFrames = " + nFrames;
		}
	}

}

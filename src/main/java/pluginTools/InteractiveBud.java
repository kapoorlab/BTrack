package pluginTools;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Scrollbar;
import java.awt.TextField;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.JTableHeader;

import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeriesCollection;

import budDetector.BCellobject;
import budDetector.Budobject;
import budDetector.Budpointobject;
import Buddy.plugin.trackmate.BCellobjectCollection;
import fileListeners.BTrackSaveDirectoryListener;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.OvalRoi;
import ij.gui.Overlay;
import ij.plugin.PlugIn;
import kalmanGUI.CovistoKalmanPanel;
import listeners.BTrackAutoEndListener;
import listeners.BTrackFilenameListener;
import listeners.BudAlphaListener;
import listeners.BudLinkobjectListener;
import listeners.BudMastadonListener;
import listeners.BudPREIniSearchListener;
import listeners.BudPRELostFrameListener;
import listeners.BudPREMaxSearchTListener;
import listeners.BudRestartListener;
import listeners.BudSaveAllListener;
import listeners.BudSaveBatchListener;
import listeners.BudSaveListener;
import listeners.BudSkeletonListener;
import listeners.BudSkeletonTrackLengthListener;
import listeners.BudTimeListener;
import listeners.BudTlocListener;
import listeners.BudTrackidListener;
import net.imagej.ImageJ;
import net.imglib2.Cursor;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.algorithm.labeling.ConnectedComponents;
import net.imglib2.algorithm.labeling.ConnectedComponents.StructuringElement;
import net.imglib2.img.ImagePlusAdapter;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.roi.labeling.ImgLabeling;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import net.imglib2.view.Views;
import tracker.BUDDYBudTrackModel;
import tracker.BUDDYCostFunction;
import tracker.BUDDYTrackModel;
import zGUI.CovistoZselectPanel;

public class InteractiveBud  extends JPanel implements PlugIn{

	
	private static final long serialVersionUID = 1L;
	public String usefolder = IJ.getDirectory("imagej");
	public String addToName = "BTrack_";
	public String inputstring;
	public final int scrollbarSize = 1000;
	public Set<Integer> pixellist;
	public NumberFormat nf;
	public Color Drawcolor; 
	public RandomAccessibleInterval<FloatType> originalimg;
	public RandomAccessibleInterval<FloatType> originalSecimg;
	public RandomAccessibleInterval<IntType> Segoriginalimg;
	public RandomAccessibleInterval<FloatType> SegSecoriginalimg;
	public RandomAccessibleInterval<FloatType> CurrentView;
	public RandomAccessibleInterval<IntType> CurrentViewYellowInt;
	public ArrayList<OvalRoi> BudOvalRois;
	public final String NameA;
	public int ndims;
	public MouseListener mvl;
	public MouseListener tvl;
	public MouseMotionListener tvml;
	public HashMap<String, ArrayList<Budpointobject>> AllBudpoints;
	public HashMap<String, ArrayList<Budobject>> AllBuds;
	public HashMap<String, Integer> BudLastTime;
	public BUDDYCostFunction<Budpointobject, Budpointobject> UserchosenCostFunction;
	public BUDDYCostFunction<Budobject, Budobject> BudUserchosenCostFunction;
	public int[] Clickedpoints;
	public HashMap<String, Integer> AccountedT;
	public ArrayList<ValuePair<String, Budpointobject>> Tracklist;
	public HashMap<String, Double> TrackMeanVelocitylist;
	public HashMap<String, Double> TrackMaxVelocitylist;
	public HashMap<Integer, HashMap<Integer,Double>> BudVelocityMap;
	
	
	
	public ArrayList<ValuePair<String, Budobject>> BudTracklist;
	public HashMap<String, ArrayList<BCellobject>> AllBudcells;
	public Overlay overlay;
	public ImagePlus imp;
	public String selectedID;
	public boolean mouseremoved = false;
	public int row;
	public int tablesize;
	public RealLocalizable Refcord;
	public HashMap<String, RealLocalizable> AllRefcords;
	public ArrayList<RealLocalizable> AllBudcenter;
	public ArrayList<RealLocalizable> ChosenBudcenter;
	public HashMap<String, RealLocalizable> SelectedAllRefcords;
	public int thirdDimension;
	public BUDDYTrackModel Globalmodel;
	public BUDDYBudTrackModel BudGlobalModel;
	public int thirdDimensionSize;
	public ImagePlus impA;
	public int rowchoice;
	public Frame jFreeChartFrameRate;
	public int maxframegap = 30;
	public int thirdDimensionslider = 1;
	public int thirdDimensionsliderInit = 1;
	public JProgressBar jpb;
	public MouseMotionListener ml;
	public ImagePlus resultimp;
	public XYSeriesCollection Velocitydataset;
	public ImageJ ij; 
	public JFreeChart chartVelocity;
	public double calibration;
	public double timecal;
	public File saveFile;
	public RandomAccessibleInterval<IntType> SegYelloworiginalimg;
	public RandomAccessibleInterval<IntType> SegRedoriginalimg;
	public RandomAccessibleInterval<IntType> SegGreenoriginalimg;
	public BCellobjectCollection budcells = new BCellobjectCollection();
	public HashMap<Integer,Integer> IDlist = new HashMap<Integer, Integer>();
	public HashMap<String, Budpointobject> Finalresult;
	// Input Bud and its segmentation
		public InteractiveBud(final RandomAccessibleInterval<FloatType> originalimg,
				final RandomAccessibleInterval<IntType> Segoriginalimg,
				final String NameA,final double calibration, final double timecal, String inputstring) {
			
			
			this.originalimg = originalimg;
			this.Segoriginalimg = Segoriginalimg;
			this.NameA = NameA;
			this.calibration = calibration;
			this.timecal = timecal;
			this.ndims = originalimg.numDimensions();
			this.Velocitydataset = new XYSeriesCollection();
			this.jFreeChartFrameRate = utility.BudChartMaker.display(chartVelocity, new Dimension(500, 500));
			this.jFreeChartFrameRate.setVisible(false);
			this.inputstring = inputstring;
			
			
		}
		
		
		// Input RGB and one flourescent channel segmentation images
		public InteractiveBud(final RandomAccessibleInterval<FloatType> originalimg,
				final RandomAccessibleInterval<IntType> Segoriginalimg,
				final RandomAccessibleInterval<IntType> SegYelloworiginalimg,
				final String NameA,final double calibration, final double timecal, String inputstring) {
		
			
			this.originalimg = originalimg;
			this.Segoriginalimg = Segoriginalimg;
			this.SegYelloworiginalimg = SegYelloworiginalimg;
			this.NameA = NameA;
			this.calibration = calibration;
			this.timecal = timecal;
			this.ndims = originalimg.numDimensions();
			this.Velocitydataset = new XYSeriesCollection();
			this.jFreeChartFrameRate = utility.BudChartMaker.display(chartVelocity, new Dimension(500, 500));
			this.jFreeChartFrameRate.setVisible(false);
			this.inputstring = inputstring;
			
		}
		
		// Input RGB and two flourescent channel segmentation images
		public InteractiveBud(final RandomAccessibleInterval<FloatType> originalimg,
				final RandomAccessibleInterval<IntType> Segoriginalimg,
				final RandomAccessibleInterval<IntType> SegYelloworiginalimg,
				final RandomAccessibleInterval<IntType> SegGreenoriginalimg,
				final String NameA,final double calibration, final double timecal, String inputstring) {
		
			
			this.originalimg = originalimg;
			this.Segoriginalimg = Segoriginalimg;
			this.SegYelloworiginalimg = SegYelloworiginalimg;
			this.SegGreenoriginalimg = SegGreenoriginalimg;
			this.NameA = NameA;
			this.calibration = calibration;
			this.timecal = timecal;
			this.ndims = originalimg.numDimensions();
			this.Velocitydataset = new XYSeriesCollection();
			this.jFreeChartFrameRate = utility.BudChartMaker.display(chartVelocity, new Dimension(500, 500));
			this.jFreeChartFrameRate.setVisible(false);
			this.inputstring = inputstring;
			
			
		}
		
		// Input RGB and three flourescent channel segmentation images
		public InteractiveBud(final RandomAccessibleInterval<FloatType> originalimg,
				final RandomAccessibleInterval<IntType> Segoriginalimg,
				final RandomAccessibleInterval<IntType> SegYelloworiginalimg,
				final RandomAccessibleInterval<IntType> SegGreenoriginalimg,
				final RandomAccessibleInterval<IntType> SegRedoriginalimg,
				final String NameA,final double calibration, final double timecal, String inputstring) {
		
			
			this.originalimg = originalimg;
			this.Segoriginalimg = Segoriginalimg;
			this.SegYelloworiginalimg = SegYelloworiginalimg;
			this.SegGreenoriginalimg = SegGreenoriginalimg;
			this.SegRedoriginalimg = SegRedoriginalimg;
			this.NameA = NameA;
			this.calibration = calibration;
			this.timecal = timecal;
			this.ndims = originalimg.numDimensions();
			this.Velocitydataset = new XYSeriesCollection();
			this.jFreeChartFrameRate = utility.BudChartMaker.display(chartVelocity, new Dimension(500, 500));
			this.jFreeChartFrameRate.setVisible(false);
			this.inputstring = inputstring;
			
			
		}
	
	public ImageStack prestack;
	public JTable table;
	public JTableHeader header;
	public static enum ValueChange {
		
		THIRDDIMmouse, All;
		
	}
	
	public void setTime(final int value) {
		thirdDimensionslider = value;
		thirdDimensionsliderInit = 1;
		thirdDimension = 1;
	}
	
	
	public int getTimeMax() {

		return thirdDimensionSize;
	}
	@Override
	public void run(String arg0) {

		
		BudLastTime = new HashMap<String, Integer>();
		AllRefcords = new HashMap<String, RealLocalizable>();
		AllBudcenter = new ArrayList<RealLocalizable>();
		ChosenBudcenter = new ArrayList<RealLocalizable>();
		Finalresult = new HashMap<String, Budpointobject>();
		BudOvalRois = new ArrayList<OvalRoi>();
		BudVelocityMap = new HashMap<Integer, HashMap<Integer,Double>>() ;
		SelectedAllRefcords = new HashMap<String, RealLocalizable>();
		AccountedT = new HashMap<String, Integer>();
		jpb = new JProgressBar();
		nf = NumberFormat.getInstance(Locale.ENGLISH);
		nf.setMaximumFractionDigits(3);
		nf.setGroupingUsed(false);
		Clickedpoints = new int[2];
		pixellist = new HashSet<Integer>();
		Tracklist = new ArrayList<ValuePair<String, Budpointobject>>();
		TrackMeanVelocitylist = new HashMap<String, Double>();
		TrackMaxVelocitylist = new HashMap<String, Double>();
		BudTracklist = new ArrayList<ValuePair<String, Budobject>>();
		AllBudpoints = new HashMap<String, ArrayList<Budpointobject>>(); 
		AllBuds = new HashMap<String, ArrayList<Budobject>>();
		ij = new ImageJ();
		ij.ui().showUI();
		if (ndims == 3) {

			thirdDimension = 1;

			thirdDimensionSize = (int) originalimg.dimension(2);
			AutostartTime = thirdDimension;
			AutoendTime = thirdDimensionSize;
			maxframegap = thirdDimensionSize / 4;
		}
		setTime(thirdDimension);
		CurrentView = utility.BudSlicer.getCurrentBudView(originalimg, thirdDimension, thirdDimensionSize);
		if(SegYelloworiginalimg!=null)
			CurrentViewYellowInt = utility.BudSlicer.getCurrentBudView(SegYelloworiginalimg, thirdDimension, thirdDimensionSize);
		imp = ImageJFunctions.show(CurrentView, "Original Image");
		imp.setTitle("Active Image" + " " + "time point : " + thirdDimension);
		
	
		Cardframe.repaint();
		Cardframe.validate();
		panelFirst.repaint();
		panelFirst.validate();
		saveFile = new java.io.File(".");
		
		
		
		
		StartDisplayer();
		Card();
	}
	
	
	public void updatePreview(final ValueChange change) {
		
		
		
		
		if (overlay == null) {

			overlay = new Overlay();
			imp.setOverlay(overlay);
			
		}
		
		if (change == ValueChange.THIRDDIMmouse)
		{
			imp.setTitle("Active Image" + " " + "time point : " + thirdDimension);
			String TID = Integer.toString( thirdDimension);
			AccountedT.put(TID,  thirdDimension);
			CurrentView = utility.BudSlicer.getCurrentBudView(originalimg, thirdDimension, thirdDimensionSize);
			if(SegYelloworiginalimg!=null)
				CurrentViewYellowInt = utility.BudSlicer.getCurrentBudView(SegYelloworiginalimg, thirdDimension, thirdDimensionSize);
		repaintView(CurrentView);
		
		if(CovistoKalmanPanel.Skeletontime.isEnabled()) {
			imp.getOverlay().clear();
			imp.updateAndDraw();
			//CovistoKalmanPanel.Skeletontime.setEnabled(false);
			//CovistoKalmanPanel.Timetrack.setEnabled(false);
			StartDisplayer();
			
		}
		
		}
		
	}
	
	
	public void StartDisplayer() {
		
		
		
		ComputeBorder display = new ComputeBorder(this, jpb);
		
		display.execute();
	}
	
	public void repaintView( RandomAccessibleInterval<FloatType> Activeimage) {
		
		
		
		if (imp == null || !imp.isVisible()) {
			imp = ImageJFunctions.show(Activeimage);

		}

		else {
			try {
				final float[] pixels = (float[]) imp.getProcessor().getPixels();
				
				final Cursor<FloatType> c = Views.iterable(Activeimage).cursor();

				for (int i = 0; i < pixels.length; ++i)
					pixels[i] = c.next().get();

			} catch (Exception e) {

				final float[] pixels = (float[]) imp.getProcessor().getPixels();
				final Cursor<FloatType> c = Views.iterable(Activeimage).cursor();

			
				for (int i = 0; i < pixels.length; ++i)
					pixels[i] = c.next().get();

			   
			}
			
			
			imp.updateAndDraw();

		}

	}
	
	
	
	public JFrame Cardframe = new JFrame("Bud n Cell Tracker");
	public JPanel panelFirst = new JPanel();
	public JPanel Original = new JPanel();
	public JPanel PanelSelectFile = new JPanel();
	public JPanel Timeselect = new JPanel();
	public JPanel KalmanPanel = new JPanel();
	public JPanel panelCont = new JPanel();
	public final Insets insets = new Insets(10, 0, 0, 0);
	public final GridBagLayout layout = new GridBagLayout();
	public final GridBagConstraints c = new GridBagConstraints();
	public JScrollPane scrollPane;
	public JFileChooser chooserA = new JFileChooser();
	public String choosertitleA;
	int SizeX = 400;
	int SizeY = 200;
	public Border selectfile = new CompoundBorder(new TitledBorder("Select Track"), new EmptyBorder(c.insets));
	public Label autoTstart, autoTend;
	public TextField startT, endT;
	public Label timeText = new Label("Current T = " + 1, Label.CENTER);
	
	public Label explain = new Label("Left click selcts/deselects buds (Label 1 chosen by default)" , Label.CENTER);
	
	public Label thirdexplain = new Label("Press Esc on active image to stop calculation" , Label.CENTER);
	public Label fourthexplain = new Label("Click Skeletonize buddies after selection" , Label.CENTER);
	public String timestring = "Current T";
	int textwidth = 5;
	public int AutostartTime, AutoendTime;
	public TextField inputFieldT;
	public JScrollBar timeslider = new JScrollBar(Scrollbar.HORIZONTAL, thirdDimensionsliderInit, 10, 0,
			scrollbarSize + 10);
	public JButton Savebutton = new JButton("Save Track");
	public JButton Cellbutton = new JButton("Enter BTrackmate");
	public JButton Restartbutton = new JButton("Restart");
	public JButton Batchbutton = new JButton("Save Parameters for batch mode and exit");
	public JButton SaveAllbutton = new JButton("Save All Tracks");
	public Border timeborder = new CompoundBorder(new TitledBorder("Select time"), new EmptyBorder(c.insets));
	public Label inputtrackLabel;
	public TextField inputtrackField;
	public Border selectcell = new CompoundBorder(new TitledBorder("Select Cell"), new EmptyBorder(c.insets));
	public TextField inputField = new TextField();
	public Border origborder = new CompoundBorder(new TitledBorder("Results files"),
			new EmptyBorder(c.insets));
	public final JButton ChooseDirectory = new JButton("Choose Directory to save results in");
	public void Card() {
		
	
		Cellbutton.setEnabled(false);
		CardLayout cl = new CardLayout();

		c.insets = new Insets(5, 5, 5, 5);
		
		c.anchor = GridBagConstraints.BOTH;
		c.ipadx = 35;

		c.gridwidth = 10;
		c.gridheight = 10;
		c.gridy = 1;
		c.gridx = 0;
		panelCont.setLayout(cl);

		panelCont.add(panelFirst, "1");
		
		inputtrackLabel = new Label("Enter trackID to save");
		inputtrackField = new TextField(textwidth);
		
		Object[] colnames = new Object[] { "Track Id", "Location X", "Location Y", "Location T", "Growth Rate" };

		Object[][] rowvalues = new Object[0][colnames.length];
		if (Finalresult != null && Finalresult.size() > 0) {

			rowvalues = new Object[Finalresult.size()][colnames.length];

		}
		/*if (Tracklist != null && Tracklist.size() > 0) {
			rowvalues = new Object[Tracklist.size()][colnames.length];
		}
		*/
		
		table = new JTable(rowvalues, colnames);
		header  = table.getTableHeader();
		table.setFillsViewportHeight(true);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		
		autoTend = new Label("End time for tracking");
		endT = new TextField(textwidth);
		endT.setText(Integer.toString(AutoendTime));
		
		
		
		scrollPane = new JScrollPane(table);
		Original.setLayout(layout);
		scrollPane.getViewport().add(table);
		scrollPane.setAutoscrolls(true);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		PanelSelectFile.add(scrollPane, BorderLayout.CENTER);

		PanelSelectFile.setBorder(selectfile);
		int size = 100;
		table.getColumnModel().getColumn(0).setPreferredWidth(size);
		table.getColumnModel().getColumn(1).setPreferredWidth(size);
		table.getColumnModel().getColumn(2).setPreferredWidth(size);
		table.getColumnModel().getColumn(3).setPreferredWidth(size);
		table.getColumnModel().getColumn(4).setPreferredWidth(size);
		table.setPreferredScrollableViewportSize(table.getPreferredSize());
		table.setFillsViewportHeight(true);
		table.isOpaque();
		scrollPane.setMinimumSize(new Dimension(300, 200));
		scrollPane.setPreferredSize(new Dimension(300, 200));
		
		panelFirst.setLayout(layout);
		overlay = imp.getOverlay();
		if (overlay == null) {

			overlay = new Overlay();
			imp.setOverlay(overlay);

		}

		autoTstart = new Label("Start time for tracking");
		startT = new TextField(textwidth);
		startT.setText(Integer.toString(AutostartTime));

		autoTend = new Label("End time for tracking");
		endT = new TextField(textwidth);
		endT.setText(Integer.toString(AutoendTime));
		
		Timeselect.setLayout(layout);
		inputFieldT = new TextField(textwidth);
		inputFieldT.setText(Integer.toString(thirdDimension));
		
		
		// Put time slider

		Timeselect.add(timeText, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		Timeselect.add(timeslider, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		Timeselect.add(inputFieldT, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));
		Timeselect.add(autoTend, new GridBagConstraints(2, 0, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
		Timeselect.add(endT, new GridBagConstraints(2, 2, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
		
		
		Timeselect.add(explain, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, insets, 0, 0));
		Timeselect.add(thirdexplain, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, insets, 0, 0));
		Timeselect.add(fourthexplain, new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, insets, 0, 0));
		Timeselect.setBorder(timeborder);
		
		panelFirst.add(Timeselect, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, insets, 0, 0));
       
		KalmanPanel = CovistoKalmanPanel.KalmanPanel();
		
		panelFirst.add(KalmanPanel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
		
		if(SegYelloworiginalimg!=null || SegRedoriginalimg!=null || SegGreenoriginalimg!=null)
		panelFirst.add(Cellbutton, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
		
		panelFirst.add(PanelSelectFile, new GridBagConstraints(3, 0, 2, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
		
		//Original.add(inputLabel, new GridBagConstraints(0, 3, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
		//		GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));

		//Original.add(inputField, new GridBagConstraints(0, 4, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
		//		GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));

		Original.add(inputtrackLabel, new GridBagConstraints(0, 5, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));

		Original.add(inputtrackField, new GridBagConstraints(0, 6, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));

		Original.add(ChooseDirectory, new GridBagConstraints(0, 7, 3, 1, 0.0, 0.0, GridBagConstraints.NORTH,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
		Original.add(Savebutton, new GridBagConstraints(0, 8, 3, 1, 0.0, 0.0, GridBagConstraints.NORTH,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));

		Original.add(SaveAllbutton, new GridBagConstraints(0, 9, 3, 1, 0.0, 0.0, GridBagConstraints.NORTH,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));

		Original.setBorder(origborder);
		
		
		panelFirst.add(Original, new GridBagConstraints(3, 1, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		//panelFirst.add(Batchbutton, new GridBagConstraints(3, 2, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
		//		GridBagConstraints.HORIZONTAL, insets, 0, 0));
		
		
		inputFieldT.addTextListener(new BudTlocListener(this,false));
		
		timeslider.addAdjustmentListener(new BudTimeListener(this, timeText, timestring, thirdDimensionsliderInit,
				thirdDimensionSize, scrollbarSize, timeslider));
		endT.addTextListener(new BTrackAutoEndListener(this));
		
		CovistoKalmanPanel.Skeletontime.addActionListener(new BudSkeletonListener(this));
		CovistoKalmanPanel.Restart.addActionListener(new BudRestartListener(this));
		CovistoKalmanPanel.Timetrack.addActionListener(new BudLinkobjectListener(this));
		CovistoKalmanPanel.lostframe.addTextListener(new BudPRELostFrameListener(this));
		CovistoKalmanPanel.tracklength.addTextListener(new BudSkeletonTrackLengthListener(this));

		
		CovistoKalmanPanel.maxSearchKalman.addAdjustmentListener(new BudPREMaxSearchTListener(this,
				CovistoKalmanPanel.maxSearchTextKalman, CovistoKalmanPanel.maxSearchstringKalman,
				CovistoKalmanPanel.maxSearchradiusMin, CovistoKalmanPanel.maxSearchradiusMax,
				CovistoKalmanPanel.scrollbarSize, CovistoKalmanPanel.maxSearchSS));
		CovistoKalmanPanel.setInitialsearchradius((float) (CovistoKalmanPanel.initialSearchradius/calibration));
		
		CovistoKalmanPanel.initialSearchS.addAdjustmentListener(new BudPREIniSearchListener(this,
				CovistoKalmanPanel.iniSearchText, CovistoKalmanPanel.initialSearchstring,
				CovistoKalmanPanel.initialSearchradiusMin, CovistoKalmanPanel.initialSearchradiusMax,
				CovistoKalmanPanel.scrollbarSize, CovistoKalmanPanel.initialSearchS));
		CovistoKalmanPanel.alphaS.addAdjustmentListener(new BudAlphaListener(this, CovistoKalmanPanel.alphaText, CovistoKalmanPanel.alphastring,
				CovistoKalmanPanel.alphaMin, CovistoKalmanPanel.alphaMax, CovistoKalmanPanel.scrollbarSize, CovistoKalmanPanel.alphaS));
		CovistoKalmanPanel.setInitialAlpha(CovistoKalmanPanel.alphaInit);
		KalmanPanel.validate();
		KalmanPanel.repaint();
		inputtrackField.addTextListener(new BudTrackidListener(this));
		Batchbutton.addActionListener(new BudSaveBatchListener(this));
		inputField.addTextListener(new BTrackFilenameListener(this));
		Savebutton.addActionListener(new BudSaveListener(this));
		Cellbutton.addActionListener(new BudMastadonListener(this));
		SaveAllbutton.addActionListener(new BudSaveAllListener(this));
		ChooseDirectory.addActionListener(new BTrackSaveDirectoryListener(this));
		inputField.addTextListener(new BTrackFilenameListener(this));
		
		panelFirst.setVisible(true);
		cl.show(panelCont, "1");
		Cardframe.add(panelCont, "Center");
		Cardframe.add(jpb, "Last");

		Cardframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		Cardframe.pack();
		Cardframe.setVisible(true);

		imp.getCanvas().addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == 27)
					EscapePressed = true;
				
			}
			
			
			
			
			
		});
		
		
		
		
	}
	
	
	public Boolean EscapePressed = false;

}
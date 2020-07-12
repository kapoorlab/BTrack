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

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
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

import Buddy.plugin.trackmate.BCellobjectCollection;
import budDetector.BCellobject;
import budDetector.Budobject;
import budDetector.Budpointobject;
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
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.roi.labeling.ImgLabeling;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.ValuePair;
import net.imglib2.view.Views;
import pluginTools.InteractiveBud.ValueChange;
import tracker.BUDDYBudTrackModel;
import tracker.BUDDYCostFunction;
import tracker.BUDDYTrackModel;
import tracker.GREENTrackModel;

public class InteractiveGreen  extends JPanel implements PlugIn{

	
	private static final long serialVersionUID = 1L;
	public String usefolder = IJ.getDirectory("imagej");
	public String addToName = "BTrack_";
	public String inputstring;
	public final int scrollbarSize = 1000;
	public Set<Integer> pixellist;
	public NumberFormat nf;
	public Color Drawcolor; 
	public RandomAccessibleInterval<FloatType> originalimg;
	public RandomAccessibleInterval<IntType> Segoriginalimg;
	public RandomAccessibleInterval<BitType> Maskimg;
	public RandomAccessibleInterval<FloatType> CurrentView;
	public RandomAccessibleInterval<IntType> CurrentViewInt;
	public RandomAccessibleInterval<BitType> CurrentViewMaskInt;
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
	public HashMap<String, Integer> AccountedZ;
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
	public int fourthDimension;
	public GREENTrackModel Globalmodel;
	public GREENTrackModel BudGlobalModel;
	public int thirdDimensionSize;
	public int fourthDimensionSize;
	public ImagePlus impA;
	public int rowchoice;
	public Frame jFreeChartFrameRate;
	public int maxframegap = 30;
	
	public int thirdDimensionslider = 1;
	public int thirdDimensionsliderInit = 1;
	public int fourthDimensionslider = 1;
	public int fourthDimensionsliderInit = 1;
	
	public JProgressBar jpb;
	public MouseMotionListener ml;
	public ImagePlus resultimp;
	public ImageJ ij; 
	public double calibrationX;
	public double calibrationY;
	public double calibrationZ;
	public double timecal;
	public File saveFile;
	public RandomAccessibleInterval<IntType> SegGreenoriginalimg;
	public BCellobjectCollection budcells = new BCellobjectCollection();
	public HashMap<Integer,Integer> IDlist = new HashMap<Integer, Integer>();
	public HashMap<String, Budpointobject> Finalresult;
	
	// Input Green and its segmentation
		public InteractiveGreen(final RandomAccessibleInterval<FloatType> originalimg,
				final RandomAccessibleInterval<IntType> Segoriginalimg,
				final RandomAccessibleInterval<BitType> Maskimg,
				final String NameA,final double calibrationX, double calibrationY, double calibrationZ, final double timecal, String inputstring) {
			
			
			this.originalimg = originalimg;
			this.Segoriginalimg = Segoriginalimg;
			this.Maskimg = Maskimg;
			this.NameA = NameA;
			this.calibrationX = calibrationX;
			this.calibrationY = calibrationY;
			this.calibrationZ = calibrationZ;
			this.timecal = timecal;
			this.ndims = originalimg.numDimensions();
			this.inputstring = inputstring;
			
			
		}
		
		
		
	public JTable table;
	public JTableHeader header;
	public static enum ValueChange {
		
		THIRDDIMmouse, FOURTHDIMmouse, All;
		
	}
	
	public void setTime(final int value) {
		thirdDimensionslider = value;
		thirdDimensionsliderInit = 1;
		thirdDimension = 1;
	}
	
	public void setZ(final int value) {
		fourthDimensionslider = value;
		fourthDimensionsliderInit = 1;
		fourthDimension = 1;
	}
	
	
	public int getTimeMax() {

		return thirdDimensionSize;
	}
	
	public int getZMax() {

		return fourthDimensionSize;
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
		AccountedZ = new HashMap<String, Integer>();
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
		
		if (ndims == 4) {

			thirdDimension = 1;

			thirdDimensionSize = (int) originalimg.dimension(2);
			AutostartTime = thirdDimension;
			AutoendTime = thirdDimensionSize;
			maxframegap = thirdDimensionSize / 4;
			
			
			fourthDimension = 1;
			fourthDimensionSize =  (int) originalimg.dimension(3);
			
		}
		
		
		setTime(thirdDimension);
		setZ(fourthDimension);
		
		CurrentView = utility.BudSlicer.getCurrentBudView(originalimg, thirdDimension, thirdDimensionSize, fourthDimension, fourthDimensionSize);
		CurrentViewInt = utility.BudSlicer.getCurrentBudView(Segoriginalimg, thirdDimension, thirdDimensionSize, fourthDimension, fourthDimensionSize);
		if(Maskimg!=null)
			CurrentViewMaskInt = utility.BudSlicer.getCurrentBudView(Maskimg, thirdDimension, thirdDimensionSize, fourthDimension, fourthDimensionSize);
		imp = ImageJFunctions.show(CurrentView, "Original Image");
		imp.setTitle("Active Image" + " " + "time point : " + thirdDimension);
		
	
		Cardframe.repaint();
		Cardframe.validate();
		panelFirst.repaint();
		panelFirst.validate();
		saveFile = new java.io.File(".");
		//Get Labelled images
		
		  long[] dims = new long[Segoriginalimg.numDimensions()];
      // get image dimension
		 Segoriginalimg.dimensions(dims);
      // create labeling index image
       RandomAccessibleInterval<IntType> indexImg = ArrayImgs.ints(dims);
       ImgLabeling<Integer, IntType> labeling = new ImgLabeling<>(indexImg);
       Iterator<Integer> labels = new Iterator<Integer>()
      {
          private int i = 1;

          @Override
          public boolean hasNext()
          {
              return true;
          }

          @Override
          public Integer next()
          {
              return i++;
          }

          @Override
          public void remove()
          {}
      };
      
      ConnectedComponents.labelAllConnectedComponents(Segoriginalimg, labeling, labels, StructuringElement.FOUR_CONNECTED);
      
		Segoriginalimg = labeling.getIndexImg();
		
		if(Segoriginalimg!=null) {
			
			
			
			  dims = new long[Segoriginalimg.numDimensions()];
		        // get image dimension
			  Segoriginalimg.dimensions(dims);
		        // create labeling index image
		         indexImg = ArrayImgs.ints(dims);
		        labeling = new ImgLabeling<>(indexImg);
		        labels = new Iterator<Integer>()
		        {
		            private int i = 1;

		            @Override
		            public boolean hasNext()
		            {
		                return true;
		            }

		            @Override
		            public Integer next()
		            {
		                return i++;
		            }

		            @Override
		            public void remove()
		            {}
		        };
		        
		        ConnectedComponents.labelAllConnectedComponents(Segoriginalimg, labeling, labels, StructuringElement.FOUR_CONNECTED);
		        
		        Segoriginalimg = labeling.getIndexImg();
			
		}
		
		
		if(SegGreenoriginalimg!=null) {
			
			
			
			  dims = new long[SegGreenoriginalimg.numDimensions()];
		        // get image dimension
			  SegGreenoriginalimg.dimensions(dims);
		        // create labeling index image
		         indexImg = ArrayImgs.ints(dims);
		        labeling = new ImgLabeling<>(indexImg);
		        labels = new Iterator<Integer>()
		        {
		            private int i = 1;

		            @Override
		            public boolean hasNext()
		            {
		                return true;
		            }

		            @Override
		            public Integer next()
		            {
		                return i++;
		            }

		            @Override
		            public void remove()
		            {}
		        };
		        
		        ConnectedComponents.labelAllConnectedComponents(SegGreenoriginalimg, labeling, labels, StructuringElement.FOUR_CONNECTED);
		        
		        SegGreenoriginalimg = labeling.getIndexImg();
			
		}
		
		
		
		
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
			imp.setTitle("Active Image" + " " + "time point : " + thirdDimension + " " + "z point : "  + fourthDimension);
			String TID = Integer.toString( thirdDimension);
			String ZID = Integer.toString( fourthDimension);
			AccountedT.put(TID,  thirdDimension);
			AccountedZ.put(ZID,  fourthDimension);
			CurrentView = utility.BudSlicer.getCurrentBudView(originalimg, thirdDimension, thirdDimensionSize, fourthDimension, fourthDimensionSize);
			CurrentViewInt = utility.BudSlicer.getCurrentBudView(Segoriginalimg, thirdDimension, thirdDimensionSize, fourthDimension, fourthDimensionSize);
			if(Maskimg!=null)
				CurrentViewMaskInt = utility.BudSlicer.getCurrentBudView(Maskimg, thirdDimension, thirdDimensionSize, fourthDimension, fourthDimensionSize);
		repaintView(CurrentView);
		
		
		
		if(CovistoKalmanPanel.Skeletontime.isEnabled()) {
			imp.getOverlay().clear();
			imp.updateAndDraw();
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
	public Label autoTstart, autoTend, autoZstart, autoZend;
	public TextField startT, endT, startZ, endZ;
	public Label timeText = new Label("Current T = " + 1, Label.CENTER);
	public Label zText = new Label("Current Z = " + 1, Label.CENTER);
	
	public String timestring = "Current T";
	public String Zstring = "Current Z";
	int textwidth = 5;
	public int AutostartTime, AutoendTime;
	public TextField inputFieldT, inputFieldZ;
	public JScrollBar timeslider = new JScrollBar(Scrollbar.HORIZONTAL, thirdDimensionsliderInit, 10, 0,
			scrollbarSize + 10);
	public JScrollBar Zslider = new JScrollBar(Scrollbar.HORIZONTAL, fourthDimensionsliderInit, 10, 0,
			scrollbarSize + 10);
	public JButton Savebutton = new JButton("Save Track");
	public JButton Cellbutton = new JButton("Enter BTrackmate");
	public JButton Restartbutton = new JButton("Restart");
	public JButton SaveAllbutton = new JButton("Save All Tracks");
	public Border timeborder = new CompoundBorder(new TitledBorder("Select time and Z"), new EmptyBorder(c.insets));
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
		
		
		Original.setLayout(layout);

		PanelSelectFile.setBorder(selectfile);
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
		

		autoZstart = new Label("Start Z for object linking");
		startZ = new TextField(textwidth);
		startZ.setText(Integer.toString(AutostartZ));

		autoZend = new Label("End Z for linking");
		endZ = new TextField(textwidth);
		endZ.setText(Integer.toString(AutoendZ));
		
		Zselect.setLayout(layout);
		inputFieldZ = new TextField(textwidth);
		inputFieldZ.setText(Integer.toString(fourthDimension));
		
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
		
		// Put Z slider

		Zselect.add(ZText, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		Zselect.add(Zslider, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		Zselect.add(inputFieldZ, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));
		Zselect.add(autZend, new GridBagConstraints(3, 1, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
		Zselect.add(endZ, new GridBagConstraints(3, 3, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
		
		
		
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
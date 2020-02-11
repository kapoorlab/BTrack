package pluginTools;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Scrollbar;
import java.awt.TextField;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

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

import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeriesCollection;

import budDetector.Budobject;
import budDetector.Budpointobject;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.Overlay;
import ij.plugin.PlugIn;
import kalmanGUI.CovistoKalmanPanel;
import listeners.LinkobjectListener;
import listeners.PREIniSearchListener;
import listeners.PRELostFrameListener;
import listeners.PREMaxSearchTListener;
import listeners.TimeListener;
import net.imagej.ImageJ;
import net.imglib2.Cursor;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import net.imglib2.view.Views;
import tracker.CostFunction;
import tracker.TrackModel;
import zGUI.CovistoZselectPanel;

public class InteractiveBud  extends JPanel implements PlugIn{

	
	private static final long serialVersionUID = 1L;
	public String usefolder = IJ.getDirectory("imagej");
	public String addToName = "BTrack_";
	public final int scrollbarSize = 1000;
	public Set<Integer> pixellist;
	public NumberFormat nf;
	public RandomAccessibleInterval<FloatType> originalimg;
	public RandomAccessibleInterval<FloatType> originalSecimg;
	public RandomAccessibleInterval<IntType> Segoriginalimg;
	public RandomAccessibleInterval<FloatType> SegSecoriginalimg;
	public RandomAccessibleInterval<FloatType> CurrentView;
	public final String NameA;
	public final String NameB;
	public int ndims;
	public MouseListener mvl;
	public HashMap<String, ArrayList<Budpointobject>> AllBudpoints;
	public CostFunction<Budpointobject, Budpointobject> UserchosenCostFunction;
	public int[] Clickedpoints;
	public HashMap<String, Integer> AccountedT;
	public ArrayList<ValuePair<String, Budpointobject>> Tracklist;
	public Overlay overlay;
	public ImagePlus imp;
	public int row;
	public int tablesize;
	public RealLocalizable Refcord;
	public HashMap<String, RealLocalizable> AllRefcords;
	public HashMap<String, Budpointobject> Finalresult;
	public int thirdDimension;
	public TrackModel Globalmodel;
	public int thirdDimensionSize;
	public ImagePlus impA;
	public int rowchoice;
	public Frame jFreeChartFrameRate;
	public int maxframegap = 30;
	public int thirdDimensionslider = 1;
	public int thirdDimensionsliderInit = 1;
	public JProgressBar jpb;
	public JFreeChart chartRate;
	public MouseMotionListener ml;
	public ImagePlus resultimp;
	public XYSeriesCollection Velocitydataset;
	public ImageJ ij; 
	public JFreeChart chartVelocity;
	public InteractiveBud(final RandomAccessibleInterval<FloatType> originalimg,
			final RandomAccessibleInterval<FloatType> originalSecimg,
			final RandomAccessibleInterval<IntType> Segoriginalimg,
			final RandomAccessibleInterval<FloatType> SegSecoriginalimg,
			final String NameA,
			final String NameB) {
		
		
		this.originalimg = originalimg;
		this.originalSecimg = originalSecimg;
		this.Segoriginalimg = Segoriginalimg;
		this.SegSecoriginalimg = SegSecoriginalimg;
		this.NameA = NameA;
		this.NameB = NameB;
		this.ndims = originalimg.numDimensions();
		this.Velocitydataset = new XYSeriesCollection();
		this.jFreeChartFrameRate = utility.ChartMaker.display(chartRate, new Dimension(500, 500));
		this.jFreeChartFrameRate.setVisible(false);
		
		
		
	}
	
	
	public ImageStack prestack;
	public JTable table;
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

		AllRefcords = new HashMap<String, RealLocalizable>();
		AccountedT = new HashMap<String, Integer>();
		jpb = new JProgressBar();
		nf = NumberFormat.getInstance(Locale.ENGLISH);
		nf.setMaximumFractionDigits(3);
		nf.setGroupingUsed(false);
		Clickedpoints = new int[2];
		Finalresult = new HashMap<String, Budpointobject>();
		pixellist = new HashSet<Integer>();
		Tracklist = new ArrayList<ValuePair<String, Budpointobject>>();
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
		CurrentView = utility.Slicer.getCurrentView(originalimg, thirdDimension, thirdDimensionSize);
		
		imp = ImageJFunctions.show(CurrentView, "Original Image");
		imp.setTitle("Active Image" + " " + "time point : " + thirdDimension);
		
		
		Cardframe.repaint();
		Cardframe.validate();
		panelFirst.repaint();
		panelFirst.validate();
		StartDisplayer();
		Card();
	}
	
	
	public void updatePreview(final ValueChange change) {
		
		
		
		StartDisplayer();
		if (overlay == null) {

			overlay = new Overlay();
			imp.setOverlay(overlay);
			
		}
		
		if (change == ValueChange.THIRDDIMmouse)
		{
			
			String TID = Integer.toString( thirdDimension);
			AccountedT.put(TID,  thirdDimension);
			
		repaintView(imp, CurrentView);
		StartDisplayer();
		System.out.println("repainting");
		}
		
	}
	
	
	public void StartDisplayer() {
		
		ComputeBorder display = new ComputeBorder(this, jpb);
		
		display.execute();
	}
	
	public void repaintView(ImagePlus Activeimp, RandomAccessibleInterval<FloatType> Activeimage) {
		
		
		overlay.clear();
		
		if (Activeimp == null || !Activeimp.isVisible()) {
			Activeimp = ImageJFunctions.show(Activeimage);

		}

		else {

			final float[] pixels = (float[]) Activeimp.getProcessor().getPixels();
			final Cursor<FloatType> c = Views.iterable(Activeimage).cursor();

			for (int i = 0; i < pixels.length; ++i)
				pixels[i] = c.next().get();

			Activeimp.updateAndDraw();

		}

	}
	
	public JFrame Cardframe = new JFrame("Bud n Cell Tracker");
	public JPanel panelFirst = new JPanel();
	
	public JPanel PanelSelectFile = new JPanel();
	public JPanel Timeselect = new JPanel();
	public JPanel KalmanPanel = new JPanel();
	public JPanel panelCont = new JPanel();
	public final Insets insets = new Insets(10, 0, 0, 0);
	public final GridBagLayout layout = new GridBagLayout();
	public final GridBagConstraints c = new GridBagConstraints();
	public JScrollPane scrollPane;
	int SizeX = 400;
	int SizeY = 200;
	public Border selectfile = new CompoundBorder(new TitledBorder("Select Track"), new EmptyBorder(c.insets));
	public Label autoTstart, autoTend;
	public TextField startT, endT;
	public Label timeText = new Label("Current T = " + 1, Label.CENTER);
	public String timestring = "Current T";
	int textwidth = 5;
	public int AutostartTime, AutoendTime;
	public TextField inputFieldT;
	public JScrollBar timeslider = new JScrollBar(Scrollbar.HORIZONTAL, thirdDimensionsliderInit, 10, 0,
			scrollbarSize + 10);
	
	public Border timeborder = new CompoundBorder(new TitledBorder("Select time"), new EmptyBorder(c.insets));
	
	public void Card() {
		
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
		
		
		
		
		Object[] colnames = new Object[] { "Track Id", "Location X", "Location Y", "Location T", "Growth Rate" };

		Object[][] rowvalues = new Object[0][colnames.length];
		
		if (Finalresult != null && Finalresult.size() > 0) {

			rowvalues = new Object[Finalresult.size()][colnames.length];

		}
		
		table = new JTable(rowvalues, colnames);
		table.setFillsViewportHeight(true);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		
		scrollPane = new JScrollPane(table);

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

		autoTstart = new Label("Start time for automation");
		startT = new TextField(textwidth);
		startT.setText(Integer.toString(AutostartTime));

		autoTend = new Label("End time for automation");
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

		Timeselect.setBorder(timeborder);
		
		panelFirst.add(Timeselect, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, insets, 0, 0));

		KalmanPanel = CovistoKalmanPanel.KalmanPanel();
		
		panelFirst.add(KalmanPanel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
		
		panelFirst.add(PanelSelectFile, new GridBagConstraints(3, 0, 2, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
		
		timeslider.addAdjustmentListener(new TimeListener(this, timeText, timestring, thirdDimensionsliderInit,
				thirdDimensionSize, scrollbarSize, timeslider));
		CovistoKalmanPanel.Timetrack.addActionListener(new LinkobjectListener(this));
		CovistoKalmanPanel.lostframe.addTextListener(new PRELostFrameListener(this));
		
		CovistoKalmanPanel.maxSearchKalman.addAdjustmentListener(new PREMaxSearchTListener(this,
				CovistoKalmanPanel.maxSearchTextKalman, CovistoKalmanPanel.maxSearchstringKalman,
				CovistoKalmanPanel.maxSearchradiusMin, CovistoKalmanPanel.maxSearchradiusMax,
				CovistoKalmanPanel.scrollbarSize, CovistoKalmanPanel.maxSearchSS));
		CovistoKalmanPanel.initialSearchS.addAdjustmentListener(new PREIniSearchListener(this,
				CovistoKalmanPanel.iniSearchText, CovistoKalmanPanel.initialSearchstring,
				CovistoKalmanPanel.initialSearchradiusMin, CovistoKalmanPanel.initialSearchradiusMax,
				CovistoKalmanPanel.scrollbarSize, CovistoKalmanPanel.initialSearchS));
		panelFirst.setVisible(true);
		cl.show(panelCont, "1");
		Cardframe.add(panelCont, "Center");
		Cardframe.add(jpb, "Last");

		Cardframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		Cardframe.pack();
		Cardframe.setVisible(true);

	}
	

}

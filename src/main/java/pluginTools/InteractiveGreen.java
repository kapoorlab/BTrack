package pluginTools;

import java.awt.Color;
import java.awt.Frame;
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

import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;

import Buddy.plugin.trackmate.BCellobjectCollection;
import budDetector.BCellobject;
import budDetector.Budpointobject;
import ij.IJ;
import ij.ImagePlus;
import ij.gui.OvalRoi;
import ij.gui.Overlay;
import ij.plugin.PlugIn;
import net.imagej.ImageJ;
import net.imglib2.Cursor;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.algorithm.labeling.ConnectedComponents;
import net.imglib2.algorithm.labeling.ConnectedComponents.StructuringElement;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.roi.labeling.ImgLabeling;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.ValuePair;
import net.imglib2.view.Views;
import tracker.BUDDYBudTrackModel;
import tracker.BUDDYTrackModel;

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
	public RandomAccessibleInterval<IntType> Maskimg;
	public RandomAccessibleInterval<FloatType> CurrentView;
	public RandomAccessibleInterval<IntType> CurrentViewInt;
	public RandomAccessibleInterval<IntType> CurrentViewMaskInt;
	public ArrayList<OvalRoi> BudOvalRois;
	public final String NameA;
	public int ndims;
	public MouseListener mvl;
	public MouseListener tvl;
	public MouseMotionListener tvml;
	
	public ArrayList<ValuePair<String, BCellobject>> GreenTracklist;
	public HashMap<String, ArrayList<BCellobject>> AllGreencells;
	public Overlay overlay;
	public ImagePlus imp;
	public String selectedID;
	public int row;
	public int tablesize;
	public RealLocalizable Refcord;
	public int thirdDimension;
	public int fourthDimension;
	public BUDDYTrackModel Globalmodel;
	public BUDDYBudTrackModel BudGlobalModel;
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
	public BCellobjectCollection Greencells = new BCellobjectCollection();
	public HashMap<Integer,Integer> IDlist = new HashMap<Integer, Integer>();
	public HashMap<String, Budpointobject> Finalresult;
	
	// Input Green and its segmentation Time is the fourth dimension
		public InteractiveGreen(final RandomAccessibleInterval<FloatType> originalimg,
				final RandomAccessibleInterval<IntType> Segoriginalimg,
				final RandomAccessibleInterval<IntType> Maskimg,
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
	
	public void setZ(final int value) {
		thirdDimensionslider = value;
		thirdDimensionsliderInit = 1;
		thirdDimension = 1;
	}
	
	public void setTime(final int value) {
		fourthDimensionslider = value;
		fourthDimensionsliderInit = 1;
		fourthDimension = 1;
	}
	
	
	public int getZMax() {

		return thirdDimensionSize;
	}
	
	public int getTimeMax() {

		return fourthDimensionSize;
	}
	
	@Override
	public void run(String arg0) {

		
		jpb = new JProgressBar();
		nf = NumberFormat.getInstance(Locale.ENGLISH);
		nf.setMaximumFractionDigits(3);
		nf.setGroupingUsed(false);
		pixellist = new HashSet<Integer>();
		ij = new ImageJ();
		ij.ui().showUI();
	
		
		setZ(thirdDimension);
		setTime(fourthDimension);
		
		CurrentView = utility.BudSlicer.getCurrentGreenView(originalimg, thirdDimension, thirdDimensionSize, fourthDimension, fourthDimensionSize);
		CurrentViewInt = utility.BudSlicer.getCurrentGreenView(Segoriginalimg, thirdDimension, thirdDimensionSize, fourthDimension, fourthDimensionSize);
		if(Maskimg!=null)
			CurrentViewMaskInt = utility.BudSlicer.getCurrentGreenView(Maskimg, thirdDimension, thirdDimensionSize, fourthDimension, fourthDimensionSize);
		imp = ImageJFunctions.show(CurrentView, "Original Image");
		imp.setTitle("Active Image" + " " + "time point : " + thirdDimension);
		
	
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
		
		
		

		System.out.println("Cell collector");
		CellCollector();
	}
	
	
	public void updatePreview(final ValueChange change) {
		
		
		
		
		if (overlay == null) {

			overlay = new Overlay();
			imp.setOverlay(overlay);
			
		}
		
		if (change == ValueChange.THIRDDIMmouse)
		{
			imp.setTitle("Active Image" + " " + "time point : " + thirdDimension );
			CurrentView = utility.BudSlicer.getCurrentGreenView(originalimg, thirdDimension, thirdDimensionSize, fourthDimension, fourthDimensionSize);
			CurrentViewInt = utility.BudSlicer.getCurrentGreenView(Segoriginalimg, thirdDimension, thirdDimensionSize, fourthDimension, fourthDimensionSize);
			if(Maskimg!=null)
				CurrentViewMaskInt = utility.BudSlicer.getCurrentGreenView(Maskimg, thirdDimension, thirdDimensionSize, fourthDimension, fourthDimensionSize);
		repaintView(CurrentView);
		
		
		
		
		
		}
		
	}
	
	
	public void CellCollector() {
		
		
		
		CollectGreenCells display = new CollectGreenCells(this, jpb);
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
	
	
	

}
package pluginTools;

import java.awt.CardLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Scrollbar;
import java.awt.TextField;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.Overlay;
import ij.plugin.PlugIn;
import listeners.TimeListener;
import net.imglib2.Cursor;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;

public class InteractiveBud  extends JPanel implements PlugIn{

	
	private static final long serialVersionUID = 1L;
	public String usefolder = IJ.getDirectory("imagej");
	public String addToName = "BTrack_";
	public final int scrollbarSize = 1000;
	public Set<Integer> pixellist;
	
	public RandomAccessibleInterval<FloatType> originalimg;
	public RandomAccessibleInterval<FloatType> originalSecimg;
	public RandomAccessibleInterval<IntType> Segoriginalimg;
	public RandomAccessibleInterval<FloatType> SegSecoriginalimg;
	public RandomAccessibleInterval<FloatType> CurrentView;
	public final String NameA;
	public final String NameB;
	public int ndims;
	public Overlay overlay;
	public ImagePlus imp;
	public RealLocalizable Refcord;
	public HashMap<String, RealLocalizable> AllRefcords;
	
	public int thirdDimension;
	public int thirdDimensionSize;
	public ImagePlus impA;
	public int maxframegap = 30;
	public int thirdDimensionslider = 1;
	public int thirdDimensionsliderInit = 1;
	public JProgressBar jpb;
	
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
		
		
		
		
	}
	
	
	
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
		jpb = new JProgressBar();
		pixellist = new HashSet<Integer>();
		
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
		repaintView(imp, CurrentView);
		
		}
		
	}
	
	
	public void StartDisplayer() {
		
		ComputeBorder display = new ComputeBorder(this, jpb);
		
		display.execute();
	}
	
	public void repaintView(ImagePlus Activeimp, RandomAccessibleInterval<FloatType> Activeimage) {
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
	public JPanel Timeselect = new JPanel();
	public JPanel panelCont = new JPanel();
	public final Insets insets = new Insets(10, 0, 0, 0);
	public final GridBagLayout layout = new GridBagLayout();
	public final GridBagConstraints c = new GridBagConstraints();
	public JScrollPane scrollPane;
	int SizeX = 400;
	int SizeY = 200;
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
		
		panelFirst.add(Timeselect, new GridBagConstraints(0, 0, 5, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, insets, 0, 0));

		
		timeslider.addAdjustmentListener(new TimeListener(this, timeText, timestring, thirdDimensionsliderInit,
				thirdDimensionSize, scrollbarSize, timeslider));
		
		panelFirst.setVisible(true);
		cl.show(panelCont, "1");
		Cardframe.add(panelCont, "Center");
		Cardframe.add(jpb, "Last");

		Cardframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		Cardframe.pack();
		Cardframe.setVisible(true);

	}
	

}

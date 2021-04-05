package pluginTools;

import java.awt.CardLayout;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Scrollbar;
import java.awt.TextComponent;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;
import java.io.File;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollBar;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.jgrapht.generate.EmptyGraphGenerator;

import budDetector.Cellobject;
import budDetector.Roiobject;

import javax.swing.JOptionPane;

import fileListeners.ChooseGreenOrigMap;
import fileListeners.ChooseGreenSegMap;
import ij.ImagePlus;
import ij.WindowManager;
import io.scif.img.ImgIOException;
import listeners.BTrackGo3DMaskFLListener;
import listeners.BTrackGoFreeFlListener;
import listeners.BTrackGoGreenFLListener;
import listeners.BTrackGoYellowFLListener;
import listeners.BudCheckpointListener;
import listeners.BudZListener;
import listeners.CsvLoader;
import listeners.GreenCheckpointListener;
import listeners.ImageLoader;
import listeners.ThreeDCellGoFreeFLListener;
import loadfile.CovistoOneChFileLoader;
import net.imglib2.Cursor;
import net.imglib2.Dimensions;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.cell.CellImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.NativeType;
import net.imglib2.type.Type;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import net.imglib2.view.Views;
import fileListeners.SimplifiedIO;


public class ThreeDTimeCellFileChooser extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public JFrame Cardframe = new JFrame("XYZT Cell Tracker");
	public JPanel panelCont = new JPanel();
	public ImagePlus impOrigGreen, impSegGreen, impMask;
	public File impOrigGreenfile;
	public JPanel panelFirst = new JPanel();
	public JPanel Panelfile = new JPanel();
	public JPanel Panelsuperfile = new JPanel();
	public JPanel Panelfileoriginal = new JPanel();
	public JPanel Panelfilemask = new JPanel();
	public JPanel Paneldone = new JPanel();
	public JPanel Panelrun = new JPanel();
	public JPanel Microscope = new JPanel();
	public final Insets insets = new Insets(10, 10, 0, 10);
	public final GridBagLayout layout = new GridBagLayout();
	public final GridBagConstraints c = new GridBagConstraints();
	public final String[] imageNames, blankimageNames;
	public JComboBox<String> ChooseImage;
	public JComboBox<String> ChoosesuperImage;
	public JComboBox<String> ChooseoriginalImage;
	public JButton Done = new JButton("Start Tracker");
	public HashMap<Integer, ArrayList<Cellobject>> CSVGreen = new HashMap<Integer, ArrayList<Cellobject>>(); 
	public String chooseCellSegstring = "Input segmentation image for cells";
	public Border chooseCellSeg = new CompoundBorder(new TitledBorder(chooseCellSegstring), new EmptyBorder(c.insets));
    public int fakedim = 2;
	public String chooseMaskSegstring = "Input segmentation image for Cells and  Mask";
	public Border chooseMaskSeg = new CompoundBorder(new TitledBorder(chooseMaskSegstring), new EmptyBorder(c.insets));
	public JProgressBar jpb = new JProgressBar();

	public int thirdDimensionsliderInit = 1;
	public final int scrollbarSize = 1000;
	public JScrollBar Zslider = new JScrollBar(Scrollbar.HORIZONTAL, thirdDimensionsliderInit, 10, 0,
			scrollbarSize + 10);
	
	
	public RandomAccessibleInterval<FloatType> imageOrigGreen;
	public RandomAccessibleInterval<IntType> imageSegA;
	public String chooseoriginalCellfilestring = "Input Image";
	public Border chooseoriginalCellfile = new CompoundBorder(new TitledBorder(chooseoriginalCellfilestring),
			new EmptyBorder(c.insets));

	public String donestring = "Done Selection";
	public Border LoadBtrack = new CompoundBorder(new TitledBorder(donestring), new EmptyBorder(c.insets));

	public double  TimeTotal;
	
	public Label inputZ, inputT;
	public TextField inputFieldZ, inputFieldT;

	public TextField inputFieldcalX, inputFieldcalY, inputFieldcalZ, FieldinputLabelcalT;
	public Border microborder = new CompoundBorder(new TitledBorder("Microscope parameters"),
			new EmptyBorder(c.insets));
	public boolean DoMask = false;
	public boolean NoMask = true;
	public JButton Checkpointbutton = new JButton("Load Data From CSV");
	
	public boolean LoadImage = false;
	public boolean LoadCSV = true;
	public CheckboxGroup SegLoadmode = new CheckboxGroup();
	public Checkbox ImageMode = new Checkbox("Load Segmentation Data as tif images", LoadImage, SegLoadmode);
	public Checkbox CsvMode = new Checkbox("Load Segmentation Data as csv", LoadCSV, SegLoadmode);
	
	public CheckboxGroup cellmode = new CheckboxGroup();
	public Checkbox FreeMode = new Checkbox("No Mask", NoMask, cellmode);
	public Checkbox MaskMode = new Checkbox("With Mask", DoMask, cellmode);

	public ThreeDTimeCellFileChooser() {

		
		panelFirst.setLayout(layout);
		
		inputT = new Label("Total TimePoints");
		inputFieldT = new TextField(5);
		inputFieldT.setText("1");
		
		
		inputZ = new Label("Current Z = 1");
		inputFieldZ = new TextField(5);
		inputFieldZ.setText("1");
		
		
		panelFirst.setLayout(layout);

		Paneldone.setLayout(layout);
		Microscope.setLayout(layout);
		CardLayout cl = new CardLayout();


		panelCont.setLayout(cl);
		panelCont.add(panelFirst, "1");
		imageNames = WindowManager.getImageTitles();
		blankimageNames = new String[imageNames.length + 1];
		blankimageNames[0] = " ";

		for (int i = 0; i < imageNames.length; ++i)
			blankimageNames[i + 1] = imageNames[i];

		ChooseImage = new JComboBox<String>(blankimageNames);
		ChooseoriginalImage = new JComboBox<String>(blankimageNames);
		ChoosesuperImage = new JComboBox<String>(blankimageNames);

		CovistoOneChFileLoader original = new CovistoOneChFileLoader(chooseoriginalCellfilestring, blankimageNames);

		Panelfileoriginal = original.SingleChannelOption();

		panelFirst.add(ImageMode, new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));
		panelFirst.add(CsvMode, new GridBagConstraints(0, 1, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));
		
		panelFirst.add(Panelfileoriginal, new GridBagConstraints(0, 2, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		original.ChooseImage.addActionListener(new ChooseGreenOrigMap(this, original.ChooseImage));



		Paneldone.add(Checkpointbutton, new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.NORTH,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
		
		Paneldone.add(Done, new GridBagConstraints(0, 1, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
		Paneldone.setBorder(LoadBtrack);
		
		
		
		panelFirst.add(Paneldone, new GridBagConstraints(0, 10, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

	


		ImageMode.addItemListener(new ImageLoader(this)); 
		CsvMode.addItemListener(new CsvLoader(this));
		FreeMode.addItemListener(new ThreeDCellGoFreeFLListener(this));
		MaskMode.addItemListener(new BTrackGo3DMaskFLListener(this));
		Checkpointbutton.addActionListener(new GreenCheckpointListener(this));
		inputFieldT.addTextListener(new InputTListener());
		Done.addActionListener(new GreenDoneListener());
		panelFirst.setVisible(true);
		cl.show(panelCont, "1");
		Cardframe.add(panelCont, "Center");
		Panelsuperfile.setEnabled(true);
		ChoosesuperImage.setEnabled(true);

		Cardframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		Cardframe.pack();
		Cardframe.setVisible(true);
	}

	
	
	
	public class InputTListener implements TextListener {

		@Override
		public void textValueChanged(TextEvent e) {
			final TextComponent tc = (TextComponent) e.getSource();
			String s = tc.getText();

			if (s.length() > 0)
				 TimeTotal = Float.parseFloat(s);

		}

	}


	public class GreenDoneListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
		
			Done.setText("Thank you");
			Done.validate();
			Done.repaint();
			try {
				DoneCurrGreen(Cardframe);
			} catch (ImgIOException e1) {

			}
		}

	}

	public void DoneCurrGreen(Frame parent) throws ImgIOException {

		

		// Tracking and Measurement is done with imageA

		
		Done.setEnabled(false);
		Checkpointbutton.setEnabled(false);
		
		String name = impOrigGreen.getOriginalFileInfo().fileName;

		if (imageOrigGreen.numDimensions() > 4) {

			JOptionPane.showMessageDialog(new JFrame(),
					"This tracker is for 3D + time images only, your image has higher dimensionality, split the channels perhaps?");

		}

		
		if (imageOrigGreen.numDimensions() <= 3) 

			imageOrigGreen = copyUpImage(imageOrigGreen);
		
		if (DoMask) {

			
			RandomAccessibleInterval<IntType> imageMask = SimplifiedIO.openImage(
					impMask.getOriginalFileInfo().directory + impMask.getOriginalFileInfo().fileName, new IntType());
			
			
            if(imageSegA!=null) {				
            	
            	if (imageSegA.numDimensions() <= 3) 

        			imageSegA = copyUpIntImage(imageSegA);
            	
            	
				ImageObjects ImagePairs  = 
					Create4D(imageOrigGreen, imageMask, imageSegA);
			
			
			assert (imageOrigGreen.numDimensions() == imageSegA.numDimensions());
			
			InteractiveBud CellCollection = new InteractiveBud(ImagePairs.imageOrig, CSVGreen, ImagePairs.imageBigMask, ImagePairs.imageSegA, new File(impOrigGreen.getOriginalFileInfo().directory), 
					impOrigGreen.getOriginalFileInfo().fileName, 1, 1, 1,
					1, name, false);
			

			CellCollection.run(null);
			
			jpb = CellCollection.jpb;
			
            }
            
            else if (CSVGreen!=null && CSVGreen.size() > 0) {
            	
            	
            	
            	ImageObjects ImagePairs  = 
    					Create4D(imageOrigGreen, imageMask, imageMask);
    			
    			
    			
    			InteractiveBud CellCollection = new InteractiveBud(ImagePairs.imageOrig, CSVGreen, ImagePairs.imageBigMask, null, new File(impOrigGreen.getOriginalFileInfo().directory), 
    					impOrigGreen.getOriginalFileInfo().fileName, 1, 1, 1,
    					1, name, false);
    			

    			CellCollection.run(null);
    			
    			jpb = CellCollection.jpb;
            	
            	
            }
			
			
					
		}

		if (NoMask) {
			
			if(imageSegA!=null) {
				
				
				if (imageSegA.numDimensions() <= 3) 

        			imageSegA = copyUpIntImage(imageSegA);
				
			ImageObjects ImagePairs  = 
					Create4D(imageOrigGreen, imageSegA, imageSegA);
			
			
			InteractiveBud CellCollection = new InteractiveBud(ImagePairs.imageOrig, CSVGreen, ImagePairs.imageSegA, new File(impOrigGreen.getOriginalFileInfo().directory), impOrigGreen.getOriginalFileInfo().fileName, 1, 1, 1,
					1, name, false);
			

			CellCollection.run(null);

			jpb = CellCollection.jpb;
			
			}
			
			else if (CSVGreen!=null && CSVGreen.size() > 0) {
				
				
				InteractiveBud CellCollection = new InteractiveBud(imageOrigGreen, CSVGreen, null, new File(impOrigGreen.getOriginalFileInfo().directory), impOrigGreen.getOriginalFileInfo().fileName, 1, 1, 1,
						1, name, false);
				

				CellCollection.run(null);

				jpb = CellCollection.jpb;
				
				
				
			}
		
			
			
		}
		
		
		Done.setEnabled(true);
		Checkpointbutton.setEnabled(true);
		
		Cardframe.add(jpb, "Last");
		Cardframe.repaint();
		Cardframe.validate();
		
		


	}

	protected final void close(final Frame parent) {
		if (parent != null)
			parent.dispose();

	}

	protected ImageObjects Create4D(RandomAccessibleInterval<FloatType> imageOrig,
			RandomAccessibleInterval<IntType> imageMask,RandomAccessibleInterval<IntType> imageSegA) {

		RandomAccessibleInterval<IntType> imageBigMask = copyImage(imageMask);
		RandomAccessibleInterval<IntType> imageBigSeg = copyImage(imageSegA);


		imageBigMask = copyUpIntImage(imageBigMask); 
		
		Cursor<IntType> Bigcursor = Views.iterable(imageBigMask).localizingCursor();
		
		
		RandomAccess<IntType> segimage = imageBigSeg.randomAccess();
		
		while(Bigcursor.hasNext()) {
			
			Bigcursor.fwd();
			segimage.setPosition(Bigcursor);
			if(Bigcursor.get().get() == 0 ) {
				
						
				segimage.get().setZero();
			}
			
		}

		ImageObjects images = new ImageObjects(imageOrig, imageBigMask, imageBigSeg);
		return images;
	}
	
	public class ImageObjects{
	
		final RandomAccessibleInterval<FloatType> imageOrig;
		final RandomAccessibleInterval<IntType> imageBigMask;
		final RandomAccessibleInterval<IntType> imageSegA;
	
	public ImageObjects(RandomAccessibleInterval<FloatType> imageOrig, RandomAccessibleInterval<IntType> imageBigMask,
			RandomAccessibleInterval<IntType>imageSegA) {
		
		this.imageOrig = imageOrig;
		this.imageBigMask = imageBigMask;
		this.imageSegA = imageSegA;
		
	}
		
		
		
		
	}

	@SuppressWarnings("deprecation")
	protected RandomAccessibleInterval<IntType> CreateBorderMask(RandomAccessibleInterval<FloatType> imageOrig) {

		RandomAccessibleInterval<IntType> imageSegB = new CellImgFactory<IntType>().create(imageOrig, new IntType());

		RandomAccess<IntType> ranac = imageSegB.randomAccess();

		Cursor<FloatType> cur = Views.iterable(imageOrig).localizingCursor();

		while (cur.hasNext()) {

			cur.fwd();

			if (cur.getDoublePosition(0) <= imageOrig.dimension(0) - 1 && cur.getDoublePosition(0) > 1) {

				ranac.setPosition(cur);

				ranac.get().setOne();
			}

			if (cur.getDoublePosition(1) <= imageOrig.dimension(1) - 1 && cur.getDoublePosition(1) > 1) {

				ranac.setPosition(cur);

				ranac.get().setOne();
			}

			if (cur.getDoublePosition(2) <= imageOrig.dimension(2) - 1 && cur.getDoublePosition(2) > 1) {

				ranac.setPosition(cur);

				ranac.get().setOne();
			}

			if (cur.getDoublePosition(3) <= imageOrig.dimension(3) - 1 && cur.getDoublePosition(3) > 1) {

				ranac.setPosition(cur);

				ranac.get().setOne();
			}

		}

		return imageSegB;

	}
	  
	    public  RandomAccessibleInterval< IntType > copyImage( final RandomAccessibleInterval<IntType > input )
	    {
	        // create a new Image with the same properties
	        // note that the input provides the size for the new image as it implements
	        // the Interval interface
	    	
	    	RandomAccessibleInterval< IntType > output = new CellImgFactory<IntType>().create( input,
					new IntType() );
	 
	        // create a cursor for both images
	        Cursor< IntType > cursorInput = Views.iterable(input).cursor();
	        RandomAccess< IntType > randomAccess = output.randomAccess();
	 
	        // iterate over the input
	        while ( cursorInput.hasNext())
	        {
	            // move both cursors forward by one pixel
	            cursorInput.fwd();
	            randomAccess.setPosition( cursorInput );
	 
	            // set the value of this pixel of the output image to the same as the input,
	            // every Type supports T.set( T type )
	            randomAccess.get().set( cursorInput.get() );
	        }
	 
	        // return the copy
	        return output;
	    }
	    
	   public RandomAccessibleInterval<FloatType> copyUpImage(final RandomAccessibleInterval<FloatType> input) 
	   {
		   
		
        long[] newDim = new long[] {input.dimension(0), input.dimension(1), fakedim, input.dimension(2)};
        
		RandomAccessibleInterval<FloatType> output = new CellImgFactory<FloatType>().create(newDim, new FloatType());
		
		for(int i = 0; i < fakedim; ++i ) {
		RandomAccessibleInterval<FloatType> Slicedoutput = Views.hyperSlice(output, 2, i);
		Cursor<FloatType> cursorInput = Views.iterable(input).localizingCursor();
		RandomAccess<FloatType> randomAccess = Slicedoutput.randomAccess();
		
		while(cursorInput.hasNext()) {
			
			
			cursorInput.fwd();
			
			randomAccess.setPosition(cursorInput);
			
			randomAccess.get().set(cursorInput.get());
			
		}
		
		}

		
		   return output;
		   
	   }
		   
	    
	   public RandomAccessibleInterval<IntType> copyUpIntImage(final RandomAccessibleInterval<IntType> input) 
	   {
		   
		
       long[] newDim = new long[] {input.dimension(0), input.dimension(1), 2, input.dimension(2)};
       
		RandomAccessibleInterval<IntType> output = new CellImgFactory<IntType>().create(newDim, new IntType());
		
		for(int i = 0; i < fakedim; ++i ) {
		RandomAccessibleInterval<IntType> Slicedoutput = Views.hyperSlice(output, 2, i);
		Cursor<IntType> cursorInput = Views.iterable(input).localizingCursor();
		RandomAccess<IntType> randomAccess = Slicedoutput.randomAccess();
		
		while(cursorInput.hasNext()) {
			
			
			cursorInput.fwd();
			
			randomAccess.setPosition(cursorInput);
			
			randomAccess.get().set(cursorInput.get());
			
		}
		
		}
		
		
		
		   return output;
		   
	   }
		
	    
}
package pluginTools;

import java.awt.CardLayout;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.TextComponent;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;
import java.io.File;
import java.io.FilenameFilter;
import ij.process.ImageConverter;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import fileListeners.ChooseCellOrigMap;
import fileListeners.ChooseCellSegAMap;
import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import io.scif.img.ImgIOException;
import listeners.BTrackGoFreeFlListener;
import listeners.BTrackGoGreenFLListener;
import listeners.BTrackGoMaskFLListener;
import listeners.BTrackGoRedFLListener;
import listeners.TwoDCellGoFreeFLListener;
import loadfile.CovistoOneChFileLoader;
import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;
import fileListeners.SimplifiedIO;


public class TwoDTimeCellFileChooser extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public JFrame Cardframe = new JFrame("TwoD Cell tracker");
	public JPanel panelCont = new JPanel();
	public ImagePlus impOrig, impSegA, impSegB, impSegC;
	public File impOrigfile,  impSegAfile, impSegBfile, impSegCfile;
	public JPanel panelFirst = new JPanel();
	public JPanel Panelfile = new JPanel();
	public JPanel Panelsuperfile = new JPanel();
	public JPanel Panelfileoriginal = new JPanel();
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
	public JButton Done = new JButton("Collect Cells and Start Tracker");

	public boolean simple = false;
	public boolean curvesuper = true;
	public boolean curvesimple = false;
	public boolean twochannel = false;

	public String chooseCellSegstring = "Segmentation Image for Cells";
	public Border chooseCellSeg = new CompoundBorder(new TitledBorder(chooseCellSegstring), new EmptyBorder(c.insets));

	public String chooseMaskSegstring = "Segmentation Image for Cells and Mask";
	public Border chooseMaskSeg = new CompoundBorder(new TitledBorder(chooseMaskSegstring),
			new EmptyBorder(c.insets));
	public JProgressBar jpb = new JProgressBar();

	

	public String chooseoriginalCellfilestring = "Cells are tracked inside a region";
	public Border chooseoriginalCellfile = new CompoundBorder(new TitledBorder(chooseoriginalCellfilestring),
			new EmptyBorder(c.insets));

	public String donestring = "Done Selection";
	public Border LoadBtrack = new CompoundBorder(new TitledBorder(donestring), new EmptyBorder(c.insets));

	public Label inputLabelcalX, wavesize;
	public double calibration, FrameInterval;

	public TextField inputFieldcalX, Fieldwavesize;
	public Border microborder = new CompoundBorder(new TitledBorder("Microscope parameters"),
			new EmptyBorder(c.insets));

	public boolean DoMask = false;
	public boolean NoMask = true;

	public CheckboxGroup cellmode = new CheckboxGroup();
	public Checkbox FreeMode = new Checkbox("No Mask", NoMask, cellmode);
	public Checkbox MaskMode = new Checkbox("With Mask", DoMask, cellmode);

	public TwoDTimeCellFileChooser() {

		inputLabelcalX = new Label("Pixel calibration in X,Y (um)");
		inputFieldcalX = new TextField(5);
		inputFieldcalX.setText("1");

		wavesize = new Label("Pixel calibration in T (min)");
		Fieldwavesize = new TextField(5);
		Fieldwavesize.setText("1");
		panelFirst.setLayout(layout);

		Paneldone.setLayout(layout);
		Microscope.setLayout(layout);
		CardLayout cl = new CardLayout();
		calibration = Float.parseFloat(inputFieldcalX.getText());
		FrameInterval = Float.parseFloat(Fieldwavesize.getText());

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

		panelFirst.add(Panelfileoriginal, new GridBagConstraints(0, 2, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		original.ChooseImage.addActionListener(new ChooseCellOrigMap(this, original.ChooseImage));

		panelFirst.add(FreeMode, new GridBagConstraints(0, 3, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));
		panelFirst.add(MaskMode, new GridBagConstraints(0, 4, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));
		

		CovistoOneChFileLoader segmentation = new CovistoOneChFileLoader(chooseCellSegstring, blankimageNames);
		Panelfile = segmentation.SingleChannelOption();

		panelFirst.add(Panelfile, new GridBagConstraints(0, 7, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		Paneldone.add(Done, new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
		Paneldone.setBorder(LoadBtrack);
		panelFirst.add(Paneldone, new GridBagConstraints(0, 9, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		Microscope.add(inputLabelcalX, new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		Microscope.add(inputFieldcalX, new GridBagConstraints(0, 1, 3, 1, 0.1, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.RELATIVE, insets, 0, 0));

		Microscope.add(wavesize, new GridBagConstraints(0, 2, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		Microscope.add(Fieldwavesize, new GridBagConstraints(0, 3, 3, 1, 0.1, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.RELATIVE, insets, 0, 0));

		Microscope.setBorder(microborder);
		panelFirst.add(Microscope, new GridBagConstraints(0, 8, 5, 1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));

		// Listeneres

		FreeMode.addItemListener(new TwoDCellGoFreeFLListener(this));
		MaskMode.addItemListener(new BTrackGoMaskFLListener(this));
		segmentation.ChooseImage.addActionListener(new ChooseCellSegAMap(this, segmentation.ChooseImage));

		inputFieldcalX.addTextListener(new CalXListener());
		Fieldwavesize.addTextListener(new WaveListener());
		Done.addActionListener(new CellDoneListener());
		panelFirst.setVisible(true);
		cl.show(panelCont, "1");
		Cardframe.add(panelCont, "Center");
		Panelsuperfile.setEnabled(true);
		ChoosesuperImage.setEnabled(true);

		
		Cardframe.add(jpb, "Last");
		
		
		Cardframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		Cardframe.pack();
		Cardframe.setVisible(true);
	}

	public class CalXListener implements TextListener {

		@Override
		public void textValueChanged(TextEvent e) {
			final TextComponent tc = (TextComponent) e.getSource();
			String s = tc.getText();

			if (s.length() > 0)
				calibration = Double.parseDouble(s);
		}

	}

	public class WaveListener implements TextListener {

		@Override
		public void textValueChanged(TextEvent e) {
			final TextComponent tc = (TextComponent) e.getSource();
			String s = tc.getText();

			if (s.length() > 0)
				FrameInterval = Float.parseFloat(s);

		}

	}

	public class CellDoneListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			try {
				DoneCurrCell(Cardframe);
			} catch (ImgIOException e1) {

			}
		}

	}

	public void DoneCurrCell(Frame parent) throws ImgIOException {

		// Tracking and Measurement is done with imageA

		Done.setEnabled(false);
		RandomAccessibleInterval<FloatType> imageOrig = SimplifiedIO.openImage(
				impOrig.getOriginalFileInfo().directory + impOrig.getOriginalFileInfo().fileName, new FloatType());

		// Segmentation image for green cells
		RandomAccessibleInterval<IntType> imageSegA = SimplifiedIO.openImage(
				impSegA.getOriginalFileInfo().directory + impSegA.getOriginalFileInfo().fileName, new IntType());

		

		String name = impOrig.getOriginalFileInfo().fileName;
		WindowManager.closeAllWindows();
		// Image -> Mask -> Cell Mask
		Cardframe.remove(jpb);
		if(DoMask) {
			
			
			// Mask image like bud image
			RandomAccessibleInterval<IntType> imageSegB = SimplifiedIO.openImage(
					impSegB.getOriginalFileInfo().directory + impSegB.getOriginalFileInfo().fileName, new IntType());
			
			assert (imageOrig.numDimensions() == imageSegA.numDimensions());
			assert (imageOrig.numDimensions() == imageSegB.numDimensions());
			InteractiveBud CellCollection = new InteractiveBud(imageOrig, imageSegB, imageSegA, impOrig.getOriginalFileInfo().fileName, calibration,
					FrameInterval, name, false);
			
			CellCollection.run(null);
			jpb = CellCollection.jpb;
		}
		
		
		if(NoMask) {
			
			RandomAccessibleInterval<IntType> imageSegB = CreateBorderMask(imageOrig);
			InteractiveBud CellCollection = new InteractiveBud(imageOrig, imageSegB, imageSegA, impOrig.getOriginalFileInfo().fileName, calibration,
					FrameInterval, name, false);
			
			CellCollection.run(null);
			
			jpb = CellCollection.jpb;
			
		}
		
		Cardframe.add(jpb, "Last");
		Cardframe.repaint();
		Cardframe.validate();
		
		
		
		
		
		calibration = Float.parseFloat(inputFieldcalX.getText());
		FrameInterval = Float.parseFloat(Fieldwavesize.getText());
		

	}
	
	@SuppressWarnings("deprecation")
	protected RandomAccessibleInterval<IntType> CreateBorderMask(RandomAccessibleInterval<FloatType> imageOrig) {
		
		RandomAccessibleInterval<IntType> imageSegB = new ArrayImgFactory<IntType>().create(imageOrig, new IntType());
		
		
		RandomAccess<IntType> ranac = imageSegB.randomAccess(); 
		
		Cursor<FloatType> cur = Views.iterable(imageOrig).localizingCursor();
		
		while(cur.hasNext()) {
			
			
			cur.fwd();
			
			if(cur.getDoublePosition(0)<= imageOrig.dimension(0) - 1 && cur.getDoublePosition(0) > 1) {
				
				
				ranac.setPosition(cur);
				
				ranac.get().setOne();
			}
			
           if(cur.getDoublePosition(1) <= imageOrig.dimension(1) - 1 && cur.getDoublePosition(1) > 1) {
				
				
				ranac.setPosition(cur);
				
				ranac.get().setOne();
			}
				
			
		}
		
		return imageSegB;
		
	}

	protected final void close(final Frame parent) {
		if (parent != null)
			parent.dispose();

	}

}
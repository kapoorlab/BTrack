package pluginTools;

import javax.swing.JPanel;

import ij.plugin.PlugIn;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.real.FloatType;

public class InteractiveBud  extends JPanel implements PlugIn{

	
	
	public RandomAccessibleInterval<FloatType> originalimg;
	public RandomAccessibleInterval<FloatType> originalSecimg;
	public RandomAccessibleInterval<FloatType> Segoriginalimg;
	public RandomAccessibleInterval<FloatType> SegSecoriginalimg;
	public final String NameA;
	public final String NameB;
	public int ndims;
	public InteractiveBud(final RandomAccessibleInterval<FloatType> originalimg,
			final RandomAccessibleInterval<FloatType> originalSecimg,
			final RandomAccessibleInterval<FloatType> Segoriginalimg,
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
	
	
	@Override
	public void run(String arg0) {
		// TODO Auto-generated method stub
		
	}

}

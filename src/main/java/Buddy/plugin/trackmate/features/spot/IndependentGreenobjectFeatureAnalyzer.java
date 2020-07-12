package Buddy.plugin.trackmate.features.spot;

import java.util.Iterator;

import greenDetector.Greenobject;
import net.imagej.ImgPlus;
import net.imglib2.type.numeric.RealType;

public abstract class IndependentGreenobjectFeatureAnalyzer<T extends RealType<T>> implements GreenobjectAnalyzer<T> {

	protected final ImgPlus<T> img;

	protected final Iterator<Greenobject> Greenobjects;

	protected String errorMessage;

	private long processingTime;

	public IndependentGreenobjectFeatureAnalyzer(final ImgPlus<T> img, final Iterator<Greenobject> Greenobjects) {
		this.img = img;
		this.Greenobjects = Greenobjects;
	}

	public abstract void process(final Greenobject Greenobject);

	@Override
	public boolean checkInput() {
		return true;
	}

	@Override
	public boolean process() {
		final long start = System.currentTimeMillis();
		while (Greenobjects.hasNext()) {
			process(Greenobjects.next());
		}
		processingTime = System.currentTimeMillis() - start;
		return true;
	}

	@Override
	public String getErrorMessage() {
		return errorMessage;
	}

	@Override
	public long getProcessingTime() {
		return processingTime;
	}

}

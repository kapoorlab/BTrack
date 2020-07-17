package Buddy.plugin.trackmate.visualization.trackscheme;

import Buddy.plugin.trackmate.GreenSettings;
import Buddy.plugin.trackmate.Settings;
import Buddy.plugin.trackmate.util.TMUtils;
import greenDetector.Greenobject;
import net.imagej.ImgPlus;
import net.imglib2.meta.view.HyperSliceImgPlus;

@SuppressWarnings("deprecation")
public class GreenobjectImageUpdater {

	private int previousFrame;

	private int previousChannel;

	private GreenobjectIconGrabber<?> grabber;

	private final GreenSettings settings;

	/**
	 * Instantiates a new Greenobject image updater.
	 *
	 * @param settings
	 *            the {@link Settings} object from which we read the raw image and
	 *            the target channel.
	 */
	public GreenobjectImageUpdater(final GreenSettings settings) {
		this.settings = settings;
		this.previousFrame = -1;
		this.previousChannel = -1;
	}

	/**
	 * Returns the image string of the given Greenobject, based on the raw images
	 * contained in the given model. For performance, the image at target frame is
	 * stored for subsequent calls of this method. So it is a good idea to group
	 * calls to this method for Greenobjects that belong to the same frame.
	 *
	 * @param radiusFactor
	 *            a factor that determines the size of the thumbnail. The thumbnail
	 *            will have a size equal to the Greenobject diameter times this
	 *            radius.
	 * @return the image string.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public String getImageString(final Greenobject Greenobject, final double radiusFactor) {
		final int frame = Greenobject.getFeature(Greenobject.POSITION_T).intValue();
		final int targetChannel = settings.imp.getC() - 1;
		if (frame == previousFrame && targetChannel == previousChannel) {
			// Keep the same image than in memory
		} else {
			final ImgPlus img = TMUtils.rawWraps(settings.imp);
			final ImgPlus fixChannelAxis = HyperSliceImgPlus.fixChannelAxis(img, targetChannel);
			final ImgPlus<?> imgCT = HyperSliceImgPlus.fixTimeAxis(fixChannelAxis, frame);
			grabber = new GreenobjectIconGrabber(imgCT);
			previousFrame = frame;
			previousChannel = targetChannel;
		}
		return grabber.getImageString(Greenobject, radiusFactor);
	}
}

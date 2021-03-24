package Buddy.plugin.trackmate.tracking.oldlap;


import java.util.Map;

import org.scijava.Priority;
import org.scijava.plugin.Plugin;
import Buddy.plugin.trackmate.tracking.BCellobjectTrackerFactory;
import Buddy.plugin.trackmate.BCellobjectCollection;
import Buddy.plugin.trackmate.tracking.BCellobjectTracker;

@Plugin( type = BCellobjectTrackerFactory.class, priority = Priority.VERY_LOW, visible= false )
public class FastLAPTrackerFactory extends LAPTrackerFactory
{
	public static final String THIS_TRACKER_KEY = "FAST_LAP_TRACKER";

	public static final String THIS_NAME = "Old LAP Tracker";

	public static final String THIS_INFO_TEXT = "<html>" + "This tracker is based on the Linear Assignment Problem mathematical framework. <br>" + "Its implementation is adapted from the following paper: <br>" + "<i>Robust single-particle tracking in live-cell time-lapse sequences</i> - <br>" + "Jaqaman <i> et al.</i>, 2008, Nature Methods. <br>" + "<p>" + "Tracking happens in 2 steps: First BCellobjects are linked from frame to frame to <br>" + "build track segments. These track segments are investigated in a second step <br>" + "for gap-closing (missing detection), splitting and merging events.  <br> " + "<p>" + "Linking costs are proportional to the square distance between source and  <br> " + "target BCellobjects, which makes this tracker suitable for Brownian motion.  <br> " + "Penalties can be set to favor linking between BCellobjects that have similar  <br> " + "features. " + "<p>" + "Solving the LAP relies on the Jonker-Volgenant solver, <br> " + "that solves an assignment problem in O(n^3) instead of O(n^4)."
			+ "<p>"
			+ "This is the old version of the LAP tracker (pre v2.5.0) that uses a dense cost matrix, and is therefore NOT suited for large problems." + " </html>";

	@Override
	public String getName()
	{
		return THIS_NAME;
	}

	@Override
	public String getKey()
	{
		return THIS_TRACKER_KEY;
	}

	@Override
	public String getInfoText()
	{
		return THIS_INFO_TEXT;
	}

	@Override
	public BCellobjectTracker create( final BCellobjectCollection BCellobjects, final Map< String, Object > settings )
	{
		return new FastLAPTracker( BCellobjects, settings );
	}
}

package Buddy.plugin.trackmate.providers;

import Buddy.plugin.trackmate.tracking.BCellobjectTrackerFactory;
import Buddy.plugin.trackmate.tracking.GreenobjectTrackerFactory;

public class GreenTrackerProvider extends AbstractProvider<GreenobjectTrackerFactory> {

	public GreenTrackerProvider() {
		super(GreenobjectTrackerFactory.class);
	}


}

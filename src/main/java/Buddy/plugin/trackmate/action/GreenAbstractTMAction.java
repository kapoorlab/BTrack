package Buddy.plugin.trackmate.action;

import Buddy.plugin.trackmate.Logger;

public abstract class GreenAbstractTMAction implements GreenTrackMateAction {

	protected Logger logger = Logger.VOID_LOGGER;

	@Override
	public void setLogger(final Logger logger) {
		this.logger = logger;
	}
}

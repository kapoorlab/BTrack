package Buddy.plugin.trackmate.tracking.oldlap;

import Buddy.plugin.trackmate.BCellobjectCollection;
import Buddy.plugin.trackmate.tracking.oldlap.hungarian.AssignmentAlgorithm;
import Buddy.plugin.trackmate.tracking.oldlap.hungarian.JonkerVolgenantAlgorithm;
import pluginTools.InteractiveBud;

import java.util.Map;

public class FastLAPTracker extends LAPTracker {

	public FastLAPTracker(final InteractiveBud parent, final Map<String, Object> settings) {
		super(parent, settings);
	}

	@Override
	protected AssignmentAlgorithm createAssignmentProblemSolver() {
		return new JonkerVolgenantAlgorithm();
	}
}

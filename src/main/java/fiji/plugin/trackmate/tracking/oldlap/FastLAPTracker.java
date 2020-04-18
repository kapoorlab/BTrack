package fiji.plugin.trackmate.tracking.oldlap;

import fiji.plugin.trackmate.BCellobjectCollection;
import fiji.plugin.trackmate.tracking.oldlap.hungarian.AssignmentAlgorithm;
import fiji.plugin.trackmate.tracking.oldlap.hungarian.JonkerVolgenantAlgorithm;
import pluginTools.InteractiveBud;

import java.util.Map;

public class FastLAPTracker extends LAPTracker {

	public FastLAPTracker( final InteractiveBud parent, final Map< String, Object > settings )
	{
		super( parent, settings );
	}

	@Override
	protected AssignmentAlgorithm createAssignmentProblemSolver() {
		return new JonkerVolgenantAlgorithm();
	}
}

package tracker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A {@link CostMatrixCreator} that can generate a cost matrix from a list of
 * sources, a list of targets and a {@link CostFunction} that can generate a
 * cost for any combination.
 * 
 * @author Jean-Yves Tinevez - 2014
 * 
 * @param <K>
 * @param <J>
 */
public class BUDDYJaqamanLinkingCostMatrixCreator<K extends Comparable<K>, J extends Comparable<J>>
		implements BUDDYCostMatrixCreator<K, J> {

	private static final String BASE_ERROR_MSG = "[JaqamanLinkingCostMatrixCreator] ";

	private final Iterable<K> sources;

	private final Iterable<J> targets;

	private final BUDDYCostFunction<K, J> costFunction;

	private BUDDYSparseCostMatrix scm;

	private long processingTime;

	private String errorMessage;

	private final double costThreshold;

	private List<K> sourceList;

	private List<J> targetList;

	private double alternativeCost;

	private final double alternativeCostFactor;

	private final double percentile;

	public BUDDYJaqamanLinkingCostMatrixCreator(final Iterable<K> sources, final Iterable<J> targets,
			final BUDDYCostFunction<K, J> costFunction, final double costThreshold, final double alternativeCostFactor,
			final double percentile) {
		this.sources = sources;
		this.targets = targets;
		this.costFunction = costFunction;
		this.costThreshold = costThreshold;
		this.alternativeCostFactor = alternativeCostFactor;
		this.percentile = percentile;
	}

	@Override
	public boolean checkInput() {
		if (null == sources || !sources.iterator().hasNext()) {
			errorMessage = BASE_ERROR_MSG + "The source list is empty or null.";
			return false;
		}
		if (null == targets || !targets.iterator().hasNext()) {
			errorMessage = BASE_ERROR_MSG + "The target list is empty or null.";
			return false;
		}
		return true;
	}

	@Override
	public boolean process() {
		final long start = System.currentTimeMillis();

		final List<K> accSources = new ArrayList<K>();
		final List<J> accTargets = new ArrayList<J>();
		final BUDDYResizableDoubleArray costs = new BUDDYResizableDoubleArray();

		for (final K source : sources) {
			for (final J target : targets) {

				final double cost = costFunction.linkingCost(source, target);

				if (cost < costThreshold) {
					accSources.add(source);
					accTargets.add(target);
					costs.add(cost);
				}
			}
		}
		costs.trimToSize();

		/*
		 * Check if accepted source or target lists are empty and deal with it.
		 */

		if (accSources.isEmpty() || accTargets.isEmpty()) {

			sourceList = Collections.emptyList();
			targetList = Collections.emptyList();
			alternativeCost = Double.NaN;
			scm = null;
			/*
			 * CAREFUL! We return null if no acceptable links are found.
			 */
		} else {

			final BUDDYDefaultCostMatrixCreator<K, J> cmCreator = new BUDDYDefaultCostMatrixCreator<K, J>(accSources,
					accTargets, costs.data, alternativeCostFactor, percentile);
			if (!cmCreator.checkInput() || !cmCreator.process()) {
				errorMessage = cmCreator.getErrorMessage();
				return false;
			}

			scm = cmCreator.getResult();
			sourceList = cmCreator.getSourceList();
			targetList = cmCreator.getTargetList();
			alternativeCost = cmCreator.computeAlternativeCosts();
		}

		final long end = System.currentTimeMillis();
		processingTime = end - start;
		return true;
	}

	@Override
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * Returns the cost matrix generated.
	 * <p>
	 * Careful, it can be <code>null</code> if not acceptable costs have been found
	 * for the specified configuration. In that case, the lists returned by
	 * {@link #getSourceList()} and {@link #getTargetList()} are empty.
	 * 
	 * @return a new {@link SparseCostMatrix} or <code>null</code>.
	 */
	@Override
	public BUDDYSparseCostMatrix getResult() {
		return scm;
	}

	@Override
	public List<K> getSourceList() {
		return sourceList;
	}

	@Override
	public List<J> getTargetList() {
		return targetList;
	}

	@Override
	public long getProcessingTime() {
		return processingTime;
	}

	@Override
	public double getAlternativeCostForSource(final K source) {
		return alternativeCost;
	}

	@Override
	public double getAlternativeCostForTarget(final J target) {
		return alternativeCost;
	}

}

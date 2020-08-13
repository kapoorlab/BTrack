package Buddy.plugin.trackmate.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.graph.SimpleGraph;

import Buddy.plugin.trackmate.GreenModel;
import Buddy.plugin.trackmate.GreenTrackModel;
import Buddy.plugin.trackmate.Model;
import Buddy.plugin.trackmate.TrackModel;
import greenDetector.Greenobject;
import net.imglib2.algorithm.Algorithm;
import net.imglib2.algorithm.Benchmark;

/**
 * A class that can decompose the tracks of a {@link Model} in convex branches.
 * <p>
 * A convex branch is a portion of a track for which all Greenobjects - but the
 * first and last one - have exactly one predecessor and one successor (in
 * time). The first and last Greenobjects of a branch may have 0 or 1 or more
 * predecessors or successors respectively, depending on they are the start or
 * the end of a track, or a fusion or merging point, or a gap (see below).
 * <p>
 * Schematically, if a track is arranged as follow:
 * 
 * <pre>
 * A
 * |
 * B--+
 * |  |
 * C  I
 * |  |
 * D  J
 * |  |
 * E  K
 * |  |
 * F  L
 * |  |
 * G--+
 * |
 * H
 * </pre>
 * 
 * then
 * 
 * <pre>
 * A - B,
 * C - D - E - F,
 * I - J - K - L,
 * G - H
 * </pre>
 * 
 * are convex branches. This class generates the decomposition of a track in
 * these branches.
 * <p>
 * In the example above, note that another acceptable decomposition could be:
 * 
 * <pre>
 * A,
 * B - C - D - E - F - G,
 * I - J - K - L,
 * H
 * </pre>
 * 
 * depending on to what branch you choose to attach splitting or merging points.
 * This class attaches split points to the end of the early branch, and merge
 * points to the beginning of the late branch. So that for our example above,
 * the output is indeed:
 * 
 * <pre>
 * A - B,
 * C - D - E - F,
 * I - J - K - L,
 * G - H
 * </pre>
 * <p>
 * The behavior of this algorithm can be tuned using two boolean flags. The
 * first one specifies whether we can violate the convex branch contract and
 * have branches that contain a Greenobject with more than one predecessor and
 * one successor. For instance, if a track is as follow:
 * 
 * <pre>
 * A
 * |
 * B
 * |
 * C--+
 * |  |
 * D  F
 * |  |
 * E  G
 * </pre>
 * 
 * the default output would be:
 * 
 * <pre>
 * A - B - C,
 * D - E,
 * F - G
 * </pre>
 * 
 * Setting the <code>forbidMiddleLinks</code> flag to <code>false</code> would
 * give instead:
 * 
 * <pre>
 * A - B - C - D - E,
 * F - G
 * </pre>
 * 
 * which yields fewer and longer branches.
 * <p>
 * Some branches may have gaps in them, that is two Greenobjects separated by
 * more than one frame. By default this does not lead to cutting the branch in
 * two. If you want to force branches to contain Greenobjects that are separated
 * by exactly only one frame, set the <code>forbidGaps</code> flag to
 * <code>true</code>. In that case, a track arranged as following
 * (<code>ø</code> is a missing detection in a frame, or a gap):
 * 
 * <pre>
 * A - B - C - ø - D - E - F
 * </pre>
 * 
 * will be split in two branches.
 * 
 * <pre>
 * A - B - C,
 * D - E - F
 * </pre>
 * <p>
 * It is ensured that each Greenobject in the model is present in exactly one
 * branch of the decomposition. Only Greenobjects belonging to visible tracks
 * are taken into account. This class also outputs the links that were cut in
 * the source model to generate these branches.
 * 
 * @author Jean-Yves Tinevez - 2014
 */
public class GreenConvexBranchesDecomposition implements Algorithm, Benchmark {
	private static final String BASE_ERROR_MSG = "[ConvexBranchesDecomposition] ";

	private String errorMessage;

	private Collection<List<Greenobject>> branches;

	private Collection<List<Greenobject>> links;

	private Map<Integer, Collection<List<Greenobject>>> branchesPerTrack;

	private Map<Integer, Collection<List<Greenobject>>> linksPerTrack;

	private long processingTime;

	private final GreenTrackModel tm;

	private final GreenTimeDirectedNeighborIndex neighborIndex;

	private final boolean forbidMiddleLinks;

	private final boolean forbidGaps;

	/**
	 * Creates a new track splitter.
	 *
	 * @param model
	 *            the {@link Model} from which tracks are to be split. Only tracks
	 *            marked visible will be processed.
	 * @param forbidMiddleLinks
	 *            specifies whether we enforce links between branches to be between
	 *            an end point of a branch and a start point of another branch. If
	 *            <code>true</code>, links will only reach for these Greenobjects.
	 *            If <code>false</code>, a link can target a Greenobject within a
	 *            branch, which can lead to fewer and longer branches.
	 * @param forbidGaps
	 *            specifies whether we forbid gaps in tracks. If <code>true</code>,
	 *            a track containing a gap (detections missing in at least 1
	 *            consecutive frames) will be split in 2 branches. If
	 *            <code>false</code>, branches may contain gaps.
	 */
	public GreenConvexBranchesDecomposition(final GreenModel model, final boolean forbidMiddleLinks, final boolean forbidGaps) {
		this.forbidMiddleLinks = forbidMiddleLinks;
		this.forbidGaps = forbidGaps;
		this.tm = model.getTrackModel();
		this.neighborIndex = tm.getDirectedNeighborIndex();
	}

	/**
	 * Creates a new track splitter. Links between Greenobjects from within branches
	 * and gaps within convex branches are forbidden.
	 *
	 * @param model
	 *            the {@link Model} from which tracks are to be split. Only tracks
	 *            marked visible will be processed.
	 */
	public GreenConvexBranchesDecomposition(final GreenModel model) {
		this(model, true, true);
	}

	@Override
	public long getProcessingTime() {
		return processingTime;
	}

	@Override
	public boolean checkInput() {
		final long start = System.currentTimeMillis();
		for (final DefaultWeightedEdge edge : tm.edgeSet()) {
			final Greenobject source = tm.getEdgeSource(edge);
			final Greenobject target = tm.getEdgeTarget(edge);
			if (source.diffTo(target, Greenobject.POSITION_T) == 0d) {
				errorMessage = BASE_ERROR_MSG + "Cannot deal with links between two Greenobjects in the same frame ("
						+ source + " & " + target + ").\n";
				return false;
			}
		}
		final long end = System.currentTimeMillis();
		processingTime = end - start;
		return true;
	}

	@Override
	public boolean process() {
		final long startT = System.currentTimeMillis();

		final Set<Integer> trackIDs = tm.trackIDs(true);

		branches = new ArrayList<>();
		branchesPerTrack = new HashMap<>();
		links = new ArrayList<>();
		linksPerTrack = new HashMap<>();
		for (final Integer trackID : trackIDs) {
			final GreenTrackBranchDecomposition branchDecomposition = processTrack(trackID, tm, neighborIndex,
					forbidMiddleLinks, forbidGaps);

			branchesPerTrack.put(trackID, branchDecomposition.branches);
			linksPerTrack.put(trackID, branchDecomposition.links);

			branches.addAll(branchDecomposition.branches);
			links.addAll(branchDecomposition.links);
		}

		final long endT = System.currentTimeMillis();
		processingTime = endT - startT;

		return true;

	}

	/**
	 * A static utility that generates the convex branch decomposition of a specific
	 * track in a model.
	 *
	 * @param trackID
	 *            the ID of the track to decompose.
	 * @param tm
	 *            the {@link TrackModel} in which the track is stored.
	 * @param neighborIndex
	 *            a {@link TimeDirectedNeighborIndex} needed to quickly retrieve
	 *            neighbors in the mother graph.
	 * @param forbidMiddleLinks
	 *            if <code>true</code>, the decomposition will include branches
	 *            where only the first and last Greenobjects may have more than one
	 *            predecessor and one successor respectively. If <code>false</code>,
	 *            some Greenobjects inside a branch may be a fusion or splitting
	 *            point. This leads to fewer and longer branches.
	 * @param forbidGaps
	 *            if <code>true</code>, two neighbor Greenobjects in a branch will
	 *            be separated by exactly one frame. If <code>false</code>, branches
	 *            will include gaps.
	 * @return a new {@link TrackBranchDecomposition}.
	 * @see ConvexBranchesDecomposition
	 */
	public static final GreenTrackBranchDecomposition processTrack(final Integer trackID, final GreenTrackModel tm,
			final GreenTimeDirectedNeighborIndex neighborIndex, final boolean forbidMiddleLinks, final boolean forbidGaps) {
		final Set<Greenobject> allGreenobjects = tm.trackGreenobjects(trackID);
		final Set<DefaultWeightedEdge> allEdges = tm.trackEdges(trackID);
		final SimpleGraph<Greenobject, DefaultWeightedEdge> graph = new SimpleGraph<>(DefaultWeightedEdge.class);

		for (final Greenobject Greenobject : allGreenobjects) {
			graph.addVertex(Greenobject);
		}
		for (final DefaultWeightedEdge edge : allEdges) {
			graph.addEdge(tm.getEdgeSource(edge), tm.getEdgeTarget(edge));
		}

		final Collection<List<Greenobject>> links = new HashSet<>();
		for (final Greenobject Greenobject : allGreenobjects) {
			final Set<Greenobject> successors = neighborIndex.successorsOf(Greenobject);
			final Set<Greenobject> predecessors = neighborIndex.predecessorsOf(Greenobject);
			if (predecessors.size() <= 1 && successors.size() <= 1) {
				continue;
			}

			if (predecessors.size() == 0) {
				boolean found = false;
				for (final Greenobject successor : successors) {
					if (!forbidMiddleLinks && !found && successor.diffTo(Greenobject, Greenobject.POSITION_T) < 2) {
						found = true;
					} else {
						graph.removeEdge(Greenobject, successor);
						links.add(makeLink(Greenobject, successor));
					}
				}
			} else if (successors.size() == 0) {
				boolean found = false;
				for (final Greenobject predecessor : predecessors) {
					if (!forbidMiddleLinks && !found && Greenobject.diffTo(predecessor, Greenobject.POSITION_T) < 2) {
						found = true;
					} else {
						graph.removeEdge(predecessor, Greenobject);
						links.add(makeLink(predecessor, Greenobject));
					}
				}
			} else if (predecessors.size() == 1) {
				final Greenobject previous = predecessors.iterator().next();
				if (previous.diffTo(Greenobject, Greenobject.POSITION_T) < 2) {
					for (final Greenobject successor : successors) {
						graph.removeEdge(Greenobject, successor);
						links.add(makeLink(Greenobject, successor));
					}
				} else {
					graph.removeEdge(previous, Greenobject);
					links.add(makeLink(previous, Greenobject));
					boolean found = false;
					for (final Greenobject successor : successors) {
						if (!forbidMiddleLinks && !found && successor.diffTo(Greenobject, Greenobject.POSITION_T) < 2) {
							found = true;
						} else {
							graph.removeEdge(Greenobject, successor);
							links.add(makeLink(Greenobject, successor));
						}
					}
				}
			} else if (successors.size() == 1) {
				final Greenobject next = successors.iterator().next();
				if (Greenobject.diffTo(next, Greenobject.POSITION_T) < 2) {
					for (final Greenobject predecessor : predecessors) {
						graph.removeEdge(predecessor, Greenobject);
						links.add(makeLink(predecessor, Greenobject));
					}
				} else {
					graph.removeEdge(Greenobject, next);
					links.add(makeLink(Greenobject, next));
					boolean found = false;
					for (final Greenobject predecessor : predecessors) {
						if (!forbidMiddleLinks && !found
								&& Greenobject.diffTo(predecessor, Greenobject.POSITION_T) < 2) {
							found = true;
						} else {
							graph.removeEdge(predecessor, Greenobject);
							links.add(makeLink(predecessor, Greenobject));
						}
					}
				}
			} else {
				/*
				 * Complex point: we have more than 2 successor and more than 2 predecessors.
				 */
				boolean found = false;
				for (final Greenobject predecessor : predecessors) {
					if (!forbidMiddleLinks && !found && Greenobject.diffTo(predecessor, Greenobject.POSITION_T) < 2) {
						found = true;
					} else {
						graph.removeEdge(predecessor, Greenobject);
						links.add(makeLink(predecessor, Greenobject));
					}
				}
				if (!forbidMiddleLinks) {
					// Possibly extend the branch requires resetting this to
					// false, so that we do not destroy on outgoing link.
					found = false;
				}
				for (final Greenobject successor : successors) {
					if (!forbidMiddleLinks && !found && successor.diffTo(Greenobject, Greenobject.POSITION_T) < 2) {
						found = true;
					} else {
						graph.removeEdge(Greenobject, successor);
						links.add(makeLink(Greenobject, successor));
					}
				}
			}
		}

		/*
		 * 2nd pass: remove gaps.
		 */

		if (forbidGaps) {
			final Set<DefaultWeightedEdge> newEdges = graph.edgeSet();
			final Set<DefaultWeightedEdge> toRemove = new HashSet<>();
			for (final DefaultWeightedEdge edge : newEdges) {
				final Greenobject source = graph.getEdgeSource(edge);
				final Greenobject target = graph.getEdgeTarget(edge);
				if (Math.abs(source.diffTo(target, Greenobject.POSITION_T)) > 1) {
					toRemove.add(edge);
					links.add(makeLink(source, target));
				}
			}

			for (final DefaultWeightedEdge edge : toRemove) {
				graph.removeEdge(edge);
			}
		}

		/*
		 * Output
		 */

		final ConnectivityInspector<Greenobject, DefaultWeightedEdge> connectivity = new ConnectivityInspector<>(graph);
		final List<Set<Greenobject>> connectedSets = connectivity.connectedSets();
		final Collection<List<Greenobject>> branches = new HashSet<>(connectedSets.size());
		final Comparator<Greenobject> comparator = Greenobject.frameComparator;
		for (final Set<Greenobject> set : connectedSets) {
			final List<Greenobject> branch = new ArrayList<>(set.size());
			branch.addAll(set);
			Collections.sort(branch, comparator);
			branches.add(branch);
		}

		final GreenTrackBranchDecomposition output = new GreenTrackBranchDecomposition();
		output.branches = branches;
		output.links = links;
		return output;

	}

	/**
	 * Builds a directed graph made of a convex branch decomposition.
	 * <p>
	 * In the graph, the vertices are made of the branches of the decomposition, and
	 * the edges are the links between each branch.
	 * 
	 * @param branchDecomposition
	 *            the convex branch decomposition to transform.
	 * @return a new simple directed graph. The direction of the edges in the graph
	 *         are taken as the end of a branch is the source, and the beginning of
	 *         a branch as the target, following time.
	 */
	public static final SimpleDirectedGraph<List<Greenobject>, DefaultEdge> buildBranchGraph(
			final GreenTrackBranchDecomposition branchDecomposition) {
		final SimpleDirectedGraph<List<Greenobject>, DefaultEdge> branchGraph = new SimpleDirectedGraph<>(
				DefaultEdge.class);

		final Collection<List<Greenobject>> branches = branchDecomposition.branches;
		final Collection<List<Greenobject>> links = branchDecomposition.links;

		// Map of the first Greenobject of each branch.
		final Map<Greenobject, List<Greenobject>> firstGreenobjects = new HashMap<>(branches.size());
		// Map of the last Greenobject of each branch.
		final Map<Greenobject, List<Greenobject>> lastGreenobjects = new HashMap<>(branches.size());
		for (final List<Greenobject> branch : branches) {
			firstGreenobjects.put(branch.get(0), branch);
			lastGreenobjects.put(branch.get(branch.size() - 1), branch);
			branchGraph.addVertex(branch);
		}

		for (final List<Greenobject> link : links) {
			final Greenobject source = link.get(0);
			final Greenobject target = link.get(1);

			List<Greenobject> targetBranch = firstGreenobjects.get(target);
			if (targetBranch == null) {
				/*
				 * We could not find this link's target in the map of first Greenobjects. Most
				 * likely this means that the link targets a middle Greenobject, because the
				 * branch decomposition authorized it. So we have to find it...
				 */
				for (final List<Greenobject> branch : branches) {
					if (branch.contains(target)) {
						targetBranch = branch;
						break;
					}
				}
			}

			List<Greenobject> sourceBranch = lastGreenobjects.get(source);
			if (sourceBranch == null) {
				for (final List<Greenobject> branch : branches) {
					if (branch.contains(source)) {
						sourceBranch = branch;
						break;
					}
				}
			}

			branchGraph.addEdge(sourceBranch, targetBranch);
		}

		return branchGraph;
	}

	private static final List<Greenobject> makeLink(final Greenobject GreenobjectA, final Greenobject GreenobjectB) {
		final List<Greenobject> link = new ArrayList<>(2);
		link.add(GreenobjectA);
		link.add(GreenobjectB);
		return link;
	}

	@Override
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * Returns the collection of branches built by this algorithm.
	 * <p>
	 * Branches are returned as list of Greenobject. It is ensured that the
	 * Greenobjects are ordered in the list by increasing frame number, and that two
	 * consecutive Greenobject are separated by exactly one frame.
	 *
	 * @return the collection of branches.
	 */
	public Collection<List<Greenobject>> getBranches() {
		return branches;
	}

	/**
	 * Returns the mapping of each source track ID to the branches it was split in.
	 * <p>
	 * Branches are returned as list of Greenobject. It is ensured that the
	 * Greenobjects are ordered in the list by increasing frame number, and that two
	 * consecutive Greenobject are separated by exactly one frame.
	 *
	 * @return a mapping of collections of branches.
	 */
	public Map<Integer, Collection<List<Greenobject>>> getBranchesPerTrack() {
		return branchesPerTrack;
	}

	/**
	 * Returns the links cut by this algorithm when splitting the model in linear,
	 * convex branches.
	 * <p>
	 * These links are returned as a collection of 2-elements list. If the instance
	 * was created with <code>forbidMiddleLinks</code> sets to <code>true</code>, it
	 * is ensured that the first element of all links is the last Greenobject of a
	 * branch, and the second element of this link is the first Greenobject of
	 * another branch. Otherwise, a link can target a Greenobject within a branch.
	 *
	 * @return a collection of links as a 2-elements list.
	 */
	public Collection<List<Greenobject>> getLinks() {
		return links;
	}

	/**
	 * Returns the mapping of each source track ID to the links that were cut in it
	 * to split it in branches.
	 * <p>
	 * These links are returned as a collection of 2-elements list. If the instance
	 * was created with <code>forbidMiddleLinks</code> sets to <code>true</code>, it
	 * is ensured that the first element of all links is the last Greenobject of a
	 * branch, and the second element of this link is the first Greenobject of
	 * another branch. Otherwise, a link can target a Greenobject within a branch.
	 *
	 * @return the mapping of track IDs to the links.
	 */
	public Map<Integer, Collection<List<Greenobject>>> getLinksPerTrack() {
		return linksPerTrack;
	}

	/*
	 * STATIC CLASSES
	 */

	/**
	 * A two public fields class used to return the convex branch decomposition of a
	 * track.
	 */
	public static final class GreenTrackBranchDecomposition {
		/**
		 * Branches are returned as list of Greenobject. It is ensured that the
		 * Greenobjects are ordered in the list by increasing frame number, and that two
		 * consecutive Greenobject are separated by exactly one frame.
		 */
		public Collection<List<Greenobject>> branches;

		/**
		 * Links, as a collection of 2-elements list.
		 */
		public Collection<List<Greenobject>> links;

		@Override
		public String toString() {
			final StringBuilder str = new StringBuilder();
			str.append(super.toString() + ";\n");
			str.append("  Branches:\n");
			int index = 0;
			for (final List<Greenobject> branch : branches) {
				str.append(String.format("    % 4d:\t" + branch + '\n', index++));
			}
			str.append("  Links:\n");
			index = 0;
			for (final List<Greenobject> link : links) {
				str.append(String.format("    % 4d:\t" + link.get(0) + "\t→\t" + link.get(1) + '\n', index++));
			}
			return str.toString();
		}
	}
}

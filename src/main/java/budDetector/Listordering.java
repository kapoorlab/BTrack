package budDetector;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.imglib2.KDTree;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPoint;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;

public class Listordering {

	
	
	// @VKapoor

	public static List<RealLocalizable> getCopyList(List<RealLocalizable> copytruths) {

		List<RealLocalizable> orderedtruths = new ArrayList<RealLocalizable>();
		Iterator<RealLocalizable> iter = copytruths.iterator();

		while (iter.hasNext()) {

			orderedtruths.add(iter.next());

		}

		return orderedtruths;
	}
	
	
	
	public static Pair<RealLocalizable, List<RealLocalizable>> getOrderedList(List<RealLocalizable> truths) {
		
		List<RealLocalizable> copytruths = getCopyList(truths);
		List<RealLocalizable> orderedtruths = new ArrayList<RealLocalizable>(truths.size());
		RealLocalizable minCord;

		RealLocalizable meanCord = getMeanCord(copytruths);
		minCord = getMinCord(copytruths);
		RealLocalizable refcord = minCord;

	
		orderedtruths.add(minCord);

		copytruths.remove(minCord);
		int count = 0;
		do {

			List<RealLocalizable> subcopytruths = getNexinLine(copytruths, minCord, meanCord, count);
			if (subcopytruths != null && subcopytruths.size() > 0) {
				count++;
				RealLocalizable nextCord = getNextNearest(minCord, subcopytruths);
				copytruths.remove(nextCord);
				if (copytruths.size() != 0) {
					copytruths.add(nextCord);

					RealLocalizable chosenCord = null;

					chosenCord = nextCord;

					minCord = chosenCord;
					orderedtruths.add(minCord);

					copytruths.remove(chosenCord);
				} else {

					orderedtruths.add(nextCord);
					break;

				}
			}
			else break;
		} while (copytruths.size() >= 0);

		


		return new ValuePair<RealLocalizable, List<RealLocalizable>>(refcord, orderedtruths);
		
		
		
	}
	/**
	 * 
	 * 
	 * Get the Next nearest point in the list
	 * 
	 * @param minCord
	 * @param truths
	 * @return
	 */

	public static RealLocalizable getNextNearest(RealLocalizable minCord, List<RealLocalizable> truths) {

		RealLocalizable nextobject = null;

		final List<RealPoint> targetCoords = new ArrayList<RealPoint>(truths.size());
		final List<FlagNode<RealLocalizable>> targetNodes = new ArrayList<FlagNode<RealLocalizable>>(truths.size());

		for (RealLocalizable localcord : truths) {

			targetCoords.add(new RealPoint(localcord));
			targetNodes.add(new FlagNode<RealLocalizable>(localcord));
		}

		if (targetNodes.size() > 0 && targetCoords.size() > 0) {

			final KDTree<FlagNode<RealLocalizable>> Tree = new KDTree<FlagNode<RealLocalizable>>(targetNodes,
					targetCoords);

			final NNFlagsearchKDtree<RealLocalizable> Search = new NNFlagsearchKDtree<RealLocalizable>(Tree);

			Search.search(minCord);

			final FlagNode<RealLocalizable> targetNode = Search.getSampler().get();

			nextobject = targetNode.getValue();
		}

		return nextobject;

	}
	public static List<RealLocalizable> getNexinLine(List<RealLocalizable> truths, RealLocalizable Refpoint,
			RealLocalizable meanCord, int count) {

		List<RealLocalizable> copytruths = getCopyList(truths);

		List<RealLocalizable> sublisttruths = new ArrayList<RealLocalizable>();

		Iterator<RealLocalizable> listiter = copytruths.iterator();

		while (listiter.hasNext()) {

			RealLocalizable listpoint = listiter.next();

			double angledeg = Distance.AngleVectors(Refpoint, listpoint, meanCord);
			
				
				if (angledeg >= 0 )
					sublisttruths.add(listpoint);
				
			
				
			
		}
		return sublisttruths;

	}
	
	public static RealLocalizable getMeanCord(List<RealLocalizable> truths) {

		Iterator<RealLocalizable> iter = truths.iterator();
		double Xmean = 0, Ymean = 0;
		while (iter.hasNext()) {

			RealLocalizable currentpair = iter.next();

			RealLocalizable currentpoint = currentpair;

			Xmean += currentpoint.getDoublePosition(0);
			Ymean += currentpoint.getDoublePosition(1);

		}
		RealPoint meanCord = new RealPoint(new double[] { Xmean / truths.size(), Ymean / truths.size() });

		return meanCord;
	}
	
	/**
	 * 
	 * Get the starting XY co-ordinates to create an ordered list, start from minX
	 * and minY
	 * 
	 * @param truths
	 * @return
	 */

	public static RealLocalizable getMinCord(List<RealLocalizable> truths) {

		double minVal = Double.MAX_VALUE;
		
		RealLocalizable minobject = null;
		Iterator<RealLocalizable> iter = truths.iterator();

		while (iter.hasNext()) {

			RealLocalizable currentpair = iter.next();

			if (currentpair.getDoublePosition(1) <= minVal ) {

				minobject = currentpair;
				minVal = currentpair.getDoublePosition(1);
				

			}
			

		}

		return minobject;
	}

}

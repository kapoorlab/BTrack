package utility;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import ij.gui.OvalRoi;
import ij.gui.Roi;
import net.imglib2.KDTree;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPoint;
import pluginTools.InteractiveBud;

public class GetNearest {
	
	public static OvalRoi getNearestRois(ArrayList<OvalRoi> Allrois, double[] Clickedpoint) {

		OvalRoi KDtreeroi = null;

		final List<RealPoint> targetCoords = new ArrayList<RealPoint>(Allrois.size());
		final List<FlagNode<OvalRoi>> targetNodes = new ArrayList<FlagNode<OvalRoi>>(Allrois.size());
		for (int index = 0; index < Allrois.size(); ++index) {

			Roi r = Allrois.get(index);
			Rectangle rect = r.getBounds();

			targetCoords.add(new RealPoint(rect.x + rect.width / 2.0, rect.y + rect.height / 2.0));

			targetNodes.add(new FlagNode<OvalRoi>(Allrois.get(index)));

		}

		if (targetNodes.size() > 0 && targetCoords.size() > 0) {

			final KDTree<FlagNode<OvalRoi>> Tree = new KDTree<FlagNode<OvalRoi>>(targetNodes, targetCoords);

			final NNFlagsearchKDtree<OvalRoi> Search = new NNFlagsearchKDtree<OvalRoi>(Tree);

			final double[] source = Clickedpoint;
			final RealPoint sourceCoords = new RealPoint(source);
			Search.search(sourceCoords);
			final FlagNode<OvalRoi> targetNode = Search.getSampler().get();

			KDtreeroi = targetNode.getValue();

		}

		return KDtreeroi;
	}
	public static RealLocalizable getNearestPoint(final InteractiveBud parent, RealLocalizable ClickedPoint) {
		
		
		RealLocalizable KDtreeroi = null;
		
		
        ArrayList<RealLocalizable> Allrois = parent.AllBudcenter;
		final List<RealPoint> targetCoords = new ArrayList<RealPoint>(Allrois.size());
		final List<FlagNode<RealLocalizable>> targetNodes = new ArrayList<FlagNode<RealLocalizable>>(Allrois.size());
		for (int index = 0; index < Allrois.size(); ++index) {

			RealLocalizable r = Allrois.get(index);

			targetCoords.add(new RealPoint(r));

			targetNodes.add(new FlagNode<RealLocalizable>(Allrois.get(index)));

		}

		if (targetNodes.size() > 0 && targetCoords.size() > 0) {

			final KDTree<FlagNode<RealLocalizable>> Tree = new KDTree<FlagNode<RealLocalizable>>(targetNodes, targetCoords);

			final NNFlagsearchKDtree<RealLocalizable> Search = new NNFlagsearchKDtree<RealLocalizable>(Tree);

			final RealLocalizable source = ClickedPoint;
			final RealPoint sourceCoords = new RealPoint(source);
			Search.search(sourceCoords);
			final FlagNode<RealLocalizable> targetNode = Search.getSampler().get();

			KDtreeroi = targetNode.getValue();

		}

		return KDtreeroi;
		
	}
	public static RealLocalizable getNearestBudcenter(final InteractiveBud parent, RealLocalizable ClickedPoint) {
		
		
		RealLocalizable KDtreeroi = null;
		
		
        ArrayList<RealLocalizable> Allrois = parent.ChosenBudcenter;
		final List<RealPoint> targetCoords = new ArrayList<RealPoint>(Allrois.size());
		final List<FlagNode<RealLocalizable>> targetNodes = new ArrayList<FlagNode<RealLocalizable>>(Allrois.size());
		for (int index = 0; index < Allrois.size(); ++index) {

			RealLocalizable r = Allrois.get(index);

			targetCoords.add(new RealPoint(r));

			targetNodes.add(new FlagNode<RealLocalizable>(Allrois.get(index)));

		}

		if (targetNodes.size() > 0 && targetCoords.size() > 0) {

			final KDTree<FlagNode<RealLocalizable>> Tree = new KDTree<FlagNode<RealLocalizable>>(targetNodes, targetCoords);

			final NNFlagsearchKDtree<RealLocalizable> Search = new NNFlagsearchKDtree<RealLocalizable>(Tree);

			final RealLocalizable source = ClickedPoint;
			final RealPoint sourceCoords = new RealPoint(source);
			Search.search(sourceCoords);
			final FlagNode<RealLocalizable> targetNode = Search.getSampler().get();

			KDtreeroi = targetNode.getValue();

		}

		return KDtreeroi;
		
	}

}

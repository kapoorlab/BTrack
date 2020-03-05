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

}

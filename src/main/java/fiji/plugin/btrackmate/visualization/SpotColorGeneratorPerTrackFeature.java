package fiji.plugin.btrackmate.visualization;

import java.awt.Color;

import fiji.plugin.btrackmate.Model;
import fiji.plugin.btrackmate.Spot;
import fiji.plugin.btrackmate.gui.displaysettings.Colormap;

public class SpotColorGeneratorPerTrackFeature implements FeatureColorGenerator<Spot> {

	private final Model model;

	private final Color missingValueColor;

	private final PerTrackFeatureColorGenerator colorGenerator;

	public SpotColorGeneratorPerTrackFeature(final Model model, final String trackFeature,
			final Color missingValueColor, final Color undefinedValueColor, final Colormap colormap, final double min,
			final double max) {
		this.model = model;
		this.missingValueColor = missingValueColor;
		this.colorGenerator = new PerTrackFeatureColorGenerator(model, trackFeature, missingValueColor,
				undefinedValueColor, colormap, min, max);
	}

	@Override
	public Color color(final Spot spot) {
		final Integer trackID = model.getTrackModel().trackIDOf(spot);
		if (null == trackID || !model.getTrackModel().isVisible(trackID))
			return missingValueColor;

		return colorGenerator.colorOf(trackID);
	}
}

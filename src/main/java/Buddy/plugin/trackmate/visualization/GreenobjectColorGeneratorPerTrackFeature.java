package Buddy.plugin.trackmate.visualization;

import Buddy.plugin.trackmate.GreenModel;
import Buddy.plugin.trackmate.Model;

import java.awt.Color;

import greenDetector.Greenobject;

public class GreenobjectColorGeneratorPerTrackFeature implements FeatureColorGenerator<Greenobject> {

	private final PerTrackFeatureColorGenerator trackColorGenerator;

	private final GreenModel model;

	public GreenobjectColorGeneratorPerTrackFeature(final GreenModel model, final String feature) {
		this.model = model;
		this.trackColorGenerator = new PerTrackFeatureColorGenerator(model, feature);
	}

	@Override
	public Color color(final Greenobject Greenobject) {
		final Integer trackID = model.getTrackModel().trackIDOf(Greenobject);
		if (null == trackID)
			return TrackMateModelView.DEFAULT_Greenobject_COLOR;

		return trackColorGenerator.colorOf(trackID);
	}

	@Override
	public void setFeature(final String feature) {
		trackColorGenerator.setFeature(feature);
	}

	@Override
	public String getFeature() {
		return trackColorGenerator.getFeature();
	}

	@Override
	public void terminate() {
		trackColorGenerator.terminate();
	}

	@Override
	public void activate() {
		trackColorGenerator.activate();
	}

	/*
	 * MINMAXADJUSTABLE
	 */

	@Override
	public double getMin() {
		return trackColorGenerator.getMin();
	}

	@Override
	public double getMax() {
		return trackColorGenerator.getMax();
	}

	@Override
	public void setMinMax(final double min, final double max) {
		trackColorGenerator.setMinMax(min, max);
	}

	@Override
	public void autoMinMax() {
		trackColorGenerator.autoMinMax();
	}

	@Override
	public void setAutoMinMaxMode(final boolean autoMode) {
		trackColorGenerator.setAutoMinMaxMode(autoMode);
	}

	@Override
	public boolean isAutoMinMaxMode() {
		return trackColorGenerator.isAutoMinMaxMode();
	}

	@Override
	public void setFrom(final MinMaxAdjustable minMaxAdjustable) {
		setAutoMinMaxMode(minMaxAdjustable.isAutoMinMaxMode());
		if (!minMaxAdjustable.isAutoMinMaxMode()) {
			setMinMax(minMaxAdjustable.getMin(), minMaxAdjustable.getMax());
		}
	}
}

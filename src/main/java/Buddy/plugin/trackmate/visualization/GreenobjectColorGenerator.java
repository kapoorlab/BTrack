package Buddy.plugin.trackmate.visualization;

import java.awt.Color;
import java.util.Set;

import greenDetector.Greenobject;
import Buddy.plugin.trackmate.GreenModel;
import Buddy.plugin.trackmate.GreenModelChangeEvent;
import Buddy.plugin.trackmate.GreenModelChangeListener;
import Buddy.plugin.trackmate.Model;
import Buddy.plugin.trackmate.ModelChangeEvent;
import Buddy.plugin.trackmate.ModelChangeListener;
import Buddy.plugin.trackmate.TrackMateOptionUtils;
import Buddy.plugin.trackmate.org.jfree.chart.renderer.InterpolatePaintScale;

public class GreenobjectColorGenerator implements FeatureColorGenerator<Greenobject>, GreenModelChangeListener {

	private final GreenModel model;

	private String feature = null;

	private double min;

	private double max;

	private boolean autoMode = true;

	private final InterpolatePaintScale generator;

	public GreenobjectColorGenerator(final GreenModel model) {
		this.model = model;
		model.addModelChangeListener(this);
		generator = TrackMateOptionUtils.getOptions().getPaintScale();
	}

	@Override
	public Color color(final Greenobject Greenobject) {
		if (null == feature)
			return TrackMateModelView.DEFAULT_Greenobject_COLOR;

		final Double feat = Greenobject.getFeature(feature);
		if (null == feat)
			return TrackMateModelView.DEFAULT_UNASSIGNED_FEATURE_COLOR;

		final double val = feat.doubleValue();
		if (Double.isNaN(val))
			return TrackMateModelView.DEFAULT_UNDEFINED_FEATURE_COLOR;

		return generator.getPaint((val - min) / (max - min));
	}

	@Override
	public String getFeature() {
		return feature;
	}

	@Override
	public void terminate() {
		model.removeModelChangeListener(this);
	}

	@Override
	public void activate() {
		model.addModelChangeListener(this);
	}

	@Override
	public void modelChanged(final GreenModelChangeEvent event) {
		if (!autoMode || null == feature) {
			return;
		}
		if (event.getEventID() == GreenModelChangeEvent.MODEL_MODIFIED) {
			final Set<Greenobject> Greenobjects = event.getGreenobject();
			if (Greenobjects.size() > 0)
				computeGreenobjectColors();

		} else if (event.getEventID() == GreenModelChangeEvent.Greenobject_COMPUTED) {
			computeGreenobjectColors();
		}
	}

	/**
	 * Sets the feature that will be used to color Greenobjects. <code>null</code>
	 * is accepted; it will color all the Greenobject with the same default color.
	 *
	 * @param feature
	 *            the feature to color Greenobjects with.
	 */
	@Override
	public void setFeature(final String feature) {
		if (null != feature) {
			if (feature.equals(this.feature))
				return;

			this.feature = feature;
			computeGreenobjectColors();
		} else {
			this.feature = null;
		}
	}

	/*
	 * PRIVATE METHODS
	 */

	private void computeGreenobjectColors() {
		if (null == feature) {
			return;
		}

		// Get min & max
		min = Float.POSITIVE_INFINITY;
		max = Float.NEGATIVE_INFINITY;
		Double val;
		for (final int ikey : model.getGreenobjects().keySet()) {
			for (final Greenobject Greenobject : model.getGreenobjects().iterable(ikey, false)) {
				val = Greenobject.getFeature(feature);
				if (null == val || Double.isNaN(val.doubleValue()))
					continue;

				if (val > max)
					max = val.doubleValue();

				if (val < min)
					min = val.doubleValue();
			}
		}
	}

	/*
	 * MINMAXADJUSTABLE
	 */

	@Override
	public double getMin() {
		return min;
	}

	@Override
	public double getMax() {
		return max;
	}

	@Override
	public void setMinMax(final double min, final double max) {
		this.min = min;
		this.max = max;
	}

	@Override
	public void autoMinMax() {
		computeGreenobjectColors();
	}

	@Override
	public void setAutoMinMaxMode(final boolean autoMode) {
		this.autoMode = autoMode;
		if (autoMode) {
			activate();
		} else {
			terminate();
		}
	}

	@Override
	public boolean isAutoMinMaxMode() {
		return autoMode;
	}

	@Override
	public void setFrom(final MinMaxAdjustable minMaxAdjustable) {
		setAutoMinMaxMode(minMaxAdjustable.isAutoMinMaxMode());
		if (!minMaxAdjustable.isAutoMinMaxMode())
			setMinMax(minMaxAdjustable.getMin(), minMaxAdjustable.getMax());
	}




}

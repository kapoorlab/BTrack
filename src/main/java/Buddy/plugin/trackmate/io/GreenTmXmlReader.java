package Buddy.plugin.trackmate.io;

import static Buddy.plugin.trackmate.io.IOUtils.readBooleanAttribute;
import static Buddy.plugin.trackmate.io.IOUtils.readDoubleAttribute;
import static Buddy.plugin.trackmate.io.IOUtils.readIntAttribute;
import static Buddy.plugin.trackmate.io.GreenTmXmlKeys.ANALYSER_ELEMENT_KEY;
import static Buddy.plugin.trackmate.io.GreenTmXmlKeys.ANALYSER_KEY_ATTRIBUTE;
import static Buddy.plugin.trackmate.io.GreenTmXmlKeys.ANALYZER_COLLECTION_ELEMENT_KEY;
import static Buddy.plugin.trackmate.io.GreenTmXmlKeys.CROP_ELEMENT_KEY;
import static Buddy.plugin.trackmate.io.GreenTmXmlKeys.CROP_TEND_ATTRIBUTE_NAME;
import static Buddy.plugin.trackmate.io.GreenTmXmlKeys.CROP_TSTART_ATTRIBUTE_NAME;
import static Buddy.plugin.trackmate.io.GreenTmXmlKeys.CROP_XEND_ATTRIBUTE_NAME;
import static Buddy.plugin.trackmate.io.GreenTmXmlKeys.CROP_XSTART_ATTRIBUTE_NAME;
import static Buddy.plugin.trackmate.io.GreenTmXmlKeys.CROP_YEND_ATTRIBUTE_NAME;
import static Buddy.plugin.trackmate.io.GreenTmXmlKeys.CROP_YSTART_ATTRIBUTE_NAME;
import static Buddy.plugin.trackmate.io.GreenTmXmlKeys.CROP_ZEND_ATTRIBUTE_NAME;
import static Buddy.plugin.trackmate.io.GreenTmXmlKeys.CROP_ZSTART_ATTRIBUTE_NAME;
import static Buddy.plugin.trackmate.io.GreenTmXmlKeys.DETECTOR_SETTINGS_ELEMENT_KEY;
import static Buddy.plugin.trackmate.io.GreenTmXmlKeys.EDGE_ANALYSERS_ELEMENT_KEY;
import static Buddy.plugin.trackmate.io.GreenTmXmlKeys.EDGE_FEATURES_ELEMENT_KEY;
import static Buddy.plugin.trackmate.io.GreenTmXmlKeys.FEATURE_ATTRIBUTE;
import static Buddy.plugin.trackmate.io.GreenTmXmlKeys.FEATURE_DECLARATIONS_ELEMENT_KEY;
import static Buddy.plugin.trackmate.io.GreenTmXmlKeys.FEATURE_DIMENSION_ATTRIBUTE;
import static Buddy.plugin.trackmate.io.GreenTmXmlKeys.FEATURE_ELEMENT_KEY;
import static Buddy.plugin.trackmate.io.GreenTmXmlKeys.FEATURE_ISINT_ATTRIBUTE;
import static Buddy.plugin.trackmate.io.GreenTmXmlKeys.FEATURE_NAME_ATTRIBUTE;
import static Buddy.plugin.trackmate.io.GreenTmXmlKeys.FEATURE_SHORT_NAME_ATTRIBUTE;
import static Buddy.plugin.trackmate.io.GreenTmXmlKeys.FILTERED_TRACK_ELEMENT_KEY;
import static Buddy.plugin.trackmate.io.GreenTmXmlKeys.FILTER_ABOVE_ATTRIBUTE_NAME;
import static Buddy.plugin.trackmate.io.GreenTmXmlKeys.FILTER_ELEMENT_KEY;
import static Buddy.plugin.trackmate.io.GreenTmXmlKeys.FILTER_FEATURE_ATTRIBUTE_NAME;
import static Buddy.plugin.trackmate.io.GreenTmXmlKeys.FILTER_VALUE_ATTRIBUTE_NAME;
import static Buddy.plugin.trackmate.io.GreenTmXmlKeys.FRAME_ATTRIBUTE_NAME;
import static Buddy.plugin.trackmate.io.GreenTmXmlKeys.GUI_STATE_ATTRIBUTE;
import static Buddy.plugin.trackmate.io.GreenTmXmlKeys.GUI_STATE_ELEMENT_KEY;
import static Buddy.plugin.trackmate.io.GreenTmXmlKeys.GUI_VIEW_ATTRIBUTE;
import static Buddy.plugin.trackmate.io.GreenTmXmlKeys.GUI_VIEW_ELEMENT_KEY;
import static Buddy.plugin.trackmate.io.GreenTmXmlKeys.IMAGE_ELEMENT_KEY;
import static Buddy.plugin.trackmate.io.GreenTmXmlKeys.IMAGE_FILENAME_ATTRIBUTE_NAME;
import static Buddy.plugin.trackmate.io.GreenTmXmlKeys.IMAGE_FOLDER_ATTRIBUTE_NAME;
import static Buddy.plugin.trackmate.io.GreenTmXmlKeys.IMAGE_HEIGHT_ATTRIBUTE_NAME;
import static Buddy.plugin.trackmate.io.GreenTmXmlKeys.IMAGE_NFRAMES_ATTRIBUTE_NAME;
import static Buddy.plugin.trackmate.io.GreenTmXmlKeys.IMAGE_NSLICES_ATTRIBUTE_NAME;
import static Buddy.plugin.trackmate.io.GreenTmXmlKeys.IMAGE_PIXEL_HEIGHT_ATTRIBUTE_NAME;
import static Buddy.plugin.trackmate.io.GreenTmXmlKeys.IMAGE_PIXEL_WIDTH_ATTRIBUTE_NAME;
import static Buddy.plugin.trackmate.io.GreenTmXmlKeys.IMAGE_TIME_INTERVAL_ATTRIBUTE_NAME;
import static Buddy.plugin.trackmate.io.GreenTmXmlKeys.IMAGE_VOXEL_DEPTH_ATTRIBUTE_NAME;
import static Buddy.plugin.trackmate.io.GreenTmXmlKeys.IMAGE_WIDTH_ATTRIBUTE_NAME;

import static Buddy.plugin.trackmate.io.GreenTmXmlKeys.INITIAL_Greenobject_FILTER_ELEMENT_KEY;
import static Buddy.plugin.trackmate.io.GreenTmXmlKeys.INITIAL_Greenobject_FILTER_ELEMENT_KEY;
import static Buddy.plugin.trackmate.io.GreenTmXmlKeys.LOG_ELEMENT_KEY;
import static Buddy.plugin.trackmate.io.GreenTmXmlKeys.MODEL_ELEMENT_KEY;
import static Buddy.plugin.trackmate.io.GreenTmXmlKeys.PLUGIN_VERSION_ATTRIBUTE_NAME;
import static Buddy.plugin.trackmate.io.GreenTmXmlKeys.SETTINGS_ELEMENT_KEY;
import static Buddy.plugin.trackmate.io.GreenTmXmlKeys.SPATIAL_UNITS_ATTRIBUTE_NAME;
import static Buddy.plugin.trackmate.io.GreenTmXmlKeys.Greenobject_ANALYSERS_ELEMENT_KEY;
import static Buddy.plugin.trackmate.io.GreenTmXmlKeys.Greenobject_COLLECTION_ELEMENT_KEY;
import static Buddy.plugin.trackmate.io.GreenTmXmlKeys.Greenobject_COLLECTION_NGreenobjectS_ATTRIBUTE_NAME;
import static Buddy.plugin.trackmate.io.GreenTmXmlKeys.Greenobject_ELEMENT_KEY;
import static Buddy.plugin.trackmate.io.GreenTmXmlKeys.Greenobject_FEATURES_ELEMENT_KEY;
import static Buddy.plugin.trackmate.io.GreenTmXmlKeys.Greenobject_FILTER_COLLECTION_ELEMENT_KEY;
import static Buddy.plugin.trackmate.io.GreenTmXmlKeys.Greenobject_FRAME_COLLECTION_ELEMENT_KEY;
import static Buddy.plugin.trackmate.io.GreenTmXmlKeys.Greenobject_ID_ATTRIBUTE_NAME;
import static Buddy.plugin.trackmate.io.GreenTmXmlKeys.Greenobject_NAME_ATTRIBUTE_NAME;
import static Buddy.plugin.trackmate.io.GreenTmXmlKeys.TIME_UNITS_ATTRIBUTE_NAME;
import static Buddy.plugin.trackmate.io.GreenTmXmlKeys.TRACKER_SETTINGS_ELEMENT_KEY;
import static Buddy.plugin.trackmate.io.GreenTmXmlKeys.TRACK_ANALYSERS_ELEMENT_KEY;
import static Buddy.plugin.trackmate.io.GreenTmXmlKeys.TRACK_COLLECTION_ELEMENT_KEY;
import static Buddy.plugin.trackmate.io.GreenTmXmlKeys.TRACK_EDGE_ELEMENT_KEY;
import static Buddy.plugin.trackmate.io.GreenTmXmlKeys.TRACK_ELEMENT_KEY;
import static Buddy.plugin.trackmate.io.GreenTmXmlKeys.TRACK_FEATURES_ELEMENT_KEY;
import static Buddy.plugin.trackmate.io.GreenTmXmlKeys.TRACK_FILTER_COLLECTION_ELEMENT_KEY;
import static Buddy.plugin.trackmate.io.GreenTmXmlKeys.TRACK_ID_ELEMENT_KEY;
import static Buddy.plugin.trackmate.io.GreenTmXmlKeys.TRACK_NAME_ATTRIBUTE_NAME;
import static Buddy.plugin.trackmate.tracking.TrackerKeys.XML_ATTRIBUTE_TRACKER_NAME;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.jdom2.Attribute;
import org.jdom2.DataConversionException;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import greenDetector.Greenobject;
import Buddy.plugin.trackmate.GreenobjectCollection;
import Buddy.plugin.trackmate.Dimension;
import Buddy.plugin.trackmate.FeatureModel;
import Buddy.plugin.trackmate.GreenFeatureModel;
import Buddy.plugin.trackmate.GreenModel;
import Buddy.plugin.trackmate.GreenSettings;
import Buddy.plugin.trackmate.Logger;
import Buddy.plugin.trackmate.Logger.StringBuilderLogger;
import Buddy.plugin.trackmate.Model;
import Buddy.plugin.trackmate.SelectionModel;
import Buddy.plugin.trackmate.Settings;
import Buddy.plugin.trackmate.features.FeatureFilter;
import Buddy.plugin.trackmate.features.edges.EdgeAnalyzer;
import Buddy.plugin.trackmate.features.edges.EdgeTargetAnalyzer;
import Buddy.plugin.trackmate.features.edges.GreenEdgeAnalyzer;
import Buddy.plugin.trackmate.features.edges.GreenEdgeTargetAnalyzer;
import Buddy.plugin.trackmate.features.spot.GreenobjectAnalyzerFactory;
import Buddy.plugin.trackmate.features.spot.GreenobjectAnalyzerFactory;
import Buddy.plugin.trackmate.features.track.GreenTrackAnalyzer;
import Buddy.plugin.trackmate.features.track.TrackAnalyzer;
import Buddy.plugin.trackmate.features.track.TrackIndexAnalyzer;
import Buddy.plugin.trackmate.gui.descriptors.ConfigureViewsDescriptor;
import Buddy.plugin.trackmate.providers.GreenobjectAnalyzerProvider;
import Buddy.plugin.trackmate.providers.EdgeAnalyzerProvider;
import Buddy.plugin.trackmate.providers.GreenEdgeAnalyzerProvider;
import Buddy.plugin.trackmate.providers.GreenTrackAnalyzerProvider;
import Buddy.plugin.trackmate.providers.GreenTrackerProvider;
import Buddy.plugin.trackmate.providers.GreenobjectAnalyzerProvider;
import Buddy.plugin.trackmate.providers.TrackAnalyzerProvider;
import Buddy.plugin.trackmate.providers.TrackerProvider;
import Buddy.plugin.trackmate.providers.ViewProvider;
import Buddy.plugin.trackmate.tracking.GreenobjectTrackerFactory;
import Buddy.plugin.trackmate.visualization.TrackMateModelView;
import Buddy.plugin.trackmate.visualization.ViewFactory;
import Buddy.plugin.trackmate.visualization.trackscheme.TrackScheme;
import ij.IJ;
import ij.ImagePlus;
import tracker.GREENDimension;

public class GreenTmXmlReader {

	protected static final boolean DEBUG = true;

	protected Document document = null;

	protected final File file;

	/**
	 * A map of all Greenobjects loaded. We need this for performance, since we need
	 * to recreate both the filtered Greenobject collection and the tracks graph
	 * from the same Greenobject objects that the main Greenobject collection.. In
	 * the file, they are referenced by their {@link Greenobject#ID()}, and parsing
	 * them all to retrieve the one with the right ID is a drag. We made this cache
	 * a {@link ConcurrentHashMap} because we hope to load large data in a
	 * multi-threaded way.
	 */
	protected ConcurrentHashMap<Integer, Greenobject> cache;

	protected StringBuilderLogger logger = new StringBuilderLogger();

	protected final Element root;
	
	protected ConcurrentHashMap<Integer, Greenobject> greencache;



	/**
	 * If <code>false</code>, an error occurred during reading.
	 *
	 * @see #getErrorMessage()
	 */
	protected boolean ok = true;

	/*
	 * CONSTRUCTORS
	 */

	/**
	 * Initialize this reader to read the file given in argument.
	 */
	public GreenTmXmlReader(final File file) {
		this.file = file;
		final SAXBuilder sb = new SAXBuilder();
		Element r = null;
		try {
			document = sb.build(file);
			r = document.getRootElement();
		} catch (final JDOMException e) {
			ok = false;
			logger.error("Problem parsing " + file.getName()
					+ ", it is not a valid TrackMate XML file.\nError message is:\n" + e.getLocalizedMessage() + '\n');
		} catch (final IOException e) {
			logger.error(
					"Problem reading " + file.getName() + ".\nError message is:\n" + e.getLocalizedMessage() + '\n');
			ok = false;
		}
		this.root = r;
	}

	/*
	 * PUBLIC METHODS
	 */

	/**
	 * Returns the log text saved in the file, or <code>null</code> if log text was
	 * not saved.
	 */
	public String getLog() {
		final Element logElement = root.getChild(LOG_ELEMENT_KEY);
		if (null != logElement)
			return logElement.getTextTrim();

		return "";
	}

	/**
	 * Returns the GUI state saved in the file.
	 *
	 * @return the saved GUI state, as a string.
	 */
	public String getGUIState() {
		final Element guiel = root.getChild(GUI_STATE_ELEMENT_KEY);
		if (null != guiel) {
			final String guiState = guiel.getAttributeValue(GUI_STATE_ATTRIBUTE);
			if (null == guiState) {
				logger.error("Could not find GUI state attribute. Returning defaults.\n");
				ok = false;
				return ConfigureViewsDescriptor.KEY;
			}
			return guiState;
		}

		logger.error("Could not find GUI state element. Returning defaults.\n");
		ok = false;
		return ConfigureViewsDescriptor.KEY;
	}

	/**
	 * Returns the collection of views that were saved in this file. The views
	 * returned are not rendered yet.
	 *
	 * @param provider
	 *            the {@link ViewProvider} to instantiate the view. Each saved view
	 *            must be known by the specified provider.
	 * @param model
	 *            the model to display in the views.
	 * @param settings
	 *            the settings to build the views.
	 * @param selectionModel
	 *            the {@link SelectionModel} model that will be shared with the new
	 *            views.
	 * @return the collection of views.
	 * @see TrackMateModelView#render()
	 */
	public Collection<TrackMateModelView> getViews(final ViewProvider provider, final GreenModel model,
			final GreenSettings settings, final SelectionModel selectionModel) {
		final Element guiel = root.getChild(GUI_STATE_ELEMENT_KEY);
		if (null != guiel) {

			final List<Element> children = guiel.getChildren(GUI_VIEW_ELEMENT_KEY);
			final Collection<TrackMateModelView> views = new ArrayList<>(children.size());

			for (final Element child : children) {
				final String viewKey = child.getAttributeValue(GUI_VIEW_ATTRIBUTE);
				if (null == viewKey) {
					logger.error("Could not find view key attribute for element " + child + ".\n");
					ok = false;
				} else {
					// Do not instantiate TrackScheme if found in the file.
					if (viewKey.equals(TrackScheme.KEY))
						continue;

					final ViewFactory factory = provider.getFactory(viewKey);
					if (null == factory) {
						logger.error("Unknown view factory for key " + viewKey + ".\n");
						ok = false;
						continue;
					}

					final TrackMateModelView view = factory.create(model, settings, selectionModel);
					if (null == view) {
						logger.error("Unknown view for key " + viewKey + ".\n");
						ok = false;
					} else {
						views.add(view);
					}
				}
			}
			return views;
		}

		logger.error("Could not find GUI state element.\n");
		ok = false;
		return new ArrayList<>();
	}

	/**
	 * Returns the model saved in the file, or <code>null</code> if a saved model
	 * cannot be found in the xml file.
	 *
	 * @return a new {@link Model}.
	 */
	public GreenModel getModel() {
		final Element modelElement = root.getChild(MODEL_ELEMENT_KEY);
		if (null == modelElement)
			return null;

		final GreenModel model = createModel();

		// Physical units
		final String spaceUnits = modelElement.getAttributeValue(SPATIAL_UNITS_ATTRIBUTE_NAME);
		final String timeUnits = modelElement.getAttributeValue(TIME_UNITS_ATTRIBUTE_NAME);
		model.setPhysicalUnits(spaceUnits, timeUnits);

		// Feature declarations
		readFeatureDeclarations(modelElement, model);

		// Greenobjects
		final GreenobjectCollection Greenobjects = getGreenobjects(modelElement);
		model.setGreenobjects(Greenobjects, false);

		// Tracks
		if (!readTracks(modelElement, model))
			ok = false;

		// Track features

		try {
			final Map<Integer, Map<String, Double>> savedFeatureMap = readTrackFeatures(modelElement);
			for (final Integer savedKey : savedFeatureMap.keySet()) {

				final Map<String, Double> savedFeatures = savedFeatureMap.get(savedKey);
				for (final String feature : savedFeatures.keySet())
					model.getFeatureModel().putTrackFeature(savedKey, feature, savedFeatures.get(feature));
			}
		} catch (final RuntimeException re) {
			logger.error("Problem populating track features:\n");
			logger.error(re.getMessage());
			ok = false;
		}

		// That's it
		return model;
	}

	
	
	/**
	 * Hook for subclassers:<br>
	 * Creates the instance of {@link Model} that will be built upon loading with
	 * this reader.
	 *
	 * @return a new {@link Model} instance.
	 */
	protected GreenModel createModel() {
		return new GreenModel();
	}
	

	/**
	 * Reads the settings element of the file, and sets the fields of the specified
	 * {@link Settings} object according to the xml file content. The provided
	 * {@link Settings} object is left untouched if the settings element cannot be
	 * found in the file.
	 *
	 * @param settings
	 *            the {@link Settings} object to flesh out.
	 * @param detectorProvider
	 *            the detector provider, required to configure the settings with a
	 *            correct {@link GreenobjectDetectorFactory}. If <code>null</code>,
	 *            will skip reading detector parameters.
	 * @param trackerProvider
	 *            the tracker provider, required to configure the settings with a
	 *            correct {@link Buddy.plugin.trackmate.tracking.GreenobjectTracker}.
	 *            If <code>null</code>, will skip reading tracker parameters.
	 * @param GreenobjectAnalyzerProvider
	 *            the Greenobject analyzer provider, required to instantiates the
	 *            saved {@link GreenobjectAnalyzerFactory}s. If <code>null</code>,
	 *            will skip reading Greenobject analyzers.
	 * @param edgeAnalyzerProvider
	 *            the edge analyzer provider, required to instantiates the saved
	 *            {@link EdgeAnalyzer}s. If <code>null</code>, will skip reading
	 *            edge analyzers.
	 * @param trackAnalyzerProvider
	 *            the track analyzer provider, required to instantiates the saved
	 *            {@link TrackAnalyzer}s. If <code>null</code>, will skip reading
	 *            track analyzers.
	 */
	public void readSettings(final GreenSettings settings, final GreenTrackerProvider trackerProvider,
			final GreenobjectAnalyzerProvider GreenobjectAnalyzerProvider,
			final GreenEdgeAnalyzerProvider edgeAnalyzerProvider, final GreenTrackAnalyzerProvider trackAnalyzerProvider) {
		final Element settingsElement = root.getChild(SETTINGS_ELEMENT_KEY);
		if (null == settingsElement)
			return;

		// Base
		getBaseSettings(settingsElement, settings);

		// Image
		settings.imp = getImage(settingsElement);

		// Tracker
		if (null != trackerProvider)
			getTrackerSettings(settingsElement, settings, trackerProvider);

		// Greenobject Filters
		final FeatureFilter initialFilter = getInitialFilter(settingsElement);
		if (null != initialFilter)
			settings.initialGreenobjectFilterValue = initialFilter.value;

		final List<FeatureFilter> GreenobjectFilters = getGreenobjectFeatureFilters(settingsElement);
		settings.setGreenobjectFilters(GreenobjectFilters);

		// Track Filters
		final List<FeatureFilter> trackFilters = getTrackFeatureFilters(settingsElement);
		settings.setTrackFilters(trackFilters);

		// Features analyzers
		readAnalyzers(settingsElement, settings, GreenobjectAnalyzerProvider, edgeAnalyzerProvider,
				trackAnalyzerProvider);
	}

	/**
	 * Returns the version string stored in the file.
	 */
	public String getVersion() {
		return root.getAttribute(PLUGIN_VERSION_ATTRIBUTE_NAME).getValue();
	}

	/**
	 * Returns an explanatory message about the last unsuccessful read attempt.
	 *
	 * @return an error message.
	 * @see #isReadingOk()
	 */
	public String getErrorMessage() {
		return logger.toString();
	}

	/**
	 * Returns <code>true</code> if the last reading method call happened without
	 * any warning or error, <code>false</code> otherwise.
	 *
	 * @return <code>true</code> if reading was ok.
	 * @see #getErrorMessage()
	 */
	public boolean isReadingOk() {
		return ok;
	}

	/*
	 * PRIVATE METHODS
	 */

	private ImagePlus getImage(final Element settingsElement) {
		final Element imageInfoElement = settingsElement.getChild(IMAGE_ELEMENT_KEY);
		final String filename = imageInfoElement.getAttributeValue(IMAGE_FILENAME_ATTRIBUTE_NAME);
		String folder = imageInfoElement.getAttributeValue(IMAGE_FOLDER_ATTRIBUTE_NAME);
		if (null == filename || filename.isEmpty()) {
			logger.error("Cannot find image file name in xml file.\n");
			ok = false;
			return null;
		}
		if (null == folder || folder.isEmpty())
			folder = file.getParent(); // it is a relative path, then

		File imageFile = new File(folder, filename);
		if (!imageFile.exists() || !imageFile.canRead()) {
			// Could not find it to the absolute path. Then we look for the same
			// path of the xml file
			folder = file.getParent();
			imageFile = new File(folder, filename);
			if (!imageFile.exists() || !imageFile.canRead()) {
				logger.error("Cannot read image file: " + imageFile + ".\n");
				ok = false;
				return null;
			}
		}
		return IJ.openImage(imageFile.getAbsolutePath());
	}

	/**
	 * Returns a map of the saved track features, as they appear in the file
	 */
	private Map<Integer, Map<String, Double>> readTrackFeatures(final Element modelElement) {

		final HashMap<Integer, Map<String, Double>> featureMap = new HashMap<>();

		final Element allTracksElement = modelElement.getChild(TRACK_COLLECTION_ELEMENT_KEY);
		if (null == allTracksElement) {
			logger.error("Cannot find the track collection in file.\n");
			ok = false;
			return null;
		}

		// Load tracks
		final List<Element> trackElements = allTracksElement.getChildren(TRACK_ELEMENT_KEY);
		for (final Element trackElement : trackElements) {

			int trackID = -1;
			try {
				trackID = trackElement.getAttribute(TrackIndexAnalyzer.TRACK_ID).getIntValue();
			} catch (final DataConversionException e1) {
				logger.error("Found a track with invalid trackID for " + trackElement + ". Skipping.\n");
				ok = false;
				continue;
			}

			final HashMap<String, Double> trackMap = new HashMap<>();

			final List<Attribute> attributes = trackElement.getAttributes();
			for (final Attribute attribute : attributes) {

				final String attName = attribute.getName();
				if (attName.equals(TRACK_NAME_ATTRIBUTE_NAME))
					continue; // Skip trackID attribute

				Double attVal = Double.NaN;
				try {
					attVal = attribute.getDoubleValue();
				} catch (final DataConversionException e) {
					logger.error("Track " + trackID + ": Cannot read the feature " + attName + " value. Skipping.\n");
					ok = false;
					continue;
				}

				trackMap.put(attName, attVal);

			}

			featureMap.put(trackID, trackMap);
		}

		return featureMap;

	}

	/**
	 * Return the initial filter value on quality stored in this file. Return
	 * <code>null</code> if the initial threshold data cannot be found in the file.
	 *
	 * @param settingsElement
	 *            the settings {@link Element} to read from.
	 * @return the initial filter, as a {@link FeatureFilter}.
	 */
	private FeatureFilter getInitialFilter(final Element settingsElement) {
		final Element itEl = settingsElement.getChild(INITIAL_Greenobject_FILTER_ELEMENT_KEY);
		final String feature = itEl.getAttributeValue(FILTER_FEATURE_ATTRIBUTE_NAME);
		final Double value = readDoubleAttribute(itEl, FILTER_VALUE_ATTRIBUTE_NAME, logger);
		final boolean isAbove = readBooleanAttribute(itEl, FILTER_ABOVE_ATTRIBUTE_NAME, logger);
		final FeatureFilter ft = new FeatureFilter(feature, value, isAbove);
		return ft;
	}

	/**
	 * Return the list of {@link FeatureFilter} for Greenobjects stored in this
	 * file.
	 *
	 * @param settingsElement
	 *            the settings {@link Element} to read from.
	 * @return a list of {@link FeatureFilter}s.
	 */
	private List<FeatureFilter> getGreenobjectFeatureFilters(final Element settingsElement) {
		final List<FeatureFilter> featureThresholds = new ArrayList<>();
		final Element ftCollectionEl = settingsElement.getChild(Greenobject_FILTER_COLLECTION_ELEMENT_KEY);
		final List<Element> ftEls = ftCollectionEl.getChildren(FILTER_ELEMENT_KEY);
		for (final Element ftEl : ftEls) {
			final String feature = ftEl.getAttributeValue(FILTER_FEATURE_ATTRIBUTE_NAME);
			final Double value = readDoubleAttribute(ftEl, FILTER_VALUE_ATTRIBUTE_NAME, logger);
			final boolean isAbove = readBooleanAttribute(ftEl, FILTER_ABOVE_ATTRIBUTE_NAME, logger);
			final FeatureFilter ft = new FeatureFilter(feature, value, isAbove);
			featureThresholds.add(ft);
		}
		return featureThresholds;
	}

	/**
	 * Returns the list of {@link FeatureFilter} for tracks stored in this file.
	 *
	 * @param settingsElement
	 *            the settings {@link Element} to read from.
	 * @return a list of {@link FeatureFilter}s.
	 */
	private List<FeatureFilter> getTrackFeatureFilters(final Element settingsElement) {
		final List<FeatureFilter> featureThresholds = new ArrayList<>();
		final Element ftCollectionEl = settingsElement.getChild(TRACK_FILTER_COLLECTION_ELEMENT_KEY);
		final List<Element> ftEls = ftCollectionEl.getChildren(FILTER_ELEMENT_KEY);
		for (final Element ftEl : ftEls) {
			final String feature = ftEl.getAttributeValue(FILTER_FEATURE_ATTRIBUTE_NAME);
			final Double value = readDoubleAttribute(ftEl, FILTER_VALUE_ATTRIBUTE_NAME, logger);
			final boolean isAbove = readBooleanAttribute(ftEl, FILTER_ABOVE_ATTRIBUTE_NAME, logger);
			final FeatureFilter ft = new FeatureFilter(feature, value, isAbove);
			featureThresholds.add(ft);
		}
		return featureThresholds;
	}

	/**
	 * Set the base settings of the provided {@link Settings} object, extracted from
	 * the specified {@link Element}.
	 *
	 * @param settingsElement
	 *            the settings {@link Element} to read parameters from.
	 * @param settings
	 *            the {@link Settings} to feed/
	 */
	private void getBaseSettings(final Element settingsElement, final GreenSettings settings) {
		// Basic settings
		final Element settingsEl = settingsElement.getChild(CROP_ELEMENT_KEY);
		if (null != settingsEl) {
			settings.xstart = readIntAttribute(settingsEl, CROP_XSTART_ATTRIBUTE_NAME, logger, 1);
			settings.xend = readIntAttribute(settingsEl, CROP_XEND_ATTRIBUTE_NAME, logger, 512);
			settings.ystart = readIntAttribute(settingsEl, CROP_YSTART_ATTRIBUTE_NAME, logger, 1);
			settings.yend = readIntAttribute(settingsEl, CROP_YEND_ATTRIBUTE_NAME, logger, 512);
			settings.zstart = readIntAttribute(settingsEl, CROP_ZSTART_ATTRIBUTE_NAME, logger, 1);
			settings.zend = readIntAttribute(settingsEl, CROP_ZEND_ATTRIBUTE_NAME, logger, 10);
			settings.tstart = readIntAttribute(settingsEl, CROP_TSTART_ATTRIBUTE_NAME, logger, 1);
			settings.tend = readIntAttribute(settingsEl, CROP_TEND_ATTRIBUTE_NAME, logger, 10);
			// settings.detectionChannel = readIntAttribute(settingsEl,
			// CROP_DETECTION_CHANNEL_ATTRIBUTE_NAME, logger, 1);
		}
		// Image info settings
		final Element infoEl = settingsElement.getChild(IMAGE_ELEMENT_KEY);
		if (null != infoEl) {
			settings.dx = readDoubleAttribute(infoEl, IMAGE_PIXEL_WIDTH_ATTRIBUTE_NAME, logger);
			settings.dy = readDoubleAttribute(infoEl, IMAGE_PIXEL_HEIGHT_ATTRIBUTE_NAME, logger);
			settings.dz = readDoubleAttribute(infoEl, IMAGE_VOXEL_DEPTH_ATTRIBUTE_NAME, logger);
			settings.dt = readDoubleAttribute(infoEl, IMAGE_TIME_INTERVAL_ATTRIBUTE_NAME, logger);
			settings.width = readIntAttribute(infoEl, IMAGE_WIDTH_ATTRIBUTE_NAME, logger, 512);
			settings.height = readIntAttribute(infoEl, IMAGE_HEIGHT_ATTRIBUTE_NAME, logger, 512);
			settings.nslices = readIntAttribute(infoEl, IMAGE_NSLICES_ATTRIBUTE_NAME, logger, 1);
			settings.nframes = readIntAttribute(infoEl, IMAGE_NFRAMES_ATTRIBUTE_NAME, logger, 1);
			settings.imageFileName = infoEl.getAttributeValue(IMAGE_FILENAME_ATTRIBUTE_NAME);
			settings.imageFolder = infoEl.getAttributeValue(IMAGE_FOLDER_ATTRIBUTE_NAME);
		}
	}

	/**
	 * Update the given {@link Settings} object with the
	 * {@link GreenobjectDetectorFactory} and settings map fields named
	 * {@link Settings#detectorFactory} and {@link Settings#detectorSettings} read
	 * within the XML file this reader is initialized with.
	 *
	 * @param settingsElement
	 *            the Element in which the {@link Settings} parameters are stored.
	 * @param settings
	 *            the base {@link Settings} object to update.
	 * @param provider
	 *            a {@link DetectorProvider}, required to read detector parameters.
	 */

	/**
	 * Update the given {@link Settings} object with {@link GreenobjectTracker}
	 * proper settings map fields named {@link Settings#trackerSettings} and
	 * {@link Settings#trackerFactory} read within the XML file this reader is
	 * initialized with.
	 * <p>
	 * If the tracker settings or the tracker info can be read, but cannot be
	 * understood (most likely because the class the XML refers to is unknown) then
	 * a default object is substituted.
	 *
	 * @param settingsElement
	 *            the {@link Element} in which the tracker parameters are stored.
	 * @param settings
	 *            the base {@link Settings} object to update.
	 * @param provider
	 *            the {@link TrackerProvider}, required to read the tracker
	 *            parameters.
	 */
	private void getTrackerSettings(final Element settingsElement, final GreenSettings settings,
			final GreenTrackerProvider provider) {
		final Element element = settingsElement.getChild(TRACKER_SETTINGS_ELEMENT_KEY);
		if (null == element) {
			logger.error("Could not find the tracker element in file.\n");
			this.ok = false;
			return;
		}

		final Map<String, Object> ds = new HashMap<>();

		// Get the tracker key
		final String trackerKey = element.getAttributeValue(XML_ATTRIBUTE_TRACKER_NAME);
		if (null == trackerKey) {
			logger.error("Could not find the tracker key element in file.\n");
			this.ok = false;
			return;
		}

		final GreenobjectTrackerFactory factory = provider.getFactory(trackerKey);
		if (null == factory) {
			logger.error("The tracker identified by the key " + trackerKey + " is unknown to TrackMate.\n");
			this.ok = false;
			return;
		}
		settings.trackerFactory = factory;

		// All the hard work is delegated to the factory.
		final boolean lOk = factory.unmarshall(element, ds);
		if (lOk)
			settings.trackerSettings = ds;
	}

	/**
	 * Read the list of all Greenobjects stored in this file.
	 * <p>
	 * Internally, this methods also builds the cache field, which will be required
	 * by the other methods.
	 * 
	 * It is therefore sensible to call this method first, just after parsing the
	 * file. If not called, this method will be called anyway by the other methods
	 * to build the cache.
	 *
	 * @param modelElement
	 *            the {@link Element} in which the model content was written.
	 * @return a new {@link GreenobjectCollection}.
	 */
	private GreenobjectCollection getGreenobjects(final Element modelElement) {
		// Root element for collection
		final Element GobjectCollection = modelElement.getChild(Greenobject_COLLECTION_ELEMENT_KEY);

		// Retrieve children elements for each frame
		final List<Element> frameContent = GobjectCollection.getChildren(Greenobject_FRAME_COLLECTION_ELEMENT_KEY);

		// Determine total number of Greenobjects
		int nGreenobjects = readIntAttribute(GobjectCollection, Greenobject_COLLECTION_NGreenobjectS_ATTRIBUTE_NAME,
				Logger.VOID_LOGGER);
		if (nGreenobjects == 0) {
			/*
			 * Could not find it or read it. Determine it by quick sweeping through children
			 * element.
			 */
			for (final Element currentFrameContent : frameContent)
				nGreenobjects += currentFrameContent.getChildren(Greenobject_ELEMENT_KEY).size();
		}

		// Instantiate cache
		cache = new ConcurrentHashMap<>(nGreenobjects);

		// Load collection and build cache
		int currentFrame = 0;
		final Map<Integer, Set<Greenobject>> content = new HashMap<>(frameContent.size());
		for (final Element currentFrameContent : frameContent) {

			currentFrame = readIntAttribute(currentFrameContent, FRAME_ATTRIBUTE_NAME, logger);
			final List<Element> GreenobjectContent = currentFrameContent.getChildren(Greenobject_ELEMENT_KEY);
			final Set<Greenobject> GreenobjectSet = new HashSet<>(GreenobjectContent.size());

			content.put(currentFrame, GreenobjectSet);
		}
		final GreenobjectCollection allGreenobjects = GreenobjectCollection.fromMap(content);
		return allGreenobjects;
	}

	/**
	 * Load the tracks, the track features and the ID of the filtered tracks into
	 * the model specified. The track collection element is expected to be found as
	 * a child of the specified element.
	 *
	 * @return true if reading tracks was successful, false otherwise.
	 */
	protected boolean readTracks(final Element modelElement, final GreenModel model) {

		final Element allTracksElement = modelElement.getChild(TRACK_COLLECTION_ELEMENT_KEY);
		final List<Element> trackElements = allTracksElement.getChildren(TRACK_ELEMENT_KEY);

		// What we have to flesh out from the file
		final SimpleWeightedGraph<Greenobject, DefaultWeightedEdge> graph = new SimpleWeightedGraph<>(
				DefaultWeightedEdge.class);
		final Map<Integer, Set<Greenobject>> connectedVertexSet = new HashMap<>(trackElements.size());
		final Map<Integer, Set<DefaultWeightedEdge>> connectedEdgeSet = new HashMap<>(trackElements.size());
		final Map<Integer, String> savedTrackNames = new HashMap<>(trackElements.size());

		// The list of edge features. that we will set.
		final GreenFeatureModel fm = model.getFeatureModel();
		final Collection<String> edgeFeatures = fm.getEdgeFeatures();
		final Map<String, Boolean> edgeFeatureIsInt = fm.getEdgeFeatureIsInt();

		for (final Element trackElement : trackElements) {

			// Get track ID as it is saved on disk
			final int trackID = readIntAttribute(trackElement, TrackIndexAnalyzer.TRACK_ID, logger);
			String trackName = trackElement.getAttributeValue(TRACK_NAME_ATTRIBUTE_NAME);
			if (null == trackName)
				trackName = "Unnamed";

			// Iterate over edges & Greenobjects
			final List<Element> edgeElements = trackElement.getChildren(TRACK_EDGE_ELEMENT_KEY);
			final Set<DefaultWeightedEdge> edges = new HashSet<>(edgeElements.size());
			final Set<Greenobject> Greenobjects = new HashSet<>(edgeElements.size());

			for (final Element edgeElement : edgeElements) {

				// Get source and target ID for this edge
				final int sourceID = readIntAttribute(edgeElement, GreenEdgeTargetAnalyzer.Greenobject_SOURCE_ID, logger);
				final int targetID = readIntAttribute(edgeElement, GreenEdgeTargetAnalyzer.Greenobject_TARGET_ID, logger);

				// Get matching Greenobjects from the cache
				final Greenobject sourceGreenobject = cache.get(sourceID);
				final Greenobject targetGreenobject = cache.get(targetID);

				// Get weight
				double weight = 0;
				if (null != edgeElement.getAttribute(EdgeTargetAnalyzer.EDGE_COST))
					weight = readDoubleAttribute(edgeElement, EdgeTargetAnalyzer.EDGE_COST, logger);

				// Error check
				if (null == sourceGreenobject) {
					logger.error("Unknown Greenobject ID: " + sourceID + "\n");
					return false;
				}
				if (null == targetGreenobject) {
					logger.error("Unknown Greenobject ID: " + targetID + "\n");
					return false;
				}

				if (sourceGreenobject.equals(targetGreenobject)) {
					logger.error("Bad link for track " + trackID + ". Source = Target with ID: " + sourceID + "\n");
					return false;
				}

				/*
				 * Add Greenobjects to connected set. We might add the same Greenobject twice
				 * (because we iterate over edges) but this is fine for we use a set.
				 */
				Greenobjects.add(sourceGreenobject);
				Greenobjects.add(targetGreenobject);

				// Add Greenobjects to graph and build edge
				graph.addVertex(sourceGreenobject);
				graph.addVertex(targetGreenobject);
				final DefaultWeightedEdge edge = graph.addEdge(sourceGreenobject, targetGreenobject);

				if (edge == null) {
					logger.error("Bad edge found for track " + trackID + "\n");
					return false;
				}

				graph.setEdgeWeight(edge, weight);

				// Put edge features
				for (final String feature : edgeFeatures) {
					if (null == edgeElement.getAttribute(feature))
						continue; // Skip missing values.

					final double val;
					if (edgeFeatureIsInt.get(feature).booleanValue())
						val = readIntAttribute(edgeElement, feature, logger);
					else
						val = readDoubleAttribute(edgeElement, feature, logger);

					fm.putEdgeFeature(edge, feature, val);
				}

				// Adds the edge to the set
				edges.add(edge);

			} // Finished parsing over the edges of the track

			// Store one of the Greenobject in the saved trackID key map
			connectedVertexSet.put(trackID, Greenobjects);
			connectedEdgeSet.put(trackID, edges);
			savedTrackNames.put(trackID, trackName);
		}

		/*
		 * Now on to the visibility.
		 */
		final Set<Integer> savedFilteredTrackIDs = readFilteredTrackIDs(modelElement);
		final Map<Integer, Boolean> visibility = new HashMap<>(connectedEdgeSet.size());
		final Set<Integer> ids = new HashSet<>(connectedEdgeSet.keySet());
		for (final Integer id : savedFilteredTrackIDs)
			visibility.put(id, Boolean.TRUE);

		ids.removeAll(savedFilteredTrackIDs);
		for (final Integer id : ids)
			visibility.put(id, Boolean.FALSE);

		/*
		 * Pass read results to model.
		 */
		model.getTrackModel().from(graph, connectedVertexSet, connectedEdgeSet, visibility, savedTrackNames);

		return true;
	}

	/**
	 * Reads and returns the list of track indices that define the filtered track
	 * collection.
	 */
	private Set<Integer> readFilteredTrackIDs(final Element modelElement) {
		final Element filteredTracksElement = modelElement.getChild(FILTERED_TRACK_ELEMENT_KEY);
		if (null == filteredTracksElement) {
			logger.error("Could not find the filtered track IDs in file.\n");
			ok = false;
			return null;
		}

		/*
		 * We double-check that all trackID in the filtered list exist in the track
		 * list. First, prepare a sorted array of all track IDs.
		 */
		final Element allTracksElement = modelElement.getChild(TRACK_COLLECTION_ELEMENT_KEY);
		if (null == allTracksElement) {
			logger.error("Could not find the track collection in file.\n");
			ok = false;
			return null;
		}

		final List<Element> trackElements = allTracksElement.getChildren(TRACK_ELEMENT_KEY);
		final int[] IDs = new int[trackElements.size()];
		int index = 0;
		for (final Element trackElement : trackElements) {
			final int trackID = readIntAttribute(trackElement, TrackIndexAnalyzer.TRACK_ID, logger);
			IDs[index] = trackID;
			index++;
		}
		Arrays.sort(IDs);

		final List<Element> elements = filteredTracksElement.getChildren(TRACK_ID_ELEMENT_KEY);
		final HashSet<Integer> filteredTrackIndices = new HashSet<>(elements.size());
		for (final Element indexElement : elements) {
			final int trackID = readIntAttribute(indexElement, TrackIndexAnalyzer.TRACK_ID, logger);

			// Check if this one exist in the list
			final int search = Arrays.binarySearch(IDs, trackID);
			if (search < 0) {
				logger.error("Invalid filtered track index: " + trackID + ". Track ID does not exist.\n");
				ok = false;
			} else {
				filteredTrackIndices.add(trackID);
			}
		}
		return filteredTrackIndices;
	}

	protected static final void removeAttributeFromName(final List<Attribute> attributes,
			final String attributeNameToRemove) {
		final List<Attribute> toRemove = new ArrayList<>();
		for (final Attribute attribute : attributes)
			if (attribute.getName().equals(attributeNameToRemove))
				toRemove.add(attribute);

		attributes.removeAll(toRemove);
	}

	private void readFeatureDeclarations(final Element modelElement, final GreenModel model) {

		final GreenFeatureModel fm = model.getFeatureModel();
		final Element featuresElement = modelElement.getChild(FEATURE_DECLARATIONS_ELEMENT_KEY);
		if (null == featuresElement) {
			logger.error("Could not find feature declarations in file.\n");
			ok = false;
			return;
		}

		// Greenobjects
		final Element GreenobjectFeaturesElement = featuresElement.getChild(Greenobject_FEATURES_ELEMENT_KEY);
		if (null == GreenobjectFeaturesElement) {
			logger.error("Could not find Greenobject feature declarations in file.\n");
			ok = false;

		} else {

			final List<Element> children = GreenobjectFeaturesElement.getChildren(FEATURE_ELEMENT_KEY);
			final Collection<String> features = new ArrayList<>(children.size());
			final Map<String, String> featureNames = new HashMap<>(children.size());
			final Map<String, String> featureShortNames = new HashMap<>(children.size());
			final Map<String, GREENDimension> featureDimensions = new HashMap<>(children.size());
			final Map<String, Boolean> isIntFeature = new HashMap<>();
			for (final Element child : children)
				readSingleFeatureDeclaration(child, features, featureNames, featureShortNames, featureDimensions,
						isIntFeature);

			fm.declareGreenobjectFeatures(features, featureNames, featureShortNames, featureDimensions, isIntFeature);
		}

		// Edges
		final Element edgeFeaturesElement = featuresElement.getChild(EDGE_FEATURES_ELEMENT_KEY);
		if (null == edgeFeaturesElement) {
			logger.error("Could not find edge feature declarations in file.\n");
			ok = false;

		} else {

			final List<Element> children = edgeFeaturesElement.getChildren(FEATURE_ELEMENT_KEY);
			final Collection<String> features = new ArrayList<>(children.size());
			final Map<String, String> featureNames = new HashMap<>(children.size());
			final Map<String, String> featureShortNames = new HashMap<>(children.size());
			final Map<String, GREENDimension> featureDimensions = new HashMap<>(children.size());
			final Map<String, Boolean> isIntFeature = new HashMap<>(children.size());
			for (final Element child : children)
				readSingleFeatureDeclaration(child, features, featureNames, featureShortNames, featureDimensions,
						isIntFeature);

			fm.declareEdgeFeatures(features, featureNames, featureShortNames, featureDimensions, isIntFeature);
		}

		// Tracks
		final Element trackFeaturesElement = featuresElement.getChild(TRACK_FEATURES_ELEMENT_KEY);
		if (null == trackFeaturesElement) {
			logger.error("Could not find track feature declarations in file.\n");
			ok = false;

		} else {

			final List<Element> children = trackFeaturesElement.getChildren(FEATURE_ELEMENT_KEY);
			final Collection<String> features = new ArrayList<>(children.size());
			final Map<String, String> featureNames = new HashMap<>(children.size());
			final Map<String, String> featureShortNames = new HashMap<>(children.size());
			final Map<String, GREENDimension> featureDimensions = new HashMap<>(children.size());
			final Map<String, Boolean> isIntFeature = new HashMap<>();
			for (final Element child : children)
				readSingleFeatureDeclaration(child, features, featureNames, featureShortNames, featureDimensions,
						isIntFeature);

			fm.declareTrackFeatures(features, featureNames, featureShortNames, featureDimensions, isIntFeature);
		}
	}

	private void readAnalyzers(final Element settingsElement, final GreenSettings settings,
			final GreenobjectAnalyzerProvider GreenobjectAnalyzerProvider,
			final GreenEdgeAnalyzerProvider edgeAnalyzerProvider, final GreenTrackAnalyzerProvider trackAnalyzerProvider) {

		final Element analyzersEl = settingsElement.getChild(ANALYZER_COLLECTION_ELEMENT_KEY);
		if (null == analyzersEl) {
			logger.error("Could not find the feature analyzer element.\n");
			ok = false;
			return;
		}

		// Greenobject analyzers
		if (null != GreenobjectAnalyzerProvider) {
			final Element GreenobjectAnalyzerEl = analyzersEl.getChild(Greenobject_ANALYSERS_ELEMENT_KEY);
			if (null == GreenobjectAnalyzerEl) {
				logger.error("Could not find the Greenobject analyzer element.\n");
				ok = false;

			} else {

				if (settings.imp == null) {
					logger.error("The source image is not loaded; cannot instantiates Greenobject analyzers.\n");
					ok = false;

				} else {

					final List<Element> children = GreenobjectAnalyzerEl.getChildren(ANALYSER_ELEMENT_KEY);
					for (final Element child : children) {

						final String key = child.getAttributeValue(ANALYSER_KEY_ATTRIBUTE);
						if (null == key) {
							logger.error("Could not find analyzer name for element " + child + ".\n");
							ok = false;
							continue;
						}

						final GreenobjectAnalyzerFactory<?> GreenobjectAnalyzer = GreenobjectAnalyzerProvider
								.getFactory(key);
						if (null == GreenobjectAnalyzer) {
							logger.error("Unknown Greenobject analyzer key: " + key + ".\n");
							ok = false;

						} else {
							settings.addGreenobjectAnalyzerFactory(GreenobjectAnalyzer);
						}
					}
				}
			}
		}

		// Edge analyzers
		if (null != edgeAnalyzerProvider) {
			final Element edgeAnalyzerEl = analyzersEl.getChild(EDGE_ANALYSERS_ELEMENT_KEY);
			if (null == edgeAnalyzerEl) {
				logger.error("Could not find the edge analyzer element.\n");
				ok = false;

			} else {

				final List<Element> children = edgeAnalyzerEl.getChildren(ANALYSER_ELEMENT_KEY);
				for (final Element child : children) {

					final String key = child.getAttributeValue(ANALYSER_KEY_ATTRIBUTE);
					if (null == key) {
						logger.error("Could not find analyzer name for element " + child + ".\n");
						ok = false;
						continue;
					}

					final GreenEdgeAnalyzer edgeAnalyzer = edgeAnalyzerProvider.getFactory(key);
					if (null == edgeAnalyzer) {
						logger.error("Unknown edge analyzer key: " + key + ".\n");
						ok = false;
					} else {
						settings.addEdgeAnalyzer(edgeAnalyzer);
					}
				}
			}
		}

		// Track analyzers
		if (null != trackAnalyzerProvider) {
			final Element trackAnalyzerEl = analyzersEl.getChild(TRACK_ANALYSERS_ELEMENT_KEY);
			if (null == trackAnalyzerEl) {
				logger.error("Could not find the track analyzer element.\n");
				ok = false;

			} else {

				final List<Element> children = trackAnalyzerEl.getChildren(ANALYSER_ELEMENT_KEY);
				for (final Element child : children) {

					final String key = child.getAttributeValue(ANALYSER_KEY_ATTRIBUTE);
					if (null == key) {
						logger.error("Could not find analyzer name for element " + child + ".\n");
						ok = false;
						continue;
					}

					final GreenTrackAnalyzer trackAnalyzer = trackAnalyzerProvider.getFactory(key);
					if (null == trackAnalyzer) {
						logger.error("Unknown track analyzer key: " + key + ".\n");
						ok = false;
					} else {
						settings.addTrackAnalyzer(trackAnalyzer);
					}
				}
			}
		}
	}

	private void readSingleFeatureDeclaration(final Element child, final Collection<String> features,
			final Map<String, String> featureNames, final Map<String, String> featureShortNames,
			final Map<String, GREENDimension> featureDimensions, final Map<String, Boolean> isIntFeature) {

		final String feature = child.getAttributeValue(FEATURE_ATTRIBUTE);
		if (null == feature) {
			logger.error("Could not find feature declaration for element " + child + ".\n");
			ok = false;
			return;
		}
		final String featureName = child.getAttributeValue(FEATURE_NAME_ATTRIBUTE);
		if (null == featureName) {
			logger.error("Could not find name for feature " + feature + ".\n");
			ok = false;
			return;
		}
		final String featureShortName = child.getAttributeValue(FEATURE_SHORT_NAME_ATTRIBUTE);
		if (null == featureShortName) {
			logger.error("Could not find short name for feature " + feature + ".\n");
			ok = false;
			return;
		}
		final GREENDimension featureDimension = GREENDimension.valueOf(child.getAttributeValue(FEATURE_DIMENSION_ATTRIBUTE));
		if (null == featureDimension) {
			logger.error("Could not find dimension for feature " + feature + ".\n");
			ok = false;
			return;
		}
		boolean isInt = false;
		try {
			isInt = child.getAttribute(FEATURE_ISINT_ATTRIBUTE).getBooleanValue();
		} catch (final Exception e) {
			logger.error("Could not read the isInt attribute for feature " + feature + ".\n");
			ok = false;
		}

		features.add(feature);
		featureNames.put(feature, featureName);
		featureShortNames.put(feature, featureShortName);
		featureDimensions.put(feature, featureDimension);
		isIntFeature.put(feature, Boolean.valueOf(isInt));
	}
	
	
	

	
	
	
}

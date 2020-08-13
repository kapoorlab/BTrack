package Buddy.plugin.trackmate.visualization.trackscheme.utils;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.font.TextAttribute;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JTextField;

import greenDetector.Greenobject;
import Buddy.plugin.trackmate.GreenModel;
import Buddy.plugin.trackmate.gui.GreenTrackMateWizard;
import Buddy.plugin.trackmate.visualization.GreenTrackMateModelView;

@SuppressWarnings("unchecked")
public class GreenSearchBar extends JTextField {
	private static final long serialVersionUID = 1L;

	private final static Font NORMAL_FONT = GreenTrackMateWizard.FONT.deriveFont(10f);

	private final static Font NOTFOUND_FONT;
	static {
		@SuppressWarnings("rawtypes")
		final Map attributes = NORMAL_FONT.getAttributes();
		attributes.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
		attributes.put(TextAttribute.FOREGROUND, Color.RED.darker());
		NOTFOUND_FONT = new Font(attributes);
	}

	private final PropertyChangeSupport observer = new PropertyChangeSupport(this);

	private final GreenModel model;

	private final GreenTrackMateModelView view;

	/**
	 * Creates new form SearchBox
	 * 
	 * @param model
	 *            the model to search in.
	 * @param view
	 *            the view to update when a BCellobject is found.
	 */
	public GreenSearchBar(final GreenModel model, final GreenTrackMateModelView view) {
		this.model = model;
		this.view = view;
		putClientProperty("JTextField.variant", "search");
		putClientProperty("JTextField.Search.Prompt", "Search");
		setPreferredSize(new Dimension(80, 25));
		setFont(NORMAL_FONT);

		addFocusListener(new java.awt.event.FocusAdapter() {
			@Override
			public void focusGained(final java.awt.event.FocusEvent evt) {
				searchBoxFocusGained(evt);
			}

			@Override
			public void focusLost(final java.awt.event.FocusEvent evt) {
				searchBoxFocusLost(evt);
			}
		});
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(final KeyEvent e) {
				searchBoxKey(e);
			}
		});
		observer.addPropertyChangeListener(new SearchAction());
	}

	private void searchBoxKey(final KeyEvent e) {
		setFont(NORMAL_FONT);
		if (getText().length() > 1 || e.getKeyCode() == KeyEvent.VK_ENTER) {
			observer.firePropertyChange("Searching started", null, getText());
		}
	}

	/**
	 * @param evt
	 */
	private void searchBoxFocusGained(final java.awt.event.FocusEvent evt) {
		setFont(NORMAL_FONT);
		setFont(getFont().deriveFont(Font.PLAIN));
		// setText( null );
	}

	/**
	 * @param evt
	 */
	private void searchBoxFocusLost(final java.awt.event.FocusEvent evt) {
		setFont(NORMAL_FONT);
		setFont(getFont().deriveFont(Font.ITALIC));
		// setText( "Search" );
	}

	private class SearchAction implements PropertyChangeListener, Iterator<Greenobject> {

		private Iterator<Greenobject> iterator;

		private Iterator<Integer> trackIterator;

		public SearchAction() {
			trackIterator = model.getTrackModel().trackIDs(true).iterator();
			if (trackIterator.hasNext()) {
				final Integer currentTrackID = trackIterator.next();
				final Greenobject trackStart = firstGreenobjectOf(currentTrackID);
				iterator = model.getTrackModel().getSortedDepthFirstIterator(trackStart, Greenobject.nameComparator,
						false);
			} else {
				iterator = Collections.EMPTY_LIST.iterator();
			}
		}

		@Override
		public void propertyChange(final PropertyChangeEvent evt) {
			final String text = (String) evt.getNewValue();
			if (!text.isEmpty()) {
				search(text);
			}
		}

		private void search(final String text) {
			Greenobject start = null;
			Greenobject Greenobject;
			while ((Greenobject = next()) != start) {
				if (start == null) {
					start = Greenobject;
				}
				if (Greenobject.getName().contains(text)) {
					view.centerViewOn(Greenobject);
					return;
				}
			}
			setFont(NOTFOUND_FONT);
		}

		@Override
		public boolean hasNext() {
			return true;
		}

		@Override
		public Greenobject next() {
			if (null == iterator || !iterator.hasNext()) {
				if (null == trackIterator || !trackIterator.hasNext()) {
					trackIterator = model.getTrackModel().trackIDs(true).iterator();
				}
				final Integer currentTrackID = trackIterator.next();
				final Greenobject trackStart = firstGreenobjectOf(currentTrackID);
				iterator = model.getTrackModel().getSortedDepthFirstIterator(trackStart, Greenobject.nameComparator,
						false);
			}
			return iterator.next();
		}

		private Greenobject firstGreenobjectOf(final Integer trackID) {
			final List<Greenobject>  trackGreenobjects  = new ArrayList<>(
					model.getTrackModel().trackGreenobjects(trackID));
			Collections.sort(trackGreenobjects, Greenobject.frameComparator);
			return trackGreenobjects.get(0);
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
}

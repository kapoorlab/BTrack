package fiji.plugin.btrackmate.util;

import static fiji.plugin.btrackmate.gui.Fonts.SMALL_FONT;

import java.awt.Color;

import javax.swing.JLabel;

import fiji.plugin.btrackmate.Logger;

public class JLabelLogger extends JLabel {

	private static final long serialVersionUID = 1L;
	private final MyLogger logger;

	public JLabelLogger() {
		this.logger = new MyLogger(this);
		setFont(SMALL_FONT);
	}

	public Logger getLogger() {
		return logger;
	}

	/*
	 * INNER CLASS
	 */

	private class MyLogger extends Logger {

		private final JLabelLogger label;

		public MyLogger(final JLabelLogger logger) {
			this.label = logger;
		}

		@Override
		public void log(final String message, final Color color) {
			label.setText(message);
			label.setForeground(color);
		}

		@Override
		public void error(final String message) {
			log(message, Logger.ERROR_COLOR);
		}

		/** Ignored. */
		@Override
		public void setProgress(final double val) {
		}

		@Override
		public void setStatus(final String status) {
			log(status, Logger.BLUE_COLOR);
		}

	}
}

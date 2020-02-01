package org.mastodon.revised.ui;

public interface ProgressListener
{
	public void showStatus( String string );

	public void showProgress( int current, int total );

	public void clearStatus();
}

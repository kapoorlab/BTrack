package tracker;

public enum GREENDimension {

	NONE, POSITION, VELOCITY, LENGTH, ANGLE, INTENSITY, INTENSITY_SQUARED,RATE, // we separate length and
	// position so that
	// x,y,z are plotted on
	// a different graph
	// from spot sizes
TIME, // count per frames
STRING; // for non-numeric features
	
}
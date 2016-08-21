package com.autumnsinger.opencv.util;

public interface Callback {
	void onBefore();

	boolean onRun();

	void onAfter(boolean b);
}

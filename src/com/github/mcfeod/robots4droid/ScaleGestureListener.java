package com.github.mcfeod.robots4droid;

import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener;

public class ScaleGestureListener extends SimpleOnScaleGestureListener{

	private final DrawThread mDrawThread;

	public ScaleGestureListener(DrawThread drawThread){
		mDrawThread = drawThread;
	}

	@Override
	public boolean onScale(ScaleGestureDetector detector) {
		int d = (int)(detector.getCurrentSpan()-detector.getPreviousSpan())/3;
		mDrawThread.changeCellSize(d);
		mDrawThread.draw();
		return true;
	}

}
package com.github.mcfeod.robots4droid;

import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;

class ScrollGestureListener extends SimpleOnGestureListener{

	private final DrawThread mDrawThread;

	public ScrollGestureListener(DrawThread drawThread){
		mDrawThread = drawThread;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY){
		mDrawThread.changeStartPos((int)distanceX,(int)distanceY);
		mDrawThread.draw();
		return true;
	}

}
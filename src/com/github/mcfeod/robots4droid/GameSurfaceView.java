package com.github.mcfeod.robots4droid;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameSurfaceView extends SurfaceView implements SurfaceHolder.Callback{

	private final SurfaceHolder mSurfaceHolder;
	public DrawThread mDrawThread;
	private final Context context;
	private GestureDetector gestureDetector;
	private ScaleGestureDetector scaleGestureDetector;

	/** Три следующих метода обязательны, не удалять! */
	/** Создание области рисования */
	//@Override
	public void surfaceCreated(SurfaceHolder holder){
	}

	/** Изменение области рисования */
	//@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height){
	}

	/** Уничтожение области рисования */
	//@Override
	public void surfaceDestroyed(SurfaceHolder holder){
	}

	/** Конструктор */
	public GameSurfaceView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		mSurfaceHolder = getHolder();
		mSurfaceHolder.addCallback(this);
		this.context = context;
	}

	public DrawThread getDrawThread(){
		return mDrawThread;
	}

	public void CreateThread(){
		mDrawThread = new DrawThread(mSurfaceHolder, context, this);
		gestureDetector = new GestureDetector(context,
			new ScrollGestureListener(mDrawThread));
		scaleGestureDetector = new ScaleGestureDetector(context,
			new ScaleGestureListener(mDrawThread));
	}

	public void StartThread(){
		mDrawThread.start();
	}
	public void StopThread(){
		mDrawThread.customKill();
		mDrawThread = null;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getPointerCount()){
			case 1: gestureDetector.onTouchEvent(event);
			case 2: scaleGestureDetector.onTouchEvent(event);
		}
		return true;
	}

}

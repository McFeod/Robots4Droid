package com.github.mcfeod.robots4droid;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameSurfaceView extends SurfaceView implements SurfaceHolder.Callback{

	private SurfaceHolder mSurfaceHolder;
	//public DrawThread mDrawThread;
    public DrawThread1 mDrawThread;
	World world;
	Context context;
	Point startTouchPos, endTouchPos;

	/** Три следующих метода обязательны, не удалять! */
	/** Создание области рисования */
	//@Override
	public void surfaceCreated(SurfaceHolder holder){
        mDrawThread = new DrawThread1(mSurfaceHolder, context, world, this);
        mDrawThread.start();
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
        startTouchPos = new Point(0,0);
        endTouchPos = new Point(0,0);
    }
    
    public void SetWorld(World world){
    	this.world = world;
    }
    
	public void StopThread(){
    	//mDrawThread.customKill();
        mDrawThread.interrupt();
        try {
            mDrawThread.join();
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }
        Log.d("View","Thread interrupted");
    	mDrawThread = null;
    }
    
	@Override
    public boolean onTouchEvent(MotionEvent event) {
        final int amplifier = 8;
		switch (event.getAction()) {
			//событие возникает при нажатии на экран
			case MotionEvent.ACTION_DOWN:
				//запоминаем координаты нажатия
				startTouchPos.x = (int) event.getX();
				startTouchPos.y = (int) event.getY();
				break;
			//событие возникает при движении по экрану
			case MotionEvent.ACTION_MOVE:
				//запоминаем координаты касания
				endTouchPos.x = (int) event.getX();
				endTouchPos.y = (int) event.getY();

				/*перерисовываем поле startTouchPos.x - endTouchPos.x и
				  startTouchPos.y - endTouchPos.y - разница между предыдущей
				  точкой касания и текущей. Определяет, на сколько необходимо
				  передвинуть поле по x и по y*/
                if(mDrawThread.getState() == Thread.State.RUNNABLE)  {
//                    mDrawThread.ChangeStartPos(startTouchPos.x - endTouchPos.x,
//                            startTouchPos.y - endTouchPos.y);
//                    mDrawThread.Draw();
//                    mDrawThread.delay(200);
                      mDrawThread.setTargetCorner((startTouchPos.x - endTouchPos.x)*amplifier,
                                                  (startTouchPos.y - endTouchPos.y)*amplifier);
                      startTouchPos.x = endTouchPos.x;
                      startTouchPos.y = endTouchPos.y;
                }
		}
		return true;
	}
}

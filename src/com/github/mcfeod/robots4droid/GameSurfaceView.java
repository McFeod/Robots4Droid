package com.github.mcfeod.robots4droid;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameSurfaceView extends SurfaceView implements SurfaceHolder.Callback{

	private SurfaceHolder mSurfaceHolder;
	public DrawThread mDrawThread;
	World world;
	Context context;
	Point startTouchPos, endTouchPos;

	/** “ри следующих метода об€зательны, не удал€ть! */
	/** —оздание области рисовани€ */
	//@Override
	public void surfaceCreated(SurfaceHolder holder){
	}

	/** »зменение области рисовани€ */
	//@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height){
	}

	/** ”ничтожение области рисовани€ */
	//@Override
	public void surfaceDestroyed(SurfaceHolder holder){
	}
    
	/**  онструктор */
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
    
    public void CreateThread(){
        mDrawThread = new DrawThread(mSurfaceHolder, context, world, this);
    }
    
    public void StartThread(){
    	//mDrawThread.setRunning(true);
    	mDrawThread.start();
    }
    
	@Override
    public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
			//событие возникает при нажатии на экран
			case MotionEvent.ACTION_DOWN:
				//запоминаем координаты нажати€
				startTouchPos.x = (int) event.getX();
				startTouchPos.y = (int) event.getY();
				break;
			//событие возникает при движении по экрану
			case MotionEvent.ACTION_MOVE:
				//запоминаем координаты касани€
				endTouchPos.x = (int) event.getX();
				endTouchPos.y = (int) event.getY();
				/*перерисовываем поле startTouchPos.x - endTouchPos.x и
				  startTouchPos.y - endTouchPos.y - разница между предыдущей
				  точкой касани€ и текущей. ќпредел€ет, на сколько необходимо
				  передвинуть поле по x и по y*/
				mDrawThread.ChangeStartPos(startTouchPos.x - endTouchPos.x,
				 startTouchPos.y - endTouchPos.y);
				mDrawThread.Draw();
				//запоминаем новые координаты начальной точки касани€
				startTouchPos.x = endTouchPos.x;
				startTouchPos.y = endTouchPos.y;
				break;
		}
		return true;
	}
    
}

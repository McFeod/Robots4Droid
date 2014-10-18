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

	/** ��� ��������� ������ �����������, �� �������! */
	/** �������� ������� ��������� */
	//@Override
	public void surfaceCreated(SurfaceHolder holder){
	}

	/** ��������� ������� ��������� */
	//@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height){
	}

	/** ����������� ������� ��������� */
	//@Override
	public void surfaceDestroyed(SurfaceHolder holder){
	}
    
	/** ����������� */
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
			//������� ��������� ��� ������� �� �����
			case MotionEvent.ACTION_DOWN:
				//���������� ���������� �������
				startTouchPos.x = (int) event.getX();
				startTouchPos.y = (int) event.getY();
				break;
			//������� ��������� ��� �������� �� ������
			case MotionEvent.ACTION_MOVE:
				//���������� ���������� �������
				endTouchPos.x = (int) event.getX();
				endTouchPos.y = (int) event.getY();
				/*�������������� ���� startTouchPos.x - endTouchPos.x �
				  startTouchPos.y - endTouchPos.y - ������� ����� ����������
				  ������ ������� � �������. ����������, �� ������� ����������
				  ����������� ���� �� x � �� y*/
				mDrawThread.ChangeStartPos(startTouchPos.x - endTouchPos.x,
				 startTouchPos.y - endTouchPos.y);
				mDrawThread.Draw();
				//���������� ����� ���������� ��������� ����� �������
				startTouchPos.x = endTouchPos.x;
				startTouchPos.y = endTouchPos.y;
				break;
		}
		return true;
	}
    
}

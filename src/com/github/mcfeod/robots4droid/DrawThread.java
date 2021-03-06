package com.github.mcfeod.robots4droid;


import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.preference.PreferenceManager;
import android.view.SurfaceHolder;

public class DrawThread extends Thread {
	private Bitmap bitRobot, bitFastRobot, bitPlayer, bitCell, bitCell2,
	 bitJunk, bitMine;
	private final Bitmap bitRobotOriginal;
	private final Bitmap bitFastRobotOriginal;
	private final Bitmap bitPlayerOriginal;
	private final Bitmap bitCellOriginal;
	private final Bitmap bitCell2Original;
	private final Bitmap bitJunkOriginal;
	private final Bitmap bitMineOriginal;
	private int widthPX=35;
	private int heightPX=35;//размеры клетки в пикселях
	private World world;
	private Canvas canvas;
	private Paint paint;
	private Point startPos, movePos;
	private GameSurfaceView view;
	private boolean drawing = false, scrollingToPlayer = false, mustDie = false;
	private int step = 8;
	private int indent=30;
	private int interval = 16;
	private final SurfaceHolder mSurfaceHolder; //Область для рисования

	private Random mLSDRandom;
	private Paint mLSDPaint;
	private boolean isLSD, isFullLSD;

	public DrawThread(SurfaceHolder surfaceHolder, Context context, GameSurfaceView view){
		isLSD = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("LSD", false);
		isFullLSD = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("full_LSD", false);
		mSurfaceHolder = surfaceHolder;
		startPos = new Point(0, 0);
		movePos = new Point(0, 0);
		this.view = view;
		paint = new Paint();
		mLSDRandom = new Random();
		mLSDPaint = new Paint();

		bitRobotOriginal=BitmapFactory.decodeResource(context.getResources(),R.drawable.robot);
		bitFastRobotOriginal=BitmapFactory.decodeResource(context.getResources(),R.drawable.fastrobot);
		bitPlayerOriginal=BitmapFactory.decodeResource(context.getResources(),R.drawable.player); 
		bitJunkOriginal=BitmapFactory.decodeResource(context.getResources(),R.drawable.junk);               
		bitCellOriginal=BitmapFactory.decodeResource(context.getResources(),R.drawable.cell);
		bitCell2Original=BitmapFactory.decodeResource(context.getResources(),R.drawable.cell2);
		bitMineOriginal=BitmapFactory.decodeResource(context.getResources(),R.drawable.mine);
	}

	public void setScrolling(boolean scrolling){
		scrollingToPlayer = scrolling;
	}

	public void setWorld(World world){
		this.world = world;
	}
		//подгоняем размеры bitmap-ов под размеры клеток
	void changeBitmapSize(boolean toCompare){
		boolean isEqual = false;
		if (toCompare)
			if (bitRobot.getWidth() == widthPX - 2)
				isEqual = true;
		if (!isEqual){
			bitRobot = Bitmap.createScaledBitmap(bitRobotOriginal, widthPX-2,
			 heightPX-2, false);
			bitFastRobot = Bitmap.createScaledBitmap(bitFastRobotOriginal, widthPX-2,
			 heightPX-2,false);
			bitPlayer = Bitmap.createScaledBitmap(bitPlayerOriginal, widthPX-2,
			 heightPX-2,false);
			bitJunk = Bitmap.createScaledBitmap(bitJunkOriginal, widthPX-2,
			 heightPX-2,false);
			bitCell2 = Bitmap.createScaledBitmap(bitCell2Original, widthPX-2,
			 heightPX-2,false);
			bitCell = Bitmap.createScaledBitmap(bitCellOriginal, widthPX-2,
			 heightPX-2,false);
			bitMine = Bitmap.createScaledBitmap(bitMineOriginal, widthPX-2,
			 heightPX-2,false);
		}
	}

	void changeStep(){
		double l = (Math.sqrt((startPos.x-movePos.x)*(startPos.x-movePos.x)+
		 (startPos.y-movePos.y)*(startPos.y-movePos.y)));		
		if (l < widthPX * 2)
			l *= 3;
		step = (int)(l / (200 / interval));
		if (step == 0)
			step = 1;
	}

	void changeIndent(){
		indent = widthPX / 3;
	}

	void checkCellSize(){
		if (widthPX * world.getWidth() + indent * 2 < view.getWidth()){
			heightPX = widthPX = (view.getWidth() - indent * 2) / world.getWidth();
		}
		if (heightPX * world.getHeight() + indent * 2 < view.getHeight()){
			widthPX = heightPX = (view.getHeight() - indent * 2) / world.getHeight();
		}
		if (widthPX * 5 > view.getWidth()){
			heightPX = widthPX = view.getWidth() / 5;
		}
		if (heightPX * 5 > view.getHeight()){
			widthPX = heightPX = view.getHeight() / 5;
		}
	}

	public void setDefaultCellSize(){
		heightPX = widthPX = view.getWidth() / world.getWidth() * 2;
		checkCellSize();
		changeStep();
		changeIndent();
		changeBitmapSize(false);
	}

	public void changeCellSize(int d){
		if (drawing)
			return;
		float procX = (startPos.x+view.getWidth()/2) / (float)(world.getWidth()*widthPX);
		float procY = (startPos.y+view.getHeight()/2) / (float)(world.getHeight()*heightPX);
		widthPX += d;
		heightPX += d;
		checkCellSize();
		changeStep();
		changeIndent();
		changeBitmapSize(true);
		int newStartPosX = (int)((world.getWidth()*widthPX)*procX - view.getWidth()/2);
		int newStartPosY = (int)((world.getHeight()*heightPX)*procY - view.getHeight()/2);
		changeStartPos(newStartPosX-startPos.x, newStartPosY-startPos.y);
		if (scrollingToPlayer)
			scrollToPlayer();
	}
	
	public void delay(int n){
		while (scrollingToPlayer)
			try{
				Thread.sleep(n);
			}catch (InterruptedException e) {}
	}

	@Override
	public void run()
	{
		while (!mustDie){
			try{
			Thread.sleep(8);
			}catch (InterruptedException e) {}
			
			if (scrollingToPlayer){
				if (movePos.x - startPos.x >= step)
					startPos.x += step;
				else
					if (movePos.x - startPos.x <= -step)
						startPos.x -= step;
					else
						startPos.x = movePos.x;
				if (movePos.y - startPos.y >= step)
					startPos.y += step;
				else
					if (movePos.y - startPos.y <= -step)
						startPos.y -= step;
					else
						startPos.y = movePos.y;
				changeStartPos(0, 0);
				if ((startPos.x == movePos.x) && (startPos.y == movePos.y))
					scrollingToPlayer = false;
				drawing = true;
			}
			
			if (drawing){
				try{
					canvas = mSurfaceHolder.lockCanvas();
					synchronized (mSurfaceHolder){
						repaint();
					}
				}
				catch (Exception e) { }
				finally{
					if (canvas != null)
						mSurfaceHolder.unlockCanvasAndPost(canvas);
				}
				drawing = false;
			}
		}
	}

	public void draw(){
		drawing = true;
	}
		
	public void scrollToPlayer(){
		movePos.x = (world.player.getPos().x*widthPX+widthPX/2) - view.getWidth()/2 + indent;
		movePos.y = (world.player.getPos().y*heightPX+heightPX/2) - view.getHeight()/2 + indent;
		int boardX=world.getWidth()*widthPX+indent*2;
		int boardY=world.getHeight()*heightPX+indent*2;
		//проверка выхода за пределы экрана
		if (movePos.x < 0)
			movePos.x = 0;
		if (movePos.y < 0)
			movePos.y = 0;
		if (movePos.x + view.getWidth() > boardX)
			movePos.x = boardX - view.getWidth();
		if (movePos.y + view.getHeight() > boardY)
			movePos.y = boardY - view.getHeight();
		changeStep();
		scrollingToPlayer = true;
	}
	
	boolean isVisible(int x, int y){
		int startX = x * widthPX + indent;
		int startY = y * heightPX + indent;
		int endX = (x+1) * widthPX + indent;
		int endY = (y+1) * heightPX + indent;
		int left = startPos.x;
		int left_width = startPos.x + view.getWidth();
		int top = startPos.y;
		int top_height = startPos.y + view.getHeight();
		return ((((startX >= left) && (startX <= left_width)) ||
		 ((endX >= left) && (endX <= left_width))) &&
		 (((startY >= top) && (startY <= top_height)) ||
		 ((endY >= top) && (endY <= top_height))));
	}
	
	void repaint(){
		Canvas lsdCanvas = new Canvas(bitCell2);
		Canvas fulllsdCanvas = new Canvas(bitCell);
		canvas.drawColor(Color.GRAY);
		for (int i=0; i<world.getWidth(); i++)
			for (int j=0; j<world.getHeight(); j++)
				if (isVisible(i,j)){
					if ((i+j)%2 == 0){
						if (isLSD){
							mLSDPaint.setColor(mLSDRandom.nextInt());
							lsdCanvas.drawRect(1, 1, bitCell2.getWidth()-1,
							 bitCell2.getHeight()-1, mLSDPaint);
						}
						canvas.drawBitmap(bitCell2,widthPX*i+indent-startPos.x,
						 heightPX*j+indent-startPos.y,paint);
					}else{
						if (isLSD && isFullLSD){
							mLSDPaint.setColor(mLSDRandom.nextInt());
							fulllsdCanvas.drawRect(1, 1, bitCell.getWidth()-1,
							 bitCell.getHeight()-1, mLSDPaint);
						}
						canvas.drawBitmap(bitCell,widthPX*i+indent-startPos.x,
						 heightPX*j+indent-startPos.y,paint);
					}
					switch (world.board.GetKind(i,j)){
						case Board.JUNK:
							canvas.drawBitmap(bitJunk,widthPX*i+indent-startPos.x,
							 heightPX*j+indent-startPos.y,paint);
							break;
						case Board.ROBOT:
							canvas.drawBitmap(bitRobot,widthPX*i+indent-startPos.x,
							 heightPX*j+indent-startPos.y,paint);
							break;
						case Board.FASTROBOT:
							canvas.drawBitmap(bitFastRobot,widthPX*i+indent-startPos.x,
							 heightPX*j+indent-startPos.y,paint);
							break;
						case Board.MINE:
							canvas.drawBitmap(bitMine,widthPX*i+indent-startPos.x,
							 heightPX*j+indent-startPos.y,paint);
							break;
					}
				}
		canvas.drawBitmap(bitPlayer,world.player.getPos().x*widthPX+indent-startPos.x,
		 world.player.getPos().y*heightPX+indent-startPos.y,paint);
	}

	public void changeStartPos(int dX, int dY){
			if (drawing){
				return;
			}
			//меняем начальную точку
			startPos.x += dX;
			startPos.y += dY;
			//int screenX = view.getWidth(), screenY = view.getHeight();
			int boardX=world.getWidth()*widthPX+indent*2;
			int boardY=world.getHeight()*heightPX+indent*2;
			//проверка выхода за пределы экрана
			if (startPos.x < 0)
				startPos.x = 0;
			if (startPos.y < 0)
				startPos.y = 0;
			if (startPos.x + view.getWidth() > boardX)
				startPos.x = boardX - view.getWidth();
			if (startPos.y + view.getHeight() > boardY)
				startPos.y = boardY - view.getHeight();
	}

	public void customKill(){
		mustDie = true;
		this.interrupt();
	}
}

package com.github.mcfeod.robots4droid;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;

public class DrawThread extends Thread {
	
	private Bitmap bitRobot, bitFastRobot, bitPlayer, bitCell, bitCell2,
	 bitJunk, bitMine;
	private Bitmap bitRobotOriginal, bitFastRobotOriginal, bitPlayerOriginal,
	 bitCellOriginal, bitCell2Original, bitJunkOriginal, bitMineOriginal;
	public int widthPX=35, heightPX=35;//размеры клетки в пикселях
	private World world;
	private Canvas canvas;
	private Paint paint;
	private Point startPos, movePos;
	private GameSurfaceView view;
	private boolean drawing = false, scrollingToPlayer = false, mustDie = false;
	private int step = 8;
	private int indent=30;
	private int interval = 16;
	private SurfaceHolder mSurfaceHolder; //Область для рисования
	
	public DrawThread(SurfaceHolder surfaceHolder, Context context, GameSurfaceView view){
    	mSurfaceHolder = surfaceHolder;
        startPos = new Point(0, 0);
        movePos = new Point(0, 0);
        this.view = view;
        paint = new Paint();
        
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
	
	public void changeBitmapSize(boolean toCompare){
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

	public void changeStep(){
		double l = (Math.sqrt((startPos.x-movePos.x)*(startPos.x-movePos.x)+
		 (startPos.y-movePos.y)*(startPos.y-movePos.y)));		
		if (l < widthPX * 2)
			l *= 3;
		step = (int)(l / (200 / interval));
		if (step == 0)
			step = 1;
	}
	
	public void changeIndent(){
		indent = widthPX / 3;
	}
	
	public void checkCellSize(){
		if (widthPX * world.getWidth() + indent * 2 < view.getWidth()){
			widthPX = (view.getWidth() - indent * 2) / world.getWidth();
			heightPX = widthPX;
		}
		if (heightPX * world.getHeight() + indent * 2 < view.getHeight()){
			heightPX = (view.getHeight() - indent * 2) / world.getHeight();
			widthPX = heightPX;
		}
		if (widthPX * 5 > view.getWidth()){
			widthPX = view.getWidth() / 5;
			heightPX = widthPX;
		}
		if (heightPX * 5 > view.getHeight()){
			heightPX = view.getHeight() / 5;
			widthPX = heightPX;
		}
	}
	
	public void setDefaultCellSize(){
    	widthPX = view.getWidth() / world.getWidth() * 2;
    	heightPX = widthPX;
    	checkCellSize();
    	changeStep();
    	changeIndent();
    	changeBitmapSize(false);
	}
	
	public void changeCellSize(int d){
		widthPX += d;
		heightPX += d;
		checkCellSize();
		changeStep();
		changeIndent();
		changeBitmapSize(true);		
		changeStartPos(0, 0);
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
    
    public boolean isVisible(int x, int y){
    	int startX = x * widthPX + indent;
    	int startY = y * heightPX + indent;
    	int endX = (x+1) * widthPX + indent;
    	int endY = (y+1) * heightPX + indent;
    	int left = startPos.x;
    	int left_width = startPos.x + view.getWidth();
    	int top = startPos.y;
    	int top_height = startPos.y + view.getHeight();    	
    	if ((((startX >= left) && (startX <= left_width)) ||
    	 ((endX >= left) && (endX <= left_width))) &&
    	 (((startY >= top) && (startY <= top_height)) ||
    	 ((endY >= top) && (endY <= top_height))))
    		return true;
    	return false;
    }
    
    public void repaint(){
		canvas.drawColor(Color.GRAY);
		for (int i=0; i<world.getWidth(); i++)
        	for (int j=0; j<world.getHeight(); j++)
        		if (isVisible(i,j)){
        			if ((i+j)%2 == 0)
        				canvas.drawBitmap(bitCell2,widthPX*i+indent-startPos.x,
        				 heightPX*j+indent-startPos.y,paint);
        			else
        				canvas.drawBitmap(bitCell,widthPX*i+indent-startPos.x,
               			 heightPX*j+indent-startPos.y,paint);
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

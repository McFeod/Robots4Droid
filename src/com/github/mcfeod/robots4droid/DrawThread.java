package com.github.mcfeod.robots4droid;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.widget.TextView;

public class DrawThread extends Thread {
	
	private Bitmap bitRobot, bitFastRobot, bitPlayer, bitCell, bitCell2,
	 bitJunk, bitMine;
	private Bitmap bitRobotOriginal, bitFastRobotOriginal, bitPlayerOriginal,
	 bitCellOriginal, bitCell2Original, bitJunkOriginal, bitMineOriginal;
	public int widthPX=35, heightPX=35;//размеры клетки в пикселях
	private World world;
	private int indent=30;
	private Canvas canvas;
	private Paint paint;
	private Point startPos, movePos;
	private GameSurfaceView view;
	private boolean toDraw, toMove, mustDie = false;
	private int step = 8;
	private SurfaceHolder mSurfaceHolder; //Область для рисования
	Context context;
	TextView text;
	Activity activity;
	
	public DrawThread(SurfaceHolder surfaceHolder, Context context, World world,
	 GameSurfaceView view){
    	mSurfaceHolder = surfaceHolder;
        this.world = world;
        startPos = new Point(0, 0);
        movePos = new Point(0, 0);
        this.view = view;
        toDraw = false;
        toMove = false;
        this.context = context;
        paint = new Paint();
        
        bitRobotOriginal=BitmapFactory.decodeResource(context.getResources(),R.drawable.robot);
        bitFastRobotOriginal=BitmapFactory.decodeResource(context.getResources(),R.drawable.fastrobot);
        bitPlayerOriginal=BitmapFactory.decodeResource(context.getResources(),R.drawable.player); 
        bitJunkOriginal=BitmapFactory.decodeResource(context.getResources(),R.drawable.junk);               
        bitCellOriginal=BitmapFactory.decodeResource(context.getResources(),R.drawable.cell);
        bitCell2Original=BitmapFactory.decodeResource(context.getResources(),R.drawable.cell2);
        bitMineOriginal=BitmapFactory.decodeResource(context.getResources(),R.drawable.mine);
    }
	
	public boolean GetToMove(){
		return toMove;
	}
	
	public void SetToMove(boolean toMove){
		this.toMove = toMove;
	}
	
	public void ChangeBitmapSize(boolean toCompare){
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

	public void ChangeStep(){
		//step = widthPX / 3;
		step = (int)(Math.sqrt((startPos.x-movePos.x)*(startPos.x-movePos.x)+
		 (startPos.y-movePos.y)*(startPos.y-movePos.y))/10);
	}
	
	public void ChangeIndent(){
		indent = widthPX / 3;
	}
	
	public void CheckCellSize(){
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
	
	public void SetDefaultCellSize(){
    	widthPX = view.getWidth() / world.getWidth() * 2;
    	heightPX = widthPX;
    	CheckCellSize();
    	ChangeStep();
    	ChangeIndent();
    	ChangeBitmapSize(false);
	}
	
	public void ChangeCellSize(int d){
		widthPX += d;
		heightPX += d;
		CheckCellSize();
		ChangeStep();
		ChangeIndent();
		ChangeBitmapSize(true);		
		ChangeStartPos(0, 0);
		if (toMove)
			MoveToPlayer();
	}

	
	public void delay(int n){
		while (toMove)
	    	try{
	    		Thread.sleep(n);
	        }catch (InterruptedException e) {}
	}
	
    @Override
    public void run()
    {
        while (!mustDie){
        	try{
        		Thread.sleep(40);
            }catch (InterruptedException e) {}
        	
        	if (toMove){
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
    			ChangeStartPos(0, 0);
    			if ((startPos.x == movePos.x) && (startPos.y == movePos.y))
        			toMove = false;
        		toDraw = true;
        	}
        	
        	if (toDraw){
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
        		toDraw = false;
        	}
        }
    }
    
    public void SetActivity(Activity activity){
    	this.activity = activity;
    }
        
    public void Draw(){
    	toDraw = true;
    }
    
    public void SetText(TextView text){
    	this.text = text;
    }
    
    public void MoveToPlayer(){
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
        ChangeStep();
    	toMove = true;
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
    
    public void ChangeStartPos(int dX, int dY){
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

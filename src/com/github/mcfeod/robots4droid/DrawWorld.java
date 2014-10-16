package com.github.mcfeod.robots4droid;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.widget.ImageView;

public class DrawWorld {
		
	private ImageView image;
	private World world;
	private Canvas canvas;
	private Paint paint;
	private Bitmap bitMain, bitRobot, bitFastRobot, bitPlayer, bitCell, bitCell2,
	 bitJunk, bitMine, bitRobotMirror;
	public int widthPX, heightPX;
	private Point screen;
	private Point startPos; //координаты, на которые смещается поле
	boolean isMirror = false;
	private byte indent = 30; //отступ вокруг поля

	//объекты и переменные отвечающие за плавное перемещение поля
	private boolean toMove;
	private MoveRunnable moveRunnable;
	private MoveTimerTask moveTimerTask;
	private Timer moveTimer;
	private Activity activity;
	private Point movePos;
	
	public DrawWorld(Context context, ImageView image, World world, Point screen,
	 int widthPX, int heightPX, Activity activity){
		this.image = image;
		this.world = world;
		this.screen = new Point(screen);
		this.widthPX = widthPX;
		this.heightPX = heightPX;
        startPos = new Point(0, 0);
        this.activity = activity;
        movePos = new Point(0, 0);
        moveRunnable = new MoveRunnable();
        moveTimerTask = new MoveTimerTask();
        moveTimer = new Timer();
        toMove=false;
        moveTimer.schedule(moveTimerTask, 20, 20);
        
        
		bitMain=Bitmap.createBitmap(widthPX*world.mWidth + indent * 2,
		 heightPX*world.mHeight + indent * 2,Bitmap.Config.ARGB_8888);
        bitRobot=BitmapFactory.decodeResource(context.getResources(),R.drawable.robot);
    	bitFastRobot=BitmapFactory.decodeResource(context.getResources(),R.drawable.fastrobot);
        bitPlayer=BitmapFactory.decodeResource(context.getResources(),R.drawable.player); 
        bitJunk=BitmapFactory.decodeResource(context.getResources(),R.drawable.junk);
        
		bitCell=BitmapFactory.decodeResource(context.getResources(),R.drawable.cell);
		bitCell2=BitmapFactory.decodeResource(context.getResources(),R.drawable.cell2);
		bitMine=BitmapFactory.decodeResource(context.getResources(),R.drawable.mine); 
		bitRobotMirror=BitmapFactory.decodeResource(context.getResources(),R.drawable.robot_mirror); 

        //подгоняем размеры bitmap-ов под размеры клеток
		bitRobot=Bitmap.createScaledBitmap(bitRobot,widthPX-2,heightPX-2,false);
	    bitFastRobot=Bitmap.createScaledBitmap(bitFastRobot,widthPX-2,heightPX-2,false);
	    bitPlayer=Bitmap.createScaledBitmap(bitPlayer,widthPX-2,heightPX-2,false);
	    bitJunk=Bitmap.createScaledBitmap(bitJunk,widthPX-2,heightPX-2,false);
	    
	    bitCell2=Bitmap.createScaledBitmap(bitCell2,widthPX-2,heightPX-2,false);
	    bitCell=Bitmap.createScaledBitmap(bitCell,widthPX-2,heightPX-2,false);
	    bitMine=Bitmap.createScaledBitmap(bitMine,widthPX-2,heightPX-2,false);
	    bitRobotMirror=Bitmap.createScaledBitmap(bitRobotMirror,widthPX-2,heightPX-2,false);

        canvas = new Canvas(bitMain);
        paint = new Paint();
	}
	
	class MoveRunnable implements Runnable{
		public void run(){
			if (movePos.x - startPos.x >= 5)
				startPos.x += 5;
			else
				if (movePos.x - startPos.x <= -5)
					startPos.x -= 5;
				else
					startPos.x = movePos.x;
			if (movePos.y - startPos.y >= 5)
				startPos.y += 5;
			else
				if (movePos.y - startPos.y <= -5)
					startPos.y -= 5;
				else
					startPos.y = movePos.y;
			repaint(0,0);
		}
	}
	
	class MoveTimerTask extends TimerTask{
		@Override
		public void run(){
			if (toMove){
				activity.runOnUiThread(moveRunnable);
				if ((movePos.x == startPos.x) && (movePos.y == startPos.y) && (toMove))
					toMove=false;
			}
		}
	}
	
	//передвигает экран так, чтобы игрок оказался в центре
	public void centerPlayerPos(int x, int y){
		startPos.x = x - (int) (screen.x / 2) + indent;
		startPos.y = y - (int) (screen.y / 2) + indent;
		repaint(0, 0);
	}
	
	//перерисовывает все поле в bitMain, не выводит на Image
	public void mainRepaint(){
		canvas.drawColor(Color.WHITE);
		for (int i=0; i<world.mWidth; i++)
        	for (int j=0; j<world.mHeight; j++){
        		if ((i+j)%2 == 0)
    				canvas.drawBitmap(bitCell2,widthPX*i+indent,heightPX*j+indent,paint);
    			else
    				canvas.drawBitmap(bitCell,widthPX*i+indent,heightPX*j+indent,paint);
       			switch (world.mBoard.GetKind(i,j)){
        			case Board.JUNK:
        				canvas.drawBitmap(bitJunk,i*widthPX+1+indent,j*heightPX+1+indent,paint);
        				break;
        			case Board.ROBOT:
        				if (isMirror)
        					canvas.drawBitmap(bitRobot,i*widthPX+1+indent,j*heightPX+1+indent,paint);
        				else
        					canvas.drawBitmap(bitRobotMirror,i*widthPX+1+indent,j*heightPX+1+indent,paint);
        				break;
        			case Board.FASTROBOT:
        				canvas.drawBitmap(bitFastRobot,i*widthPX+1+indent,j*heightPX+1+indent,paint);
        				break;
        			case Board.MINE:
        				canvas.drawBitmap(bitMine,i*widthPX+1+indent,j*heightPX+1+indent,paint);
        				break;
        		}
        	}
        isMirror = !isMirror;
        canvas.drawBitmap(bitPlayer,world.player.getPos().x*widthPX+1+indent,
         world.player.getPos().y*heightPX+1+indent,paint);
	}
	
	//рисует часть bitMain размером screen.x*screen.y, начиная с точки startPos
	public void repaint(int dX, int dY){
		//меняем начальную точку
        startPos.x += dX;
        startPos.y += dY;
        int screenX = screen.x, screenY = screen.y;
        //проверка выхода за пределы экрана
        if (startPos.x < 0)
        	startPos.x = 0;
        if (startPos.y < 0)
        	startPos.y = 0;
        if (startPos.x + screenX > bitMain.getWidth())
        	startPos.x = bitMain.getWidth() - screenX;
        if (startPos.y + screenY > bitMain.getHeight())
        	startPos.y = bitMain.getHeight() - screenY;
        if (screenX > bitMain.getWidth()){
        	startPos.x = 0;
        	screenX = bitMain.getWidth();        	
        }
        if (screenY > bitMain.getHeight()){
        	startPos.y = 0;
        	screenY = bitMain.getHeight();        	
        }
        //рисование
        image.setImageBitmap(Bitmap.createBitmap(bitMain, startPos.x, startPos.y,
         screenX, screenY));
	}
	
	public void moveTo(int x, int y){
		movePos.x = x - (int)screen.x/2 + indent;
		movePos.y = y - (int)screen.y/2 + indent;
		if (movePos.x < 0)
			movePos.x = 0;
        if (movePos.y < 0)
        	movePos.y = 0;
        if (movePos.x + screen.x > bitMain.getWidth())
        	movePos.x = bitMain.getWidth() - screen.x;
        if (movePos.y + screen.y > bitMain.getHeight())
        	movePos.y = bitMain.getHeight() - screen.y;
        toMove=true;
	}
	
	public void death(){
		canvas.drawColor(Color.BLACK);
		paint.setColor(Color.RED);
		paint.setTextSize(50);
		canvas.drawText("GAME OVER", heightPX/2, 200, paint);
	}
}
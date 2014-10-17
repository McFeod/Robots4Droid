package com.github.mcfeod.robots4droid;

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
	 bitJunk, bitMine;
	public int widthPX, heightPX;
	private Point screen;
	private Point startPos; //координаты, на которые смещается поле

	public DrawWorld(Context context, ImageView image, World world, Point screen,
	 int widthPX, int heightPX){
		this.image = image;
		this.world = world;
		this.screen = new Point(screen);
		this.widthPX = widthPX;
		this.heightPX = heightPX;

        startPos = new Point(0, 0);
        
		bitMain=Bitmap.createBitmap(widthPX*world.mWidth,heightPX*world.mHeight,Bitmap.Config.ARGB_8888);
        bitRobot=BitmapFactory.decodeResource(context.getResources(),R.drawable.robot);
    	bitFastRobot=BitmapFactory.decodeResource(context.getResources(),R.drawable.fastrobot);
        bitPlayer=BitmapFactory.decodeResource(context.getResources(),R.drawable.player); 
        bitJunk=BitmapFactory.decodeResource(context.getResources(),R.drawable.junk);
        
		bitCell=BitmapFactory.decodeResource(context.getResources(),R.drawable.cell);
		bitCell2=BitmapFactory.decodeResource(context.getResources(),R.drawable.cell2);
		bitMine=BitmapFactory.decodeResource(context.getResources(),R.drawable.mine); 

        //подгоняем размеры bitmap-ов под размеры клеток
		bitRobot=Bitmap.createScaledBitmap(bitRobot,widthPX-1,heightPX-1,false);
	    bitFastRobot=Bitmap.createScaledBitmap(bitFastRobot,widthPX-1,heightPX-1,false);
	    bitPlayer=Bitmap.createScaledBitmap(bitPlayer,widthPX-1,heightPX-1,false);
	    bitJunk=Bitmap.createScaledBitmap(bitJunk,widthPX-1,heightPX-1,false);
	    
	    bitCell2=Bitmap.createScaledBitmap(bitCell2,widthPX-1,heightPX-1,false);
	    bitCell=Bitmap.createScaledBitmap(bitCell,widthPX-1,heightPX-1,false);
	    bitMine=Bitmap.createScaledBitmap(bitMine,widthPX-1,heightPX-1,false);
        
        canvas = new Canvas(bitMain);
        paint = new Paint();
	}
	
	//передвигает экран так, чтобы игрок оказался в центре
	public void centerPlayerPos(int x, int y){
		startPos.x = x-(int)(screen.x / 2);
		startPos.y = y-(int)(screen.y / 2);
		repaint(0, 0);
	}
	
	//перерисовывает все поле в bitMain, не выводит на Image
	public void mainRepaint(){  
		canvas.drawColor(Color.WHITE);
        for (int i=0; i<world.mWidth; i++)
        	for (int j=0; j<world.mHeight; j++){
        		if ((i+j)%2 == 0)
    				canvas.drawBitmap(bitCell2,widthPX*i,heightPX*j,paint);
    			else
    				canvas.drawBitmap(bitCell,widthPX*i,heightPX*j,paint);
       			switch (world.mBoard.GetKind(i,j)){        			
        			case Board.JUNK:
        				canvas.drawBitmap(bitJunk,i*widthPX+1,j*heightPX+1,paint);
        				break;
        			case Board.ROBOT:
        				canvas.drawBitmap(bitRobot,i*widthPX+1,j*heightPX+1,paint);
        				break;
        			case Board.FASTROBOT:
        				canvas.drawBitmap(bitFastRobot,i*widthPX+1,j*heightPX+1,paint);
        				break;
        			case Board.MINE:
        				canvas.drawBitmap(bitMine,i*widthPX+1,j*heightPX+1,paint);
        				break;
        		}
        	}
        canvas.drawBitmap(bitPlayer,world.player.getPos().x*widthPX+1,
         world.player.getPos().y*heightPX+1,paint);
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
	
	public void death(){
		repaint(-startPos.x, -startPos.y);
		canvas.drawColor(Color.BLACK);
		paint.setColor(Color.RED);
		paint.setTextSize(50);
		canvas.drawText("GAME OVER", heightPX/2, 200, paint);
	}
}
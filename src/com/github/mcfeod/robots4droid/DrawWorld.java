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
	private Bitmap bitMain, bitRobot, bitFastRobot, bitPlayer, bitJunk, bitMine;
	private int width, height;
	private Point screen;
	private Bitmap bitCell, bitCell2;
	
	public DrawWorld(Context context, ImageView image, World world, Point screen){
		this.image = image;
		this.world = world;
		this.screen = new Point();
		//размеры клеток
        width = (int) screen.y / world.mHeight;
        height = width;
        this.screen.x = (int)(width*world.mWidth);
        this.screen.y = (int)(height*world.mHeight);
        
		bitMain=Bitmap.createBitmap(this.screen.x,this.screen.y,Bitmap.Config.ARGB_8888);
        bitRobot=BitmapFactory.decodeResource(context.getResources(),R.drawable.robot);
    	bitFastRobot=BitmapFactory.decodeResource(context.getResources(),R.drawable.fastrobot);
        bitPlayer=BitmapFactory.decodeResource(context.getResources(),R.drawable.player); 
        bitJunk=BitmapFactory.decodeResource(context.getResources(),R.drawable.junk);
        
		bitCell=BitmapFactory.decodeResource(context.getResources(),R.drawable.cell);
		bitCell2=BitmapFactory.decodeResource(context.getResources(),R.drawable.cell2);
		bitMine=BitmapFactory.decodeResource(context.getResources(),R.drawable.mine); 

        //подгоняем размеры bitmap-ов под размеры клеток
		bitRobot=Bitmap.createScaledBitmap(bitRobot,width-1,height-1,false);
	    bitFastRobot=Bitmap.createScaledBitmap(bitFastRobot,width-1,height-1,false);
	    bitPlayer=Bitmap.createScaledBitmap(bitPlayer,width-1,height-1,false);
	    bitJunk=Bitmap.createScaledBitmap(bitJunk,width-1,height-1,false);
	    
	    bitCell2=Bitmap.createScaledBitmap(bitCell2,width-1,height-1,false);
	    bitCell=Bitmap.createScaledBitmap(bitCell,width-1,height-1,false);
	    bitMine=Bitmap.createScaledBitmap(bitMine,width-1,height-1,false);
        
        canvas = new Canvas(bitMain);
        paint = new Paint();
	}
	
	public void repaint(){   
		canvas.drawColor(Color.WHITE);
        for (int i=0; i<world.mWidth; i++)
        	for (int j=0; j<world.mHeight; j++){
        		if ((i+j)%2 == 0)
    				canvas.drawBitmap(bitCell2,width*i,height*j,paint);
    			else
    				canvas.drawBitmap(bitCell,width*i,height*j,paint);
       			switch (world.mBoard.GetKind(i,j)){        			
        			case Board.JUNK:
        				canvas.drawBitmap(bitJunk,i*width+1,j*height+1,paint);
        				break;
        			case Board.ROBOT:
        				canvas.drawBitmap(bitRobot,i*width+1,j*height+1,paint);
        				break;
        			case Board.FASTROBOT:
        				canvas.drawBitmap(bitFastRobot,i*width+1,j*height+1,paint);
        				break;
        			case Board.MINE:
        				canvas.drawBitmap(bitMine,i*width+1,j*height+1,paint);
        				break;
        		}
        	}
        canvas.drawBitmap(bitPlayer,world.player.getPos().x*width+1,
         world.player.getPos().y*height+1,paint);        
        image.setImageBitmap(bitMain);		
	}
	public void death(){
		canvas.drawColor(Color.BLACK);
		paint.setColor(Color.RED);
		paint.setTextSize(50);
		canvas.drawText("GAME OVER", height/2, 200, paint);
	}
}
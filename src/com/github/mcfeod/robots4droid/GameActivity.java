package com.github.mcfeod.robots4droid;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class GameActivity extends ActionBarActivity {
	private int width=10, height=10; //размеры сторон
	private int sizew, sizeh;
	//главный bitmap
	private Bitmap bitMain;
    //создаем bitmap-ы с картинками из ресурсов
	private Bitmap bitRobot, bitFastRobot, bitPlayer, bitJunk;
    private World world;
    Point screen;
	
	
	
	public Point GetScreenSize(){
		Display display = getWindowManager().getDefaultDisplay();
		DisplayMetrics metrics = new DisplayMetrics();
		display.getMetrics(metrics);
		Point size = new Point(metrics.widthPixels, metrics.heightPixels);
		return size;
	}
	
	public OnCompletionListener playerListener = new OnCompletionListener(){
		@Override
		public void onCompletion(MediaPlayer mp){
	        mp.start();
	    }
	};

	
	public OnClickListener listener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			//MediaPlayer mediaPlayer = MediaPlayer.create(v.getContext(), R.raw.muz); // создаём новый объект mediaPlayer
	        //mediaPlayer.start(); // запускаем воспроизведение
			boolean succ=false;
			switch (v.getId()){
				case R.id.left_button: succ=world.movePlayer((byte)(3)); break;
				case R.id.right_button: succ=world.movePlayer((byte)(5)); break;
				case R.id.up_button: succ=world.movePlayer((byte)(1)); break;
				case R.id.down_button: succ=world.movePlayer((byte)(7)); break;
				case R.id.left_up_button: succ=world.movePlayer((byte)(0)); break;
				case R.id.left_down_button: succ=world.movePlayer((byte)(6)); break;
				case R.id.right_up_button: succ=world.movePlayer((byte)(2)); break;
				case R.id.right_down_button: succ=world.movePlayer((byte)(8)); break;	
				case R.id.stay_button: succ=world.movePlayer((byte)(4)); break;	
				case R.id.teleport_button: succ=world.movePlayer((byte)(9)); break;	
				case R.id.safe_teleport_button: succ=world.movePlayer((byte)(10)); break;	
			}
			if (succ)
				world.moveBots();
			repaint();
			TextView text=(TextView)findViewById(R.id.textView1);
			String str="Level: "+Integer.toString(world.getLevel())+
			 "  Score: "+Integer.toString(world.player.getScore())+
			 "  Energy: "+Integer.toString(world.player.getEnergy())+
			 "  isAlive: "+world.player.isAlive;
			text.setText(str);
		}
	};
	
	private void repaint(){
		ImageView image=(ImageView)findViewById(R.id.imageView1);
        Canvas c = new Canvas(bitMain);
        Paint p=new Paint();
        //Рисование
        c.drawColor(Color.WHITE);
        p.setColor(Color.BLACK);        
        for (int i=0; i<=width; i++)
        	c.drawLine(i*sizew,0,i*sizew,screen.y,p);
        for (int i=0; i<=height; i++)
        	c.drawLine(0,i*sizeh,screen.x,i*sizeh,p); 	
        for (int i=0; i<width; i++)
        	for (int j=0; j<height; j++)
        		if (world.sBoard.GetObject(i,j) != null)
        			switch (world.sBoard.GetObject(i,j).GetKind()){        			
        				case Object.JUNK: c.drawBitmap(bitJunk,i*sizew+1,j*sizeh+1,p); break;
        				case Object.BOT: c.drawBitmap(bitRobot,i*sizew+1,j*sizeh+1,p); break;
        				case Object.FASTBOT: c.drawBitmap(bitFastRobot,i*sizew+1,j*sizeh+1,p); break;
        	}
        c.drawBitmap(bitPlayer,world.player.getPos().x*sizew+1,
         world.player.getPos().y*sizeh+1,p);        
        image.setImageBitmap(bitMain);		
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        
        screen = GetScreenSize();
        screen.y -= 130;
        sizew = screen.x / width; //Длина клетки в пикселях
        sizeh = screen.y / height; //Высота клетки в пикселях

        bitMain=Bitmap.createBitmap(screen.x+1,screen.y+1,Bitmap.Config.ARGB_8888);;
        bitRobot=BitmapFactory.decodeResource(getResources(),R.drawable.robot);
    	bitFastRobot=BitmapFactory.decodeResource(getResources(),R.drawable.fastrobot);
        bitPlayer=BitmapFactory.decodeResource(getResources(),R.drawable.player); 
        bitJunk=BitmapFactory.decodeResource(getResources(),R.drawable.junk);
        
        //Подгоняем размеры bitmap-ов под размеры клетки
        bitRobot=Bitmap.createScaledBitmap(bitRobot,sizew-1,sizeh-1,false);
        bitFastRobot=Bitmap.createScaledBitmap(bitFastRobot,sizew-1,sizeh-1,false);
        bitPlayer=Bitmap.createScaledBitmap(bitPlayer,sizew-1,sizeh-1,false);
        bitJunk=Bitmap.createScaledBitmap(bitJunk,sizew-1,sizeh-1,false);
        
        findViewById(R.id.left_button).setOnClickListener(listener);
        findViewById(R.id.right_button).setOnClickListener(listener);
        findViewById(R.id.up_button).setOnClickListener(listener);
        findViewById(R.id.down_button).setOnClickListener(listener);
        findViewById(R.id.left_up_button).setOnClickListener(listener);
        findViewById(R.id.left_down_button).setOnClickListener(listener);
        findViewById(R.id.right_up_button).setOnClickListener(listener);
        findViewById(R.id.right_down_button).setOnClickListener(listener); 
        findViewById(R.id.teleport_button).setOnClickListener(listener); 
        findViewById(R.id.safe_teleport_button).setOnClickListener(listener); 
        findViewById(R.id.stay_button).setOnClickListener(listener); 
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.muz); // создаём новый объект mediaPlayer
        mediaPlayer.setOnCompletionListener(playerListener);
        mediaPlayer.setLooping(true);
        mediaPlayer.start(); // запускаем воспроизведение
        world = new World(width, height, (TextView)findViewById(R.id.textView2));     
        repaint();
        
        TextView text=(TextView)findViewById(R.id.textView1);
        String str="Level: "+Integer.toString(world.getLevel())+
   		 "  Score: "+Integer.toString(world.player.getScore())+
   		 "  Energy: "+Integer.toString(world.player.getEnergy())+
   		 "  isAlive: "+world.player.isAlive;
   		text.setText(str);
    }


    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.game, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }*/
}

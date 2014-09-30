package com.github.mcfeod.robots4droid;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class GameActivity extends ActionBarActivity {
	private static int width=320, height=320; //длина и ширина поля
	private static int count=10; //размеры сторон
	SimpleWorld world;
	
	public OnClickListener listener = new OnClickListener() {
		@Override
		public void onClick(View v) {	
			boolean succ=false;
			switch (v.getId()){
				case R.id.left_button: succ=world.movePlayer((byte)(2)); break;
				case R.id.right_button: succ=world.movePlayer((byte)(3)); break;
				case R.id.up_button: succ=world.movePlayer((byte)(0)); break;
				case R.id.down_button: succ=world.movePlayer((byte)(1)); break;
				case R.id.left_up_button: succ=world.movePlayer((byte)(4)); break;
				case R.id.left_down_button: succ=world.movePlayer((byte)(6)); break;
				case R.id.right_up_button: succ=world.movePlayer((byte)(5)); break;
				case R.id.right_down_button: succ=world.movePlayer((byte)(7)); break;	
				case R.id.stay_button: succ=world.movePlayer((byte)(8)); break;	
				case R.id.teleport_button: succ=world.movePlayer((byte)(9)); break;	
				case R.id.safe_teleport_button: succ=world.movePlayer((byte)(10)); break;	
			}
			if (succ)
				world.moveBots();
			repaint();
			TextView text=(TextView)findViewById(R.id.textView1);
			String str="Level: "+Integer.toString(world.player.getLevel())+
			 "  Score: "+Integer.toString(world.player.getScore())+
			 "  Energy: "+Integer.toString(world.player.getEnergy())+
			 "  isAlive: "+world.player.isAlive;
			text.setText(str);
		}
	};
	
	private void repaint(){
		ImageView image=(ImageView)findViewById(R.id.imageView1);
        Bitmap b=Bitmap.createBitmap(width+1,height+1,Bitmap.Config.ARGB_8888);//главный bitmap 
        //создаем bitmap-ы с картинками из ресурсов
        Bitmap bit_robot=BitmapFactory.decodeResource(getResources(),R.drawable.robot);
        Bitmap bit_fastrobot=BitmapFactory.decodeResource(getResources(),R.drawable.fastrobot);
        Bitmap bit_player=BitmapFactory.decodeResource(getResources(),R.drawable.player); 
        Bitmap bit_junk=BitmapFactory.decodeResource(getResources(),R.drawable.junk);
        Canvas c=new Canvas(b);
        Paint p=new Paint();
        int sizew=width/count; //Длина клетки в пикселях
        int sizeh=height/count; //Высота клетки в пикселях
        
        //Подгоняем размеры bitmap-ов под размеры клетки
        bit_robot=Bitmap.createScaledBitmap(bit_robot,sizew-1,sizeh-1,false);
        bit_fastrobot=Bitmap.createScaledBitmap(bit_fastrobot,sizew-1,sizeh-1,false);
        bit_player=Bitmap.createScaledBitmap(bit_player,sizew-1,sizeh-1,false);
        bit_junk=Bitmap.createScaledBitmap(bit_junk,sizew-1,sizeh-1,false);
        
        //Рисование
        c.drawColor(Color.WHITE);
        p.setColor(Color.BLACK);        
        for (int i=0; i<=count; i++){
        	c.drawLine(i*sizew,0,i*sizew,height,p);
        	c.drawLine(0,i*sizeh,width,i*sizeh,p);        	
        }
        for (int i=0; i<count; i++)
        	for (int j=0; j<count; j++){
        		switch (world.sBoard[i][j]){
        			case 2: c.drawBitmap(bit_robot,j*sizew+1,i*sizeh+1,p); break;
        			case 3: c.drawBitmap(bit_fastrobot,j*sizew+1,i*sizeh+1,p); break;
        			case 4: c.drawBitmap(bit_junk,j*sizew+1,i*sizeh+1,p); break;
        		}        		
        	}
        c.drawBitmap(bit_player,world.player.getPos().x*sizew+1,
         world.player.getPos().y*sizeh+1,p);        
        image.setImageBitmap(b);		
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

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
        
        world = new SimpleWorld(count,count);     
        repaint();
        
        TextView text=(TextView)findViewById(R.id.textView1);
        String str="Level: "+Integer.toString(world.player.getLevel())+
   		 "  Score: "+Integer.toString(world.player.getScore())+
   		 "  Energy: "+Integer.toString(world.player.getEnergy())+
   		 "  isAlive: "+world.player.isAlive;
   		text.setText(str);
    }


    @Override
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
    }
}

package com.github.mcfeod.robots4droid;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class GameActivity extends Activity {
	private int width=20, height=15; //размеры сторон
    private World world;
    private DrawWorld drawWorld;
    Point screen;
	String mMsgStr;

    private MediaPlayer mSoundTrack;
    private boolean isMusicOn;

	public Point GetScreenSize(){
		Display display = getWindowManager().getDefaultDisplay();
		DisplayMetrics metrics = new DisplayMetrics();
		display.getMetrics(metrics);
		Point size = new Point(metrics.widthPixels, metrics.heightPixels);
		return size;
	}

	public OnClickListener listener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			boolean succ=false;
			if (world.player.isAlive){
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
				drawWorld.repaint();
				
				if (world.player.isAlive)
					mMsgStr="Level: "+Integer.toString(world.mLevel)+
					"  Score: "+Integer.toString(world.player.getScore())+
					"  Energy: "+Integer.toString(world.player.getEnergy());
				
				if (!world.player.isAlive){
					mMsgStr = "Tap any button to replay";
					drawWorld.death();
				}
			}
			else{
				world.defeat();
				drawWorld.repaint();
			}
			TextView text=(TextView)findViewById(R.id.textView1);
			text.setText(mMsgStr);
		}
	};

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
         WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_game);

        screen = GetScreenSize();

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
        
        mSoundTrack = MediaPlayer.create(this, R.raw.muz);
        mSoundTrack.setLooping(true);
        // при сворачивании приложения музыка должна выключаться, а при восстановлении включаться.
        // по этой причине start() и stop() размещены в onStart() и onStop()
        String settings = getIntent().getStringExtra(MainActivity.SETTINGS);
        isMusicOn = SettingsParser.isMusicOn(settings);
        
        world = new World(width, height);
        drawWorld = new DrawWorld(this.getApplicationContext(), 
         (ImageView)findViewById(R.id.imageView1), world, screen);
        drawWorld.repaint();
        
        TextView text=(TextView)findViewById(R.id.textView1);
        String str="Level: "+Integer.toString(world.mLevel)+
   		 "  Score: "+Integer.toString(world.player.getScore())+
   		 "  Energy: "+Integer.toString(world.player.getEnergy());
		text.setText(str);
    }

    @Override
    protected void onResume(){
        super.onResume();
        if(isMusicOn) {
            mSoundTrack.start();
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        mSoundTrack.pause();
    }
}
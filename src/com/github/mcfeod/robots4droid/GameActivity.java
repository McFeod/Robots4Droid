package com.github.mcfeod.robots4droid;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class GameActivity extends Activity {
	private int width=15, height=20; //размеры сторон
    private World world;
    private String mMsgStr;

    private MediaPlayer mSoundTrack;
    private boolean isMusicOn;
    
    private TextView text;
    private GameSurfaceView view;
    
    /*public Point GetScreenSize(){
		Display display = getWindowManager().getDefaultDisplay();
		DisplayMetrics metrics = new DisplayMetrics();
		display.getMetrics(metrics);
		Point size = new Point(metrics.widthPixels, metrics.heightPixels);
		return size;
	}*/

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
					case R.id.mine_button:
						world.setMine();
						succ=false;
						//view.mDrawThread.Draw();
						view.mDrawThread.moveTo(world.player.getPos().x*35+
						 35/2, world.player.getPos().y*35+35/2);
						//drawWorld.centerPlayerPos(world.player.getPos().x*widthPX+
						// widthPX/2, world.player.getPos().y*heightPX+heightPX/2);
						mMsgStr="Level: "+Integer.toString(world.mLevel)+
						 "  Score: "+Integer.toString(world.player.getScore())+
						 "  Energy: "+Integer.toString(world.player.getEnergy());
						text.setText(mMsgStr);
						break;	
					case R.id.bomb_button:
						succ=world.bomb();
						break;
				}
				if (succ){
					//передвигаем роботов
					world.moveBots();
					if (world.player.isAlive){
						mMsgStr="Level: "+Integer.toString(world.mLevel)+
						 "  Score: "+Integer.toString(world.player.getScore())+
						 "  Energy: "+Integer.toString(world.player.getEnergy());
						text.setText(mMsgStr);
						//отрисовываем роботов
						view.mDrawThread.moveTo(world.player.getPos().x*35+
								 35/2, world.player.getPos().y*35+35/2);
						//drawWorld.moveTo(world.player.getPos().x*widthPX+
						// widthPX/2, world.player.getPos().y*heightPX+heightPX/2);
					}else{
						AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
						builder.setTitle(R.string.dialog);
						builder.setMessage(R.string.dialog);
						builder.setCancelable(false);
						builder.setPositiveButton(R.string.restart, new DialogInterface.OnClickListener() { // Кнопка ОК
						    @Override
						    public void onClick(DialogInterface dialog, int which) {
						    	world.defeat();
						    	view.mDrawThread.moveTo(world.player.getPos().x*35+
										 35/2, world.player.getPos().y*35+35/2);
								//drawWorld.moveTo(world.player.getPos().x*widthPX+
								// widthPX/2, world.player.getPos().y*heightPX+heightPX/2);
						        dialog.dismiss();
						    }
						});
						AlertDialog dialog = builder.create();
						dialog.show();
					}
				}
			}
		}
	};
	
	

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
         WindowManager.LayoutParams.FLAG_FULLSCREEN);
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
        findViewById(R.id.mine_button).setOnClickListener(listener);
        findViewById(R.id.bomb_button).setOnClickListener(listener);
                
        mSoundTrack = MediaPlayer.create(this, R.raw.muz2);
        mSoundTrack.setLooping(true);
        String settings = getIntent().getStringExtra(MainActivity.SETTINGS);
        isMusicOn = SettingsParser.isMusicOn(settings);
        
        world = new World(width, height);

        
        
        
        
		view = (GameSurfaceView)findViewById(R.id.game);
        view.SetWorld(world);
        
        
        view.CreateThread();
        view.mDrawThread.SetActivity(this);
        text=(TextView)findViewById(R.id.textView1);
        view.mDrawThread.SetText(text);
        view.StartThread();
        
        //view.mDrawThread.moveTo(world.player.getPos().x*35+
		//		 35/2, world.player.getPos().y*35+35/2);
        
        
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
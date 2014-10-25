package com.github.mcfeod.robots4droid;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import saves.BinaryIOManager;
import saves.SaveManager;


public class GameActivity extends Activity {
	private int width=20, height=15; //размеры сторон
    private World world;
    private int mLastLevel=-1;
    private boolean mNeedCrutchForLaunch = true;

    private MediaPlayer mSoundTrack;
    private boolean isMusicOn;

    private TextView text;
    private Button saveButton;
    private boolean isSaveUsed = false;
    private GameSurfaceView view;


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
					case R.id.mine_button: succ=world.setMine(); break;	
					case R.id.bomb_button: succ=world.bomb(); break;
				}
				if (succ){
					view.mDrawThread.moveTo(world.player.getPos());
					view.mDrawThread.delay(400);
					//передвигаем роботов
					world.moveBots();
					if (world.player.isAlive){
						changeText();
						//отрисовываем роботов
						view.mDrawThread.moveTo(world.player.getPos());
					}else{
                        saveButton.setEnabled(false);
						AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
						builder.setTitle(R.string.dialog);
						builder.setMessage(R.string.dialog);
						builder.setCancelable(false);
						builder.setPositiveButton(R.string.restart, new DialogInterface.OnClickListener() { // Кнопка ОК
						    @Override
						    public void onClick(DialogInterface dialog, int which) {
						    	world.defeat();
						    	view.mDrawThread.moveTo(world.player.getPos());
								dialog.dismiss();
                                saveButton.setEnabled(!isSaveUsed);
						    }
						});
						AlertDialog dialog = builder.create();
						dialog.show();
						dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
					}
				}else{
					Toast.makeText(GameActivity.this, "You can't do it", Toast.LENGTH_SHORT).show();
				}
					
			}
			if (mLastLevel != world.mLevel){
				mLastLevel = world.mLevel;
				Toast.makeText(GameActivity.this, "NEW LEVEL: " + mLastLevel , Toast.LENGTH_SHORT).show();
				
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

        saveButton = (Button)findViewById(R.id.save_button);
        saveButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });
        
        mSoundTrack = MediaPlayer.create(this, R.raw.muz);
        mSoundTrack.setLooping(true);
        // при сворачивании приложения музыка должна выключаться, а при восстановлении включаться.
        // по этой причине start() и stop() размещены в onStart() и onStop()
        String settings = getIntent().getStringExtra(MainActivity.SETTINGS);
        isMusicOn = SettingsParser.isMusicOn(settings);
        
        if (savedInstanceState == null){
        	world = new World(width, height);
        	String settings = getIntent().getStringExtra(MainActivity.SETTINGS);
            isMusicOn = SettingsParser.isMusicOn(settings);
            // если выбрана соохранённая игра, загружаем из соохранения
            if(SaveManager.INSTANCE.hasLoadingGame()){
                load();
            }
        }else{
        	world = new World(
        			savedInstanceState.getInt("width"),
        			savedInstanceState.getInt("height"),
        			savedInstanceState.getInt("bots"),
        			savedInstanceState.getInt("fastbots"),
        			savedInstanceState.getInt("playerX"),
        			savedInstanceState.getInt("playerY"),
        			savedInstanceState.getInt("energy"),
        			savedInstanceState.getInt("score"),
        			savedInstanceState.getBoolean("isAlive"),
        			savedInstanceState.getInt("level")
        			);
        	for(int i=0; i<width; ++i){
        		 world.board.setRow(i, savedInstanceState.getByteArray("board_"+i));
        	}
        	isMusicOn = savedInstanceState.getBoolean("isMusicOn");
        	if (isMusicOn)
        		mSoundTrack.seekTo(savedInstanceState.getInt("musicTime")+400);
        }
		view = (GameSurfaceView)findViewById(R.id.game);
        view.SetWorld(world);
        
        
        view.CreateThread();
        view.mDrawThread.SetActivity(this);
        text=(TextView)findViewById(R.id.textView1);
        view.mDrawThread.SetText(text);
        view.StartThread();
		changeText();
    }

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (mNeedCrutchForLaunch){
			view.mDrawThread.moveTo(world.player.getPos());
			mNeedCrutchForLaunch = false;
		}
	}

    @Override
    protected void onResume(){
        super.onResume();
        if(isMusicOn) {
            mSoundTrack.start();
        }
        view.mDrawThread.moveTo(world.player.getPos());
    }

    @Override
    protected void onPause(){
        super.onPause();
        if(isMusicOn) {
        	mSoundTrack.pause();
        }
    }
    
    @Override
    protected void onDestroy(){
    	super.onDestroy();
    	view.StopThread();
        mSoundTrack.release();
    }
    
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
    	super.onSaveInstanceState(savedInstanceState);
    	savedInstanceState.putInt("width", width);
    	savedInstanceState.putInt("height", height);
    	for(int i=0; i<width; ++i){
    		savedInstanceState.putByteArray("board_"+i, world.board.getRow(i));
    	}
    	savedInstanceState.putInt("fastbots", world.board.getAliveFastBotCount());
    	savedInstanceState.putInt("bots", world.board.getAliveBotCount());
    	savedInstanceState.putInt("level", world.getLevel());
    	savedInstanceState.putInt("score", world.player.getScore());
    	savedInstanceState.putInt("energy", world.player.getEnergy());
    	savedInstanceState.putInt("playerX", world.player.getPos().x);
    	savedInstanceState.putInt("playerY", world.player.getPos().y);
    	savedInstanceState.putBoolean("isAlive", world.player.isAlive);
    	savedInstanceState.putBoolean("isMusicOn", isMusicOn);
    	if (isMusicOn)
    		savedInstanceState.putInt("musicTime",mSoundTrack.getCurrentPosition());
    }

    private void changeText(){
        text.setText(String.format("L: %d, S: %d, E: %d",
                world.mLevel, world.player.getScore(), world.player.getEnergy()));
    }

    private void load(){
        BinaryIOManager loader = new BinaryIOManager(getApplicationContext(), world);
        world.board.giveLinkToManager(loader);
        SaveManager.INSTANCE.loadGameFromBinary(loader);
    }

    private void save(){
        BinaryIOManager saver = new BinaryIOManager(getApplicationContext(), world);
        world.board.giveLinkToManager(saver);
        SaveManager.INSTANCE.saveGameToBinary(saver);
        // костыль, чтобы не соохранялисб больше одного раза
        saveButton.setEnabled(false);
        isSaveUsed = true;
    }
  
}
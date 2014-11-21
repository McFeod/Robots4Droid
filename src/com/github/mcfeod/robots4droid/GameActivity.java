package com.github.mcfeod.robots4droid;

import saves.BinaryIOManager;
import saves.SaveManager;
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
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

public class GameActivity extends Activity {
	private int width=20, height=15; //размеры сторон
	private World world;
	private int mLastLevel=-1;
	private boolean mNeedCrutchForLaunch = true;

	private MediaPlayer mSoundTrack; 
	private boolean isMusicOn;
	private boolean areMinesOn;
	private boolean areBombsOn;

	private TextView safeTeleportTextView, bombTextView, mineTextView, teleportTextView;
	private TextView levelTextView, scoreTextView, energyTextView, botCountTextView;
	private FrameLayout safeTeleportFrameLayout, mineFrameLayout, bombFrameLayout,
	 teleportFrameLayout;
	private GameSurfaceView view;
	private DrawThread mDrawThread;

	public OnClickListener listenerArrowButtons = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (world.player.isAlive){
				switch (v.getId()){
					case R.id.left_button: moveBots(world.movePlayer(World.LEFT)); break;
					case R.id.right_button: moveBots(world.movePlayer(World.RIGHT)); break;
					case R.id.up_button: moveBots(world.movePlayer(World.UP)); break;
					case R.id.down_button: moveBots(world.movePlayer(World.DOWN)); break;
					case R.id.left_up_button: moveBots(world.movePlayer(World.UP_LEFT)); break;
					case R.id.left_down_button: moveBots(world.movePlayer(World.DOWN_LEFT)); break;
					case R.id.right_up_button: moveBots(world.movePlayer(World.UP_RIGHT)); break;
					case R.id.right_down_button: moveBots(world.movePlayer(World.DOWN_RIGHT)); break;
					case R.id.stay_button: moveBots(world.movePlayer(World.STAY)); break;
				}
			}			
		}
	};
	
	public OnClickListener listenerOtherButtons = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (world.player.isAlive){
				if (v == teleportFrameLayout.getChildAt(0))
					moveBots(world.movePlayer(World.TELEPORT));
				if (v == safeTeleportFrameLayout.getChildAt(0))
					moveBots(world.movePlayer(World.SAFE_TELEPORT));
				if (v == mineFrameLayout.getChildAt(0))
					moveBots(world.setMine());
				if (v == bombFrameLayout.getChildAt(0))
					moveBots(world.bomb());
			}
		}
	};
	
	private void moveBots(boolean succ){
		if (succ){
			mDrawThread.scrollToPlayer();
			mDrawThread.delay(200);
			//передвигаем роботов
			world.moveBots();
			refreshButtons();
			changeText();
			if (world.player.isAlive){
				//отрисовываем роботов
				mDrawThread.scrollToPlayer();
				if (mLastLevel != world.getLevel()){
					mLastLevel = world.getLevel();
					showNewLevelToast();
				}
			}else{
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setCancelable(false);
				DialogInterface.OnClickListener	positiveButtonListener = new DialogInterface.OnClickListener() { // Кнопка ОК
				    @Override
				    public void onClick(DialogInterface dialog, int which) {
				    	world.defeat();
				    	mDrawThread.scrollToPlayer();
						dialog.dismiss();
						mLastLevel = world.getLevel();
						showNewLevelToast();
					}
				};
				DialogInterface.OnClickListener	negativeButtonListener = new DialogInterface.OnClickListener() { // Кнопка неОК
					@Override
					public void onClick(DialogInterface dialog, int which) {
						GameActivity.this.finish();
						dialog.dismiss();
					}
				};
				
				switch (SettingsParser.getLanguage()) {
					case 0:
						builder.setTitle(R.string.dialog_ru);
						builder.setMessage(R.string.dialog_ru);
						builder.setPositiveButton(R.string.restart_ru, positiveButtonListener);
						builder.setNegativeButton(R.string.away_ru, negativeButtonListener);
						break;
					case 1:
						builder.setTitle(R.string.dialog);
						builder.setMessage(R.string.dialog);
						builder.setPositiveButton(R.string.restart, positiveButtonListener);
						builder.setNegativeButton(R.string.away, negativeButtonListener);
						break;
				}
				AlertDialog dialog = builder.create();
				dialog.show();
				dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
			}
		}else
			switch (SettingsParser.getLanguage()) {
			case 0:
				Toast.makeText(GameActivity.this, R.string.not_possible_ru, Toast.LENGTH_SHORT).show();
				break;
			case 1:
				Toast.makeText(GameActivity.this, R.string.not_possible, Toast.LENGTH_SHORT).show();
				break;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		 WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_game);

		view = (GameSurfaceView)findViewById(R.id.game);
		levelTextView = (TextView)findViewById(R.id.level_textView);
		scoreTextView = (TextView)findViewById(R.id.score_textView);
		energyTextView = (TextView)findViewById(R.id.energy_textView);
		botCountTextView = (TextView)findViewById(R.id.bot_count_textView);
		
		teleportFrameLayout = (FrameLayout)findViewById(R.id.teleport_frameLayout);
		safeTeleportFrameLayout = (FrameLayout)findViewById(R.id.safe_teleport_frameLayout);
		mineFrameLayout = (FrameLayout)findViewById(R.id.mine_frameLayout);
		bombFrameLayout = (FrameLayout)findViewById(R.id.bomb_frameLayout);
		
		findViewById(R.id.left_button).setOnClickListener(listenerArrowButtons);
		findViewById(R.id.right_button).setOnClickListener(listenerArrowButtons);
		findViewById(R.id.up_button).setOnClickListener(listenerArrowButtons);
		findViewById(R.id.down_button).setOnClickListener(listenerArrowButtons);
		findViewById(R.id.left_up_button).setOnClickListener(listenerArrowButtons);
		findViewById(R.id.left_down_button).setOnClickListener(listenerArrowButtons);
		findViewById(R.id.right_up_button).setOnClickListener(listenerArrowButtons);
		findViewById(R.id.right_down_button).setOnClickListener(listenerArrowButtons);
		findViewById(R.id.stay_button).setOnClickListener(listenerArrowButtons);
		
		teleportFrameLayout.getChildAt(0).setOnClickListener(listenerOtherButtons);
		safeTeleportFrameLayout.getChildAt(0).setOnClickListener(listenerOtherButtons);
		mineFrameLayout.getChildAt(0).setOnClickListener(listenerOtherButtons);
		bombFrameLayout.getChildAt(0).setOnClickListener(listenerOtherButtons);

		safeTeleportTextView = (TextView)safeTeleportFrameLayout.getChildAt(1);
		mineTextView = (TextView)mineFrameLayout.getChildAt(1);
		bombTextView = (TextView)bombFrameLayout.getChildAt(1);
		teleportTextView = (TextView)teleportFrameLayout.getChildAt(1);

		safeTeleportTextView.setText(Byte.toString(World.SAFE_TELEPORT_COST));
		mineTextView.setText(Byte.toString(World.MINE_COST));
		teleportTextView.setText("\u221e");
		
		mSoundTrack = MediaPlayer.create(this, R.raw.muz);
		mSoundTrack.setLooping(true);
		// при сворачивании приложения музыка должна выключаться, а при восстановлении включаться.
		// по этой причине start() и stop() размещены в onStart() и onStop()
		if (savedInstanceState == null){
			if(SaveManager.getInstance().hasLoadingGame()){
				load();
				mLastLevel = world.getLevel();
			}else{
				world = new World(width, height);
				mLastLevel = world.getLevel();
				showNewLevelToast();
			}
			isMusicOn = SettingsParser.isMusicOn();
			areBombsOn = SettingsParser.areBombsOn();
			areMinesOn = SettingsParser.areMinesOn();
			world.player.areSuicidesForbidden = !SettingsParser.areSuicidesOn();
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
			world.player.areSuicidesForbidden = savedInstanceState.getBoolean("areSuicidesForbidden");
			isMusicOn = savedInstanceState.getBoolean("isMusicOn");
			areBombsOn = savedInstanceState.getBoolean("areBombsOn");
			areMinesOn = savedInstanceState.getBoolean("areMinesOn");
			if (isMusicOn)
				mSoundTrack.seekTo(savedInstanceState.getInt("musicTime")+400);
			mLastLevel = world.getLevel();
		}

		if (!areBombsOn)
			bombFrameLayout.getChildAt(0).setVisibility(View.GONE);
		if (!areMinesOn)
			mineFrameLayout.getChildAt(0).setVisibility(View.GONE);

		view.CreateThread();
		mDrawThread = view.getDrawThread();
		mDrawThread.setWorld(world);
		view.StartThread();
		changeText();
		refreshButtons();
	}
	
	public void refreshButtons(){
		byte bombCost = world.getBombCost();
		int energy = world.player.getEnergy();
		if (World.SAFE_TELEPORT_COST > energy)
			safeTeleportFrameLayout.setVisibility(View.GONE);
		else
			safeTeleportFrameLayout.setVisibility(View.VISIBLE);
		if (World.MINE_COST > energy)
			mineFrameLayout.setVisibility(View.GONE);
		else
			mineFrameLayout.setVisibility(View.VISIBLE);
		if (bombCost > energy)
			bombFrameLayout.setVisibility(View.GONE);
		else{
			bombFrameLayout.setVisibility(View.VISIBLE);
			bombTextView.setText(Byte.toString(bombCost));
		}
	}
	
	private void showNewLevelToast(){
		switch (SettingsParser.getLanguage()) {
			case 0:
				Toast.makeText(GameActivity.this, String.format(getString
				 (R.string.new_level_ru), mLastLevel) , Toast.LENGTH_SHORT).show();
				break;
			case 1:
				Toast.makeText(GameActivity.this, String.format(getString
				 (R.string.new_level), mLastLevel) , Toast.LENGTH_SHORT).show();
				break;
		}
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (mNeedCrutchForLaunch){
			view.mDrawThread.setDefaultCellSize();
			view.mDrawThread.scrollToPlayer();
			mNeedCrutchForLaunch = false;
		}
	}
	
    @Override
    protected void onResume(){
        super.onResume();
        if(isMusicOn) {
            mSoundTrack.start();
        }
        view.mDrawThread.scrollToPlayer();
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
		savedInstanceState.putBoolean("areSuicidesForbidden", world.player.areSuicidesForbidden);
		savedInstanceState.putBoolean("isMusicOn", isMusicOn);
		savedInstanceState.putBoolean("areMinesOn", areMinesOn);
		savedInstanceState.putBoolean("areBombsOn", areBombsOn);
		if (isMusicOn)
			savedInstanceState.putInt("musicTime",mSoundTrack.getCurrentPosition());
	}

	private void changeText(){
		switch (SettingsParser.getLanguage()) {
			case 0:
				levelTextView.setText(String.format(getString(R.string.level_ru),
				 world.getLevel()));
				break;
			case 1:
				levelTextView.setText(String.format(getString(R.string.level),
				 world.getLevel()));
				break;
		}
		scoreTextView.setText(String.format("%d   ", world.player.getScore()));		
		energyTextView.setText(String.format("%d   ", world.player.getEnergy()));
		botCountTextView.setText(String.format("%d   ",
		 world.board.getAliveBotCount()+world.board.getAliveFastBotCount()));
	}

	private void load(){
		BinaryIOManager loader = new BinaryIOManager(getApplicationContext(), world);
		SaveManager.getInstance().loadGameFromBinary(loader);
		world = loader.updatedWorld();
	}

	private void save(){
		BinaryIOManager saver = new BinaryIOManager(getApplicationContext(), world);
		SaveManager.getInstance().saveGameToBinary(saver);
	}
	
	@Override
	public void onBackPressed() {
		save();
		super.onBackPressed();
	}
}
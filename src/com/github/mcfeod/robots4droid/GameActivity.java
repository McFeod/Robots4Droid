package com.github.mcfeod.robots4droid;

import java.util.Random;

import saves.BinaryIOManager;
import saves.SaveManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class GameActivity extends Activity {
	private World world;
	private int mLastLevel=-1;

	private MediaPlayer mSoundTrack;
	private boolean isMusicOn;
	private boolean areMinesOn;
	private boolean areBombsOn;
	private boolean isLSD;
	private boolean isLSDAnim;
	private String[] mToastArray;
	private Random rand;
	private TextView levelTextView, scoreTextView, energyTextView, botCountTextView;
	private TextView scoreInfoTextView;
	private Button safeTeleButton;
	private Button bombButton;
	private Button mineButton;
	private GameSurfaceView view;
	private DrawThread mDrawThread;
	private LinearLayout mGameOverLinearLayout;
	private EditText inputNameEditText;
	private AlertDialog addScoreDialog;
	private SharedPreferences mSettings;

	/** Кнопка "По новой" */
	private final OnClickListener restartButtonListener = new OnClickListener() {
		@Override
		public void onClick(View v){
			mGameOverLinearLayout.setVisibility(View.GONE);
			world.defeat();
			mDrawThread.scrollToPlayer();
			mLastLevel = world.getLevel();
			showNewLevelToast();
			changeText();
		}
	};

	/** Кнопка "Да ну вас" */
	private final OnClickListener awayButtonListener = new OnClickListener() {
		@Override
		public void onClick(View v){
			GameActivity.this.finish();
		}
	};
	
	/** Кнопка "Хочу похвастаться" */
	private final OnClickListener addScoreButtonListener = new OnClickListener() {
		@Override
		public void onClick(View v){
			inputNameEditText.setText("");
			addScoreDialog.show();
		}
	};

	private final OnClickListener listener = new OnClickListener() {
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
					case R.id.teleport_button: moveBots(world.movePlayer(World.TELEPORT)); break;	
					case R.id.safe_teleport_button: moveBots(world.movePlayer(World.SAFE_TELEPORT)); break;	
					case R.id.mine_button: moveBots(world.setMine()); break;	
					case R.id.bomb_button: moveBots(world.bomb()); break;
				}
			}
		}
	};

	private void moveBots(boolean succ){
		if (succ){
			mDrawThread.scrollToPlayer();
			mDrawThread.delay(200);
			//передвигаем роботов
			world.moveBots(false);
			mDrawThread.scrollToPlayer();
			if (world.board.getAliveFastBotCount()>0){
				mDrawThread.delay(100);
				//передвигаем быстрых роботов
				world.moveBots(true);
				mDrawThread.scrollToPlayer();
			}
			changeText();
			if (world.player.isAlive){
				if (mLastLevel != world.getLevel()){
					mLastLevel = world.getLevel();
					showNewLevelToast();
				}
			}else
				showGameOverDialog();
		}else{
			Toast.makeText(GameActivity.this, mToastArray[rand.nextInt(mToastArray.length)], Toast.LENGTH_SHORT).show();
		}
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);

		inputNameEditText = new EditText(GameActivity.this);
		AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);
		builder.setMessage(R.string.input_name);
		builder.setView(inputNameEditText);
		builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				try{
					SaveManager.getInstance().addScore(GameActivity.this,
					 inputNameEditText.getText().toString(), world);
				}catch(Exception e){}
				//чтобы при повороте экрана не появлялась кнопка
				world.player.chScore(-world.player.getScore());
				//прячет кнопку после создания нового рекорда
				scoreInfoTextView.setVisibility(View.GONE);
				findViewById(R.id.add_score_button).setVisibility(View.GONE);
			}
		});
		builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		addScoreDialog = builder.create();
		
		mGameOverLinearLayout = (LinearLayout) findViewById(R.id.defeat_linearLayout);
		view = (GameSurfaceView) findViewById(R.id.game);
		levelTextView = (TextView) findViewById(R.id.levelView);
		scoreTextView = (TextView) findViewById(R.id.scoreView);
		energyTextView = (TextView) findViewById(R.id.energyView);
		botCountTextView = (TextView) findViewById(R.id.botCountView);
		scoreInfoTextView = (TextView) findViewById(R.id.score_info_textView);
		mToastArray = this.getResources().getStringArray(R.array.reject_movement);
		rand = new Random();

		findViewById(R.id.restart_button).setOnClickListener(restartButtonListener);
		findViewById(R.id.away_button).setOnClickListener(awayButtonListener);
		findViewById(R.id.add_score_button).setOnClickListener(addScoreButtonListener);
		
		findViewById(R.id.left_button).setOnClickListener(listener);
		findViewById(R.id.right_button).setOnClickListener(listener);
		findViewById(R.id.up_button).setOnClickListener(listener);
		findViewById(R.id.down_button).setOnClickListener(listener);
		findViewById(R.id.left_up_button).setOnClickListener(listener);
		findViewById(R.id.left_down_button).setOnClickListener(listener);
		findViewById(R.id.right_up_button).setOnClickListener(listener);
		findViewById(R.id.right_down_button).setOnClickListener(listener);
		Button teleButton = (Button) findViewById(R.id.teleport_button);
		teleButton.setOnClickListener(listener); 
		safeTeleButton = (Button) findViewById(R.id.safe_teleport_button);
		safeTeleButton.setOnClickListener(listener); 
		findViewById(R.id.stay_button).setOnClickListener(listener);
		mineButton = (Button) findViewById(R.id.mine_button);
		mineButton.setOnClickListener(listener);
		bombButton = (Button) findViewById(R.id.bomb_button);
		bombButton.setOnClickListener(listener);
		
		// при сворачивании приложения музыка должна выключаться, а при восстановлении включаться.
		// по этой причине start() и stop() размещены в onStart() и onStop()
		if (savedInstanceState == null){
			mSettings = PreferenceManager.getDefaultSharedPreferences(this);
			if (SaveManager.getInstance().hasLoadingGame()){
				load();
				mLastLevel = world.getLevel();
			}else{
				world = new World(
						mSettings.getInt("width", 20),
						mSettings.getInt("height", 15),
						Integer.parseInt(mSettings.getString("complexity", "0")),
						mSettings.getBoolean("energy_shortage", false),
						mSettings.getBoolean("vamp_mode", true)
						);
				mLastLevel = world.getLevel();
				showNewLevelToast();
			}
			isLSD = mSettings.getBoolean("LSD", false);
			isLSDAnim = mSettings.getBoolean("LSD_anim", false);
			isMusicOn = mSettings.getBoolean("music", true);
			areBombsOn = mSettings.getBoolean("bomb", false);
			areMinesOn = mSettings.getBoolean("mine", false);
			world.player.areSuicidesForbidden = mSettings.getBoolean("suicides_forbidden", false);
			if (isLSD)
				mSoundTrack = MediaPlayer.create(this, R.raw.lsd);
			else
				mSoundTrack = MediaPlayer.create(this, R.raw.muz);
			mSoundTrack.setLooping(true);
			
		}else{
			world = new World(
				savedInstanceState.getInt("width"),
				savedInstanceState.getInt("height"),
				savedInstanceState.getInt("bots"),
				savedInstanceState.getInt("fastbots"),
				savedInstanceState.getInt("playerX"),
				savedInstanceState.getInt("playerY"),
				savedInstanceState.getInt("energy"),
				savedInstanceState.getLong("score"),
				savedInstanceState.getBoolean("isAlive"),
				savedInstanceState.getBoolean("isWinner"),
				savedInstanceState.getInt("level"),
				savedInstanceState.getInt("gameMode"),
				savedInstanceState.getBoolean("energy_shortage"),
				savedInstanceState.getBoolean("vamp_mode")
				);
			for(int i=0; i<world.getWidth(); ++i){
				world.board.setRow(i, savedInstanceState.getByteArray("board_"+i));
			}
			world.player.areSuicidesForbidden = savedInstanceState.getBoolean("suicides_forbidden");
			isLSD = savedInstanceState.getBoolean("LSD");
			isLSDAnim = savedInstanceState.getBoolean("LSD_anim");
			isMusicOn = savedInstanceState.getBoolean("isMusicOn");
			areBombsOn = savedInstanceState.getBoolean("areBombsOn");
			areMinesOn = savedInstanceState.getBoolean("areMinesOn");
			if (isLSD)
				mSoundTrack = MediaPlayer.create(this, R.raw.lsd);
			else
				mSoundTrack = MediaPlayer.create(this, R.raw.muz);
			mSoundTrack.setLooping(true);
			if (isMusicOn)
				mSoundTrack.seekTo(savedInstanceState.getInt("musicTime")+400);
			mLastLevel = world.getLevel();
		}
		if (!world.player.isAlive)
			showGameOverDialog();
		if (!areBombsOn)
			findViewById(R.id.bomb_button).setVisibility(View.GONE);
		if (!areMinesOn)
			findViewById(R.id.mine_button).setVisibility(View.GONE);
		view.CreateThread();
		mDrawThread = view.getDrawThread();
		mDrawThread.setWorld(world);
		view.StartThread();
		changeText();
	}

	void refreshButtons(){
		byte bombCost = world.getBombCost();
		int energy = world.player.getEnergy();
		if (World.SAFE_TELEPORT_COST > energy){
			safeTeleButton.setVisibility(View.GONE);
		}
		else{
			safeTeleButton.setVisibility(View.VISIBLE);
		}
		if (areMinesOn){
			if (World.MINE_COST > energy)
				mineButton.setVisibility(View.GONE);
			else{
				mineButton.setVisibility(View.VISIBLE);
				mineButton.setText(Byte.toString(World.MINE_COST));
			}
		}
		if (areBombsOn){
			if (bombCost > energy)
				bombButton.setVisibility(View.GONE);
			else{
				bombButton.setVisibility(View.VISIBLE);
				bombButton.setText(Byte.toString(bombCost));
			}
		}
	}
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
			if (isLSD && isLSDAnim){
				Animation a = AnimationUtils.loadAnimation(this, R.anim.lsd);
				view.startAnimation(a);
			}
			view.mDrawThread.setDefaultCellSize();
			view.mDrawThread.scrollToPlayer();
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
		savedInstanceState.putInt("width", world.getWidth());
		savedInstanceState.putInt("height", world.getHeight());
		for(int i=0; i<world.getWidth(); ++i){
			savedInstanceState.putByteArray("board_"+i, world.board.getRow(i));
		}
		savedInstanceState.putInt("fastbots", world.board.getAliveFastBotCount());
		savedInstanceState.putInt("bots", world.board.getAliveBotCount());
		savedInstanceState.putInt("level", world.getLevel());
		savedInstanceState.putInt("gameMode", world.getGameMode());
		savedInstanceState.putBoolean("vamp_mode", world.isVampMode());
		savedInstanceState.putBoolean("energy_shortage", world.isShortageMode());
		savedInstanceState.putLong("score", world.player.getScore());
		savedInstanceState.putInt("energy", world.player.getEnergy());
		savedInstanceState.putInt("playerX", world.player.getPos().x);
		savedInstanceState.putInt("playerY", world.player.getPos().y);
		savedInstanceState.putBoolean("isAlive", world.player.isAlive);
		savedInstanceState.putBoolean("isWinner", world.player.isWinner);
		savedInstanceState.putBoolean("suicides_forbidden", world.player.areSuicidesForbidden);
		savedInstanceState.putBoolean("isMusicOn", isMusicOn);
		savedInstanceState.putBoolean("areMinesOn", areMinesOn);
		savedInstanceState.putBoolean("areBombsOn", areBombsOn);
		savedInstanceState.putBoolean("LSD", isLSD);
		savedInstanceState.putBoolean("LSD_anim", isLSDAnim);
		if (isMusicOn)
			savedInstanceState.putInt("musicTime",mSoundTrack.getCurrentPosition());
	}

	private void changeText(){
		levelTextView.setText(String.format(getString(R.string.level),world.getLevel()));
		scoreTextView.setText(String.format(" %d", world.player.getScore()));	
		energyTextView.setText(String.format(" %d", world.player.getEnergy()));
		botCountTextView.setText(String.format(" %d",
		 world.board.getAliveBotCount()+world.board.getAliveFastBotCount()));
		refreshButtons();
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
	
	private void showNewLevelToast(){
		Toast.makeText(GameActivity.this, String.format(getString
		 (R.string.new_level), mLastLevel) , Toast.LENGTH_SHORT).show();
	}
	
	private void showGameOverDialog(){
		TextView result = (TextView)findViewById(R.id.gameover_dialog_text);
		if (world.player.isWinner){
			world.player.chScore(world.player.getScore());
			result.setText(R.string.victory);
		}else{
			result.setText(R.string.diedlog);
		}
		if (SaveManager.getInstance().canAddScore(world.player.getScore())){
			scoreInfoTextView.setText(String.format(getString(R.string.score_info),
			 world.player.getScore()));
			scoreInfoTextView.setVisibility(View.VISIBLE);
			findViewById(R.id.add_score_button).setVisibility(View.VISIBLE);
			mGameOverLinearLayout.setVisibility(View.VISIBLE);
		}else{
			scoreInfoTextView.setVisibility(View.GONE);
			findViewById(R.id.add_score_button).setVisibility(View.GONE);
			mGameOverLinearLayout.setVisibility(View.VISIBLE);
		}
	}
	
	@Override
	public void onBackPressed(){
		if (world.player.isAlive){
			save();
			super.onBackPressed();
		}
	}
}

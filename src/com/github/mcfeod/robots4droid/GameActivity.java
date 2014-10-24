package com.github.mcfeod.robots4droid;

import android.app.Activity;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import saves.BinaryIOManager;
import saves.SaveManager;


public class GameActivity extends Activity {
	private int width=10, height=10; //размеры сторон
	private int sizew, sizeh;
	//главный bitmap
	private Bitmap bitMain;
    //bitmap-ы для картинок из ресурсов
	private Bitmap bitRobot, bitFastRobot, bitPlayer, bitJunk;
    private World world;
    private DrawWorld drawWorld;
    Point screen;

    TextView mTextView;

    private MediaPlayer mSoundTrack;
    private boolean isMusicOn;

	public Point GetScreenSize(){
		Display display = getWindowManager().getDefaultDisplay();
		DisplayMetrics metrics = new DisplayMetrics();
		display.getMetrics(metrics);
		return new Point(metrics.widthPixels, metrics.heightPixels);
	}

	public OnClickListener listener = new OnClickListener() {
		@Override
		public void onClick(View v) {
            //Лавинообразный мусор
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
				case R.id.teleport_button: succ=world.movePlayer((byte)(9)); break;	
				case R.id.safe_teleport_button: succ=world.movePlayer((byte)(10)); break;
                case R.id.stay_button: succ = world.movePlayer((byte)4); break;
            }
			if (succ)
				world.moveBots();
			drawWorld.repaint();
			TextView text=(TextView)findViewById(R.id.textView1);
			String str="Level: "+Integer.toString(world.mLevel)+
			 "  Score: "+Integer.toString(world.player.getScore())+
			 "  Energy: "+Integer.toString(world.player.getEnergy())+
			 "  isAlive: "+world.player.isAlive;
			text.setText(str);
		}
	};

    public void load(){
        BinaryIOManager loader = new BinaryIOManager(getApplicationContext(), world);
        world.mBoard.giveLinkToManager(loader);
        SaveManager.INSTANCE.loadGameFromBinary(loader);
    }

    public void save(){
        BinaryIOManager saver = new BinaryIOManager(getApplicationContext(), world);
        world.mBoard.giveLinkToManager(saver);
        SaveManager.INSTANCE.saveGameToBinary(saver);
    }

    private void changeText(){
        String str="Level: "+Integer.toString(world.mLevel)+
                "  Score: "+Integer.toString(world.player.getScore())+
                "  Energy: "+Integer.toString(world.player.getEnergy())+
                "  isAlive: "+world.player.isAlive;
        mTextView.setText(str);
    }

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        findViewById(R.id.save_level_button).setOnClickListener(new OnClickListener() {
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
        
        world = new World(width, height);
        drawWorld = new DrawWorld(this.getApplicationContext(), 
                    (ImageView)findViewById(R.id.imageView1), world, screen);
        mTextView=(TextView)findViewById(R.id.textView1);

        if(SaveManager.INSTANCE.hasLoadingGame()){
            load();
        }
        drawWorld.repaint();
        changeText();
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

    @Override
    protected void onDestroy(){
        super.onDestroy();
        mSoundTrack.release();
    }
}

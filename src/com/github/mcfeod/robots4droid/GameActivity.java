package com.github.mcfeod.robots4droid;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class GameActivity extends Activity {
	private int width=20, height=15; //размеры сторон
	private int widthPX=35, heightPX=35; //размеры клетки в пикселях
    private World world;
    private DrawWorld drawWorld;
    private Point screen;
    private String mMsgStr;
    private int mLastLevel=-1;

    //объекты и переменные для прокручивания поля
	private boolean toScroll;
	private Point startTouchPos, endTouchPos;

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
			TextView text=(TextView)findViewById(R.id.textView1);
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
						succ=world.setMine();
						drawWorld.mainRepaint();
						drawWorld.centerPlayerPos(world.player.getPos().x*widthPX+
						 widthPX/2, world.player.getPos().y*heightPX+heightPX/2);
						mMsgStr="Level: "+Integer.toString(world.mLevel)+
						 "  Score: "+Integer.toString(world.player.getScore())+
						 "  Energy: "+Integer.toString(world.player.getEnergy());
						text.setText(mMsgStr);
						break;
				}
				if (succ){
					/*отрисовываем игрока с центрированием только при телепортации,
					  потому что при простом ходе получается очень резкое перемещение поля*/
					switch (v.getId()){
						case R.id.teleport_button:
							drawWorld.centerPlayerPos(world.player.getPos().x*widthPX+
							 widthPX/2, world.player.getPos().y*heightPX+heightPX/2);
							break;
						case R.id.safe_teleport_button: 
							drawWorld.centerPlayerPos(world.player.getPos().x*widthPX+
							 widthPX/2, world.player.getPos().y*heightPX+heightPX/2);
							break;
						default:
							drawWorld.repaint(0,0);
					}
					//передвигаем роботов
					world.moveBots();
					//отрисовываем роботов
					drawWorld.mainRepaint();
					drawWorld.repaint(0,0);
					if (world.player.isAlive)
						mMsgStr="Level: "+Integer.toString(world.mLevel)+
						 "  Score: "+Integer.toString(world.player.getScore())+
						 "  Energy: "+Integer.toString(world.player.getEnergy());			
					else{
						mMsgStr = "Tap any button to replay";
						drawWorld.death();
						drawWorld.repaint(0,0);
					}					
					text.setText(mMsgStr);
				}else{
					Toast.makeText(GameActivity.this, "You can't do it", Toast.LENGTH_SHORT).show();
				}
			}else{
				world.defeat();
				drawWorld.mainRepaint();
				drawWorld.centerPlayerPos(world.player.getPos().x*widthPX+
				 widthPX/2, world.player.getPos().y*heightPX+heightPX/2);
			}	
			if (mLastLevel != world.mLevel){
				drawWorld.repaint(0, 0);
				mLastLevel = world.mLevel;
				Toast.makeText(GameActivity.this, "NEW LEVEL: " + mLastLevel , Toast.LENGTH_SHORT).show();
				
			}
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
        
        startTouchPos = new Point();
        endTouchPos = new Point();

        screen = GetScreenSize();
        //костыль. Не могу понять, почему screen.x меньше реальной ширины экрана
        screen.x +=50;
        //screen.y - не весь экран, а область, в которой отрисовывается поле
        screen.y -= findViewById(R.id.frameLayout).getTop();

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
        
        mSoundTrack = MediaPlayer.create(this, R.raw.muz);
        mSoundTrack.setLooping(true);
        // при сворачивании приложения музыка должна выключаться, а при восстановлении включаться.
        // по этой причине start() и stop() размещены в onStart() и onStop()
        String settings = getIntent().getStringExtra(MainActivity.SETTINGS);
        isMusicOn = SettingsParser.isMusicOn(settings);
        
        world = new World(width, height);
        drawWorld = new DrawWorld(this.getApplicationContext(), 
         (ImageView)findViewById(R.id.imageView1), world, screen, widthPX, heightPX);
		drawWorld.mainRepaint();
		drawWorld.centerPlayerPos(world.player.getPos().x*widthPX+
		 widthPX/2, world.player.getPos().y*heightPX+heightPX/2);
		
        TextView text=(TextView)findViewById(R.id.textView1);
        String str="Level: "+Integer.toString(world.mLevel)+
   		 "  Score: "+Integer.toString(world.player.getScore())+
   		 "  Energy: "+Integer.toString(world.player.getEnergy());
		text.setText(str);
    }
	
	@Override
    public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
			//событие возникает при нажатии на экран
			case MotionEvent.ACTION_DOWN:
				//запоминаем координаты нажатия
				startTouchPos.x = (int) event.getRawX();
				startTouchPos.y = (int) event.getRawY();
				//если координата y выше поля, то запрещаем scroll
				if (startTouchPos.y < findViewById(R.id.frameLayout).getTop())
					toScroll = false;
				else
					toScroll = true;
				break;
			//событие возникает при движении по экрану
			case MotionEvent.ACTION_MOVE:
				if (toScroll){
					//запоминаем координаты касания
					endTouchPos.x = (int) event.getRawX();
					endTouchPos.y = (int) event.getRawY();
					//если координата y выше поля, то не перерисовываем поле
					if (endTouchPos.y > findViewById(R.id.frameLayout).getTop()){
						/*перерисовываем поле startTouchPos.x - endTouchPos.x и
						  startTouchPos.y - endTouchPos.y - разница между предыдущей
						  точкой касания и текущей. Определяет, на сколько необходимо
						  передвинуть поле по x и по y*/
						drawWorld.repaint(startTouchPos.x - endTouchPos.x,
						 startTouchPos.y - endTouchPos.y);
						//запоминаем новые координаты начальной точки касания
						startTouchPos.x = endTouchPos.x;
						startTouchPos.y = endTouchPos.y;
					}
					break;
				}
			case MotionEvent.ACTION_UP:
				toScroll = false;
				break;
		}
		return true;
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
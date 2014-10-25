package com.github.mcfeod.robots4droid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends Activity {
    public static final String SETTINGS =
            "com.github.mcfeod.robots4droid.settings";
    //здесь хранятся игровые настройки
    String mSettings = "0";
    //что означает: по умолчанию звук включен

    @Override
    protected void onCreate(Bundle savedInstanceBundle){
        super.onCreate(savedInstanceBundle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
         WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.main);

        findViewById(R.id.settings_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, SettingsActivity.class);
                i.putExtra(SETTINGS, mSettings);
                startActivityForResult(i, 0);
            }
        });
        findViewById(R.id.game_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, GameActivity.class);
                i.putExtra(SETTINGS, mSettings);
                startActivity(i);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        mSettings = data.getStringExtra(SETTINGS);
        /*
        * Вот что каждый раз при этом говорит logcat:
        *  10-10 11:25:38.061    1679-1679/com.github.mcfeod.robots4droid I/Choreographer﹕ Skipped 37 frames!  The application may be doing too much work on its main thread.
        *  10-10 11:25:38.662    1679-1679/com.github.mcfeod.robots4droid I/Choreographer﹕ Skipped 67 frames!  The application may be doing too much work on its main thread.
        *  10-10 11:25:46.171    1679-1683/com.github.mcfeod.robots4droid D/dalvikvm﹕ GC_CONCURRENT freed 104K, 3% free 6357K/6496K, paused 22ms+16ms, total 241ms
        *  10-10 11:25:46.281    1679-1679/com.github.mcfeod.robots4droid I/Choreographer﹕ Skipped 44 frames!  The application may be doing too much work on its main thread.
        * Есть лёгкое подозрение, что что-то можно оптимизировать.
        */
    }

}

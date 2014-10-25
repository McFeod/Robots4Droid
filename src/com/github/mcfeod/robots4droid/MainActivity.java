package com.github.mcfeod.robots4droid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import saves.LoadActivity;
import saves.SaveManager;

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
        findViewById(R.id.load_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, LoadActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        mSettings = data.getStringExtra(SETTINGS);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(SaveManager.INSTANCE.hasLoadingGame()){
            Intent i = new Intent(MainActivity.this, GameActivity.class);
            i.putExtra(SETTINGS, mSettings);
            startActivity(i);
        }
    }
}

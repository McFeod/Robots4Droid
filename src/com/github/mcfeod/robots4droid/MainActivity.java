package com.github.mcfeod.robots4droid;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import saves.LoadActivity;
import saves.SaveManager;

public class MainActivity extends Activity {
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
                startActivity(i);

            }
        });
        findViewById(R.id.game_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, GameActivity.class);
                startActivity(i);
            }
        });
        findViewById(R.id.load_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	 //if (Build.VERSION.SDK_INT >= 11){
            		 Intent i = new Intent(MainActivity.this, LoadActivity.class);
            		 startActivity(i);
            //	 }
            }
        });
        //findViewById(R.id.load_button).setVisibility();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(SaveManager.getInstance().hasLoadingGame()){
            Intent i = new Intent(MainActivity.this, GameActivity.class);
            startActivity(i);
        }
    }
}

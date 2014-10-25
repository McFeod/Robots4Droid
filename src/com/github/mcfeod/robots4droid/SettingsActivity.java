package com.github.mcfeod.robots4droid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class SettingsActivity extends Activity {

    private CheckBox musicBox;
    private CheckBox suicideBox;
    private RadioButton mNormalButton;
    private RadioButton mExtraFastButton;
    private RadioGroup complexityGroup;
    private Intent mIntent;

    @Override
    protected void onCreate(Bundle savedInstanceBundle) {
        super.onCreate(savedInstanceBundle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
         WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_settings);

        musicBox = (CheckBox) findViewById(R.id.musicBox);
        suicideBox = (CheckBox) findViewById(R.id.suicideBox);
        complexityGroup = (RadioGroup) findViewById(R.id.complexityGroup);
        mNormalButton = (RadioButton) findViewById(R.id.normalRadio);
        mExtraFastButton = (RadioButton) findViewById(R.id.extraRadio);

        musicBox.setChecked(SettingsParser.isMusicOn());
        suicideBox.setChecked(SettingsParser.areSuicidesOn());
        if(SettingsParser.needExtraFastBots()){
            mExtraFastButton.setChecked(true);
        }else{
            mNormalButton.setChecked(true);
        }

        musicBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingsParser.setMusicMode(musicBox.isChecked());
            }
        });

        suicideBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingsParser.setSuicidePermission(suicideBox.isChecked());
            }
        });

        complexityGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.normalRadio:
                        SettingsParser.setGameComplexity(SettingsParser.NORMAL_MODE);
                        break;
                    case R.id.extraRadio:
                        SettingsParser.setGameComplexity(SettingsParser.EXTRA_FAST_BOTS);
                        break;
                }
            }
        });
    }
}

//    Даже так не работает
//    @Override
//    protected void onPause(){
//        mIntent.putExtra(MainActivity.SETTINGS,
//                SettingsParser.getSettingsString(musicBox.isChecked(), false, 0));
//        setResult(RESULT_OK, mIntent);
//        Log.i("Settings", "stopped\n");
//        super.onPause();
//    }

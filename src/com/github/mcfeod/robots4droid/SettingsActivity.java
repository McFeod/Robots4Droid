package com.github.mcfeod.robots4droid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;

public class SettingsActivity extends Activity {

    private CheckBox musicBox;
    private Intent mIntent;

    @Override
    protected void onCreate(Bundle savedInstanceBundle) {
        super.onCreate(savedInstanceBundle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
         WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_settings);

        musicBox = (CheckBox)findViewById(R.id.musicBox);
        mIntent = getIntent();
        //страховочный костыль на случай, если сразу нажата Back
        setResult(RESULT_OK, mIntent);

        if(mIntent.getStringExtra(MainActivity.SETTINGS).charAt(0) == '1'){
            musicBox.setChecked(true);
        }else{
            musicBox.setChecked(false);
        }

        musicBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIntent.putExtra(MainActivity.SETTINGS,
                                 SettingsParser.getSettingsString(musicBox.isChecked()));
                setResult(RESULT_OK, mIntent);
            }
        });
    }
}

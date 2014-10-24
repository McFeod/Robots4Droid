package com.github.mcfeod.robots4droid;


import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import saves.MyListFragment;

public class SettingsActivity extends Activity {

    private CheckBox musicBox;
    private Intent mIntent;
    private FragmentManager mFm;

    @Override
    protected void onCreate(Bundle savedInstanceBundle) {
        super.onCreate(savedInstanceBundle);
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

        // пошли фрагменты

        mFm = getFragmentManager();
        Fragment fragment = mFm.findFragmentById(R.id.fragmentContainer);
        if(fragment == null){
            //fragment = new SenselessFragment();
            fragment = new MyListFragment();
            mFm.beginTransaction()
                    .add(R.id.fragmentContainer, fragment)
                    .commit();
        }

    }
//    Не знаю, почему, но так не работает. Было бы эффективнее.
//    @Override
//    protected void onDestroy(){
//        mIntent.putExtra(MainActivity.SETTINGS,
//                SettingsParser.getSettingsString(musicBox.isChecked()));
//        setResult(RESULT_OK, mIntent);
//    }
}

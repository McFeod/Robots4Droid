package com.github.mcfeod.robots4droid;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;

public class SaveActivity extends HostActivity {
    @Override
    protected Fragment createFragment(){
        int saveNumber = getIntent().getIntExtra(SaveFragment.SAVED_GAME_NUMBER, -1);
        if(saveNumber < 0){
            Log.d("SaveActivity Error","No SAVED_GAME_NUMBER extra in Intent!");
            // throw exception?
            throw new RuntimeException("No SAVED_GAME_NUMBER extra in Intent!");
        }else{
            Log.d("SaveActivity Report","SaveFragment.newInstance() called");
            return SaveFragment.newInstance(saveNumber);
        }
    }
}

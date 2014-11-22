package com.github.mcfeod.robots4droid;

import java.io.IOException;

import saves.SaveManager;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
public class SettingsActivity extends Activity {

	private CheckBox musicBox, suicideBox, mineBox, bombBox;
	private RadioButton mNormalButton, mExtraFastButton;
	private RadioGroup complexityGroup;

	@Override
	protected void onCreate(Bundle savedInstanceBundle) {
		super.onCreate(savedInstanceBundle);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		 WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_settings);

		musicBox = (CheckBox) findViewById(R.id.musicBox);
		suicideBox = (CheckBox) findViewById(R.id.suicideBox);
		mineBox = (CheckBox) findViewById(R.id.mineBox);
		bombBox = (CheckBox) findViewById(R.id.bombBox);
		complexityGroup = (RadioGroup) findViewById(R.id.complexityGroup);
		mNormalButton = (RadioButton) findViewById(R.id.normalRadio);
		mExtraFastButton = (RadioButton) findViewById(R.id.extraRadio);
		musicBox.setChecked(SettingsParser.isMusicOn());
		suicideBox.setChecked(!SettingsParser.areSuicidesOn());
		mineBox.setChecked(SettingsParser.areMinesOn());
		bombBox.setChecked(SettingsParser.areBombsOn());
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
				SettingsParser.setSuicidePermission(!suicideBox.isChecked());
			}
		});

		mineBox.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				SettingsParser.setMineMode(mineBox.isChecked());
			}
		});

		bombBox.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				SettingsParser.setBombMode(bombBox.isChecked());
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
	@Override
	public void onBackPressed() {
		try {
			SaveManager.getInstance().saveGeneralSettings(SettingsActivity.this);
		} catch (IOException e) {}
		super.onBackPressed();
	}
}

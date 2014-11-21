package com.github.mcfeod.robots4droid;

import java.io.IOException;

import saves.SaveManager;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

public class SettingsActivity extends Activity {

	private CheckBox musicBox, suicideBox, mineBox, bombBox;
	private RadioButton mNormalButton, mExtraFastButton;
	private RadioGroup complexityGroup;
	private Spinner languageSpinner;
	private TextView complexityTextView, languageTextView;

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
		languageSpinner = (Spinner) findViewById(R.id.language_spinner);
		complexityTextView = (TextView) findViewById(R.id.complexity_textView);
		languageTextView = (TextView) findViewById(R.id.language_textView);
		
		musicBox.setChecked(SettingsParser.isMusicOn());
		suicideBox.setChecked(SettingsParser.areSuicidesOn());
		mineBox.setChecked(SettingsParser.areMinesOn());
		bombBox.setChecked(SettingsParser.areBombsOn());
		if(SettingsParser.needExtraFastBots()){
			mExtraFastButton.setChecked(true);
		}else{
			mNormalButton.setChecked(true);
		}
		languageSpinner.setSelection(SettingsParser.getLanguage());
		
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
		
		languageSpinner.setOnItemSelectedListener(new OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
			 int position, long id) {
				SettingsParser.setLanguage((byte)position);
				refreshLanguage();
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0){
			}
		});
		refreshLanguage();
	}

	private void refreshLanguage(){
		switch (SettingsParser.getLanguage()){
			case 0:
				musicBox.setText(R.string.music_setting_ru);
				suicideBox.setText(R.string.suicide_setting_ru);
				mineBox.setText(R.string.mine_setting_ru);
				bombBox.setText(R.string.bomb_setting_ru);
				mNormalButton.setText(R.string.easy_setting_ru);
				mExtraFastButton.setText(R.string.hard_setting_ru);
		        complexityTextView.setText(R.string.complexity_setting_ru);
		        languageTextView.setText(R.string.language_setting_ru);
		        break;
			case 1:
				musicBox.setText(R.string.music_setting);
				suicideBox.setText(R.string.suicide_setting);
				mineBox.setText(R.string.mine_setting);
				bombBox.setText(R.string.bomb_setting);
				mNormalButton.setText(R.string.easy_setting);
				mExtraFastButton.setText(R.string.hard_setting);
		        complexityTextView.setText(R.string.complexity_setting);
		        languageTextView.setText(R.string.language_setting);
		        break;
		}
	}
	
	@Override
	public void onBackPressed() {
		try {
			SaveManager.getInstance().saveGeneralSettings(SettingsActivity.this);
		} catch (IOException e) {}
		super.onBackPressed();
	}
}
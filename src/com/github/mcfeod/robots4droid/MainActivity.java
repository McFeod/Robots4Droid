package com.github.mcfeod.robots4droid;

import java.io.IOException;

import saves.LoadActivity;
import saves.SaveManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

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

		findViewById(R.id.new_game_button).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(MainActivity.this, GameActivity.class);
				startActivity(i);
			}
		});

		findViewById(R.id.continue_button).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (SaveManager.getInstance().markLast()){
					Intent i = new Intent(MainActivity.this, GameActivity.class);
					startActivity(i);}
				else{
					Toast.makeText(MainActivity.this, getString(R.string.no_saved), Toast.LENGTH_SHORT).show();
				}
			}
		});

		findViewById(R.id.load_button).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
					 Intent i = new Intent(MainActivity.this, LoadActivity.class);
					 startActivity(i);
			}
		});

		findViewById(R.id.about_button).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
				builder.setMessage(R.string.dialog_about_message);
				builder.setCancelable(true);
				AlertDialog dialog = builder.create();
				dialog.show();
			}
		});
		try {
			SaveManager.getInstance().loadGeneralSettings(MainActivity.this);
		} catch (IOException e) {}
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

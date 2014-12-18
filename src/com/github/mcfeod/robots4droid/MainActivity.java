package com.github.mcfeod.robots4droid;

import java.io.IOException;

import saves.LoadActivity;
import saves.SaveManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	private final OnItemClickListener listener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
			Intent intent = null;
			switch (position){
				case 0: 
					if (SaveManager.getInstance().markLast())
						intent = new Intent(MainActivity.this, GameActivity.class);
					else
						Toast.makeText(MainActivity.this, getString(R.string.no_saved), Toast.LENGTH_SHORT).show();
					break;
				case 1: 
					intent = new Intent(MainActivity.this, GameActivity.class);
					break;
				case 2: 
					intent = new Intent(MainActivity.this, LoadActivity.class);
					break;
				case 3: 
					intent = new Intent(MainActivity.this, ScoreActivity.class);
					break;
				case 4: 
					intent = new Intent(MainActivity.this, SettingsActivity.class);
					break;
				case 5: 
					intent = new Intent(MainActivity.this, AboutActivity.class);
					break;
			}
			if (intent != null)
				startActivity(intent);
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceBundle){
		super.onCreate(savedInstanceBundle);
		setContentView(R.layout.main);
		try{
			SaveManager.getInstance().openDatabaseConnection();
			SaveManager.getInstance().loadSavesFromDatabase(MainActivity.this);
			SaveManager.getInstance().closeDatabaseConnection();
		}catch (RuntimeException e){
			Log.d("MainActivity", "Loading error" + e.getMessage());
		}
		try{
			SaveManager.getInstance().loadScores(MainActivity.this);
		}catch (IOException e){}
		ListView buttons = (ListView)findViewById(R.id.menu_buttons);
		String[] btns = this.getResources().getStringArray(R.array.main_menu);
		buttons.setAdapter(new ArrayAdapter<String>(MainActivity.this, R.layout.main_menu_item, btns));
		buttons.setOnItemClickListener(listener);
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

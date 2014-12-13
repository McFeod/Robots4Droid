package com.github.mcfeod.robots4droid;

import java.io.IOException;

import saves.SaveManager;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ScoreActivity extends Activity {

	private ListView listView;
	
	@Override
	public void onCreate(Bundle savedInstanceBundle){
		super.onCreate(savedInstanceBundle);
		setContentView(R.layout.activity_score);

		listView = (ListView) findViewById(R.id.listView);
		
		String[] ar = SaveManager.getInstance().getScores();
		ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
		 android.R.layout.simple_list_item_1, ar);
		listView.setAdapter(adapter);
		
		findViewById(R.id.delete_scores_button).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try{
					SaveManager.getInstance().deleteScores(ScoreActivity.this);
				}catch(IOException e){}
				String[] ar = SaveManager.getInstance().getScores();
				ArrayAdapter<String> adapter = new ArrayAdapter<>(ScoreActivity.this,
				 android.R.layout.simple_list_item_1, ar);
				listView.setAdapter(adapter);
			}
		});
	}
}
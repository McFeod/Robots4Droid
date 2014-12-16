package com.github.mcfeod.robots4droid;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import saves.SaveManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class ScoreActivity extends Activity {

	private ListView listView;
	
	@Override
	public void onCreate(Bundle savedInstanceBundle){
		super.onCreate(savedInstanceBundle);
		setContentView(R.layout.activity_score);

		listView = (ListView) findViewById(R.id.listView);
		refreshListView();
		findViewById(R.id.delete_scores_button).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(ScoreActivity.this);
				builder.setMessage(getString(R.string.delete));
				builder.setCancelable(false);
				builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						try{
							SaveManager.getInstance().deleteScores(ScoreActivity.this);
						}catch(IOException e){}
						refreshListView();
					}
				});
				builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
				builder.show();
			}
		});
	}
	
	public void refreshListView(){
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		String[] scoresName = SaveManager.getInstance().getScoresName(ScoreActivity.this);
		String[] scoresInfo = SaveManager.getInstance().getScoresInfo(ScoreActivity.this);
	    for (int i=0; i<scoresName.length; i++){
	        HashMap<String,String> data = new HashMap<String, String>();
	        data.put("Title", scoresName[i]);
	        data.put("SubItem", scoresInfo[i]);
	        list.add(data);
	    }
	    SimpleAdapter adapter = new SimpleAdapter(this, list,
	     android.R.layout.simple_list_item_2, new String[] {"Title", "SubItem"}, 
	     new int[] {android.R.id.text1, android.R.id.text2});
		listView.setAdapter(adapter);
	}
}
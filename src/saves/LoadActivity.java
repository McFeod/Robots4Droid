package saves;

import java.util.ArrayList;
import java.util.Random;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.github.mcfeod.robots4droid.R;

public class LoadActivity extends ListActivity {
	private int mPosition;
	Random rand;

	@Override
	public void onCreate(Bundle savedInstanceBundle){
		super.onCreate(savedInstanceBundle);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		 WindowManager.LayoutParams.FLAG_FULLSCREEN);

		try{
			SaveManager.getInstance().openDatabaseConnection();
			SaveManager.getInstance().loadSavesFromDatabase(LoadActivity.this);
		}catch (RuntimeException e){
			Log.d("MyListFragment", "Loading error" + e.getMessage());
		}
		
		rand = new Random();
		SaveAdapter adapter = new SaveAdapter(SaveManager.getInstance().mGames);
		setListAdapter(adapter);
		this.getListView().setOnItemLongClickListener(new OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> av, View v, int position, long id) {
				String selectedItem = av.getItemAtPosition(position).toString();
				mPosition = position;
				AlertDialog.Builder builder = new AlertDialog.Builder(LoadActivity.this);
				builder.setMessage(selectedItem + getString(R.string.delete));
				builder.setCancelable(false);
				builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						SaveManager.getInstance().removeSave(mPosition, LoadActivity.this.getApplicationContext());
						setListAdapter(new SaveAdapter(SaveManager.getInstance().mGames));
						//костыль. Должен быть способ как-то обновить ListView. Хотя раньше вообще OnCreate вызывался...
					}
				});
				
				builder.setNeutralButton(getString(R.string.maybe), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if(rand.nextInt()%2 == 0)
							dialog.cancel();
						else{
							SaveManager.getInstance().removeSave(mPosition, LoadActivity.this.getApplicationContext());
							setListAdapter(new SaveAdapter(SaveManager.getInstance().mGames));
						}
					}
				});
				
				builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
				builder.show();
				return true;
			}
		});
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		SaveManager.getInstance().rememberGame(position);
		LoadActivity.this.finish();
	}

	@Override
	public void onResume(){
		super.onResume();
		if(SaveManager.getInstance().hasLoadingGame()){
			SaveManager.getInstance().closeDatabaseConnection();
			LoadActivity.this.finish();
		}
		((SaveAdapter)getListAdapter()).notifyDataSetChanged();
	}

	private class SaveAdapter extends ArrayAdapter<SavedGame>{
		public SaveAdapter(ArrayList<SavedGame> games){
			super(LoadActivity.this, 0, games);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// если представление не получено
			if(convertView == null){
				convertView = LoadActivity.this.getLayoutInflater()
				 .inflate(R.layout.list_item_save, null);
			}
			//настройка представления
			SavedGame game = getItem(position);
			TextView saveInfo = (TextView)convertView.findViewById(R.id.itemSaveTitle);
			saveInfo.setText(game.toString());
			return convertView;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		SaveManager.getInstance().closeDatabaseConnection();
	}
}
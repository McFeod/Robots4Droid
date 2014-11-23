package saves;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.github.mcfeod.robots4droid.R;

public class LoadActivity extends ListActivity {
	private int mPosition;

	@Override
	public void onCreate(Bundle savedInstanceBundle){
		super.onCreate(savedInstanceBundle);

		try {
			SaveManager.getInstance().openDatabaseConnection();
			SaveManager.getInstance().loadSavesFromDatabase();
		}
		catch (RuntimeException e) {
			Log.d("MyListFragment", "Loading error" + e.getMessage());
		}

		SaveAdapter adapter = new SaveAdapter(SaveManager.getInstance().mGames);
		setListAdapter(adapter);
		
		this.getListView().setOnItemLongClickListener(new OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> av, View v, int position, long id) {
				String selectedItem = av.getItemAtPosition(position).toString();
				mPosition = position;
				AlertDialog.Builder builder = new AlertDialog.Builder(LoadActivity.this);
				builder.setMessage(selectedItem + getString(R.string.delete));
				builder.setCancelable(false);
				builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								SaveManager.getInstance().removeSave(mPosition, LoadActivity.this.getApplicationContext());
								setListAdapter(new SaveAdapter(SaveManager.getInstance().mGames));
								//костыль. Должен быть способ как-то обновить ListView. Хотя раньше вообще OnCreate вызывался...
							}
						});
				builder.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
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
		SavedGame game = ((SaveAdapter)getListAdapter()).getItem(position);
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
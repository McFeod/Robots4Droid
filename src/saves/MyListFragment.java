package saves;


import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.github.mcfeod.robots4droid.R;

import java.util.ArrayList;

public class MyListFragment extends ListFragment{

    @Override
    public void onCreate(Bundle savedInstanceBundle){
        super.onCreate(savedInstanceBundle);

        try {
            SaveManager.INSTANCE.openDatabaseConnection();
            SaveManager.INSTANCE.loadSavesFromDatabase();
        }
        catch (RuntimeException e) {
            Log.d("MyListFragment", "Loading error" + e.getMessage());
        }

        SaveAdapter adapter = new SaveAdapter(SaveManager.INSTANCE.mGames);
        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        SavedGame game = ((SaveAdapter)getListAdapter()).getItem(position);

        // запускаем активность с информацией о соохранении game
        Intent i = new Intent(getActivity(), SaveActivity.class);
        i.putExtra(SaveFragment.SAVED_GAME_NUMBER, position);
        startActivity(i);
    }

    @Override
    public void onResume(){
        super.onResume();
        if(SaveManager.INSTANCE.hasLoadingGame()){
            SaveManager.INSTANCE.closeDatabaseConnection();
            getActivity().finish();
        }
        ((SaveAdapter)getListAdapter()).notifyDataSetChanged();
    }

    private class SaveAdapter extends ArrayAdapter<SavedGame>{
        public SaveAdapter(ArrayList<SavedGame> games){
            super(getActivity(), 0, games);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // если представление не получено
            if(convertView == null){
                convertView = getActivity().getLayoutInflater()
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
        SaveManager.INSTANCE.closeDatabaseConnection();
    }
}

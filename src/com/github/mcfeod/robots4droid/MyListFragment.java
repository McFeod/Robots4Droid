package com.github.mcfeod.robots4droid;


import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.util.ArrayList;

public class MyListFragment extends ListFragment{
    @Override
    public void onCreate(Bundle savedInstanceBundle){
        super.onCreate(savedInstanceBundle);
        getActivity().setTitle("123 Fragment appears!\n");

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
}

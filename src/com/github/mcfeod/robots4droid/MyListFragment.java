package com.github.mcfeod.robots4droid;


import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.util.ArrayList;

public class MyListFragment extends ListFragment{
    @Override
    public void onCreate(Bundle savedInstanceBundle){
        super.onCreate(savedInstanceBundle);
        getActivity().setTitle("123 Fragment appears!\n");


        ArrayAdapter<SavedGame> adapter =
                new ArrayAdapter<SavedGame>(getActivity(),
                        android.R.layout.simple_list_item_1,
                        SaveManager.INSTANCE.mGames);
        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        //super.onListItemClick(l, v, position, id);
        SavedGame game = (SavedGame)(getListAdapter()).getItem(position);
        Log.i("Clock on", String.valueOf(game.getNumber()));
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

            TextView saveInfo = (TextView)convertView.findViewById(R.id.saveInfo);
            saveInfo.setText(game.toString());

            return convertView;
        }
    }

}

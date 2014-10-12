package com.github.mcfeod.robots4droid;


import java.util.ArrayList;

public class SaveManager {
    public ArrayList<SavedGame> mGames = new ArrayList<SavedGame>();
    public static final SaveManager INSTANCE = new SaveManager();


    private SaveManager(){
        final int primeNumber = 15;
        for (int i = 0; i < primeNumber ; i++) {
            mGames.add(new SavedGame());
        }
    }
}

package saves;

import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.util.Log;

public class SaveManager {
    public static final String TAG = "SaveManager";
    private static final SaveManager INSTANCE = new SaveManager();
    public static final int UNDEFINED_NUMBER = -2;

    public ArrayList<SavedGame> mGames  = new ArrayList<SavedGame>();

    private int mLoadingGameNumber = UNDEFINED_NUMBER;
    private boolean mIsDatabaseClosed = true;

    public static SaveManager getInstance() {
		return INSTANCE;
	}

	public void closeDatabaseConnection(){
        if(!mIsDatabaseClosed){
        	SaveDatabaseManager.getInstance().closeDatabase();
            mIsDatabaseClosed = true;
        }
    }

    public boolean hasLoadingGame(){
        return mLoadingGameNumber != UNDEFINED_NUMBER;
    }

    public void loadGameFromBinary(BinaryIOManager fileManager) {
        try {
            SavedGame loadingGame = mGames.get(mLoadingGameNumber);
            fileManager.loadGame(loadingGame);
            Log.d(TAG, "Successful load");
            // удаляем соохранение из списка
            mGames.remove(mLoadingGameNumber);
            // удаляем запись о соохранении из базы данных
            SaveDatabaseManager.getInstance().openDatabase();
            SaveDatabaseManager.getInstance().deleteSave(loadingGame);
            SaveDatabaseManager.getInstance().closeDatabase();
            // удаляем файл соохранения
            fileManager.deleteGame(loadingGame);
            // стираем память о соохранении
            mLoadingGameNumber = UNDEFINED_NUMBER;
        } catch (IOException e) {
            Log.d(TAG, "IOException during load");
            // Исключение может быть брошено только второй строкой, следовательно,
            // если файл не удастся прочитать, соохранение не будет удалено.
            // Теоретически нужно немного подождать и попробовать ещё раз.
        }
    }

    public void loadSavesFromDatabase(){
    	SaveDatabaseManager.getInstance().loadSaves();
    }

    public void openDatabaseConnection(){
        if(mIsDatabaseClosed){
        	SaveDatabaseManager.getInstance().openDatabase();
            mIsDatabaseClosed = false;
        }
    }

    public void rememberGame(int gameNumber){
        mLoadingGameNumber = gameNumber;
    }

    public void removeSave(int saveNumber, Context context){
        SavedGame save = mGames.get(saveNumber);
        // удаляем соохранение из списка
        mGames.remove(saveNumber);
        // удаляем запись о соохранении из базы данных
        SaveDatabaseManager.getInstance().deleteSave(save);
        // удаляем файл соохранения
        BinaryIOManager.deleteGameFile(save, context);
    }

    public void saveGameToBinary(BinaryIOManager fileManager){
        try {
            SavedGame save = fileManager.saveGame();
            mGames.add(save);
            SaveDatabaseManager.getInstance().openDatabase();
            SaveDatabaseManager.getInstance().insertSave(save);
            SaveDatabaseManager.getInstance().closeDatabase();
        }
        catch (IOException e){
            Log.d(TAG, "IOException during save");
            // Теоретически нужно немного подождать и попробовать ещё раз.
        }
    }

    private SaveManager(){}
}
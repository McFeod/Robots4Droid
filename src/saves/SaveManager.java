package saves;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.util.Log;

import com.github.mcfeod.robots4droid.SettingsParser;

public class SaveManager {
	public static final String TAG = "SaveManager";
	private static final SaveManager INSTANCE = new SaveManager();
	public static final int UNDEFINED_NUMBER = -2;

	public ArrayList<SavedGame> mGames = new ArrayList<SavedGame>();
	
	private static final byte MAX_SCORE_COUNT = 10;
	private String[] mScoreNameArray = new String[MAX_SCORE_COUNT];
	private int[] mScoreArray = new int[MAX_SCORE_COUNT];
	private byte mScoreCount = 0;
	
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

	public void loadSavesFromDatabase(Context context){
		SaveDatabaseManager.getInstance().loadSaves(context);
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
	
	public boolean markLast(){
		if (mGames.size() > 0){
			mLoadingGameNumber = mGames.size()-1;
			return true;
		}
		return false; // сигнал того, что загружать нечего
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

	public void saveGeneralSettings(Context context) throws IOException{
		BufferedOutputStream stream = null;
		stream = new BufferedOutputStream(
		 context.openFileOutput("Gen_Settings", Context.MODE_PRIVATE));
		DataOutput out = new DataOutputStream(stream);
		out.writeBoolean(SettingsParser.isMusicOn());
		stream.close();
	}

	public void loadGeneralSettings(Context context) throws IOException{
		BufferedInputStream stream = null;
		stream = new BufferedInputStream(context.openFileInput("Gen_Settings"));
		DataInput in = new DataInputStream(stream);
		SettingsParser.setMusicMode(in.readBoolean());
		stream.close();
	}
	
	/** Загрузка списка рекордов */
	public void loadScores(Context context) throws IOException{
		BufferedInputStream stream = new BufferedInputStream(context.openFileInput("Score"));
		DataInput in = new DataInputStream(stream);
		mScoreCount = 0;
		while (stream.available() != 0){
			mScoreArray[mScoreCount] = in.readInt();
			mScoreNameArray[mScoreCount] = in.readUTF();
			mScoreCount++;
		}
		stream.close();
	}
	
	/** Добавление нового рекорда и сохранение в файл */
	public void addScore(Context context, String name, int score) throws IOException{
		boolean found = false;
		for (byte i=0; i<mScoreCount; i++)
			if (mScoreArray[i] < score){
				for (byte j=mScoreCount; j>i; j--)
					if (j != MAX_SCORE_COUNT-1){
						mScoreArray[j] = mScoreArray[j-1];
						mScoreNameArray[j] = mScoreNameArray[j-1];
					}
				mScoreArray[i] = score;
				mScoreNameArray[i] = name;
				mScoreCount++;
				found = true;
				break;
			}
		if (!found && (mScoreCount != MAX_SCORE_COUNT)){
			mScoreArray[mScoreCount] = score;
			mScoreNameArray[mScoreCount] = name;
			mScoreCount++;
		}
		saveScores(context);
	}
	
	/** Возвращает список строк с рекордом и именем*/
	public String[] getScores(){
		String[] ar = new String[mScoreCount];
		for (byte i=0; i<mScoreCount; i++)
			ar[i] = mScoreNameArray[i] + " - " + Integer.toString(mScoreArray[i]);
		return ar;
	}
	
	/** Сохраняет список рекордов в файл */
	public void saveScores(Context context) throws IOException{
		BufferedOutputStream stream = new BufferedOutputStream(context.openFileOutput("Score", Context.MODE_PRIVATE));
		DataOutput out = new DataOutputStream(stream);
		for (byte i=0; i<mScoreCount; i++){
			out.writeInt(mScoreArray[i]);
			out.writeUTF(mScoreNameArray[i]);
		}
		stream.close();
	}
	
	/** Удаляет все рекорды */
	public void deleteScores(Context context) throws IOException{
		mScoreCount = 0;
		saveScores(context);
	}
	
	/** Возвращает true, если score!=0 и в списке есть место для записи нового рекорда */
	public boolean canAddScore(int score){
		if (score == 0)
			return false;
		for (byte i=0; i<mScoreCount; i++)
			if (mScoreArray[i] < score)
				return true;
		if (mScoreCount < MAX_SCORE_COUNT)
			return true;
		return false;
	}
}
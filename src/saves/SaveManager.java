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

import com.github.mcfeod.robots4droid.R;
import com.github.mcfeod.robots4droid.World;

public class SaveManager {
	private static final String TAG = "SaveManager";
	private static final SaveManager INSTANCE = new SaveManager();
	private static final int UNDEFINED_NUMBER = -2;

	public ArrayList<SavedGame> mGames = new ArrayList<>();
	
	private static final byte MAX_SCORE_COUNT = 10;
	private String[] mScoreNameArray = new String[MAX_SCORE_COUNT];
	private byte[] mScoreGameModeArray = new byte[MAX_SCORE_COUNT];
	private boolean[] mScoreShortageArray = new boolean[MAX_SCORE_COUNT];
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

	/** Загрузка списка рекордов */
	public void loadScores(Context context) throws IOException{
		BufferedInputStream stream = new BufferedInputStream(context.openFileInput("Score"));
		DataInput in = new DataInputStream(stream);
		mScoreCount = 0;
		while (stream.available() != 0){
			mScoreArray[mScoreCount] = in.readInt();
			mScoreNameArray[mScoreCount] = in.readUTF();
			mScoreGameModeArray[mScoreCount] = in.readByte();
			mScoreShortageArray[mScoreCount] = in.readBoolean();
			mScoreCount++;
		}
		stream.close();
	}
	
	/** Добавление нового рекорда и сохранение в файл */
	public void addScore(Context context, String name, World world) throws IOException{
		boolean found = false;
		for (byte i=0; i<mScoreCount; i++)
			if (mScoreArray[i] < world.player.getScore()){
				for (byte j=mScoreCount; j>i; j--)
					if (j != MAX_SCORE_COUNT){
						mScoreArray[j] = mScoreArray[j-1];
						mScoreNameArray[j] = mScoreNameArray[j-1];
						mScoreGameModeArray[j] = mScoreGameModeArray[j-1];
						mScoreShortageArray[j] = mScoreShortageArray[j-1];
					}
				mScoreArray[i] = world.player.getScore();
				mScoreNameArray[i] = name;
				mScoreGameModeArray[i] = (byte)world.getGameMode();
				mScoreShortageArray[i] = world.isShortageMode();
				if (mScoreCount != MAX_SCORE_COUNT)
					mScoreCount++;
				found = true;
				break;
			}
		if (!found && (mScoreCount != MAX_SCORE_COUNT)){
			mScoreArray[mScoreCount] = world.player.getScore();
			mScoreNameArray[mScoreCount] = name;
			mScoreGameModeArray[mScoreCount] = (byte)world.getGameMode();
			mScoreShortageArray[mScoreCount] = world.isShortageMode();
			mScoreCount++;
		}
		saveScores(context);
	}
	
	/** Возвращает список строк с рекордом и именем*/
	public String[] getScoresName(Context context){
		String[] ar = new String[mScoreCount];
		for (byte i=0; i<mScoreCount; i++)
			ar[i] = String.format(context.getString(R.string.save_score_name),
			 mScoreNameArray[i], mScoreArray[i]);
		return ar;
	}
	
	/** Возвращает список строк с информацией о рекордах*/
	public String[] getScoresInfo(Context context){
		String[] ar = new String[mScoreCount];
		for (byte i=0; i<mScoreCount; i++)
			if (mScoreShortageArray[i])
				ar[i] = String.format(context.getString(R.string.save_score_info),
				 context.getResources().getStringArray
				 (R.array.difficulty)[mScoreGameModeArray[i]], 
				 context.getString(R.string.on));
			else
				ar[i] = String.format(context.getString(R.string.save_score_info),
				 context.getResources().getStringArray
				 (R.array.difficulty)[mScoreGameModeArray[i]], 
				 context.getString(R.string.off));
		return ar;
	}
	
	/** Сохраняет список рекордов в файл */
	void saveScores(Context context) throws IOException{
		BufferedOutputStream stream = new BufferedOutputStream(context.openFileOutput("Score", Context.MODE_PRIVATE));
		DataOutput out = new DataOutputStream(stream);
		for (byte i=0; i<mScoreCount; i++){
			out.writeInt(mScoreArray[i]);
			out.writeUTF(mScoreNameArray[i]);
			out.writeByte(mScoreGameModeArray[i]);
			out.writeBoolean(mScoreShortageArray[i]);
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
		return (mScoreCount < MAX_SCORE_COUNT);
	}
}
package saves;

import java.util.Date;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

class SaveDatabaseManager {
	private static final String TAG = "SaveDatabaseManager";
    private static final String DB_PATH = "/data/data/com.github.mcfeod.robots4droid/database";
    private static final String TABLE_SAVES = "saves";

    // стандартное дроидное название
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_SAVES_DATE = "date";
    private static final String COLUMN_SAVES_LEVEL = "level";
    private static final String COLUMN_SAVES_SCORE = "score";
    private static final String CREATE_DATABASE_SQL = String.format(
                    "create table %s( %s integer primary key autoincrement, %s integer, %s integer, %s integer)",
                    TABLE_SAVES,COLUMN_ID, COLUMN_SAVES_DATE, COLUMN_SAVES_LEVEL, COLUMN_SAVES_SCORE);

    private static final String[] sAllColumns = {COLUMN_ID, COLUMN_SAVES_DATE, COLUMN_SAVES_LEVEL, COLUMN_SAVES_SCORE};
    private static final int ID_INDEX = 0;
    private static final int DATE_INDEX = 1;
    private static final int LEVEL_INDEX = 2;
    private static final int SCORE_INDEX = 3;
    
    private static final SaveDatabaseManager INSTANCE = new SaveDatabaseManager();

    private SQLiteDatabase mDatabase;
    private String[] mArg = new String[1];

    private SaveDatabaseManager(){}

    public static SaveDatabaseManager getInstance() {
		return INSTANCE;
	}

	public void closeDatabase(){
        mDatabase.close();
        Log.d(TAG, "Database closed");
    }

    public void deleteSave(SavedGame save){
        Log.d(TAG, "deleted number:" + save.mId);
        mArg[0] = String.valueOf(save.mId);
        mDatabase.delete(TABLE_SAVES, "_id=?", mArg);
    }

    public void insertSave(SavedGame save){
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_SAVES_DATE, save.mCreationDate.getTime());
        cv.put(COLUMN_SAVES_LEVEL, save.mLevel);
        cv.put(COLUMN_SAVES_SCORE, save.mScore);
        save.mId = mDatabase.insert(TABLE_SAVES, null, cv);
    }

    /*Очищает список mScore и загружает в него соохранения из базы данных*/
    public void loadSaves(){
        Log.d(TAG, "Loading saves");
        SaveManager.getInstance().mGames.clear();

        // выгружаем всё из базы
        Cursor cursor = mDatabase.query(TABLE_SAVES, sAllColumns, null, null, null, null, null);

        Date date;
        long id;
        int level;
        int score;

        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            id = cursor.getLong(ID_INDEX);
            date = new Date(cursor.getLong(DATE_INDEX));
            level = cursor.getInt(LEVEL_INDEX);
            score = cursor.getInt(SCORE_INDEX);
            SaveManager.getInstance().mGames.add(new SavedGame(level, score, date, id));
            cursor.moveToNext();
        }
        cursor.close();
    }

    public void openDatabase(){
        try{
            mDatabase = SQLiteDatabase.openDatabase(DB_PATH, null, SQLiteDatabase.OPEN_READWRITE);
            Log.d(TAG, "Existing database opened");
        }
        catch (SQLiteException e){
            mDatabase = SQLiteDatabase.openDatabase(DB_PATH, null, SQLiteDatabase.CREATE_IF_NECESSARY);
            mDatabase.execSQL(CREATE_DATABASE_SQL);
            Log.d(TAG, "New database created");
        }
    }
}

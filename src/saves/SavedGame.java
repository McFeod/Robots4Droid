package saves;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;

import com.github.mcfeod.robots4droid.R;

class SavedGame {

	public long mId;
	public int mLevel;
	public int mScore;
	public java.util.Date mCreationDate;
	private Context mContext;

	private static StringBuilder sBuilder;

	public SavedGame(int level, int score, Date creationDate, Context context){
		sBuilder = new StringBuilder();
		mLevel = level;
		mScore = score;
		mCreationDate = creationDate;
		mContext = context;
	}

	public SavedGame(int level, int score, Date creationDate, long id, Context context){
		sBuilder = new StringBuilder();
		mLevel = level;
		mScore = score;
		mCreationDate = creationDate;
		mId = id;
		mContext = context;
	}

	@Override
	public String toString(){
		sBuilder.setLength(0);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM yy  HH:mm. EEEE");
		sBuilder.append(simpleDateFormat.format(mCreationDate));
		sBuilder.append("\n"+String.format(mContext.getString(R.string.level), mLevel));
		sBuilder.append("  "+String.format(mContext.getString(R.string.score), mScore));
		return sBuilder.toString();
	}

	public String getFileName(){
		return String.valueOf(mCreationDate.getTime());
	}
}

package saves;

import java.text.SimpleDateFormat;
import java.util.Date;

class SavedGame {

	public long mId;
	public int mLevel;
	public int mScore;
	public java.util.Date mCreationDate;

	private static StringBuilder sBuilder;

	public SavedGame(int level, int score, Date creationDate){
		sBuilder = new StringBuilder();
		mLevel = level;
		mScore = score;
		mCreationDate = creationDate;
	}

	public SavedGame(int level, int score, Date creationDate, long id){
		sBuilder = new StringBuilder();
		mLevel = level;
		mScore = score;
		mCreationDate = creationDate;
		mId = id;
	}

	@Override
	public String toString(){
		sBuilder.setLength(0);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM yy  HH:mm. EEEE");
		sBuilder.append(simpleDateFormat.format(mCreationDate));
		sBuilder.append("\nLevel: ");
		sBuilder.append(mLevel);
		sBuilder.append(" Score: ");
		sBuilder.append(mScore);
		return sBuilder.toString();
	}

	public String getFileName(){
		return String.valueOf(mCreationDate.getTime());
	}
}

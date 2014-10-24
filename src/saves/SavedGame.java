package saves;

import java.util.Date;

/* Класс преждназначен для хранения информации о соохранённой игре,
* которая будет видна в меню выбора соохранения. Остальная информация лежит в файле Internal Storage,
* имя которого представляет собой значение, возвращаемое
* */
class SavedGame {

    public long mId;
    public int mLevel;
    public int mScore;
    public java.util.Date mCreationDate;

    private static StringBuilder sBuilder = new StringBuilder();

    public SavedGame(int level, int score, Date creationDate){
        mLevel = level;
        mScore = score;
        mCreationDate = creationDate;
    }

    public SavedGame(int level, int score, Date creationDate, long id){
        mLevel = level;
        mScore = score;
        mCreationDate = creationDate;
        mId = id;
    }

    public int getLevel() {
        return mLevel;
    }

    public int getScore() {
        return mScore;
    }

    public Date getCreationDate() {
        return mCreationDate;
    }


    @Override
    public String toString(){
        sBuilder.setLength(0);
        sBuilder.append(mId);
        sBuilder.append(mCreationDate.toString());
        sBuilder.append("# Level: ");
        sBuilder.append(mLevel);
        sBuilder.append(" Score: ");
        sBuilder.append(mScore);
        return sBuilder.toString();
    }

    public String getFileName(){
        return String.valueOf(mCreationDate.getTime());
    }
}

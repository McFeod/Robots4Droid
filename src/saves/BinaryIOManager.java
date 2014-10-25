package saves;

import com.github.mcfeod.robots4droid.*;
import android.content.Context;
import android.util.Log;

import java.io.*;
import java.util.Date;

public class BinaryIOManager {
    private static final String TAG = "BinaryIOManager";

    private byte[][] mBoard = null;
    private Context mContext;
    private World mWorld;

    public static void deleteGameFile(SavedGame save, Context context){
        if(! context.deleteFile(save.getFileName())){
            Log.d(TAG, "Deleting file not found");
        }
    }

    public BinaryIOManager(Context context, World world){
        mContext = context;
        mWorld = world;
    }

    public void deleteGame(SavedGame save){
        BinaryIOManager.deleteGameFile(save, mContext);
    }

    /* SaveFileManager создаёт новый экземпляр класса Player и присваивает ему соохранённые в файле свойства.
    * Специально для этого в класс Player добавлен новый конструктор Player(x, y, energy, score)
    * После этого в поле player объекта world помещается ссылка на свежесозданный экземпляр.
    *
    * Для изменения игрового поля пока используется костыль: метод giveLinkToManager класса Board,
    * который вызывает метод setBoard данного класса.
    * C помощью этого костыля инициализируется внутреннее поле mBoard.
    *
    * В перспективе нужно создавать новый объект Board и помещать ссылку в соответствующее поле объекта world
    * */

    public SavedGame saveGame() throws IOException{
        if(mBoard == null){
            throw new RuntimeException("Board for load is not initialised");
        }

        SavedGame save = new SavedGame(mWorld.mLevel, mWorld.player.getScore(), new Date());
        String fileName = save.getFileName();
        BufferedOutputStream stream = null;
        try {
            stream = new BufferedOutputStream(
                    mContext.openFileOutput(fileName, Context.MODE_PRIVATE)
            );
            DataOutput out = new DataOutputStream(stream);
            // пишем данные в файл
            savePlayer(out);
            saveDesk(out);
        }
        catch (FileNotFoundException e){
            Log.d(TAG, "No save file");
        }
        finally {
            if(stream != null){
                stream.close();
            }
        }
        return save;
    }

    public void loadGame(SavedGame save) throws IOException{
        if(mBoard == null){
            throw new RuntimeException("Board for load is not initialised");
        }

        String fileName = save.getFileName();
        BufferedInputStream stream = null;
        try {
            stream = new BufferedInputStream(
                    mContext.openFileInput(fileName)
            );
            DataInput in = new DataInputStream(stream);
            // читаем данные из файла
            loadPlayer(in);
            Log.d(TAG, "Player loaded");
            loadDesk(in);
            Log.d(TAG, "Desk loaded");
        }
        catch (FileNotFoundException e){
            Log.d(TAG, "No save file");
        }
        finally {
            if(stream != null){
                stream.close();
            }
        }
    }

    public void setBoard(byte[][] board) {
        mBoard = board;
    }

    //TODO проверить, не устроит ли старый игрок утечку памяти
    private void loadPlayer(DataInput input)
    throws IOException{
        int x = input.readInt();
        int y = input.readInt();
        int energy = input.readInt();
        int score = input.readInt();
        //Log.d(TAG,String.format("Player x=%d, y=%d, energy=%d, score=%d\n",x,y,energy,score));
        mWorld.player = new Player(x,y,energy,score,true);
    }

    private void loadDesk(DataInput input)
    throws IOException{
        int x = input.readInt();
        int y = input.readInt();
        //Log.d(TAG,"X & Y loaded");
        //Log.d(TAG,String.format("x=%d, y=%d\n",x,y));
        // а кто из них кто?
        for (int i = 0; i < mBoard.length ; i++) {
            input.readFully(mBoard[i]);
            //Log.d(TAG, String.format("%d: %d %d %d %d\n", i, mBoard[i][0], mBoard[i][1], mBoard[i][2], mBoard[i][3]));
        }
    }

    private void savePlayer(DataOutput output)
            throws IOException{
        Point pos = mWorld.player.getPos();
        output.writeInt(pos.x);
        output.writeInt(pos.y);
        output.writeInt(mWorld.player.getEnergy());
        output.writeInt(mWorld.player.getScore());
    }

    private void saveDesk(DataOutput output)
            throws IOException{
        output.writeInt(mBoard.length);
        Log.d(TAG, String.valueOf(mBoard.length));
        output.writeInt(mBoard[0].length);
        Log.d(TAG, String.valueOf(mBoard.length));

        for (int i = 0; i < mBoard.length; i++) {
            output.write(mBoard[i]);
        }
    }
}

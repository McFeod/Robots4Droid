package saves;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;

import android.content.Context;
import android.util.Log;

import com.github.mcfeod.robots4droid.Point;
import com.github.mcfeod.robots4droid.World;

public class BinaryIOManager {
	private static final String TAG = "BinaryIOManager";

	private final Context mContext;
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

	/* SaveFileManager создаёт новый экземпляр класса World и присваивает ему соохранённые в файле свойства.
	* После этого в GameActivity.world помещается ссылка на свежесозданный экземпляр.
	* */

	public SavedGame saveGame() throws IOException{
		SavedGame save = new SavedGame(mWorld.getLevel(), mWorld.player.getScore(),
		 new Date(), mContext);

		String fileName = save.getFileName();
		BufferedOutputStream stream = null;
		try {
			stream = new BufferedOutputStream(
					mContext.openFileOutput(fileName, Context.MODE_PRIVATE)
			);
			DataOutput out = new DataOutputStream(stream);
			// пишем данные в файл
			saveWorld(out);
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
		String fileName = save.getFileName();
		BufferedInputStream stream = null;
		try {
			stream = new BufferedInputStream(mContext.openFileInput(fileName));
			DataInput in = new DataInputStream(stream);
			// читаем данные из файла
			loadWorld(in);
			Log.d(TAG, "World loaded");
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

	private void loadWorld(DataInput input)
			throws IOException{
		int x = input.readInt();
		int y = input.readInt();
		int energy = input.readInt();
		int score = input.readInt();
		int lvl = input.readInt();
		int mode = input.readInt();
		boolean shortage = input.readBoolean();
		int width = input.readInt();
		int height = input.readInt();
		int bots = input.readInt();
		int fast = input.readInt();
		mWorld = new World(width, height, bots, fast, x, y, energy, score, true, lvl, mode, shortage);
		byte[] row;
		for (int i = 0; i < width ; ++i) {
			row = new byte[height];
			input.readFully(row, 0, height);
			mWorld.board.setRow(i, row);
		}
	}

	private void saveWorld(DataOutput output) //I'm Superman!
			throws IOException{
		Point pos = mWorld.player.getPos();
		output.writeInt(pos.x);
		output.writeInt(pos.y);
		output.writeInt(mWorld.player.getEnergy());
		output.writeInt(mWorld.player.getScore());
		output.writeInt(mWorld.getLevel());
		output.writeInt(mWorld.getGameMode());
		output.writeBoolean(mWorld.isShortageMode());
		output.writeInt(mWorld.getWidth());
		output.writeInt(mWorld.getHeight());
		output.writeInt(mWorld.board.getAliveBotCount());
		output.writeInt(mWorld.board.getAliveFastBotCount());
		for (int i = 0; i < mWorld.getWidth(); ++i) {
			output.write(mWorld.board.getRow(i));
		}
	}
	
	/* костыыыыыль :'( */
	public World updatedWorld(){
		return mWorld;
	}
}

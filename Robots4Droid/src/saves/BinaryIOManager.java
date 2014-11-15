package saves;

import com.github.mcfeod.robots4droid.*;

import android.content.Context;
import android.util.Log;

import java.io.*;
import java.util.Date;

public class BinaryIOManager {
	private static final String TAG = "BinaryIOManager";

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

	/* SaveFileManager создаёт новый экземпляр класса World и присваивает ему соохранённые в файле свойства.
	* После этого в GameActivity.world помещается ссылка на свежесозданный экземпляр.
	* */

	public SavedGame saveGame() throws IOException{
		SavedGame save = new SavedGame(mWorld.getLevel(), mWorld.player.getScore(), new Date());
		String fileName = save.getFileName();
		BufferedOutputStream stream = null;
		try {
			stream = new BufferedOutputStream(
					mContext.openFileOutput(fileName, Context.MODE_PRIVATE)
			);
			DataOutput out = new DataOutputStream(stream);
			// пишем данные в файл
			saveWorld(out);
			saveSettings(out);
		}	/* SaveFileManager создаёт новый экземпляр класса Player и присваивает ему соохранённые в файле свойства.
		* Специально для этого в класс Player добавлен новый конструктор Player(x, y, energy, score)
		* После этого в поле player объекта world помещается ссылка на свежесозданный экземпляр.
		*
		* Для изменения игрового поля пока используется костыль: метод giveLinkToManager класса Board,
		* который вызывает метод setBoard данного класса.
		* C помощью этого костыля инициализируется внутреннее поле mBoard.
		*
		* В перспективе нужно создавать новый объект Board и помещать ссылку в соответствующее поле объекта world
		* */
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
			stream = new BufferedInputStream(
					mContext.openFileInput(fileName)
			);
			DataInput in = new DataInputStream(stream);
			// читаем данные из файла
			loadWorld(in);
			Log.d(TAG, "World loaded");
			loadSettings(in);
			Log.d(TAG, "Settings loaded");
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
		int width = input.readInt();
		int height = input.readInt();
		int bots = input.readInt();
		int fast = input.readInt();
		mWorld = new World(width, height, bots, fast, x, y, energy, score, true, lvl);
		byte[] row;
		for (int i = 0; i < width ; ++i) {
			row = new byte[height];
			input.readFully(row, 0, height);
			mWorld.board.setRow(i, row);
		}
	}
	
	private void  loadSettings(DataInput input)
			throws IOException{
		SettingsParser.setBombMode(input.readBoolean());
		SettingsParser.setMineMode(input.readBoolean());
		SettingsParser.setSuicidePermission(input.readBoolean());
		SettingsParser.setGameComplexity(input.readBoolean() ? '1': '0');
	}
	
	private void saveSettings(DataOutput output)
			throws IOException{
		output.writeBoolean(SettingsParser.areBombsOn());
		output.writeBoolean(SettingsParser.areMinesOn());
		output.writeBoolean(SettingsParser.areSuicidesOn());
		output.writeBoolean(SettingsParser.needExtraFastBots());
	}
	private void saveWorld(DataOutput output) //I'm Superman!
			throws IOException{
		Point pos = mWorld.player.getPos();
		output.writeInt(pos.x);
		output.writeInt(pos.y);
		output.writeInt(mWorld.player.getEnergy());
		output.writeInt(mWorld.player.getScore());
		output.writeInt(mWorld.getLevel());
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

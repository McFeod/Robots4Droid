package com.github.mcfeod.robots4droid;


import android.content.Context;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class SavedGameSerializer {
    private Context mContext;
    private String mFileName;
    private byte[][] mBoard = null;

    public SavedGameSerializer(Context context, String fileName){
        mContext = context;
        mFileName = fileName;
    }

    public void saveGame() throws IOException{
        if(mBoard == null){
            throw new RuntimeException("Board for save is not initialised");
        }
        // соохранение массива в начале файла: пока корректно для одного
        Writer writer = null;
        try{
            OutputStream out = mContext.
                    openFileOutput(mFileName, Context.MODE_PRIVATE);
            writer = new OutputStreamWriter(out);
            saveDesk(writer);
        }
        finally {
            if(writer != null){
                writer.close();
            }

        }
    }

    public void setBoard(byte[][] board) {
        mBoard = board;
    }

    private void saveDesk(Writer writer)
        throws IOException{
        for (int i = 0; i < mBoard.length; i++) {
            for (int j = 0; j < mBoard[i].length; j++) {
                writer.write(Integer.toString(mBoard[i][j]));
            }
        }
    }
}

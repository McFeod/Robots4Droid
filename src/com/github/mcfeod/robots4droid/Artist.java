package com.github.mcfeod.robots4droid;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.*;
import android.util.Log;

public class Artist {
    public static final int ANIMATION_COMPLETED = 0;
    public static final int ANIMATION_RUNNING   = 427839;
    public static final int ANIMATION_STARTED   = 428;

    private static final boolean LETS_USE_PREPARED_BACKGROUND = false;
    private static final int     SQUALID_ANIMATION_DURATION   = 6;
    private static final String  TAG                          = "Artist";

    private Bitmap bitRobot, bitFastRobot, bitPlayer, bitBackground,
            bitJunk, bitMine, bitCell, bitCell2;

    // переменная для удобного доступа к картинкам
    private Resources mResources;
    private int mCellWidth;
    private int mCellHeight;

    // переменная, служащая для рисования продолжительных эффектов
    private int effectStage;

    // Paint без настроек для рисования bitmap-ов
    private Paint dumbPaint;
    // Paint для анимации импульсной бомбы(отвёртки?)
    private Paint squalidCirclePaint;

    public Artist(Context context, int cellWidth, int cellHeight, int lineWidth){
        mResources  = context.getResources();
        mCellWidth  = cellWidth;
        mCellHeight = cellHeight;

        reloadResources();
        if(! LETS_USE_PREPARED_BACKGROUND){
            bitCell  = getScaledBitmap(R.drawable.cell);
            bitCell2 = getScaledBitmap(R.drawable.cell2);
        }
        dumbPaint = new Paint();
        squalidCirclePaint = new Paint();

        // будем рисовать синюю полупрозрачную окружность
        squalidCirclePaint.setColor(Color.BLUE);
        squalidCirclePaint.setStyle(Paint.Style.STROKE);
        squalidCirclePaint.setAlpha(128);
        squalidCirclePaint.setStrokeWidth(lineWidth);
    }
    public void drawBackground(Canvas canvas){
        canvas.drawColor(Color.GRAY);
        if(LETS_USE_PREPARED_BACKGROUND) {
            canvas.drawBitmap(bitBackground, 0, 0, dumbPaint);
        }
    }
    public void drawElement(Canvas canvas, Point corner, byte kind, boolean evenness){
        if(! LETS_USE_PREPARED_BACKGROUND){
            if (evenness)
                canvas.drawBitmap(bitCell2,corner.x,corner.y,dumbPaint);
            else
                canvas.drawBitmap(bitCell,corner.x,corner.y,dumbPaint);
        }
        switch (kind){
            case Board.JUNK:
                canvas.drawBitmap(bitJunk,corner.x,corner.y,dumbPaint);
                break;
            case Board.ROBOT:
                canvas.drawBitmap(bitRobot,corner.x,corner.y,dumbPaint);
                break;
            case Board.FASTROBOT:
                canvas.drawBitmap(bitFastRobot,corner.x,corner.y,dumbPaint);
                break;
            case Board.MINE:
                canvas.drawBitmap(bitMine,corner.x,corner.y,dumbPaint);
                break;
        }
    }
    public void drawPlayer(Canvas canvas, Point corner){
        canvas.drawBitmap(bitPlayer, corner.x, corner.y, dumbPaint);
    }

    public int drawSpecialEffect(Canvas canvas, Point corner, DrawThread1.SpecialEffect effect){
        if(effect == DrawThread1.SpecialEffect.BOMB_IMPULSE){
            // если анимация ещё не началась
            if(effectStage == 0){
                effectStage = SQUALID_ANIMATION_DURATION;
                canvas.drawCircle(corner.x, corner.y, (float)1.41 * mCellWidth, squalidCirclePaint);
                return ANIMATION_STARTED;
            }else{
                float radius = (float) 1.41 * mCellWidth *effectStage / SQUALID_ANIMATION_DURATION;
                canvas.drawCircle(corner.x, corner.y, radius, squalidCirclePaint);
            }
        }
        // общий шаг анимации
        effectStage --;
        if(effectStage == 0){
            return ANIMATION_COMPLETED;
        }else{
            return ANIMATION_RUNNING;
        }
    }

    public void freeResources(){
        bitRobot.recycle();
        bitFastRobot.recycle();
        bitPlayer.recycle();
        bitJunk.recycle();
        bitMine.recycle();
        if(LETS_USE_PREPARED_BACKGROUND){
            bitBackground.recycle();
        }else{
            bitCell.recycle();
            bitCell2.recycle();
        }
    }

    /** Эта функция пока не работает */
    public void makeBackground(int width, int height){
        if(! LETS_USE_PREPARED_BACKGROUND){
            return;
        }

        Bitmap oddCell  = getScaledBitmap(R.drawable.cell);
        Bitmap evenCell = getScaledBitmap(R.drawable.cell2);

        // освобождаем старую доску, если такая была, и создаём новую
        if(bitBackground != null && !bitBackground.isRecycled()){
            bitBackground.recycle();
        }
        bitBackground = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitBackground);

        for (int i = 0; i < width; i+=mCellWidth) {
            for (int j = 0; j < height; j+= mCellHeight) {
                canvas.drawBitmap(bitBackground, i, j, dumbPaint);
            }
        }
        Log.d(TAG, "Background is made");

        oddCell.recycle();
        evenCell.recycle();
    }

    public void reloadResources(){
        bitRobot     = getScaledBitmap(R.drawable.robot);
        bitFastRobot = getScaledBitmap(R.drawable.fastrobot);
        bitPlayer    = getScaledBitmap(R.drawable.player);
        bitJunk      = getScaledBitmap(R.drawable.junk);
        bitMine      = getScaledBitmap(R.drawable.mine);
    }

    private Bitmap getScaledBitmap(int id){
        // здесь можно будет добавить оптимизацию с BitmapFactory options.inSampleSize
        Bitmap src = BitmapFactory.decodeResource(mResources, id);
        Bitmap bitmap1 = Bitmap.createScaledBitmap(src, mCellWidth, mCellHeight, false);

        // в предыдущей версии была явная утечка памяти, т.к. загруженные большие изображения не удалялись
        src.recycle();
        return bitmap1;
    }
}

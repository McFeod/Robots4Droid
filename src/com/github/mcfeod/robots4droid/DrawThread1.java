package com.github.mcfeod.robots4droid;

import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;

import java.util.concurrent.locks.ReentrantLock;

public class DrawThread1 extends Thread {
    public static enum SpecialEffect{
        NO,
        BOMB_IMPULSE
    }

    private static final String TAG = "DrawThread1";

    /** Количество клеток, помещающееся на доске в длину*/
    private int boardCellWidth;
    /** Количество клеток, помещающееся на доске в длину*/
    private int boardCellHeight;

    // Внещние объекты, с которыми приходится иметь дело
    private SurfaceHolder   holder;
    private World           world;
    private Artist          artist;

    private SpecialEffect currentEffect = SpecialEffect.NO;

    // Хранитель пиксельных значений
    private final PXManager PX = new PXManager();

    /*Рассмотрим глобальную систему координат, в которой левый верхний угол каждой клетки
    * имеет координаты (i * PX.cellWidth; j * PX.cellHeight).
    * Начало области видимости в этой системе координат записано в viewCorner
    * а максимально возможные координаты viewCorner хранятся в farBoardCorner.*/
    private final Point farBoardCorner = new Point();
    private final Point viewCorner     = new Point();

    // позиция, куда постепенно смещается viewCorner
    private final Point targetCorner   = new Point();

    // Эти поля не имеют глобального смысла и используются в локальных функциях,
    // чтобы не создавать лишние объекты. В Dalvik жуткий сборщик мусора.
    private final Point startPosition  = new Point();
    private final Point bitmapPosition = new Point();


    // темнокнижный блокировщик для синхронизации
    private final ReentrantLock targetCornerLock = new ReentrantLock();

    public DrawThread1(SurfaceHolder surfaceHolder, Context context, World world,
                      GameSurfaceView view){
        // запоминаем внешние объекты
        this.holder = surfaceHolder;
        this.world = world;

        // полагаю, что мы не будем менять ширину SurfaceView в процессе игры
        PX.viewWidth =  view.getWidth();
        PX.viewHeight = view.getHeight();
        setDensityDependent(context.getResources().getDisplayMetrics().density);

        artist = new Artist(context, PX.cellWidth, PX.cellHeight, PX.lineWidth);
        artist.makeBackground(boardCellWidth * PX.cellWidth, boardCellHeight * PX.cellHeight);

        //определяем стартовую позицию видимой области в глобальных координатах
        setTargetCorner();
        viewCorner.cloneCoordinates(targetCorner);
    }

    public void notifyBombUsage(){
        currentEffect = SpecialEffect.BOMB_IMPULSE;
    }

    @Override
    public void run() {
        while(! Thread.currentThread().isInterrupted()){
            targetCornerLock.lock();
            //Log.d(TAG, "targetCorner locked by thread");
            try{
                changeViewCorner();
                redraw();
            }
            finally {
                //Log.d(TAG, "targetCorner unlocked by thread");
                targetCornerLock.unlock();
            }
        }
    }

    public void setTargetCorner(int dx, int dy){
        targetCornerLock.lock();
        //Log.d(TAG, "targetCorner locked by touchEvent");
        try{
            targetCorner.x =viewCorner.x + dx;
            targetCorner.y =viewCorner.y + dy;
            validateCorner(targetCorner);
            //Log.d(TAG, String.format("After touch target x=%d, y=%d", targetCorner.x, targetCorner.y));
        }
        finally {
            //Log.d(TAG, "targetCorner unlocked by touchEvent");
            targetCornerLock.unlock();
        }
    }

    public void setTargetCorner(){
        targetCornerLock.lock();
        Log.d(TAG, "targetCorner locked by moveEvent");
        try {
            setCoordinates(targetCorner, world.player.getPos());
            Log.d(TAG, "Player coordinates:" + targetCorner);
            targetCorner.x -= PX.viewWidth / 2;
            targetCorner.y -= PX.viewHeight / 2;
            validateCorner(targetCorner);
            Log.d(TAG, "TargetCorner coordinates:" + targetCorner);
        }
        finally {
            Log.d(TAG, "targetCorner unlocked by moveEvent");
            targetCornerLock.unlock();
        }
    }

    private void changeViewCorner(){
        if (targetCorner.x - viewCorner.x >= PX.step)
            viewCorner.x += PX.step;
        else if (targetCorner.x - viewCorner.x <= -PX.step)
            viewCorner.x -= PX.step;
        else
            viewCorner.x = targetCorner.x;

        if (targetCorner.y - viewCorner.y >= PX.step)
            viewCorner.y += PX.step;
        else if (targetCorner.y - viewCorner.y <= -PX.step)
            viewCorner.y -= PX.step;
        else
            viewCorner.y = targetCorner.y;
    }

    /** Устанавливает значения, зависящие от плотности пикселов на экране:
     * размеры клеток, шаг анимации, размеры доски в клетках, farBoardCorner.
     * @param density Плотность пикселов на экране */
    private void setDensityDependent(float density){
        PX.setValuesForDensity(density);
        Log.d(TAG, String.format("ViewWidth:%d, viewHeight:%d",PX.viewWidth,PX.viewHeight));
        boardCellWidth =  PX.viewWidth  / PX.cellWidth  + 2;
        boardCellHeight = PX.viewHeight / PX.cellHeight + 2;
        Log.d(TAG, String.format("Width:%d cells, height:%d cells",boardCellWidth,boardCellHeight));

        farBoardCorner.x = world.board.getWidth()  * PX.cellWidth - PX.viewWidth + PX.indent;
        farBoardCorner.y = world.board.getHeight() * PX.cellHeight- PX.viewHeight + PX.indent;
        Log.d(TAG, String.format("MaxX:%d, MaxY:%d",farBoardCorner.x,farBoardCorner.y));
    }

    private void setCoordinates(Point dst, int x, int y){
        dst.x = x * PX.cellWidth;
        dst.y = y * PX.cellHeight;
    }

    private void setCoordinates(Point dst, Point pos){
        dst.x = pos.x * PX.cellWidth;
        dst.y = pos.y * PX.cellHeight;
    }

    /* Функция блокирует canvas, вызывает методы объекта artist для отрисовки изобрпажения
    * и посылает canvas обратно в SurfaceView
    * */
    private void redraw() {
        // Мы здесь не создаём объект, просто получаем ссылку
        // В использовании объектного поля this.canvas не видно смысла
        Canvas canvas = holder.lockCanvas();
        if(canvas == null){
            Log.d(TAG, "got null canvas");
        }

        // Рисуем задний фон
        artist.drawBackground(canvas);

        // вычисляем индексы первой и последней видимых клеток в массиве
        // внимание: целочисленное деление!
        int firstVisibleCellX = viewCorner.x / PX.cellWidth;
        int firstVisibleCellY = viewCorner.y / PX.cellHeight;
        int lastVisibleCellX = firstVisibleCellX + boardCellWidth - 1;
            lastVisibleCellX = Math.min(lastVisibleCellX, world.board.getWidth()-1);
        int lastVisibleCellY = firstVisibleCellY + boardCellHeight - 1;
            lastVisibleCellY = Math.min(lastVisibleCellY, world.board.getHeight()-1);

        /* На основании индекса первой видимой клетки, вычисляем её позицию в абсолютных координатах,
        * а затем её координаты относительно viewCorner. Такие мелочи, как отрицательный результат,
        * не должны нас расстраивать, т.к. canvas услужливо нарисует лишь видимую часть bitmap-а.
        * (я надеюсь)*/
        setCoordinates(startPosition, firstVisibleCellX, firstVisibleCellY);
        startPosition.x -= viewCorner.x ;
        startPosition.y -= viewCorner.y;
        // получены относительные координаты

        //позиция текущего bitmap-а
        bitmapPosition.cloneCoordinates(startPosition);

        for (int i = firstVisibleCellX; i <= lastVisibleCellX; i++) {
            for (int j = firstVisibleCellY; j <= lastVisibleCellY; j++) {
                artist.drawElement(canvas, bitmapPosition, world.board.GetKind(i, j), (i + j) % 2 == 0);
                bitmapPosition.y += PX.cellHeight;
            }
            bitmapPosition.y = startPosition.y;
            bitmapPosition.x += PX.cellWidth;
        }

        // получаем абсолютные и относительные координаты игрока
        setCoordinates(bitmapPosition, world.player.getPos());
        bitmapPosition.x -= viewCorner.x;
        bitmapPosition.y -= viewCorner.y;
        //отрисовка игрока
        artist.drawPlayer(canvas, bitmapPosition);

        if(currentEffect != SpecialEffect.NO){
            // записываем в bitmapPosition координаты центра клетки игрока
            bitmapPosition.x += PX.cellWidth/2;
            bitmapPosition.y += PX.cellHeight/2;

            switch (artist.drawSpecialEffect(canvas, bitmapPosition, currentEffect)){

                case Artist.ANIMATION_STARTED:
                    targetCornerLock.lock();    // основной поток будет заблокирован до окночания анимации
                    break;

                case Artist.ANIMATION_COMPLETED:
                    currentEffect = SpecialEffect.NO;
                    targetCornerLock.unlock();
                    break;
            }
        }
        // посылаем canvas по назначению
        holder.unlockCanvasAndPost(canvas);
    }

    private void validateCorner(Point coordinates){
        if(coordinates.x < -PX.indent){
            coordinates.x = -PX.indent;
        }else if(coordinates.x >= farBoardCorner.x){
            coordinates.x = farBoardCorner.x;
        }
        if(coordinates.y < -PX.indent){
            coordinates.y = -PX.indent;
        }else if(coordinates.y >= farBoardCorner.y){
            coordinates.y = farBoardCorner.y;
        }
    }
}

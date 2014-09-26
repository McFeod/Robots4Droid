package com.github.mcfeod.robots4droid;
import com.github.mcfeod.board_objects.*;

/* Здесь описан весь функционал класса.
 * Никакого другого не планируется.
 * Можно смело пользоваться его интерфейсом.
 *  */
public class View {
    private int mLeftTopX;
    private int mLeftTopY;
    private int mRightBottomX;
    private int mRightBottomY;

    public View(Player player){
        setNewView(player);
    }

    public void setNewView(Player player){
        int dWidth = 5;
        int dHeight = 5;
        /*int dWidth = Activity.getWidth()/2;
        int dHeight = Activity.getHeight()/2;*/
        int x = player.getX();
        int y = player.getY();
        // Инициализация и проверка, можно оптимизировать?
        mLeftTopX     = GamePlay.sBoard.normaliseX(x-dWidth);
        mLeftTopY     = GamePlay.sBoard.normaliseY(y-dHeight);
        mRightBottomX = GamePlay.sBoard.normaliseX(x+dWidth);
        mRightBottomY = GamePlay.sBoard.normaliseY(y+dHeight);
    }
    public boolean isIn(int x, int y){
        // дублирование кода с одноименной функцией
        return (x>=mLeftTopX)&&(x<=mRightBottomX)&&(y>=mLeftTopY)&&(y<=mRightBottomY);
    }
    public boolean isIn(Playable obj){
        int x = obj.getX();
        int y = obj.getY();
        return (x>=mLeftTopX)&&(x<=mRightBottomX)&&(y>=mLeftTopY)&&(y<=mRightBottomY);
        // дублирование кода с предыдущим
    }
    public int getXOffset(){
        return mLeftTopX;
    }
    public int getYOffset(){
        return mLeftTopY;
    }
}

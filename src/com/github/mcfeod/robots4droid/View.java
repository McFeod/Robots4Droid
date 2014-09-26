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
        /*int dWidth = Activity.getWidth()/2;
        int dHeight = Activity.getHeight()/2;
        int x = player.getX();
        int y = player.getY();
        int boardWidth = GamePlay.Board.getWidth();
        int boardHeight = GamePlay.Board.getHeight();
        mLeftTopX = x - dWidth;
        if(mLeftTopX<0){

        }else if(){

        }
        * Инициализация и проверка, можно оптимизировать
        */
    }
    public boolean isIn(int x, int y){
        return (x>=mLeftTopX)&&(x<=mRightBottomX)&&(y>=mLeftTopY)&&(y<=mRightBottomY);
    }
    public int getXOffset(){
        return mLeftTopX;
    }
    public int getYOffset(){
        return mLeftTopY;
    }
}

package com.github.mcfeod.board_objects;
import com.github.mcfeod.robots4droid.GamePlay;

/*public interface Playable {
    public int getX();
    public int getY();
    public void makeMove();
    //public void spawn(); переписать как абстрактный класс
}*/
public abstract class Playable implements GameObject{
    public Playable next;
    public Playable previous;

    protected int mX;
    protected int mY;

    public abstract int getX();
    public abstract int getY();
    public abstract boolean isFast();
    public abstract void makeMove();

    public void spawn(){
        // заглушка
        mX = 0;
        mY = 0;
        GamePlay.sBoard.add(mX, mY, this);
    }
}
